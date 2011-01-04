import java.util.ArrayList;

public class Shop extends EconEntity {
	private static ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	private boolean							infAmount		= true;
	private DataManager						data			= null;
	
	public Shop() {
		initialize();
	}
	
	public Shop(DataManager dataman) {
		// TODO Auto-generated constructor stub
		this.data = dataman;
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
	
	public void buy(User user, String[] split) {
		// Check players balance, find what they want to buy, check priv level, buy
		String itemName = "";
		if (split.length >= 3) {
			int amount = Integer.valueOf(split[1]);
			for (int i = 2; i < split.length; i++) {
				// Check if it is a number
				itemName += split[i] + " ";
			}
			itemName = itemName.trim();
			for (ShopItemStack iter : availableItems) {
				if (iter.getShopItem().getName().equalsIgnoreCase(itemName)) {
					// This is a valid name, check if player can buy
					ShopItemStack inCart = new ShopItemStack(iter.getShopItem(), amount);
					if (user.canBuy(inCart)) {
						// Buy it
						user.pay(this, inCart);
					}
				}
				else {
					
				}
			}
		}
	}
}
