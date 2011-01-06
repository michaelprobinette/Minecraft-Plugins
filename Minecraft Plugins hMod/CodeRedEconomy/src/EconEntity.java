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

import java.util.ArrayList;

public abstract class EconEntity {
	protected Money						money			= new Money();
	protected ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	protected String					name			= "";
	protected boolean					isPlayer		= false;
	protected boolean					hasUser			= false;
	protected Transaction				lastTrans		= null;
	private User						user			= null;
	protected long						lastAutoDeposit	= 0;
	
	public EconEntity(Money money) {
		this.money = money;
	}
	
	public EconEntity() {
	}
	
	public Money getMoney() {
		return money;
	}
	
	public long getLastAutoDeposit() {
		return lastAutoDeposit;
	}
	
	public void autoDesposit(long serverTime) {
		if (serverTime - lastAutoDeposit >= DataManager.getAutoDepositTime()) {
			money.addAmount(DataManager.getAutoDepositAmount());
		}
	}
	
	public void recieveMoney(Money money) {
		money.addAmount(money.getAmount());
	}
	
	public ArrayList<ShopItemStack> getAvailItems() {
		return availableItems;
	}
	
	public boolean isPlayer() {
		return isPlayer;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public void setUser(User user) {
		this.user = user;
		hasUser = true;
	}
	
	public boolean hasUser() {
		return hasUser;
	}
	
	public User getUser() {
		return user;
	}
	
	/**
	 * @param shopItemStack
	 * @return
	 */
	public boolean canBuy(ShopItemStack shopItemStack) {
		if (shopItemStack != null) {
			// Check if the entity is a player
			if (isPlayer) {
				// Get the player from the user and check if they are in the correct group to buy the item, as well as if they have enough
				// money
				if ((user.getPlayer().isInGroup(DataManager.getReqGroup(shopItemStack.getShopItem().getItemID())))
						&& (shopItemStack.getTotalBuyPrice().getAmount() <= money.getAmount() || money.getAmount() == DataManager
								.getInfValue())) {
					return true; // True it can buy
				}
			}
			else {
				// Not a player, check if it has enough money
				if (shopItemStack.getTotalBuyPrice().getAmount() <= money.getAmount() || money.getAmount() == DataManager.getInfValue()) {
					return true; // True it can buy
				}
			}
			return false; // False it cannot buy
		}
		else {
			return true;
		}
	}
	
	/**
	 * @param stack
	 * @return
	 */
	public boolean canSell(ShopItemStack stack) {
		if (stack != null) {
			// Check if the entity is a player
			if (isPlayer) {
				user.updateArray();
				for (ShopItemStack iter : availableItems) {
					if (iter.getItemID() == stack.getItemID()) {
						// Check amount
						return (stack.getAmountAvail() <= iter.getAmountAvail());
					}
				}
				return user.getPlayer().getInventory().hasItem(stack.getItemID(), stack.getAmountAvail());
			}
			else {
				if (hasItems(stack)) {
					return true;
				}
			}
			return false;
		}
		else {
			return true;
		}
	}
	
	/**
	 * @param item
	 * @return
	 */
	public int numberOfItems(ShopItem item) {
		if (isPlayer) {
			user.updateArray(); // Grab the items from the players inventory
		}
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == item.getItemID()) {
				return iter.getAmountAvail();
			}
		}
		return 0;
	}
	
	public boolean hasItems(ShopItemStack stack) {
		if (isPlayer) {
			user.updateArray(); // Grab the items from the players inventory
		}
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID()) {
				if (iter.getAmountAvail() >= stack.getAmountAvail() || iter.getAmountAvail() == DataManager.getInfValue()) {
					return true;
				}
			}
		}
		return false;
	}
	
	public void addShopItems(ShopItemStack stack) {
		boolean found = false;
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID()) {
				found = true;
				if (iter.getAmountAvail() != DataManager.getInfValue()) {
					iter.addAmountAvail(stack.getAmountAvail());
				}
				break;
			}
		}
		if (!found) {
			availableItems.add(stack);
		}
	}
	
	public void removeShopItems(ShopItemStack stack) {
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID() && iter.getAmountAvail() != -1) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	public void setLastTrans(Transaction trans) {
		lastTrans = trans;
	}
}
