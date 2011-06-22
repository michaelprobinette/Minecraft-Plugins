/**
 * 
 */
package com.Vandolis.CodeRedLite;

import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * @author Vandolis
 */
public class EconPlayerListener extends PlayerListener {
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedEcon
	 */
	public EconPlayerListener(CodeRedLite codeRedEcon) {
		plugin = codeRedEcon;
	}
	
	public void onPlayerJoin(PlayerJoinEvent event) {
		plugin.loadPlayer(event.getPlayer());
	}
	
	public void onPlayerQuit(PlayerQuitEvent event) {
		plugin.unloadPlayer(event.getPlayer());
	}
	
	public void onPlayerKick(PlayerKickEvent event) {
		plugin.unloadPlayer(event.getPlayer());
	}
}
