/*
 * Copyright (C) 2011 Vandolis <http://vandolis.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.Vandolis.CodeRedLite;

import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 * @author Vandolis
 */
public class EconProperties
{
  // Player
  private boolean           autoPay                  = false;
  private int               autoPayTime              = 10;                     // Minutes
  private int               autoPayAmount            = 20;
  
  // Shop
  private boolean           shopsHaveInfiniteItems   = false;
  private boolean           shopsHaveInfiniteMoney   = true;
  private boolean           autoRestock              = false;
  private int               autoRestockTime          = 10;                     // Minutes
  //If true players can sell anything in the items table for the given price.
  private boolean           enforceItemWhitelist     = true;
  
  // Items
  private boolean           isDynamicPrices          = false;
  private boolean           individualDynamicPricing = false;
  private float             slope                    = 1.0f;
  private int               baseValue                = 250;
  
  // General
  private boolean           autoSave                 = true;
  private int               autoSaveTime             = 10;                     // Minutes
  private String            moneyName                = "Strypes";
  private String            pluginMessage            = "";
  private ArrayList<String> debugees                 = new ArrayList<String>();
  
  // Vars
  private CodeRedLite       plugin                   = null;
  
  /**
   * 1 arg ctor. Sets the CodeRedLite instance to the given.
   * 
   * @param CodeRedLite
   *          instance
   */
  public EconProperties(CodeRedLite codeRed)
  {
    plugin = codeRed;
    
    loadSettings();
    //		configCheck();
  }
  
  /**
   * Loads the settings. Will make the directories if they do not exist, as well as set the defaults for the
   * settings.yml file
   */
  private void loadSettings()
  {
    // Make the directory
    if (plugin.getDataFolder().mkdirs())
    {
      // Actually made it, notify console
      plugin.getLog().info("CodeRedLite has created its directory.");
    }
    
    // Read the file settings
    readSettings();
  }
  
  /**
   * Reads the settings from the settings.yml file. Does a check for an empty file (new file, or not created yet) and
   * if that check is true it will create a file with the default values.
   */
  private void readSettings()
  {
    // Get the configuration from the plugin
    Configuration config = plugin.getConfiguration();
    config.load(); // Load it
    
    // Check if the config is empty. If it is that means either the file is empty or it has not been created
    if (config.getAll().isEmpty())
    {
      // Write the defualts to the file
      writeDefaults();
    }
    else
    {
      // Read the settings
      plugin.getLog().info("CodeRedLite loaded: ");
      
      // General
      pluginMessage = config.getString("settings.general.MessagePrefix");
      plugin.getLog().info("settings.general.MessagePrefix = " + pluginMessage);
      moneyName = config.getString("settings.general.MoneyName");
      plugin.getLog().info("settings.general.MoneyName = " + moneyName);
      
      // Items
      enforceItemWhitelist = config.getBoolean("settings.items.UseItemWhitelist", enforceItemWhitelist);
      plugin.getLog().info("settings.items.UseItemWhitelist = " + enforceItemWhitelist);
      isDynamicPrices = config.getBoolean("settings.items.UseDynamicPricing", isDynamicPrices);
      plugin.getLog().info("settings.items.UseDynamicPricing = " + isDynamicPrices);
      individualDynamicPricing = config.getBoolean("settings.items.UseIndividualPricing", individualDynamicPricing);
      plugin.getLog().info("settings.items.UseIndividualPricing = " + individualDynamicPricing);
      baseValue = config.getInt("settings.items.PriceBase", baseValue);
      plugin.getLog().info("settings.items.PriceBase = " + baseValue);
      slope = (float) config.getDouble("settings.items.PriceSlope", slope);
      plugin.getLog().info("settings.items.PriceSlope = " + slope);
      
      // Shops
      shopsHaveInfiniteItems = config.getBoolean("settings.shops.InfiniteItems", shopsHaveInfiniteItems);
      plugin.getLog().info("settings.shops.InfiniteItems = " + shopsHaveInfiniteItems);
      shopsHaveInfiniteMoney = config.getBoolean("settings.shops.InfiniteMoney", shopsHaveInfiniteMoney);
      plugin.getLog().info("settings.shops.InfiniteMoney = " + shopsHaveInfiniteMoney);
      
      // Debugees
      debugees = (ArrayList<String>) config.getStringList("settings.debugees", new ArrayList<String>());
      plugin.getLog().info("settings.debugees:");
      for (String iter : debugees)
      {
        plugin.getLog().info("\t" + iter);
      }
      
      // Output a success message
      plugin.getLog().info("CodeRedLite has finished loading settings");
    }
    
    // Dont need to save because we are not changing anything in this method
  }
  
