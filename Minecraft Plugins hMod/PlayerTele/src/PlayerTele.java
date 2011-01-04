import java.util.logging.Logger;

/**
 * @author Vandolis
 */
public class PlayerTele extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "PlayerTele";
	private String					version	= "v1.0.0";
	
	public void enable() {
		etc.getInstance().addCommand("/tele", "- Teleport to a specific x, z, y coordinate.");
	}
	
	public void disable() {
		etc.getInstance().removeCommand("/tele");
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
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
				player.sendMessage(Colors.Purple + "Teleporting you...");
				// Check up
				d: for (int distance = 0; distance < 64; distance++) {
					if (y == 0) {
						// for (int i = (int) player.getY(); i < etc.getServer().getHighestBlockY(pos[0], (int) player.getZ()); i++) {
						for (int i = (int) player.getY(); i < player.getY() + distance; i++) {
							if (etc.getServer().getBlockIdAt(pos[0], i, (int) player.getZ()) == 0) {
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
						for (int i = (int) player.getY(); i > player.getY() - distance && player.getY() - distance > 0; i--) {
							if (etc.getServer().getBlockIdAt(pos[0], i, (int) player.getZ()) == 0) {
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
				player.teleportTo(pos[0], y, player.getZ(), player.getRotation(), player.getPitch());
				break;
			case 2:
				// Check up
				d: for (int distance = 0; distance < 64; distance++) {
					if (y == 0) {
						// for (int i = (int) player.getY(); i < etc.getServer().getHighestBlockY(pos[0], (int) player.getZ()); i++) {
						for (int i = (int) player.getY(); i < player.getY() + distance; i++) {
							if (etc.getServer().getBlockIdAt(pos[0], i, pos[1]) == 0) {
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
						for (int i = (int) player.getY(); i > player.getY() - distance && player.getY() - distance > 0; i--) {
							if (etc.getServer().getBlockIdAt(pos[0], i, pos[1]) == 0) {
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
				player.sendMessage(Colors.Purple + "Teleporting you...");
				player.teleportTo(pos[0], y, pos[1], player.getRotation(), player.getPitch());
				break;
			case 3:
				player.sendMessage(Colors.Purple + "Teleporting you...");
				player.teleportTo(pos[0], pos[2], pos[1], player.getRotation(), player.getPitch());
				break;
			case 4:
				player.sendMessage(Colors.Purple + "Teleporting you...");
				player.teleportTo(pos[0], pos[2], pos[1], pos[3], player.getPitch());
				break;
			case 5:
				player.sendMessage(Colors.Purple + "Teleporting you...");
				player.teleportTo(pos[0], pos[2], pos[1], pos[3], pos[4]);
				break;
			default:
				player.sendMessage("Did not teleport anywhere.");
				break;
		}
	}
	
	public class Listener extends PluginListener {
		PlayerTele	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(PlayerTele plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, java.lang.String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/tele")) {
					if (split.length >= 2) {
						telePlayer(player, split);
					}
					else {
						player.sendMessage("Usage is /tele [x] [z] [y] [rotation] [pitch]. Leave blank if unknown.");
					}
					return true;
				}
			}
			
			return false;
		}
	}
}
