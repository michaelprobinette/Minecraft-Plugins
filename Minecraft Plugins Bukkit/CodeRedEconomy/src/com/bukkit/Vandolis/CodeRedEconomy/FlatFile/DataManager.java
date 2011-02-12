/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import com.bukkit.Vandolis.CodeRedEconomy.CodeRedEconomy;
import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * Used to load all of the data needed to run the CodeRedEconomy.
 * 
 * @author Vandolis
 */
public class DataManager {
	/*
	 * General Stuff
	 */
	private static CodeRedEconomy			plugin			= null;
	
	/*
	 * Regex stuff
	 */
	private static final String				PLAYER_REGEX	= ":";
	private static final String				PLAYER2_REGEX	= " ";
	private static final String				SHOP_REGEX		= ":";
	private static final String				SHOP2_REGEX		= " ";
	private static final String				STATS_REGEX		= ":";
	private static final String				STATS2_REGEX	= " ";
	private static final String				ITEM_REGEX		= ":";
	
	/*
	 * Shop stuff
	 * Shops Holds all of the shops, might use to make different shops based on EconomyProperties.getDIR().getPath()ation or something, might use in the future. Or right now.
	*/
	static ArrayList<Shop>					shops			= new ArrayList<Shop>();
	private static final File				file_shop		= new File(EconomyProperties.getDIR().getPath() + "/shops.txt");
	
	/*
	 * Privilege stuff
	 */
	private static final File				file_privGroups	= new File(EconomyProperties.getDIR().getPath() + "/shopGroups.txt");
	private static ArrayList<ShopGroup>		privGroups		= new ArrayList<ShopGroup>();
	
	/*
	 * Items stuff
	 */
	private static final File				file_itemlist	= new File(EconomyProperties.getDIR().getPath() + "/items.txt");
	private static ArrayList<ShopItem>		itemList		= new ArrayList<ShopItem>();
	
	/*
	 * Player data... stuff
	 */
	private static final File				file_playerData	= new File(EconomyProperties.getDIR().getPath() + "/users.txt");
	private static ArrayList<User>			users			= new ArrayList<User>();
	
	/*
	 * Stats stuff
	 */
	private static boolean					useStats		= true;
	private static final File				file_stats		= new File(EconomyProperties.getDIR().getPath() + "/stats.txt");
	
	/*
	 * Bad word stuff
	 */
	private static HashMap<String, Integer>	badWords		= new HashMap<String, Integer>();
	private static final File				file_badWords	= new File(EconomyProperties.getDIR().getPath() + "/badWords.txt");
	
	public DataManager(CodeRedEconomy instance) {
		load(instance);
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
			if (EconomyProperties.isDebug()) {
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
				// TODO Auto-generated catch bEconomyProperties.getDIR().getPath()k
				e.printStackTrace();
			}
		}
		else {
			if (EconomyProperties.isDebug()) {
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
		user.autoDesposit(EconomyProperties.getTime());
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
			// TODO Auto-generated catch bEconomyProperties.getDIR().getPath()k
			e.printStackTrace();
		}
	}
	
