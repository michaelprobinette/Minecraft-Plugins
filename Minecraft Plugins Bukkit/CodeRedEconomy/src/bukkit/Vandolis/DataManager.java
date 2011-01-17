/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

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
 * @author Vandolis Used to load all of the data needed to run the plugin.
 */
public class DataManager {
	// General stuff
	private static String					LOC					= "Econ/";
	private static boolean					dirExists			= new File(LOC).mkdir();
	private static PropertiesFile			props				= new PropertiesFile(LOC + "data.properties");	// Properties file
	private static boolean					debug				= false;
	private static String					pluginMessage		= "[§cCodeRedEcon§f] ";
	private static int						infValue			= -1;
	private static Server					server				= null;
	
	// SQLite
	private static boolean					useSQL				= false;
	private static final String				DB					= "jdbc:sqlite:CodeRedEconomy";
	
	// Regex stuff
	private static final String				PLAYER_REGEX		= ":";
	private static final String				PLAYER2_REGEX		= " ";
	private static final String				SHOP_REGEX			= ":";
	private static final String				SHOP2_REGEX			= " ";
	private static final String				STATS_REGEX			= ":";
	private static final String				STATS2_REGEX		= " ";
	private static final String				ITEM_REGEX			= ":";
	
	// Money stuff
	private static String					moneyName			= "Strypes";
	
	// Shop stuff
	// Holds all of the shops, might use to make different shops based on location or something, might use in the future. Or right now.
	static ArrayList<Shop>					shops				= new ArrayList<Shop>();
	private static long						restockTime			= 60000;
	private static final File				file_shop			= new File(LOC + "shops.txt");
	
	// Privilege stuff
	private static final File				file_privGroups		= new File(LOC + "privGroups.txt");
	private static ArrayList<ShopGroup>		privGroups			= new ArrayList<ShopGroup>();
	
	// Items stuff
	private static final File				file_itemlist		= new File(LOC + "items.txt");
	private static ArrayList<ShopItem>		itemList			= new ArrayList<ShopItem>();
	
	// Player data... stuff
	private static final File				file_playerData		= new File(LOC + "playerData.txt");
	private static ArrayList<User>			users				= new ArrayList<User>();
	private static long						autoDepositTime		= 60000;
	private static int						autoDepositAmount	= 50;
	
	// Stats stuff
	private static boolean					useStats			= true;
	private static final File				file_stats			= new File(LOC + "stats.txt");
	
	// Bad word
	private static HashMap<String, Integer>	badWords			= new HashMap<String, Integer>();
	private static boolean					blockBadWords		= false;
	private static final File				file_badWords		= new File(LOC + "badWords.txt");
	private static final boolean			messageOnBadWord	= true;
	
	public static void addShop(Shop shop) {
		if (debug) {
			System.out.println("Adding shop under the name of: " + shop.getName());
		}
		shops.add(shop);
		try {
			write("shops");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
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
	
	public static boolean allowedBlock(String group, int itemID) {
		for (ShopGroup iter : privGroups) {
			System.out.println("Checking " + iter.getGroupName() + " against " + group);
			if (iter.getGroupName().equalsIgnoreCase(group)) {
				for (int biter : iter.getAllowed()) {
					if (biter == itemID) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public static boolean blockBadWords() {
		return blockBadWords;
	}
	
	public static int getAutoDepositAmount() {
		return autoDepositAmount;
	}
	
	public static long getAutoDepositTime() {
		return autoDepositTime;
	}
	
	public static String getBadWord(String message) {
		for (String iter : badWords.keySet()) {
			if (message.contains(iter)) {
				return iter;
			}
		}
		return "";
	}
	
	public static HashMap<String, Integer> getBadWords() {
		return badWords;
	}
	
	public static int getBuyPrice(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getBuyPrice();
			}
		}
		return 0;
	}
	
	public static boolean getDebug() {
		return debug;
	}
	
	public static ShopGroup getGroup(String groupName) {
		for (ShopGroup iter : privGroups) {
			if (iter.getGroupName().equalsIgnoreCase(groupName)) {
				return iter;
			}
		}
		return null;
	}
	
	public static ArrayList<ShopGroup> getGroups() {
		return privGroups;
	}
	
	public static int getInfValue() {
		return infValue;
	}
	
	public static ShopItem getItem(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter;
			}
		}
		return null;
	}
	
	public static ShopItem getItem(String itemName) {
		for (ShopItem iter : itemList) {
			if (iter.getName().equalsIgnoreCase(itemName)) {
				return iter;
			}
		}
		return null;
	}
	
	public static ArrayList<ShopItem> getItemList() {
		return itemList;
	}
	
	public static String getItemRegex() {
		return ITEM_REGEX;
	}
	
	public static int getMaxAvail(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getMaxAvail();
			}
		}
		return 0;
	}
	
