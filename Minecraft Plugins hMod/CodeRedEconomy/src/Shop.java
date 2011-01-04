import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Shop extends EconEntity {
	private static ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	private boolean							infAmount		= true;
	private DataManager						data			= null;
	protected static final Logger			log				= Logger.getLogger("Minecraft");
	
	public Shop() {
		initialize();
	}
	
	public Shop(DataManager dataman) {
		// TODO Auto-generated constructor stub
		this.data = dataman;
		initialize();
	}
	
	private void initialize() {
		for (ShopItem iter : DataManager.getItemList()) {
			if (infAmount) {
				availableItems.add(new ShopItemStack(iter, -1337));
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
			if (CodeRedEconomy.debug) {
				log.log(Level.INFO, "Item name is: " + itemName);
			}
			for (ShopItemStack iter : availableItems) {
				if (iter.getShopItem().getName().equalsIgnoreCase(itemName)
						&& (iter.getAmountAvail() >= amount || iter.getAmountAvail() == -1337)) {
					// This is a valid name, check if player can buy
					ShopItemStack inCart = new ShopItemStack(iter.getShopItem(), amount);
					if (user.canBuy(inCart)) {
						// Buy it
						user.getPlayer().sendMessage(DataManager.getPluginMessage() + "Here are your items. Please come again.");
						
						user.pay(this, inCart);
						user.sendMessage(DataManager.getPluginMessage() + "Your new balance is: " + user.getMoney().getAmount() + " "
								+ Money.getMoneyName());
					}
					else {
						// Can't buy
						if (user.getMoney().getAmount() < inCart.getTotalPrice()) {
							user.getPlayer().sendMessage(DataManager.getPluginMessage() + "You do not have enough money.");
						}
						else if (amount > iter.getAmountAvail()) {
							user.sendMessage(DataManager.getPluginMessage() + "There are too few items.");
						}
					}
				}
				else {
					
				}
			}
		}
	}
}