	/**
	 * @return A boolean of whether to check for bad words or not.
	 */
	public static boolean blockBadWords() {
		return EconomyProperties.isBlockBadWords();
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
	 * @return The EconomyProperties.isDebug() setting. True if enabled.
	 */
	public static boolean getDebug() {
		return EconomyProperties.isDebug();
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
	 * @return the CodeRedEconomy
	 */
	protected static CodeRedEconomy getPlugin() {
		return plugin;
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
	 * @return the current server
	 */
	public static Server getServer() {
		return plugin.getServer();
	}
	
	/**
	 * Searches the list of shops for the given {@link EconEntity}. If not found, adds a new {@link Shop} to the list and returns it.
	 * 
	 * @param ent
	 *            of the shop
	 * @return the found shop
	 */
	public static Shop getShop(EconEntity ent) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(ent.getName())) {
				if (EconomyProperties.isDebug()) {
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
	 *            of shop
	 * @return the shop
	 */
	public static Shop getShop(String name) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(name)) {
				if (EconomyProperties.isDebug()) {
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
	 *            of the user
	 * @return the user
	 */
	public static User getUser(Player player) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(player.getName())) {
				iter.setPlayer(player);
				iter.autoDesposit(EconomyProperties.getTime());
				return iter;
			}
		}
		
		/*
		 * User not found, make a new user and tie the player to it.
		 * Add the new user to the user list and write to file
		 */
		User temp = new User(player);
		temp.autoDesposit(EconomyProperties.getTime());
		addUser(temp);
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch bEconomyProperties.getDIR().getPath()k
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
	 *            of the user
	 * @return the user
	 */
	public static User getUser(String name) {
		if (name.contains(":")) {
			name = name.replace(":", "");
		}
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(name)) {
				if (EconomyProperties.isDebug()) {
					System.out.println("Was looking for: " + name + " and found " + iter.getName());
				}
				iter.autoDesposit(EconomyProperties.getTime());
				return iter;
			}
		}
		
		/*
		 * User not found, make a new one and add it to the list.
		 * Write to file.
		 */
		User temp = new User(name);
		temp.autoDesposit(EconomyProperties.getTime());
		addUser(temp);
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch bEconomyProperties.getDIR().getPath()k
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
	 * @throws SQLException
	 */
	public static void load(CodeRedEconomy instance) {
		System.out.println("Loading CodeRedEconomy...");
		
		plugin = instance;
		
		/*Change the folder EconomyProperties.getDIR().getPath()ation to the given one*/
		//		EconomyProperties.getDIR().getPath() = instance.getDataFolder().getPath();
		
		try {
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
			
			if (EconomyProperties.isBlockBadWords()) {
				/*Read bad words file*/
				readBadWords();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Loads the bad words from file
	 * 
	 * @throws SQLException
	 */
	private static void readBadWords() throws SQLException {
		
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
	
	/**
	 * Loads the item data from file.
	 * Item file formatting is "itemID:buyPrice:sellPrice:maxAvail"
	 * 
	 * @throws SQLException
	 */
	private static void readItemFile() throws SQLException {
		BufferedReader reader;
		String raw = "";
		itemList = new ArrayList<ShopItem>();
		
		try {
			reader = new BufferedReader(new FileReader(file_itemlist));
			
			while ((raw = reader.readLine()) != null) {
				if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
					ShopItem temp = new ShopItem(raw);
					itemList.add(temp);
					if (EconomyProperties.isDebug()) {
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
	
	/**
	 * Loads the {@link ShopGroup} data from file.
	 */
	private static void readPrivFile() {
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
	
	/**
	 * Loads the {@link Shop} data from file
	 * 
	 * @throws SQLException
	 */
	private static void readShopFile() throws SQLException {
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
	
	/**
	 * Loads the {@link EconStats} from file
	 */
	private static void readStatsFile() {
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
	
	/**
	 * Loads the {@link User} data from file.
	 * 
	 * @throws SQLException
	 */
	private static void readUserFile() throws SQLException {
		System.out.println("Reading Users File...");
		
		BufferedReader reader;
		String raw = "";
		
		try {
			reader = new BufferedReader(new FileReader(file_playerData));
			
			while ((raw = reader.readLine()) != null) {
				if (EconomyProperties.isDebug()) {
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
				e1.printStackTrace();
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
	 * Parent function for writing data to file. Supply the name of the data you want to write.
	 * 
	 * @param fileName
	 * @throws SQLException
	 */
	private static void write(String fileName) throws SQLException {
		if (fileName.equalsIgnoreCase("player")) {
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
		else if (fileName.equalsIgnoreCase("item")) {
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
		else if (fileName.equalsIgnoreCase("stats")) {
			
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
		else if (fileName.equalsIgnoreCase("shops")) {
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
	
	/**
	 * 
	 */
	public static void destroy() {
		badWords = new HashMap<String, Integer>();
		itemList = new ArrayList<ShopItem>();
		privGroups = new ArrayList<ShopGroup>();
		shops = new ArrayList<Shop>();
		users = new ArrayList<User>();
	}
}
