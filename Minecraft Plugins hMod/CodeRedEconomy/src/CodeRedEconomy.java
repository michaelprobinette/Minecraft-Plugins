/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

import java.util.logging.Logger;

public class CodeRedEconomy extends Plugin {
	private Listener				l		= new Listener(this);
	protected static final Logger	log		= Logger.getLogger("Minecraft");
	private String					name	= "CodeRedEconomy";
	private String					version	= "v0.5.0";
	
	public void enable() {
		etc.getInstance().addCommand("/pay", "- Pays the given player. /pay [name] [amount]");
		etc.getInstance().addCommand("/buy", "- Buys the given item. /buy [itemName] [amount]");
		etc.getInstance().addCommand("/sell", "- Sells the given item. /sell [itemName] [amount]");
		etc.getInstance().addCommand("/prices", "- Displays the price list for the current shop. /prices [page]");
		etc.getInstance().addCommand("/undo", "- Undoes the last item transaction. /undo");
	}
	
	public void disable() {
		etc.getInstance().removeCommand("/pay");
		etc.getInstance().removeCommand("/buy");
		etc.getInstance().removeCommand("/sell");
		etc.getInstance().removeCommand("/undo");
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SERVERCOMMAND, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_BROKEN, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.CHAT, l, this, PluginListener.Priority.MEDIUM);
		
		DataManager.load();
		// DataManager.getDebug() = data.getDataManager.getDebug()();
	}
	
	public class Listener extends PluginListener {
		CodeRedEconomy	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(CodeRedEconomy plugin) {
			p = plugin;
		}
		
		public boolean onChat(Player player, String message) {
			String msg = message;
			String badWord = "";
			User user = DataManager.getUser(player);
			int penalty = 0;
			while (!(badWord = DataManager.getBadWord(msg)).equalsIgnoreCase("")) {
				penalty = DataManager.getBadWords().get(badWord);
				User target = DataManager.getUser("BadWord- " + badWord);
				
				if (Transaction.process(new Transaction(target, user, new Money(penalty)), false) == 0 && DataManager.messageOnBadWord()) {
					// Sends a forced silent transaction. No messages and forces allowed
					user.sendMessage("You have been penalized " + penalty + " " + Money.getMoneyName() + " for the use of the bad word \""
							+ badWord + "\". Please refrain from using this word in the future.");
				}
				
				msg = msg.replace(badWord, "");
			}
			if (penalty != 0) {
				return DataManager.blockBadWords();
			}
			return false;
		}
		
		public boolean onBlockBreak(Player player, Block block) {
			// Check if it is a valid block
			if (DataManager.validID(block.getType())) {
				DataManager.getUser(player).getMoney().addAmount(DataManager.getItem(block.getType()).getBreakValue());
			}
			
			return false;
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
						String targetName = split[1].trim();
						if (etc.getServer().getPlayer(targetName) != null) {
							User target = DataManager.getUser(targetName);
							try {
								// The way it is formatted the person receiving money is the seller, and the person loosing money is the
								// buyer,
								// so this is formatted target, user so the money goes from the user to the target
								Transaction.process(new Transaction(target, user, new Money(Integer.valueOf(split[2]))));
							}
							catch (NumberFormatException e) {
								user.sendMessage("The correct use is \"/pay [name] [amount]\"");
							}
						}
						else {
							user.sendMessage("The player you wish to pay must be online.");
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
