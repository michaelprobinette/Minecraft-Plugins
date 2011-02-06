/**
 * 
 */
package com.Vandolis.TheWall;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.DataManager;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.User;

/**
 * @author Vandolis
 */
public class TheWallPlayerListener extends PlayerListener {
	private final TheWall	plugin;
	
	/**
	 * 
	 */
	public TheWallPlayerListener(TheWall instance) {
		plugin = instance;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		String split[] = event.getMessage().split(" ");
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/thewall")) {
				Player p = event.getPlayer();
				User user = DataManager.getUser(p);
				if (split.length >= 2) {
					if (split[1].equalsIgnoreCase("create") && plugin.getEcon().isOp(user.getName())) {
						if (plugin.getEcon().isOp(p.getName())) {
							if (plugin.getWall() == null) {
								plugin.setWall(new Wall(plugin, p.getLocation()));
								user.sendMessage("Wall created.");
							}
							else {
								user.sendMessage("There is already a wall active.");
							}
						}
						else {
							user.sendMessage("You do not have the required permission.");
						}
					}
					else if (split[1].equalsIgnoreCase("destroy") && plugin.getEcon().isOp(user.getName())) {
						plugin.getWall().destroy();
						plugin.setWall(null);
						user.sendMessage("The Wall was destroyed.");
					}
					else if (split[1].equalsIgnoreCase("show") && plugin.getEcon().isOp(user.getName())) {
						if (plugin.getWall() != null) {
							if (plugin.getWall().isHidden()) {
								plugin.getWall().create();
								user.sendMessage("Wall shown.");
							}
							else {
								user.sendMessage("The wall is already shown.");
							}
						}
						else {
							user.sendMessage("There is no wall to show.");
						}
					}
					else if (split[1].equalsIgnoreCase("hide") && plugin.getEcon().isOp(user.getName())) {
						if (plugin.getWall() != null) {
							if (!plugin.getWall().isHidden()) {
								plugin.getWall().destroy();
								user.sendMessage("Wall hidden.");
							}
							else {
								user.sendMessage("The wall is already hidden.");
							}
						}
						else {
							user.sendMessage("There is no wall to hide.");
						}
					}
					else if (split[1].equalsIgnoreCase("mine")) {
						
					}
					else if (split[1].equalsIgnoreCase("add")) {
						if (plugin.getWall() == null) {
							user.sendMessage("There must be a wall created first.");
						}
						else if (split.length >= 3) {
							/*
							 * Adds a new entry to the wall
							 */

							String entry = "";
							
							for (int i = 2; i < split.length; i++) {
								entry += split[i] + " ";
							}
							
							System.out.println("Entry is: " + entry.trim());
							
							if (plugin.getWall().addNew(user, entry.trim())) {
								user.sendMessage("Your wall post has been added.");
							}
							else {
								user.sendMessage("There was not enough space on the wall. Please try again later.");
							}
						}
						else {
							// HELP
						}
						event.setCancelled(true);
					}
					else if (split[1].equalsIgnoreCase("remove")) {
						
					}
				}
				else {
					user.sendMessage("TheWall useage is /thewall [add,remove] [item name] [amount]");
				}
				
				event.setCancelled(true);
			}
		}
	}
}
