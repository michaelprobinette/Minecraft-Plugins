/**
 *
 */
package com.vandolis.MineRift;

import java.util.ArrayList;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.vandolis.MineRift.Base.Rift;
import com.vandolis.MineRift.Rifts.Fire.FireRift;
import com.vandolis.MineRift.Rifts.Nature.NatureRift;
import com.vandolis.MineRift.Rifts.Water.WaterRift;

/**
 * @author Vandolis
 */
public class MineRift extends JavaPlugin
{
  //private final RiftPlayerListener playerListener = new RiftPlayerListener(this);
  private Logger                log         = null;
  //  private final int                MIN_TIME         = 1;
  //  private final int                MAX_TIME         = 60;
  //  private final int                MAX_RIFTS_ACTIVE = 1;
  private final ArrayList<Rift> activeRifts = new ArrayList<Rift>();
  private static final int      MIN_Y       = 40;
  private static final int      MAX_Y       = 100;
  private static final int      HEIGHT_MAX  = 128;
  
  /* (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onDisable()
   */
  @Override
  public void onDisable()
  {
    for (Rift iter : activeRifts)
    {
      iter.getClosingSequence().closeFast();
    }
  }
  
  /* (non-Javadoc)
   * @see org.bukkit.plugin.Plugin#onEnable()
   */
  @Override
  public void onEnable()
  {
    log = getServer().getLogger();
    Rift.setPlugin(this);
    
    // Register our events
    //PluginManager pm = getServer().getPluginManager();
    
    getCommand("rift").setExecutor(this);
    getCommand("chunk").setExecutor(this);
    
    //		getServer().getScheduler().scheduleSyncRepeatingTask(this, new RiftOpener(this), MIN_TIME * 1200,
    //			(long) Math.random() * MAX_TIME + MIN_TIME);
    
    // EXAMPLE: Custom code, here we just output some info so we can check all is well
    PluginDescriptionFile pdfFile = getDescription();
    log.info(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
  }
  
  /* (non-Javadoc)
   * @see org.bukkit.plugin.java.JavaPlugin#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command,
   * java.lang.String, java.lang.String[])
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
  {
    if (command.getName().equalsIgnoreCase("chunk"))
    {
      sender.sendMessage("" + ((Player) sender).getWorld().getChunkAt(((Player) sender).getLocation()));
      
      return true;
    }
    else if (command.getName().equalsIgnoreCase("rift"))
    {
      if (args.length >= 1)
      {
        String cmd = args[0];
        
        if (cmd.equalsIgnoreCase("open"))
        {
          if (args.length >= 2)
          {
            if (args[1].equalsIgnoreCase("fire"))
            {
              if (args.length >= 3)
              {
                int radius = Integer.parseInt(args[2]);
                
                activeRifts.add(new FireRift(((Player) sender).getWorld(),
                  ((Player) sender).getTargetBlock(null, 500).getLocation(), radius));
                
                activeRifts.get(0).open();
                
                getServer().broadcastMessage("Fire Rift opened");
              }
            }
            else if (args[1].equalsIgnoreCase("nature"))
            {
              int radius = Integer.parseInt(args[2]);
              
              activeRifts.add(new NatureRift(((Player) sender).getWorld(),
                ((Player) sender).getTargetBlock(null, 500).getLocation(), radius));
              
              activeRifts.get(0).open();
              
              getServer().broadcastMessage("Nature Rift opened");
            }
            else if (args[1].equalsIgnoreCase("water"))
            {
              int radius = Integer.parseInt(args[2]);
              
              activeRifts.add(new WaterRift(((Player) sender).getWorld(),
                ((Player) sender).getTargetBlock(null, 500).getLocation(), radius));
              
              activeRifts.get(0).open();
              
              getServer().broadcastMessage("Water Rift opened");
            }
          }
        }
        else if (cmd.equalsIgnoreCase("close"))
        {
          if (activeRifts.size() >= 1)
          {
            activeRifts.get(0).close();
            activeRifts.remove(0);
            sender.sendMessage("Rift closed");
          }
          else
          {
            sender.sendMessage("No rifts to close");
          }
        }
      }
      return true;
    }
    else
    {
      return super.onCommand(sender, command, label, args);
    }
  }
  
  /**
   * @return the minY
   */
  public static int getMinY()
  {
    return MIN_Y;
  }
  
  /**
   * @return the maxY
   */
  public static int getMaxY()
  {
    return MAX_Y;
  }
  
  /**
   * @return the heightMax
   */
  public static int getHeightMax()
  {
    return HEIGHT_MAX;
  }
}
