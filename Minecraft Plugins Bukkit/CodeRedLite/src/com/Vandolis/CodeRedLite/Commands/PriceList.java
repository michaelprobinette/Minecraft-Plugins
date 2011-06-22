/**
 * 
 */
package com.Vandolis.CodeRedLite.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;

/**
 * @author Vandolis
 */
public class PriceList implements CommandExecutor {
	private CodeRedLite			plugin			= null;
	private final int			MAX_PER_PAGE	= 8;
	private ArrayList<String>	raw				= new ArrayList<String>();
	
	/**
	 * @param codeRedLite
	 */
	public PriceList(CodeRedLite codeRedLite) {
		plugin = codeRedLite;
	}
	
	public void sort() {
		raw = new ArrayList<String>();
		
		for (EconItemStack iter : plugin.getShop().getInventory()) {
			if (iter.isSubtyped()) {
				raw.add(iter.getCompactName() + ":" + iter.getDurability());
			}
			else {
				raw.add(iter.getCompactName());
			}
		}
		
		Collections.sort(raw);
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		sort();
		
		int pageNumber = 0;
		
		if (split.length == 1) {
			try {
				pageNumber = Integer.parseInt(split[0]);
			}
			catch (Exception e) {
			}
		}
		
		ArrayList<String> processed = new ArrayList<String>();
		
		for (String iter : raw) {
			boolean subtyped = false;
			short subtype = 0;
			String itemName = "";
			
			if ((subtyped = iter.contains(":")) == true) {
				String[] args = iter.split(":");
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
				item = plugin.getShop().getItem(iter);
			}
			
			String str = "";
			
			if (subtyped) {
				str = "   " + itemName + ":" + subtype;
			}
			else {
				str = "   " + iter;
			}
			
			processed.add(str);
			
			if (item.isInfinite() || (item.getAmount() == -1)) {
				str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() + "   Stock: Infinite";
			}
			else {
				str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() + "   Stock: " + item.getAmount();
			}
			
			processed.add(str);
		}
		
		HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
		
		int count = 0;
		ArrayList<String> page = new ArrayList<String>();
		
		while (processed.isEmpty() == false) {
			page.add(processed.get(0));
			//plugin.getLog().info("Added: " + processed.get(0));
			processed.remove(0);
			if (page.size() == MAX_PER_PAGE) {
				//plugin.getLog().info("Adding page to map");
				map.put(count, page);
				count++;
				page = new ArrayList<String>();
			}
		}
		
		if (page.isEmpty() == false) {
			map.put(count, page);
		}
		
		sender.sendMessage(plugin.getPluginMessage() + "PriceList for " + plugin.getShop().getName() + " (Page " + pageNumber + " of "
				+ (map.size() - 1) + ")");
		
		page = map.get(pageNumber);
		
		for (String iter : page) {
			sender.sendMessage(plugin.getPluginMessage() + iter);
		}
		
		return true;
	}
}
