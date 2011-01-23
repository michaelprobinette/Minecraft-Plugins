/**
 * 
 */
package com.Vandolis.TheWall;

import org.bukkit.Location;

/**
 * @author Vandolis
 */
public class WallLocation extends Location {
	private final int	wallX;
	private final int	wallY;
	private final int	wallZ;
	private final int	wallBlockX;
	private final int	wallBlockY;
	private final int	wallBlockZ;
	
	private SignPost	sign;
	
	public WallLocation(SignPost sign, Location loc, int x, int y, int z) {
		super(loc.getWorld(), loc.getX(), loc.getY(), loc.getZ());
		this.sign = sign;
		wallX = x;
		wallY = y;
		wallZ = z;
		wallBlockX = loc.getBlockX() + x;
		wallBlockY = loc.getBlockY() + y;
		wallBlockZ = loc.getBlockZ() + 1 + z;
	}
	
	/**
	 * @return the sign
	 */
	protected SignPost getSign() {
		return sign;
	}
	
	/**
	 * @param sign
	 *            the sign to set
	 */
	protected void setSign(SignPost sign) {
		this.sign = sign;
	}
	
	/**
	 * @return the wallX
	 */
	protected int getWallX() {
		return wallX;
	}
	
	/**
	 * @return the wallY
	 */
	protected int getWallY() {
		return wallY;
	}
	
	/**
	 * @return the wallZ
	 */
	protected int getWallZ() {
		return wallZ;
	}
	
	/**
	 * @return the wallBlockX
	 */
	protected int getWallBlockX() {
		return wallBlockX;
	}
	
	/**
	 * @return the wallBlockY
	 */
	protected int getWallBlockY() {
		return wallBlockY;
	}
	
	/**
	 * @return the wallBlockZ
	 */
	protected int getWallBlockZ() {
		return wallBlockZ;
	}
	
	@Override
	public String toString() {
		return "x: " + wallBlockX + " z: " + wallBlockZ + " y: " + wallBlockY;
	}
}
