package bukkit.Vandolis;

import org.bukkit.inventory.ItemStack;

/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

public class ShopItem {
	private final int				itemID;
	private final String			itemName;
	// private final int privLevel;
	private final int				buyPrice;
	private final int				sellPrice;
	private final int				maxAvail;
	private static final String[]	blockNames		= {
			"Other", "Stone", "Grass", "Dirt", "Cobblestone", "Wooden Planks", "Sapling", "Bedrock", "Water", "Stationary Water", "Lava",
			"Stationary Lava", "Sand", "Gravel", "Gold Ore", "Iron Ore", "Coal Ore", "Wood", "Leaves", "Sponge", "Glass",
			"Lapis Lazuli Ore", "Lapis Lazuli Block", "Dispenser", "Sandstone", "Note Block", "", "", "", "", "", "", "", "", "", "Wool",
			"", "Yellow Flower", "Red Rose", "Brown Mushroom", "Red Mushroom", "Gold Block", "Iron Block", "Double Stone Slab",
			"Stone Slab", "Brick", "TNT", "Bookshelf", "Moss Stone", "Obsidian", "Torch", "Fire", "Monster Spawner", "Wodden Stairs",
			"Chest", "Redstone Wire", "Diamond Ore", "Diamond Block", "Workbench", "Crops", "Soil", "Furnace", "Burning Furnace",
			"Sign Post", "Wooden Door", "Ladder", "Minecart Tracks", "Cobblestone Stairs", "Wall Sign", "Lever", "Stone Pressure Plate",
			"Iron Door", "Wooden Pressure Plate", "Redstone Ore", "GlowingRedstone", "RedstoneTorchOf", "RedstoneTorchOn", "StoneButton",
			"Snow", "Ice", "SnowBlock", "Cactus", "Clay", "Sugar Cane", "Jukebox", "Fence", "Pumpkin", "Netherrack", "Soul Sand",
			"Glowstone", "Portal", "Jack-O-Lantern", "Cake Block"
													};
	private static final String[]	itemNames		= {
			"Iron Shovel", "Iron Pickaxe", "Iron Axe", "Flint and Steel", "Apple", "Bow", "Arrow", "Coal", "Diamond", "Iron Ingot",
			"Gold Ingot", "Iron Sword", "Wooden Sword", "Woodern Shovel", "Wooden Pickaxe", "Wooden Axe", "Stone Sword", "Stone Shovel",
			"Stone Pickaxe", "Stone Axe", "Diamond Sword", "Diamond Shovel", "Diamond Pickaxe", "Diamond Axe", "Stick", "Bowl",
			"Mushroom Soup", "Gold Sword", "Gold Shovel", "Gold Pickaxe", "Gold Axe", "String", "Feather", "Sulphur", "Wooden Hoe",
			"Stone Hoe", "Iron Hoe", "Diamond Hoe", "Gold Hoe", "Seeds", "Wheat", "Bread", "Leather Helmet", "Leather Chestpalte",
			"Leather Leggings", "Leather Boots", "Chainmail Helmet", "Chainmail Chestplate", "Chainmail Leggings", "Chainmail Boots",
			"Iron Helmet", "Iron Chestplate", "Iron Leggings", "Iron Boots", "Diamond Helmet", "Diamond Chest", "Diamond Leggings",
			"Diamond Boots", "Gold Helmet", "Gold Chestplate", "Gold Leggings", "Gold Boots", "Flint", "Raw Porkchop", "Cooked Porkchop",
			"Painting", "Golden Apple", "Sign", "Wooden Door", "Bucket", "Water Bucket", "Lava Bucket", "Minecart", "Saddle", "Iron Door",
			"Redstone", "Snowball", "Boat", "Leather", "Milk", "Clay Brick", "Clay Balls", "Reed", "Paper", "Book", "Slimeball",
			"Storage Minecart", "Powered Minecart", "Egg", "Compass", "Fishing Rod", "Clock", "Glowstone Dust", "Raw Fish", "Cooked Fish",
			"Ink Sack", "Bone", "Sugar", "Cake"
													};
	private static final String[]	specialItems	= {
			"Gold Music Disk", "Green Music Disk"
													};
	private final ItemStack			item;
	private final int				maxSell;
	private final int				breakValue;
	private final int				maxBuy;
	
	public ShopItem() {
		itemID = 0;
		itemName = "Air";
		// privLevel = 0;
		buyPrice = 0;
		sellPrice = 0;
		maxAvail = 0;
		item = new ItemStack(0);
		maxSell = -1;
		breakValue = 0;
		maxBuy = -1;
	}
	
	public ShopItem(int itemID) {
		this.itemID = itemID;
		itemName = getName(itemID);
		// privLevel = 0;
		ShopItem temp = DataManager.getItem(itemID);
		buyPrice = temp.getBuyPrice();
		sellPrice = temp.getSellPrice();
		maxAvail = temp.getMaxAvail();
		item = new ItemStack(itemID, 1);
		maxSell = temp.getMaxSell();
		breakValue = temp.getBreakValue();
		maxBuy = temp.getMaxBuy();
	}
	
	public ShopItem(int itemID, int buyPrice, int sellPrice) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		ShopItem temp = DataManager.getItem(itemID);
		maxAvail = temp.getMaxAvail();
		maxSell = temp.getMaxSell();
		breakValue = temp.getBreakValue();
		item = new ItemStack(itemID, 1);
		maxBuy = temp.getMaxBuy();
	}
	
	public ShopItem(int itemID, int buyPrice, int sellPrice, int maxAvail, int maxSell, int maxBuy, int breakValue) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxAvail = maxAvail;
		this.maxSell = maxSell;
		this.breakValue = breakValue;
		item = new ItemStack(itemID, 1);
		this.maxBuy = maxBuy;
	}
	
	public ShopItem(String loadData) {
		String split[] = loadData.split(":");
		
		itemID = Integer.valueOf(split[0]);
		buyPrice = Integer.valueOf(split[1]);
		sellPrice = Integer.valueOf(split[2]);
		maxAvail = Integer.valueOf(split[3]);
		maxSell = Integer.valueOf(split[4]);
		maxBuy = Integer.valueOf(split[5]);
		breakValue = Integer.valueOf(split[6]);
		item = new ItemStack(itemID, 1);
		itemName = getName(itemID);
	}
	
	public int getBreakValue() {
		return breakValue;
	}
	
	public int getBuyMax() {
		return maxBuy;
	}
	
	public int getBuyPrice() {
		return buyPrice;
	}
	
	public ItemStack getItem() {
		return item;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getMaxAvail() {
		return maxAvail;
	}
	
	public int getMaxBuy() {
		return maxBuy;
	}
	
	// public int getPrivLevel() {
	// return privLevel;
	// }
	
	public int getMaxSell() {
		return maxSell;
	}
	
	public String getName() {
		return itemName;
	}
	
	private String getName(int itemID) {
		if (itemID < 255) {
			return blockNames[itemID];
		}
		else if (itemID < 500) {
			return itemNames[itemID - 256];
		}
		else if (itemID < 3000) {
			return specialItems[itemID - 2256];
		}
		
		return "Air";
		
		// I wish this worked
		// return item.getType().name();
	}
	
	public int getSellPrice() {
		return sellPrice;
	}
	
	@Override
	public String toString() {
		return itemID + ":" + buyPrice + ":" + sellPrice + ":" + maxAvail + ":" + maxSell + ":" + maxBuy + ":" + breakValue;
	}
}
