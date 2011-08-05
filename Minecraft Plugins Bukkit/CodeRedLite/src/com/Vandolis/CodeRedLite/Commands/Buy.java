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
	private CodeRedLite	plugin				= null;
	private final int	LEGAL_MIN_AMOUNT	= 1;
	private final int	MAX_STACK_SIZE		= 64;
	
	/**
	 * @param codeRedLite
	 */
	public Buy(CodeRedLite codeRedLite)
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
		EconPlayer econPlayer = null; // Holds the EconPlayer
		StringBuffer buf = null; // String buffer for constructing item name
		String itemName = null; // Holds the item name.
		int amount; // Item amount
		short subtype; // Item subtype
		EconItemStack roughItem = null; // Holds the item from the shop/raw item
		EconItemStack item = null; // Holds the item and amounts to be added
		int available; // Largest amount available
		
		// Initializations
		econPlayer = plugin.getEconPlayer((Player) sender); // Get the EconPlayer from the plugin
		buf = new StringBuffer(); // Setup buffer
		itemName = ""; // New string
		subtype = 0; // Default subtype is 0
		amount = LEGAL_MIN_AMOUNT; // Default amount
		
		// Loop through the split to construct the item name and the item amount
		for (String iter : split)
		{
			// Try and convert into the amount, if that fails it must be part of the name.
			try
			{
				amount = Integer.valueOf(iter); // Try and parse it
			}
			catch (NumberFormatException e)
			{
				// Not a number, add to name.
				
				buf.append(iter); // Append it to the buffer
			}
		}
		
		itemName = buf.toString().trim(); // Make the buffer a string, trim it
		itemName = itemName.toLowerCase(); // Make the item lower case
		
		// Check if legal amount
		if (amount < LEGAL_MIN_AMOUNT)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
			return true; // Get out
		}
		
		// Check for 'max' keyword to buy the maximum amount
		if (itemName.contains("max"))
		{
			// 'max' found
			
			// Remove the word max from the item name
			itemName = itemName.replace("max", "");
			
			// Trim the item name
			itemName = itemName.trim();
			
			// Set the amount to extremely large amount.
			amount = 100000000;
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
		
		// Get the rough item from the shop
		roughItem = plugin.getShop().getItem(itemName, subtype);
		
		//		if (subtyped)
		//		{
		//			roughItem = plugin.getShop().getItem(itemName, subtype);
		//		}
		//		else
		//		{
		//			roughItem = plugin.getShop().getItem(itemName);
		//		}
		
		// Check for invalid rough item (the shop doesn't have it)
		if (roughItem == null)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
			return true; // Get out
		}
		
		// Check amount
		if ((!roughItem.isInfinite()) && (roughItem.getAmount() != -1))
		{
			// Check if the shop has less than the player asked for
			if (roughItem.getAmount() < amount)
			{
				// Shop has less
				
				// Set the new amount to the current amount the shop has
				amount = roughItem.getAmount();
			}
		}
		
		// Check for shop having 0
		if (amount == 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName()
				+ " does not have enough of that item.");
			return true;
		}
		
		// Create the item with the roughItem data, and the given amount
		item = new EconItemStack(roughItem, amount, plugin);
		
		// Set the items total buy and total sell to the quoted values
		item.setTotalBuy(roughItem.quoteBuy(amount));
		item.setTotalSell(roughItem.quoteSell(amount));
		
		// Resize to amount the player can afford
		if (econPlayer.getBalance() < item.getTotalBuy())
		{
			int count = econPlayer.getBalance() / item.getPriceBuy(); // Count the largest whole number they can buy
			
			// Check against the quoted values
			for (int x = 0; x < count; x++)
			{
				item.changeAmount(x); // Change to the new amount
				
				// Change the item prices
				item.setTotalBuy(roughItem.quoteBuy(x));
				item.setTotalSell(roughItem.quoteSell(x));
				
				// Check the price
				if (item.getTotalBuy() > econPlayer.getBalance())
				{
					// Price too high, go down one
					
					item.changeAmount(x - 1); // Change to last valid price
					
					// Set the buy/sell values
					item.setTotalBuy(roughItem.quoteBuy(x - 1));
					item.setTotalSell(roughItem.quoteSell(x - 1));
					
					break; // Done with loop
				}
			}
			
			//item = new EconItemStack(roughItem, count);
		}
		
		// Double check the amount
		if (item.getAmount() < LEGAL_MIN_AMOUNT)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You cannot afford to buy any!");
			return true;
		}
		
		// Check inventory space
		available = 0; // The amount the player can hold
		
		plugin.getLog().info("Item max stack size: ");
		
		// Loop through the players inventory
		for (ItemStack iter : econPlayer.getPlayer().getInventory().getContents())
		{
			// If the slot is null, it can hold a full stack
			if (iter == null)
			{
				// Empty slot
				available += MAX_STACK_SIZE;
			}
			else if ((iter.getTypeId() == item.getTypeId()) && (iter.getDurability() == item.getDurability()))
			{
				// Same item type check how much more the stack can hold
				available += (MAX_STACK_SIZE - iter.getAmount());
			}
		}
		
		// Check the available against the item amount
		if (available < item.getAmount())
		{
			// Available is less than the item amount
			
			item.changeAmount(available); // Change the item amount
			
			// Set the items buy/sell
			item.setTotalBuy(roughItem.quoteBuy(available));
			item.setTotalSell(roughItem.quoteSell(available));
		}
		
		// Check for legal amount
		if (item.getAmount() < LEGAL_MIN_AMOUNT)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You do not have enough space.");
			return true;
		}
		
		/*
		 * All good from checks, lets do this thing
		 */

		econPlayer.removeMoney(item.getTotalBuy()); // Remomve the money from the player
		
		plugin.getShop().addMoney(item.getTotalSell()); // Add the money to the shop
		
		econPlayer.getPlayer().getInventory().addItem(item); // Add the item to the players inventory
		
		plugin.getShop().removeItem(item); // Remove the item from the shop
		
		sender.sendMessage(plugin.getPluginMessage() + "You bought " + item.getAmount() + " " + item.getName()
			+ " for "
				+ item.getTotalBuy() + " " + plugin.getProperties().getMoneyName());
		sender.sendMessage(plugin.getPluginMessage() + "Your new balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName());
		
		econPlayer.update(); // Update the sql
		//plugin.getShop().updateItem(item);
		
		// Log the buy
		try
		{
			plugin.getSQL().logBuy(econPlayer, item);
		}
		catch (SQLException e)
		{
			plugin.getLog().log(Level.WARNING, e.getLocalizedMessage());
			plugin.getLog().log(Level.WARNING, "CodeRedLite could not update a new buy.");
		}
		
		return true;
	}
}
