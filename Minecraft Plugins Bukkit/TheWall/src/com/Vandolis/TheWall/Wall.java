/**
 * 
 */
package com.Vandolis.TheWall;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import com.bukkit.Vandolis.CodeRedEconomy.DataManager;
import com.bukkit.Vandolis.CodeRedEconomy.Shop;
import com.bukkit.Vandolis.CodeRedEconomy.Transaction;
import com.bukkit.Vandolis.CodeRedEconomy.User;

/**
 * @author Vandolis
 */
public class Wall extends Shop {
	private final TheWall			plugin;
	private final int				sizeX			= 10;
	private final int				sizeY			= 5;
	private final int				sizeZ			= 1;
	private Location				loc				= null;
	private ArrayList<WallLocation>	wallLocations	= new ArrayList<WallLocation>();
	private boolean					hidden			= true;
	
	/**
	 * @param instance
	 * @param loadData
	 */
	public Wall(TheWall instance, ArrayList<String> loadData) {
		/*
		 * Shop with the name The Wall, finite items, infinite money, can't restock
		 */
		super("The Wall", false, DataManager.getInfValue(), false);
		plugin = instance;
		load(loadData);
	}
	
	/**
	 * @param instance
	 * @param location
	 */
	public Wall(TheWall instance, Location location) {
		/*
		 * Shop with the name The Wall, finite items, infinite money, can't restock
		 */
		super("The Wall", false, DataManager.getInfValue(), false);
		plugin = instance;
		loc = location;
	}
	
	/**
	 * @param loadData
	 */
	private void load(ArrayList<String> loadData) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return
	 */
	private WallLocation getFirstEmptyLocation() {
		for (WallLocation iter : wallLocations) {
			if (iter.getSign() == null) {
				return iter;
			}
		}
		
		return null;
	}
	
	/**
	 * @param sign
	 *            to remove
	 */
	private void remove(SignPost sign) {
		for (WallLocation iter : wallLocations) {
			if (iter.getSign() != null) {
				if (iter.getSign().equals(sign)) {
					iter.setSign(null);
					
					refreshWall();
					
					break;
				}
			}
		}
	}
	
	/**
	 * Creates the wall on the map
	 */
	public void create() {
		hidden = false;
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				for (int y = 0; y < sizeY; y++) {
					
					loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z).setType(Material.BRICK);
					
					wallLocations.add(new WallLocation(null, loc, x, y, z));
				}
			}
		}
	}
	
	/**
	 * Removes the wall from the map
	 */
	public void destroy() {
		hidden = true;
		for (int x = 0; x < sizeX; x++) {
			for (int z = 0; z < sizeZ; z++) {
				for (int y = 0; y < sizeY; y++) {
					loc.getWorld().getBlockAt(loc.getBlockX() + x, loc.getBlockY() + y, loc.getBlockZ() + z).setType(Material.AIR);
				}
			}
		}
	}
	
	/**
	 * @param block
	 * @return
	 */
	public boolean isSign(Block block) {
		return (getSign(block) != null);
	}
	
	/**
	 * @param block
	 */
	public void process(User user, Block block) {
		System.out.println("Processing a sign.");
		
		SignPost sign = getSign(block);
		User seller = DataManager.getUser(sign.getOwner());
		
		Transaction.process(new Transaction(seller, user, sign.getStack()));
		
		remove(sign);
	}
	
	/**
	 * @param block
	 * @return
	 */
	private SignPost getSign(Block block) {
		Location temp = block.getLocation();
		for (WallLocation iter : wallLocations) {
			if (temp.getBlockX() == iter.getWallBlockX()) {
				if (temp.getBlockY() == iter.getWallBlockY()) {
					if (temp.getBlockZ() == iter.getWallBlockZ()) {
						return iter.getSign();
					}
				}
			}
		}
		return null;
	}
	
	/**
	 * @param user
	 * @param trim
	 * @return
	 */
	public boolean addNew(User user, String trim) {
		System.out.println("Adding a new sign, getting first empty.");
		WallLocation wallLoc = getFirstEmptyLocation();
		
		if (wallLoc != null) {
			System.out.println("Found the first empty at: " + wallLoc);
			
			wallLoc.setSign(new SignPost(user, trim));
			
			refreshWall();
			
			return true;
		}
		
		return false;
	}
	
	/**
	 * Refreshes the signs on the wall
	 */
	private void refreshWall() {
		if (!hidden) {
			System.out.println("Refreshing The Wall");
			
			for (WallLocation iter : wallLocations) {
				/*
				 * Set each block to air, then turn them back into signs.
				 */
				loc.getWorld().getBlockAt(iter.getWallBlockX(), iter.getWallBlockY(), iter.getWallBlockZ()).setType(Material.AIR);
				
				if (iter.getSign() != null) {
					System.out.println("Sign found at : " + iter + ", refreshing.");
					loc.getWorld().getBlockAt(iter.getWallBlockX(), iter.getWallBlockY(), iter.getWallBlockZ()).setType(Material.WALL_SIGN);
					try {
						Sign si =
								(Sign) loc.getWorld().getBlockAt(iter.getWallBlockX(), iter.getWallBlockY(), iter.getWallBlockZ())
										.getState();
						si.setLine(0, "§1" + iter.getSign().getStack().getName());
						si.setLine(1, "Amount: " + iter.getSign().getStack().getAmountAvail());
						si.setLine(2, "Price:");
						si.setLine(3, "" + iter.getSign().getStack().getTotalBuyPrice());
					}
					catch (Exception e) {
						System.out.println("There was a sign destroyed at: " + iter);
						iter.setSign(null);
					}
				}
			}
		}
	}
	
	/**
	 * @return the hidden
	 */
	protected boolean isHidden() {
		return hidden;
	}
	
	/**
	 * @param hidden
	 *            the hidden to set
	 */
	protected void setHidden(boolean hidden) {
		this.hidden = hidden;
	}
}
