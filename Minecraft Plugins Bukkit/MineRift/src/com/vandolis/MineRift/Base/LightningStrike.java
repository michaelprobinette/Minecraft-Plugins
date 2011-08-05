/**
 *
 */
package com.vandolis.MineRift.Base;

import org.bukkit.Location;

/**
 * @author Vandolis
 */
public class LightningStrike implements Runnable
{
  private final Location center;
  
  /**
   * @param center
   */
  public LightningStrike(Location center)
  {
    this.center = center;
  }
  
  /* (non-Javadoc)
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    System.out.println("Lightning strike!");
    
    center.getWorld().strikeLightningEffect(center);
  }
  
}
