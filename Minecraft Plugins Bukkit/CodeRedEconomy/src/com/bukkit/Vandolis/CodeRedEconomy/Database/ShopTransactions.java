/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class ShopTransactions {
	private int		ID;
	private int		playerID;
	private int		shopItemID;
	private int		itemAmount;
	private int		moneyAmountBuy;
	private int		moneyAmountSell;
	private Date	timestamp;
	private boolean	playerBought;
	
	/**
	 * Loads a shop transaction with the given ID
	 * 
	 * @param ID
	 */
	public ShopTransactions(int ID) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			ResultSet rs = stat.executeQuery("select * from ShopTransactions where ID = " + ID + ";");
			
			this.ID = ID;
			playerID = rs.getInt("PlayerID");
			shopItemID = rs.getInt("ShopItemID");
			itemAmount = rs.getInt("ItemAmount");
			moneyAmountBuy = rs.getInt("MoneyAmountBuy");
			moneyAmountSell = rs.getInt("MoneyAmountSell");
			timestamp = rs.getDate("Timestamp");
			playerBought = rs.getBoolean("PlayerBought");
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param user
	 * @param string
	 * @param itemName
	 * @param amount
	 */
	public ShopTransactions(Player player, String shopName, String itemName, int amount, boolean buy) {
		playerID = Players.getID(player);
		
		int shopId = Shops.getId(shopName);
		int itemId = Items.getId(itemName);
		
		shopItemID = ShopItems.getShopItemId(shopId, itemId);
		
		itemAmount = amount;
		
		ShopItems item = null;
		
		if (shopItemID != 0) {
			item = new ShopItems(shopItemID);
			
			moneyAmountBuy = (int) (Math.round(item.getBuyPrice()) * amount);
			moneyAmountSell = (int) (Math.round(item.getSellPrice()) * amount);
		}
		
		timestamp = EconomyProperties.getDate();
		
		playerBought = buy;
	}
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
	}
	
	/**
	 * @return the playerID
	 */
	public int getPlayerID() {
		return playerID;
	}
	
	/**
	 * @param playerID
	 *            the playerID to set
	 */
	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}
	
	/**
	 * @return the shopItemID
	 */
	public int getShopItemID() {
		return shopItemID;
	}
	
	/**
	 * @param shopItemID
	 *            the shopItemID to set
	 */
	public void setShopItemID(int shopItemID) {
		this.shopItemID = shopItemID;
	}
	
	/**
	 * @return the itemAmount
	 */
	public int getItemAmount() {
		return itemAmount;
	}
	
	/**
	 * @param itemAmount
	 *            the itemAmount to set
	 */
	public void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}
	
	/**
	 * @return the timestamp
	 */
	public Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * @return the moneyAmountBuy
	 */
	public int getMoneyAmountBuy() {
		return moneyAmountBuy;
	}
	
	/**
	 * @param moneyAmountBuy
	 *            the moneyAmountBuy to set
	 */
	public void setMoneyAmountBuy(int moneyAmountBuy) {
		this.moneyAmountBuy = moneyAmountBuy;
	}
	
	/**
	 * @return the moneyAmountSell
	 */
	public int getMoneyAmountSell() {
		return moneyAmountSell;
	}
	
	/**
	 * @param moneyAmountSell
	 *            the moneyAmountSell to set
	 */
	public void setMoneyAmountSell(int moneyAmountSell) {
		this.moneyAmountSell = moneyAmountSell;
	}
	
	/**
	 * @return the playerBought
	 */
	public boolean isPlayerBought() {
		return playerBought;
	}
	
	/**
	 * @param playerBought
	 *            the playerBought to set
	 */
	public void setPlayerBought(boolean playerBought) {
		this.playerBought = playerBought;
	}
	
	/**
	 * Removes the ShopTransaction from the table
	 */
	public void remove() {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from ShopTransactions where ID = " + ID + ";");
			
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
