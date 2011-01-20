/**
 * 
 */
package bukkit.Vandolis;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * @author Vandolis
 */
public class SignDispenser extends JavaPlugin {
	private final SignDispenserBlockListener	blockListener	= new SignDispenserBlockListener(this);
	private final File							OPS_FILE		= new File("ops.txt");
	private ArrayList<String>					ops				= new ArrayList<String>();
	
	private static final String[]				blockNames		=
																		{
			"Air", "Stone", "Grass", "Dirt", "Cobblestone", "Wood", "Sapling", "Bedrock", "Water", "StationaryWater", "Lava",
			"StationaryLava", "Sand", "Gravel", "GoldOre", "IronOre", "CoalOre", "Log", "Leaves", "Sponge", "Glass", "LapisLazuliOre",
			"LapisLazuli", "Dispenser", "Sandstone", "NoteBlock", "", "", "", "", "", "", "", "", "", "Wool", "", "YellowFlower",
			"RedRose", "BrownMushroom", "RedMushroom", "GoldBlock", "IronBlock", "DoubleStoneSlab", "StoneSlab", "Brick", "TNT",
			"Bookshelf", "MossStone", "Obsidian", "Torch", "Fire", "MonsterSpawner", "WoddenStairs", "Chest", "RedstoneWire", "DiamondOre",
			"DiamondBlock", "Workbench", "Crops", "Soil", "Furnace", "BurningFurnace", "SignPost", "WoodenDoor", "Ladder",
			"MinecartTracks", "CobbleStairs", "WallSign", "Lever", "StonePressure", "IronDoor", "WoodenPressure", "RedstoneOre",
			"GlowingRedstone", "RedstoneTorchOf", "RedstoneTorchOn", "StoneButton", "Snow", "Ice", "SnowBlock", "Cactus", "Clay", "Reed",
			"Jukebox", "Fence", "Pumpkin", "Netherrack", "SoulSand", "Glowstone", "Portal", "Jack-O-Lantern", "CakeBlock"
																		};
	private static final String[]				itemNames		=
																		{
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
			"CookedFish", "Dye", "Bone", "Sugar", "Cake"
																		};
	private static final String[]				specialItems	= {
			"GoldMusicDisk", "GreenMusicDisk"
																};
	
	public SignDispenser(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		loadOps();
		// NOTE: Event registration should be done in onEnable not here as all events are unregistered when a plugin is disabled
	}
	
	/**
	 * 
	 */
	private void loadOps() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(OPS_FILE));
			String raw = "";
			while ((raw = reader.readLine()) != null) {
				ops.add(raw);
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void onEnable() {
		// Register our events
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_PLACED, blockListener, Priority.Normal, this);
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	public void onDisable() {
		// NOTE: All registered events are automatically unregistered when a plugin is disabled
		
		// EXAMPLE: Custom code, here we just output some info so we can check all is well
		System.out.println("Goodbye world!");
	}
	
	public int getArrayIndex(String[] arr, String str) {
		for (int i = 0; i < arr.length; i++) {
			if (arr[i].equalsIgnoreCase(str)) {
				return i;
			}
		}
		return -1;
	}
	
	public boolean isOp(String name) {
		for (String iter : ops) {
			if (iter.equalsIgnoreCase(name)) {
				return true;
			}
		}
		return false;
	}
	
	public String getName(int itemID) {
		if (itemID < 255) {
			return getBlocknames()[itemID];
		}
		else if (itemID < 500) {
			return getItemNames()[itemID - 256];
		}
		else if (itemID < 3000) {
			return specialItems[itemID - 2256];
		}
		
		return "";
	}
	
	/**
	 * @return the itemnames
	 */
	public static String[] getItemNames() {
		return itemNames;
	}
	
	/**
	 * @return the blocknames
	 */
	public static String[] getBlocknames() {
		return blockNames;
	}
}