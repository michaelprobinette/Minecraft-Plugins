/**
 *
 */
package com.vandolis.MineRift.Rifts.Fire;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.LivingEntity;

import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class FireRift extends Rift
{
  private final int          PRIMARY_BLOCK         = 87;
  private final int          FIRE                  = 51;
  private final double       FIRE_RATE             = 0.13;
  private final CreatureType PRIMARY_CREATURE      = CreatureType.PIG_ZOMBIE;
  private final double       PRIMARY_CREATURE_RATE = 0.90;
  private final CreatureType SECONDARY_CREATURE    = CreatureType.GHAST;
  private final int          MOBS_PER_WAVE         = (2 * getRadius()) / 3;
  
  /**
   * @param world
   * @param center
   * @param radius
   */
  public FireRift(World world, Location center, int radius)
  {
    super(world, center, radius);
    
    setOpeningSequence(new FireOpening(this, true));
    setClosingSequence(new FireClosing(this, true));
  }
  
  /**
   * @return the pRIMARY_BLOCK
   */
  public int getPRIMARY_BLOCK()
  {
    return PRIMARY_BLOCK;
  }
  
  /**
   * @return the fIRE
   */
  public int getFIRE()
  {
    return FIRE;
  }
  
  /**
   * @return the fIRE_RATE
   */
  public double getFIRE_RATE()
  {
    return FIRE_RATE;
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Rift#spawnWave()
   */
  @Override
  public void spawnWave()
  {
    // Get the active players
    populatePlayers();
    
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
      if (Math.random() <= PRIMARY_CREATURE_RATE)
      {
        System.out.println("Adding mob #" + i + " as a " + PRIMARY_CREATURE);
        
        activeMobs.add(getWorld().spawnCreature(getCenter().add(0, 2, 0), PRIMARY_CREATURE));
        getCenter().subtract(0, 2, 0); // Remove the added amount
        if (!activePlayers.isEmpty())
        {
          ((Creature) activeMobs.get(i)).setTarget(activePlayers.get(0));
        }
      }
      else
      {
        System.out.println("Adding mob #" + i + " as a " + SECONDARY_CREATURE);
        
        activeMobs.add(getWorld().spawnCreature(getCenter().add(0, 2, 0), SECONDARY_CREATURE));
        getCenter().subtract(0, 2, 0); // Remove the added amount
        //        if (!activePlayers.isEmpty())
        //        {
        //          ((Creature) activeMobs.get(i)).setTarget(activePlayers.get(0));
        //        }
      }
    }
  }
}
