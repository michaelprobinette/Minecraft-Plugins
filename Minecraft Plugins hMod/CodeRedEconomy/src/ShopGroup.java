/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

public class ShopGroup {
	private String	groupName		= "";
	private int[]	allowedBlocks	= new int[1];
	
	public ShopGroup(String group) {
		this.groupName = group;
	}
	
	public ShopGroup(String group, int[] allowed) {
		this.groupName = group;
		allowedBlocks = allowed;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public int[] getAllowed() {
		return allowedBlocks;
	}
}
