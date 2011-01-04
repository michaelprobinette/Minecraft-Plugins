import java.util.logging.Logger;

public class CodeRedEconomy extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "CodeRedEconomy";
	private String					version	= "v0.0.1";
	private Shop					shop;
	public static boolean			debug	= true;
	private static DataManager		data	= new DataManager();
	
	public void enable() {
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
		
		debug = data.getDebug();
		shop = new Shop();
	}
	
	public class Listener extends PluginListener {
		CodeRedEconomy	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(CodeRedEconomy plugin) {
			p = plugin;
		}
		
		public boolean onCommand(Player player, String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/buy")) {
					User user = DataManager.getUser(player);
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
				if (split[0].equalsIgnoreCase("/balance")) {
					DataManager.getUser(player).showBalance();
					return true;
				}
				if (split[0].equalsIgnoreCase("/add") && debug) {
					if (split.length >= 2) {
						if (split.length >= 3) {
							DataManager.getUser(split[1]).getMoney().addAmount(Integer.valueOf(split[2]));
						}
						else {
							DataManager.getUser(player).getMoney().addAmount(Integer.valueOf(split[1]));
						}
					}
					return true;
				}
				if (split[0].equalsIgnoreCase("/save") && debug) {
					DataManager.save();
					return true;
				}
				if (split[0].equalsIgnoreCase("/prices")) {
					if (split.length >= 2) {
						int page = Integer.valueOf(split[1]);
						PriceList.priceList(player, page);
					}
					else {
						PriceList.priceList(player, 1);
					}
					return true;
				}
			}
			return false;
		}
	}
}
