/**
 *
 */
package com.vandolis.MineRift.Base;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.Dispenser;
import org.bukkit.block.Furnace;
import org.bukkit.block.Sign;

import com.vandolis.MineRift.MineRift;

/**
 * @author Vandolis
 */
public class BackupChunk
{
  private final int              CHUNK_SIZE = 16;
  
  private BlockState[][][]       blocks;
  private ArrayList<BackupChest> chests     = new ArrayList<BackupChest>();
  private ArrayList<Sign>        signs      = new ArrayList<Sign>();
  private ArrayList<Furnace>     furnaces   = new ArrayList<Furnace>();
  private ArrayList<Dispenser>   dispensers = new ArrayList<Dispenser>();
  private Chunk                  chunk;
  
  public BackupChunk(Chunk chunk)
  {
    this.chunk = chunk;
    
    backup();
  }
  
  /**
   * @param chunk
   */
  private void backup()
  {
    Block block;
    blocks = new BlockState[CHUNK_SIZE][MineRift.getHeightMax() - MineRift.getMinY()][CHUNK_SIZE];
    
    for (int y = 0; y < (MineRift.getHeightMax() - MineRift.getMinY()); y++)
    {
      for (int x = 0; x < CHUNK_SIZE; x++)
      {
        for (int z = 0; z < CHUNK_SIZE; z++)
        {
          block = chunk.getBlock(x, y + MineRift.getMinY(), z);
          blocks[x][y][z] = block.getState();
          
          if (block.getState() instanceof Chest)
          {
            chests.add(new BackupChest(((Chest) block.getState())));
          }
          //          else if (block.getState() instanceof Sign)
          //          {
          //            signs.add(((Sign) block.getState()));
          //          }
          //          else if (block.getState() instanceof Furnace)
          //          {
          //            furnaces.add(((Furnace) block.getState()));
          //          }
          //          else if (block.getState() instanceof Dispenser)
          //          {
          //            dispensers.add(((Dispenser) block.getState()));
          //          }
        }
      }
    }
  }
  
  public void restore()
  {
    BlockState state;
    Block block;
    
    for (int y = 0; y < (MineRift.getHeightMax() - MineRift.getMinY()); y++)
    {
      for (int x = 0; x < CHUNK_SIZE; x++)
      {
        for (int z = 0; z < CHUNK_SIZE; z++)
        {
          state = blocks[x][y][z];
          block = chunk.getBlock(x, y + MineRift.getMinY(), z);
          
          block.setType(state.getType());
          block.setData(state.getData().getData());
          
          if (state instanceof Chest)
          {
            ((Chest) block.getState()).getInventory().setContents(chests.get(0).getItems());
            chests.remove(0);
          }
        }
      }
    }
    
    //    for (Chest iter : chests)
    //    {
    //      for (ItemStack item : iter.getInventory().getContents())
    //      {
    //        System.out.println("Item: " + item);
    //      }
    //
    //      ((Chest) chunk.getWorld().getBlockAt(iter.getX(), iter.getY(), iter.getZ()).getState()).getInventory()
    //        .setContents(iter.getInventory().getContents());
    //
    //      System.out.println("Restored a chest");
    //    }
    //
    //    for (Sign iter : signs)
    //    {
    //      Sign sign = ((Sign) chunk.getWorld().getBlockAt(iter.getX(), iter.getY(), iter.getZ()).getState());
    //      String line;
    //
    //      for (int i = 0; i < iter.getLines().length; i++)
    //      {
    //        line = iter.getLine(i);
    //        sign.setLine(i, line);
    //      }
    //    }
    //
    //    for (Furnace iter : furnaces)
    //    {
    //      Furnace furnace = ((Furnace) chunk.getWorld().getBlockAt(iter.getX(), iter.getY(), iter.getZ()).getState());
    //
    //      furnace.getInventory().setContents(iter.getInventory().getContents());
    //      furnace.setBurnTime(iter.getBurnTime());
    //      furnace.setCookTime(iter.getCookTime());
    //    }
    //
    //    for (Dispenser iter : dispensers)
    //    {
    //      ((Dispenser) chunk.getWorld().getBlockAt(iter.getX(), iter.getY(), iter.getZ()).getState()).getInventory()
    //        .setContents(iter.getInventory().getContents());
    //    }
  }
  
  /**
   * @return the chunk
   */
  public Chunk getChunk()
  {
    return chunk;
  }
}
