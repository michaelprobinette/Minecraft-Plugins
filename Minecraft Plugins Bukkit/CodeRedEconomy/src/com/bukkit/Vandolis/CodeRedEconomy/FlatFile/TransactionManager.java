/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import java.util.ArrayList;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Mike
 */
public class TransactionManager {
	private ArrayList<Transaction>	que	= new ArrayList<Transaction>();
	
	public void add(Transaction trans) {
		que.add(trans);
		
		clean();
		process();
	}
	
	/**
	 * Removes any failed transactions from the list
	 */
	private void clean() {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @return the que
	 */
	protected ArrayList<Transaction> getQue() {
		return que;
	}
	
	/**
	 * @param que
	 *            the que to set
	 */
	protected void setQue(ArrayList<Transaction> que) {
		this.que = que;
	}
	
	/**
	 * Process all of the remaining transactions in the que
	 */
	private void process() {
		for (Transaction trans : que) {
			EconEntity seller = trans.getSeller();
			EconEntity buyer = trans.getBuyer();
			ShopItemStack stack = trans.getStack();
			
			if (DataManager.getDebug()) {
				System.out.println("Can buy and can sell. Processing...");
			}
			
			/*
			 * Variables that hold the amount of money each entity must be modified by.
			 * Spent is for the buyer.
			 * Earned is for the seller.
			 */
			int spent = 0;
			int earned = 0;
			
			/*
			 * If the transaction is not cash only
			 * then set the last transaction for the buyer and seller.
			 */
			if (!trans.cashOnly()) {
				buyer.setLastTrans(trans);
				seller.setLastTrans(trans);
			}
			
			/*
			 * If the transaction is cash only then set both the spent and earned to the amount of money.
			 * If not, then set the spent to be the buy price, and the earned to the sell price.
			 */
			if (trans.cashOnly()) {
				spent = trans.getAmount().getAmount();
				earned = trans.getAmount().getAmount();
			}
			else {
				spent = stack.getTotalBuyPrice().getAmount();
				earned = stack.getTotalSellPrice().getAmount();
			}
			
			seller.getMoney().addAmount(earned);
			buyer.getMoney().removeAmount(spent);
			
			/*
			 * Stats handling
			 */
			if (EconomyProperties.isUseSQL()) {
				EconStats.log(trans);
			}
			else {
				/*
				 * Stats handling for the buyer.
				 * Will only log the info in the stats if it is a user, if not it will ignore.
				 */
				if (buyer.getUser() != null) {
					if (trans.cashOnly()) {
						com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconStats.paid(trans);
					}
					else {
						com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconStats.bought(stack);
					}
				}
				else if (trans.cashOnly()) {
					// Silent payment to a non player
					com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconStats.paid(trans);
				}
				
				/*
				 * Stats handling for the seller.
				 * Will only log the info in the stats if it is a user, if not it will ignore.
				 */
				if (seller.getUser() != null) {
					if (trans.cashOnly()) {
						/*
						 * Don't do anything here, the seller is getting paid by the buyer.
						 * I only want to log the people that pay somebody else.
						 */
					}
					else {
						com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconStats.sold(stack);
					}
				}
			}
			
			/*
			 * Handle the items remove and add.
			 * Skip if it is a cashOnly transaction
			 */
			if (!trans.cashOnly()) {
				/*
				 * Add the items to the buyer
				 */
				if (buyer.getUser() != null) {
					/*
					 * If it is a player, go ahead and give them the item.
					 */
					buyer.getUser().addItem(stack);
				}
				else {
					/*
					 * If it is not a player, go ahead and add the item to their availableItems array.
					 */
					buyer.addShopItems(trans.getStack());
				}
				
				/*
				 * Remove the items from the seller. If they are a player, remove it from their inventory.
				 * If it is a shop, remove it from their availableItems array.
				 */
				if (seller.getUser() != null) {
					// If it is a player, go ahead and give them the item
					seller.getUser().removeItem(stack);
				}
				else {
					// If is is not a player, add it to their availableItems list
					seller.removeShopItems(trans.getStack());
				}
				
				/*
				 * First, normal transaction messages
				 */
				if (!trans.isUndo()) {
					/*
					 * Add it to the sellers and the buyers transaction list for keeping tally of maxBuy and maxSell
					 */
					buyer.addTransaction(trans);
					seller.addTransaction(trans);
					
					/*
					 * Send messages to the seller and buyer telling the success if they are players
					 */
					if (buyer.getUser() != null) {
						// EconStats.bought(stack);
						buyer.getUser().sendMessage("You bought " + trans.getStack() + " for " + trans.getStack().getTotalBuyPrice());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney());
					}
					
					if (seller.getUser() != null) {
						// EconStats.sold(stack);
						seller.getUser().sendMessage("You sold " + trans.getStack() + " for " + trans.getStack().getTotalSellPrice());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney());
					}
				}
				else {
					/*
					 * Last, undone transaction messages
					 * The buyer is paying the seller
					 */
					if (buyer.getUser() != null) {
						// EconStats.undoSell(stack);
						buyer.getUser().sendMessage(
								"The last transaction was undone. " + trans.getStack() + " has been refunded and "
										+ trans.getStack().getTotalBuyPrice() + " has been removed.");
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney());
					}
					
					if (seller.getUser() != null) {
						// EconStats.undoBuy(stack);
						seller.getUser().sendMessage(
								"The last transaction was undone. " + trans.getStack() + " has been removed and you have been refunded "
										+ trans.getStack().getTotalSellPrice());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney());
					}
				}
			}
		}
	}
}
