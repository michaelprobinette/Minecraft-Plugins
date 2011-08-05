/**
 *
 */
package com.vandolis.MineRift.Rifts.Nature;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.TreeType;

import com.vandolis.MineRift.MineRift;
import com.vandolis.MineRift.Base.OpeningSequence;
import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class NatureOpening extends OpeningSequence
{
  
  /**
   * @param rift
   * @param fast
   */
  public NatureOpening(Rift rift, boolean fast)
  {
    super(rift, fast);
    // TODO Auto-generated constructor stub
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.OpeningSequence#openFast()
   */
  @Override
  public void openFast()
  {
    getRift().getCenterBlock().setType(Material.DIAMOND_BLOCK);
    
    // Circle on ground
    for (int r = 1; r <= getRift().getRadius(); r++)
    {
      for (double theta = 0; theta < 2 * Math.PI; theta += (Math.PI / 2) / getRift().getRadius())
      {
        int x = getRift().getCircleX(r, theta);
        int z = getRift().getCircleZ(r, theta);
        int y = 0;
        
        // Look for highest block
        for (y = MineRift.getMaxY(); y > MineRift.getMinY(); y--)
        {
          if (Rift.isValidBlock(getRift().getWorld().getBlockAt(x, y, z).getType()))
          {
            break;
          }
        }
        
        for (int i = 0; (i < getRift().getRadius()) && (y - i > MineRift.getMinY()); i++)
        {
          getRift().getWorld().getBlockAt(x, y - i, z).setTypeId(((NatureRift) getRift()).getPRIMARY_BLOCK());
        }
        
        //        if (Math.random() <= ((NatureRift) getRift()).getLONG_GRASS_RATE())
        //        {
        //          getRift().getWorld().getBlockAt(x, y + 1, z).setType(Material.LONG_GRASS);
        //        }
        //        else
        if (Math.random() <= ((NatureRift) getRift()).getFLOWER_RATE())
        {
          getRift().getWorld().getBlockAt(x, y + 1, z).setType(Material.YELLOW_FLOWER);
        }
        else if (Math.random() <= ((NatureRift) getRift()).getROSE_RATE())
        {
          getRift().getWorld().getBlockAt(x, y + 1, z).setType(Material.RED_ROSE);
        }
        
        if (r == getRift().getRadius())
        {
          if (Math.random() <= ((NatureRift) getRift()).getTREE_BORDER_RATE())
          {
            getRift().getWorld().generateTree(new Location(getRift().getWorld(), x, y + 1, z), TreeType.BIG_TREE);
          }
        }
      }
    }
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.OpeningSequence#openSlow()
   */
  @Override
  public void openSlow()
  {
    // TODO Auto-generated method stub
    
  }
  
}
