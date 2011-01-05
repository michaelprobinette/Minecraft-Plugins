public class ShopItem {
	private final int				itemID;
	private final String			itemName;
	// private final int privLevel;
	private final int				buyPrice;
	private final int				sellPrice;
	private final int				maxAvail;
	private static final String[]	blockNames		= {
			"Other", "Stone", "Grass", "Dirt", "Cobblestone", "Wooden Planks", "Sapling", "Bedrock", "Water", "Stationary Water", "Lava",
			"Stationary Lava", "Sand", "Gravel", "Gold Ore", "Iron Ore", "Coal Ore", "Wood", "Leaves", "Sponge", "Glass", "Red Cloth",
			"Orange Cloth", "Yellow Cloth", "Lime Cloth", "Green Cloth", "Aquagreen Cloth", "Cyan Cloth", "Blue Cloth", "Purple Cloth",
			"Indigo Cloth", "Violet Cloth", "Magenta Cloth", "Pink Cloth", "Black Cloth", "Wool", "White Cloth", "Yellow Flower",
			"Red Rose", "Brown Mushroom", "Red Mushroom", "Gold Block", "Iron Block", "Double Stone Slab", "Stone Slab", "Brick", "TNT",
			"Bookshelf", "Moss Stone", "Obsidian", "Torch", "Fire", "Monster Spawner", "Wodden Stairs", "Chest", "Redstone Wire",
			"Diamond Ore", "Diamond Block", "Workbench", "Crops", "Soil", "Furnace", "Burning Furnace", "Sign Post", "Wooden Door",
			"Ladder", "Minecart Tracks", "Cobblestone Stairs", "Wall Sign", "Lever", "Stone Pressure Plate", "Iron Door",
			"Wooden Pressure Plate", "Redstone Ore", "GlowingRedstone", "RedstoneTorchOf", "RedstoneTorchOn", "StoneButton", "Snow", "Ice",
			"SnowBlock", "Cactus", "Clay", "Reed", "Jukebox", "Fence", "Pumpkin", "Netherrack", "Soul Sand", "Glowstone", "Portal",
			"Jack-O-Lantern"
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
			"Storage Minecart", "Powered Minecart", "Egg", "Compass", "Fishing Rod", "Clock", "Glowstone Dust", "Raw Fish", "Cooked Fish"
													};
	private static final String[]	specialItems	= {
			"Gold Music Disk", "Green Music Disk"
													};
	private final Item				item;
	
	public ShopItem() {
		itemID = 0;
		itemName = "Air";
		// privLevel = 0;
		buyPrice = 0;
		sellPrice = 0;
		maxAvail = 0;
		item = new Item();
	}
	
	public ShopItem(int itemID) {
		this.itemID = itemID;
		itemName = getName(itemID);
		// privLevel = 0;
		buyPrice = DataManager.getBuyPrice(itemID);
		sellPrice = DataManager.getSellPrice(itemID);
		maxAvail = DataManager.getMaxAvail(itemID);
		item = new Item(itemID, 1);
	}
	
	public ShopItem(int itemID, int buyPrice) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.buyPrice = buyPrice;
		sellPrice = DataManager.getSellPrice(itemID);
		maxAvail = DataManager.getMaxAvail(itemID);
		item = new Item(itemID, 1);
	}
	
	public ShopItem(int itemID, int buyPrice, int sellPrice) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		maxAvail = DataManager.getMaxAvail(itemID);
		item = new Item(itemID, 1);
	}
	
	public ShopItem(int itemID, int buyPrice, int sellPrice, int maxAvail) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.buyPrice = buyPrice;
		this.sellPrice = sellPrice;
		this.maxAvail = maxAvail;
		item = new Item(itemID, 1);
	}
	
	public String getName() {
		return itemName;
	}
	
	public int getMaxAvail() {
		return maxAvail;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public Item getItem() {
		return item;
	}
	
	// public int getPrivLevel() {
	// return privLevel;
	// }
	
	public int getBuyPrice() {
		return buyPrice;
	}
	
	public int getSellPrice() {
		return sellPrice;
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
	}
	
	@Override
	public String toString() {
		return itemID + ":" + buyPrice + ":" + sellPrice + ":" + maxAvail;
	}
}
