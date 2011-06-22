/**
 * 
 */
package com.Vandolis.CodeRedLite.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;

/**
 * @author Vandolis
 */
public class Price implements CommandExecutor {
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Price(CodeRedLite codeRedLite) {
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		String itemName = "";
		
		for (String iter : split) {
			itemName += iter + " ";
		}
		
		itemName = itemName.trim();
		
		boolean subtyped = false;
		short subtype = 0;
		
		if ((subtyped = itemName.contains(":")) == true) {
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
		
		EconItemStack item = null;
		
		if (subtyped) {
			item = plugin.getShop().getItem(itemName, subtype);
		}
		else {
			item = plugin.getShop().getItem(itemName);
		}
		
		if (item.isSubtyped()) {
			sender.sendMessage(plugin.getPluginMessage() + "Price results for " + item.getName() + " - " + subtype);
		}
		else {
			sender.sendMessage(plugin.getPluginMessage() + "Price results for " + item.getName());
		}
		if (item.isInfinite() || (item.getAmount() == -1)) {
			sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + item.getPriceBuy() + "    Sell: " + item.getPriceSell()
					+ "    Stock: Infinite");
		}
		else {
			sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + item.getPriceBuy() + "    Sell: " + item.getPriceSell()
					+ "    Stock: " + item.getAmount());
		}
		
		return true;
	}
}
