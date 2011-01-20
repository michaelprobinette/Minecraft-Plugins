/**
 * 
 */
package bukkit.Vandolis;

import java.io.File;
import java.util.ArrayList;

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
public class PinPointTeleport extends JavaPlugin {
	private final TeleportPlayerListener	playerListener	= new TeleportPlayerListener(this);
	private static ArrayList<Material>		airBlocks		= new ArrayList<Material>();
	private static ArrayList<Material>		notFloorBlocks	= new ArrayList<Material>();
	
	public PinPointTeleport(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
		airBlocks.add(Material.AIR);
		airBlocks.add(Material.TORCH);
		airBlocks.add(Material.SIGN);
		airBlocks.add(Material.SIGN_POST);
		airBlocks.add(Material.WATER);
		airBlocks.add(Material.WALL_SIGN);
		airBlocks.add(Material.BROWN_MUSHROOM);
		airBlocks.add(Material.CAKE_BLOCK);
		airBlocks.add(Material.CROPS);
		airBlocks.add(Material.LADDER);
		airBlocks.add(Material.PAINTING);
		airBlocks.add(Material.PORTAL);
		airBlocks.add(Material.RED_MUSHROOM);
		airBlocks.add(Material.REDSTONE_TORCH_OFF);
		airBlocks.add(Material.REDSTONE_TORCH_ON);
		airBlocks.add(Material.REDSTONE_WIRE);
		airBlocks.add(Material.SAPLING);
		airBlocks.add(Material.SUGAR_CANE_BLOCK);
		airBlocks.add(Material.WOOD_DOOR);
		airBlocks.add(Material.WOODEN_DOOR);
		airBlocks.add(Material.MINECART);
		airBlocks.add(Material.BOAT);
		airBlocks.add(Material.IRON_DOOR_BLOCK);
		airBlocks.add(Material.LEVER);
		airBlocks.add(Material.STONE_BUTTON);
		airBlocks.add(Material.STONE_PLATE);
		airBlocks.add(Material.WOOD_PLATE);
		
		notFloorBlocks.add(Material.LAVA);
		notFloorBlocks.add(Material.AIR);
		notFloorBlocks.add(Material.FIRE);
		notFloorBlocks.add(Material.FENCE);
		notFloorBlocks.add(Material.SUGAR_CANE_BLOCK);
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
	
	private boolean isAir(Material mat) {
		for (Material iter : airBlocks) {
			if (iter.equals(mat)) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isFloor(Material mat) {
		for (Material iter : notFloorBlocks) {
			if (iter.equals(mat)) {
				return false;
			}
		}
		
		return true;
	}
	
	public int checkDown(World w, int distance, int x, int z, int y) {
		int safe = 0;
		
		// Check down
		for (int i = y; (i > y - distance) && (i - distance > 10); i--) {
			// Check if the block is air and the count of air is less than 2
			// If the count is equal to 2 try and find solid ground
			Material material1 = w.getBlockAt(x, i, z).getType();
			Material material2 = w.getBlockAt(x, i - 1, z).getType();
			Material floor = w.getBlockAt(x, i - 2, z).getType();
			
			if (isAir(material1) && isAir(material2) && isFloor(floor)) {
				safe = i - 1;
				
				if (floor.equals(Material.LAVA)) {
					w.getBlockAt(x, i - 2, z).setType(Material.COBBLESTONE);
				}
				
				break;
			}
		}
		
		return safe;
	}
	
	public int checkUp(World w, int distance, int x, int z, int y) {
		int safe = 0;
		// Check up
		for (int i = y; i < y + distance; i++) {
			// Check if it has passed through solid ground
			// Check if the block is air
			Material material1 = w.getBlockAt(x, i, z).getType();
			Material material2 = w.getBlockAt(x, i - 1, z).getType();
			Material floor = w.getBlockAt(x, i - 2, z).getType();
			
			if (isAir(material1) && isAir(material2) && isFloor(floor)) {
				
				if (floor.equals(Material.LAVA)) {
					w.getBlockAt(x, i - 2, z).setType(Material.COBBLESTONE);
				}
				safe = i - 1;
				
				break;
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
