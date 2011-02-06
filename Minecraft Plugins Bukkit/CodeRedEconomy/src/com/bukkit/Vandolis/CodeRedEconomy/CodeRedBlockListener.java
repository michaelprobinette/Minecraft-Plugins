/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

package com.bukkit.Vandolis.CodeRedEconomy;

import org.bukkit.block.BlockDamageLevel;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockEvent;
import org.bukkit.event.block.BlockListener;

import com.bukkit.Vandolis.CodeRedEconomy.Database.Items;
import com.bukkit.Vandolis.CodeRedEconomy.Database.Players;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.DataManager;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Money;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.ShopItem;

/**
 * Block Listener class for any {@link BlockEvent}
 * 
 * @author Vandolis
 */
public class CodeRedBlockListener extends BlockListener {
	private final CodeRedEconomy	plugin;
	
	/**
	 * @param codeRedEconomy
	 */
	public CodeRedBlockListener(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		Player player = event.getPlayer();
		
		/*
		 * Check to see if the block is broken
		 */
		if (event.getDamageLevel().equals(BlockDamageLevel.BROKEN)) {
			/*
			 * Broken block, check to see if there is a value attached to it.
			 * If there is go ahead and pay the user
			 */

			if (EconomyProperties.isUseSQL()) {
				int playerID = Players.getID(player);
				
				Players p = null;
				
				if (playerID == 0) {
					p = new Players(player.getName());
				}
				else {
					p = new Players(playerID);
				}
				
				Items item = new Items(Items.getID(event.getBlock().getTypeId(), event.getBlock().getData()));
				
				p.setBalance(p.getBalance() + item.getBreakValue());
			}
			else {
				ShopItem broken = DataManager.getItem(event.getBlock().getTypeId());
				
				if (broken != null) {
					Money breakValue = broken.getBreakValue();
					
					if (breakValue.getAmount() != 0) {
						DataManager.getUser(player).getMoney().addAmount(breakValue.getAmount());
					}
				}
			}
		}
	}
}
