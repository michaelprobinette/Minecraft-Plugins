import java.util.logging.Level;
import java.util.logging.Logger;

public class Shop extends EconEntity {
	private boolean					infAmount	= false;
	private int						stdAmount	= 64;
	protected static final Logger	log			= Logger.getLogger("Minecraft");
	
	public Shop() {
		initialize();
	}
	
	private void initialize() {
		for (ShopItem iter : DataManager.getItemList()) {
			if (infAmount) {
				availableItems.add(new ShopItemStack(iter, -1337));
			}
			else {
				availableItems.add(new ShopItemStack(iter, stdAmount));
			}
		}
		money.setAmount(-1337);
	}
	
	public void sell(User user, String[] split) {
		String itemName = "";
		if (split.length >= 3) {
			int amount = Integer.valueOf(split[1]);
			for (int i = 2; i < split.length; i++) {
				itemName += split[i] + " ";
			}
			itemName = itemName.trim();
			if (CodeRedEconomy.debug) {
				log.log(Level.INFO, "Item name is: " + itemName);
			}
			// Update the users availableItems array
			user.updateArray();
			for (ShopItemStack iter : user.availableItems) {
				if (iter.getShopItem().getName().equalsIgnoreCase(itemName)) {
					// Item found, check amount
					if (amount <= iter.getAmountAvail()) {
						// Enough to sell
						user.sendMessage("Selling " + amount + " " + itemName);
						Transaction.transaction(new Transaction(user, this, new ShopItemStack(new ShopItem(iter.getItemID()), amount)));
						user.sendMessage("Your new balance is: " + user.getMoney().toString());
						// user.sell(new Transaction(user, this, new ShopItemStack(new ShopItem(iter.getItemID()), amount)));
					}
					else {
						// Too few items
					}
				}
			}
		}
	}
	
	public void buy(User user, String[] split) {
		// Check players balance, find what they want to buy, check priv level, buy
		String itemName = "";
		if (split.length >= 3) {
			int amount = Integer.valueOf(split[1]);
			for (int i = 2; i < split.length; i++) {
				itemName += split[i] + " ";
			}
			itemName = itemName.trim();
			if (CodeRedEconomy.debug) {
				log.log(Level.INFO, "Item name is: " + itemName);
			}
			for (ShopItemStack iter : availableItems) {
				if (iter.getShopItem().getName().equalsIgnoreCase(itemName)) {
					if (iter.getAmountAvail() >= amount || iter.getAmountAvail() == -1337) {
						// This is a valid name, check if player can buy
						ShopItemStack inCart = new ShopItemStack(iter.getShopItem(), amount);
						if (user.canBuy(inCart)) {
							// Buy it
							user.sendMessage("Here are your items. Please come again.");
							
							System.out.println("Buying " + inCart.getShopItem().getName() + " amount: " + inCart.getAmountAvail());
							Transaction.transaction(new Transaction(this, user, inCart));
							// user.buy(new Transaction(this, user, inCart));
							// user.pay(this, inCart);
							user.sendMessage("Your new balance is: " + user.getMoney().getAmount() + " " + Money.getMoneyName());
							break;
						}
						else {
							// Can't buy
							if (user.getMoney().getAmount() < inCart.getTotalBuyPrice()) {
								user.sendMessage("You do not have enough money.");
							}
							else if (amount > iter.getAmountAvail() && iter.getAmountAvail() != -1337) {
								user.sendMessage("There are too few items.");
							}
							else if (!user.canBuy(inCart)) {
								user.sendMessage("You are not allowed to buy that.");
							}
							break;
						}
					}
					else if (amount > iter.getAmountAvail() && iter.getAmountAvail() != -1337) {
						user.sendMessage("There are only " + iter.getAmountAvail() + " available.");
					}
				}
				else {
					
				}
			}
		}
	}
}
