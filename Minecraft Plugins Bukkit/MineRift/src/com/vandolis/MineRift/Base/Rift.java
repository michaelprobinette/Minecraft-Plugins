/**
 *
 */
package com.vandolis.MineRift.Base;

import java.util.ArrayList;

import org.bukkit.Chunk;
import org.bukkit.ChunkSnapshot;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import com.vandolis.MineRift.MineRift;

/**
 * @author Vandolis
 */
public abstract class Rift
{
  private static MineRift           plugin;
  private final int                 CHUNK_SIZE = 16;
  
  private World                     world;
  private Location                  center;
  private int                       radius;
  private Chunk                     centerChunk;
  private Block                     centerBlock;
  private BackupChunk[][]           originalChunks;
  private ChunkSnapshot[][]         chunkSnapshots;
  private OpeningSequence           openingSequence;
  private ClosingSequence           closingSequence;
  protected ArrayList<LivingEntity> activeMobs;
  protected ArrayList<Player>       activePlayers;
  
  public Rift(World world, Location center, int radius)
  {
    this.world = world;
    this.center = center;
    this.radius = radius;
    centerChunk = world.getChunkAt(center);
    centerBlock = world.getBlockAt(center);
    
    activeMobs = new ArrayList<LivingEntity>();
    activePlayers = new ArrayList<Player>();
    
    populateChunks();
    populatePlayers();
  }
  
  //  protected void regenerateChunks()
  //  {
  //    for (int i = 0; i < originalChunks.length; i++)
  //    {
  //      for (int j = 0; j < originalChunks[i].length; j++)
  //      {
  //        world.regenerateChunk(originalChunks[i][j].getX(), originalChunks[i][j].getZ());
  //        chunkSnapshots[i][j] =
  //          world.getChunkAt(originalChunks[i][j].getX(), originalChunks[i][j].getZ()).getChunkSnapshot(true, false,
  //            false);
  //      }
  //    }
  //  }
  
  public void restoreChunks()
  {
    //    BackupChunk chunk;
    
    for (int i = 0; i < originalChunks.length; i++)
    {
      for (int j = 0; j < originalChunks[i].length; j++)
      {
        originalChunks[i][j].restore();
      }
    }
  }
  
  /**
   *
   */
  private void populateChunks()
  {
    System.out.println("Populating Chunks...");
    
    int x, z, ns, ew, posX, posZ;
    
    x = Math.abs(center.getBlockX() % CHUNK_SIZE);
    z = Math.abs(center.getBlockZ() % CHUNK_SIZE);
    
    // Find north
    ns = Math.abs(radius + x) / CHUNK_SIZE;
    if (Math.abs(radius + x) % CHUNK_SIZE != 0)
    {
      ns++; // Round up
    }
    
    if (Math.abs(radius - x) / CHUNK_SIZE >= ns)
    {
      ns = Math.abs(radius - x) / CHUNK_SIZE;
      if (Math.abs(radius - x) % CHUNK_SIZE != 0)
      {
        ns++; // Round up
      }
    }
    
    // Find east
    ew = Math.abs(radius + z) / CHUNK_SIZE;
    if (Math.abs(radius + z) % CHUNK_SIZE != 0)
    {
      ew++;
    }
    
    if (Math.abs(radius - z) / CHUNK_SIZE >= ew)
    {
      ew = Math.abs(radius - z) / CHUNK_SIZE;
      if (Math.abs(radius - z) % CHUNK_SIZE != 0)
      {
        ew++;
      }
    }
    
    System.out.println("X: " + x + " Z: " + z + " NS: " + ns + " EW: " + ew);
    
    originalChunks = new BackupChunk[2 * ns + 1][2 * ew + 1];
    chunkSnapshots = new ChunkSnapshot[2 * ns + 1][2 * ew + 1];
    
    posX = 0;
    posZ = 0;
    
    if (ns >= ew)
    {
      for (int i = -ns; i <= ns; i++)
      {
        // Reset y count
        posZ = 0;
        
        // Loop through the east to west
        for (int j = -ew; j <= ew; j++)
        {
          System.out.println("POSX: " + posX + " POSZ: " + posZ);
          
          originalChunks[posX][posZ] = new BackupChunk(
            world.getChunkAt(centerChunk.getX() + i, centerChunk.getZ() + j));
          
          System.out.println(originalChunks[posX][posZ].getChunk());
          
          posZ++;
        }
        
        posX++;
      }
    }
    else
    {
      for (int j = -ew; j <= ew; j++)
      {
        // Reset y count
        posX = 0;
        
        // Loop through the east to west
        for (int i = -ns; i <= ns; i++)
        {
          //        System.out.println("Getting chunk at X: " + centerChunk.getX() + (i * CHUNK_SIZE) + " Y: " + centerChunk.getZ()
          //          + (j * CHUNK_SIZE));
          
          System.out.println("POSX: " + posX + " POSZ: " + posZ);
          
          originalChunks[posX][posZ] = new BackupChunk(
            world.getChunkAt(centerChunk.getX() + i, centerChunk.getZ() + j));
          
          System.out.println(originalChunks[posX][posZ].getChunk());
          
          posX++;
        }
        
        posZ++;
      }
    }
    
    for (int i = 0; i < originalChunks.length; i++)
    {
      for (int j = 0; j < originalChunks[i].length; j++)
      {
        chunkSnapshots[i][j] = originalChunks[i][j].getChunk().getChunkSnapshot();
      }
    }
  }
  
