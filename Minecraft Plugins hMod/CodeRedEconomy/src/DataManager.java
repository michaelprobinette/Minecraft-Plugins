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
	// General
	private static final String			LOC				= "Econ/";
	
	// Items
	private static File					file_privlist	= new File(LOC + "privItems.txt");
	private static ArrayList<ShopItem>	itemList		= new ArrayList<ShopItem>();
	
	// Player data
	
	public DataManager() {
		readPrivFile();
	}
	
	private void readPrivFile() {
		BufferedReader reader;
		String raw = "";
		try {
			reader = new BufferedReader(new FileReader(file_privlist));
			
			while ((raw = reader.readLine()) != null) {
				String split[] = raw.split(" ");
				if (split.length >= 1) {
					int id = Integer.valueOf(split[0]);
					if (split.length >= 2) {
						int priv = Integer.valueOf(split[1]);
						itemList.add(new ShopItem(id, priv));
					}
					else {
						itemList.add(new ShopItem(id, 0));
					}
					
				}
			}
			
			reader.close();
		}
		catch (FileNotFoundException e) {
			try {
				// File not found, create empty file
				BufferedWriter writer = new BufferedWriter(new FileWriter(file_privlist));
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
	
	public ArrayList<ShopItem> getItemList() {
		return itemList;
	}
}
