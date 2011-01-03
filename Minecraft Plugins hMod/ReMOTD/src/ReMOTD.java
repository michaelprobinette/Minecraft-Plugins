import java.util.logging.Logger;

public class ReMOTD extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "ReMOTD";
	private String					version	= "v1.0.0";
	private PropertiesFile			props	= new PropertiesFile("server.properties");
	
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
	
	public void setMotd(String[] motd) {
		etc.getInstance().setMotd(motd);
		String temp = "";
		for (String a : motd) {
			temp += a + " ";
		}
		temp = temp.trim();
		props.setString("motd", temp);
		etc.getServer().messageAll(temp);
	}
	
	public class Listener extends PluginListener {
		ReMOTD	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(ReMOTD plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/remotd") && player.canUseCommand("/remotd")) {
					if (split.length >= 2) {
						String[] msg = new String[split.length - 1];
						System.arraycopy(split, 1, msg, 0, msg.length);
						String temp = "";
						for (String a : msg) {
							temp += a + " ";
						}
						temp = temp.trim();
						player.sendMessage("Setting the motd to: " + temp);
						setMotd(msg);
					}
					else {
						player.sendMessage("Could not set the MotD");
					}
					return true;
				}
			}
			return false;
		}
	}
}