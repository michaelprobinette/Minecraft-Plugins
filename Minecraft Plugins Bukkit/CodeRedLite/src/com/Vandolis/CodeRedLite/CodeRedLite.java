/**
 * 
 */
package com.Vandolis.CodeRedLite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.Vandolis.CodeRedLite.Commands.Balance;
import com.Vandolis.CodeRedLite.Commands.Buy;
import com.Vandolis.CodeRedLite.Commands.Debug;
import com.Vandolis.CodeRedLite.Commands.Pay;
import com.Vandolis.CodeRedLite.Commands.Price;
import com.Vandolis.CodeRedLite.Commands.PriceList;
import com.Vandolis.CodeRedLite.Commands.Quote;
import com.Vandolis.CodeRedLite.Commands.Sell;
import com.Vandolis.CodeRedLite.Runnable.AutoPay;
import com.Vandolis.CodeRedLite.Runnable.AutoRestock;
import com.Vandolis.CodeRedLite.Runnable.AutoSave;

/**
 * Econ plugin for Bukkit
 * 
 * @author Vandolis
 */
public class CodeRedLite extends JavaPlugin {
	private final EconPlayerListener	playerListener	= new EconPlayerListener(this);
	private final EconBlockListener		blockListener	= new EconBlockListener(this);
	protected Logger					log				= null;
	protected ArrayList<EconPlayer>		PLAYERS			= null;
	protected SQLDatabase				database		= null;
	protected EconProperties			properties		= null;
	protected EconShop					shop			= null;
	private String						pluginMessage	= "";								//"[§cCodeRedLite§f] ";
	private ArrayList<EconItemStack>	rawItems		= new ArrayList<EconItemStack>();
	
	public void onDisable() {
		for (EconPlayer iter : PLAYERS) {
			iter.unload();
		}
		
		shop.update();
		
		log = null;
		PLAYERS = null;
		database = null;
		properties = null;
		shop = null;
	}
	
	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events
		log = getServer().getLogger();
		PLAYERS = new ArrayList<EconPlayer>();
		database = new SQLDatabase(this);
		properties = new EconProperties(this);
		pluginMessage = properties.getPluginMessage();
		try {
			shop = database.getEconShop("The Shop");
			shop.setAllItemsInfinite(properties.isShopsHaveInfiniteItems());
			shop.setUseMoney(!properties.isShopsHaveInfiniteMoney());
			log.info("The Shop has loaded " + shop.getInventory().size() + " items.");
			database.populateRawItems(rawItems);
		}
		catch (SQLException e) {
			log.log(Level.SEVERE, "Could not load the shop. " + e.getLocalizedMessage());
		}
		
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		
		// Register our commands
		getCommand("buy").setExecutor(new Buy(this));
		getCommand("sell").setExecutor(new Sell(this));
		getCommand("pricelist").setExecutor(new PriceList(this));
		getCommand("price").setExecutor(new Price(this));
		getCommand("balance").setExecutor(new Balance(this));
		getCommand("debug").setExecutor(new Debug(this));
		getCommand("pay").setExecutor(new Pay(this));
		getCommand("quote").setExecutor(new Quote(this));
		
		// Schedule the autopay
		if (properties.isAutoPay() == true) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoPay(this), 1200 * properties.getAutoPayTime(),
					1200 * properties.getAutoPayTime());
		}
		
		// Schedule autosave
		if (properties.isAutoSave() == true) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSave(this), 1200 * properties.getAutoSaveTime(),
					1200 * properties.getAutoSaveTime());
		}
		
		// Schedule shop restocking if needed
		if ((properties.isShopsHaveInfiniteItems() == false) && (properties.isAutoRestock() == true)) {
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoRestock(this), 0, 1200 * properties.getRestockTime());
		}
		
		// Setup player plugin link
		EconPlayer.setPlugin(this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	/**
	 * @param player
	 */
	public void loadPlayer(Player player) {
		try {
			PLAYERS.add(database.getEconPlayer(player));
			
			log.info("Loaded player: " + player.getName());
		}
		catch (Exception e) {
			log.log(Level.WARNING, "CodeRedLite could not load " + player.getName() + " " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * @param player
	 */
	public void unloadPlayer(Player player) {
		for (EconPlayer iter : PLAYERS) {
			if (iter.getPlayer() == player) {
				iter.update();
				PLAYERS.remove(iter);
				log.info("Unloaded player: " + player.getName());
				break;
			}
		}
	}
	
	public ArrayList<EconPlayer> getPlayers() {
		return PLAYERS;
	}
	
	/**
	 * @return properties
	 */
	public EconProperties getProperties() {
		return properties;
	}
	
	public SQLDatabase getSQL() {
		return database;
	}
	
	/**
	 * @return the lOG
	 */
	public Logger getLog() {
		return log;
	}
	
	/**
	 * @return the pLAYERS
	 */
	public ArrayList<EconPlayer> getPLAYERS() {
		return PLAYERS;
	}
	
	/**
	 * @return the shop
	 */
	public EconShop getShop() {
		return shop;
	}
	
	/**
	 * @return the pluginMessage
	 */
	public String getPluginMessage() {
		return pluginMessage;
	}
	
	public EconPlayer getEconPlayer(Player player) {
		for (EconPlayer iter : PLAYERS) {
			if (iter.getPlayer() == player) {
				return iter;
			}
		}
		
		loadPlayer(player);
		
		return getEconPlayer(player); // Hurrah recursion
	}
	
	/**
	 * @param database
	 *            the database to set
	 */
	public void setDatabase(SQLDatabase database) {
		this.database = database;
	}
	
	/**
	 * @return the rawItems
	 */
	public ArrayList<EconItemStack> getRawItems() {
		return rawItems;
	}
	
	/**
	 * @param iter
	 * @return
	 */
	public EconPlayer getEconPlayer(String name) {
		for (EconPlayer iter : PLAYERS) {
			if (iter.getPlayer().getName().equalsIgnoreCase(name)) {
				return iter;
			}
		}
		
		Player player = getServer().getPlayer(name);
		
		if (player == null) {
			return null;
		}
		
		loadPlayer(player);
		
		return getEconPlayer(player); // Hurrah recursion
	}
}