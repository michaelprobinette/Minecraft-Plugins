/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class User extends EconEntity {
	private final String	regex		= DataManager.getPlayerRegex();
	private Player			player		= null;
	private final String	groupName	= "";
	
	/**
	 * Default constructor
	 */
	public User() {
		super();
		setUser(this);
	}
	
	/**
	 * New {@link User} constructor based on the given {@link Player}.
	 * 
	 * @param player
	 */
	public User(Player player) {
		super();
		this.player = player;
		name = player.getName();
		
		// FIXME Once bukkit has a permissions system
		
		// if (player.getGroups().length >= 1) {
		// groupName = player.getGroups()[0];
		// }
		// else {
		// groupName = "nogroup";
		// }
		
		/*
		 * Sets the EconEntities user to this object, easy way to get the correct user when dealing with entities.
		 * Then registers the user with the DataManager.
		 */
		setUser(this);
		DataManager.addUser(this);
	}
	
	/**
	 * Creates a {@link User} object based off of a save string from a file. Save string contains the users name, amount of money, as well
	 * as the last time they got an auto-deposit.
	 * 
	 * @param saveString
	 */
	public User(String saveString) {
		super();
		/*
		 * Split it and grab the data
		 */
		String split[] = saveString.split(regex);
		if (split.length >= 2) {
			name = split[0];
			int temp = Integer.valueOf(split[1]);
			money.setAmount(temp);
			if (split.length >= 3) {
				lastAutoDeposit = Integer.valueOf(split[2]);
			}
			setPlayer(DataManager.getServer().getPlayer(name));
		}
		else if (split.length == 1) {
			/*
			 * Came from a new user, only the name
			 */
			if (DataManager.getDebug()) {
				System.out.println("Creating a new user with the name of " + name);
			}
			
			name = saveString;
			setPlayer(DataManager.getServer().getPlayer(name));
		}
		setUser(this);
	}
	
	/**
	 * Used for loading from the file
	 * 
	 * @param user
	 */
	public User(User user) {
		super(user.getMoney());
		player = user.getPlayer();
		name = user.getPlayer().getName();
		setUser(this);
	}
	
	/**
	 * Returns the group the user belongs to.
	 * 
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/**
	 * Returns the {@link Player} attached to the {@link User} object. Null if no player has been tied yet.
	 * 
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * Sends a message to the user using the default message format.
	 * 
	 * @param message
	 */
	public void sendMessage(String message) {
		if (player != null) {
			player.sendMessage(DataManager.getPluginMessage() + message);
		}
	}
	
	/**
	 * Ties the given {@link Player} to the {@link User}.
	 * 
	 * @param player
	 */
	public void setPlayer(Player player) {
		if (player != null) {
			this.player = player;
			
			// FIXME Once bukkit has a permission system
			
			// if (player.getGroups().length >= 1) {
			// groupName = player.getGroups()[0];
			// }
			// else {
			// groupName = "nogroup";
			// }
		}
	}
	
	/**
	 * Prints the {@link User} balance.
	 */
	public void showBalance() {
		autoDesposit(DataManager.getServer().getTime());
		if (money.getAmount() != DataManager.getInfValue()) {
			sendMessage("Your balance is: " + money.toString());
		}
		else {
			sendMessage("You have Infinite money.");
		}
	}
	
	/**
	 * Returns the save string to be written to file. Format is name:money:autoDeposit
	 * 
	 * @return
	 */
	public String getSaveString() {
		return name + regex + money.getAmount() + regex + lastAutoDeposit;
	}
	
	/**
	 * Tries to undo the users last {@link Transaction}.
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
		/*
		 * Clear the availableItems to prevent errors.
		 */
		availableItems.clear();
		
		/*
		 * Iterate through the players inventory and add the items to the availableItems array.
		 */
		for (ItemStack iter : player.getInventory().getContents()) {
			if (iter != null) {
				if (iter.getTypeId() > 0) {
					/*
					 * Check if it is already in the array
					 */
					boolean found = false;
					for (ShopItemStack siter : availableItems) {
						if (siter.getItemId() == iter.getTypeId()) {
							/*
							 * Already in available items, so add to the amount
							 */
							siter.addAmountAvail(iter.getAmount());
							found = true;
						}
					}
					/*
					 * Add it to the array if it was not found already
					 */
					if (!found && DataManager.validID(iter.getTypeId())) {
						availableItems.add(new ShopItemStack(iter.getTypeId(), iter.getAmount()));
					}
				}
			}
		}
	}
	
	/**
	 * Adds the item to the players inventory.
	 * 
	 * @param stack
	 */
	public void addItem(ShopItemStack stack) {
		getPlayer().getInventory().addItem(stack.getItem());
	}
	
	/**
	 * Removes the item from the players inventory.
	 * 
	 * @param stack
	 */
	public void removeItem(ShopItemStack stack) {
		getPlayer().getInventory().removeItem(stack.getItem());
	}
}
