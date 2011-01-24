/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

/**
 * Class that handles any sort of economy action. Processes {@link Transaction} by adding/removing money/items
 * 
 * @author Vandolis
 */
public class Transaction {
	private final EconEntity	seller;
	private final EconEntity	buyer;
	private ShopItemStack		stack;
	private final boolean		cashOnly;
	private final long			time;
	private Money				amount	= null;
	
	/**
	 * Cash Only {@link Transaction} constructor.
	 * 
	 * @param seller
	 * @param buyer
	 * @param amount
	 */
	public Transaction(EconEntity seller, EconEntity buyer, Money amount) {
		this.seller = seller;
		this.buyer = buyer;
		cashOnly = true;
		this.amount = amount;
		stack = null;
		time = DataManager.getServer().getTime();
	}
	
	/**
	 * Normal Item {@link Transaction} constructor.
	 * 
	 * @param seller
	 * @param buyer
	 * @param stack
	 */
	public Transaction(EconEntity seller, EconEntity buyer, ShopItemStack stack) {
		this.seller = seller;
		this.buyer = buyer;
		this.stack = stack;
		cashOnly = false;
		time = DataManager.getServer().getTime();
	}
	
	/**
	 * Old and easy way to have a {@link Transaction} processed. Calls the full process function with the standard values.
	 * 
	 * @param trans
	 * @return represents the status of it. 0 mean all went fine, if negative something went wrong
	 */
	public static boolean process(Transaction trans) {
		// call process with messages on
		return process(trans, false, true);
	}
	
	/**
	 * Calls a process of the transaction with the given loud value. If loud is false, then the transaction will be forced and no messages
	 * will be sent. Used by the badWord function.
	 * 
	 * @param trans
	 * @param loud
	 */
	public static boolean process(Transaction trans, boolean loud) {
		// call process with messages on
		return process(trans, false, loud);
	}
	
	/**
	 * For player to player payment the buyer pays the seller
	 * 
	 * @param trans
	 * @param flag
	 *            Undo trans or not. Changes the messages shown to the players
	 * @return
	 */
	public static boolean process(Transaction trans, boolean undo, boolean loud) {
		/*
		 * Grab all of the transactions info for easy use
		 */
		EconEntity buyer = trans.getBuyer();
		EconEntity seller = trans.getSeller();
		ShopItemStack stack = trans.getStack();
		
		/*
		 * Used to see if the transaction can proceed.
		 */
		boolean canBuy = false;
		boolean canSell = false;
		
		/*Check if they need to autoDeposit*/
		buyer.autoDesposit(DataManager.getServer().getTime());
		seller.autoDesposit(DataManager.getServer().getTime());
		
		/*
		 * Check if the seller is a shop, if it is check if it needs to restock
		 */
		if (seller.getShop() != null) {
			DataManager.getShop(seller).restock();
		}
		
		/*
		 * See if the buyer can buy and if the seller can sell. 
		 * If not then an exception will be thrown and will be handled.
		 */
		try {
			if (!trans.cashOnly()) {
				/*
				 * Check the buyer and seller, store the results in the check boolean
				 */
				canSell = seller.canSell(trans.getStack());
				canBuy = buyer.canBuy(trans.getStack());
			}
			else {
				canBuy = buyer.canBuy(trans.getAmount());
				canSell = true;
			}
		}
		catch (EconException e) {
			if (DataManager.getDebug()) {
				System.out.println("EconException caught.");
			}
			
			/*
			 * Message the entities if it is a loud transaction
			 */
			if (loud) {
				if (buyer.getUser() != null) {
					buyer.getUser().sendMessage(e.getBuyMsg());
				}
				if (seller.getUser() != null) {
					seller.getUser().sendMessage(e.getSellMsg());
				}
			}
			
			/*
			 * Check to see if the stack attached to the exception is null. If it is not null, 
			 * then we need to re-process the transaction with the new stack
			 */
			if (e.getStack() != null) {
				if (DataManager.getDebug()) {
					System.out.println("Stack is not null, reprocessing.");
				}
				
				/*
				 * Set the transactions stack to the one supplied by the exception, then re-process it
				 */
				trans.setStack(e.getStack());
				process(trans, undo, loud);
			}
		}
		
		/*
		 * If they can buy and sell go ahead and process. 
		 * If the transaction is forced, skip the check altogether
		 */
		if ((canBuy && canSell) || !loud) {
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
			 * Print the debug stuff to the console
			 */
			if (DataManager.getDebug()) {
				System.out.println("Can buy and can sell.");
				if (!loud) {
					System.out.println("Forcing a silent transaction.");
				}
			}
			
			/*
			 * If the transaction is not cash only and is loud (Not a forced transaction) 
			 * then set the last transaction for the buyer and seller.
			 */
			if (!trans.cashOnly() && loud) {
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
			 * Stats handling for the buyer.
			 * Will only log the info in the stats if it is a user, if not it will ignore.
			 */
			if (buyer.getUser() != null) {
				if (undo) {
					EconStats.undoSell(stack);
				}
				else if (trans.cashOnly()) {
					EconStats.paid(trans);
				}
				else {
					EconStats.bought(stack);
				}
			}
			else if (!loud && trans.cashOnly()) {
				// Silent payment to a non player
				EconStats.paid(trans);
			}
			
			/*
			 * Stats handling for the seller.
			 * Will only log the info in the stats if it is a user, if not it will ignore.
			 */
			if (seller.getUser() != null) {
				if (undo) {
					EconStats.undoBuy(stack);
				}
				else if (trans.cashOnly()) {
					/*
					 * Don't do anything here, the seller is getting paid by the buyer.
					 * I only want to log the people that pay somebody else.
					 */
				}
				else {
					EconStats.sold(stack);
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
				 * Messages to the players
				 */
				if (loud) {
					/*
					 * First, normal transaction messages
					 */
					if (!undo) {
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
									"The last transaction was undone. " + trans.getStack()
											+ " has been removed and you have been refunded " + trans.getStack().getTotalSellPrice());
							seller.getUser().sendMessage("Your new balance is: " + seller.getMoney());
						}
					}
				}
			}
			else {
				/*
				 * Cash only transaction messages
				 */
				if (!undo) {
					/*
					 * First, normal transaction messages
					 * Send messages to the seller and buyer telling the success
					 */
					if (buyer.getUser() != null) {
						// EconStats.paid(trans);
						buyer.getUser().sendMessage("You have paid " + seller.getName() + " " + trans.getAmount());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney());
					}
					if (seller.getUser() != null) {
						// Nothing
						seller.getUser().sendMessage(buyer.getName() + " has paid you " + trans.getAmount());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney());
					}
				}
			}
			
