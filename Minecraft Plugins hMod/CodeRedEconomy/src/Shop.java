import java.util.ArrayList;

public class Shop {
	private static ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	private boolean							infAmount		= true;
	
	public Shop() {
		initialize();
	}
	
	private void initialize() {
		for (int i = 1; i <= 91; i++) {
			if (infAmount) {
				
			}
			else {
				// Add with a limited amount, read from a file
				// Maybe have certain blocks sell out, and slowly restock
			}
		}
	}
	
	public static boolean isAvailable(int itemID) {
		
		return false;
	}
}
