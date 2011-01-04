import java.util.ArrayList;

public class User extends EconEntity {
	private Player	player		= null;
	private String	groupName	= "";
	
	/**
	 * New player
	 * 
	 * @param player
	 */
	public User(Player player) {
		super();
		this.player = player;
		name = player.getName();
		if (player.getGroups().length >= 1) {
			groupName = player.getGroups()[0];
		}
		else
			groupName = "nogroup";
		
		isPlayer = true;
		DataManager.addUser(this);
	}
	
	/**
	 * Used for loading from the file
	 * 
	 * @param user
	 */
	public User(User user) {
		super(user.getMoney());
		this.player = user.getPlayer();
		name = user.getPlayer().getName();
	}
	
	/**
	 * @param saveString
	 */
	public User(String saveString) {
		// Split it and grab the data
		String split[] = saveString.split(":");
		if (split.length >= 2) {
			name = split[0];
			int temp = Integer.valueOf(split[1]);
			money.setAmount(temp);
		}
	}
	
	/**
	 * 
	 */
	public User() {
		super();
	}
	
	/**
	 * @param player
	 */
	public void setPlayer(Player player) {
		this.player = player;
		if (player.getGroups().length >= 1) {
			groupName = player.getGroups()[0];
		}
		else
			groupName = "nogroup";
		
		isPlayer = true;
	}
	
	/**
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * @param item
	 * @return
	 */
	public boolean canBuy(ShopItem item) {
		if (item.getBuyPrice() > money.getAmount()) {
			return false;
		}
		
		// else if (item.getPrivLevel() > privLevel) {
		// return false;
		// }
		return true;
	}
	
	/**
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	/* (non-Javadoc)
	 * @see EconEntity#getMoney()
	 */
	public Money getMoney() {
		return money;
	}
	
	/**
	 * @param trans
	 * @return
	 */
	// public boolean buy(Transaction trans) {
	// money.removeAmount(trans.getStack().getTotalBuyPrice());
	// if (money.isValid()) {
	// // Take care of the seller
	// trans.getSeller().recieveMoney(new Money(trans.getStack().getTotalBuyPrice()));
	// for (ShopItemStack iter : trans.getSeller().getAvailItems()) {
	// if (iter.getItemID() == trans.getStack().getItemID()) {
	// if (iter.getAmountAvail() != -1337) {
	// // Remove the items
	// iter.addAmountAvail(-trans.getStack().getAmountAvail());
	// }
	// }
	// }
	//			
	// availableItems.add(trans.getStack());
	// transferFromArray();
	// // player.giveItem(trans.getStack().getShopItem().getItemID(), trans.getStack().getAmountAvail());
	// lastTrans = trans;
	// return true;
	// }
	// else {
	// money.addAmount(trans.getStack().getTotalBuyPrice());
	// return false;
	// }
	// }
	
	/**
	 * @param shopItemStack
	 * @return
	 */
	public boolean canBuy(ShopItemStack shopItemStack) {
		if ((player.isInGroup(DataManager.getReqGroup(shopItemStack.getShopItem().getItemID())))
				&& shopItemStack.getTotalBuyPrice() <= money.getAmount()) {
			return true;
		}
		// else if (shopItemStack.getShopItem().getPrivLevel() < privLevel) {
		// return false;
		// }
		return false;
	}
	
	/**
	 * 
	 */
	public void undoLastTrans() {
		if (lastTrans != null) {
			if (lastTrans.getBuyer().isPlayer()) {
				DataManager.getUser(lastTrans.getBuyer().getName()).updateArray();
			}
			if (lastTrans.getBuyer().hasItems(lastTrans.getStack())) {
				sendMessage("Undoing last transaction.");
				
				Transaction.undoTransaction(lastTrans);
				
				lastTrans = null;
				sendMessage("Your new balance is: " + money.toString());
			}
			else {
				sendMessage("You do not have the items.");
			}
		}
		else {
			sendMessage("There is no last transaction to undo.");
		}
	}
	
	// /**
	// * @param stack
	// */
	// private void removeFromInv(ShopItemStack stack) {
	// int countRemoved = 0;
	// for (Item iter : player.getInventory().getContents()) {
	// if (iter != null) {
	// if (iter.getItemId() == stack.getItemID()) {
	// int amountPresent = iter.getAmount();
	// // If there is more than what still needs to be removed in this one stack
	// if (amountPresent >= stack.getAmountAvail() - countRemoved) {
	// iter.setAmount(iter.getAmount() - stack.getAmountAvail() - countRemoved);
	// countRemoved = stack.getAmountAvail() - countRemoved;
	// }
	// else {
	// countRemoved = iter.getAmount();
	// iter.setAmount(0);
	// }
	// }
	// }
	// }
	// if (countRemoved != stack.getAmountAvail()) {
	// // Did not remove everything, check hotbar
	//			
	// }
	// }
	
	public void updateArray() {
		availableItems = new ArrayList<ShopItemStack>();
		for (Item iter : player.getInventory().getContents()) {
			if (iter != null) {
				if (iter.getItemId() > 0) {
					// Check if it is already in the array
					boolean found = false;
					for (ShopItemStack siter : availableItems) {
						if (siter.getItemID() == iter.getItemId()) {
							// Already in available items, so add to the amount
							siter.addAmountAvail(iter.getAmount());
							found = true;
						}
					}
					// Add it to the array
					if (!found) {
						availableItems.add(new ShopItemStack(new ShopItem(iter.getItemId()), iter.getAmount()));
					}
				}
			}
		}
	}
	
	/**
	 * Takes the items from the available items and adds it to the users inv
	 */
	public void transferFromArray() {
		
		for (ShopItemStack iter : availableItems) {
			System.out.println("Amount adding is: " + iter.getAmountAvail());
			if (iter.getAmountAvail() != 0) {
				player.giveItem(iter.getItemID(), iter.getAmountAvail());
			}
			// player.giveItem(new Item(iter.getItemID(), iter.getAmountAvail()));
			// player.giveItem(iter.getItemID(), iter.getAmountAvail());
		}
	}
	
	/**
	 * 
	 */
	public void showBalance() {
		sendMessage("Your balance is: " + money.toString());
	}
	
	/**
	 * @param message
	 */
	public void sendMessage(String message) {
		player.sendMessage(DataManager.getPluginMessage() + message);
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return name + ":" + money.getAmount();
	}
	
	// public void sell(Transaction trans) {
	// lastTrans = trans;
	//		
	// // Give the seller the money
	// trans.getSeller().money.addAmount(trans.getStack().getTotalSellPrice());
	//		
	// // Check if an inf shop
	// if (trans.getBuyer().getMoney().getAmount() != 1337) {
	// // Subtract the money from the buyer
	// trans.getBuyer().getMoney().addAmount(-trans.getStack().getTotalBuyPrice());
	// }
	//		
	// // Add the items to the buyer
	// if (trans.getBuyer().isPlayer()) {
	// DataManager.getUser(trans.getBuyer().getName()).getPlayer().giveItem(trans.getStack().getItemID(),
	// trans.getStack().getAmountAvail());
	// }
	// else {
	// trans.getBuyer().addShopItems(trans.getStack());
	// }
	//		
	// // Remove the items from the seller
	// if (trans.getSeller().isPlayer()) {
	// DataManager.getUser(trans.getSeller().getName()).getPlayer().getInventory().removeItem(trans.getStack().getItemID(),
	// trans.getStack().getAmountAvail());
	// }
	// else {
	// trans.getSeller().removeShopItems(trans.getStack());
	// }
	// }
}
