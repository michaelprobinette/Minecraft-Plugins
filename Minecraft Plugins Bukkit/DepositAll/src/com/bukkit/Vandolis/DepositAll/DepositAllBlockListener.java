/**
 * 
 */
package com.bukkit.Vandolis.DepositAll;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.block.BlockListener;
import org.bukkit.inventory.ItemStack;

import com.bukkit.Vandolis.DepositAll.DepositAll.Action;

/**
 * @author Vandolis
 */
public class DepositAllBlockListener extends BlockListener {
	private DepositAll	plugin	= null;
	
	public DepositAllBlockListener(DepositAll instance) {
		plugin = instance;
		
	}
	
	/**
	 * @param trans
	 * @return number of items the player can hold
	 */
	private static int getAvailableSlotCount(ItemStack[] inventory, ItemStack item) {
		int available = 0; // The amount the player can hold
		
		for (ItemStack iter : inventory) {
			if ((iter.getType() == item.getType()) && ((iter.getDurability() == item.getDurability()) || (item.getDurability() == 0))) {
				// Same item type check how much more the stack can hold
				available += (64 - iter.getAmount());
			}
			else if (iter.getType().equals(Material.AIR)) {
				// Empty slot
				available += 64;
			}
		}
		
		return available;
	}
	
	public void onBlockDamage(BlockDamageEvent event) {
		Block block = event.getBlock();
		
		if (block.getType() == Material.CHEST) {
			Player player = event.getPlayer();
			boolean found = false;
			
			for (Player iter : plugin.getPlayers().keySet()) {
				if (iter.getName().equalsIgnoreCase(player.getName())) {
					found = true;
				}
			}
			
			if (found) {
				boolean canUse = false;
				
				if (plugin.isUseLWC()) {
					//					plugin.getLwc().canAccessChest(player, plugin.getLwc().getp)
					canUse = plugin.getLwc().getLWC().canAccessChest(player, block.getX(), block.getY(), block.getZ());
				}
				else {
					canUse = true;
				}
				
				if (canUse) {
					ItemStack[] toPutInto = null;
					ItemStack[] toTakeFrom = null;
					Chest chest = ((Chest) block.getState());
					Action action = plugin.getPlayers().get(player);
					
					switch (action) {
						case WITHDRAW:
							toPutInto = player.getInventory().getContents();
							toTakeFrom = chest.getInventory().getContents();
							player.sendMessage("All possible items withdrawn.");
							plugin.getPlayers().remove(player);
							break;
						case DEPOSIT:
							toPutInto = chest.getInventory().getContents();
							toTakeFrom = player.getInventory().getContents();
							player.sendMessage("All possible items deposited.");
							plugin.getPlayers().remove(player);
							break;
					}
					
					for (ItemStack iter : toTakeFrom) {
						if (iter != null) {
							if (iter.getType() != Material.AIR) {
								int available = getAvailableSlotCount(toPutInto, iter);
								
								if (available > 0) {
									switch (action) {
										case WITHDRAW:
											if (available >= iter.getAmount()) {
												chest.getInventory().remove(iter);
												player.getInventory().addItem(iter);
											}
											else {
												chest.getInventory().remove(new ItemStack(iter.getTypeId(), available));
												player.getInventory().addItem(new ItemStack(iter.getTypeId(), available));
											}
											break;
										case DEPOSIT:
											if (available >= iter.getAmount()) {
												player.getInventory().remove(iter);
												chest.getInventory().addItem(iter);
											}
											else {
												player.getInventory().remove(new ItemStack(iter.getTypeId(), available));
												chest.getInventory().addItem(new ItemStack(iter.getTypeId(), available));
											}
											break;
									}
									
								}
							}
						}
					}
				}
				else {
					player.sendMessage("Chest is locked.");
					plugin.getPlayers().remove(player);
				}
			}
		}
	}
}
