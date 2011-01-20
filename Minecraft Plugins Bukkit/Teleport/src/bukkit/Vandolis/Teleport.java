/**
 * 
 */
package bukkit.Vandolis;

import java.io.File;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
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
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
	}
	
	public void onEnable() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		pm.registerEvent(Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void onDisable() {
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Teleport Disabled");
	}
	
	public int findSafeY(World w, int x, int z, int y) {
		int safe = 0; // The safe Y pos returned
		
		d: for (int distance = 0; distance < 64; distance++) {
			if (safe == 0) {
				if ((safe = checkUp(w, distance, x, z, y)) != 0) {
					break d;
				}
			}
			if (safe == 0) {
				if ((safe = checkDown(w, distance, x, z, y)) != 0) {
					break d;
				}
			}
		}
		return safe;
	}
	
	public void caveElevator(Player p, boolean up) {
		int y = 0;
		for (int distance = 0; distance <= 64; distance++) {
			if (up) {
				// Add 1 to the Y pos so that it doesn't return their current spot
				y =
						checkUp(p.getWorld(), distance, p.getLocation().getBlockX(), p.getLocation().getBlockZ(), p.getLocation()
								.getBlockY() + 2);
				
				if (y != 0) {
					break;
				}
			}
			else {
				// Subtract 1 from the Y pos so that it doesn't return their current spot
				y =
						checkDown(p.getWorld(), distance, p.getLocation().getBlockX(), p.getLocation().getBlockZ(), p.getLocation()
								.getBlockY());
				
				if (y != 0) {
					break;
				}
			}
		}
		if (y != 0) {
			// Found a spot, teleport the player
			p.teleportTo(new Location(p.getWorld(), p.getLocation().getX(), y, p.getLocation().getZ()));
		}
		else {
			p.sendMessage("Could not find a safe area.");
		}
	}
	
	public int checkDown(World w, int distance, int x, int z, int y) {
		int safe = 0;
		int count = 0;
		
		// Check down
		for (int i = y; (i > y - distance) && (y - distance > 10); i--) {
			// Check if the block is air and the count of air is less than 2
			// If the count is equal to 2 try and find solid ground
			Material material1 = w.getBlockAt(x, i, z).getType();
			Material material2 = w.getBlockAt(x, i - 1, z).getType();
			Material floor = w.getBlockAt(x, i - 2, z).getType();
			
			if (((material1.equals(Material.AIR)) || (material1.equals(Material.WATER)))
					&& (material2.equals(Material.AIR) || material2.equals(Material.WATER))
					&& (!floor.equals(Material.LAVA) && !floor.equals(Material.AIR))) {
				safe = i - 1;
				
				break;
				
				//				count++;
				//				System.out.println("Found " + material1);
				//				
				//				if (count >= 2) {
				//					// Two blocks of air found, check floor
				//					safe = i;
				//					
				//					break;
				//				}
			}
			else {
				// Not air, reset the count to 0
				count = 0;
			}
		}
		
		return safe;
	}
	
	public int checkUp(World w, int distance, int x, int z, int y) {
		int safe = 0;
		int count = 0;
		// Check up
		for (int i = y; i < y + distance; i++) {
			// Check if it has passed through solid ground
			// Check if the block is air
			Material material = w.getBlockAt(x, i, z).getType();
			Material floor = w.getBlockAt(x, i - 2, z).getType();
			
			if ((material.equals(Material.AIR) || (material.equals(Material.WATER)))
					&& (!(floor.equals(Material.AIR)) && !floor.equals(Material.WATER))) {
				count++;
				System.out.println("Found " + material);
				
				if (count == 2) {
					// Two blocks of air found, break out
					// Don't need to check for floor as the count starts when it passes from a block of material into a block of air
					safe = i - 1;
					
					break;
				}
			}
			else {
				// Not air, reset the count to 0
				count = 0;
			}
		}
		return safe;
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
		switch (pos.length) {
			case 0:
				break;
			case 1:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], findSafeY(player.getWorld(), pos[0], (int) player.getLocation()
						.getZ(), (int) player.getLocation().getY()), player.getLocation().getZ()));
				break;
			case 2:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], findSafeY(player.getWorld(), pos[0], pos[1], player.getLocation()
						.getBlockY()), pos[1]));
				break;
			case 3:
				player.sendMessage("Teleporting you...");
				player.teleportTo(new Location(player.getWorld(), pos[0], findSafeY(player.getWorld(), pos[0], pos[1], pos[2]), pos[1]));
				break;
			default:
				player.sendMessage("Did not teleport anywhere.");
				break;
		}
	}
}
