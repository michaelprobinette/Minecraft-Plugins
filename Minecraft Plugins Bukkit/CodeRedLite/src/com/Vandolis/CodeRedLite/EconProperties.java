/**
 *
 */
package com.Vandolis.CodeRedLite;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.entity.Player;
import org.bukkit.util.config.Configuration;

/**
 * @author Vandolis
 */
public class EconProperties
{
	// Player
	private boolean				autoPay						= false;
	private int					autoPayTime					= 10;						// Minutes
	private int					autoPayAmount				= 20;
	
	// Shop
	private boolean				shopsHaveInfiniteItems		= false;
	private boolean				shopsHaveInfiniteMoney		= true;
	private boolean				autoRestock					= false;
	private int					autoRestockTime				= 10;						// Minutes
	private boolean				enforceItemWhitelist		= true;					// If true players can sell anything in the items table for the given price.
																						
	// Items
	private boolean				isDynamicPrices				= false;
	private boolean				individualDynamicPricing	= false;
	private float				slope						= 1.0f;
	private int					baseValue					= 250;
	
	// General
	private boolean				autoSave					= true;
	private int					autoSaveTime				= 10;						// Minutes
	private String				moneyName					= "Strypes";
	private String				pluginMessage				= "";
	private ArrayList<String>	debugees					= new ArrayList<String>();
	
	// Vars
	private CodeRedLite			plugin						= null;
	private File				file						= null;
	
	public EconProperties(CodeRedLite codeRed)
	{
		plugin = codeRed;
		file = new File(plugin.getDataFolder().getPath() + File.separator + "settings.yml");
		
		loadSettings();
		//		configCheck();
	}
	
	/**
	 *
	 */
	private void loadSettings()
	{
		if (plugin.getDataFolder().mkdir())
		{
			plugin.getLog().info("CodeRedLite has created its directory.");
		}
		
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
				writeDefaults();
			}
			catch (Exception ex)
			{
				ex.printStackTrace();
			}
		}
		else
		{
			readSettings();
		}
	}
	
	/**
	 *
	 */
	private void readSettings()
	{
		Configuration config = new Configuration(file);
		config.load();
		
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
		
		plugin.getLog().info("CodeRedLite has finished loading settings");
	}
	
	/**
	 *
	 */
	private void writeDefaults()
	{
		plugin.getLog().info("CodeRedLite did not detect a settings file. Writing defaults...");
		
		Configuration config = new Configuration(file);
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
