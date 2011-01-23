/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;

/**
 * Used to load all of the data needed to run the plugin.
 * 
 * @author Vandolis
 */
public class DataManager {
	/*
	 * General stuff
	 */
	private static String					LOC					= "plugins/CodeRedEconomy/";
	private static boolean					dirExists			= false;
	private static PropertiesFile			props				= new PropertiesFile(LOC + "data.properties");	// Properties file
	private static boolean					debug				= false;
	private static String					pluginMessage		= "[§cCodeRedEcon§f] ";
	private static int						infValue			= -1;
	private static Server					server				= null;
	
	/*
	 * SQLite
	 */
	private static boolean					useSQL				= false;
	private static final String				DB					= "jdbc:sqlite:CodeRedEconomy";
	
	/*
	 * Regex stuff
	 */
	private static final String				PLAYER_REGEX		= ":";
	private static final String				PLAYER2_REGEX		= " ";
	private static final String				SHOP_REGEX			= ":";
	private static final String				SHOP2_REGEX			= " ";
	private static final String				STATS_REGEX			= ":";
	private static final String				STATS2_REGEX		= " ";
	private static final String				ITEM_REGEX			= ":";
	
	/*
	 * Money stuff
	 */
	private static String					moneyName			= "Strypes";
	
	/*
	 * Shop stuff
	 * Shops Holds all of the shops, might use to make different shops based on location or something, might use in the future. Or right now.
	*/
	static ArrayList<Shop>					shops				= new ArrayList<Shop>();
	private static long						restockTime			= 60000;
	private static final File				file_shop			= new File(LOC + "shops.txt");
	
	/*
	 * Privilege stuff
	 */
	private static final File				file_privGroups		= new File(LOC + "shopGroups.txt");
	private static ArrayList<ShopGroup>		privGroups			= new ArrayList<ShopGroup>();
	
	/*
	 * Items stuff
	 */
	private static final File				file_itemlist		= new File(LOC + "items.txt");
	private static ArrayList<ShopItem>		itemList			= new ArrayList<ShopItem>();
	
	/*
	 * Player data... stuff
	 */
	private static final File				file_playerData		= new File(LOC + "users.txt");
	private static ArrayList<User>			users				= new ArrayList<User>();
	private static long						autoDepositTime		= 60000;
	private static int						autoDepositAmount	= 50;
	private static long						maxBuySellTime		= 20000;
	
	/*
	 * Stats stuff
	 */
	private static boolean					useStats			= true;
	private static final File				file_stats			= new File(LOC + "stats.txt");
	
	/*
	 * Bad word stuff
	 */
	private static HashMap<String, Integer>	badWords			= new HashMap<String, Integer>();
	private static boolean					blockBadWords		= false;
	private static final File				file_badWords		= new File(LOC + "badWords.txt");
	private static final boolean			messageOnBadWord	= true;
	
	public DataManager(CodeRedEconomy instance) {
		load(instance);
	}
	
	public static String getDataLoc() {
		return LOC;
	}
	
