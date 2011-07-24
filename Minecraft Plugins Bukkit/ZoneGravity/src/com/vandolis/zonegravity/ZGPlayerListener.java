/**
 *
 */
package com.vandolis.zonegravity;

import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * @author Vandolis
 */
public class ZGPlayerListener extends PlayerListener
{
  private final ZoneGravity plugin;
  
  /**
   * @param zoneGravity
   */
  public ZGPlayerListener(ZoneGravity zoneGravity)
  {
    plugin = zoneGravity;
  }
  
  public void onPlayerMove(PlayerMoveEvent event)
  {
    Zone zone = plugin.getZone(event.getPlayer());
    
    event.getPlayer().sendMessage(
      "X: " + event.getPlayer().getVelocity().getX() + " Y: " + event.getPlayer().getVelocity().getY() + " Z: "
        + event.getPlayer().getVelocity().getZ());
    
    if (zone != null)
    {
      //      event.getPlayer().sendMessage("You are in zone: " + zone.getName());
    }
  }
}
