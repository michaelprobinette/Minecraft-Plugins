/**
 *
 */
package com.vandolis.MineRift.Rifts.Fire;

import org.bukkit.Material;

import com.vandolis.MineRift.MineRift;
import com.vandolis.MineRift.Base.OpeningSequence;
import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class FireOpening extends OpeningSequence
{
  
  /**
   * @param rift
   * @param fast
   */
  public FireOpening(Rift rift, boolean fast)
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
          getRift().getWorld().getBlockAt(x, y - i, z).setTypeId(((FireRift) getRift()).getPRIMARY_BLOCK());
        }
        
        if (Math.random() <= ((FireRift) getRift()).getFIRE_RATE())
        {
          getRift().getWorld().getBlockAt(x, y + 1, z).setTypeId(((FireRift) getRift()).getFIRE());
        }
        
        if (r == getRift().getRadius())
        {
          int count = (int) Math.round(Math.random() * (MineRift.getHeightMax() - y));
          
          for (int i = 1; i < count; i++)
          {
            getRift().getWorld().getBlockAt(x, y + i, z).setTypeId(((FireRift) getRift()).getPRIMARY_BLOCK());
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
