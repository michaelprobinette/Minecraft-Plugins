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
	protected Transaction				lastTrans		= null;
	private User						user			= null;
	private Shop						shop			= null;
	protected long						lastAutoDeposit	= 0;
	protected ArrayList<Transaction>	transactions	= new ArrayList<Transaction>();
	
	public EconEntity() {
		
	}
	
	public EconEntity(Money money) {
		this.money = money;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public long getLastAutoDeposit() {
		return lastAutoDeposit;
	}
	
	public void setUser(User user) {
		this.user = user;
	}
	
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	public Shop getShop() {
		return shop;
	}
	
	public void autoDesposit(long serverTime) {
		if (serverTime - lastAutoDeposit >= DataManager.getAutoDepositTime() && money.getAmount() != DataManager.getInfValue()) {
			if (DataManager.getDebug()) {
				System.out.println("Auto Depositing " + DataManager.getAutoDepositAmount() + " into " + name + " Server: " + serverTime
						+ " Last: " + lastAutoDeposit);
			}
			money.addAmount(DataManager.getAutoDepositAmount());
			lastAutoDeposit = serverTime;
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
	
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	public User getUser() {
		return user;
	}
	
	/**
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean canBuy(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can buy " + stack.getAmountAvail() + " "
						+ stack.getShopItem().getName());
			}
		}
		boolean canBuy = false;
		boolean boughtLessThanMax = true;
		if (stack != null) {
			// Check if the entity is a player
			if (isPlayer) {
				// Get the player from the user and check if they are in the correct group to buy the item, as well as if they have enough
				// money
				canBuy = ((user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getShopItem().getItemID()))) && (stack
						.getTotalBuyPrice().getAmount() <= money.getAmount() || money.getAmount() == DataManager.getInfValue()));
				if (!(user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getShopItem().getItemID())))) {
					throw new EconException("You are not allowed to purchase this item.", name + " is not allowed to purchase this item.");
				}
			}
			else {
				// Not a player, check if it has enough money
				canBuy = (stack.getTotalBuyPrice().getAmount() <= money.getAmount() || money.getAmount() == DataManager.getInfValue());
			}
			if ((stack.getTotalBuyPrice().getAmount() > money.getAmount() && money.getAmount() != DataManager.getInfValue())) {
				throw new EconException("You do not have enough " + Money.getMoneyName() + " (" + getMoney().getAmount() + "/"
						+ stack.getTotalBuyPrice().getAmount() + ")", getName() + " does not have enough " + Money.getMoneyName());
			}
		}
		else {
			canBuy = true;
		}
		
		if (user != null && stack != null) {
			// Will only check if it is a user buying
			boughtLessThanMax = checkMaxBuy(stack);
		}
		
		return (canBuy && boughtLessThanMax);
	}
	
	public boolean checkMaxBuy(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max buy for " + stack.getShopItem().getName());
		}
		int totBought = 0;
		for (Transaction iter : transactions) {
			// If the buyer is this entity and it is an item transaction, check the item id
			if (iter.getBuyer().getName().equalsIgnoreCase(name) && iter.getStack() != null) {
				if (iter.getStack().getItemID() == stack.getItemID()) {
					// Same item id, add it to the total bought
					totBought += iter.getStack().getAmountAvail();
				}
			}
		}
		if (totBought + stack.getAmountAvail() > stack.getShopItem().getMaxBuy()
				&& stack.getShopItem().getMaxBuy() != DataManager.getInfValue()) {
			// The total bought plus the stack amount is over the max buy allowed limit, return false.
			if (user != null) {
				if (totBought != stack.getShopItem().getMaxBuy()) {
					throw new EconException("You can only buy " + (stack.getShopItem().getMaxBuy() - totBought) + " more "
							+ stack.getShopItem().getName(), name + " has can't buy that many " + stack.getShopItem().getName());
				}
				else {
					throw new EconException("You can't buy anymore " + stack.getShopItem().getName(), name + " can't buy anymore "
							+ stack.getShopItem().getName());
				}
			}
			if (DataManager.getDebug()) {
				System.out.println(name + " can only buy " + (stack.getShopItem().getMaxBuy() - totBought) + " more "
						+ stack.getShopItem().getName() + ".");
			}
			return false;
		}
		return true;
	}
	
	public boolean checkMaxSell(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max sell for " + stack.getShopItem().getName());
		}
		int totSold = 0;
		for (Transaction iter : transactions) {
			if (iter.getSeller().getName().equalsIgnoreCase(name) && iter.getStack() != null) {
				if (iter.getStack().getItemID() == stack.getItemID()) {
					totSold += iter.getStack().getAmountAvail();
				}
			}
		}
		if (totSold + stack.getAmountAvail() > stack.getShopItem().getMaxSell()
				&& stack.getShopItem().getMaxSell() != DataManager.getInfValue()) {
			if (user != null) {
				if (totSold != stack.getShopItem().getMaxSell()) {
					throw new EconException(name + " can't buy that many " + stack.getShopItem().getName(), "You can only sell "
							+ (stack.getShopItem().getMaxSell() - totSold) + " more " + stack.getShopItem().getName());
				}
				else {
					throw new EconException(name + " can't sell anymore " + stack.getShopItem().getName(), "You can't sell anymore "
							+ stack.getShopItem().getName());
				}
			}
			if (DataManager.getDebug()) {
				System.out.println(name + " can only sell " + (stack.getShopItem().getMaxSell() - totSold) + " more "
						+ stack.getShopItem().getName() + ".");
			}
			return false;
		}
		return true;
	}
	
	public void addTransaction(Transaction trans) {
		transactions.add(trans);
	}
	
	public void undoTransaction(Transaction trans) {
		transactions.remove(trans);
	}
	
	public Transaction getTrans(String buyer, String seller, int itemID) {
		for (Transaction iter : transactions) {
			if (iter.getBuyer().getName().equalsIgnoreCase(buyer) || iter.getSeller().getName().equalsIgnoreCase(seller)) {
				if (iter.getStack() != null) {
					if (iter.getStack().getItemID() == itemID) {
						return iter;
					}
				}
			}
		}
		
		return null;
	}
	
	/**
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean canSell(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can sell " + stack.getAmountAvail() + " "
						+ stack.getShopItem().getName());
			}
		}
		boolean hasItems = false;
		boolean soldLessThanMax = true;
		if (stack != null) {
			// Check if the entity is a player
			if (isPlayer) {
				user.updateArray();
			}
			ShopItemStack sis = getStack(availableItems, stack);
			if (sis != null) {
				hasItems = (stack.getAmountAvail() <= sis.getAmountAvail());
				if (sis.getAmountAvail() == -1) {
					hasItems = true;
				}
			}
		}
		else {
			hasItems = true;
		}
		
		if (!hasItems) {
			if (stack.getItemID() != 0) {
				throw new EconException(name + " does not have enough " + stack.getShopItem().getName(), "You do not have enough "
						+ stack.getShopItem().getName() + " (" + numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
			}
			else {
				throw new EconException(name + " does not sell that item.", "You can't sell that item.");
			}
		}
		
		if (user != null && stack != null) {
			// Only checks if it is a user selling
			soldLessThanMax = checkMaxSell(stack);
		}
		
		return (hasItems && soldLessThanMax);
	}
	
	/**
	 * @param item
	 * @return
	 */
	public int numberOfItems(ShopItem item) {
		if (isPlayer) {
			user.updateArray(); // Grab the items from the players inventory
		}
		ShopItemStack sis = getStack(availableItems, new ShopItemStack(item, 1));
		if (sis != null) {
			return sis.getAmountAvail();
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
		ShopItemStack sis = getStack(availableItems, stack);
		if (sis != null) {
			if (sis.getAmountAvail() != DataManager.getInfValue()) {
				sis.addAmountAvail(stack.getAmountAvail());
			}
		}
		else {
			availableItems.add(stack);
		}
	}
	
	public void removeShopItems(ShopItemStack stack) {
		ShopItemStack sis = getStack(availableItems, stack);
		if (sis != null) {
			if (sis.getAmountAvail() != DataManager.getInfValue()) {
				sis.addAmountAvail(-stack.getAmountAvail());
			}
		}
	}
	
	public void setLastTrans(Transaction trans) {
		lastTrans = trans;
	}
	
	public ShopItemStack getStack(ArrayList<ShopItemStack> arr, ShopItemStack stack) {
		for (ShopItemStack iter : arr) {
			if (iter.getItemID() == stack.getItemID()) {
				return iter;
			}
		}
		return null;
	}
}
