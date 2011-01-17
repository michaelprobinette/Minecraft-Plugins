/*
 * Proof of concept for a /follow command. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

package bukkit.Vandolis;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Follow for Bukkit
 * 
 * @author Vandolis
 */
public class Follow extends JavaPlugin {
	private final FollowPlayerListener			playerListener	= new FollowPlayerListener(this);
	private final HashMap<Player, Boolean>		debugees		= new HashMap<Player, Boolean>();
	private HashMap<Player, ArrayList<Player>>	players			= new HashMap<Player, ArrayList<Player>>();
	
	public Follow(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		// TODO: Place any custom initialisation code here
		
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
	}
	
	public HashMap<Player, ArrayList<Player>> getPlayers() {
		return players;
	}
	
	public boolean isDebugging(final Player player) {
		if (debugees.containsKey(player)) {
			return debugees.get(player);
		}
		else {
			return false;
		}
	}
	
	public void onDisable() {
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		System.out.println("Follow has been disabled.");
	}
	
	public void onEnable() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}