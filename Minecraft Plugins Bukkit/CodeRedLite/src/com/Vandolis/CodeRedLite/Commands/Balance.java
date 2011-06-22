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
public class Balance implements CommandExecutor {
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public Balance(CodeRedLite codeRedLite) {
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split) {
		EconPlayer econPlayer = plugin.getEconPlayer((Player) sender);
		
		sender.sendMessage(plugin.getPluginMessage() + "Your current balance is: " + econPlayer.getBalance() + " "
				+ plugin.getProperties().getMoneyName() + ".");
		
		return true;
	}
	
}
