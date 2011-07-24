/**
 *
 */
package com.vandolis.zonegravity;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * @author Vandolis
 */
public class ZoneMaker implements CommandExecutor
{
  private final ZoneGravity plugin;
  private Player            player;
  private String            zoneName;
  private Location          loc1, loc2;
  private double            grav;
  private Stage             stage;
  
  public enum Stage
  {
    NONE, NEW, LOC1, LOC2, GRAV, DONE
  };
  
  /**
   * @param zoneGravity
   */
  public ZoneMaker(ZoneGravity zoneGravity)
  {
    plugin = zoneGravity;
  }
  
  public void prompt()
  {
    switch (stage)
    {
      case NEW:
        player.sendMessage("Please follow the next steps carefully! (use '/zgmake cancel' to stop)");
      case LOC1:
        stage = Stage.LOC1;
        player.sendMessage("Type '/zgmake point' while aiming at your first corner.");
        break;
      case LOC2:
        player.sendMessage("Type '/zgmake point' while aiming at your second corner.");
        break;
      case GRAV:
        player.sendMessage("Type '/zgmake grav [amount]' with amount being a decimal between -1 and 1");
        break;
      case DONE:
        player.sendMessage("You done setting up your zone! Type '/zg make [zone_name]' to make another.");
        plugin.addZone(new Zone(loc1, loc2, grav, zoneName));
        stage = Stage.NONE;
        player = null;
        break;
      default:
        break;
    }
  }
  
  /**
   * @return the player
   */
  public Player getPlayer()
  {
    return player;
  }
  
  /**
   * @param player
   *          the player to set
   */
  public void makeZone(Player player, String zoneName)
  {
    this.player = player;
    this.zoneName = zoneName;
    stage = Stage.NEW;
    
    loc1 = loc2 = null;
    grav = 1;
    
    prompt();
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String,
   * java.lang.String[])
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
  {
    if (!((Player) sender).equals(player))
    {
      return false;
    }
    
    if (split.length >= 1)
    {
      if (split[0].equalsIgnoreCase("point"))
      {
        switch (stage)
        {
          case LOC1:
            loc1 = player.getTargetBlock(null, ZoneGravity.MAX_SIGHT_RANGE).getLocation();
            stage = Stage.LOC2;
            prompt();
            break;
          case LOC2:
            loc2 = player.getTargetBlock(null, ZoneGravity.MAX_SIGHT_RANGE).getLocation();
            stage = Stage.GRAV;
            prompt();
            break;
          default:
            prompt();
            break;
        }
      }
      else if (split[0].equalsIgnoreCase("grav"))
      {
        if (split.length >= 2)
        {
          switch (stage)
          {
            case GRAV:
              try
              {
                grav = Double.parseDouble(split[1]);
                
                if ((grav > 1) || (grav < -1))
                {
                  throw new Exception();
                }
                
                stage = Stage.DONE;
              }
              catch (Exception e)
              {
                sender.sendMessage("Invalid number.");
              }
              
              prompt();
              break;
            default:
              prompt();
              break;
          }
        }
        else
        {
          prompt();
        }
      }
      else if (split[0].equalsIgnoreCase("exit") || split[0].equalsIgnoreCase("cancel")
        || split[0].equalsIgnoreCase("stop"))
      {
        sender.sendMessage("Zone creation stopped");
        stage = Stage.DONE;
      }
    }
    else
    {
      prompt();
    }
    
    return true;
  }
}
