/**
 *
 */
package com.Vandolis.CodeRedLite.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class Pay implements CommandExecutor
{
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Pay(CodeRedLite codeRedLite)
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
		
		int amount = 0;
		EconPlayer target = null;
		
		if (split.length != 2)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid parameters.");
			return true;
		}
		
		for (String iter : split)
		{
			try
			{
				amount = Integer.parseInt(iter);
			}
			catch (Exception e)
			{
				target = plugin.getEconPlayer(iter);
			}
		}
		
		if (target == null)
		{
			sender.sendMessage(plugin.getPluginMessage() + "The target must be online to pay them.");
			return true;
		}
		
		if (amount <= 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
			return true;
		}
		
		if ((amount > econPlayer.getBalance()) && (econPlayer.getBalance() != -1))
		{
			amount = econPlayer.getBalance();
		}
		
		if (amount <= 0)
		{
			sender.sendMessage(plugin.getPluginMessage() + "You do not have any " + plugin.getProperties().getMoneyName());
			return true;
		}
		
		// Good to go
		econPlayer.removeMoney(amount);
		target.addMoney(amount);
		
		sender.sendMessage(plugin.getPluginMessage() + "You have paid " + target.getPlayer().getName() + " " + amount + " "
				+ plugin.getProperties().getMoneyName());
		sender.sendMessage(plugin.getPluginMessage() + "Your new balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName());
		
		target.getPlayer().sendMessage(
				plugin.getPluginMessage() + econPlayer.getPlayer().getName() + " has paid you " + amount + " "
						+ plugin.getProperties().getMoneyName());
		target.getPlayer().sendMessage(
				plugin.getPluginMessage() + "Your new balance is: " + target.getBalance() + " " + plugin.getProperties().getMoneyName());
		
		econPlayer.update();
		target.update();
		
		return true;
	}
}
