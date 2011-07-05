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
public class Debug implements CommandExecutor
{
	
	/**
	 * @param codeRedLite
	 */
	public Debug(CodeRedLite codeRedLite)
	{
		
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String, java.lang.String[])
	 */
	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
	{
		return true;
	}
}
