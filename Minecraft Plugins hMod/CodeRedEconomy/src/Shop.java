public class Shop extends EconEntity {
	private boolean	infItems	= false;
	
	public Shop() {
		initialize();
	}
	
	/**
	 * @param infItems
	 * @param infMoney
	 */
	public Shop(boolean infItems) {
		this.infItems = infItems;
		initialize();
	}
	
	public Shop(String name, boolean infItems, int amountMoney) {
		this.infItems = infItems;
		this.name = name;
		this.money.setAmount(amountMoney);
		initialize();
	}
	
	/**
	 * Use to set infItems and to set the base amount of money use DataManager.getInfValue() in the amountMoney for infMoney
	 * 
	 * @param infItems
	 * @param amountMoney
	 */
	public Shop(boolean infItems, int amountMoney) {
		this.infItems = infItems;
		this.money.setAmount(amountMoney);
		initialize();
	}
	
	private void initialize() {
		// Load all of the implemented items from the DataManager
		for (ShopItem iter : DataManager.getItemList()) {
			if (infItems) {
				availableItems.add(new ShopItemStack(iter, DataManager.getInfValue())); // Inf items value
			}
			else {
				availableItems.add(new ShopItemStack(iter, iter.getMaxAvail()));
			}
		}
	}
	
	public void sell(User user, String[] split) {
		String itemName = "";
		int amount = 0;
		if (split.length >= 2) {
			try {
				for (String iter : split) {
					if (!iter.equalsIgnoreCase(split[0])) {
						// It is not the /sell part
						// Try and convert to a number
						
						try {
							amount = Integer.valueOf(iter);
						}
						catch (NumberFormatException e) {
							// Not a number, add to name
							itemName += iter + " ";
						}
					}
				}
				if (amount == 0) {
					amount = 1;
				}
				itemName = itemName.trim();
				if (CodeRedEconomy.debug) {
					System.out.println("Item name is: " + itemName);
				}
				
				Transaction.process(new Transaction(user, this, new ShopItemStack(DataManager.getItem(itemName), amount)));
			}
			catch (NumberFormatException e1) {
				user.sendMessage("The correct use is \"/sell [item name] [amount]\"");
			}
		}
		else {
			user.sendMessage("The correct use is \"/sell [item name] [amount]\"");
		}
	}
	
	public void buy(User user, String[] split) {
		// Check players balance, find what they want to buy, check priv level, buy
		String itemName = "";
		int amount = 0;
		if (split.length >= 2) {
			try {
				for (String iter : split) {
					if (!iter.equalsIgnoreCase(split[0])) {
						// It is not the /sell part
						// Try and convert to a number
						
						try {
							amount = Integer.valueOf(iter);
						}
						catch (NumberFormatException e) {
							// Not a number, add to name
							itemName += iter + " ";
						}
					}
				}
				if (amount == 0) {
					amount = 1;
				}
				itemName = itemName.trim();
				if (CodeRedEconomy.debug) {
					System.out.println("Item name is: " + itemName);
				}
				
				Transaction.process(new Transaction(this, user, new ShopItemStack(DataManager.getItem(itemName), amount)));
			}
			catch (NumberFormatException e1) {
				user.sendMessage("The correct use is \"/buy [item name] [amount]\"");
			}
		}
		else {
			user.sendMessage("The correct use is \"/buy [item name] [amount]\"");
		}
	}
	// String itemName = "";
	// if (split.length >= 3) {
	// try {
	// int amount = Integer.valueOf(split[1]);
	// for (int i = 2; i < split.length; i++) {
	// itemName += split[i] + " ";
	// }
	// itemName = itemName.trim();
	// if (CodeRedEconomy.debug) {
	// System.out.println("Item name is: " + itemName);
	// }
	// for (ShopItemStack iter : availableItems) {
	// if (iter.getShopItem().getName().equalsIgnoreCase(itemName)) {
	// if (iter.getAmountAvail() >= amount || iter.getAmountAvail() == DataManager.getInfValue()) {
	// // This is a valid name, check if player can buy
	// ShopItemStack inCart = new ShopItemStack(iter.getShopItem(), amount);
	// Transaction.process(new Transaction(this, user, inCart));
	// }
	// else if (amount > iter.getAmountAvail() && iter.getAmountAvail() != DataManager.getInfValue()) {
	// user.sendMessage("There are only " + iter.getAmountAvail() + " available.");
	// }
	// }
	// }
	// }
	// catch (NumberFormatException e) {
	// user.sendMessage("The correct use is \"/buy [item name] [amount]\"");
	// }
	// }
	// else {
	// user.sendMessage("The correct use is \"/buy [item name] [amount]\"");
	// }
	// }
}
