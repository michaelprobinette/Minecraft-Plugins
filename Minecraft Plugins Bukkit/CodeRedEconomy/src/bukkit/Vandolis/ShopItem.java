/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import org.bukkit.inventory.ItemStack;

/**
 * Class that is the base item used by the economy. Eventually gets added directly to the players inventory.
 * 
 * @author Vandolis
 */
public class ShopItem {
	private final String			REGEX			= DataManager.getItemRegex();
	private final int				itemId;
	private final String			itemName;
	private final int				buyPrice;
	private final int				sellPrice;
	private final int				maxAvail;
	private ItemStack				item;
	private final int				maxSell;
	private final Money				breakValue;
	private final int				maxBuy;
	private boolean					valid			= true;
	
	private static final String[]	blockNames		=
															{
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
	private static final String[]	itemNames		=
															{
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
	
	/**
	 * Creates a {@link ShopItem} based on the itemId given. Loads the rest of the data from the {@link DataManager}.
	 * 
	 * @param itemID
	 */
	public ShopItem(int itemID) {
		itemId = itemID;
		itemName = getName(itemID);
		
		/*
		 * Load the data from the DataManager
		 */
		ShopItem temp = DataManager.getItem(itemID);
		buyPrice = temp.getBuyPrice();
		sellPrice = temp.getSellPrice();
		maxAvail = temp.getMaxAvail();
		item = new ItemStack(itemID, 1);
		maxSell = temp.getMaxSell();
		breakValue = temp.getBreakValue();
		maxBuy = temp.getMaxBuy();
	}
	
	/**
	 * Constructor for loading from a save string. Format is itemId:buyPrice:sellPrice:maxAvail:maxSell:maxBuy:breakValue
	 * 
	 * @param loadData
	 */
	public ShopItem(String loadData) {
		String split[] = loadData.split(REGEX);
		
		itemId = Integer.valueOf(split[0]);
		buyPrice = Integer.valueOf(split[1]);
		sellPrice = Integer.valueOf(split[2]);
		maxAvail = Integer.valueOf(split[3]);
		maxSell = Integer.valueOf(split[4]);
		maxBuy = Integer.valueOf(split[5]);
		breakValue = new Money(Integer.valueOf(split[6]));
		item = new ItemStack(itemId, 1);
		itemName = getName(itemId);
	}
	
	/**
	 * Used by the undo transaction to make a inverted item.
	 * 
	 * @param itemId2
	 * @param sellPrice2
	 * @param buyPrice2
	 */
	public ShopItem(int itemId, int sellPrice, int buyPrice) {
		this.itemId = itemId;
		itemName = getName(itemId);
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		
		/*
		 * Load the data from the DataManager
		 */
		ShopItem temp = DataManager.getItem(itemId);
		maxAvail = temp.getMaxAvail();
		item = new ItemStack(itemId, 1);
		maxSell = temp.getMaxSell();
		breakValue = temp.getBreakValue();
		maxBuy = temp.getMaxBuy();
		
		valid = (itemId != 0);
	}
	
	/**
	 * Returns the amount of money a entity gets for destroying this item.
	 * 
	 * @return
	 */
	public Money getBreakValue() {
		return breakValue;
	}
	
	/**
	 * Returns the maximum amount a player can buy.
	 * 
	 * @return
	 */
	public int getBuyMax() {
		return maxBuy;
	}
	
	/**
	 * Returns the buy price of the item.
	 * 
	 * @return
	 */
	public int getBuyPrice() {
		return buyPrice;
	}
	
	/**
	 * Returns the actual {@link ItemStack} object.
	 * 
	 * @return
	 */
	public ItemStack getItem() {
		return item;
	}
	
	/**
	 * Returns the itemId.
	 * 
	 * @return
	 */
	public int getItemId() {
		return itemId;
	}
	
	/**
	 * @return Whether or not the item is valid.
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * Returns the maximum amount a {@link Shop} can restock to. Check {@link DataManager} for the infinite value.
	 * 
	 * @return
	 */
	public int getMaxAvail() {
		return maxAvail;
	}
	
	/**
	 * Returns the maximum amount a player can buy.
	 * 
	 * @return
	 */
	public int getMaxBuy() {
		return maxBuy;
	}
	
	/**
	 * Returns the maximum amount a player can sell.
	 * 
	 * @return
	 */
	public int getMaxSell() {
		return maxSell;
	}
	
	/**
	 * Returns the items name.
	 * 
	 * @return
	 */
	public String getName() {
		return itemName;
	}
	
	/**
	 * Used to get the item name from the arrays based on its itemId.
	 * 
	 * @param itemID
	 * @return
	 */
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
	
	/**
	 * Returns the sell price of the item.
	 * 
	 * @return
	 */
	public int getSellPrice() {
		return sellPrice;
	}
	
	/**
	 * Sets the item to the given itemStack.
	 * 
	 * @param itemStack
	 */
	protected void setItem(ItemStack itemStack) {
		item = itemStack;
	}
	
	/**
	 * Returns the save string for writing to a file. Format is itemId:buyPrice:sellPrice:maxAvail:maxSell:maxBuy:breakValue
	 * 
	 * @return
	 */
	public String getSaveString() {
		return itemId + REGEX + buyPrice + REGEX + sellPrice + REGEX + maxAvail + REGEX + maxSell + REGEX + maxBuy + REGEX
				+ breakValue.getAmount();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return itemName;
	}
}
