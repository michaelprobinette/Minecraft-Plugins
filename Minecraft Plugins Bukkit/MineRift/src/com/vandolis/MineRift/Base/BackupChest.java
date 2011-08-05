/**
 *
 */
package com.vandolis.MineRift.Base;

import org.bukkit.block.Chest;
import org.bukkit.inventory.ItemStack;

/**
 * @author Vandolis
 */
public class BackupChest
{
  private Chest       chest;
  private ItemStack[] items;
  
  public BackupChest(Chest chest)
  {
    this.chest = chest;
    items = new ItemStack[chest.getInventory().getContents().length];
    
    for (int i = 0; i < chest.getInventory().getContents().length; i++)
    {
      if (chest.getInventory().getContents()[i] == null)
      {
        continue;
      }
      
      ItemStack item =
        new ItemStack(chest.getInventory().getContents()[i].getType(),
          chest.getInventory().getContents()[i].getAmount(), chest.getInventory().getContents()[i].getDurability());
      
      items[i] = item;
    }
  }
  
  /**
   * @return the items
   */
  public ItemStack[] getItems()
  {
    return items;
  }
  
  /**
   * @return the chest
   */
  public Chest getChest()
  {
    return chest;
  }
  
}