			/*
			 * Save on each transaction
			 */
			DataManager.save();
			return true;
		}
		return false;
	}
	
	/**
	 * Sets the transactions {@link ShopItemStack} to the given stack.
	 * 
	 * @param stack2
	 */
	private void setStack(ShopItemStack stack2) {
		stack = stack2;
	}
	
	/**
	 * Used to undo a {@link Transaction}. Undoes a transaction by switching all of the variables. I.E. buyer becomes seller, seller becomes
	 * buyer. Sell price is buy price, buy price is sell price. Works like a charm.
	 * 
	 * @param trans
	 *            the transaction to be reversed
	 */
	public static boolean undoTransaction(Transaction trans) {
		if (DataManager.getDebug()) {
			System.out.println("Undoing a transaction...");
		}
		
		/*
		 * Update the arrays if they are players
		 */
		if (trans.getBuyer().getUser() != null) {
			DataManager.getUser(trans.getBuyer().getName()).updateArray();
		}
		if (trans.getSeller().getUser() != null) {
			DataManager.getUser(trans.getSeller().getName()).updateArray();
		}
		
		/*
		 * Re-processing an inverted transaction. Don't allow a cash only transaction to be undone.
		 * Leaving it commented out in case I want it back in the future.
		 */
		if (trans.cashOnly()) {
			//			if (process(new Transaction(trans.getBuyer(), trans.getSeller(), trans.getAmount()), true, true)) {
			//				// Succeeded
			//				// Remove the transaction from their lists
			//				trans.getSeller().undoTransaction(trans);
			//				trans.getBuyer().undoTransaction(trans);
			//				return true;
			//			}
		}
		else {
			/*
			 * Item transaction. Invert all of the values and send it to be processed.
			 */
			if (process(new Transaction(trans.getBuyer(), trans.getSeller(), new ShopItemStack(trans.getStack().getItemId(), trans
					.getStack().getBuyPrice(), trans.getStack().getSellPrice(), trans.getStack().getAmountAvail())), true, true)) {
				/*
				 * Succeeded
				 * Remove the transaction from their lists for keeping tally of maxBuy and maxSell
				 */
				trans.getSeller().undoTransaction(trans);
				trans.getBuyer().undoTransaction(trans);
				
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the boolean of whether or not it is a cash only {@link Transaction}.
	 * 
	 * @return
	 */
	public boolean cashOnly() {
		return cashOnly;
	}
	
	/**
	 * Gets the amount of {@link Money} for a cash only {@link Transaction}.
	 * 
	 * @return
	 */
	public Money getAmount() {
		return amount;
	}
	
	/**
	 * Returns the buyer {@link EconEntity}.
	 * 
	 * @return
	 */
	public EconEntity getBuyer() {
		return buyer;
	}
	
	/**
	 * Returns the seller {@link EconEntity}.
	 * 
	 * @return
	 */
	public EconEntity getSeller() {
		return seller;
	}
	
	/**
	 * Returns the {@link ShopItemStack} if the {@link Transaction} is an item transaction. Null if not an item transaction.
	 * 
	 * @return
	 */
	public ShopItemStack getStack() {
		return stack;
	}
	
	/**
	 * Returns the time the {@link Transaction} occurred.
	 * 
	 * @return
	 */
	public long getTime() {
		return time;
	}
}
