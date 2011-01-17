/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import org.bukkit.inventory.ItemStack;

public class Transaction {
	private final EconEntity	seller, buyer;
	private final ShopItemStack	stack;
	private final boolean		cashOnly;
	private final long			time;
	
	private Money				amount	= null;
	
	/**
	 * @param trans
	 * @return represents the status of it. 0 mean all went fine, if negative something went wrong
	 */
	public static void process(Transaction trans) {
		// call process with messages on
		process(trans, false, true);
	}
	
	public static void process(Transaction trans, boolean loud) {
		// call process with messages on
		process(trans, false, loud);
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
		EconEntity buyer = trans.getBuyer();
		EconEntity seller = trans.getSeller();
		ShopItemStack stack = trans.getStack();
		
		// Check if autoDeposit
		buyer.autoDesposit(DataManager.getServer().getTime());
		seller.autoDesposit(DataManager.getServer().getTime());
		
		if (seller.getShop() != null) {
			// Check for restock
			DataManager.getShop(seller).restock();
		}
		
		boolean canBuy = false;
		boolean canSell = false;
		try {
			canBuy = buyer.canBuy(trans.getStack());
			canSell = seller.canSell(trans.getStack());
		}
		catch (EconException e) {
			// Message the players if needed
			if (loud) {
				if (buyer.isPlayer()) {
					buyer.getUser().sendMessage(e.getBuyMsg());
				}
				if (seller.isPlayer()) {
					seller.getUser().sendMessage(e.getSellMsg());
				}
			}
		}
		if ((canBuy && canSell) || !loud) {
			if (DataManager.getDebug()) {
				System.out.println("Can buy and can sell.");
				if (!loud) {
					System.out.println("Forcing a silent transaction.");
				}
			}
			if (!trans.cashOnly() && loud) {
				buyer.setLastTrans(trans);
				seller.setLastTrans(trans);
			}
			int spent = 0;
			int earned = 0;
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
			
			//			if ((seller.getMoney().getAmount() != DataManager.getInfValue()) && !trans.cashOnly()) {
			//				// Give the seller the money
			//				seller.money.addAmount(trans.getStack().getTotalSellPrice().getAmount());
			//			}
			//			else if ((seller.getMoney().getAmount() != DataManager.getInfValue()) && trans.cashOnly()) {
			//				seller.getMoney().addAmount(trans.getAmount().getAmount());
			//			}
			
			// Check if an inf shop
			//			if ((buyer.getMoney().getAmount() != DataManager.getInfValue()) && !trans.cashOnly()) {
			//				// Subtract the money from the buyer
			//				buyer.getMoney().addAmount(-trans.getStack().getTotalBuyPrice().getAmount());
			//			}
			//			else if ((buyer.getMoney().getAmount() != DataManager.getInfValue()) && trans.cashOnly()) {
			//				buyer.getMoney().addAmount(-trans.getAmount().getAmount());
			//			}
			
			// Stats handling
			if (buyer.isPlayer()) {
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
			if (seller.isPlayer()) {
				if (undo) {
					EconStats.undoBuy(stack);
				}
				else if (trans.cashOnly()) {
					
				}
				else {
					EconStats.sold(stack);
				}
			}
			
			if (!trans.cashOnly()) {
				// Add the items to the buyer
				if (buyer.isPlayer()) {
					// If it is a player, go ahead and give them the item
					if (buyer.numberOfItems(stack.getShopItem()) != DataManager.getInfValue()) {
						buyer.getUser().addItem(stack);
					}
				}
				else {
					if (buyer.numberOfItems(stack.getShopItem()) != DataManager.getInfValue()) {
						// If it is not a player, add it to their availableItems list
						buyer.addShopItems(trans.getStack());
					}
				}
				
				// Remove the items from the seller
				if (seller.isPlayer()) {
					// If it is a player, go ahead and give them the item
					seller.getUser().removeItem(stack);
				}
			}
			else if (seller.numberOfItems(stack.getShopItem()) != DataManager.getInfValue()) {
				// If is is not a player, add it to their availableItems list
				seller.removeShopItems(trans.getStack());
			}
			
			// ///////////////////////////////////////////
			// /////////////// Messages///////////////////
			// ///////////////////////////////////////////
			if (loud) {
				if (!undo) {
					// Add it to their transaction list
					buyer.addTransaction(trans);
					seller.addTransaction(trans);
					
					// Send messages to the seller and buyer telling the success
					if (buyer.isPlayer()) {
						// EconStats.bought(stack);
						buyer.getUser().sendMessage("You bought " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName() + " for " + trans.getStack().getTotalBuyPrice().toString());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						// EconStats.sold(stack);
						seller.getUser().sendMessage("You sold " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName() + " for " + trans.getStack().getTotalSellPrice().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
				else {
					// Undo, the buyer is the person who sold the item in the transaction
					// The seller is the person who bought the item in the last transaction
					
					if (buyer.isPlayer()) {
						// EconStats.undoSell(stack);
						buyer.getUser().sendMessage("The last transaction was undone. " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName() + " has been refunded and " + trans.getStack().getTotalSellPrice().toString() + " has been removed.");
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						// EconStats.undoBuy(stack);
						seller.getUser().sendMessage("The last transaction was undone. " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName() + " has been removed and you have been refunded " + trans.getStack().getTotalBuyPrice().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
			}
			else {
				if (!undo) {
					
					// Send messages to the seller and buyer telling the success
					if (buyer.isPlayer()) {
						// EconStats.paid(trans);
						buyer.getUser().sendMessage("You have paid " + seller.getName() + " " + trans.getAmount().toString());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						// Nothing
						seller.getUser().sendMessage(buyer.getName() + " has paid you " + trans.getAmount().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
			}
			DataManager.save(); // Save on each transaction
			return true;
		}
		return false;
	}
	
	/**
	 * @param trans
	 *            the transaction to be reversed
	 */
	public static boolean undoTransaction(Transaction trans) {
		// Undoes a transaction by switching all of the variables. I.E. buyer becomes seller, seller becomes buyer. Sell price is buy price,
		// buy price is sell price. Works like a charm
		
		// Update the arrays if they are players
		if (trans.getBuyer().isPlayer()) {
			DataManager.getUser(trans.getBuyer()).updateArray();
		}
		if (trans.getSeller().isPlayer()) {
			DataManager.getUser(trans.getSeller()).updateArray();
		}
		
		if (trans.cashOnly()) {
			// Cash transaction only, check if the buyer
			if (process(new Transaction(trans.getBuyer(), trans.getSeller(), trans.getAmount()), true, true)) {
				// Succeeded
				// Remove the transaction from their lists
				trans.getSeller().undoTransaction(trans);
				trans.getBuyer().undoTransaction(trans);
				return true;
			}
		}
		else {
			// Item transaction, check if the buyer has the item, and if the seller has the money
			if (process(new Transaction(trans.getBuyer(), trans.getSeller(), new ShopItemStack(new ShopItem(trans.getStack().getItemID(), trans.getStack().getShopItem().getSellPrice(), trans.getStack().getShopItem().getBuyPrice()), trans.getStack().getAmountAvail())), true, true)) {
				// Succeeded
				// Remove the transaction from their lists
				trans.getSeller().undoTransaction(trans);
				trans.getBuyer().undoTransaction(trans);
				return true;
			}
		}
		return false;
	}
	
	public Transaction(EconEntity seller, EconEntity buyer, Money amount) {
		this.seller = seller;
		this.buyer = buyer;
		cashOnly = true;
		this.amount = amount;
		stack = null;
		time = DataManager.getServer().getTime();
	}
	
	public Transaction(EconEntity seller, EconEntity buyer, ShopItemStack stack) {
		this.seller = seller;
		this.buyer = buyer;
		this.stack = stack;
		cashOnly = false;
		time = DataManager.getServer().getTime();
	}
	
	public boolean cashOnly() {
		return cashOnly;
	}
	
	public Money getAmount() {
		return amount;
	}
	
	public EconEntity getBuyer() {
		return buyer;
	}
	
	public EconEntity getSeller() {
		return seller;
	}
	
	public ShopItemStack getStack() {
		return stack;
	}
	
	public long getTime() {
		return time;
	}
}
