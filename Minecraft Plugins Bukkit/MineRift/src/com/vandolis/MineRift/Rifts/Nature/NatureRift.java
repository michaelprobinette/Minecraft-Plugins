/**
 *
 */
package com.vandolis.MineRift.Rifts.Nature;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Creature;
import org.bukkit.entity.CreatureType;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;

import com.vandolis.MineRift.Base.Rift;

/**
 * @author Vandolis
 */
public class NatureRift extends Rift
{
  private final int          PRIMARY_BLOCK               = 2;
  private final CreatureType PRIMARY_CREATURE            = CreatureType.SPIDER;
  private final double       PRIMARY_CREATURE_RATE       = 0.90;
  private final CreatureType SECONDARY_CREATURE          = CreatureType.CREEPER;
  private final double       SECONDARY_CREATURE_MOD_RATE = 0.05;
  private final int          MOBS_PER_WAVE               = (2 * getRadius()) / 3;
  private final double       FLOWER_RATE                 = 0.20;
  private final double       ROSE_RATE                   = 0.15;
  private final double       LONG_GRASS_RATE             = 0.80;
  private final double       TREE_BORDER_RATE            = 0.80;
  
  /**
   * @param world
   * @param center
   * @param radius
   */
  public NatureRift(World world, Location center, int radius)
  {
    super(world, center, radius);
    
    setOpeningSequence(new NatureOpening(this, true));
    setClosingSequence(new NatureClosing(this, true));
  }
  
  /**
   * @return the fLOWER_RATE
   */
  public double getFLOWER_RATE()
  {
    return FLOWER_RATE;
  }
  
  /**
   * @return the rOSE_RATE
   */
  public double getROSE_RATE()
  {
    return ROSE_RATE;
  }
  
  /**
   * @return the lONG_GRASS_RATE
   */
  public double getLONG_GRASS_RATE()
  {
    return LONG_GRASS_RATE;
  }
  
  /* (non-Javadoc)
   * @see com.vandolis.MineRift.Base.Rift#spawnWave()
   */
  @Override
  public void spawnWave()
  {
    // Get the active players
    activePlayers = getPlayers();
    
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
        if (Math.random() <= SECONDARY_CREATURE_MOD_RATE)
        {
          ((Creeper) activeMobs.get(i)).setPowered(true);
        }
        
        if (!activePlayers.isEmpty())
        {
          ((Creature) activeMobs.get(i)).setTarget(activePlayers.get(0));
        }
      }
    }
  }
  
  /**
   * @return the tREE_BORDER_RATE
   */
  public double getTREE_BORDER_RATE()
  {
    return TREE_BORDER_RATE;
  }
  
  /**
   * @return the pRIMARY_BLOCK
   */
  public int getPRIMARY_BLOCK()
  {
    return PRIMARY_BLOCK;
  }
}
