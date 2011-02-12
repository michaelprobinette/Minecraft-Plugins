/**
 * 
 */
package com.bukkit.Vandolis.DepositAll;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.griefcraft.lwc.LWCPlugin;

/**
 * @author Vandolis
 */
public class DepositAll extends JavaPlugin {
	private HashMap<Player, Action>		players			= new HashMap<Player, Action>();
	private DepositAllBlockListener		blockListener	= new DepositAllBlockListener(this);
	private DepositAllPlayerListener	playerListener	= new DepositAllPlayerListener(this);
	private boolean						useLWC			= false;
	private LWCPlugin					lwc				= null;
	
	/**
	 * @return the useLWC
	 */
	public boolean isUseLWC() {
		if (lwc == null) {
			try {
				PluginManager pm = getServer().getPluginManager();
				lwc = (LWCPlugin) pm.getPlugin("LWCPlugin");
				System.out.println("DepositAll is using LWC");
				useLWC = true;
			}
			catch (ClassCastException ex) {
				System.out.println("There's a plugin disguised as LWC! It's not the one I was expecting!");
			}
		}
		
		return useLWC;
	}
	
	enum Action {
		DEPOSIT, WITHDRAW
	}
	
	/**
	 * @param pluginLoader
	 * @param instance
	 * @param desc
	 * @param folder
	 * @param plugin
	 * @param cLoader
	 */
	public DepositAll(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		// TODO Auto-generated constructor stub
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	@Override
	public void onDisable() {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
		
		// Load the datamanager
		//		DataManager.load(this);
		
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	/**
	 * @return the lwc
	 */
	public LWCPlugin getLwc() {
		if (lwc == null) {
			try {
				PluginManager pm = getServer().getPluginManager();
				lwc = (LWCPlugin) pm.getPlugin("LWC");
				System.out.println("DepositAll is using LWC");
				useLWC = true;
			}
			catch (ClassCastException ex) {
				System.out.println("There's a plugin disguised as LWC! It's not the one I was expecting!");
			}
		}
		
		return lwc;
	}
	
	/**
	 * @param player
	 */
	public void addPlayer(Player player, Action action) {
		players.put(player, action);
	}
	
	/**
	 * @return the players
	 */
	public HashMap<Player, Action> getPlayers() {
		return players;
	}
	
	/**
	 * @param players
	 *            the players to set
	 */
	public void setPlayers(HashMap<Player, Action> players) {
		this.players = players;
	}
	
}
