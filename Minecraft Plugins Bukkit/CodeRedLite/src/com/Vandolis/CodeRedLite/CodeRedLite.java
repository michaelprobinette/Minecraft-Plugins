package com.Vandolis.CodeRedLite;

/**
 *
 */

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
 * Econ plugin for Bukkit.
 * 
 * @author Vandolis
 */
public class CodeRedLite extends JavaPlugin
{
	private final EconPlayerListener	playerListener	= new EconPlayerListener(this);
	//private final EconBlockListener		blockListener	= new EconBlockListener(this);
	private Logger						log				= null;
	private ArrayList<EconPlayer>		loadedPlayers	= null;
	private SQLDatabase					database		= null;
	private EconProperties				properties		= null;
	private EconShop					shop			= null;
	private String						pluginMessage	= "";								//"[§cCodeRedLite§f] ";
	private ArrayList<EconItemStack>	rawItems		= new ArrayList<EconItemStack>();
	private ArrayList<Player>			debugees		= new ArrayList<Player>();
	
	public void onDisable()
	{
		for (EconPlayer iter : loadedPlayers)
		{
			iter.unload();
		}
		
		shop.update();
		
		log = null;
		loadedPlayers = null;
		database = null;
		properties = null;
		shop = null;
		debugees = null;
	}
	
	public void onEnable()
	{
		log = getServer().getLogger();
		loadedPlayers = new ArrayList<EconPlayer>();
		database = new SQLDatabase(this);
		properties = new EconProperties(this);
		pluginMessage = properties.getPluginMessage();
		try
		{
			shop = database.getEconShop("The Shop");
			
			log.info("The Shop has loaded " + shop.getInventory().size() + " items.");
			database.populateRawItems(rawItems);
		}
		catch (SQLException e)
		{
			log.log(Level.SEVERE, "Could not load the shop. " + e.getLocalizedMessage());
		}
		
		// Load based on properties
		shop.setAllItemsInfinite(properties.isShopsHaveInfiniteItems());
		shop.setUseMoney(!properties.isShopsHaveInfiniteMoney());
		pluginMessage = properties.getPluginMessage();
		if (!properties.isIndividualDynamicPricing())
		{
			for (EconItemStack iter : shop.getInventory())
			{
				iter.setBasePrice(properties.getBaseValue());
				iter.setSlope(properties.getSlope());
				iter.updatePrice();
			}
		}
		
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Event.Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		//pm.registerEvent(Event.Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		
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
		if (properties.isAutoPay())
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoPay(this), 1200 * properties.getAutoPayTime(),
					1200 * properties.getAutoPayTime());
		}
		
		// Schedule autosave
		if (properties.isAutoSave())
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSave(this), 1200 * properties.getAutoSaveTime(),
					1200 * properties.getAutoSaveTime());
		}
		
		// Schedule shop restocking if needed
		if ((!properties.isShopsHaveInfiniteItems()) && (properties.isAutoRestock()))
		{
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
	public void loadPlayer(Player player)
	{
		try
		{
			loadedPlayers.add(database.getEconPlayer(player));
			for (String iter : properties.getDebugees())
			{
				if (player.getName().equalsIgnoreCase(iter))
				{
					log.info("CodeRedLite added debugee " + player.getName());
					player.sendMessage(pluginMessage + "You have been added as a debugee.");
					debugees.add(player);
				}
			}
			
			log.info("Loaded player: " + player.getName());
		}
		catch (Exception e)
		{
			log.log(Level.WARNING, "CodeRedLite could not load " + player.getName() + " " + e.getLocalizedMessage());
		}
	}
	
	/**
	 * @param player
	 */
	public void unloadPlayer(Player player)
	{
		for (EconPlayer iter : loadedPlayers)
		{
			if (iter.getPlayer() == player)
			{
				iter.update();
				loadedPlayers.remove(iter);
				debugees.remove(player);
				log.info("Unloaded player: " + player.getName());
				break;
			}
		}
	}
	
	public ArrayList<EconPlayer> getPlayers()
	{
		return loadedPlayers;
	}
	
	/**
	 * @return properties
	 */
	public EconProperties getProperties()
	{
		return properties;
	}
	
	public SQLDatabase getSQL()
	{
		return database;
	}
	
	/**
	 * @return the lOG
	 */
	public Logger getLog()
	{
		return log;
	}
	
	/**
	 * @return the pLAYERS
	 */
	public ArrayList<EconPlayer> getLoadedPlayers()
	{
		return loadedPlayers;
	}
	
	/**
	 * @return the shop
	 */
	public EconShop getShop()
	{
		return shop;
	}
	
	/**
	 * @return the pluginMessage
	 */
	public String getPluginMessage()
	{
		return pluginMessage;
	}
	
	public EconPlayer getEconPlayer(Player player)
	{
		for (EconPlayer iter : loadedPlayers)
		{
			if (iter.getPlayer() == player)
			{
				return iter;
			}
		}
		
		loadPlayer(player);
		
		return getEconPlayer(player); // Hurrah recursion
	}
	
	/**
	 * @return the rawItems
	 */
	public ArrayList<EconItemStack> getRawItems()
	{
		return rawItems;
	}
	
	/**
	 * @param iter
	 * @return loaded EconPlayer
	 */
	public EconPlayer getEconPlayer(String name)
	{
		for (EconPlayer iter : loadedPlayers)
		{
			if (iter.getPlayer().getName().equalsIgnoreCase(name))
			{
				return iter;
			}
		}
		
		Player player = getServer().getPlayer(name);
		
		if (player == null)
		{
			return null;
		}
		
		loadPlayer(player);
		
		return getEconPlayer(player); // Hurrah recursion
	}
	
	public boolean isDebugging(final Player player)
	{
		if (debugees.contains(player))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public void setDebugging(final Player player)
	{
		debugees.add(player);
	}
}
