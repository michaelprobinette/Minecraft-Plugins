/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import org.bukkit.Server;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * Class that handles buy/sell of items.
 * 
 * @author Vandolis
 */
public class Shop extends EconEntity {
	private final String	regex			= DataManager.getShopRegex();
	private final String	regex2			= DataManager.getShop2Regex();
	private boolean			infItems		= false;
	private long			lastRestock		= 0;
	private boolean			canRestock		= true;
	private boolean			usersCanBuy		= true;
	private boolean			usersCanSell	= true;
	private boolean			hidden			= false;
	
	/**
	 * Loads a shop from a save string.
	 * 
	 * @param saveData
	 */
	public Shop(String saveData) {
		load(saveData);
		setShop(this);
	}
	
	/**
	 * Makes a shop with the given name, given infItems value, and given {@link Money} amount. Preferred constructor.
	 * 
	 * @param name
	 *            Name of the shop
	 * @param infItems
	 *            Have infinite stock of items or not
	 * @param amountMoney
	 *            Starting amount of money.
	 * @param canRestock
	 *            Let the shop restock
	 */
	public Shop(String name, boolean infItems, int amountMoney, boolean canRestock) {
		super(new Money(amountMoney));
		this.infItems = infItems;
		setName(name);
		this.canRestock = canRestock;
		setShop(this);
		initialize();
	}
	
	/**
	 * @param string
	 * @param long1
	 * @param boolean1
	 * @param long2
	 * @param boolean2
	 * @param boolean3
	 * @param boolean4
	 * @param boolean5
	 * @param string2
	 */
	public Shop(String string, long long1, boolean boolean1, long long2, boolean boolean2, boolean boolean3, boolean boolean4,
			boolean boolean5, String string2) {
		setName(string);
		getMoney().setAmount((int) long1);
		infItems = boolean1;
		lastRestock = long2;
		canRestock = boolean2;
		usersCanBuy = boolean3;
		usersCanSell = boolean4;
		hidden = boolean5;
		loadItems(string2);
		setShop(this);
	}
	
	/**
	 * @param string2
	 */
	private void loadItems(String string2) {
		if (DataManager.getDebug()) {
			System.out.println("Loading items from \"" + string2 + "\"");
		}
		
		String split[] = string2.split(":");
		
		for (String iter : split) {
			if (DataManager.getDebug()) {
				System.out.println("Loading an item from: " + iter);
			}
			String ss[] = iter.split(" ");
			if (ss.length == 2) {
				getAvailableItems().add(new ShopItemStack(Integer.valueOf(ss[0]), Integer.valueOf(ss[1])));
			}
			else if (ss.length == 4) {
				getAvailableItems().add(
						new ShopItemStack(Integer.valueOf(ss[0]), Integer.valueOf(ss[1]), Integer.valueOf(ss[2]), Integer.valueOf(ss[3])));
			}
		}
	}
	
	/**
	 * @return the restock
	 */
	public boolean isRestock() {
		return canRestock;
	}
	
	/**
	 * @param restock
	 *            the restock to set
	 */
	public void setRestock(boolean restock) {
		canRestock = restock;
	}
	
	/**
	 * @return item string for SQL
	 */
	public String getItemString() {
		String temp = "";
		
		for (ShopItemStack iter : getAvailableItems()) {
			temp += ":" + iter.getItemId() + " " + iter.getAmountAvail() + " " + iter.getBuyPrice() + " " + iter.getSellPrice();
		}
		temp = temp.replaceFirst(":", "");
		
		if (DataManager.getDebug()) {
			System.out.println("Item string is \"" + temp + "\"");
		}
		
		return temp;
	}
	
	/**
	 * Stocks the shop with items.
	 */
	private void initialize() {
		restock();
		setShop(this);
	}
	
	/**
	 * Loads a shop from a save string.
	 * 
	 * @param loadData
	 */
	public void load(String loadData) {
		String sc[] = loadData.split(DataManager.getShopRegex()); // Split colon
		
		if (DataManager.getDebug()) {
			System.out.println("Loading a shop named " + sc[0]);
		}
		
		/*
		 * Safety check of length then load the data.
		 */
		if (sc.length >= 4) {
			setName(sc[0]);
			getMoney().setAmount(Integer.valueOf(sc[1]));
			infItems = Boolean.valueOf(sc[2]);
			lastRestock = Long.valueOf(sc[3]);
			for (String iter : sc) {
				if (!iter.equalsIgnoreCase(sc[0]) && !iter.equalsIgnoreCase(sc[1]) && !iter.equalsIgnoreCase(sc[2])
						&& !iter.equalsIgnoreCase(sc[3])) {
					String[] ss = iter.split(regex2);
					if (ss.length == 2) {
						/*
						 * Old format
						 */
						getAvailItems().add(new ShopItemStack(Integer.valueOf(ss[0].trim()), Integer.valueOf(ss[1].trim())));
					}
					else {
						/*
						 * New format
						 */
						getAvailItems().add(
								new ShopItemStack(Integer.valueOf(ss[0].trim()), Integer.valueOf(ss[2].trim()), Integer.valueOf(ss[1]
										.trim()), Integer.valueOf(ss[3].trim())));
					}
				}
			}
		}
		else {
			initialize();
		}
	}
	
