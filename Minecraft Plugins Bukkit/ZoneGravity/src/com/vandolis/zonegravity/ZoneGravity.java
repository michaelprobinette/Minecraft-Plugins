/**
 *
 */
package com.vandolis.zonegravity;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/**
 * @author Vandolis
 */
public class ZoneGravity extends JavaPlugin
{
  private final ZGPlayerListener playerListener  = new ZGPlayerListener(this);
  private ArrayList<Zone>        zones;
  private Logger                 log;
  static final int               MAX_SIGHT_RANGE = 500;
  private PermissionHandler      permissionHandler;
  private ZoneMaker              zoneMaker;
  private YMLDatabase            ymlDB;
  static final double            ASSUMED_GRAV    = 0.0785;
  static final double            BUMP            = Math.pow(4.9, -324);
  private static final int       TASK_FREQ       = 1;
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.plugin.Plugin#onDisable()
   */
  @Override
  public void onDisable()
  {
    ymlDB.saveZones(zones);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.plugin.Plugin#onEnable()
   */
  @Override
  public void onEnable()
  {
    zones = new ArrayList<Zone>();
    log = getServer().getLogger();
    ymlDB = new YMLDatabase(this);
    zoneMaker = new ZoneMaker(this);
    
    PluginManager pm = getServer().getPluginManager();
    pm.registerEvent(Type.PLAYER_MOVE, playerListener, Priority.Normal, this);
    
    setupPermissions();
    
    getCommand("zgmake").setExecutor(zoneMaker);
    
    zones = ymlDB.getSavedZones();
    restartTasks();
    log.info("ZoneGravity loaded " + zones.size() + " zones.");
    
    PluginDescriptionFile pdfFile = getDescription();
    log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
  }
  
  private void setupPermissions()
  {
    if (permissionHandler != null)
    {
      return;
    }
    
    Plugin permissionsPlugin = getServer().getPluginManager().getPlugin("Permissions");
    
    if (permissionsPlugin == null)
    {
      log.info("Permission system not detected, defaulting to Debugees");
      return;
    }
    
    permissionHandler = ((Permissions) permissionsPlugin).getHandler();
    log.info("Found and will use plugin " + ((Permissions) permissionsPlugin).getDescription().getFullName());
  }
  
  /**
   * @param player
   * @return The zone the player is in. Null if no zone
   */
  public Zone getZone(Player player)
  {
    for (Zone iter : zones)
    {
      if (iter.inZone(player.getLocation()))
      {
        for (Player pl : iter.getPlayers())
        {
          if (pl.equals(player))
          {
            return iter;
          }
        }
        
        iter.getPlayers().add(player);
        
        if (iter.getTaskID() == -1)
        {
          int taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, iter, 0, TASK_FREQ);
          iter.setTaskID(taskID);
        }
        
        return iter;
      }
      else
      {
        iter.getPlayers().remove(player);
      }
      
      if (iter.getPlayers().isEmpty())
      {
        if (iter.getTaskID() != -1)
        {
          getServer().getScheduler().cancelTask(iter.getTaskID());
          iter.setTaskID(-1);
          log.info("Stopped task: " + iter.getName());
        }
      }
    }
    
    return null;
  }
  
  /**
   *
   **/
  public void addZone(Zone zone)
  {
    int taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, zone, 0, TASK_FREQ);
    zone.setTaskID(taskID);
    
