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
		setUser(this);
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
		setUser(this);
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
		else if (split.length == 1) {
			// Came from a new user, only the name
			name = saveString;
		}
		setUser(this);
	}
	
	/**
	 * 
	 */
	public User() {
		super();
		setUser(this);
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
	 * 
	 */
	public void undoLastTrans() {
		if (lastTrans != null) {
			lastTrans = Transaction.undoTransaction(lastTrans);
		}
		else {
			sendMessage("There is no last transaction to undo.");
		}
	}
	
	/**
	 * Updates the availableItems array to the users current inventory. Used to check amounts easily before selling
	 */
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
	// public void transferFromArray() {
	//		
	// for (ShopItemStack iter : availableItems) {
	// System.out.println("Amount adding is: " + iter.getAmountAvail());
	// if (iter.getAmountAvail() != 0) {
	// player.giveItem(iter.getItemID(), iter.getAmountAvail());
	// }
	// // player.giveItem(new Item(iter.getItemID(), iter.getAmountAvail()));
	// // player.giveItem(iter.getItemID(), iter.getAmountAvail());
	// }
	// }
	
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
}
