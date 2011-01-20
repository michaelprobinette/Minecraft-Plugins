/**
 * 
 */
package bukkit.Vandolis;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockRightClickEvent;
import org.bukkit.inventory.ItemStack;

/**
 * @author Vandolis
 */
public class SignDispenserBlockListener extends BlockListener {
	private final SignDispenser			plugin;
	private static ArrayList<Player>	players	= new ArrayList<Player>();
	
	public SignDispenserBlockListener(SignDispenser instance) {
		plugin = instance;
	}
	
	public void onBlockPlace(BlockPlaceEvent event) {
		Player p = event.getPlayer();
		for (Player iter : players) {
			if (iter.equals(p)) {
				//				System.out.println("Removing " + iter.getName());
				event.setBuild(false);
				players.remove(iter);
				break;
			}
		}
	}
	
	public void onBlockRightClick(BlockRightClickEvent event) {
		Player p = event.getPlayer();
		if (event.getBlock() != null) {
			if (event.getBlock().getType().equals(Material.SIGN) || event.getBlock().getType().equals(Material.SIGN_POST)) {
				// Sign was placed, grab text
				Sign si = (Sign) event.getBlock().getState();
				String lines[] = si.getLines();
				if (lines.length >= 1) {
					/*
					 * Update the syntax to a better looking one
					 */
					if (lines[0].equalsIgnoreCase("Item ID")) {
						si.setLine(0, "Right Click:");
						int temp = 0;
						try {
							temp = Integer.valueOf(lines[1].trim());
							si.setLine(1, plugin.getName(temp));
						}
						catch (Exception e) {
							si.setLine(1, "Incorrect Id");
						}
						si.setLine(2, "Amount:");
						try {
							temp = Integer.valueOf(lines[3].trim());
						}
						catch (Exception e1) {
							si.setLine(3, "" + 1);
						}
						
						si.update();
						lines = si.getLines();
					}
					
					if (lines[0].equalsIgnoreCase("Right Click:")) {
						int itemId = -1;
						int amount = 1;
						try {
							if (!lines[1].equalsIgnoreCase("Incorrect ID")) {
								if ((itemId = plugin.getArrayIndex(SignDispenser.getItemNames(), lines[1])) != -1) {
									itemId += 256;
									if (lines[2].equalsIgnoreCase("amount:")) {
										amount = Integer.parseInt(lines[3].trim());
									}
								}
								else if ((itemId = plugin.getArrayIndex(SignDispenser.getBlocknames(), lines[1])) != -1) {
									if (lines[2].equalsIgnoreCase("amount:")) {
										amount = Integer.parseInt(lines[3].trim());
									}
								}
								else {
									itemId = Integer.valueOf(lines[1].trim());
									// TODO redo this
									//									int temp = Integer.parseInt(lines[1].trim());
									//									if (Item.isValidItem(temp)) {
									//										itemId = temp;
									//									}
								}
								
								if (itemId > 0) {
									p.getInventory().addItem(new ItemStack(itemId, amount));
									p.sendMessage("[§eSignDispenser§f] Here You go!");
									
									boolean found = false;
									
									for (Player iter : players) {
										if (iter.equals(p)) {
											found = true;
										}
									}
									if (!found) {
										//										System.out.println("Adding " + p.getName());
										players.add(p);
									}
								}
								else {
									si.setLine(1, "Incorrect Id");
									si.update();
								}
							}
						}
						catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
