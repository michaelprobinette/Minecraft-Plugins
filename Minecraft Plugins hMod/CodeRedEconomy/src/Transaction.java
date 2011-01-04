public class Transaction {
	private final EconEntity	seller, buyer;
	private final ShopItemStack	stack;
	
	public Transaction(EconEntity seller, EconEntity buyer, ShopItemStack stack) {
		this.seller = seller;
		this.buyer = buyer;
		this.stack = stack;
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
	
	/**
	 * A static way to process a transaction
	 * 
	 * @param trans
	 */
	public static void transaction(Transaction trans) {
		trans.getBuyer().setLastTrans(trans);
		trans.getSeller().setLastTrans(trans);
		
		// Give the seller the money
		trans.getSeller().money.addAmount(trans.getStack().getTotalSellPrice());
		
		// Check if an inf shop
		if (trans.getBuyer().getMoney().getAmount() != 1337) {
			// Subtract the money from the buyer
			trans.getBuyer().getMoney().addAmount(-trans.getStack().getTotalBuyPrice());
		}
		
		// Add the items to the buyer
		if (trans.getBuyer().isPlayer()) {
			// If it is a player, go ahead and give them the item
			
			// Gets the user from the DataManager user list, and returns it to let us use
			DataManager.getUser(trans.getBuyer()).getPlayer().giveItem(trans.getStack().getItemID(), trans.getStack().getAmountAvail());
		}
		else {
			// If it is not a player, add it to their availableItems list
			trans.getBuyer().addShopItems(trans.getStack());
		}
		
		// Remove the items from the seller
		if (trans.getSeller().isPlayer()) {
			// If it is a player, go ahead and give them the iter
			DataManager.getUser(trans.getSeller()).getPlayer().getInventory().removeItem(trans.getStack().getItemID(),
					trans.getStack().getAmountAvail()); // Gets the user from the DataManager user list, and returns it to let us use
		}
		else {
			// If is is not a plyaer, add it to their availableItems list
			trans.getSeller().removeShopItems(trans.getStack());
		}
	}
	
	/**
	 * @param trans
	 *            the transaction to be reversed
	 */
	public static void undoTransaction(Transaction trans) {
		// Undoes a transaction by switching all of the variables. I.E. buyer becomes seller, seller becomes buyer. Sell price is buy price,
		// buy price is sell price. Works like a charm.
		transaction(new Transaction(trans.getBuyer(), trans.getSeller(), new ShopItemStack(new ShopItem(trans.getStack().getItemID(), trans
				.getStack().getShopItem().getSellPrice(), trans.getStack().getShopItem().getBuyPrice()), trans.getStack().getAmountAvail())));
	}
}