    zones.add(zone);
  }
  
  /**
   *
   **/
  public void removeZone(Zone zone)
  {
    getServer().getScheduler().cancelTask(zone.getTaskID());
    
    zones.remove(zone);
  }
  
  /**
   *
   **/
  private void restartTasks()
  {
    getServer().getScheduler().cancelTasks(this);
    
    for (Zone zone : zones)
    {
      if (!zone.getPlayers().isEmpty())
      {
        int taskID = getServer().getScheduler().scheduleSyncRepeatingTask(this, zone, 0, TASK_FREQ);
        zone.setTaskID(taskID);
      }
    }
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command,
   * java.lang.String,java.lang.String[])
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    Player player = (Player) sender;
    
    if (command.getName().equalsIgnoreCase("zg"))
    {
      if (permissionHandler != null)
      {
        if (!permissionHandler.has(player, "zonegravity.admin"))
        {
          //          sender.sendMessage("You do not have permission.");
          return false;
        }
      }
      else
      {
        if (!((Player) sender).isOp())
        {
          //          sender.sendMessage("You do not have permission.");
          return false;
        }
      }
      
      if (args.length >= 1)
      {
        if (args[0].equalsIgnoreCase("help"))
        {
          sender.sendMessage("ZoneGravity commands are: ");
          sender.sendMessage("  make [zone_name] (Makes a zone with the given name)");
          sender.sendMessage("  set [zone_name] [c1/c2] (sets a zones corners)");
          sender.sendMessage("  remove [zone_name] (removes a zone)");
          sender.sendMessage("  gravity [name] [0.01-1] (changes the gravity of a zone or world)");
          sender.sendMessage("  rename [zone_name] [new_name] (renames a zone)");
          
          return true;
        }
        else if (args[0].equalsIgnoreCase("make"))
        {
          if (args.length == 2)
          {
            String zoneName = args[1];
            boolean found = false;
            
            for (Zone zone : zones)
            {
              if (zone.getName().equalsIgnoreCase(zoneName))
              {
                found = true;
              }
            }
            
            if (!found)
            {
              sender.sendMessage("Ready to make a zone by the name of: " + zoneName);
              zoneMaker.makeZone(player, zoneName);
            }
            else
            {
              sender.sendMessage("There is another zone by the name of: " + zoneName);
            }
            
            return true;
          }
          else if (args.length >= 2)
          {
            sender.sendMessage("Zones names are not allowed to have spaces. Use a '_' instead!");
            return true;
          }
          else
          {
            sender.sendMessage("Correct usage is: /zg make [zone_name]");
            return true;
          }
        }
        else if (args[0].equalsIgnoreCase("rename"))
        {
          if (args.length == 3)
          {
            String oldName = args[1], newName = args[2];
            Zone zone = null;
            
            for (Zone iter : zones)
            {
              if (iter.getName().equalsIgnoreCase(oldName))
              {
                zone = iter;
                break;
              }
            }
            
            if (zone == null)
            {
              sender.sendMessage("No zone by the name of: " + oldName);
              return true;
            }
            else if (args.length >= 3)
            {
              sender.sendMessage("Zones names are not allowed to have spaces. Use a '_' instead!");
              return true;
            }
            else
            {
              sender.sendMessage("Changed zone: " + zone.getName() + " to zone: " + newName);
              zone.setName(newName);
              return true;
            }
          }
          else
          {
            sender.sendMessage("Correct usage is: /zg rename [old_name] [new_name]");
            return true;
          }
        }
        else if (args[0].equalsIgnoreCase("gravity"))
        {
          if (args.length == 3)
          {
            String zoneName = args[1];
            double gravity = 1;
            Zone zone = null;
            
            for (Zone iter : zones)
            {
              if (iter.getName().equalsIgnoreCase(zoneName))
              {
                zone = iter;
                break;
              }
            }
            
            if (zone == null)
            {
              sender.sendMessage("No zone by the name of: " + zoneName);
              return true;
            }
            
            try
            {
              gravity = Double.parseDouble(args[2]);
            }
            catch (Exception e)
            {
              sender.sendMessage("Invalid gravity.");
              return true;
            }
            
            zone.setGravity(gravity);
            
            sender.sendMessage("Gravity changed for zone: " + zone.getName());
            return true;
          }
          else if (args.length >= 3)
          {
            sender.sendMessage("Zones names are not allowed to have spaces. Use a '_' instead!");
            return true;
          }
          else
          {
            sender.sendMessage("Correct useage is: /zg gravity [zone_name] [0.01-1]");
            return true;
          }
        }
        else if (args[0].equalsIgnoreCase("remove"))
        {
          if (args.length == 2)
          {
            String zoneName = args[1];
            Zone zone = null;
            
            for (Zone iter : zones)
            {
              if (iter.getName().equalsIgnoreCase(zoneName))
              {
                zone = iter;
                break;
              }
            }
            
            if (zone == null)
            {
              sender.sendMessage("No zone by the name of: " + zoneName);
              return true;
            }
            else
            {
              removeZone(zone);
              sender.sendMessage("Removed zone: " + zoneName);
              return true;
            }
          }
          else if (args.length >= 2)
          {
            sender.sendMessage("Zones names are not allowed to have spaces. Use a '_' instead!");
            return true;
          }
          else
          {
            sender.sendMessage("Correct useage is: /zg remove [zone_name]");
            return true;
          }
        }
        else if (args[0].equalsIgnoreCase("set"))
        {
          if (args.length >= 3)
          {
            String zoneName = args[1];
            String corner = args[2];
            int point = 0;
            Zone zone = null;
            
            for (Zone iter : zones)
            {
              if (iter.getName().equalsIgnoreCase(zoneName))
              {
                zone = iter;
                break;
              }
            }
            
            if (zone == null)
            {
              sender.sendMessage("No zone by the name of: " + zoneName);
              sender.sendMessage("Use /zg make [zone_name] to add a zone with that name");
              return true;
            }
            
            for (char c : corner.toCharArray())
            {
              try
              {
                point = Integer.parseInt(String.valueOf(c));
              }
              catch (Exception e)
              {
              }
            }
            
            switch (point)
            {
              case 1:
                sender.sendMessage("Corner 1 set for zone: " + zoneName);
                zone.setLoc1(player.getTargetBlock(null, MAX_SIGHT_RANGE).getLocation());
                break;
              case 2:
                sender.sendMessage("Corner 2 set for zone: " + zoneName);
                zone.setLoc2(player.getTargetBlock(null, MAX_SIGHT_RANGE).getLocation());
                break;
              default:
                sender.sendMessage("Invalid corner. Choices are 1,2");
                break;
            }
            
            return true;
          }
          else if (args.length >= 3)
          {
            sender.sendMessage("Zones names are not allowed to have spaces. Use a '_' instead!");
            return true;
          }
          else
          {
            sender.sendMessage("Correct usage is /zg set [zone_name] [c1/c2]. Point is taken from target block.");
            return true;
          }
        }
      }
      
      return false;
    }
    
    return super.onCommand(sender, command, label, args);
  }
  
  /**
   * @return the log
   */
  public Logger getLog()
  {
    return log;
  }
}