	/**
	 * Tries to restock the {@link Shop}. If not forced, checks the last restock time against the current {@link Server} time.
	 * 
	 * @param force
	 */
	public void restock(boolean force) {
		/*
		 * Check the current time against the last restock time
		 */
		if (((DataManager.getServer().getTime() - lastRestock >= EconomyProperties.getRestockTime()) || force) && canRestock) {
			System.out.println("Restocking " + getName());
			lastRestock = DataManager.getServer().getTime();
			for (ShopItemStack iter : getAvailItems()) {
				/*
				 * Checks the current value against the max value for the item. 
				 * Only restocks the item if the current amount is less than the max.
				 */
				if (iter.getAmountAvail() < iter.getMaxAvail()) {
					iter.setAmountAvail(iter.getMaxAvail());
				}
			}
			
			if (force) {
				boolean found = false;
				for (ShopItem iter : DataManager.getItemList()) {
					found = false;
					for (ShopItem current : getAvailItems()) {
						if (iter.getItemId() == current.getItemId()) {
							found = true;
						}
					}
					
					if (!found) {
						getAvailItems().add(new ShopItemStack(iter.getItemId(), iter.getMaxAvail()));
					}
				}
			}
		}
	}
	
	/**
	 * Normal restock call, not forced.
	 */
	public void restock() {
		restock(false);
	}
	
	/**
	 * Returns the save string to write to file. Format is ShopName:Shop Money:infAmount:lastRestock:idamount:id amount:id amount
	 * 
	 * @return
	 */
	public String getSaveString() {
		// Used for saving the shop, data needed is:
		// 
		String temp = getName() + regex + getMoney().getAmount() + regex + infItems + regex + lastRestock;
		
		for (ShopItemStack iter : getAvailItems()) {
			temp += regex + iter.getItemId() + regex2 + iter.getBuyPrice() + regex2 + iter.getSellPrice() + regex2 + iter.getAmountAvail();
		}
		
		return temp;
	}
	
	/**
	 * Returns if the {@link Shop} has all infinite items
	 * 
	 * @return
	 */
	public boolean getInfItems() {
		return infItems;
	}
	
	/**
	 * Returns the last time the {@link Shop} was restocked.
	 * 
	 * @return
	 */
	public long getLastRestock() {
		return lastRestock;
	}
	
	/**
	 * @return the canRestock
	 */
	protected boolean isCanRestock() {
		return canRestock;
	}
	
	/**
	 * @param canRestock
	 *            the canRestock to set
	 */
	protected void setCanRestock(boolean canRestock) {
		this.canRestock = canRestock;
	}
	
	/**
	 * @return the usersCanBuy
	 */
	protected boolean isUsersCanBuy() {
		return usersCanBuy;
	}
	
	/**
	 * @param usersCanBuy
	 *            the usersCanBuy to set
	 */
	protected void setUsersCanBuy(boolean usersCanBuy) {
		this.usersCanBuy = usersCanBuy;
	}
	
	/**
	 * @return the usersCanSell
	 */
	protected boolean isUsersCanSell() {
		return usersCanSell;
	}
	
	/**
	 * @param usersCanSell
	 *            the usersCanSell to set
	 */
	protected void setUsersCanSell(boolean usersCanSell) {
		this.usersCanSell = usersCanSell;
	}
	
	/**
	 * @return the showInLists
	 */
	protected boolean isHidden() {
		return hidden;
	}
	
	/**
	 * @param showInLists
	 *            the showInLists to set
	 */
	protected void setShowInLists(boolean showInLists) {
		hidden = showInLists;
	}
	
	/**
	 * @param infItems
	 *            the infItems to set
	 */
	protected void setInfItems(boolean infItems) {
		this.infItems = infItems;
	}
	
	/**
	 * @param lastRestock
	 *            the lastRestock to set
	 */
	protected void setLastRestock(long lastRestock) {
		this.lastRestock = lastRestock;
	}
	
	/**
	 * Returns the number of items in the inventory with a given name. Returns 0 if the item is not listed.
	 * 
	 * @param itemName
	 * @return
	 */
	public int getAvailableCount(String itemName) {
		for (ShopItemStack iter : getAvailItems()) {
			if (iter.getName().equalsIgnoreCase(itemName)) {
				return iter.getAmountAvail();
			}
		}
		return 0;
	}
}
