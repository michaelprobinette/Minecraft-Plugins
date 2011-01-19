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

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public abstract class EconEntity {
	protected Money						money			= new Money();
	protected ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	protected String					name			= "";
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
	 * Makes a default entity with the given {@link Money}.
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
	 * Adds the given {@link Transaction} to the entities array of transactions. Increases the correct numTransBuy/Sell based on which the
	 * entity is.
	 * 
	 * @param trans
	 */
	public void addTransaction(Transaction trans) {
		transactions.add(trans);
		if (trans.getBuyer().getName().equalsIgnoreCase(name)) {
			numTransBuy++;
		}
		else {
			numTransSell++;
		}
	}
	
	/**
	 * Used to add money to the entity on a given interval. Send it the current server time.
	 * 
	 * @param serverTime
	 */
	public void autoDesposit(long serverTime) {
		if ((serverTime - lastAutoDeposit >= DataManager.getAutoDepositTime()) && (money.getAmount() != DataManager.getInfValue())) {
			if (DataManager.getDebug()) {
				System.out.println("Auto Depositing " + DataManager.getAutoDepositAmount() + " into " + name + " Server: " + serverTime
						+ " Last: " + lastAutoDeposit);
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
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can buy " + stack.getAmountAvail() + " " + stack.getName());
			}
		}
		if (stack != null) {
			/*Item transaction*/
			if (user != null) {
				/*Player*/

				/*
				 * Check to see if they are allowed to buy the item
				 * Will throw an exception if it fails
				 */
				checkAllowed(stack);
				
				/* 
				 * Check to see if the player can buy that many more before hitting the buy cap.
				 * Will throw an exception if it fails
				 */
				checkMaxBuy(stack);
				
				/* 
				 * Check to see if they have enough empty slots to hold the items
				 * Will throw an exception if it fails
				 */
				checkEnoughSlots(stack);
				
				/* Return if the player has the money*/
				return (money.getAmount() >= stack.getTotalBuyPrice().getAmount());
			}
			else {
				/*Shop*/

				/*Check the money*/
				return (money.getAmount() >= stack.getTotalBuyPrice().getAmount());
			}
		}
		
		return true;
	}
	
	/**
	 * Checks the entities {@link ShopGroup} to see if they are allowed to buy that item.
	 * 
	 * @param stack
	 * @throws EconException
	 */
	private void checkAllowed(ShopItemStack stack) throws EconException {
		// FIXME Need to change once bukkit implements a permissions system
		//				
		// canBuy = ((user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getItemID()))) && ((stack
		// .getTotalBuyPrice().getAmount() <= money.getAmount()) || (money.getAmount() == DataManager.getInfValue())));
		// if (!(user.getPlayer().isInGroup(DataManager.getReqGroup(stack.getItemID())))) {
		// throw new EconException("You are not allowed to purchase this item.", name + " is not allowed to purchase this item.");
		// }
	}
	
	/**
	 * Called if the entity is a {@link User}. Checks their inventory to see if the full amount will fit.
	 * 
	 * @param stack
	 */
	private void checkEnoughSlots(ShopItemStack stack) throws EconException {
		int available = 0; // The amount the player can hold
		for (ItemStack iter : user.getPlayer().getInventory().getContents()) {
			if (iter.getTypeId() == stack.getItemId()) {
				// Same item type check how much more the stack can hold
				available += (64 - iter.getAmount());
			}
			else if (iter.getType().equals(Material.AIR)) {
				// Empty slot
				available += 64;
			}
		}
		if (available < stack.getAmountAvail()) {
			if (available != 0) {
				throw new EconException("You do not have enough space for " + stack.getAmountAvail() + " " + stack.getName() + ".", name
						+ " does not have enough space for " + stack.getAmountAvail() + " " + stack.getName() + ".", new ShopItemStack(
						stack.getItemId(), available));
			}
			else {
				throw new EconException("You do not have enough space for " + stack.getAmountAvail() + " " + stack.getName() + ".", name
						+ " does not have enough space for " + stack.getAmountAvail() + " " + stack.getName() + ".");
			}
		}
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
		if (DataManager.getDebug()) {
			if (stack != null) {
				System.out.println("Checking to see if " + name + " can sell " + stack.getAmountAvail() + " " + stack.getName());
			}
		}
		if (stack != null) {
			/*Item transaction*/
			if (user != null) {
				/*Player*/

				/*
				 * Check to see if they are allowed to buy the item
				 * Will throw an exception if it fails
				 */
				checkAllowed(stack);
				
				/* 
				 * Check to see if the player can buy that many more before hitting the buy cap.
				 * Will throw an exception if it fails
				 */
				checkMaxSell(stack);
				
				/*Make sure that this is not a cash only*/
				if (stack != null) {
					/* 
					 * Check to see if they have the required items.
					 * Will throw an exception if it fails
					 */
					checkHasItems(stack);
				}
			}
			else {
				/*Shop*/

				/*Make sure that this is not a cash only*/
				if (stack != null) {
					/* 
					 * Check to see if they have the required items.
					 * Will throw an exception if it fails
					 */
					checkHasItems(stack);
				}
			}
		}
		/*Reached this far, no exception thrown so return true*/
		return true;
		
		//		if (DataManager.getDebug()) {
		//			if (stack != null) {
		//				System.out.println("Checking to see if " + name + " can sell " + stack.getAmountAvail() + " "
		//						+ stack.getName());
		//			}
		//		}
		//		boolean hasItems = false;
		//		boolean soldLessThanMax = true;
		//		if (stack != null) {
		//			// Check if the entity is a player
		//			if (user != null) {
		//				user.updateArray();
		//			}
		//			ShopItemStack sis = getStack(availableItems, stack);
		//			if (sis != null) {
		//				hasItems = (stack.getAmountAvail() <= sis.getAmountAvail());
		//				if (sis.getAmountAvail() == -1) {
		//					hasItems = true;
		//				}
		//			}
		//		}
		//		else {
		//			hasItems = true;
		//		}
		//		
		//		if (!hasItems) {
		//			if (stack.getItemID() != 0) {
		//				throw new EconException(name + " does not have enough " + stack.getName(), "You do not have enough "
		//						+ stack.getName() + " (" + numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
		//			}
		//			else {
		//				throw new EconException(name + " does not sell that item.", "You can't sell that item.");
		//			}
		//		}
		//		
		//		if ((user != null) && (stack != null)) {
		//			// Only checks if it is a user selling
		//			soldLessThanMax = checkMaxSell(stack);
		//		}
		//		
		//		return (hasItems && soldLessThanMax);
	}
	
	/**
	 * Checks to see if the entity has the required items. Throws an {@link EconException}
	 * 
	 * @param stack
	 * @throws EconException
	 */
	private void checkHasItems(ShopItemStack stack) throws EconException {
		/*If a user, update the array*/
		if (user != null) {
			user.updateArray();
		}
		/*Amount the player currently has*/
		int current = getStack(availableItems, stack).getAmountAvail();
		if (current < stack.getAmountAvail()) {
			/*
			 * Check to make sure it is not air, as that would mean it is not an item that can be sold or bought
			 */
			if (stack.getItemId() != 0) {
				/*
				 * Not enough of the item, throw an exception with the correct messages and with a new stack size of the max the entity has
				 */
				throw new EconException(name + " does not have enough " + stack.getName(), "You do not have enough " + stack.getName()
						+ " (" + numberOfItems(stack) + "/" + stack.getAmountAvail() + ")", new ShopItemStack(stack.getItemId(), current));
			}
			else {
				/*
				 * Can't sell that item, not on the list.
				 */
				throw new EconException(name + " does not sell that item.", "You can't sell that item.");
			}
		}
	}
	
	/**
	 * Checks to see if the entity can buy the given amount of items based on the {@link ShopItem} maxBuy. Throws an {@link EconException}
	 * if they can't buy that many.
	 * 
	 * @param stack
	 * @throws EconException
	 */
	private void checkMaxBuy(ShopItemStack stack) throws EconException {
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max buy for " + stack.getName());
		}
		int totBought = 0;
		for (Transaction iter : transactions) {
			// If the buyer is this entity and it is an item transaction, check the item id
			if (iter.getBuyer().getName().equalsIgnoreCase(name) && (iter.getStack() != null)) {
				if (iter.getStack().getItemId() == stack.getItemId()) {
					// Same item id, add it to the total bought
					totBought += iter.getStack().getAmountAvail();
				}
			}
		}
		if ((totBought + stack.getAmountAvail() > stack.getMaxBuy()) && (stack.getMaxBuy() != DataManager.getInfValue())) {
			// Print debug info to console
			if (DataManager.getDebug()) {
				System.out.println(name + " can only buy " + (stack.getMaxBuy() - totBought) + " more " + stack.getName() + ".");
			}
			
			// Exception handling
			if (totBought != stack.getMaxBuy()) {
				throw new EconException("You can only buy " + (stack.getMaxBuy() - totBought) + " more " + stack.getName(), name
						+ " has can't buy that many " + stack.getName());
			}
			else {
				throw new EconException("You can't buy anymore " + stack.getName(), name + " can't buy anymore " + stack.getName());
			}
		}
	}
	
	/**
	 * Checks to see if the entity can sell the given amount of items based on the {@link ShopItem} maxSell. Throws an {@link EconException}
	 * if they can't buy that many.
	 * 
	 * @param stack
	 * @throws EconException
	 */
	private void checkMaxSell(ShopItemStack stack) throws EconException {
		// Print debug info to console
		if (DataManager.getDebug()) {
			System.out.println("Checking " + name + " for the max sell for " + stack.getName());
		}
		
		int totSold = 0; // A count of how many of the item the entity has already sold
		
		for (Transaction iter : transactions) {
			if (iter.getSeller().getName().equalsIgnoreCase(name) && (iter.getStack() != null)) {
				if (iter.getStack().getItemId() == stack.getItemId()) {
					// Same itemId, add it to the totSold count
					totSold += iter.getStack().getAmountAvail();
				}
			}
		}
		if ((totSold + stack.getAmountAvail() > stack.getMaxSell()) && (stack.getMaxSell() != DataManager.getInfValue())) {
			// Print debug info to console
			if (DataManager.getDebug()) {
				System.out.println(name + " can only sell " + (stack.getMaxSell() - totSold) + " more " + stack.getName() + ".");
			}
			
			// Exception handling
			if (totSold != stack.getMaxSell()) {
				throw new EconException(name + " can't buy that many " + stack.getName(), "You can only sell "
						+ (stack.getMaxSell() - totSold) + " more " + stack.getName());
			}
			else {
				throw new EconException(name + " can't sell anymore " + stack.getName(), "You can't sell anymore " + stack.getName());
			}
		}
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
			if (iter.getItemId() == stack.getItemId()) {
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
					if (iter.getStack().getItemId() == itemID) {
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
	 * @deprecated
	 * @param stack
	 * @return
	 */
	@Deprecated
	public boolean hasItems(ShopItemStack stack) {
		if (user != null) {
			user.updateArray(); // Grab the items from the players inventory
		}
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemId() == stack.getItemId()) {
				if ((iter.getAmountAvail() >= stack.getAmountAvail()) || (iter.getAmountAvail() == DataManager.getInfValue())) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * Returns the number of items in the availableItems array. If the entity is a player it will also update the array before checking.
	 * 
	 * @param item
	 * @return
	 */
	public int numberOfItems(ShopItem item) {
		if (user != null) {
			user.updateArray(); // Grab the items from the players inventory
		}
		ShopItemStack sis = getStack(availableItems, new ShopItemStack(item.getItemId(), 1));
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
	 * Removes the last {@link Transaction} from the transaction list. Called when the last transaction is undone. Also removes from the
	 * correct numTransBuy/Sell.
	 * 
	 * @param trans
	 */
	public void undoTransaction(Transaction trans) {
		transactions.remove(trans);
		if (trans.getBuyer().getName().equalsIgnoreCase(name)) {
			numTransBuy--;
		}
		else {
			numTransSell--;
		}
	}
	
	/**
	 * Returns the Entities number of buy {@link Transaction}.
	 * 
	 * @return
	 */
	public int getNumTransactionsBuy() {
		return numTransBuy;
	}
	
	/**
	 * Returns the entities number of sell {@link Transaction}.
	 * 
	 * @return
	 */
	public int getNumTransactionsSell() {
		return numTransSell;
	}
}
