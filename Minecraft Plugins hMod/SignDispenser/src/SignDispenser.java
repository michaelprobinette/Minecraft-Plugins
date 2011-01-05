/*
 * Minecraft plugin that allows signs to give items on right click. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

import java.util.logging.Logger;

/**
 * @author Vandolis
 */
public class SignDispenser extends Plugin {
	private Listener				l				= new Listener(this);
	protected static final Logger	log				= Logger.getLogger("Minecraft");
	private String					name			= "SignDispenser";
	private String					version			= "v1.6.0";
	private static boolean			useNames		= false;
	private static String[]			useGroups		= null;
	private static String[]			destroyGroups	= null;
	private static String[]			createGroups	= null;
	private static final String[]	blockNames		= {
			"Air", "Stone", "Grass", "Dirt", "Cobblestone", "Wood", "Sapling", "Bedrock", "Water", "StationaryWater", "Lava",
			"StationaryLava", "Sand", "Gravel", "GoldOre", "IronOre", "CoalOre", "Log", "Leaves", "Sponge", "Glass", "RedCloth",
			"OrangeCloth", "YellowCloth", "LimeCloth", "GreenCloth", "AquagreenCloth", "CyanCloth", "BlueCloth", "PurpleCloth",
			"IndigoCloth", "VioletCloth", "MagentaCloth", "PinkCloth", "BlackCloth", "Wool", "WhiteCloth", "YellowFlower", "RedRose",
			"BrownMushroom", "RedMushroom", "GoldBlock", "IronBlock", "DoubleStoneSlab", "StoneSlab", "Brick", "TNT", "Bookshelf",
			"MossStone", "Obsidian", "Torch", "Fire", "MonsterSpawner", "WoddenStairs", "Chest", "RedstoneWire", "DiamondOre",
			"DiamondBlock", "Workbench", "Crops", "Soil", "Furnace", "BurningFurnace", "SignPost", "WoodenDoor", "Ladder",
			"MinecartTracks", "CobbleStairs", "WallSign", "Lever", "StonePressure", "IronDoor", "WoodenPressure", "RedstoneOre",
			"GlowingRedstone", "RedstoneTorchOf", "RedstoneTorchOn", "StoneButton", "Snow", "Ice", "SnowBlock", "Cactus", "Clay", "Reed",
			"Jukebox", "Fence", "Pumpkin", "Netherrack", "SoulSand", "Glowstone", "Portal", "Jack-O-Lantern"
													};
	private static final String[]	itemNames		= {
			"IronShovel", "IronPickaxe", "IronAxe", "FlintandSteel", "Apple", "Bow", "Arrow", "Coal", "Diamond", "IronIngot", "GoldIngot",
			"IronSword", "WoodenSword", "WoodernShovel", "WoodenPickaxe", "WoodenAxe", "StoneSword", "StoneShovel", "StonePickaxe",
			"StoneAxe", "DiamondSword", "DiamondShovel", "DiamondPickaxe", "DiamondAxe", "Stick", "Bowl", "MushroomSoup", "GoldSword",
			"GoldShovel", "GoldPickaxe", "GoldAxe", "String", "Feather", "Sulphur", "WoodenHoe", "StoneHoe", "IronHoe", "DiamondHoe",
			"GoldHoe", "Seeds", "Wheat", "Bread", "LeatherHelmet", "LeatherChest", "LeatherLeg", "LeatherBoots", "ChainmailHelmet",
			"ChainmailChest", "ChainmailLeg", "ChainmailBoots", "IronHelmet", "IronChest", "IronLeggings", "IronBoots", "DiamondHelmet",
			"DiamondChest", "DiamondLeg", "DiamondBoots", "GoldHelmet", "GoldChestplate", "GoldLeggings", "GoldBoots", "Flint",
			"RawPorkchop", "CookedPorkchop", "Painting", "GoldenApple", "Sign", "WoodenDoor", "Bucket", "WaterBucket", "LavaBucket",
			"Minecart", "Saddle", "IronDoor", "Redstone", "Snowball", "Boat", "Leather", "Milk", "ClayBrick", "ClayBalls", "Reed", "Paper",
			"Book", "Slimeball", "StorageMinecart", "PoweredMinecart", "Egg", "Compass", "FishingRod", "Clock", "GlowstoneDust", "RawFish",
			"CookedFish"
													};
	private static final String[]	specialItems	= {
			"GoldMusicDisk", "GreenMusicDisk"
													};
	private PropertiesFile			props			= new PropertiesFile("SignDispenser.properties");
	
	public void enable() {
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_DESTROYED, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.BLOCK_RIGHTCLICKED, l, this, PluginListener.Priority.MEDIUM);
		
