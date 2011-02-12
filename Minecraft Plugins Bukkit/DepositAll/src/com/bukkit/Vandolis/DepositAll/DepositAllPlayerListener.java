/**
 * 
 */
package com.bukkit.Vandolis.DepositAll;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

import com.bukkit.Vandolis.DepositAll.DepositAll.Action;

/**
 * @author Vandolis
 */
public class DepositAllPlayerListener extends PlayerListener {
	private DepositAll	plugin	= null;
	
	public DepositAllPlayerListener(DepositAll instance) {
		plugin = instance;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/da")) {
				plugin.addPlayer(player, Action.DEPOSIT);
				
				player.sendMessage("Left click a chest to deposit all of your inventory.");
				
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/wa")) {
				plugin.addPlayer(player, Action.WITHDRAW);
				
				player.sendMessage("Left click a chest to withdraw max items.");
				
				event.setCancelled(true);
			}
		}
	}
}
