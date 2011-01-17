package bukkit.Vandolis;
/**
 * 
 */


/**
 * @author Vandolis
 */
import java.io.File;
import java.sql.SQLException;
import java.util.HashMap;

import org.bukkit.Player;
import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * BloodBucket for Bukkit
 * 
 * @author Vandolis
 */
public class BloodBucket extends JavaPlugin {
	private final BloodBucketPlayerListener		playerListener	= new BloodBucketPlayerListener(this);
	private final BloodBucketBlockListener		blockListener	= new BloodBucketBlockListener(this);
	private final HashMap<Player, Boolean>		debugees		= new HashMap<Player, Boolean>();
	private final HashMap<Player, CommandInfo>	players			= new HashMap<Player, CommandInfo>();
	
	public BloodBucket(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, plugin, cLoader);
		// TODO: Place any custom initialisation code here
		
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
	}
	
	public HashMap<Player, CommandInfo> getPlayers() {
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
		// TODO: Place any custom disable code here
		
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Goodbye world!");
	}
	
	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events
		
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		
		// Check the db
		try {
			DataManager.init();
		}
		catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void setDebugging(final Player player, final boolean value) {
		debugees.put(player, value);
	}
}