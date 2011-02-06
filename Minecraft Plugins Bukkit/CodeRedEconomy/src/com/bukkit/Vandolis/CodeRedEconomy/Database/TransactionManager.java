/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.util.ArrayList;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.bukkit.Vandolis.CodeRedEconomy.CodeRedEconomy;
import com.bukkit.Vandolis.CodeRedEconomy.CommandInterpreter;
import com.bukkit.Vandolis.CodeRedEconomy.CommandInterpreter.Command;
import com.bukkit.Vandolis.CodeRedEconomy.EconException;
import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class TransactionManager {
	private static ArrayList<ShopTransactions>		shopQue		= new ArrayList<ShopTransactions>();
	private static ArrayList<PlayerTransactions>	playerQue	= new ArrayList<PlayerTransactions>();
	
	public static void add(ShopTransactions trans) {
		shopQue.add(trans);
		
		clean();
		process();
	}
	
	/**
	 * @param playerTransactions
	 */
	public static void add(PlayerTransactions playerTransactions) {
		playerQue.add(playerTransactions);
		
		clean();
		
		process();
	}
	
	/**
	 * Process the remaining transactions. If it is still in here, it has not failed.
	 */
	private static void process() {
		for (ShopTransactions iter : shopQue) {
			Shops shop = new Shops(ShopItems.getShopID(iter.getShopItemID()));
			ShopItems shopItem = new ShopItems(iter.getShopItemID());
			Items item = new Items(shopItem.getItemID());
			Players p = new Players(iter.getPlayerID());
			Player player = CodeRedEconomy.getPlayer(p.getName());
			
			if (iter.isPlayerBought()) {
				/*
				 * Player buying, remove from shop
				 */
				p.setBalance(p.getBalance() - iter.getMoneyAmountBuy());
				
				if (item.isSubtyped()) {
					player.getInventory().addItem(new ItemStack(shopItem.getItemID(), iter.getItemAmount(), (short) item.getSubtype()));
				}
				else {
					player.getInventory().addItem(new ItemStack(shopItem.getItemID(), iter.getItemAmount()));
				}
				
				if (!shop.isAllItemsInfinite()) {
					shopItem.setCurrentStock(shopItem.getCurrentStock() - iter.getItemAmount());
				}
				
				//				shop.setBalance(shop.getBalance()+iter.getMoneyAmountBuy());
				
				player.sendMessage(EconomyProperties.getPluginMessage() + "You bought " + iter.getItemAmount() + " " + item.getName()
						+ " for " + iter.getMoneyAmountBuy() + " " + EconomyProperties.getMoneyName());
				
				CommandInterpreter.interpret(Command.BALANCE, player, null);
			}
			else {
				/*
				 * Player selling, add to shop
				 */
				if (item.isSubtyped()) {
					player.getInventory().remove(new ItemStack(shopItem.getItemID(), iter.getItemAmount(), (short) item.getSubtype()));
				}
				else {
					player.getInventory().remove(new ItemStack(shopItem.getItemID(), iter.getItemAmount()));
				}
				
				p.setBalance(p.getBalance() + iter.getMoneyAmountSell());
				
				if (!shop.isAllItemsInfinite()) {
					shopItem.setCurrentStock(shopItem.getCurrentStock() + iter.getItemAmount());
				}
				
				player.sendMessage(EconomyProperties.getPluginMessage() + "You sold " + iter.getItemAmount() + " " + item.getName()
						+ " for " + iter.getMoneyAmountSell() + " " + EconomyProperties.getMoneyName());
				
				CommandInterpreter.interpret(Command.BALANCE, player, null);
			}
		}
		
		for (PlayerTransactions iter : playerQue) {
			Players s = new Players(iter.getSenderID());
			Player sender = CodeRedEconomy.getPlayer(s.getName());
			Players r = new Players(iter.getSenderID());
			Player reciever = CodeRedEconomy.getPlayer(r.getName());
			
			s.setBalance(s.getBalance() - iter.getMoneyAmount());
			
			r.setBalance(r.getBalance() + iter.getMoneyAmount());
			
			sender.sendMessage(EconomyProperties.getPluginMessage() + "You paid " + r.getName() + " " + iter.getMoneyAmount() + " "
					+ EconomyProperties.getMoneyName());
			
			reciever.sendMessage(EconomyProperties.getPluginMessage() + s.getName() + " has paid you " + iter.getMoneyAmount() + " "
					+ EconomyProperties.getMoneyName());
			
			CommandInterpreter.interpret(Command.BALANCE, sender, null);
			CommandInterpreter.interpret(Command.BALANCE, reciever, null);
		}
	}
	
	/**
	 * @param trans
	 * @return number of items the player can hold
	 */
	private static int getAvailableSlotCount(ShopTransactions trans) {
		Players p = new Players(trans.getPlayerID());
		Player player = CodeRedEconomy.getPlayer(p.getName());
		ShopItems shopItem = new ShopItems(trans.getShopItemID());
		Items item = new Items(shopItem.getItemID());
		
		int available = 0; // The amount the player can hold
		for (ItemStack iter : player.getInventory().getContents()) {
			if (item.isSubtyped()) {
				if ((iter.getTypeId() == shopItem.getItemID()) && (iter.getDurability() == (short) item.getSubtype())) {
					// Same item type check how much more the stack can hold
					available += (64 - iter.getAmount());
				}
				else if (iter.getType().equals(Material.AIR)) {
					// Empty slot
					available += 64;
				}
			}
			else {
				if ((iter.getTypeId() == shopItem.getItemID())) {
					// Same item type check how much more the stack can hold
					available += (64 - iter.getAmount());
				}
				else if (iter.getType().equals(Material.AIR)) {
					// Empty slot
					available += 64;
				}
			}
		}
		
		return available;
	}
	
	/**
	 * Removes any failed transactions from the list
	 */
	private static void clean() {
		boolean broke = false;
		
		for (ShopTransactions iter : shopQue) {
			Shops shop = new Shops(ShopItems.getShopID(iter.getShopItemID()));
			ShopItems item = new ShopItems(iter.getShopItemID());
			Players player = new Players(iter.getPlayerID());
			Player p = CodeRedEconomy.getPlayer(player.getName());
			
			try {
				int actualAmount = item.getCurrentStock();
				
				/*
				 * Check amount of item
				 */
				if (((iter.getItemAmount() <= actualAmount) || shop.isAllItemsInfinite() || item.isInfinite() || ((item.getCurrentStock() == EconomyProperties
						.getInfValue()) && iter.isPlayerBought())) || (!iter.isPlayerBought())) {
					if (!iter.isPlayerBought()) {
						/*
						 * Check if the player has the items
						 */
						int itemCount = getItemCount(iter);
						
						if (itemCount < iter.getItemAmount()) {
							if (itemCount > 0) {
								ShopTransactions temp = iter;
								temp.setItemAmount(itemCount);
								shopQue.add(temp);
							}
							
							throw new EconException("The seller doesn't have enough of that item.", "You don't have enough of that item.");
						}
					}
					
					/*
					 * Check price
					 */
					if ((((player.getBalance() >= iter.getMoneyAmountBuy()) && iter.isPlayerBought())) || !iter.isPlayerBought()
							|| (player.getBalance() == EconomyProperties.getInfValue())) {
						int availableSlots = getAvailableSlotCount(iter);
						
						/*
						 * Check slots
						 */
						if (((availableSlots >= iter.getItemAmount()) && iter.isPlayerBought()) || !iter.isPlayerBought()) {
							/*
							 * Has enough slots
							 */

							/*
							 * All checks passed. The player should:
							 * 
							 * Have enough of the item available to buy/sell
							 * Have enough money to buy the item
							 * Have enough slots to hold the item
							 */
						}
						else {
							/*
							 * Not enough slots
							 */
							if (availableSlots > 0) {
								ShopTransactions temp = iter;
								
								temp.setItemAmount(availableSlots);
								
								shopQue.add(temp);
							}
							
							throw new EconException("You can't hold anymore.", "The buyer can't hold anymore");
						}
					}
					else {
						int count = (int) (player.getBalance() / item.getBuyPrice());
						
						if (count > 0) {
							ShopTransactions temp = iter;
							
							temp.setItemAmount(count);
							
							shopQue.add(temp);
						}
						
						throw new EconException("You don't have enough " + EconomyProperties.getMoneyName(),
								"The buyer doesn't have enough " + EconomyProperties.getMoneyName());
					}
				}
				else {
					if (actualAmount > 0) {
						/*
						 * There are items that can be bought, make a new shoptransaction and change the item amount to the current amount
						 */
						ShopTransactions temp = iter;
						
						temp.setItemAmount(actualAmount);
						
						shopQue.add(temp);
					}
					
					throw new EconException("Not enough of that item to buy.", "You don't have enough of that item to sell.");
				}
			}
			catch (EconException e) {
				if (iter.isPlayerBought()) {
					p.sendMessage(EconomyProperties.getPluginMessage() + e.getBuyMsg());
				}
				else {
					p.sendMessage(EconomyProperties.getPluginMessage() + e.getSellMsg());
				}
				
				shopQue.remove(iter);
				
				broke = true;
				break;
			}
		}
		
		for (PlayerTransactions iter : playerQue) {
			Players sender = new Players(iter.getSenderID());
			Player send = CodeRedEconomy.getPlayer(sender.getName());
			
			try {
				/*
				 * Check money
				 */
				if (sender.getBalance() >= iter.getMoneyAmount()) {
					/*
					 * Enough money
					 */
				}
				else {
					if (sender.getBalance() > 0) {
						PlayerTransactions temp = iter;
						temp.setMoneyAmount(sender.getBalance());
						playerQue.add(temp);
					}
					
					throw new EconException("You don't have enough " + EconomyProperties.getMoneyName(), "The buyer doesn't have enough "
							+ EconomyProperties.getMoneyName());
				}
			}
			catch (EconException e) {
				send.sendMessage(EconomyProperties.getPluginMessage() + e.getBuyMsg());
				
				playerQue.remove(iter);
				
				broke = true;
				break;
			}
		}
		
		if (broke) {
			/*
			 * If it broke due to remove, call clean again to check the rest. Should prevent concurrent modification errors
			 */
			clean();
		}
	}
	
	/**
	 * @param iter
	 * @return number of current item
	 */
	private static int getItemCount(ShopTransactions trans) {
		Players p = new Players(trans.getPlayerID());
		Player player = CodeRedEconomy.getPlayer(p.getName());
		ShopItems shopItem = new ShopItems(trans.getShopItemID());
		Items item = new Items(shopItem.getItemID());
		
		int itemCount = 0;
		
		for (ItemStack iter : player.getInventory().getContents()) {
			if (item.isSubtyped()) {
				if ((iter.getTypeId() == shopItem.getItemID()) && (iter.getDurability() == (short) item.getSubtype())) {
					itemCount += iter.getAmount();
				}
			}
			else if ((iter.getTypeId() == shopItem.getItemID())) {
				itemCount += iter.getAmount();
			}
		}
		
		return itemCount;
	}
	
	/**
	 * @return the que
	 */
	protected ArrayList<ShopTransactions> getShopQue() {
		return shopQue;
	}
	
	/**
	 * @param que
	 *            the que to set
	 */
	protected void setShopQue(ArrayList<ShopTransactions> que) {
		shopQue = que;
	}
	
	/**
	 * @return the playerQue
	 */
	protected ArrayList<PlayerTransactions> getPlayerQue() {
		return playerQue;
	}
	
	/**
	 * @param playerQue
	 *            the playerQue to set
	 */
	protected void setPlayerQue(ArrayList<PlayerTransactions> playerQue) {
		this.playerQue = playerQue;
	}
	
	/**
	 * @param undone
	 */
	public static void undo(ShopTransactions undone) {
		Players p = new Players(undone.getPlayerID());
		ShopItems shopItem = new ShopItems(undone.getShopItemID());
		Items item = new Items(shopItem.getItemID());
		Shops shop = new Shops(shopItem.getShopID());
		Player player = CodeRedEconomy.getPlayer(p.getName());
		
		try {
			if (undone.isPlayerBought()) {
				/*
				 * Player bought the items, remove items return money
				 */

				int itemCount = getItemCount(undone);
				
				if (itemCount >= undone.getItemAmount()) {
					if (item.isSubtyped()) {
						player.getInventory().remove(new ItemStack(item.getItemID(), undone.getItemAmount(), (short) item.getSubtype()));
					}
					else {
						player.getInventory().remove(new ItemStack(item.getItemID(), undone.getItemAmount()));
					}
					
					p.setBalance(p.getBalance() + undone.getMoneyAmountBuy());
					
					if (!shop.isAllItemsInfinite()) {
						shopItem.setCurrentStock(shopItem.getCurrentStock() + undone.getItemAmount());
					}
					
					shop.setBalance(shop.getBalance() - undone.getMoneyAmountBuy());
					
					player.sendMessage(EconomyProperties.getPluginMessage() + "Last transaction undone.");
					
					CommandInterpreter.interpret(Command.BALANCE, player, null);
					
					p.setLastShopTransaction(null);
					
					undone.remove();
				}
				else {
					throw new EconException("Undo failed, not enough of the item.", "");
				}
			}
			else {
				/*
				 * Player sold the item, return items, remove money
				 */
				if (p.getBalance() >= undone.getMoneyAmountSell()) {
					/*
					 * Has enough money
					 */
					int slotCount = getAvailableSlotCount(undone);
					
					if (slotCount >= undone.getItemAmount()) {
						p.setBalance(p.getBalance() - undone.getMoneyAmountSell());
						
						if (item.isSubtyped()) {
							player.getInventory().addItem(
									new ItemStack(item.getItemID(), undone.getItemAmount(), (short) item.getSubtype()));
						}
						else {
							player.getInventory().addItem(new ItemStack(item.getItemID(), undone.getItemAmount()));
						}
						
						if (!shop.isAllItemsInfinite()) {
							shopItem.setCurrentStock(shopItem.getCurrentStock() - undone.getItemAmount());
						}
						
						shop.setBalance(shop.getBalance() + undone.getMoneyAmountSell());
						
						player.sendMessage(EconomyProperties.getPluginMessage() + "Last transaction undone.");
						
						CommandInterpreter.interpret(Command.BALANCE, player, null);
						
						p.setLastShopTransaction(null);
						
						undone.remove();
					}
					else {
						throw new EconException("Undo failed, not enough inventory space.", "");
					}
				}
				else {
					throw new EconException("Undo failed, not enough " + EconomyProperties.getMoneyName(), "");
				}
			}
		}
		catch (EconException e) {
			player.sendMessage(EconomyProperties.getPluginMessage() + e.getBuyMsg());
		}
	}
}
