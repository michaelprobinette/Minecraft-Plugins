/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import java.util.HashMap;

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
	private HashMap<String, String>	playerShops	= new HashMap<String, String>();
	
	public CodeRedPlayerListener(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
	
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();
		User user = DataManager.getUser(player);
		
		/*
		 * Go ahead and see if it is one of our commands. If it is, process it
		 */
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/buy")) {
				DataManager.getShop("The Shop").buy(user, split);
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/sell")) {
				DataManager.getShop("The Shop").sell(user, split);
				event.setCancelled(true);
			}
			// if (split[0].equalsIgnoreCase("/trade") && player.canUseCommand("/trade")) {
			//					
			// return true;
			// }
			else if (split[0].equalsIgnoreCase("/shops")) {
				
				/*
				 * Display the shops to the user
				 */
				if (split.length >= 2) {
					int page = Integer.valueOf(split[1]);
					ShopList.showPage(user, page);
				}
				else {
					ShopList.showPage(user, 1);
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/setshop")) {
				
				if (split.length >= 2) {
					String shopName = "";
					for (int i = 1; i < split.length; i++) {
						shopName += split[i] + " ";
					}
					
					if (DataManager.getDebug()) {
						System.out.println("Searching shop names for: \"" + shopName.trim() + "\"");
					}
					
					boolean found = false;
					
					for (Shop shopIter : DataManager.getShops()) {
						if (shopIter.getName().equalsIgnoreCase(shopName.trim())) {
							/*
							 * Valid shop
							 */
							playerShops.put(player.getName(), shopIter.getName());
							found = true;
							user.sendMessage("Set your active shop to: " + shopIter.getName());
							break;
						}
					}
					
					if (!found) {
						user.sendMessage("Please enter a valid shop name. Type /shops for a list.");
					}
				}
				else {
					String shopName = playerShops.get(user.getName());
					
					if (shopName == null) {
						/*
						 * Add them to default shop, "The Shop"
						 */
						shopName = "The Shop";
						
						playerShops.put(user.getName(), "The Shop");
					}
					user.sendMessage("Active shop is: " + shopName + ". To change use /setshop [shop name] Type /shops for a list.");
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/balance")) {
				user.showBalance();
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/pay")) {
				
				if (split.length >= 3) {
					Player pTarget = plugin.getServer().getPlayer(split[1].trim());
					if (pTarget != null) {
						User target = DataManager.getUser(pTarget);
						
						try {
							int amount = 0;
							amount = Integer.valueOf(split[2]);
							Money money = new Money(amount);
							
							if (amount > 0) {
								/*
								 * The way it is formatted the person receiving money is the seller, and the person loosing money is the buyer,
								 * so this is formatted target, user so the money goes from the user to the target
								 */
								Transaction.process(new Transaction(target, user, money));
							}
							else {
								user.sendMessage(money + " is not a valid amount.");
							}
						}
						catch (NumberFormatException e) {
							user.sendMessage("The correct use is /pay [name] [amount]");
						}
					}
					else {
						user.sendMessage("Player not online.");
					}
				}
				else {
					user.sendMessage("Correct use is /pay [player] [amount]");
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/add") && (DataManager.getDebug() || plugin.isOp(user.getName()))) {
				if (split.length >= 2) {
					if (split.length >= 3) {
						DataManager.getUser(split[1]).getMoney().addAmount(Integer.valueOf(split[2]));
					}
					else {
						user.getMoney().addAmount(Integer.valueOf(split[1]));
					}
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
				String shopName = playerShops.get(user.getName());
				
				if (shopName == null) {
					/*
					 * Add them to default shop, "The Shop"
					 */
					shopName = "The Shop";
					
					playerShops.put(user.getName(), "The Shop");
				}
				
				if (split.length >= 2) {
					try {
						int page = Integer.valueOf(split[1]);
						
						PriceList.priceList(user, page, DataManager.getShop(shopName));
					}
					catch (Exception e) {
						String itemName = event.getMessage().substring(7).trim().toLowerCase();
						PriceList.priceSingleItem(DataManager.getUser(player), itemName, DataManager.getShop("The Shop"));
					}
				}
				else {
					PriceList.priceList(user, 1, DataManager.getShop(shopName));
				}
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/undo")) {
				user.undoLastTrans();
				
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
			else if (split[0].equalsIgnoreCase("/econ")) {
				if (split.length >= 2) {
					if (split[1].equalsIgnoreCase("reset") && plugin.isOp(user.getName())) {
						if (split.length >= 3) {
							if (split[2].equalsIgnoreCase("shops") || split[2].equalsIgnoreCase("shop")) {
								
							}
							else if (split[2].equalsIgnoreCase("stats")) {
								user.sendMessage("Resetting stats.");
								EconStats.reset();
							}
						}
						else {
							user.sendMessage("Useage is /econ reset [items,stats]");
						}
					}
				}
				event.setCancelled(true);
			}
		}
	}
}
