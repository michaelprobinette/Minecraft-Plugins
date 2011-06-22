/**
 * 
 */
package com.Vandolis.CodeRedLite.Commands;

import java.sql.SQLException;
import java.util.logging.Level;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class Sell implements CommandExecutor {
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Sell(CodeRedLite codeRedLite) {
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		EconPlayer econPlayer = plugin.getEconPlayer((Player) sender);
		
		String itemName = "";
		int amount = 1;
		
		for (String iter : split) {
			/*
			 * Try and convert into the amount, if that fails it must be part of the name.
			 */
			try {
				amount = Integer.valueOf(iter);
			}
			catch (NumberFormatException e) {
				/*
				 * Not a number, add to name.
				 */
				itemName += iter;
			}
		}
		
		itemName = itemName.trim();
		itemName = itemName.toLowerCase();
		
		if (amount <= 0) {
			sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
			return true;
		}
		
		boolean subtyped = false;
		short subtype = 0;
		
		if (itemName.equalsIgnoreCase("wool")) {
			subtyped = true;
			subtype = 0;
		}
		
		if (itemName.contains(":") == true) {
			subtyped = itemName.contains(":");
			String[] args = itemName.split(":");
			itemName = args[0];
			if (args.length == 2) {
				subtype = Short.parseShort(args[1]);
			}
			else {
				sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
				return true;
			}
		}
		
		if (itemName.contains("max")) {
			itemName = itemName.replace("max", "");
			amount = 100000000;
		}
		
		//plugin.getLOG().info("Item name is: \"" + itemName + "\"");
		EconItemStack roughItem = null;
		
		if (subtyped) {
			roughItem = plugin.getShop().getItem(itemName, subtype);
		}
		else {
			roughItem = plugin.getShop().getItem(itemName);
		}
		
		if ((roughItem == null) && !plugin.getProperties().isEnforceItemWhitelist()) {
			sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
			return true;
		}
		else if (roughItem == null) {
			// Check whitelist
			for (EconItemStack iter : plugin.getRawItems()) {
				if (iter.getCompactName().equalsIgnoreCase(itemName)) {
					if (subtyped) {
						if (iter.getDurability() == subtype) {
							roughItem = iter;
						}
					}
					else {
						roughItem = iter;
					}
				}
			}
		}
		
		if (roughItem == null) {
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " will not buy that item.");
			return true;
		}
		
		int available = 0; // The amount the player can hold
		for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents()) {
			if (iter == null) {
			}
			else if (roughItem.isSubtyped()) {
				if ((iter.getTypeId() == roughItem.getTypeId()) && (iter.getDurability() == roughItem.getDurability())) {
					// Same item type check how much more the stack can hold
					available += iter.getAmount();
				}
			}
			else {
				if (iter.getTypeId() == roughItem.getTypeId()) {
					available += iter.getAmount();
				}
			}
		}
		
		if (available < amount) {
			amount = available;
		}
		
		if (amount == 0) {
			sender.sendMessage(plugin.getPluginMessage() + "You do not have any of that item!");
			return true;
		}
		
		EconItemStack item = new EconItemStack(roughItem, amount, plugin);
		item.setTotalSell(roughItem.quoteSell(amount));
		
		if (plugin.getShop().isUseMoney()) {
			if (plugin.getShop().getBalance() < item.getTotalBuy()) {
				int count = econPlayer.getBalance() / item.getPriceBuy();
				
				for (int x = 0; x < count; x++) {
					item.changeAmount(x);
					if (item.getTotalBuy() > econPlayer.getBalance()) {
						item.changeAmount(x - 1);
						item.setTotalBuy(roughItem.quoteBuy(x - 1));
						item.setTotalSell(roughItem.quoteSell(x - 1));
						break;
					}
				}
			}
		}
		
		if (item.getAmount() == 0) {
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " cannot afford to buy any.");
			return true;
		}
		
		if (subtyped) {
			int count = item.getAmount();
			
			for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents()) {
				if (iter == null) {
				}
				else if ((iter.getTypeId() == item.getTypeId()) && (iter.getDurability() == item.getDurability())) {
					int pos = econPlayer.getPlayer().getInventory().first(iter);
					
					if (count - iter.getAmount() >= 0) {
						econPlayer.getPlayer().getInventory().setItem(pos, null);
						count -= iter.getAmount();
						if (count == 0) {
							break;
						}
					}
					else {
						econPlayer.getPlayer().getInventory()
								.setItem(pos, new ItemStack(iter.getTypeId(), iter.getAmount() - count, subtype));
						break;
					}
				}
			}
		}
		else {
			econPlayer.getPlayer().getInventory().removeItem(item);
		}
		
		plugin.getShop().addItem(item);
		econPlayer.addMoney(item.getTotalSell());
		plugin.getShop().removeMoney(item.getTotalBuy());
		
		sender.sendMessage(plugin.getPluginMessage() + "You sold " + item.getAmount() + " " + item.getName() + " for "
				+ item.getTotalSell() + " " + plugin.getProperties().getMoneyName());
		sender.sendMessage(plugin.getPluginMessage() + "Your new balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName());
		
		econPlayer.update();
		plugin.getShop().updateItem(item);
		
		try {
			plugin.getSQL().logSell(econPlayer, item);
		}
		catch (SQLException e) {
			plugin.getLog().log(Level.WARNING, "CodeRedLite could not update a new sell. " + e.getLocalizedMessage());
		}
		
		return true;
	}
}
