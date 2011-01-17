/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import java.util.ArrayList;

import org.bukkit.entity.Player;

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
	protected int						numTransBuy		= 0;
	protected int						numTransSell	= 0;
	
	/**
	 * Default entity constructor
	 */
	public EconEntity() {
	}
	
	/**
	 * Makes a default entity with the given money.
	 * 
	 * @param money
	 */
	public EconEntity(Money money) {
		this.money = money;
	}
	
	/**
	 * Used if the entity is not a {@link Player}. Adds the {@link ShopItemStack} to the availableItems array.
	 * 
	 * @param stack
	 */
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
	
	/**
	 * Adds the given {@link Transaction} to the entities array of transactions.
	 * 
	 * @param trans
	 */
	public void addTransaction(Transaction trans) {
		transactions.add(trans);
	}
	
	/**
	 * Used to add money to the entity on a given interval. Send it the current server time.
	 * 
	 * @param serverTime
	 */
	public void autoDesposit(long serverTime) {
		if ((serverTime - lastAutoDeposit >= DataManager.getAutoDepositTime()) && (money.getAmount() != DataManager.getInfValue())) {
			if (DataManager.getDebug()) {
				System.out.println("Auto Depositing " + DataManager.getAutoDepositAmount() + " into " + name + " Server: " + serverTime + " Last: " + lastAutoDeposit);
			}
			money.addAmount(DataManager.getAutoDepositAmount());
			lastAutoDeposit = serverTime;
		}
	}
	
	/**
	 * Checks to see if the entity has enough money to buy, as well as checks to see if they can buy the amount given based on the buyMax.
	 * 
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean canBuy(ShopItemStack stack) throws EconException {
		// TODO clean up the canBuy code
		
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can buy " + stack.getAmountAvail() + " " + stack.getShopItem().getName());
			}
		}
		boolean canBuy = false;
		boolean boughtLessThanMax = true;
		if (stack != null) {
			// Check if the entity is a player
			if (isPlayer) {
				// Get the player from the user and check if they are in the correct group to buy the item, as well as if they have enough
				// money
				
				canBuy = (stack.getTotalBuyPrice().getAmount() <= money.getAmount()) || (money.getAmount() == DataManager.getInfValue());
				// FIXME Need to change once bukkit implements a permissions system
				
				// canBuy = ((user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getShopItem().getItemID()))) && ((stack
				// .getTotalBuyPrice().getAmount() <= money.getAmount()) || (money.getAmount() == DataManager.getInfValue())));
				// if (!(user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getShopItem().getItemID())))) {
				// throw new EconException("You are not allowed to purchase this item.", name + " is not allowed to purchase this item.");
				// }
			}
			else {
				// Not a player, check if it has enough money
				canBuy = ((stack.getTotalBuyPrice().getAmount() <= money.getAmount()) || (money.getAmount() == DataManager.getInfValue()));
			}
			if (((stack.getTotalBuyPrice().getAmount() > money.getAmount()) && (money.getAmount() != DataManager.getInfValue()))) {
				throw new EconException("You do not have enough " + Money.getMoneyName() + " (" + getMoney().getAmount() + "/" + stack.getTotalBuyPrice().getAmount() + ")", getName() + " does not have enough " + Money.getMoneyName());
			}
		}
		else {
			canBuy = true;
		}
		
		if ((user != null) && (stack != null)) {
			// Will only check if it is a user buying, throws a EconException if they can't buy any more.
			boughtLessThanMax = checkMaxBuy(stack);
		}
		
		return (canBuy && boughtLessThanMax);
	}
	
	/**
	 * Checks to see if the entity can sell the item by seeing if they have the required items, as well as if they can sell the given amount
	 * based on sellMax.
	 * 
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean canSell(ShopItemStack stack) throws EconException {
		// TODO Clean up the canSell code
		
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can sell " + stack.getAmountAvail() + " " + stack.getShopItem().getName());
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
				throw new EconException(name + " does not have enough " + stack.getShopItem().getName(), "You do not have enough " + stack.getShopItem().getName() + " (" + numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
			}
			else {
				throw new EconException(name + " does not sell that item.", "You can't sell that item.");
			}
		}
		
		if ((user != null) && (stack != null)) {
			// Only checks if it is a user selling
			soldLessThanMax = checkMaxSell(stack);
		}
		
		return (hasItems && soldLessThanMax);
	}
	
	/**
	 * Checks to see if the entity can buy the given amount of items based on the {@link ShopItem} maxBuy. Throws an {@link EconException}
	 * if they can't buy that many.
	 * 
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean checkMaxBuy(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max buy for " + stack.getShopItem().getName());
		}
		int totBought = 0;
		for (Transaction iter : transactions) {
			// If the buyer is this entity and it is an item transaction, check the item id
			if (iter.getBuyer().getName().equalsIgnoreCase(name) && (iter.getStack() != null)) {
				if (iter.getStack().getItemID() == stack.getItemID()) {
					// Same item id, add it to the total bought
					totBought += iter.getStack().getAmountAvail();
				}
			}
		}
		if ((totBought + stack.getAmountAvail() > stack.getShopItem().getMaxBuy()) && (stack.getShopItem().getMaxBuy() != DataManager.getInfValue())) {
			// Print debug info to console
			if (DataManager.getDebug()) {
				System.out.println(name + " can only buy " + (stack.getShopItem().getMaxBuy() - totBought) + " more " + stack.getShopItem().getName() + ".");
			}
			
			// Exception handling
			if (totBought != stack.getShopItem().getMaxBuy()) {
				throw new EconException("You can only buy " + (stack.getShopItem().getMaxBuy() - totBought) + " more " + stack.getShopItem().getName(), name + " has can't buy that many " + stack.getShopItem().getName());
			}
			else {
				throw new EconException("You can't buy anymore " + stack.getShopItem().getName(), name + " can't buy anymore " + stack.getShopItem().getName());
			}
		}
		return true;
	}
	
	/**
	 * Checks to see if the entity can sell the given amount of items based on the {@link ShopItem} maxSell. Throws an {@link EconException}
	 * if they can't buy that many.
	 * 
	 * @param stack
	 * @return
	 * @throws EconException
	 */
	public boolean checkMaxSell(ShopItemStack stack) throws EconException {
		// Print debug info to console
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max sell for " + stack.getShopItem().getName());
		}
		
		int totSold = 0; // A count of how many of the item the entity has already sold
		
		for (Transaction iter : transactions) {
			if (iter.getSeller().getName().equalsIgnoreCase(name) && (iter.getStack() != null)) {
				if (iter.getStack().getItemID() == stack.getItemID()) {
					// Same itemId, add it to the totSold count
					totSold += iter.getStack().getAmountAvail();
				}
			}
		}
		if ((totSold + stack.getAmountAvail() > stack.getShopItem().getMaxSell()) && (stack.getShopItem().getMaxSell() != DataManager.getInfValue())) {
			// Print debug info to console
			if (DataManager.getDebug()) {
				System.out.println(name + " can only sell " + (stack.getShopItem().getMaxSell() - totSold) + " more " + stack.getShopItem().getName() + ".");
			}
			
			// Exception handling
			if (totSold != stack.getShopItem().getMaxSell()) {
				throw new EconException(name + " can't buy that many " + stack.getShopItem().getName(), "You can only sell " + (stack.getShopItem().getMaxSell() - totSold) + " more " + stack.getShopItem().getName());
			}
			else {
				throw new EconException(name + " can't sell anymore " + stack.getShopItem().getName(), "You can't sell anymore " + stack.getShopItem().getName());
			}
		}
		return true;
	}
	
	/**
	 * Returns an {@link ArrayList} containing the entities {@link ShopItemStack}. If the entity is a {@link User} then it will
	 * automatically update the array before returning it.
	 * 
	 * @return
	 */
	public ArrayList<ShopItemStack> getAvailItems() {
		// If there is a user for the entity, update the array with their current inventory
		if (user != null) {
			user.updateArray();
		}
		return availableItems;
	}
	
	/**
	 * Returns the last server time the entity received {@link Money} from the auto deposit system.
	 * 
	 * @return
	 */
	public long getLastAutoDeposit() {
		return lastAutoDeposit;
	}
	
	/**
	 * Returns the entities {@link Money} object.
	 * 
	 * @return
	 */
	public Money getMoney() {
		return money;
	}
	
	/**
	 * Returns the name of the entity.
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * Returns the {@link Shop} of the entity. Null if the entity is not a {@link Shop}.
	 * 
	 * @return
	 */
	public Shop getShop() {
		return shop;
	}
	
	/**
	 * Searches the given array of {@link ShopItemStack} for a stack with the same itemId as the given {@link ShopItemStack}. Returns this
	 * stack. Null if not found.
	 * 
	 * @param arr
	 * @param stack
	 * @return
	 */
	public ShopItemStack getStack(ArrayList<ShopItemStack> arr, ShopItemStack stack) {
		for (ShopItemStack iter : arr) {
			if (iter.getItemID() == stack.getItemID()) {
				return iter;
			}
		}
		return null;
	}
	
	/**
	 * Searches the entities list of transactions for one matching the buyer, seller, and itemId. Null if not found.
	 * 
	 * @param buyer
	 * @param seller
	 * @param itemID
	 * @return
	 */
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
	 * Returns the array of the entities transactions.
	 * 
	 * @return
	 */
	public ArrayList<Transaction> getTransactions() {
		return transactions;
	}
	
	/**
	 * Returns the entities user object. Null if not a user.
	 * 
	 * @return
	 */
	public User getUser() {
		return user;
	}
	
	/**
	 * Checks to see if the entity has enough of the item given in the {@link ShopItemStack}. False if there is no matching item, or if
	 * there are not enough.
	 * 
	 * @param stack
	 * @return
	 */
	public boolean hasItems(ShopItemStack stack) {
		if (isPlayer) {
			user.updateArray(); // Grab the items from the players inventory
		}
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID()) {
				if ((iter.getAmountAvail() >= stack.getAmountAvail()) || (iter.getAmountAvail() == DataManager.getInfValue())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Boolean of whether or not the entity has a player assigned to it yet.
	 * 
	 * @return
	 */
	public boolean isPlayer() {
		return isPlayer;
	}
	
	/**
	 * Returns the number of items in the availableItems array. If the entity is a player it will also update the array before checking.
	 * 
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
	
	/**
	 * Used if the entity is not a {@link Player}. Removes the {@link ShopItemStack} from its availableItems array.
	 * 
	 * @param stack
	 */
	public void removeShopItems(ShopItemStack stack) {
		ShopItemStack sis = getStack(availableItems, stack);
		if (sis != null) {
			if (sis.getAmountAvail() != DataManager.getInfValue()) {
				sis.addAmountAvail(-stack.getAmountAvail());
			}
		}
	}
	
	/**
	 * Sets the entities last {@link Transaction} to the given one. Used to change the {@link Transaction} undone with the /undo command.
	 * 
	 * @param trans
	 */
	public void setLastTrans(Transaction trans) {
		lastTrans = trans;
	}
	
	/**
	 * Assigns the entity the {@link Shop} given.
	 * 
	 * @param shop
	 */
	public void setShop(Shop shop) {
		this.shop = shop;
	}
	
	/**
	 * Assigns the entity the {@link User} given.
	 * 
	 * @param user
	 */
	public void setUser(User user) {
		this.user = user;
	}
	
	/**
	 * Removes the last {@link Transaction} from the transaction list. Called when the last transaction is undone.
	 * 
	 * @param trans
	 */
	public void undoTransaction(Transaction trans) {
		transactions.remove(trans);
	}
}
