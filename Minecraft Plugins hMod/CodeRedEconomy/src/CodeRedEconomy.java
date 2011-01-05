import java.util.logging.Logger;

public class CodeRedEconomy extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "CodeRedEconomy";
	private String					version	= "v0.0.1";
	private static DataManager		data	= new DataManager();
	
	public void enable() {
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, l, this, PluginListener.Priority.MEDIUM);
		
		// DataManager.getDebug() = data.getDataManager.getDebug()();
	}
	
	public class Listener extends PluginListener {
		CodeRedEconomy	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(CodeRedEconomy plugin) {
			p = plugin;
		}
		
		public boolean onConsoleCommand(String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("save-all")) {
					DataManager.save();
					return true;
				}
			}
			
			return false;
		}
		
		public boolean onCommand(Player player, String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/buy")) {
					DataManager.getShop("The Shop").buy(DataManager.getUser(player), split);
					return true;
				}
				if (split[0].equalsIgnoreCase("/sell")) {
					DataManager.getShop("The Shop").sell(DataManager.getUser(player), split);
					return true;
				}
				// if (split[0].equalsIgnoreCase("/trade") && player.canUseCommand("/trade")) {
				//					
				// return true;
				// }
				if (split[0].equalsIgnoreCase("/balance")) {
					DataManager.getUser(player).showBalance();
					return true;
				}
				if (split[0].equalsIgnoreCase("/test") && DataManager.getDebug()) {
					player.sendMessage("Time is: " + etc.getServer().getTime());
					return true;
				}
				if (split[0].equalsIgnoreCase("/pay")) {
					User user = DataManager.getUser(player);
					if (split.length >= 3) {
						User target = DataManager.getUser(split[1].trim());
						try {
							// The way it is formatted the person receiving money is the seller, and the person loosing money is the buyer,
							// so this is formatted target, user so the money goes from the user to the target
							Transaction.process(new Transaction(target, user, new Money(Integer.valueOf(split[2]))));
						}
						catch (NumberFormatException e) {
							user.sendMessage("The correct use is \"/pay [name] [amount]\"");
						}
					}
					else {
						user.sendMessage("Correct use is \"/pay [player] [amount]\"");
					}
					return true;
				}
				if (split[0].equalsIgnoreCase("/add") && DataManager.getDebug()) {
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
				if (split[0].equalsIgnoreCase("/shop") && DataManager.getDebug()) {
					System.out.println("There are " + DataManager.getShops().size());
					for (Shop iter : DataManager.getShops()) {
						System.out.println(iter.getName());
					}
					return true;
				}
				if (split[0].equalsIgnoreCase("/balall") && DataManager.getDebug()) {
					for (User iter : DataManager.getUsers()) {
						System.out.println(iter.getName() + iter.getMoney().toString());
					}
					return true;
				}
				if (split[0].equalsIgnoreCase("/saveredeco")) {
					DataManager.save();
					return true;
				}
				if (split[0].equalsIgnoreCase("/prices")) {
					if (split.length >= 2) {
						int page = Integer.valueOf(split[1]);
						PriceList.priceList(DataManager.getUser(player), page, DataManager.getShop("The Shop"));
					}
					else {
						PriceList.priceList(DataManager.getUser(player), 1, DataManager.getShop("The Shop"));
					}
					return true;
				}
				if (split[0].equalsIgnoreCase("/undo")) {
					DataManager.getUser(player).undoLastTrans();
					return true;
				}
			}
			return false;
		}
	}
}
