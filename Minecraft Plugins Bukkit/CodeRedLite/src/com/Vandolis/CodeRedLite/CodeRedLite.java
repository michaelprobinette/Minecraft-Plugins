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
import com.Vandolis.CodeRedLite.Commands.Econ;
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
	private final SQLDatabase			database		= new SQLDatabase(this);
	private EconProperties				properties		= null;
	private EconShop					shop			= null;
	private String						pluginMessage	= "";								//"[§cCodeRedLite§f] ";
	private ArrayList<EconItemStack>	rawItems		= new ArrayList<EconItemStack>();
	private ArrayList<Player>			debugees		= new ArrayList<Player>();
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	public void onDisable()
	{
		for (EconPlayer iter : loadedPlayers)
		{
			iter.unload();
		}
		
		shop.softUpdate();
		
		// Close the databases connections and prepared statements
		if (!database.closeConnection())
		{
			log.log(Level.SEVERE, "CodeRedLite could not close all connections.");
		}
		
		log = null;
		loadedPlayers = null;
		properties = null;
		shop = null;
		debugees = null;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	public void onEnable()
	{
		log = getServer().getLogger();
		loadedPlayers = new ArrayList<EconPlayer>();
		
		// Setup the database
		database.openConnection(); // Open the connections
		
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
			
			for (EconItemStack iter : rawItems)
			{
				iter.setBasePrice(properties.getBaseValue());
				iter.setSlope(properties.getSlope());
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
		getCommand("econ").setExecutor(new Econ(this));
		
		// Schedule the autopay
		if (properties.isAutoPay())
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoPay(this),
				1200 * properties.getAutoPayTime(),
					1200 * properties.getAutoPayTime());
		}
		
		// Schedule autosave
		if (properties.isAutoSave())
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoSave(this),
				1200 * properties.getAutoSaveTime(),
					1200 * properties.getAutoSaveTime());
		}
		
		// Schedule shop restocking if needed
		if ((!properties.isShopsHaveInfiniteItems()) && (properties.isAutoRestock()))
		{
			getServer().getScheduler().scheduleSyncRepeatingTask(this, new AutoRestock(this), 0,
				1200 * properties.getRestockTime());
		}
		
		// Setup player plugin link
		//EconPlayer.setPlugin(this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		
		// Load players in case of plugin reload
		for (Player player : getServer().getOnlinePlayers())
		{
			loadPlayer(player);
		}
	}
	
	/**
	 * Loads the given player into the loadedPlayers list. Loads their corresponding EconPlayer
	 * 
	 * @param player
	 *            to load
	 */
	public void loadPlayer(Player player)
	{
		try
		{
			// Get the player from the database
			// Add the player to the loadedPlayers list
			loadedPlayers.add(database.getEconPlayer(player));
			
			// Loop through the debugees list
			for (String iter : properties.getDebugees())
			{
				// Check if they are on the list
				if (player.getName().equalsIgnoreCase(iter))
				{
					// On the list, send them a message and add them to the plugin debugees list
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
	 * Unloads the given player from the plugin. Saves all of their information.
	 * 
	 * @param player
	 *            to unload
	 */
	public void unloadPlayer(Player player)
	{
		// Loop through the loadedPlayers
		for (EconPlayer iter : loadedPlayers)
		{
			// Check for the player
			if (iter.getPlayer() == player)
			{
				// Player found
				
				iter.update(); // Save the info
				
				loadedPlayers.remove(iter); // Remove from loadedPlayers
				debugees.remove(player); // Remove from debugees (if needed)
				
				log.info("Unloaded player: " + player.getName());
				break; // done
			}
		}
	}
	
	/**
	 * @return properties
	 */
	public EconProperties getProperties()
	{
		return properties;
	}
	
	/**
	 * @return sqlDatabase
	 */
	public SQLDatabase getSQL()
	{
		return database;
	}
	
	/**
	 * @return the logger
	 */
	public Logger getLog()
	{
		return log;
	}
	
	/**
	 * @return loadedPlayers
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
	
	/**
	 * @param player
	 * @return the EconPlayer for the given player
	 */
	public EconPlayer getEconPlayer(Player player)
	{
		// Loop through the loadedPlayers
		for (EconPlayer iter : loadedPlayers)
		{
			// Check for our player
			if (iter.getPlayer() == player)
			{
				return iter; // Found it
			}
		}
		
		// Not found, load the player
		loadPlayer(player);
		
		// After player is loaded call this method again
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
		// Loop through the loadedPlayers
		for (EconPlayer iter : loadedPlayers)
		{
			// Check for our player
			if (iter.getPlayer().getName().equalsIgnoreCase(name))
			{
				return iter; // Found it
			}
		}
		
		// Not found
		
		// Grab the player from the server
		Player player = getServer().getPlayer(name);
		
		// Check for no player online
		if (player == null)
		{
			return null; // No player, return null
		}
		
		// Not null, load the player
		loadPlayer(player);
		
		// After player is loaded call this method again
		return getEconPlayer(player); // Hurrah recursion
	}
	
	/**
	 * Checks to see if the given player is a debugee
	 * 
	 * @param player
	 * @return true if debugee
	 */
	public boolean isDebugging(final Player player)
	{
		// Check if it contains them
		if (debugees.contains(player))
		{
			return true; // Yes
		}
		else
		{
			return false; // No
		}
	}
	
	/**
	 * Sets the given player to be a debugee
	 * 
	 * @param player
	 */
	public void setDebugging(final Player player)
	{
		debugees.add(player);
	}
	
	/**
	 * Checks to see if the given player name is loaded
	 * 
	 * @param playerName
	 * @return True if given player is loaded
	 */
	public boolean playerLoaded(String playerName)
	{
		// Loop through the loadedPlayers
		for (EconPlayer iter : loadedPlayers)
		{
			// Check their names
			if (iter.getPlayer().getName().equalsIgnoreCase(playerName))
			{
				return true; // Found them, return true
			}
		}
		
		return false; // Not found, return false
	}
	
	/**
	 * Returns the players balance
	 * 
	 * @param playerName
	 * @return players balance. -1 If no player found
	 */
	public int getBalance(String playerName)
	{
		// Try and get the EconPlayer
		EconPlayer econPlayer = getEconPlayer(playerName);
		
		// Check for null player
		if (econPlayer != null)
		{
			return econPlayer.getBalance(); // Not null, return the balance
		}
		
		return -1; // Null, return -1
	}
	
	/**
	 * Sets the balance for the given player with the given amount
	 * 
	 * @param playerName
	 * @param amount
	 */
	public void setBalance(String playerName, int amount)
	{
		getEconPlayer(playerName).setBalance(amount); // Set the amount
	}
}
