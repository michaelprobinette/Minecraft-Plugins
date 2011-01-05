import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

/**
 * @author Vandolis Used to load all of the data needed to run the plugin.
 */
public class DataManager {
	// General stuff
	private static final String			LOC				= "Econ/";										// Location of all of the files
	private static PropertiesFile		props			= new PropertiesFile(LOC + "data.properties");	// Properties file
	private static boolean				debug			= false;
	private static final String			pluginMessage	= "[§cCodeRedEcon§f] ";
	private static final int			infValue		= -1;
	
	// Money stuff
	private static String				moneyName		= "";
	
	// Shop stuff
	// Holds all of the shops, might use to make different shops based on location or something, might use in the future. Or right now.
	ArrayList<Shop>						shops			= new ArrayList<Shop>();
	
	// Privilege stuff
	private static File					file_privGroups	= new File(LOC + "privGroups.txt");
	private static ArrayList<ShopGroup>	privGroups		= new ArrayList<ShopGroup>();
	
	// Items stuff
	private static File					file_itemlist	= new File(LOC + "items.txt");
	private static ArrayList<ShopItem>	itemList		= new ArrayList<ShopItem>();
	
	// Player data... stuff
	private static File					file_playerData	= new File(LOC + "playerData.txt");
	private static ArrayList<User>		users			= new ArrayList<User>();
	
	public DataManager() {
		load();
	}
	
	private void readProps() {
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
	}
	
	public static User getUser(Player player) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(player.getName())) {
				iter.setPlayer(player);
				return iter;
			}
		}
		User temp = new User(player); // Not found, make a new user
		addUser(temp); // Add user to the users list
		write("player");
		return temp;
	}
	
	public static User getUser(String name) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(name)) {
				return iter;
			}
		}
		User temp = new User(name); // Not found, make a new user
		addUser(temp); // Add user to the users list
		return temp;
	}
	
	public static ArrayList<User> getUsers() {
		return users;
	}
	
	public static User getUser(EconEntity ent) {
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(ent.getName())) {
				return iter;
			}
		}
		User temp = new User(ent.getName()); // Not found, make a new user
		addUser(temp); // Add user to the users list
		return temp;
	}
	
	private void readUserFile() {
		BufferedReader reader;
		String raw = "";
		
		try {
			reader = new BufferedReader(new FileReader(file_playerData));
			
			while ((raw = reader.readLine()) != null) {
				if (CodeRedEconomy.debug) {
					System.out.println("Raw user data read: " + raw);
				}
				if (!raw.split(":")[0].equalsIgnoreCase("")) {
					users.add(new User(raw));
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
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
		catch (IOException e) {
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Item file formatting is "itemID:buyPrice:sellPrice:maxAvail"
	 */
	private void readItemFile() {
		BufferedReader reader;
		String raw = "";
		try {
			reader = new BufferedReader(new FileReader(file_itemlist));
			
			while ((raw = reader.readLine()) != null) {
				
				String split[] = raw.split(":");
				ShopItem temp = new ShopItem(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]), Integer
						.valueOf(split[3]));
				itemList.add(temp);
				if (CodeRedEconomy.debug) {
					System.out.println("Raw item data read: " + raw);
					System.out.println("Item data: " + temp.getName() + " price: " + temp.getBuyPrice());
				}
			}
			reader.close();
		}
		catch (FileNotFoundException e) {
			try {
				// File not found, create empty file
				BufferedWriter writer = new BufferedWriter(new FileWriter(file_itemlist));
				writer.newLine();
				writer.close();
			}
			catch (IOException e1) {
				e1.printStackTrace();
			}
			// e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ShopItem> getItemList() {
		return itemList;
	}
	
	public static ArrayList<ShopGroup> getGroups() {
		return privGroups;
	}
	
	public static String getPluginMessage() {
		return pluginMessage;
	}
	
	public static void addUser(User user) {
		// Check if user is already in there
		boolean found = false;
		for (User iter : users) {
			if (iter.getName().equalsIgnoreCase(user.getName())) {
				found = true;
			}
		}
		if (!found) {
			users.add(user);
		}
		
		// Write to file
		write("player");
	}
	
	private static void write(String fileName) {
		if (fileName.equalsIgnoreCase("player")) {
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
		else if (fileName.equalsIgnoreCase("item")) {
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
	
	private void readPrivFile() {
		try {
			BufferedReader read = new BufferedReader(new FileReader(file_privGroups));
			String raw = "";
			try {
				while ((raw = read.readLine()) != null) {
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
			catch (IOException e) {
				e.printStackTrace();
			}
			try {
				read.close();
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		catch (FileNotFoundException e) {
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
	
	public static int getBuyPrice(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getBuyPrice();
			}
		}
		return 0;
	}
	
	public static int getSellPrice(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getSellPrice();
			}
		}
		return 0;
	}
	
	public static void save() {
		// Perform save actions
		write("player");
		write("item");
	}
	
	public void load() {
		// Read data from properties file
		readProps();
		
		// Read from the items file
		readItemFile();
		
		// Read from the users file
		readUserFile();
		
		// Read from the group priv file
		readPrivFile();
	}
	
	public static String getMoneyName() {
		return moneyName;
	}
	
	public static ShopGroup getGroup(String groupName) {
		for (ShopGroup iter : privGroups) {
			if (iter.getGroupName().equalsIgnoreCase(groupName)) {
				return iter;
			}
		}
		return null;
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
	
	public boolean getDebug() {
		return debug;
	}
	
	public static int getMaxAvail(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getMaxAvail();
			}
		}
		return 0;
	}
	
	public static ShopItem getItem(String itemName) {
		for (ShopItem iter : itemList) {
			if (iter.getName().equalsIgnoreCase(itemName)) {
				return iter;
			}
		}
		return null;
	}
	
	public static int getInfValue() {
		return infValue;
	}
}
