/**
 *
 */
package com.vandolis.zonegravity;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.util.config.Configuration;

/**
 * @author Vandolis
 */
public class YMLDatabase
{
  private ZoneGravity  plugin;
  private File         file;
  private final String fileName = "zones.yml";
  
  public YMLDatabase(ZoneGravity instance)
  {
    plugin = instance;
    
    file = new File(plugin.getDataFolder().getAbsoluteFile() + File.separator + fileName);
    
    file.mkdirs();
  }
  
  private void makeTemp()
  {
    try
    {
      File temp = new File(plugin.getDataFolder().getAbsolutePath() + File.separator + "zones.tmp");
      BufferedReader read = new BufferedReader(new FileReader(file));
      BufferedWriter write = new BufferedWriter(new FileWriter(temp));
      
      while (read.ready())
      {
        write.write(read.readLine());
      }
      
      read.close();
      write.close();
    }
    catch (IOException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }
  
  /**
   *
   **/
  public boolean saveZones(ArrayList<Zone> zones)
  {
    // Write old file to temp
    makeTemp();
    
    Configuration config = new Configuration(file);
    config.load();
    
    for (Zone zone : zones)
    {
      // Save loc 1
      config.setProperty(zone.getName() + ".location1.X", zone.getLoc1().getX());
      config.setProperty(zone.getName() + ".location1.Y", zone.getLoc1().getY());
      config.setProperty(zone.getName() + ".location1.Z", zone.getLoc1().getZ());
      
      // Save loc 2
      config.setProperty(zone.getName() + ".location2.X", zone.getLoc2().getX());
      config.setProperty(zone.getName() + ".location2.Y", zone.getLoc2().getY());
      config.setProperty(zone.getName() + ".location2.Z", zone.getLoc2().getZ());
      
      // Save world
      config.setProperty(zone.getName() + ".World", zone.getLoc1().getWorld().getName());
      
      // Save gravity
      config.setProperty(zone.getName() + ".Gravity", zone.getGravity());
    }
    
    return config.save();
  }
  
  /**
   *
   **/
  public ArrayList<Zone> getSavedZones()
  {
    ArrayList<Zone> zones = new ArrayList<Zone>();
    Configuration config = new Configuration(file);
    config.load();
    
    for (String key : config.getKeys())
    {
      plugin.getLog().info("Loading zone: " + key);
      
      zones.add(new Zone(new Location(plugin.getServer().getWorld(config.getString(key + ".World")),
        config.getDouble(key + ".location1.X", 0.0), config.getDouble(key + ".location1.Y", 0.0),
        config.getDouble(key + ".location1.Z", 0.0)),
        new Location(plugin.getServer().getWorld(config.getString(key + ".World")),
          config.getDouble(key + ".location2.X", 0.0), config.getDouble(key + ".location2.Y", 0.0),
          config.getDouble(key + ".location2.Z", 0.0)), config.getDouble(key + ".Gravity", 1.0), key));
    }
    
    return zones;
  }
}
