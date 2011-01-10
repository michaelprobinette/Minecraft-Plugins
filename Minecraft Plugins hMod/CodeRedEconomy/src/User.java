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
	private final String	regex		= DataManager.getPlayerRegex();
	private Player			player		= null;
	private String			groupName	= "";
	
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
		super();
		// Split it and grab the data
		String split[] = saveString.split(regex);
		if (split.length >= 2) {
			name = split[0];
			int temp = Integer.valueOf(split[1]);
			money.setAmount(temp);
			if (split.length >= 3) {
				lastAutoDeposit = Integer.valueOf(split[2]);
			}
			setPlayer(etc.getServer().getPlayer(name));
		}
		else if (split.length == 1) {
			if (DataManager.getDebug()) {
				System.out.println("Creating a new user with the name of " + name);
			}
			// Came from a new user, only the name
			name = saveString;
			setPlayer(etc.getServer().getPlayer(name));
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
		if (player != null) {
			this.player = player;
			if (player.getGroups().length >= 1) {
				groupName = player.getGroups()[0];
			}
			else
				groupName = "nogroup";
			
			isPlayer = true;
		}
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
			if (Transaction.undoTransaction(lastTrans)) {
				lastTrans = null;
			}
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
					if (!found && DataManager.validID(iter.getItemId())) {
						availableItems.add(new ShopItemStack(new ShopItem(iter.getItemId()), iter.getAmount()));
					}
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void showBalance() {
		autoDesposit(etc.getServer().getTime());
		if (money.getAmount() != DataManager.getInfValue()) {
			sendMessage("Your balance is: " + money.toString());
		}
		else {
			sendMessage("You have Infinite money.");
		}
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
		// name:money:lastdeposit
		return name + regex + money.getAmount() + regex + lastAutoDeposit;
	}
}
