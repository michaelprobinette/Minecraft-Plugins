/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Player Listener for handling all {@link PlayerEvent}
 * 
 * @author Vandolis
 */
public class CodeRedPlayerListener extends PlayerListener {
	private final CodeRedEconomy	plugin;
	
	public CodeRedPlayerListener(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
	
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();
		
		/*
		 * Go ahead and see if it is one of our commands. If it is, process it
		 */
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/buy")) {
				DataManager.getShop("The Shop").buy(DataManager.getUser(player), split);
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/sell")) {
				DataManager.getShop("The Shop").sell(DataManager.getUser(player), split);
				event.setCancelled(true);
			}
			// if (split[0].equalsIgnoreCase("/trade") && player.canUseCommand("/trade")) {
			//					
			// return true;
			// }
			
			else if (split[0].equalsIgnoreCase("/balance")) {
				DataManager.getUser(player).showBalance();
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/pay")) {
				User user = DataManager.getUser(player);
				
				if (split.length >= 3) {
					User target = DataManager.getUser(split[1].trim());
					
					try {
						/*
						 * The way it is formatted the person receiving money is the seller, and the person loosing money is the buyer,
						 * so this is formatted target, user so the money goes from the user to the target
						 */
						Transaction.process(new Transaction(target, user, new Money(Integer.valueOf(split[2]))));
					}
					catch (NumberFormatException e) {
						user.sendMessage("The correct use is \"/pay [name] [amount]\"");
					}
				}
				else {
					user.sendMessage("Correct use is \"/pay [player] [amount]\"");
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/add") && DataManager.getDebug()) {
				if (split.length >= 2) {
					if (split.length >= 3) {
						DataManager.getUser(split[1]).getMoney().addAmount(Integer.valueOf(split[2]));
					}
					else {
						DataManager.getUser(player).getMoney().addAmount(Integer.valueOf(split[1]));
					}
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/shop") && DataManager.getDebug()) {
				System.out.println("There are " + DataManager.getShops().size());
				
				for (Shop iter : DataManager.getShops()) {
					System.out.println(iter.getName());
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/balall") && DataManager.getDebug()) {
				for (User iter : DataManager.getUsers()) {
					System.out.println(iter.getName() + iter.getMoney().toString());
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/redeconsave")) {
				player.sendMessage(DataManager.getPluginMessage() + "Saving data...");
				
				DataManager.save();
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/prices")) {
				if (split.length >= 2) {
					int page = Integer.valueOf(split[1]);
					
					PriceList.priceList(DataManager.getUser(player), page, DataManager.getShop("The Shop"));
				}
				else {
					PriceList.priceList(DataManager.getUser(player), 1, DataManager.getShop("The Shop"));
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/undo")) {
				DataManager.getUser(player).undoLastTrans();
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/restock") && (DataManager.getDebug() || plugin.isOp(player.getName()))) {
				player.sendMessage(DataManager.getPluginMessage() + "Forcing all shops to restock.");
				
				System.out.println(player.getName() + " is force restocking all shops.");
				
				for (Shop iter : DataManager.getShops()) {
					iter.restock(true);
				}
				
				event.setCancelled(true);
			}
		}
	}
}
