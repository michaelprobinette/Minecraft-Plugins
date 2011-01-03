import java.util.logging.Logger;

public class PlayerPos extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "PlayerPos";
	private String					version	= "1.0";
	
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
	
	public void getPos(Player player) {
		player.sendMessage("Your position is X: " + (int) player.getX() + " Y: " + (int) player.getY() + " Z: " + (int) player.getZ());
	}
	
	public class Listener extends PluginListener {
		PlayerPos	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(PlayerPos plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, java.lang.String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/pos")) {
					getPos(player);
					return true;
				}
			}
			
			return false;
		}
	}
}
