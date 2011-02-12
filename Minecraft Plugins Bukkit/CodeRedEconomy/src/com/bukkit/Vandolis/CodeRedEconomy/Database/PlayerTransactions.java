/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.entity.Player;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class PlayerTransactions {
	private int		ID;
	private int		senderID	= 0;
	private int		recipientID	= 0;
	private int		itemID		= 0;
	private int		itemAmount	= 0;
	private int		moneyAmount	= 0;
	private Date	timestamp	= null;
	private boolean	newEntry	= false;
	
	/**
	 * Creates and adds to SQL
	 */
	public PlayerTransactions() {
		
	}
	
	/**
	 * Loads a PlayerTransaction from SQL
	 * 
	 * @param ID
	 */
	public PlayerTransactions(int ID) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			ResultSet rs = stat.executeQuery("select * from PlayerTransactions where ID = " + ID + ";");
			
			this.ID = ID;
			senderID = rs.getInt("SenderID");
			recipientID = rs.getInt("RecipientID");
			itemID = rs.getInt("ItemID");
			itemAmount = rs.getInt("ItemAmount");
			moneyAmount = rs.getInt("MoneyAmount");
			timestamp = rs.getDate("Timestamp");
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @param sender
	 * @param reciever
	 * @param moneyAmount
	 */
	public PlayerTransactions(Player sender, Player reciever, Integer moneyAmount) {
		senderID = Players.getID(sender);
		recipientID = Players.getID(reciever);
		this.moneyAmount = moneyAmount;
		timestamp = EconomyProperties.getDate();
		
		newEntry = true;
		
		update();
	}
	
	/**
	 * 
	 */
	private void update() {
		try {
			if (newEntry) {
				PreparedStatement prep =
						EconomyProperties.getConn().prepareStatement(
								"insert into PlayerTransactions (SenderID, RecipientID, ItemID, ItemAmount, MoneyAmount, TimeStamp) "
										+ "values (?, ?, ?, ?, ?, ?);");
				
				prep.setInt(1, senderID);
				prep.setInt(2, recipientID);
				prep.setInt(3, itemID);
				prep.setInt(4, itemAmount);
				prep.setInt(5, moneyAmount);
				prep.setDate(6, timestamp);
				
				prep.execute();
				
				prep.close();
				
				newEntry = false;
			}
			else {
				
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * @return the iD
	 */
	protected int getID() {
		return ID;
	}
	
	/**
	 * @param iD
	 *            the iD to set
	 */
	protected void setID(int iD) {
		ID = iD;
	}
	
	/**
	 * @return the senderID
	 */
	protected int getSenderID() {
		return senderID;
	}
	
	/**
	 * @param senderID
	 *            the senderID to set
	 */
	protected void setSenderID(int senderID) {
		this.senderID = senderID;
	}
	
	/**
	 * @return the recipientID
	 */
	protected int getRecipientID() {
		return recipientID;
	}
	
	/**
	 * @param recipientID
	 *            the recipientID to set
	 */
	protected void setRecipientID(int recipientID) {
		this.recipientID = recipientID;
	}
	
	/**
	 * @return the itemID
	 */
	protected int getItemID() {
		return itemID;
	}
	
	/**
	 * @param itemID
	 *            the itemID to set
	 */
	protected void setItemID(int itemID) {
		this.itemID = itemID;
	}
	
	/**
	 * @return the itemAmount
	 */
	protected int getItemAmount() {
		return itemAmount;
	}
	
	/**
	 * @param itemAmount
	 *            the itemAmount to set
	 */
	protected void setItemAmount(int itemAmount) {
		this.itemAmount = itemAmount;
	}
	
	/**
	 * @return the moneyAmount
	 */
	protected int getMoneyAmount() {
		return moneyAmount;
	}
	
	/**
	 * @param moneyAmount
	 *            the moneyAmount to set
	 */
	protected void setMoneyAmount(int moneyAmount) {
		this.moneyAmount = moneyAmount;
	}
	
	/**
	 * @return the timestamp
	 */
	protected Date getTimestamp() {
		return timestamp;
	}
	
	/**
	 * @param timestamp
	 *            the timestamp to set
	 */
	protected void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	/**
	 * Removes the PlayerTransaction from the table
	 */
	protected void remove() {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from PlayerTransactions where ID = " + ID + ";");
			
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
