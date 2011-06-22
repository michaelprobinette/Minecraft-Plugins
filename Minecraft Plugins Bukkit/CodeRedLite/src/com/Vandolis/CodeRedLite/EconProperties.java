/**
 * 
 */
package com.Vandolis.CodeRedLite;

import java.io.File;
import java.util.ArrayList;

import org.bukkit.util.config.Configuration;

/**
 * @author Vandolis
 */
public class EconProperties {
	// Player
	private boolean		autoPay					= true;
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
	
	// General
	private boolean		autoSave				= true;
	private int			autoSaveTime			= 10;			// Minutes
	private String		moneyName				= "Strypes";
	private String		pluginMessage			= "";
	
	// Vars
	private CodeRedLite	plugin					= null;
	private File		file					= null;
	
	public EconProperties(CodeRedLite codeRed) {
		plugin = codeRed;
		file = new File(plugin.getDataFolder().getPath() + File.separator + "config.yml");
		configCheck();
	}
	
	public void configCheck() {
		plugin.getDataFolder().mkdir();
		
		if (file.exists() == false) {
			try {
				file.createNewFile();
				addDefaults();
			}
			catch (Exception ex) {
				ex.printStackTrace();
			}
		}
		else {
			loadkeys();
		}
	}
	
	private void write(String root, Object x) {
		Configuration config = load();
		config.setProperty(root, x);
		config.save();
	}
	
	private boolean readBoolean(String root) {
		Configuration config = load();
		return config.getBoolean(root, true);
	}
	
	private double readDouble(String root) {
		Configuration config = load();
		return config.getDouble(root, 0);
	}
	
	private ArrayList<String> readStringList(String root) {
		Configuration config = load();
		return (ArrayList<String>) config.getKeys(root);
	}
	
	private String readString(String root) {
		Configuration config = load();
		return config.getString(root);
	}
	
	private int readInt(String root) {
		Configuration config = load();
		return config.getInt(root, 0);
	}
	
	private Configuration load() {
		try {
			Configuration config = new Configuration(file);
			config.load();
			return config;
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void addDefaults() {
		plugin.log.info("Generating Config File...");
		write("AutoPay", autoPay);
		write("AutoPayTime", autoPayTime);
		write("AutoPayAmount", autoPayAmount);
		write("AutoRestock", autoRestock);
		write("AutoRestockTime", autoRestockTime);
		write("MoneyName", moneyName);
		write("PluginMessage", plugin.getPluginMessage());
		write("AutoSave", autoSave);
		write("UseItemWhitelist", enfoceItemWhitelist);
		write("UseDynamicPrices", isDynamicPrices);
		write("ShopsHaveInfiniteItems", shopsHaveInfiniteItems);
		write("ShopsHaveInfiniteMoney", shopsHaveInfiniteMoney);
		loadkeys();
	}
	
	/**
	 * @return the enfoceItemWhitelist
	 */
	public boolean isEnfoceItemWhitelist() {
		return enfoceItemWhitelist;
	}
	
	/**
	 * @return the plugin
	 */
	public CodeRedLite getPlugin() {
		return plugin;
	}
	
	/**
	 * @return the isDynamicPrices
	 */
	public boolean isDynamicPrices() {
		return isDynamicPrices;
	}
	
	private void loadkeys() {
		plugin.log.info("Loading Config File...");
		
		// Player
		autoPay = readBoolean("AutoPay");
		autoPayTime = readInt("AutoPayTime");
		autoPayAmount = readInt("AutoPayAmount");
		
		// Shop
		autoRestock = readBoolean("AutoRestock");
		autoRestockTime = readInt("AutoRestockTime");
		enfoceItemWhitelist = readBoolean("UseItemWhitelist");
		shopsHaveInfiniteItems = readBoolean("ShopsHaveInfiniteItems");
		shopsHaveInfiniteMoney = readBoolean("ShopsHaveInfiniteMoney");
		
		// Item
		isDynamicPrices = readBoolean("UseDynamicPrices");
		
		// General
		moneyName = readString("MoneyName");
		pluginMessage = readString("PluginMessage");
		autoSave = readBoolean("AutoSave");
	}
	
	/**
	 * @return the autoRestock
	 */
	public boolean isAutoRestock() {
		return autoRestock;
	}
	
	/**
	 * @return the autoRestockTime
	 */
	public int getAutoRestockTime() {
		return autoRestockTime;
	}
	
	/**
	 * @return the autoPay
	 */
	public boolean isAutoPay() {
		return autoPay;
	}
	
	/**
	 * @return the autoSave
	 */
	public boolean isAutoSave() {
		return autoSave;
	}
	
	/**
	 * @return autoPayTime
	 */
	public int getAutoPayTime() {
		return autoPayTime;
	}
	
	/**
	 * @return autoPayAmount
	 */
	public int getAutoPayAmount() {
		return autoPayAmount;
	}
	
	/**
	 * @return the shopsHaveInfiniteItems
	 */
	public boolean isShopsHaveInfiniteItems() {
		return shopsHaveInfiniteItems;
	}
	
	/**
	 * @return the shopsHaveInfiniteMoney
	 */
	public boolean isShopsHaveInfiniteMoney() {
		return shopsHaveInfiniteMoney;
	}
	
	/**
	 * @return restockTime
	 */
	public int getRestockTime() {
		return autoRestockTime;
	}
	
	/**
	 * @return autoSavetime
	 */
	public int getAutoSaveTime() {
		return autoSaveTime;
	}
	
	/**
	 * @return moneyName
	 */
	public String getMoneyName() {
		return moneyName;
	}
	
	/**
	 * @return the pluginMessage
	 */
	public String getPluginMessage() {
		return pluginMessage;
	}
	
	public boolean isEnforceItemWhitelist() {
		return enfoceItemWhitelist;
	}
	
}
