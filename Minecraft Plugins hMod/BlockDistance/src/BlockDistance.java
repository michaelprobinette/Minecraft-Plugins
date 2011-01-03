import java.util.logging.Logger;

/**
 * @author Vandolis
 */
public class BlockDistance extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "BlockDistance";
	private String					version	= "v1.0";
	private static double			xA		= 0, yA = 0, zA = 0, xB = 0, yB = 0, zB = 0;
	private static double			dX		= 0, dY = 0, dZ = 0;
	private static boolean			setA	= false, setB = false;
	
	public void enable() {
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
	}
	
	// Sends a message to all players!
	public void broadcast(String message) {
		for (Player p : etc.getServer().getPlayerList()) {
			p.sendMessage(message);
		}
	}
	
	public class Listener extends PluginListener {
		BlockDistance	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(BlockDistance plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, String[] split) {
			
			try {
				if (split[0].equalsIgnoreCase("/blockdistance") || split[0].equalsIgnoreCase("/bd")) {
					if (!setA) {
						xA = player.getX();
						xA = (int) xA;
						yA = player.getY();
						yA = (int) yA;
						zA = player.getZ();
						zA = (int) zA;
						player.sendMessage("Setting block A position");
						setA = true;
					}
					else if (!setB) {
						xB = player.getX();
						xB = (int) xB;
						yB = player.getY();
						yB = (int) yB;
						zB = player.getZ();
						zB = (int) zB;
						player.sendMessage("Setting block B position");
						setB = true;
					}
					
					if (setA && setB) {
						dX = xB - xA;
						dY = yB - yA;
						dZ = zB - zA;
						setA = false;
						setB = false;
						
						player.sendMessage("The distance from A to B is:");
						player.sendMessage("X: " + Double.toString(dX));
						player.sendMessage("Y: " + Double.toString(dY));
						player.sendMessage("Z: " + Double.toString(dZ));
						
						xA = 0;
						xB = 0;
						yA = 0;
						yB = 0;
						zA = 0;
						zB = 0;
						dX = 0;
						dY = 0;
						dZ = 0;
					}
					
					return true;
				}
			}
			catch (Exception e) {
				e.printStackTrace();
			}
			
			return false;
		}
	}
}
