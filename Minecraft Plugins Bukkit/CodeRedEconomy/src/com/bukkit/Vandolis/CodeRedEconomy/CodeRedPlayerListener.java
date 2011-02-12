/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import com.bukkit.Vandolis.CodeRedEconomy.CommandInterpreter.Command;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.DataManager;

/**
 * Player Listener for handling all {@link PlayerEvent}
 * 
 * @author Vandolis
 */
public class CodeRedPlayerListener extends PlayerListener {
	
	public CodeRedPlayerListener(CodeRedEconomy codeRedEconomy) {
		CommandInterpreter.setPlugin(codeRedEconomy);
	}
	
	public void onPlayerChat(PlayerChatEvent event) {
		CommandInterpreter.interpret(Command.BAD_WORD, event);
	}
	
	@Override
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		Player player = event.getPlayer();
		
		/*
		 * Go ahead and see if it is one of our commands. If it is, process it
		 */
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/buy")) {
				CommandInterpreter.interpret(Command.BUY, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/sell")) {
				CommandInterpreter.interpret(Command.SELL, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/balance") || split[0].equalsIgnoreCase("/money")) {
				CommandInterpreter.interpret(Command.BALANCE, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/pay")) {
				CommandInterpreter.interpret(Command.PAY, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/prices")) {
				CommandInterpreter.interpret(Command.PRICES, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/undo")) {
				CommandInterpreter.interpret(Command.UNDO, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/restock") && (DataManager.getDebug() || CodeRedEconomy.isOp(player.getName()))) {
				CommandInterpreter.interpret(Command.RESTOCK, event);
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/econ")) {
				CommandInterpreter.interpret(Command.ECON, event);
				
				event.setCancelled(true);
			}
		}
	}
}
