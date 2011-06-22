/**
 *
 */
package com.Vandolis.CodeRedLite;

import java.io.File;

import org.bukkit.util.config.Configuration;

/**
 * @author Vandolis
 */
public class EconProperties
{
	// Player
	private boolean		autoPay					= false;
	private int			autoPayTime				= 10;			// Minutes
	private int			autoPayAmount			= 20;
	
	// Shop
	private boolean		shopsHaveInfiniteItems	= false;
	private boolean		shopsHaveInfiniteMoney	= true;
	private boolean		autoRestock				= false;
	private int			autoRestockTime			= 10;			// Minutes
	private boolean		enfoceItemWhitelist		= true;		// If true players can sell anything in the items table for the given price.
																
	// Items
	private boolean		isDynamicPrices			= false;
	private boolean		individualBasePrices	= false;
	private float		slope					= 1.0f;
	private int			baseValue				= 250;
	
	// General
	private boolean		autoSave				= true;
	private int			autoSaveTime			= 10;			// Minutes
	private String		moneyName				= "Strypes";
	private String		pluginMessage			= "";
	
	// Vars
	private CodeRedLite	plugin					= null;
	private File		file					= null;
	
	public EconProperties(CodeRedLite codeRed)
	{
		plugin = codeRed;
		file = new File(plugin.getDataFolder().getPath() + File.separator + "settings1.yml");
		
		loadSettings();
		//		configCheck();
	}
	
	/**
	 *
	 */
	private void loadSettings()
	{
		plugin.getDataFolder().mkdir();
		
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
		
		// General
		pluginMessage = config.getString("settings.general.MessagePrefix");
		moneyName = config.getString("settings.general.MoneyName");
		
		// Items
		enfoceItemWhitelist = config.getBoolean("settings.items.UseItemWhitelist", enfoceItemWhitelist);
		isDynamicPrices = config.getBoolean("settings.items.UseDynamicPricing", isDynamicPrices);
		individualBasePrices = config.getBoolean("settings.items.UseIndividualPriceBase", individualBasePrices);
		baseValue = config.getInt("settings.items.BaseValue", baseValue);
		slope = (float) config.getDouble("settings.items.PriceSlope", slope);
		
		// Shops
		shopsHaveInfiniteItems = config.getBoolean("settings.shops.InfiniteItems", shopsHaveInfiniteItems);
		shopsHaveInfiniteMoney = config.getBoolean("settings.shops.InfiniteMoney", shopsHaveInfiniteMoney);
		
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
		config.setProperty("settings.items.UseItemWhitelist", enfoceItemWhitelist);
		config.setProperty("settings.items.UseDynamicPricing", isDynamicPrices);
		config.setProperty("settings.items.UseIndividualBasePrice", individualBasePrices);
		config.setProperty("settings.items.PriceBase", baseValue);
		config.setProperty("settings.items.PriceSlope", slope);
		
		// Shops
		config.setProperty("settings.shops.InfiniteItems", shopsHaveInfiniteItems);
		config.setProperty("settings.shops.InfiniteMoney", shopsHaveInfiniteMoney);
		
		config.save();
	}
	
	/**
	 * @return the enfoceItemWhitelist
	 */
	public boolean isEnfoceItemWhitelist()
	{
		return enfoceItemWhitelist;
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
		return enfoceItemWhitelist;
	}
	
}