	public static String getMoneyName() {
		return moneyName;
	}
	
	public static String getPlayer2Regex() {
		return PLAYER2_REGEX;
	}
	
	public static String getPlayerRegex() {
		return PLAYER_REGEX;
	}
	
	public static String getPluginMessage() {
		return pluginMessage;
	}
	
	public static String getReqGroup(int itemID) {
		for (ShopGroup iter : privGroups) {
			for (int biter : iter.getAllowed()) {
				if (biter == itemID) {
					return iter.getGroupName();
				}
			}
		}
		return "default";
	}
	
	public static long getRestockTime() {
		return restockTime;
	}
	
	public static int getSellPrice(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getSellPrice();
			}
		}
		return 0;
	}
	
	public static Server getServer() {
		return server;
	}
	
	public static Shop getShop(EconEntity ent) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(ent.getName())) {
				return iter;
			}
		}
		
		// No shop found, make a new one with default values
		Shop temp = new Shop(ent.getName(), false, -1);
		addShop(temp);
		return temp;
	}
	
	public static Shop getShop(String name) {
		for (Shop iter : shops) {
			if (iter.getName().equalsIgnoreCase(name)) {
				if (debug) {
					System.out.println("Shop found under the name of " + iter.getName() + " returning.");
				}
				return iter;
			}
		}
		
		// No shop found, make a new one with default values
		Shop temp = new Shop(name, false, -1);
		addShop(temp);
		return temp;
	}
	
	public static String getShop2Regex() {
		return SHOP2_REGEX;
	}
	
	public static String getShopRegex() {
		return SHOP_REGEX;
	}
	
	public static ArrayList<Shop> getShops() {
		return shops;
	}
	
	public static String getStats2Regex() {
		return STATS2_REGEX;
	}
	
	public static String getStatsRegex() {
		return STATS_REGEX;
	}
	
	public static User getUser(EconEntity ent) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(ent.getName())) {
				iter.autoDesposit(server.getTime());
				return iter;
			}
		}
		User temp = new User(ent.getName()); // Not found, make a new user
		temp.autoDesposit(server.getTime()); // Starting money
		addUser(temp); // Add user to the users list
		return temp;
	}
	
	public static User getUser(Player player) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(player.getName())) {
				iter.setPlayer(player);
				iter.autoDesposit(server.getTime());
				return iter;
			}
		}
		User temp = new User(player); // Not found, make a new user
		temp.autoDesposit(server.getTime()); // Starting money
		addUser(temp); // Add user to the users list
		try {
			write("player");
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return temp;
	}
	
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
		User temp = new User(name); // Not found, make a new user
		temp.autoDesposit(server.getTime()); // Starting money
		addUser(temp); // Add user to the users list
		return temp;
	}
	
	public static ArrayList<User> getUsers() {
		return users;
	}
	
	public static void load(CodeRedEconomy instance) {
		server = instance.getServer();
		// Read data from properties file
		readProps();
		
		// Read from the items file
		readItemFile();
		
		// Read from the users file
		readUserFile();
		
		// Read from the group priv file
		readPrivFile();
		
		// Read from the stats file
		readStatsFile();
		
		// Read from the shop file
		readShopFile();
		
		// Read bad words file
		readBadWords();
	}
	
	public static boolean messageOnBadWord() {
		return messageOnBadWord;
	}
	
	private static void readBadWords() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_badWords));
				while ((raw = reader.readLine()) != null) {
					String split[] = raw.split("=");
					String word = split[0].trim();
					int cost = Integer.valueOf(split[1].trim());
					badWords.put(word, cost);
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					// File not found, create empty file
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
	 * Item file formatting is "itemID:buyPrice:sellPrice:maxAvail"
	 */
	private static void readItemFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_itemlist));
				
				while ((raw = reader.readLine()) != null) {
					ShopItem temp = new ShopItem(raw);
					itemList.add(temp);
					if (debug) {
						System.out.println("Raw item data read: " + raw);
						System.out.println("Item data: " + temp.toString());
					}
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					// File not found, create empty file
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
	
	private static void readPrivFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_privGroups));
				while ((raw = reader.readLine()) != null) {
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
				reader.close();
			}
			catch (IOException e) {
				try {
					// File not found, create empty file
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
	
	private static void readProps() {
		if (dirExists) {
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
		}
	}
	
	private static void readShopFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_shop));
				while ((raw = reader.readLine()) != null) {
					addShop(new Shop(raw));
				}
				reader.close();
			}
			catch (IOException e) {
				try {
					// File not found, create empty file
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
	
	private static void readStatsFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			try {
				reader = new BufferedReader(new FileReader(file_stats));
				ArrayList<String> data = new ArrayList<String>();
				while ((raw = reader.readLine()) != null) {
					data.add(raw);
				}
				EconStats.loadStats(data);
				reader.close();
			}
			catch (IOException e) {
				try {
					// File not found, create empty file
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
	
	private static void readUserFile() {
		if (dirExists) {
			BufferedReader reader;
			String raw = "";
			
			try {
				reader = new BufferedReader(new FileReader(file_playerData));
				
				while ((raw = reader.readLine()) != null) {
					if (debug) {
						System.out.println("Raw user data read: " + raw);
					}
					if (!raw.split(":")[0].equalsIgnoreCase("")) {
						users.add(new User(raw));
					}
				}
				reader.close();
			}
			catch (IOException e) {
				// File not found, try to make the file
				BufferedWriter writer;
				try {
					writer = new BufferedWriter(new FileWriter(file_playerData));
					writer.newLine();
					writer.close();
				}
				catch (IOException e1) {
					// FFFFFFFFUUUUUUUUU
					e1.printStackTrace();
				}
			}
		}
	}
	
	public static void save() {
		System.out.println("Saving player and item data.");
		// Perform save actions
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
	
	public static boolean usingStats() {
		return useStats;
	}
	
	public static boolean validID(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return true;
			}
		}
		return false;
	}
	
	private static void write(String fileName) throws SQLException {
		Connection conn = DriverManager.getConnection(DB);
		Statement stat = conn.createStatement();
		
		if (fileName.equalsIgnoreCase("player")) {
			// TODO Player SQL Stuff
			if (useSQL) {
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
			}
			else if (dirExists) {
				// Write player file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_playerData));
					for (User iter : users) {
						writer.write(iter.toString());
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
				stat.executeUpdate("create table if not exists items (ItemId, BuyPrice, SellPrice, ShopMaxSell, PlayerMaxSell, PlayerMaxBuy, BreakValue);");
			}
			else if (dirExists) {
				// Write item file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_itemlist));
					for (ShopItem iter : itemList) {
						writer.write(iter.toString());
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
					for (String iter : EconStats.statString()) {
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
						prep2.setInt(1, stack.getItemID());
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
			}
			else if (dirExists) {
				// Write shops file
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter(file_shop));
					for (Shop iter : shops) {
						
						writer.write(iter.toString());
						writer.newLine();
					}
					writer.close();
				}
				catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		conn.close();
	}
}
