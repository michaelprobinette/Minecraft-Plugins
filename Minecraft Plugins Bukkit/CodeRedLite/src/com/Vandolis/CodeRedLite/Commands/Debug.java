/**
 * 
 */
package com.Vandolis.CodeRedLite.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.Vandolis.CodeRedLite.CodeRedLite;

/**
 * @author Vandolis
 */
public class Debug implements CommandExecutor {
	private CodeRedLite	plugin		= null;
	private int			basePrice	= 250;
	private float		slope		= 1.0f;
	
	/**
	 * @param codeRedLite
	 */
	public Debug(CodeRedLite codeRedLite) {
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		int price = Integer.parseInt(split[0]);
		int amount = Integer.parseInt(split[1]);
		int totalBuy = 0;
		
		totalBuy = recursiveBuy(price, amount);
		if (amount == 0) {
			price = 0;
		}
		else {
			price = (int) (price / (amount * 1.0f));
		}
		
		sender.sendMessage("The total price to buy " + amount + " is " + quoteBuy(amount));
		sender.sendMessage("The total price to sell " + amount + " is " + quoteSell(amount));
		
		return true;
	}
	
	public int recursiveBuy(int price, int amount) {
		float slope = 1.0f;
		int newBuyPrice = 0;
		
		if (amount == 0) {
			return 0;
		}
		
		newBuyPrice = (int) (price / (amount * slope));
		
		return newBuyPrice + recursiveBuy(price, amount - 1);
	}
	
	public int recursiveSell(int price, int amount) {
		float slope = 1.0f;
		int newSellPrice = 0;
		
		if (amount == 0) {
			return 0;
		}
		
		newSellPrice = (int) (price / ((amount * slope) + 1));
		
		return newSellPrice + recursiveSell(price, amount - 1);
	}
	
	public int quoteBuy(int amount) {
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++) {
			runningTotal += Math.round((basePrice / ((getAmount() - x) * slope)));
		}
		
		return runningTotal;
	}
	
	public int quoteSell(int amount) {
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++) {
			runningTotal += Math.round((basePrice / (((getAmount() + x) * slope) + 1)));
		}
		
		return runningTotal;
	}
	
	public int getAmount() {
		return 4;
	}
}
