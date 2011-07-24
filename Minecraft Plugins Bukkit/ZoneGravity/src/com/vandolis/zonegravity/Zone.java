/**
 *
 */
package com.vandolis.zonegravity;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

/**
 * @author Vandolis
 */
public class Zone implements Runnable
{
  private ArrayList<Player> players = new ArrayList<Player>();
  private double            gravity = 1;
  private Location          loc1;
  private Location          loc2;
  private String            name;
  private int               xBig, xSmall, zBig, zSmall, taskID;
  
  public Zone()
  {
    gravity = 1;
    loc1 = new Location(null, 0, 0, 0);
    loc2 = new Location(null, 0, 0, 0);
    name = "";
  }
  
  public Zone(Location location1, Location location2, double gravity, String name)
  {
    loc1 = location1;
    loc2 = location2;
    this.gravity = gravity;
    this.name = name;
    
    findCoords();
  }
  
  /**
   * Finds the big X,Z and small X,Z positions and stores them in the object
   */
  private void findCoords()
  {
    if (loc1.getX() > loc2.getX())
    {
      xBig = (int) loc1.getX();
      xSmall = (int) loc2.getX();
    }
    else
    {
      xBig = (int) loc2.getX();
      xSmall = (int) loc1.getX();
    }
    
    if (loc1.getZ() > loc2.getZ())
    {
      zBig = (int) loc1.getZ();
      zSmall = (int) loc2.getZ();
    }
    else
    {
      zBig = (int) loc2.getZ();
      zSmall = (int) loc1.getZ();
    }
  }
  
  /**
   * @param player
   * @return True if the given location is inside the zone
   */
  public boolean inZone(Location loc)
  {
    return ((loc.getX() <= xBig) && (loc.getX() >= xSmall) && (loc.getZ() <= zBig) && (loc.getZ() >= zSmall));
  }
  
  /**
   * @return the gravity
   */
  public double getGravity()
  {
    return gravity;
  }
  
  /**
   * @param gravity
   *          the gravity to set
   */
  public void setGravity(double gravity)
  {
    this.gravity = gravity;
  }
  
  /**
   * @return the loc1
   */
  public Location getLoc1()
  {
    return loc1;
  }
  
  /**
   * @param loc1
   *          the loc1 to set
   */
  public void setLoc1(Location loc1)
  {
    this.loc1 = loc1;
  }
  
  /**
   * @return the loc2
   */
  public Location getLoc2()
  {
    return loc2;
  }
  
  /**
   * @param loc2
   *          the loc2 to set
   */
  public void setLoc2(Location loc2)
  {
    this.loc2 = loc2;
  }
  
  /**
   * @return the name
   */
  public String getName()
  {
    return name;
  }
  
  /**
   * @return the players
   */
  public ArrayList<Player> getPlayers()
  {
    return players;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see java.lang.Runnable#run()
   */
  @Override
  public void run()
  {
    System.out.println("Running");
    
    for (Player iter : players)
    {
      //      iter.setVelocity(new Vector(iter.getVelocity().getX(), ZoneGravity.ASSUMED_GRAV + getGravity()
      //        * ZoneGravity.ASSUMED_GRAV, iter.getVelocity().getZ()));
      //      iter.setVelocity(iter.getVelocity().add(
      //              new Vector(0, ZoneGravity.ASSUMED_GRAV - getGravity() * ZoneGravity.ASSUMED_GRAV, 0)));
      if (iter.getWorld().getBlockAt(iter.getLocation().subtract(0, 1, 0)).getType() == Material.AIR)
      {
        iter
          .setVelocity(iter.getVelocity().add(
            new Vector(ZoneGravity.BUMP, ZoneGravity.ASSUMED_GRAV - getGravity() * ZoneGravity.ASSUMED_GRAV,
              ZoneGravity.BUMP)));
      }
    }
  }
  
  /**
   * @param name
   *          the name to set
   */
  public void setName(String name)
  {
    this.name = name;
  }
  
  /**
   * @param taskID
   */
  public void setTaskID(int taskID)
  {
    this.taskID = taskID;
  }
  
  /**
   * @return the taskID
   */
  public int getTaskID()
  {
    return taskID;
  }
}