  /**
   * Writes the default values to the configuration and saves it. If the file does not exist it will create it.
   */
  private void writeDefaults()
  {
    plugin.getLog().info("CodeRedLite did not detect a settings file. Writing defaults...");
    
    // Get the configuration from the plugin
    Configuration config = plugin.getConfiguration();
    config.load();
    
    // General
    config.setProperty("settings.general.MessagePrefix", pluginMessage);
    config.setProperty("settings.general.MoneyName", moneyName);
    
    // Items
    config.setProperty("settings.items.UseItemWhitelist", enforceItemWhitelist);
    config.setProperty("settings.items.UseDynamicPricing", isDynamicPrices);
    config.setProperty("settings.items.UseIndividualPricing", individualDynamicPricing);
    config.setProperty("settings.items.PriceBase", baseValue);
    config.setProperty("settings.items.PriceSlope", slope);
    
    // Shops
    config.setProperty("settings.shops.InfiniteItems", shopsHaveInfiniteItems);
    config.setProperty("settings.shops.InfiniteMoney", shopsHaveInfiniteMoney);
    
    // Debugees
    config.setProperty("settings.debugees", new ArrayList<Player>());
    
    // Write it all
    config.save();
  }
  
  /**
   * @return the plugin
   */
  public CodeRedLite getPlugin()
  {
    return plugin;
  }
  
  /**
   * @return the isDynamicPrices
   */
  public boolean isDynamicPrices()
  {
    return isDynamicPrices;
  }
  
  /**
   * @return the autoRestock
   */
  public boolean isAutoRestock()
  {
    return autoRestock;
  }
  
  /**
   * @return the autoRestockTime
   */
  public int getAutoRestockTime()
  {
    return autoRestockTime;
  }
  
  /**
   * @return the autoPay
   */
  public boolean isAutoPay()
  {
    return autoPay;
  }
  
  /**
   * @return the autoSave
   */
  public boolean isAutoSave()
  {
    return autoSave;
  }
  
  /**
   * @return autoPayTime
   */
  public int getAutoPayTime()
  {
    return autoPayTime;
  }
  
  /**
   * @return autoPayAmount
   */
  public int getAutoPayAmount()
  {
    return autoPayAmount;
  }
  
  /**
   * @return the shopsHaveInfiniteItems
   */
  public boolean isShopsHaveInfiniteItems()
  {
    return shopsHaveInfiniteItems;
  }
  
  /**
   * @return the shopsHaveInfiniteMoney
   */
  public boolean isShopsHaveInfiniteMoney()
  {
    return shopsHaveInfiniteMoney;
  }
  
  /**
   * @return restockTime
   */
  public int getRestockTime()
  {
    return autoRestockTime;
  }
  
  /**
   * @return autoSavetime
   */
  public int getAutoSaveTime()
  {
    return autoSaveTime;
  }
  
  /**
   * @return moneyName
   */
  public String getMoneyName()
  {
    return moneyName;
  }
  
  /**
   * @return the pluginMessage
   */
  public String getPluginMessage()
  {
    return pluginMessage;
  }
  
  public boolean isEnforceItemWhitelist()
  {
    return enforceItemWhitelist;
  }
  
  /**
   * @return the individualDynamicPricing
   */
  public boolean isIndividualDynamicPricing()
  {
    return individualDynamicPricing;
  }
  
  /**
   * @return the slope
   */
  public float getSlope()
  {
    return slope;
  }
  
  /**
   * @return the baseValue
   */
  public int getBaseValue()
  {
    return baseValue;
  }
  
  /**
   * @return the debugees
   */
  public ArrayList<String> getDebugees()
  {
    return debugees;
  }
  
}
