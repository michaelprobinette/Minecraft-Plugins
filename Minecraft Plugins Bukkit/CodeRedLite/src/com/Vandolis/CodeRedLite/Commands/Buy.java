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
public class Buy implements CommandExecutor
{
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Buy(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		EconPlayer econPlayer = plugin.getEconPlayer((Player) sender);
		
		String itemName = "";
		int amount = 1;
		
		for (String iter : split)
		{
			/*
			 * Try and convert into the amount, if that fails it must be part of the name.
			 */
			try
			{
				amount = Integer.valueOf(iter);
			}
			catch (NumberFormatException e)
			{
				/*
				 * Not a number, add to name.
				 */
				itemName += iter;
			}
		}
		
		itemName = itemName.trim();
		itemName = itemName.toLowerCase();
		
		if (amount <= 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
			return true;
		}
		
		boolean subtyped = itemName.contains(":");
		short subtype = 0;
		
		if (subtyped)
		{
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
		
		if (itemName.contains("max"))
		{
			itemName = itemName.replace("max", "");
			amount = 100000000;
		}
		
		//plugin.getLOG().info("Item name is: \"" + itemName + "\"");
		
		EconItemStack roughItem = null;
		
		if (subtyped)
		{
			roughItem = plugin.getShop().getItem(itemName, subtype);
		}
		else
		{
			roughItem = plugin.getShop().getItem(itemName);
		}
		
		if (roughItem == null)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
			return true;
		}
		
		// Check amount
		if ((!roughItem.isInfinite()) && (roughItem.getAmount() != -1))
		{
			if (roughItem.getAmount() < amount)
			{
				amount = roughItem.getAmount();
			}
		}
		
		if (amount == 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " does not have enough of that item.");
			return true;
		}
		
		EconItemStack item = new EconItemStack(roughItem, amount, plugin);
		
		item.setTotalBuy(roughItem.quoteBuy(amount));
		item.setTotalSell(roughItem.quoteSell(amount));
		
		// Resize to amount the player can afford
		if (econPlayer.getBalance() < item.getTotalBuy())
		{
			int count = econPlayer.getBalance() / item.getPriceBuy();
			
			for (int x = 0; x < count; x++)
			{
				item.changeAmount(x);
				if (item.getTotalBuy() > econPlayer.getBalance())
				{
					item.changeAmount(x - 1);
					
					item.setTotalBuy(roughItem.quoteBuy(x - 1));
					item.setTotalSell(roughItem.quoteSell(x - 1));
					break;
				}
			}
			
			//item = new EconItemStack(roughItem, count);
		}
		
		if (item.getAmount() <= 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You cannot afford to buy any!");
			return true;
		}
		
		// Check inventory space
		int available = 0; // The amount the player can hold
		for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents())
		{
			if (iter == null)
			{
				// Empty slot
				available += 64;
			}
			else if (item.isSubtyped())
			{
				if ((iter.getTypeId() == item.getTypeId()) && (iter.getDurability() == item.getDurability()))
				{
					// Same item type check how much more the stack can hold
					available += (64 - iter.getAmount());
				}
			}
			else
			{
				if (iter.getTypeId() == item.getTypeId())
				{
					available += (64 - iter.getAmount());
				}
			}
		}
		
		if (available < item.getAmount())
		{
			item.changeAmount(available);
			item.setTotalBuy(roughItem.quoteBuy(available));
			item.setTotalSell(roughItem.quoteSell(available));
		}
		
		if (item.getAmount() <= 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You do not have enough space.");
			return true;
		}
		
		econPlayer.removeMoney(item.getTotalBuy());
		
		plugin.getShop().addMoney(item.getTotalSell());
		
		econPlayer.getPlayer().getInventory().addItem(item);
		
		plugin.getShop().removeItem(item);
		
		sender.sendMessage(plugin.getPluginMessage() + "You bought " + item.getAmount() + " " + item.getName() + " for "
				+ item.getTotalBuy() + " " + plugin.getProperties().getMoneyName());
		sender.sendMessage(plugin.getPluginMessage() + "Your new balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName());
		
		econPlayer.update();
		plugin.getShop().updateItem(item);
		
		try
		{
			plugin.getSQL().logBuy(econPlayer, item);
		}
		catch (SQLException e)
		{
			plugin.getLog().log(Level.WARNING, "CodeRedLite could not update a new buy. " + e.getLocalizedMessage());
		}
		
		return true;
	}
}
