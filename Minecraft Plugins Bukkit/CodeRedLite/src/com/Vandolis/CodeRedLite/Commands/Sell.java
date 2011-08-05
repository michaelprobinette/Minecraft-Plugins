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
public class Sell implements CommandExecutor
{
	private CodeRedLite	plugin				= null;
	private final int	LEGAL_MIN_AMOUNT	= 1;
	
	/**
	 * @param codeRedLite
	 */
	public Sell(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command,
	 * java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		// Declare variables
		EconPlayer econPlayer = null;
		StringBuffer buf = null;
		String itemName = null;
		EconItemStack roughItem = null;
		EconItemStack item = null;
		int amount;
		short subtype;
		int available;
		
		// Initialize
		econPlayer = plugin.getEconPlayer((Player) sender);
		buf = new StringBuffer();
		itemName = "";
		amount = LEGAL_MIN_AMOUNT;
		subtype = 0;
		
		// Loop through the split to construct the item name and the item amount
		for (String iter : split)
		{
			// Try and convert into the amount, if that fails it must be part of the name.
			
			try
			{
				amount = Integer.valueOf(iter); // Try and parse the amount
			}
			catch (NumberFormatException e)
			{
				//Not a number, add to name.
				buf.append(iter);
			}
		}
		
		itemName = buf.toString().trim(); // Make the buffer a string, trim it
		itemName = itemName.toLowerCase(); // Make the item lower case
		
		// Check for legal amount
		if (amount < LEGAL_MIN_AMOUNT)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
			return true;
		}
		
		// Check for 'max' keyword
		if (itemName.contains("max"))
		{
			// keyword 'max' found
			
			// Remove 'max' from the item name
			itemName = itemName.replace("max", "");
			
			// Retrim the item name
			itemName = itemName.trim();
			amount = 100000000; // Arbitrary large number
		}
		
		// Check for declared subtype
		if (itemName.contains(":"))
		{
			// User is defining a subtype
			
			String[] args = itemName.split(":"); // Split around the colon
			
			itemName = args[0]; // Set the item name to the first half
			
			// Double check for the length
			if (args.length == 2)
			{
				// Safe length, try to get the subtype
				
				try
				{
					subtype = Short.parseShort(args[1]);
				}
				catch (Exception e)
				{
					sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
					return true;
				}
			}
			else
			{
				sender.sendMessage(plugin.getPluginMessage() + "Invalid syntax. Correct is 'item:subtype#'");
				return true;
			}
		}
		
		//plugin.getLOG().info("Item name is: \"" + itemName + "\"");
		
		// Get the item from the shop
		roughItem = plugin.getShop().getItem(itemName, subtype);
		
		//		if (subtyped)
		//		{
		//			roughItem = plugin.getShop().getItem(itemName, subtype);
		//		}
		//		else
		//		{
		//			roughItem = plugin.getShop().getItem(itemName);
		//		}
		
		// Check if the rough item is null (shop doesn't have it) and if you can use the whitelist
		if ((roughItem == null) && !plugin.getProperties().isEnforceItemWhitelist())
		{
			// Null and cannot use whitelist
			
			sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
			return true;
		}
		else if (roughItem == null)
		{
			// Null and can use whitelist
			
			// Check whitelist
			for (EconItemStack iter : plugin.getRawItems())
			{
				// Check compact name
				if (iter.getCompactName().equalsIgnoreCase(itemName))
				{
					// Check subtype
					if (iter.getDurability() == subtype)
					{
						roughItem = iter; // Found it, set it
					}
				}
			}
		}
		
