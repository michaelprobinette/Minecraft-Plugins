/**
 *
 */
package com.Vandolis.CommandOn;

/**
 * @author Vandolis
 */
import java.util.HashMap;

import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class CommandOn extends JavaPlugin
{
	private final COPlayerListener			playerListener	= new COPlayerListener(this);
	private final COBlockListener			blockListener	= new COBlockListener(this);
	private final COEntityListener			entityListener	= new COEntityListener(this);
	private final COServerListener			serverListener	= new COServerListener(this);
	private final HashMap<Player, Boolean>	debugees		= new HashMap<Player, Boolean>();
	
	// NOTE: There should be no need to define a constructor any more for more info on moving from
	// the old constructor see:
	// http://forums.bukkit.org/threads/too-long-constructor.5032/
	
	public void onDisable()
	{
		// TODO: Place any custom disable code here
		
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Goodbye world!");
	}
	
	public void onEnable()
	{
		// TODO: Place any custom enable code here including the registration of any events
		
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		
		// Players
		pm.registerEvent(Type.PLAYER_JOIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_COMMAND_PREPROCESS, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_KICK, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_LOGIN, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_PORTAL, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.PLAYER_RESPAWN, playerListener, Priority.Normal, this);
		
		// Entity
		pm.registerEvent(Type.ENTITY_DEATH, entityListener, Priority.Normal, this);
		
		// Block
		pm.registerEvent(Type.BLOCK_BREAK, blockListener, Priority.Normal, this);
		
		// Server
		pm.registerEvent(Type.WORLD_SAVE, serverListener, Priority.Normal, this);
		
		// Register our commands
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public boolean isDebugging(final Player player)
	{
		if (debugees.containsKey(player))
		{
			return debugees.get(player);
		}
		else
		{
			return false;
		}
	}
	
	public void setDebugging(final Player player, final boolean value)
	{
		debugees.put(player, value);
	}
}