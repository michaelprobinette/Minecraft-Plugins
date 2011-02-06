/**
 * 
 */
package com.Vandolis.TheWall;

import org.bukkit.Material;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickEvent;

import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.DataManager;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.User;

/**
 * @author Vandolis
 */
public class TheWallBlockListener extends BlockListener {
	private final TheWall	plugin;
	
	/**
	 * @param theWall
	 */
	public TheWallBlockListener(TheWall theWall) {
		plugin = theWall;
	}
	
	public void onBlockRightClick(BlockRightClickEvent event) {
		if (event.getBlock().getType().equals(Material.WALL_SIGN) || event.getBlock().getType().equals(Material.SIGN_POST)) {
			User user = DataManager.getUser(event.getPlayer());
			System.out.println("Sign right clicked.");
			if (plugin.getWall().isSign(event.getBlock())) {
				plugin.getWall().process(user, event.getBlock());
			}
		}
	}
}
