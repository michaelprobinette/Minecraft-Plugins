public class Transaction {
	private final EconEntity	seller, buyer;
	private final ShopItemStack	stack;
	private final boolean		cashOnly;
	private Money				amount	= null;
	
	public Transaction(EconEntity seller, EconEntity buyer, ShopItemStack stack) {
		this.seller = seller;
		this.buyer = buyer;
		this.stack = stack;
		cashOnly = false;
	}
	
	public Transaction(EconEntity seller, EconEntity buyer, Money amount) {
		this.seller = seller;
		this.buyer = buyer;
		cashOnly = true;
		this.amount = amount;
		stack = null;
	}
	
	public EconEntity getSeller() {
		return seller;
	}
	
	public EconEntity getBuyer() {
		return buyer;
	}
	
	public ShopItemStack getStack() {
		return stack;
	}
	
	public boolean cashOnly() {
		return cashOnly;
	}
	
	public Money getAmount() {
		return amount;
	}
	
	/**
	 * @param trans
	 * @param flag
	 *            Undo trans or not. Changes the messages shown to the players
	 * @return
	 */
	public static int process(Transaction trans, boolean undo) {
		EconEntity buyer = trans.getBuyer();
		EconEntity seller = trans.getSeller();
		ShopItemStack stack = trans.getStack();
		boolean canBuy = buyer.canBuy(trans.getStack());
		boolean canSell = seller.canSell(trans.getStack());
		if (canBuy && canSell) {
			buyer.setLastTrans(trans);
			seller.setLastTrans(trans);
			
			if (seller.getMoney().getAmount() != DataManager.getInfValue() && !trans.cashOnly()) {
				// Give the seller the money
				seller.money.addAmount(trans.getStack().getTotalSellPrice().getAmount());
			}
			else if (seller.getMoney().getAmount() != DataManager.getInfValue() && trans.cashOnly()) {
				seller.getMoney().addAmount(trans.getAmount().getAmount());
			}
			
			// Check if an inf shop
			if (buyer.getMoney().getAmount() != DataManager.getInfValue() && !trans.cashOnly()) {
				// Subtract the money from the buyer
				buyer.getMoney().addAmount(-trans.getStack().getTotalBuyPrice().getAmount());
			}
			else if (buyer.getMoney().getAmount() != DataManager.getInfValue() && trans.cashOnly()) {
				buyer.getMoney().addAmount(-trans.getAmount().getAmount());
			}
			
			if (!trans.cashOnly()) {
				// Add the items to the buyer
				if (buyer.isPlayer()) {
					// If it is a player, go ahead and give them the item
					if (buyer.numberOfItems(stack.getShopItem()) != DataManager.getInfValue()) {
						// Gets the user from the DataManager user list, and returns it to let us use
						buyer.getUser().getPlayer().giveItem(trans.getStack().getItemID(), trans.getStack().getAmountAvail());
						// DataManager.getUser(buyer).getPlayer().giveItem(trans.getStack().getItemID(),
						// trans.getStack().getAmountAvail());
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
					// If it is a player, go ahead and give them the iter
					
					// Gets the user from the DataManager user list, and returns it to let us use
					seller.getUser().getPlayer().getInventory().removeItem(trans.getStack().getItemID(), trans.getStack().getAmountAvail());
					// DataManager.getUser(seller).getPlayer().getInventory().removeItem(trans.getStack().getItemID(),
					// trans.getStack().getAmountAvail());
				}
				else if (seller.numberOfItems(stack.getShopItem()) != DataManager.getInfValue()) {
					// If is is not a player, add it to their availableItems list
					seller.removeShopItems(trans.getStack());
				}
				
				// ///////////////////////////////////////////
				// /////////////// Messages///////////////////
				// ///////////////////////////////////////////
				
				if (!undo) {
					// Send messages to the seller and buyer telling the success
					if (buyer.isPlayer()) {
						buyer.getUser().sendMessage(
								"You bought " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName()
										+ " for " + trans.getStack().getTotalBuyPrice().toString());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						seller.getUser().sendMessage(
								"You sold " + trans.getStack().getAmountAvail() + " " + trans.getStack().getShopItem().getName() + " for "
										+ trans.getStack().getTotalSellPrice().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
				else {
					if (buyer.isPlayer()) {
						buyer.getUser().sendMessage(
								"The last transaction was undone. " + trans.getStack().getAmountAvail() + " "
										+ trans.getStack().getShopItem().getName() + " has been refunded and "
										+ trans.getStack().getTotalSellPrice().toString() + " has been removed.");
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						seller.getUser().sendMessage(
								"The last transaction was undone. " + trans.getStack().getAmountAvail() + " "
										+ trans.getStack().getShopItem().getName() + " has been removed and you have been refunded "
										+ trans.getStack().getTotalBuyPrice().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
			}
			else {
				if (!undo) {
					// Send messages to the seller and buyer telling the success
					if (buyer.isPlayer()) {
						buyer.getUser().sendMessage("You have paid " + seller.getName() + " " + trans.getAmount().toString());
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						seller.getUser().sendMessage(buyer.getName() + " has paid you " + trans.getAmount().toString());
						seller.getUser().sendMessage("Your new balance is: " + seller.getMoney().toString());
					}
				}
				else {
					if (buyer.isPlayer()) {
						buyer.getUser()
								.sendMessage(
										"The last transaction was undone. " + trans.getAmount().toString()
												+ " has been removed from your account.");
						buyer.getUser().sendMessage("Your new balance is: " + buyer.getMoney().toString());
					}
					if (seller.isPlayer()) {
						seller.getUser().sendMessage(
								"The last transaction was undone. " + trans.getAmount().toString() + " has been refunded to your account.");
					}
				}
			}
			return 0; // Return 0 as it succeeded
		}
		// From here on handle problems with the transaction, telling players what went wrong.
		else if (trans.cashOnly()) {
			if (!canBuy && !canSell) {
				if (buyer.isPlayer()) {
					buyer.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				if (seller.isPlayer()) {
					seller.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				return -3; // False because it failed
			}
			else if (!canBuy) {
				if (buyer.isPlayer()) {
					buyer.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				if (seller.isPlayer()) {
					seller.getUser().sendMessage(buyer.getName() + " does not have enough " + Money.getMoneyName());
				}
				return -1;
			}
			else if (!canSell) {
				if (seller.isPlayer()) {
					seller.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				if (buyer.isPlayer()) {
					buyer.getUser().getPlayer().sendMessage(
							seller.getName() + " does not have enough " + Money.getMoneyName() + " (" + seller.getMoney().getAmount() + "/"
									+ trans.getAmount().getAmount() + ")");
				}
				return -2;
			}
		}
		else {
			if (!canBuy && !canSell) {
				if (buyer.isPlayer()) {
					buyer.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				if (seller.isPlayer()) {
					seller.getUser().sendMessage(
							"You do not have enough " + trans.getStack().getShopItem().getName() + " ("
									+ seller.numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
				}
				return -3; // False because it failed
			}
			else if (!canBuy) {
				if (buyer.isPlayer()) {
					buyer.getUser().sendMessage("You do not have enough " + Money.getMoneyName());
				}
				if (seller.isPlayer()) {
					seller.getUser().sendMessage(buyer.getName() + " does not have enough " + Money.getMoneyName());
				}
				return -1;
			}
			else if (!canSell) {
				if (seller.isPlayer()) {
					seller.getUser().sendMessage(
							"You do not have enough " + trans.getStack().getShopItem().getName() + " ("
									+ seller.numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
				}
				if (buyer.isPlayer()) {
					buyer.getUser().getPlayer().sendMessage(
							seller.getName() + " does not have enough " + stack.getShopItem().getName() + " ("
									+ seller.numberOfItems(stack.getShopItem()) + "/" + stack.getAmountAvail() + ")");
				}
				return -2;
			}
		}
		return 1;
	}
	
	/**
	 * @param trans
	 * @return represents the status of it. 0 mean all went fine, if negative something went wrong
	 */
	public static int process(Transaction trans) {
		// call process with messages on
		return process(trans, false);
	}
	
	/**
	 * @param trans
	 *            the transaction to be reversed
	 */
	public static Transaction undoTransaction(Transaction trans) {
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
			process(new Transaction(trans.getBuyer(), trans.getSeller(), trans.getAmount()), true);
			return null;
		}
		else {
			// Item transaction, check if the buyer has the item, and if the seller has the money
			process(new Transaction(trans.getBuyer(), trans.getSeller(), new ShopItemStack(new ShopItem(trans.getStack().getItemID(), trans
					.getStack().getShopItem().getSellPrice(), trans.getStack().getShopItem().getBuyPrice()), trans.getStack()
					.getAmountAvail())), true);
			return null;
		}
	}
}
