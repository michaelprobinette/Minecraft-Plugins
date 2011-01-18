/**
 * 
 */
package bukkit.Vandolis;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Vandolis
 */
public class Teleport extends JavaPlugin {
	private final TeleportPlayerListener	playerListener	= new TeleportPlayerListener(this);
	
	public Teleport(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		// TODO: Place any custom initialisation code here
		
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
	}
	
	public void onEnable() {
		// TODO: Place any custom enable code here including the registration of any events
		
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = this.getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void onDisable() {
		// TODO: Place any custom disable code here
		
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Teleport Disabled");
	}
	
	public void telePlayer(Player player, String... split) {
		// Convert the split to an int array
		int pos[] = new int[split.length - 1];
		try {
			for (int i = 1; i < split.length; i++) {
				pos[i - 1] = Integer.valueOf(split[i].trim());
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		double y = 0;
		int count = 0;
		switch (pos.length) {
			case 0:
				break;
			case 1:
				player.sendMessage("Teleporting you...");
				// Check up
				d: for (int distance = 0; distance < 64; distance++) {
					if (y == 0) {
						// for (int i = (int) player.getY(); i < etc.getServer().getHighestBlockY(pos[0], (int) player.getZ()); i++) {
						for (int i = (int) player.getLocation().getY(); i < player.getLocation().getY() + distance; i++) {
							if (player.getWorld().getBlockAt(pos[0], i, (int) player.getLocation().getZ()).getType() == Material.AIR) {
								count++;
								if (count == 2) {
									y = i - 1;
									break d;
								}
							}
							else {
								count = 0;
							}
						}
					}
					if (y == 0) {
						// Check down
						// for (int i = (int) player.getY(); i > 0; i--) {
						for (int i = (int) player.getLocation().getY(); i > player.getLocation().getY() - distance && player.getLocation().getY() - distance > 0; i--) {
							
							if (player.getWorld().getBlockAt(pos[0], i, (int) player.getLocation().getZ()).getType() == Material.AIR) {
								count++;
								if (count == 2) {
									y = i;
									break d;
								}
							}
							else {
								count = 0;
							}
						}
					}
				}
				player.teleportTo(new Location(player.getWorld(), pos[0], y, player.getLocation().getZ()));
				//				player.teleportTo(pos[0], y, player.getLocation().getZ(), player.getLocation().getYaw(), player.getLocation().getPitch());
				break;
			case 2:
				// Check up
				d: for (int distance = 0; distance < 64; distance++) {
					if (y == 0) {
						// for (int i = (int) player.getY(); i < etc.getServer().getHighestBlockY(pos[0], (int) player.getZ()); i++) {
						for (int i = (int) player.getLocation().getY(); i < player.getLocation().getY() + distance; i++) {
							if (player.getWorld().getBlockAt(pos[0], i, pos[1]).getType() == Material.AIR) {
								count++;
								if (count == 2) {
									y = i - 1;
									break d;
								}
							}
							else {
								count = 0;
							}
						}
					}
					if (y == 0) {
						// Check down
						// for (int i = (int) player.getY(); i > 0; i--) {
						for (int i = (int) player.getLocation().getY(); i > player.getLocation().getY() - distance && player.getLocation().getY() - distance > 0; i--) {
							
							if (player.getWorld().getBlockAt(pos[0], i, pos[1]).getType() == Material.AIR) {
								count++;
								if (count == 2) {
									y = i;
									break d;
								}
							}
							else {
								count = 0;
							}
						}
					}
				}
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], y, pos[1]));
				//				player.teleportTo(pos[0], y, pos[1], player.getLocation().getYaw(), player.getLocation().getPitch());
				break;
			case 3:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], pos[2], pos[1]));
				//				player.teleportTo(pos[0], pos[2], pos[1], player.getLocation().getYaw(), player.getLocation().getPitch());
				break;
			case 4:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], pos[2], pos[1]));
				//				player.teleportTo(pos[0], pos[2], pos[1], pos[3], player.getLocation().getPitch());
				break;
			case 5:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], pos[2], pos[1]));
				//				player.teleportTo(pos[0], pos[2], pos[1], pos[3], pos[4]);
				break;
			default:
				player.sendMessage("Did not teleport anywhere.");
				break;
		}
	}
}
