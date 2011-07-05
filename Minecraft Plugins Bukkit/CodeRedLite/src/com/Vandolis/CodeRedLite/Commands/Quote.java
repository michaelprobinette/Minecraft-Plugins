/**
 *
 */
package com.Vandolis.CodeRedLite.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;

/**
 * @author Vandolis
 */
public class Quote implements CommandExecutor
{
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Quote(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		if (split.length >= 3)
		{
			StringBuffer buf = new StringBuffer();
			String itemName = "";
			int amount = 1;
			
			for (String iter : split)
			{
				if (!iter.equalsIgnoreCase(split[0]))
				{
					try
					{
						amount = Integer.parseInt(iter);
					}
					catch (Exception e)
					{
						//itemName += iter;
						buf.append(iter);
					}
				}
			}
			
			itemName = buf.toString().trim();
			itemName = itemName.toLowerCase();
			
			boolean subtyped = false;
			short subtype = 0;
			
			if (itemName.equalsIgnoreCase("wool"))
			{
				subtyped = true;
				subtype = 0;
			}
			
			if (itemName.contains(":"))
			{
				subtyped = itemName.contains(":");
				String[] args = itemName.split(":");
				itemName = args[0];
				if (args.length == 2)
				{
					subtype = Short.parseShort(args[1]);
				}
				else
				{
					sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
					return true;
				}
			}
			
			EconItemStack roughItem = null;
			
			if (subtyped)
			{
				roughItem = plugin.getShop().getItem(itemName, subtype);
			}
			else
			{
				roughItem = plugin.getShop().getItem(itemName);
			}
			
			if ((roughItem == null) && !plugin.getProperties().isEnforceItemWhitelist())
			{
				sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
				return true;
			}
			else if (roughItem == null)
			{
				// Check whitelist
				for (EconItemStack iter : plugin.getRawItems())
				{
					if (iter.getCompactName().equalsIgnoreCase(itemName))
					{
						if (subtyped)
						{
							if (iter.getDurability() == subtype)
							{
								roughItem = iter;
							}
						}
						else
						{
							roughItem = iter;
						}
					}
				}
			}
			
			if (roughItem == null)
			{
				sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " will not buy that item.");
				return true;
			}
			
			if (roughItem.isSubtyped())
			{
				sender.sendMessage(plugin.getPluginMessage() + "Price results for " + amount + " "
					+ roughItem.getName() + ":" + subtype);
			}
			else
			{
				sender.sendMessage(plugin.getPluginMessage() + "Price results for " + amount + " "
					+ roughItem.getName());
			}
			if (roughItem.isInfinite() || (roughItem.getAmount() == -1))
			{
				if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
				{
					sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
						+ "    Sell: "
							+ roughItem.quoteSell(amount) + "    Stock: Infinite    Base: " + roughItem.getBasePrice()
						+ "    Slope: "
							+ roughItem.getSlope());
				}
				else
				{
					sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
						+ "    Sell: "
							+ roughItem.quoteSell(amount) + "    Stock: Infinite");
				}
			}
			else
			{
				if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
				{
					sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
						+ "    Sell: "
							+ roughItem.quoteSell(amount) + "    Stock: " + roughItem.getAmount() + "    Base: "
						+ roughItem.getBasePrice()
							+ "    Slope: " + roughItem.getSlope());
				}
				else
				{
					sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
						+ "    Sell: "
							+ roughItem.quoteSell(amount) + "    Stock: " + roughItem.getAmount());
				}
			}
		}
		
		return true;
	}
}
