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

import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class User extends EconEntity {
	private final String	regex		= DataManager.getPlayerRegex();
	private Player			player		= null;
	private final String	groupName	= "";
	
	/**
	 * 
	 */
	public User() {
		super();
		setUser(this);
	}
	
	/**
	 * New player
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
		
		isPlayer = true;
		setUser(this);
		DataManager.addUser(this);
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
			setPlayer(DataManager.getServer().getPlayer(name));
		}
		else if (split.length == 1) {
			if (DataManager.getDebug()) {
				System.out.println("Creating a new user with the name of " + name);
			}
			// Came from a new user, only the name
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
	 * @return
	 */
	public String getGroupName() {
		return groupName;
	}
	
	/*
	 * (non-Javadoc)
	 * @see EconEntity#getMoney()
	 */
	@Override
	public Money getMoney() {
		return money;
	}
	
	/**
	 * @return
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @param message
	 */
	public void sendMessage(String message) {
		player.sendMessage(DataManager.getPluginMessage() + message);
	}
	
	/**
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
			
			isPlayer = true;
		}
	}
	
	/**
	 * 
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
	
	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// name:money:lastdeposit
		return name + regex + money.getAmount() + regex + lastAutoDeposit;
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
		for (ItemStack iter : player.getInventory().getContents()) {
			if (iter != null) {
				if (iter.getTypeId() > 0) {
					// Check if it is already in the array
					boolean found = false;
					for (ShopItemStack siter : availableItems) {
						if (siter.getItemID() == iter.getTypeId()) {
							// Already in available items, so add to the amount
							siter.addAmountAvail(iter.getAmount());
							found = true;
						}
					}
					// Add it to the array
					if (!found && DataManager.validID(iter.getTypeId())) {
						availableItems.add(new ShopItemStack(new ShopItem(iter.getTypeId()), iter.getAmount()));
					}
				}
			}
		}
	}
}
