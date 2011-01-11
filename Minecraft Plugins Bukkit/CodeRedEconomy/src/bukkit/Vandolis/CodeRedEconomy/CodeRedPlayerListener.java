package bukkit.Vandolis.CodeRedEconomy;

import org.bukkit.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

public class CodeRedPlayerListener extends PlayerListener {
	private final CodeRedEconomy	plugin;
	
	public CodeRedPlayerListener(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
	
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();
		
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/buy")) {
				DataManager.getShop("The Shop").buy(DataManager.getUser(player), split);
			}
			if (split[0].equalsIgnoreCase("/sell")) {
				DataManager.getShop("The Shop").sell(DataManager.getUser(player), split);
			}
			// if (split[0].equalsIgnoreCase("/trade") && player.canUseCommand("/trade")) {
			//					
			// return true;
			// }
			if (split[0].equalsIgnoreCase("/balance")) {
				DataManager.getUser(player).showBalance();
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
			}
			if (split[0].equalsIgnoreCase("/shop") && DataManager.getDebug()) {
				System.out.println("There are " + DataManager.getShops().size());
				for (Shop iter : DataManager.getShops()) {
					System.out.println(iter.getName());
				}
			}
			if (split[0].equalsIgnoreCase("/balall") && DataManager.getDebug()) {
				for (User iter : DataManager.getUsers()) {
					System.out.println(iter.getName() + iter.getMoney().toString());
				}
			}
			if (split[0].equalsIgnoreCase("/saveredeco")) {
				DataManager.save();
			}
			if (split[0].equalsIgnoreCase("/prices")) {
				if (split.length >= 2) {
					int page = Integer.valueOf(split[1]);
					PriceList.priceList(DataManager.getUser(player), page, DataManager.getShop("The Shop"));
				}
				else {
					PriceList.priceList(DataManager.getUser(player), 1, DataManager.getShop("The Shop"));
				}
			}
			if (split[0].equalsIgnoreCase("/undo")) {
				DataManager.getUser(player).undoLastTrans();
			}
		}
	}
}
