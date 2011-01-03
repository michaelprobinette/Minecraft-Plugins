public class ShopItem {
	private final int				itemID;
	private final String			itemName;
	private final int				privLevel;
	private static final String[]	blockNames		= {
			"Air", "Stone", "Grass", "Dirt", "Cobblestone", "Wood", "Sapling", "Bedrock", "Water", "Stationary Water", "Lava",
			"Stationary Lava", "Sand", "Gravel", "Gold Ore", "Iron Ore", "Coal Ore", "Log", "Leaves", "Sponge", "Glass", "Red Cloth",
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
	
	public ShopItem() {
		itemID = 0;
		itemName = "Air";
		privLevel = 0;
	}
	
	public ShopItem(int itemID) {
		this.itemID = itemID;
		itemName = getName(itemID);
		privLevel = 0;
	}
	
	public ShopItem(int itemID, int privLevel) {
		this.itemID = itemID;
		itemName = getName(itemID);
		this.privLevel = privLevel;
	}
	
	public String getName() {
		return itemName;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getPrivLevel() {
		return privLevel;
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
}