	/**
	 * Adds a {@link Shop} to the list of shops. Writes the array to file.
	 * 
	 * @param shop
	 */
	public static void addShop(Shop shop) {
		/*
		 * Double check to make sure a shop under the same name does not exist
		 */
		boolean found = false;
		
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(shop.getName())) {
				found = true;
			}
		}
		
		if (!found) {
			if (debug) {
				System.out.println("Adding shop under the name of: " + shop.getName());
			}
			shops.add(shop);
			ShopList.populate();
			
			/*
			 * Write the new array to file.
			 */
			try {
				write("shops");
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else {
			if (debug) {
				System.out.println("Shop not added, found a duplicate.");
			}
		}
	}
	
	/**
	 * Adds a {@link User} to the list of users if they aren't found. AutoDeposists money, then writes to file.
	 * 
	 * @param user
	 */
	public static void addUser(User user) {
		user.autoDesposit(server.getTime());
		// Check if user is already in there
		boolean found = false;
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(user.getName())) {
				iter = user;
				found = true;
			}
		}
		if (!found) {
			users.add(user);
		}
		
		// Write to file
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Checks the given group and itemId against the list of {@link ShopGroup} to see what blocks they are allowed.
	 * 
	 * @param group
	 * @param itemId
	 * @return True if allowed to buy, false if not
	 */
	public static boolean allowedBlock(String group, int itemId) {
		for (ShopGroup iter : privGroups) {
			System.out.println("Checking " + iter.getGroupName() + " against " + group);
			if (iter.getGroupName().equalsIgnoreCase(group)) {
				for (int biter : iter.getAllowed()) {
					if (biter == itemId) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * @return A boolean of whether to check for bad words or not.
	 */
	public static boolean blockBadWords() {
		return blockBadWords;
	}
	
	/**
	 * @return The amount of {@link Money} to add for an autoDeposit
	 */
	public static int getAutoDepositAmount() {
		return autoDepositAmount;
	}
	
	/**
	 * @return The amount of time needed to pass before autoDepositing.
	 */
	public static long getAutoDepositTime() {
		return autoDepositTime;
	}
	
	/**
	 * Checks the given message for a bad word. If found returns the bad word.
	 * 
	 * @param message
	 *            to check
	 * @return The bad word the message contains.
	 */
	public static String getBadWord(String message) {
		for (String iter : badWords.keySet()) {
			if (message.contains(iter)) {
				return iter;
			}
		}
		return "";
	}
	
	/**
	 * @return The HashMap of badWords and their penalty
	 */
	public static HashMap<String, Integer> getBadWords() {
		return badWords;
	}
	
	/**
	 * @param itemId
	 * @return the buy price for the given itemId
	 */
	public static int getBuyPrice(int itemId) {
		for (ShopItem iter : itemList) {
			if (iter.getItemId() == itemId) {
				return iter.getBuyPrice();
			}
		}
		return 0;
	}
	
	/**
	 * @return The debug setting. True if enabled.
	 */
	public static boolean getDebug() {
		return debug;
	}
	
	/**
	 * Searches the list of {@link ShopGroup} to find the one associated with the given groupName.
	 * 
	 * @param groupName
	 * @return associated {@link ShopGroup}
	 */
	public static ShopGroup getGroup(String groupName) {
		for (ShopGroup iter : privGroups) {
			if (iter.getGroupName().equalsIgnoreCase(groupName)) {
				return iter;
			}
		}
		return null;
	}
	
	/**
	 * @return ArrayList of the {@link ShopGroup}
	 */
	public static ArrayList<ShopGroup> getGroups() {
		return privGroups;
	}
	
	/**
	 * @return the value used for infinite in {@link Money} and {@link ShopItemStack} amounts.
	 */
	public static int getInfValue() {
		return infValue;
	}
	
	/**
	 * Searches the list of {@link ShopItem} for the given itemId. Returns null if not found.
	 * 
	 * @param itemId
	 * @return the {@link ShopItem} with the same itemId
	 */
	public static ShopItem getItem(int itemId) {
		for (ShopItem iter : itemList) {
			if (iter.getItemId() == itemId) {
				return iter;
			}
		}
		return null;
	}
	
	/**
	 * Searches the list of {@link ShopItem} for the given itemName. Returns 0 if not found.
	 * 
	 * @param itemName
	 * @return itemId of the item.
	 */
	public static int getItemId(String itemName) {
		for (ShopItem iter : itemList) {
			if (iter.getName().equalsIgnoreCase(itemName)) {
				return iter.getItemId();
			}
		}
		return 0;
	}
	
	/**
	 * @return The list of loaded {@link ShopItem}
	 */
	public static ArrayList<ShopItem> getItemList() {
		return itemList;
	}
	
	/**
	 * @return Regex used for items
	 */
	public static String getItemRegex() {
		return ITEM_REGEX;
	}
	
	/**
	 * Searches the list of {@link ShopItem} and returns the maxAvailable for shops. Returns 0 if not found.
	 * 
	 * @param itemID
	 * @return
	 */
	public static int getMaxAvail(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemId() == itemID) {
				return iter.getMaxAvail();
			}
		}
		return 0;
	}
	
	/**
	 * @return Official name of {@link Money}
	 */
	public static String getMoneyName() {
		return moneyName;
	}
	
	/**
	 * @return second regex used for players
	 */
	public static String getPlayer2Regex() {
		return PLAYER2_REGEX;
	}
	
	/**
	 * @return regex used for players
	 */
	public static String getPlayerRegex() {
		return PLAYER_REGEX;
	}
	
	/**
	 * @return The string to put in front of messages from the plugin.
	 */
	public static String getPluginMessage() {
		return pluginMessage;
	}
	
	/**
	 * @return The amount of time needed to pass before restocking a shop
	 */
	public static long getRestockTime() {
		return restockTime;
	}
	
	/**
	 * Searches the list of {@link ShopItem} for the given itemId. Returns 0 if not found.
	 * 
	 * @param itemId
	 * @return the items sell price.
	 */
	public static int getSellPrice(int itemId) {
		for (ShopItem iter : itemList) {
			if (iter.getItemId() == itemId) {
				return iter.getSellPrice();
			}
		}
		return 0;
	}
	
	/**
	 * @return the current server
	 */
	public static Server getServer() {
		return server;
	}
	
	/**
	 * Searches the list of shops for the given {@link EconEntity}. If not found, adds a new {@link Shop} to the list and returns it.
	 * 
	 * @param ent
	 * @return
	 */
	public static Shop getShop(EconEntity ent) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(ent.getName())) {
				if (debug) {
					System.out.println("Shop found under the name of " + iter.getName() + " returning.");
				}
				return iter;
			}
		}
		
		/*
		 * No shop found, make a new one with default values
		 */
		Shop temp;
		if (ent instanceof Shop) {
			temp = (Shop) ent;
		}
		else {
			temp = new Shop(ent.getName());
		}
		
		addShop(temp);
		return temp;
	}
	
	/**
	 * Searches the list of shops for the given name. If not found, adds a new {@link Shop} to the list and returns it.
	 * 
	 * @param name
	 * @return
	 */
	public static Shop getShop(String name) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(name)) {
				if (debug) {
					System.out.println("Shop found under the name of " + iter.getName() + " returning.");
				}
				return iter;
			}
		}
		
		/*
		 * No shop found, make a new one with default values
		 */
		Shop temp = new Shop(name, false, -1, true);
		addShop(temp);
		return temp;
	}
	
	/**
	 * @return second regex used for {@link Shop}
	 */
	public static String getShop2Regex() {
		return SHOP2_REGEX;
	}
	
	/**
	 * @return regex used for {@link Shop}
	 */
	public static String getShopRegex() {
		return SHOP_REGEX;
	}
	
	/**
	 * @return List of {@link Shop}
	 */
	public static ArrayList<Shop> getShops() {
		return shops;
	}
	
	/**
	 * @return second regex used for stats
	 */
	public static String getStats2Regex() {
		return STATS2_REGEX;
	}
	
	/**
	 * @return regex used for stats
	 */
	public static String getStatsRegex() {
		return STATS_REGEX;
	}
	
	/**
	 * Searches the list of {@link User} for the given {@link Player}. If not found adds a new user with the given player tied to the list
	 * and returns it.
	 * 
	 * @param player
	 * @return
	 */
	public static User getUser(Player player) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(player.getName())) {
				iter.setPlayer(player);
				iter.autoDesposit(server.getTime());
				return iter;
			}
		}
		
		/*
		 * User not found, make a new user and tie the player to it.
		 * Add the new user to the user list and write to file
		 */
		User temp = new User(player);
		temp.autoDesposit(server.getTime());
		addUser(temp);
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temp;
	}
	
	/**
	 * Searches the list of {@link User} for the given name. If a user is not found makes adds a new user with the given name to the list
	 * and
	 * returns it.
	 * 
	 * @param name
	 * @return
	 */
	public static User getUser(String name) {
		if (name.contains(":")) {
			name = name.replace(":", "");
		}
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(name)) {
				if (debug) {
					System.out.println("Was looking for: " + name + " and found " + iter.getName());
				}
				iter.autoDesposit(server.getTime());
				return iter;
			}
		}
		
		/*
		 * User not found, make a new one and add it to the list.
		 * Write to file.
		 */
		User temp = new User(name);
		temp.autoDesposit(server.getTime());
		addUser(temp);
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temp;
	}
	
	/**
	 * @return the list of users
	 */
	public static ArrayList<User> getUsers() {
		return users;
	}
	
	/**
	 * Used to load a static {@link DataManager}. Sets the server to the given one, and reads all the files.
	 * 
	 * @param instance
	 */
	public static void load(CodeRedEconomy instance) {
		System.out.println("Loading CodeRedEconomy...");
		
		server = instance.getServer();
		
		/*
		 * Check to see if the dir exists
		 */
		dirExists = new File(LOC).exists();
		if (!dirExists) {
			dirExists = new File(LOC).mkdir();
		}
		
		/*Change the folder location to the given one*/
		//		LOC = instance.getDataFolder().getPath();
		
		/*Read data from properties file*/
		readProps();
		
		/*Read from the items file*/
		readItemFile();
		
		/*Read from the users file*/
		readUserFile();
		
		/*Read from the group priv file*/
		readPrivFile();
		
		/*Read from the stats file*/
		readStatsFile();
		
		/*Read from the shop file*/
		readShopFile();
		
		if (blockBadWords) {
			/*Read bad words file*/
			readBadWords();
		}
	}
	
	/**
	 * @return True if a message should be sent to the offending user.
	 */
	public static boolean messageOnBadWord() {
		return messageOnBadWord;
	}
	
	/**
	 * Loads the bad words from file
	 */
	private static void readBadWords() {
		if (dirExists) {
			System.out.println("Reading Bad Words File...");
			
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_badWords));
				while ((raw = reader.readLine()) != null) {
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						String split[] = raw.split("=");
						String word = split[0].trim();
						int cost = Integer.valueOf(split[1].trim());
						badWords.put(word, cost);
					}
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					/*
					 * File not found, create empty file
					 */
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_badWords));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads the item data from file.
	 * Item file formatting is "itemID:buyPrice:sellPrice:maxAvail"
	 */
	private static void readItemFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			itemList = new ArrayList<ShopItem>();
			
			try {
				reader = new BufferedReader(new FileReader(file_itemlist));
				
				while ((raw = reader.readLine()) != null) {
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						ShopItem temp = new ShopItem(raw);
						itemList.add(temp);
						if (debug) {
							System.out.println("Raw item data read: " + raw);
							System.out.println("Item data: " + temp.toString());
						}
					}
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					/*
					 * File not found, create empty file
					 */
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_itemlist));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads the {@link ShopGroup} data from file.
	 */
	private static void readPrivFile() {
		if (dirExists) {
			System.out.println("Reading Groups File...");
			
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_privGroups));
				while ((raw = reader.readLine()) != null) {
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						String split[] = raw.split(":");
						if (split.length >= 2) {
							String groupName = split[0];
							String a[] = split[1].split(",");
							int blocks[] = new int[a.length];
							int count = 0;
							for (String iter : a) {
								iter = iter.trim();
								int temp = Integer.valueOf(iter);
								blocks[count] = temp;
								count++;
							}
							ShopGroup temp = new ShopGroup(groupName, blocks);
							privGroups.add(temp);
						}
					}
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					/*
					 * File not found, create empty file
					 */
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_privGroups));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Reads the {@link PropertiesFile}
	 */
	private static void readProps() {
		if (dirExists) {
			System.out.println("Reading properties file...");
			
			if (props.containsKey("moneyname")) {
				moneyName = props.getString("moneyname");
			}
			else {
				props.setString("moneyname", moneyName);
			}
			
			if (props.containsKey("debug")) {
				debug = props.getBoolean("debug");
			}
			else {
				props.setBoolean("debug", debug);
			}
			
			if (props.containsKey("restocktime")) {
				restockTime = props.getLong("restocktime");
			}
			else {
				props.setLong("restocktime", restockTime);
			}
			
			if (props.containsKey("ingamemessage")) {
				pluginMessage = props.getString("ingamemessage");
			}
			else {
				props.setString("ingamemessage", pluginMessage);
			}
			
			if (props.containsKey("infvalue")) {
				infValue = props.getInt("infvalue");
			}
			else {
				props.setInt("infvalue", infValue);
			}
			
			if (props.containsKey("filelocation")) {
				LOC = props.getString("filelocation");
			}
			else {
				props.setString("filelocation", LOC);
			}
			
			if (props.containsKey("usestats")) {
				useStats = props.getBoolean("usestats");
			}
			else {
				props.setBoolean("usestats", useStats);
			}
			
			if (props.containsKey("autodeposittime")) {
				autoDepositTime = props.getLong("autodeposittime");
			}
			else {
				props.setLong("autodeposittime", autoDepositTime);
			}
			
			if (props.containsKey("autodepositamount")) {
				autoDepositAmount = props.getInt("autodepositamount");
			}
			else {
				props.setInt("autodepositamount", autoDepositAmount);
			}
			
			if (props.containsKey("blockbadwords")) {
				useStats = props.getBoolean("blockbadwords");
			}
			else {
				props.setBoolean("blockbadwords", blockBadWords);
			}
			
			if (props.containsKey("messageonbadword")) {
				useStats = props.getBoolean("messageonbadword");
			}
			else {
				props.setBoolean("messageonbadword", messageOnBadWord);
			}
			
			if (props.containsKey("maxbuyselltimeout")) {
				maxBuySellTime = props.getLong("maxbuyselltimeout");
			}
			else {
				props.setLong("maxbuyselltimeout", maxBuySellTime);
			}
		}
	}
	
	/**
	 * Loads the {@link Shop} data from file
	 */
	private static void readShopFile() {
		if (dirExists) {
			System.out.println("Reading Shops File...");
			
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_shop));
				while ((raw = reader.readLine()) != null) {
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						addShop(new Shop(raw));
					}
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					/*
					 * File not found, create empty file
					 */
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_shop));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads the {@link EconStats} from file
	 */
	private static void readStatsFile() {
		if (dirExists) {
			System.out.println("Reading Stats File...");
			
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_stats));
				ArrayList<String> data = new ArrayList<String>();
				while ((raw = reader.readLine()) != null) {
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						data.add(raw);
					}
				}
				EconStats.loadStats(data);
				reader.close();
			}
			catch (IOException e) {
				try {
					/*
					 * File not found, create empty file
					 */
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_privGroups));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Loads the {@link User} data from file.
	 */
	private static void readUserFile() {
		if (dirExists) {
			System.out.println("Reading Users File...");
			
			BufferedReader reader;
			String raw = "";
			
			try {
				reader = new BufferedReader(new FileReader(file_playerData));
				
				while ((raw = reader.readLine()) != null) {
					if (debug) {
						System.out.println("Raw user data read: " + raw);
					}
					if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
						if (!raw.split(":")[0].equalsIgnoreCase("")) {
							users.add(new User(raw));
						}
					}
				}
				reader.close();
			}
			catch (IOException e) {
				/*
				 * File not found, try to make the file
				 */
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(file_playerData));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					/*
					 * FFFFFFFFUUUUUUUUU
					 */
					e1.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Performs a full save of the economy. Writes all data to file
	 */
	public static void save() {
		System.out.println("Saving player and item data.");
		
		/*
		  * Perform save actions
		  */
		try {
			write("player");
			write("item");
			write("stats");
			write("shops");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return True to use stats, false to not
	 */
	public static boolean usingStats() {
		return useStats;
	}
	
	/**
	 * Searches the list of {@link ShopItem} for the given itemId. True if found.
	 * 
	 * @param itemId
	 * @return
	 */
	public static boolean validId(int itemId) {
		for (ShopItem iter : itemList) {
			if (iter.getItemId() == itemId) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Parent function for writing data to file. Supply the name of the data you want to write.
	 * 
	 * @param fileName
	 * @throws SQLException
	 */
	private static void write(String fileName) throws SQLException {
		if (fileName.equalsIgnoreCase("player")) {
			// TODO Player SQL Stuff
			if (useSQL) {
				Connection conn = DriverManager.getConnection(DB);
				Statement stat = conn.createStatement();
				// Drop old table
				stat.executeUpdate("drop table if exists players;");
				
				// Create new table
				stat.executeUpdate("create table players (Name, Money, NumTransBuy, NumTransSell, Spent, Gained, LastAutoPayment);");
				
				PreparedStatement prep = conn.prepareStatement("insert into players values (?, ?, ?, ?, ?, ?, ?);");
				for (User iter : users) {
					prep.setString(1, iter.getName());
					prep.setInt(2, iter.getMoney().getAmount());
					prep.setInt(3, iter.getNumTransactionsBuy());
					prep.setInt(4, iter.getNumTransactionsSell());
					prep.setInt(5, iter.getMoney().getSpent());
					prep.setInt(6, iter.getMoney().getGained());
					prep.setLong(7, iter.getLastAutoDeposit());
					prep.addBatch();
				}
				
				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.setAutoCommit(true);
				conn.close();
			}
			else if (dirExists) {
				// Write player file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_playerData));
					for (User iter : users) {
						writer.write(iter.getSaveString());
						writer.newLine();
					}
					writer.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (fileName.equalsIgnoreCase("item")) {
			// TODO Item SQL Stuff
			if (useSQL) {
				Connection conn = DriverManager.getConnection(DB);
				Statement stat = conn.createStatement();
				stat
						.executeUpdate("create table if not exists items (ItemId, BuyPrice, SellPrice, ShopMaxSell, PlayerMaxSell, PlayerMaxBuy, BreakValue);");
				conn.close();
			}
			else if (dirExists) {
				// Write item file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_itemlist));
					for (ShopItem iter : itemList) {
						writer.write(iter.getSaveString());
						writer.newLine();
					}
					writer.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (fileName.equalsIgnoreCase("stats")) {
			// TODO Stats SQL Stuff
			// if (useSQL) {
			// stat.executeUpdate("create table if not exists stats ();");
			// }
			
			if (dirExists) {
				// Write stats file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_stats));
					for (String iter : EconStats.getSaveString()) {
						writer.write(iter);
						writer.newLine();
					}
					writer.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		else if (fileName.equalsIgnoreCase("shops")) {
			if (useSQL) {
				Connection conn = DriverManager.getConnection(DB);
				Statement stat = conn.createStatement();
				// TODO Shops SQL Stuff
				
				// Drop existing tables, this is a total write
				stat.executeUpdate("drop table if exists shoplist;");
				
				// Create the new tables
				stat.executeUpdate("create table shoplist (Name, Money, InfItems, LastRestock, StockId);");
				
				PreparedStatement prep = conn.prepareStatement("insert into shoplist values (?, ?, ?, ?, ?);"); // Shops entry
				int stockId = 0; // The Id for the shopstocks entry
				for (Shop iter : shops) {
					stockId++;
					prep.setString(1, iter.getName());
					prep.setInt(2, iter.getMoney().getAmount());
					prep.setBoolean(3, iter.getInfItems());
					prep.setLong(4, iter.getLastRestock());
					prep.setInt(5, stockId);
					prep.addBatch();
					
					stat.executeUpdate("drop table if exists " + iter.getName() + ";");
					stat.executeUpdate("create table " + iter.getName() + " (ItemId, Amount);");
					PreparedStatement prep2 = conn.prepareStatement("insert into " + iter.getName() + " values (?, ?);");
					for (ShopItemStack stack : iter.getAvailItems()) {
						prep2.setInt(1, stack.getItemId());
						prep2.setInt(2, stack.getAmountAvail());
						prep2.addBatch();
					}
					
					conn.setAutoCommit(false);
					prep2.executeBatch();
					conn.setAutoCommit(true);
				}
				
				// Commit the data
				conn.setAutoCommit(false);
				prep.executeBatch();
				conn.setAutoCommit(true);
				conn.close();
			}
			else if (dirExists) {
				// Write shops file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_shop));
					for (Shop iter : shops) {
						
						writer.write(iter.getSaveString());
						writer.newLine();
					}
					writer.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
	}
	
	/**
	 * @return amount of time for a transaction to timeout. Used in checkMaxBuy/Sell
	 */
	public static long getMaxBuySellTime() {
		return maxBuySellTime;
	}
}
