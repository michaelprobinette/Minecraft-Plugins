/**
 *
 */
package com.vandolis.MineRift.Rifts.Water;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class WaterRift extends Rift
{
  private final Material     PRIMARY_BLOCK    = Material.ICE;
  private final Material     SPECIAL_1        = Material.SNOW;
  private final double       SPECIAL_1_RATE   = 0.30;
  private final CreatureType PRIMARY_CREATURE = CreatureType.SQUID;
  private final int          MOBS_PER_WAVE    = (2 * getRadius()) / 3;
  
  /**
   * @param world
   * @param center
   * @param radius
   */
  public WaterRift(World world, Location center, int radius)
  {
    super(world, center, radius);
    
    setOpeningSequence(new WaterOpening(this, true));
    setClosingSequence(new WaterClosing(this, true));
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.Rift#spawnWave()
   */
  @Override
  public void spawnWave()
  {
    if (!activeMobs.isEmpty())
    {
      for (LivingEntity iter : activeMobs)
      {
        iter.remove();
      }
    }
    
    activeMobs = new ArrayList<LivingEntity>();
    
    for (int i = 0; i < MOBS_PER_WAVE; i++)
    {
      System.out.println("Adding mob #" + i + " as a " + PRIMARY_CREATURE);
      
      double modX;
      int modY;
      double modZ;
      
      modY = 2;
      modX = Math.random() * getRadius();
      modZ = Math.random() * getRadius();
      
      activeMobs.add(getWorld().spawnCreature(getCenter().add(modX, modY, modZ), PRIMARY_CREATURE));
      
      getCenter().subtract(modX, modY, modZ); // Remove the added amount
    }
  }
  
  /**
   * @return the pRIMARY_BLOCK
   */
  public Material getPRIMARY_BLOCK()
  {
    return PRIMARY_BLOCK;
  }
  
  /**
   * @return the sPECIAL_1
   */
  public Material getSPECIAL_1()
  {
    return SPECIAL_1;
  }
  
  /**
   * @return the sPECIAL_1_RATE
   */
  public double getSPECIAL_1_RATE()
  {
    return SPECIAL_1_RATE;
  }
  
}
