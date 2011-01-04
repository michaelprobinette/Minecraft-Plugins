import java.util.logging.Logger;

public class CodeRedEconomy extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "CodeRedEconomy";
	private String					version	= "v0.0.1";
	private PropertiesFile			props	= new PropertiesFile(name + ".properties");
	private static DataManager		data;
	private Shop					shop;
	
	public void enable() {
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
		
		data = new DataManager();
		shop = new Shop(data);
	}
	
	public class Listener extends PluginListener {
		CodeRedEconomy	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(CodeRedEconomy plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/buy") && player.canUseCommand("/buy")) {
					User user = data.getUser(player);
					shop.buy(user, split);
					return true;
				}
				// if (split[0].equalsIgnoreCase("/sell") && player.canUseCommand("/sell")) {
				// shop.sell(player, split);
				// return true;
				// }
				// if (split[0].equalsIgnoreCase("/trade") && player.canUseCommand("/trade")) {
				//					
				// return true;
				// }
				if (split[0].equalsIgnoreCase("/balance") && player.canUseCommand("/balance")) {
					data.getUser(player).showBalance();
					return true;
				}
			}
			return false;
		}
		
	}
}