		// Check if rough is null again
		if (roughItem == null)
		{
			// Still null, get out
			
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " will not buy that item.");
			return true;
		}
		
		available = 0; // The amount the player can hold
		
		// Loop through the players inventory
		for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents())
		{
			// Make sure it is not null
			if (iter != null)
			{
				// Check id and subtype
				if ((iter.getTypeId() == roughItem.getTypeId()) && (iter.getDurability() == roughItem.getDurability()))
				{
					// Same item type check how much more the stack can hold
					available += iter.getAmount();
				}
			}
		}
		
		// Check available against the item amount
		if (available < amount)
		{
			// Player has less than wanted
			
			amount = available; // Set to the amount available
		}
		
		// Check for 0
		if (amount == 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You do not have any of that item!");
			return true;
		}
		
		// Make the item with the rough item values and the given amount
		item = new EconItemStack(roughItem, amount, plugin);
		
		// Set the items total buy and total sell to the quoted values
		item.setTotalBuy(roughItem.quoteBuy(amount));
		item.setTotalSell(roughItem.quoteSell(amount));
		
		// Check the shops money
		if (plugin.getShop().isUseMoney())
		{
			// Shop uses money
			
			// Check the shops balance
			if ((plugin.getShop().getBalance() < item.getTotalBuy()) && (plugin.getShop().getBalance() != -1))
			{
				// Shop has less money than item is worth
				
				// Get the maxmimum the shop can buy
				int count = econPlayer.getBalance() / item.getPriceBuy();
				
				// Search for the largest amount it can afford
				for (int x = 0; x < count; x++)
				{
					// Change the item amount to x
					item.changeAmount(x);
					
					// Set the item prices
					item.setTotalBuy(roughItem.quoteBuy(x));
					item.setTotalSell(roughItem.quoteSell(x));
					
					// Check for too expensive
					if (item.getTotalBuy() > econPlayer.getBalance())
					{
						// Costs too much, go one down
						
						// Set the item amount to one less
						item.changeAmount(x - 1);
						
						// Set the item prices
						item.setTotalBuy(roughItem.quoteBuy(x - 1));
						item.setTotalSell(roughItem.quoteSell(x - 1));
						
						break; // Get out
					}
				}
			}
		}
		
		// Check for 0
		if (item.getAmount() == 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " cannot afford to buy any.");
			return true;
		}
		
		// Remove the item from the player
		int count = item.getAmount(); // Amount left to remove
		
		// Iterate through the players slots
		for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents())
		{
			// Check for null
			if (iter != null)
			{
				// Not null
				
				// Check id and subtype
				if ((iter.getTypeId() == item.getTypeId()) && (iter.getDurability() == item.getDurability()))
				{
					int pos = econPlayer.getPlayer().getInventory().first(iter); // Get the slot position
					
					// Check if there is more needed
					if (count - iter.getAmount() >= 0)
					{
						// Set the slot at position to empty space, used the whole stack
						econPlayer.getPlayer().getInventory().setItem(pos, null);
						count -= iter.getAmount(); // Resize the amount needed
					}
					else
					{
						// Doesn't use the whole stack
						
						// Resize the amount in the slot to the new amount
						econPlayer.getPlayer().getInventory()
								.setItem(pos, new ItemStack(iter.getTypeId(), iter.getAmount() - count, subtype));
						
						break; // Get out
					}
				}
			}
		}
		
		/*
		 * Good to go, do it
		 */

		plugin.getShop().addItem(item); // Add the item to the shop
		econPlayer.addMoney(item.getTotalSell()); // Add the money to the player
		plugin.getShop().removeMoney(item.getTotalBuy()); // Remove the money from the shop
		
		// Send messages
		sender.sendMessage(plugin.getPluginMessage() + "You sold " + item.getAmount() + " " + item.getName() + " for "
				+ item.getTotalSell() + " " + plugin.getProperties().getMoneyName());
		sender.sendMessage(plugin.getPluginMessage() + "Your new balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName());
		
		econPlayer.update(); // Update the player SQL
		
		// Log the sell
		try
		{
			plugin.getSQL().logSell(econPlayer, item);
		}
		catch (SQLException e)
		{
			plugin.getLog().log(Level.WARNING, e.getLocalizedMessage());
			plugin.getLog().log(Level.WARNING, "CodeRedLite could not update a new sell.");
		}
		
		return true;
	}
}
