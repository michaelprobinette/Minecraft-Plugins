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
public class Players {
	private int					ID;
	private String				name				= "";
	private int					balance				= 0;
	private Date				lastAutoPayment		= null;
	private ShopTransactions	lastShopTransaction	= null;
	private boolean				newEntry			= false;
	
	/**
	 * Used to add a new entry to the table
	 * 
	 * @param name
	 */
	public Players(String name) {
		this.name = name;
		newEntry = true;
		
		autoPay();
		
		update();
	}
	
	/**
	 * Loads from SQL
	 * 
	 * @param Id
	 *            in sql
	 */
	public Players(int Id) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs = stat.executeQuery("select * from Players where ID = " + Id + ";");
			
			ID = rs.getInt("ID");
			name = rs.getString("Name");
			balance = rs.getInt("Balance");
			lastAutoPayment = rs.getDate("LastAutoPayment");
			
			if (rs.getInt("LastShopTransaction") != -1) {
				lastShopTransaction = new ShopTransactions(rs.getInt("LastShopTransaction"));
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 */
	private void autoPay() {
		if (EconomyProperties.isAutoPay()) {
			if (newEntry) {
				balance += EconomyProperties.getAutoDepositAmount();
				lastAutoPayment = EconomyProperties.getDate();
			}
			else if (EconomyProperties.getDate().getTime() - lastAutoPayment.getTime() >= EconomyProperties.getAutoDepositTime()) {
				balance += EconomyProperties.getAutoDepositAmount();
				lastAutoPayment = EconomyProperties.getDate();
			}
		}
	}
	
	/**
	 * @param player
	 * @return -1 if not found
	 */
	public static int getID(Player player) {
		try {
			PreparedStatement prep = EconomyProperties.getConn().prepareStatement("select Players.ID from Players where Name = ?;");
			prep.setString(1, player.getName());
			
			ResultSet rs = prep.executeQuery();
			
			int id = 0;
			
			if (rs.next()) {
				id = rs.getInt("ID");
			}
			
			rs.close();
			prep.close();
			
			return id;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 0;
	}
	
	/**
	 * @return the iD
	 */
	public int getID() {
		return ID;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the balance
	 */
	public int getBalance() {
		return balance;
	}
	
	/**
	 * @return the lastAutoPayment
	 */
	public Date getLastAutoPayment() {
		return lastAutoPayment;
	}
	
	/**
	 * @return the newEntry
	 */
	public boolean isNewEntry() {
		return newEntry;
	}
	
	/**
	 * @param newEntry
	 *            the newEntry to set
	 */
	public void setNewEntry(boolean newEntry) {
		this.newEntry = newEntry;
	}
	
	/**
	 * @param iD
	 *            the iD to set
	 */
	public void setID(int iD) {
		ID = iD;
		update();
	}
	
	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
		update();
	}
	
	/**
	 * @param balance
	 *            the balance to set
	 */
	public void setBalance(int balance) {
		if (this.balance != EconomyProperties.getInfValue()) {
			this.balance = balance;
			update();
		}
	}
	
	/**
	 * @param lastAutoPayment
	 *            the lastAutoPayment to set
	 */
	public void setLastAutoPayment(Date lastAutoPayment) {
		this.lastAutoPayment = lastAutoPayment;
		update();
	}
	
	/**
	 * @return the lastShopTransaction
	 */
	public ShopTransactions getLastShopTransaction() {
		return lastShopTransaction;
	}
	
	/**
	 * @param lastShopTransaction
	 *            the lastShopTransaction to set
	 */
	public void setLastShopTransaction(ShopTransactions lastShopTransaction) {
		this.lastShopTransaction = lastShopTransaction;
		update();
	}
	
	/**
	 * Updates the entry in SQL
	 */
	public void update() {
		try {
			if (newEntry) {
				PreparedStatement prep =
						EconomyProperties.getConn().prepareStatement(
								"insert into Players (Name, Balance, LastAutoPayment, LastShopTransaction) values (?, ?, ?, ?);");
				
				prep.setString(1, name);
				prep.setInt(2, balance);
				prep.setDate(3, lastAutoPayment);
				if (lastShopTransaction != null) {
					prep.setInt(4, lastShopTransaction.getID());
				}
				else {
					prep.setInt(4, -1);
				}
				
				prep.execute();
				
				prep.close();
				
				newEntry = false;
			}
			else {
				PreparedStatement prep =
						EconomyProperties.getConn().prepareStatement(
								"update Players set Name = ?, Balance = ?, LastAutoPayment = ?, LastShopTransaction = ? where ID = " + ID
										+ ";");
				
				prep.setString(1, name);
				prep.setInt(2, balance);
				prep.setDate(3, lastAutoPayment);
				if (lastShopTransaction != null) {
					prep.setInt(4, lastShopTransaction.getID());
				}
				else {
					prep.setInt(4, -1);
				}
				
				prep.execute();
				
				prep.close();
			}
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * Removes the Player from the table
	 */
	public void remove() {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from Players where ID = " + ID + ";");
			
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
