/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

public class Shop extends EconEntity {
	private boolean	infItems	= false;
	private long	lastRestock	= 0;
	
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
	
	public Shop(String saveData) {
		load(saveData);
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
		lastRestock = etc.getServer().getTime();
	}
	
	public void restock() {
		if (etc.getServer().getTime() - lastRestock >= DataManager.getRestockTime()) {
			System.out.println("Restocking " + name);
			lastRestock = etc.getServer().getTime();
			for (ShopItemStack iter : availableItems) {
				// Check against the DataManager value
				if (iter.getAmountAvail() < DataManager.getItem(iter.getShopItem().getName()).getMaxAvail()) {
					iter.setAmountAvail(DataManager.getItem(iter.getShopItem().getName()).getMaxAvail());
				}
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
				if (DataManager.getDebug()) {
					System.out.println("Item name is: " + itemName);
				}
				
				Transaction.process(new Transaction(user, this, new ShopItemStack(DataManager.getItem(itemName), amount)));
			}
			catch (NumberFormatException e1) {
				user.sendMessage("The correct use is /sell [item name] [amount]");
			}
		}
		else {
			user.sendMessage("The correct use is /sell [item name] [amount]");
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
				if (DataManager.getDebug()) {
					System.out.println("Item name is: " + itemName);
				}
				
				Transaction.process(new Transaction(this, user, new ShopItemStack(DataManager.getItem(itemName), amount)));
			}
			catch (NumberFormatException e1) {
				user.sendMessage("The correct use is /buy [item name] [amount]");
			}
		}
		else {
			user.sendMessage("The correct use is /buy [item name] [amount]");
		}
	}
	
	public String toString() {
		// Used for saving the shop, data needed is:
		// Shop Name : Shop Money : infAmount : lastRestock : id amount : id amount : id amount
		String temp = name + ":" + money.getAmount() + ":" + infItems + ":" + lastRestock;
		for (ShopItemStack iter : availableItems) {
			temp += ":" + iter.getItemID() + " " + iter.getAmountAvail();
		}
		return temp;
	}
	
	public void load(String loadData) {
		String sc[] = loadData.split(":"); // Split colon
		
		if (DataManager.getDebug()) {
			System.out.println("Loading a shop named " + sc[0]);
		}
		
		// Safety check
		if (sc.length >= 4) {
			name = sc[0];
			money.setAmount(Integer.valueOf(sc[1]));
			infItems = Boolean.valueOf(sc[2]);
			lastRestock = Long.valueOf(sc[3]);
			for (String iter : sc) {
				if (!iter.equalsIgnoreCase(sc[0]) && !iter.equalsIgnoreCase(sc[1]) && !iter.equalsIgnoreCase(sc[2])
						&& !iter.equalsIgnoreCase(sc[3])) {
					String[] ss = iter.split(" ");
					availableItems.add(new ShopItemStack(new ShopItem(Integer.valueOf(ss[0].trim())), Integer.valueOf(ss[1].trim())));
				}
			}
		}
	}
}
