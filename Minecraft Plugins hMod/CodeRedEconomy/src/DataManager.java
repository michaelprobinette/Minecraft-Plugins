import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Vandolis Used to load all of the data needed to run the plugin.
 */
public class DataManager {
	// General
	protected static final Logger		log				= Logger.getLogger("Minecraft");
	private static final String			LOC				= "Econ/";
	private static PropertiesFile		props			= new PropertiesFile(LOC + "data.properties");
	private static String				moneyName		= "Strypes";
	private static final String			pluginMessage	= "[�cCodeRedEconomy�f] ";
	
	// Items
	private static File					file_itemlist	= new File(LOC + "items.txt");
	private static ArrayList<ShopItem>	itemList		= new ArrayList<ShopItem>();
	
	// Player data
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
	
	private void readUserFile() {
		BufferedReader reader;
		String raw = "";
		
		try {
			reader = new BufferedReader(new FileReader(file_playerData));
			
			while ((raw = reader.readLine()) != null) {
				if (CodeRedEconomy.debug) {
					log.log(Level.INFO, "Raw user data read: " + raw);
				}
				users.add(new User(raw));
			}
		}
		catch (FileNotFoundException e) {
			BufferedWriter writer;
			try {
				writer = new BufferedWriter(new FileWriter(file_playerData));
				writer.newLine();
				writer.close();
			}
			catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			// e.printStackTrace();
		}
		catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readItemFile() {
		BufferedReader reader;
		String raw = "";
		try {
			reader = new BufferedReader(new FileReader(file_itemlist));
			
			while ((raw = reader.readLine()) != null) {
				
				String split[] = raw.split(":");
				ShopItem temp = new ShopItem(Integer.valueOf(split[0]), Integer.valueOf(split[1]), Integer.valueOf(split[2]));
				itemList.add(temp);
				if (CodeRedEconomy.debug) {
					log.log(Level.INFO, "Raw item data read: " + raw);
					log.log(Level.INFO, "Item data: " + temp.getName() + " price: " + temp.getPrice() + " privLevel: "
							+ temp.getPrivLevel());
				}
				
				// String split[] = raw.split(" ");
				// if (split.length >= 1) {
				// if (split[0].length() >= 1) {
				// if (split[0].charAt(0) != '#') {
				// // Get the itemID
				// int id = Integer.valueOf(split[0]);
				// if (split.length >= 2) {
				// // Get the price
				// int price = Integer.valueOf(split[1]);
				// if (split.length >= 3) {
				// // Get the priv level
				// int priv = Integer.valueOf(split[2]);
				// itemList.add(new ShopItem(id, price, priv));
				// }
				// else {
				// itemList.add(new ShopItem(id, price, 0));
				// }
				// }
				// else {
				// itemList.add(new ShopItem(id, 0, 0));
				// }
				// }
				// else {
				// // Comment, ignore
				// }
				// }
				// }
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
			e.printStackTrace();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static ArrayList<ShopItem> getItemList() {
		return itemList;
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
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		else if (fileName.equalsIgnoreCase("item")) {
			// Write item file
		}
	}
	
	public static int getPrice(int itemID) {
		for (ShopItem iter : itemList) {
			if (iter.getItemID() == itemID) {
				return iter.getPrice();
			}
		}
		return 0;
	}
	
	public void save() {
		// Perform save actions
		write("player");
		write("item");
	}
	
	public void load() {
		readProps();
		readItemFile();
		readUserFile();
	}
	
	public static String getMoneyName() {
		return moneyName;
	}
}