		if (!props.containsKey("usegroups")) {
			props.setString("usegroups", "all");
			props.save();
		}
		if (!props.containsKey("creategroups")) {
			props.setString("creategroups", "admins");
			props.save();
		}
		if (!props.containsKey("destroygroups")) {
			props.setString("destroygroups", "admins");
			props.save();
		}
		if (!props.containsKey("useitemnames")) {
			props.setBoolean("useitemnames", true);
		}
		// Get the data from the properties file, then split and trim for use
		String useGroupsRaw = props.getString("usegroups");
		useGroups = useGroupsRaw.split(",");
		String destroyGroupsRaw = props.getString("destroygroups");
		destroyGroups = destroyGroupsRaw.split(",");
		String createGroupsRaw = props.getString("creategroups");
		createGroups = createGroupsRaw.split(",");
		// Trim values
		for (int x = 0; x < useGroups.length; x++) {
			useGroups[x] = useGroups[x].trim();
		}
		for (int x = 0; x < destroyGroups.length; x++) {
			destroyGroups[x] = destroyGroups[x].trim();
		}
		for (int x = 0; x < createGroups.length; x++) {
			createGroups[x] = createGroups[x].trim();
		}
		useNames = props.getBoolean("useitemnames");
	}
	
	public int getArrayIndex(String[] arr, String str) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equalsIgnoreCase(str)) {
				return i;
			}
		}
		return -1;
	}
	
	public String getName(int itemID) {
		if (itemID < 255) {
			return blockNames[itemID];
		}
		else if (itemID < 500) {
			return itemNames[itemID - 256];
		}
		else if (itemID < 3000) {
			return specialItems[itemID - 2256];
		}
		
		return "";
	}
	
	// Sends a message to all players!
	public void broadcast(String message) {
		etc.getServer().messageAll(message);
	}
	
	public class Listener extends PluginListener {
		SignDispenser	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(SignDispenser plugin) {
			p = plugin;
		}
		
		public void onBlockRightClicked(Player player, Block blockClicked, Item item) {
			if (blockClicked != null) {
				int x;
				int y;
				int z;
				String line1;
				boolean canUse = false;
				for (int b = 0; b < useGroups.length; b++) {
					useGroups[b] = useGroups[b].trim();
				}
				
				// Check if player is in group
				for (int n = 0; n < useGroups.length; n++) {
					if (useGroups[n].equalsIgnoreCase("all")) {
						canUse = true;
					}
					else if (player.isInGroup(useGroups[n])) {
						canUse = true;
					}
				}
				if (player.isAdmin()) {
					canUse = true;
				}
				if ((blockClicked.getType() == 63 || blockClicked.getType() == 68)) {
					x = blockClicked.getX();
					y = blockClicked.getY();
					z = blockClicked.getZ();
					
					try {
						Sign si = (Sign) (etc.getServer().getComplexBlock(x, y, z));
						si.update();
						// Get the sign text
						line1 = si.getText(0);
						String line2 = si.getText(1);
						String line3 = si.getText(2);
						String line4 = si.getText(3);
						int itemID = -1, amount = 1;
						if ((line1.equalsIgnoreCase("Item ID") || line1.equalsIgnoreCase("Right Click:")) && canUse) {
							try {
								if (!line2.equalsIgnoreCase("Incorrect ID")) {
									if ((itemID = getArrayIndex(itemNames, line2)) != -1) {
										itemID += 256;
										if (line3.equalsIgnoreCase("amount:")) {
											amount = Integer.parseInt(line4.trim());
										}
									}
									else if ((itemID = getArrayIndex(blockNames, line2)) != -1) {
										if (line3.equalsIgnoreCase("amount:")) {
											amount = Integer.parseInt(line4.trim());
										}
									}
									else {
										int temp = Integer.parseInt(line2.trim());
										if (Item.isValidItem(temp)) {
											itemID = temp;
										}
									}
									if (Item.isValidItem(itemID)) {
										player.giveItem(itemID, amount);
										player.sendMessage("[" + Colors.Yellow + "SignDispenser" + Colors.White + "] Here You go!");
									}
									else {
										si.setText(1, "Incorrect ID");
										si.update();
									}
								}
							}
							catch (Exception e) {
								e.printStackTrace();
							}
						}
						else if (line1.equalsIgnoreCase("Item id") && !canUse) {
							player.sendMessage("[" + Colors.Yellow + "SignDispenser" + Colors.White
									+ "] You do not have permission to use this.");
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
				if (((blockClicked.getType() == 63) || (blockClicked.getType() == 68))
						&& ((blockClicked.getStatus() == 0) || (blockClicked.getStatus() == 1) || (blockClicked.getStatus() == 3))) {
					
					x = blockClicked.getX();
					y = blockClicked.getY();
					z = blockClicked.getZ();
					
					try {
						boolean canDestroy = false;
						for (int b = 0; b < destroyGroups.length; b++) {
							destroyGroups[b] = destroyGroups[b].trim();
						}
						
						// Check if player is in group
						for (int n = 0; n < destroyGroups.length; n++) {
							if (destroyGroups[n].equalsIgnoreCase("all")) {
								canDestroy = true;
							}
							else if (player.isInGroup(destroyGroups[n])) {
								canDestroy = true;
							}
						}
						
						Sign si = (Sign) (etc.getServer().getComplexBlock(x, y, z));
						// Get the sign text
						line1 = si.getText(0);
						
						if (line1.equalsIgnoreCase("Item ID") && !canDestroy) {
							si.update();
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			
		}
		
		public boolean onBlockDestroy(Player player, Block block) {
			int x = 0;
			int y = 0;
			int z = 0;
			String line1 = "";
			String groups[];
			boolean admin = false;
			if (player.isAdmin()) {
				admin = true;
			}
			else if (!player.hasNoGroups()) {
				groups = player.getGroups();
				for (int d = 0; d < groups.length; d++) {
					if (groups[d].contains("admin") || (groups[d].contains("sign"))) {
						admin = true;
					}
				}
			}
			if (block != null) {
				if (((block.getType() == 63) || (block.getType() == 68))
						&& ((block.getStatus() == 0) || (block.getStatus() == 1) || (block.getStatus() == 3))) {
					
					x = block.getX();
					y = block.getY();
					z = block.getZ();
					
					try {
						Sign si = (Sign) (etc.getServer().getComplexBlock(x, y, z));
						// Get the sign text
						if (si != null) {
							boolean canDestroy = false;
							for (int b = 0; b < destroyGroups.length; b++) {
								destroyGroups[b] = destroyGroups[b].trim();
							}
							
							// Check if player is in group
							for (int n = 0; n < destroyGroups.length; n++) {
								if (destroyGroups[n].equalsIgnoreCase("all")) {
									canDestroy = true;
								}
								else if (player.isInGroup(destroyGroups[n])) {
									canDestroy = true;
								}
							}
							line1 = si.getText(0);
							if (line1.equalsIgnoreCase("item id") || line1.equalsIgnoreCase("Right click")
									|| line1.equalsIgnoreCase("Right click for")) {
								// log.log(Level.INFO, "Reformatting Sign");
								// Update to the new format
								si.setText(0, "Right Click:");
								si.update();
								if (getArrayIndex(blockNames, si.getText(1)) == -1 && getArrayIndex(itemNames, si.getText(1)) == -1
										&& getArrayIndex(specialItems, si.getText(1)) == -1) {
									String tempS = si.getText(1);
									int tempI = -1;
									tempI = Integer.parseInt(tempS.trim());
									if (tempI == -1) {
										si.setText(1, "Could not read");
										si.update();
									}
									else {
										si.setText(1, getName(tempI));
									}
								}
								String line3 = si.getText(2);
								if (line3.equalsIgnoreCase("amount")) {
									si.setText(2, "Amount:");
									si.update();
								}
								etc.getServer().getBlockAt(si.getX(), si.getY(), si.getZ()).update();
							}
							if ((line1.equalsIgnoreCase("Item ID") || line1.equalsIgnoreCase("Right Click:")) && !admin && !canDestroy) {
								etc.getServer().getBlockAt(si.getX(), si.getY(), si.getZ()).update();
								return true;
							}
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}
		
		public boolean onSignChange(Player player, Sign s) {
			String groups[];
			boolean admin = false;
			boolean canCreate = false;
			
			for (int b = 0; b < createGroups.length; b++) {
				createGroups[b] = createGroups[b].trim();
			}
			
			// Check if player is in group
			for (int n = 0; n < createGroups.length; n++) {
				if (createGroups[n].equalsIgnoreCase("all")) {
					canCreate = true;
				}
				else if (player.isInGroup(createGroups[n])) {
					canCreate = true;
				}
			}
			
			if (player.isAdmin()) {
				admin = true;
			}
			else if (!player.hasNoGroups()) {
				groups = player.getGroups();
				for (int x = 0; x < groups.length; x++) {
					if (groups[x].contains("admin") || (groups[x].contains("sign"))) {
						admin = true;
					}
				}
			}
			
			if (!admin && !canCreate) {
				String line1 = s.getText(0);
				if (line1.equalsIgnoreCase("Item ID") || line1.equalsIgnoreCase("Right Click:")) {
					s.setText(0, "Only Admins");
					s.setText(1, "Can Construct");
					s.setText(2, "Sign Dispensers");
					s.setText(3, "Sorry D:");
				}
			}
			else if (admin || canCreate) {
				String line1 = s.getText(0);
				String line2 = s.getText(1);
				String line3 = s.getText(2);
				if (line1.equalsIgnoreCase("Item ID") || line1.equalsIgnoreCase("Right Click:")) {
					s.setText(0, "Right Click:");
					int itemID = 0;
					if (line3.equalsIgnoreCase("amount")) {
						s.setText(2, "Amount:");
						s.update();
					}
					try {
						itemID = Integer.parseInt(line2.trim());
						if (!Item.isValidItem(itemID)) {
							s.setText(1, "Incorrect ID");
							s.update();
						}
						else if (useNames) {
							s.setText(1, getName(itemID));
						}
					}
					catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
			return false;
		}
	}
}