  protected void populatePlayers()
  {
    activePlayers = getPlayers();
  }
  
  public ArrayList<Player> getPlayers()
  {
    ArrayList<Player> entities = new ArrayList<Player>();
    
    for (int i = 0; i < originalChunks.length; i++)
    {
      for (int j = 0; j < originalChunks[i].length; j++)
      {
        for (Entity iter : originalChunks[i][j].getChunk().getEntities())
        {
          if (iter instanceof Player)
          {
            entities.add((Player) iter);
          }
        }
      }
    }
    
    return entities;
  }
  
  public void open()
  {
    if (openingSequence != null)
    {
      Rift.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Rift.getPlugin(), getOpeningSequence());
    }
    
    spawnWave();
  }
  
  public void close()
  {
    if (!activeMobs.isEmpty())
    {
      for (LivingEntity iter : activeMobs)
      {
        // Kill it with drops
        iter.damage(30);
      }
    }
    
    activeMobs = new ArrayList<LivingEntity>();
    
    if (openingSequence != null)
    {
      Rift.getPlugin().getServer().getScheduler().scheduleSyncDelayedTask(Rift.getPlugin(), getClosingSequence());
    }
  }
  
  public World getWorld()
  {
    return world;
  }
  
  public Location getCenter()
  {
    return center;
  }
  
  /**
   * @return the centerBlock
   */
  public Block getCenterBlock()
  {
    return centerBlock;
  }
  
  public int getRadius()
  {
    return radius;
  }
  
  public int getCircleX(int r, double theta)
  {
    return (int) Math.round(getCenter().getBlockX() + r * Math.cos(theta));
  }
  
  public int getCircleZ(int r, double theta)
  {
    return (int) Math.round(getCenter().getBlockZ() + r * Math.sin(theta));
  }
  
  protected abstract void spawnWave();
  
  /**
   * @return the centerChunk
   */
  public Chunk getCenterChunk()
  {
    return centerChunk;
  }
  
  /**
   * @return the plugin
   */
  public static MineRift getPlugin()
  {
    return plugin;
  }
  
  /**
   * @param plugin
   *          the plugin to set
   */
  public static void setPlugin(MineRift plugin)
  {
    Rift.plugin = plugin;
  }
  
  /**
   * @return the openingSequence
   */
  public OpeningSequence getOpeningSequence()
  {
    return openingSequence;
  }
  
  /**
   * @param openingSequence
   *          the openingSequence to set
   */
  public void setOpeningSequence(OpeningSequence openingSequence)
  {
    this.openingSequence = openingSequence;
  }
  
  /**
   * @return the closingSequence
   */
  public ClosingSequence getClosingSequence()
  {
    return closingSequence;
  }
  
  /**
   * @param closingSequence
   *          the closingSequence to set
   */
  public void setClosingSequence(ClosingSequence closingSequence)
  {
    this.closingSequence = closingSequence;
  }
  
  public static boolean isValidLocation(Location loc, int r)
  {
    
    return true;
  }
  
  /**
   * @return the activeMobs
   */
  public ArrayList<LivingEntity> getActiveMobs()
  {
    return activeMobs;
  }
  
  /**
   * @return the activePlayers
   */
  public ArrayList<Player> getActivePlayers()
  {
    return activePlayers;
  }
  
  /**
   * @param type
   * @return
   */
  public static boolean isValidBlock(Material type)
  {
    switch (type)
    {
      case DIRT:
      case STONE:
      case SAND:
      case GRASS:
      case GRAVEL:
      case SANDSTONE:
      case WATER:
      case LAVA:
      case CLAY:
      case COBBLESTONE:
      case MOSSY_COBBLESTONE:
      case COAL_ORE:
      case IRON_BLOCK:
      case IRON_ORE:
      case GOLD_BLOCK:
      case GOLD_ORE:
      case DIAMOND_BLOCK:
      case DIAMOND_ORE:
      case DOUBLE_STEP:
      case STEP:
      case GLASS:
      case REDSTONE_ORE:
      case GLOWING_REDSTONE_ORE:
      case GLOWSTONE:
      case LAPIS_BLOCK:
      case LAPIS_ORE:
      case ICE:
      case LOG:
      case WOOD:
      case SNOW_BLOCK:
        return true;
      default:
        return false;
    }
  }
}
