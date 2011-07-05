/**
 *
 */
package com.Vandolis.CodeRedLite;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;

import org.bukkit.entity.Player;

/**
 * @author Vandolis
 */
public class SQLDatabase
{
	private String								dataLocation	= null;
	private Connection							conn			= null;
	private CodeRedLite							plugin			= null;
	
	private HashMap<String, PreparedStatement>	statementCache	= new HashMap<String, PreparedStatement>();
	
	/**
	 * Default ctor for the database, only sets the plugin to the given instance.
	 * Before use you must run the openConnection() method.
	 * 
	 * @param codeRedLite
	 */
	protected SQLDatabase(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
	}
	
	/**
	 * Attempts to close all statements in the cache as well as the connection to the database.
	 * On error returns false and displays info to console.
	 * 
	 * @return True if all connections closed, false if error occurred
	 */
	protected boolean closeConnection()
	{
		try
		{
			// Loop through and close all prepared statements in the cache
			for (String iter : statementCache.keySet())
			{
				statementCache.get(iter).close(); // Close the prepared statement
			}
			
			statementCache.clear(); // Empty the cache
			
			conn.close(); // Close the connection
		}
		catch (SQLException e)
		{
			plugin.getLog().log(Level.SEVERE, e.getLocalizedMessage());
			return false; // Failed to close all connections
		}
		
		return true; // Closed all connections
	}
	
	/**
	 * Attempts to load the jdbc driver as well as establish a connection to the database. On error returns false
	 * and displays info to console.
	 * 
	 * @return True if connection established. False if an error occured
	 */
	protected boolean openConnection()
	{
		// Set the data location
		dataLocation = "jdbc:sqlite:" + plugin.getDataFolder() + "/CodeRedLite.sqlite";
		
		try
		{
			Class.forName("org.sqlite.JDBC"); // Load the driver
			conn = DriverManager.getConnection(dataLocation); // Get the connection
		}
		catch (SQLException e)
		{
			// Failed to connect or load driver
			plugin.getLog().log(Level.SEVERE, e.getLocalizedMessage());
			plugin.getLog().log(Level.SEVERE, "CodeRedLite could not connect to the database.");
			
			return false; // Failed to open the connection
		}
		catch (ClassNotFoundException e)
		{
			plugin.getLog().log(Level.SEVERE, e.getLocalizedMessage());
			plugin.getLog().log(Level.SEVERE,
					"CodeRedLite could not load the driver. Please check that it is in ./lib/");
			
			return false; // Failed to open the connection
		}
		
		return true; // Opened all connections
	}
	
	/**
	 * Returns the prepared statement for the given query. First looks through the statementCache for the query and,
	 * if found, returns the matching prepared statement. If the statement is not found then it creates a new
	 * statement, adds it to the cache, and returns it.
	 * 
	 * @param query
	 * @return PreparedStatement
	 * @throws SQLException
	 */
	private PreparedStatement prepare(String query) throws SQLException
	{
		// Look through the cache
		for (String iter : statementCache.keySet())
		{
			// Check if same query
			if (iter.equalsIgnoreCase(query))
			{
				return statementCache.get(query); // Return the matching statement
			}
		}
		
		// Not found, make a new statement
		PreparedStatement prep = conn.prepareStatement(query);
		
		statementCache.put(query, prep); // Add the statement to the cache
		
		return prep; // Return the statement
	}
	
	/**
	 * Creates and returns a EconPlayer object based on the given player. If no entry was found in the database a new
	 * entry will be created and returned with a ID of 0.
	 * 
	 * @param player
	 * @return
	 * @throws SQLException
	 */
	protected EconPlayer getEconPlayer(Player player) throws SQLException
	{
		String query = null; // Holds the query
		PreparedStatement prep = null; // Holds the prepared statement
		ResultSet rs = null; // Holds the result set
		EconPlayer econPlayer = null;
		
		// Set the query
		query = "SELECT ID, Balance, LastShopTransaction FROM Players WHERE Name = ?;";
		
		// Prepare the query
		prep = prepare(query);
		
		// Set the values
		prep.setString(1, player.getName());
		
		// Execute the query and get the result set
		rs = prep.executeQuery();
		
		// Start making the EconPlayer object
		econPlayer = new EconPlayer(player, plugin);
		
		// Check if there is currently a entry
		if (rs.next())
		{
			// There is, so add the id, balance, and las shoptransaction to the object
			econPlayer.setSQLID(rs.getInt("ID"));
			econPlayer.setBalance(rs.getInt("Balance"));
			econPlayer.setLastShopTransaction(rs.getInt("LastShopTransaction"));
		}
		
		// Close the resultset
		rs.close();
		
		return econPlayer; // return the object
	}
	
	/**
	 * Takes the given EconPlayer and updates their entry in the table. If the player is not yet in the table (id=0)
	 * then it will add them to the table, then it will update their id to the correct value.
	 * 
	 * @param econPlayer
	 */
	public void update(EconPlayer econPlayer) throws SQLException
	{
		// Declare our variables
		String query = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		
		// If the id is 0, it is a new player
		if (econPlayer.getSQLID() == 0)
		{
			/*
			 * Add the player to the table
			 */

			// Setup query
			query = "INSERT INTO Players (Name,Balance,LastShopTransaction) VALUES (?,?,?);";
			
			// Prepare the query
			prep = prepare(query);
			
			// Set the values
			prep.setString(1, econPlayer.getPlayer().getName());
			prep.setInt(2, econPlayer.getBalance());
			prep.setInt(3, econPlayer.getLastShopTransaction());
			
			// Execute the update
			prep.executeUpdate();
			
			/*
			 * Get the players id for use later
			 */

			// Setup the new query
			query = "SELECT ID FROM Players WHERE Name = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set values
			prep.setString(1, econPlayer.getPlayer().getName());
			
			// Execute and get the results
			rs = prep.executeQuery();
			
			// Double check that there is a result (if not then there is a much greater problem at hand)
			if (rs.next())
			{
				// Set the EconPlayer id
				econPlayer.setSQLID(rs.getInt("ID"));
			}
			
			// Close the result set
			rs.close();
		}
		else
		{
			// Setup the query
			query = "UPDATE Players SET Balance = ?, LastShopTransaction = ? WHERE ID = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setInt(1, econPlayer.getBalance());
			prep.setInt(2, econPlayer.getLastShopTransaction());
			prep.setInt(3, econPlayer.getSQLID());
			
			// Execute the update
			prep.executeUpdate();
		}
	}
	
	/**
	 * Constructs a EconShop based around the given name. Loads all of the shops settings as well as its inventory.
	 * 
	 * @param name
	 * @return loaded EconShop
	 * @throws SQLException
	 */
	public EconShop getEconShop(String name) throws SQLException
	{
		// Declare the variables
		EconShop econShop = null;
		String query = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		
		// Make a new shop with the given name
		econShop = new EconShop(name, plugin);
		
		// Setup the query
		query =
				"SELECT ID, Balance, AllItemsInfinite, CanRestock, AllowBuying, AllowSelling, UseMoney FROM Shops " +
					"WHERE Name = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		prep.setString(1, name);
		
		// Execute and get the results
		rs = prep.executeQuery();
		
		// If there is a result that means it is an existing shop, load its values
		if (rs.next())
		{
			// Load all of the shops values from the table
			econShop.setSQLID(rs.getInt("ID"));
			econShop.setBalance(rs.getInt("Balance"));
			econShop.setAllItemsInfinite(rs.getBoolean("AllItemsInfinite"));
			econShop.setCanRestock(rs.getBoolean("CanRestock"));
			econShop.setAllowBuying(rs.getBoolean("AllowBuying"));
			econShop.setAllowSelling(rs.getBoolean("AllowSelling"));
			econShop.setUseMoney(rs.getBoolean("UseMoney"));
		}
		
		// Close the result set
		rs.close();
		
		// Setup query for inventory
		query =
				"SELECT Items.ItemID, Items.IsSubtyped, Items.Subtype, Items.Name, ShopItems.CurrentStock, " +
					"ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice, ShopItems.MaxSellAmount, " +
					"ShopItems.MaxBuyAmount, ShopItems.ItemID AS ItemsID, ShopItems.ID, Items.BaseValue, Items.Slope " +
					"FROM Items,ShopItems WHERE ShopItems.ShopID = ? AND Items.ID = ShopItems.ItemID;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		prep.setInt(1, econShop.getSqlID());
		
		// Execute and store the results
		rs = prep.executeQuery();
		
		// Loop through all of the returned items and add them to the shops inventory
		while (rs.next())
		{
			econShop.getInventory().add(
					new EconItemStack(rs.getInt("ID"), rs.getInt("ItemID"), rs.getBoolean("IsSubtyped"),
						rs.getShort("Subtype"),
						rs.getString("Name"), rs.getInt("CurrentStock"), rs.getBoolean("IsInfinite"),
						rs.getInt("BuyPrice"), rs.getInt("SellPrice"), rs.getInt("MaxSellAmount"),
						rs.getInt("MaxBuyAmount"), rs.getInt("ItemsID"), rs.getInt("BaseValue"),
						rs.getFloat("Slope"), plugin));
			
			//plugin.LOG.info("Loaded item: " + rs.getString("Name"));
		}
		
		// Close the result set
		rs.close();
		
		// Return the constructed shop
		return econShop;
	}
	
	/**
	 * Updates the given EconShops database entry for the shop and inventory. If it is a new shop (id = 0)
	 * then add it to the table and store the id for later use.
	 * 
	 * @param econShop
	 * @throws SQLException
	 * @Deprecated
	 */
	@Deprecated
	public void hardShopUpdate(EconShop econShop) throws SQLException
	{
		// Declare variables
		PreparedStatement prep = null;
		String query = null;
		ResultSet rs = null;
		
		// Check for a new shop (id = 0)
		if (econShop.getSqlID() != 0)
		{
			// Setup the query
			query = "UPDATE Shops SET Balance = ? WHERE ID = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setInt(1, econShop.getBalance());
			prep.setInt(2, econShop.getSqlID());
			
			// Execute the update
			prep.executeUpdate();
		}
		else
		{
			/*
			 * Add the shop to the table
			 */

			// Setup the query
			query =
					"INSERT INTO Shops (Name, Balance, AllItemsInfinite, CanRestock, AllowBuying, AllowSelling, " +
							"UseMoney) VALUES (?,?,?,?,?,?,?);";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setString(1, econShop.getName());
			prep.setInt(2, econShop.getBalance());
			prep.setBoolean(3, econShop.isAllItemsInfinite());
			prep.setBoolean(4, econShop.isCanRestock());
			prep.setBoolean(5, econShop.isAllowBuying());
			prep.setBoolean(6, econShop.isAllowSelling());
			prep.setBoolean(7, econShop.isUseMoney());
			
			// Execute the update
			prep.executeUpdate();
			
			/*
			 * Get and store their id for later use
			 */

			// Setup new query
			query = "SELECT ID FROM Shops WHERE Name = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setString(1, econShop.getName());
			
			// Execute and store results
			rs = prep.executeQuery();
			
			// Double check there is a result
			if (rs.next())
			{
				// Set the shops id
				econShop.setSQLID(rs.getInt("ID"));
			}
			
			// Close the result set
			rs.close();
		}
		
		/*
		 * Delete all of the shops current items from the table, this is a hard save
		 */

		// Setup delete query
		query = "DELETE FROM ShopItems WHERE ShopID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		prep.setInt(1, econShop.getSqlID());
		
		// Execute the update
		prep.executeUpdate();
		
		/*
		 * Add the new items to the table
		 */

		// Setup the add query
		query =
				"INSERT INTO ShopItems (ShopID,ItemID,CurrentStock,IsInfinite,BuyPrice,SellPrice) " +
						"VALUES (?,?,?,?,?,?);";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		// Loop through the shops inventory and add each one to the batch
		for (EconItemStack iter : econShop.getInventory())
		{
			// Set the values
			prep.setInt(1, econShop.getSqlID());
			prep.setInt(2, iter.getItemsID());
			prep.setInt(3, iter.getAmount());
			prep.setBoolean(4, iter.isInfinite());
			prep.setInt(5, iter.getPriceBuy());
			prep.setInt(6, iter.getPriceSell());
			
			// Add to batch
			prep.addBatch();
		}
		
		// Execute the batch
		prep.executeBatch();
	}
	
	/**
	 * Updates the given item in the ShopItems table for the given shopID. If the shop item is not listed,
	 * it will then add it to the table.
	 * 
	 * @param shopID
	 * @param shopItem
	 * @throws SQLException
	 */
	public void softShopUpdateItem(int shopID, EconItemStack shopItem) throws SQLException
	{
		// Declare variables
		String query = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int rowsAffected = 0;
		
		/*
		 * Attempt to update the existing entry
		 */

		// Setup the query
		query = "UPDATE ShopItems SET CurrentStock = ? WHERE ID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		prep.setInt(1, shopItem.getAmount());
		prep.setInt(2, shopItem.getSqlID());
		
		// Execute the update, store the rows affected
		rowsAffected = prep.executeUpdate();
		
		/*
		 * Check for 0 affected rows, as this means there is no entry. If it is then add the new entry
		 */

		if (rowsAffected == 0)
		{
			/*
			 * Add a new entry
			 */

			// Setup the query
			query =
					"INSERT INTO ShopItems (ShopID,ItemID,CurrentStock,IsInfinite,BuyPrice,SellPrice) " +
							"VALUES (?,?,?,?,?,?);";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setInt(1, shopID);
			prep.setInt(2, shopItem.getItemsID());
			prep.setInt(3, shopItem.getAmount());
			prep.setBoolean(4, shopItem.isInfinite());
			prep.setInt(5, shopItem.getPriceBuy());
			prep.setInt(6, shopItem.getPriceSell());
			
			// Execute the update
			prep.executeUpdate();
			
			/*
			 * Get and store their id for later use
			 */

			// Setup the query
			query = "SELECT ID FROM ShopItems WHERE ShopID = ? AND ItemID = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setInt(1, shopID);
			prep.setInt(2, shopItem.getItemsID());
			
			// Execute query and get results
			rs = prep.executeQuery();
			
			// Double check there is a result
			if (rs.next())
			{
				// Set the id
				shopItem.setSqlID(rs.getInt("ID"));
			}
			
			// Close the result set
			rs.close();
		}
	}
	
	/**
	 * @param econShop
	 * @throws SQLException
	 */
	public void softShopUpdate(EconShop econShop) throws SQLException
	{
		// Declare variables
		String query = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		int[] batchRowsAffected = null;
		
		// Check for a new shop (id = 0)
		if (econShop.getSqlID() != 0)
		{
			// Setup the query
			query = "UPDATE Shops SET Balance = ? WHERE ID = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setInt(1, econShop.getBalance());
			prep.setInt(2, econShop.getSqlID());
			
			// Execute the update
			prep.executeUpdate();
		}
		else
		{
			/*
			 * Add the shop to the table
			 */

			// Setup the query
			query =
					"INSERT INTO Shops (Name, Balance, AllItemsInfinite, CanRestock, AllowBuying, AllowSelling, " +
							"UseMoney) VALUES (?,?,?,?,?,?,?);";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setString(1, econShop.getName());
			prep.setInt(2, econShop.getBalance());
			prep.setBoolean(3, econShop.isAllItemsInfinite());
			prep.setBoolean(4, econShop.isCanRestock());
			prep.setBoolean(5, econShop.isAllowBuying());
			prep.setBoolean(6, econShop.isAllowSelling());
			prep.setBoolean(7, econShop.isUseMoney());
			
			// Execute the update
			prep.executeUpdate();
			
			/*
			 * Get and store their id for later use
			 */

			// Setup new query
			query = "SELECT ID FROM Shops WHERE Name = ?;";
			
			// Prepare it
			prep = prepare(query);
			
			// Set the values
			prep.setString(1, econShop.getName());
			
			// Execute and store results
			rs = prep.executeQuery();
			
			// Double check there is a result
			if (rs.next())
			{
				// Set the shops id
				econShop.setSQLID(rs.getInt("ID"));
			}
			
			// Close the result set
			rs.close();
		}
		
		/*
		 * Items
		 */

		// Setup the query
		query = "UPDATE ShopItems SET CurrentStock = ? WHERE ID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Loop through the inventory and add each to the batch
		for (EconItemStack item : econShop.getInventory())
		{
			// Set the values
			prep.setInt(1, item.getAmount());
			prep.setInt(2, item.getSqlID());
			
			// Add it to the batch
			prep.addBatch();
		}
		
		// Execute the batch and assign the results to batchRowsAffected
		batchRowsAffected = prep.executeBatch();
		
		/*
		 * Add any items not currently in the table
		 */

		// Setup the query to add new items
		query =
			"INSERT INTO ShopItems (ShopID,ItemID,CurrentStock,IsInfinite,BuyPrice,SellPrice) VALUES (?,?,?,?,?,?);";
		
		// Prepare it
		prep = prepare(query);
		
		for (int i = 0; i < batchRowsAffected.length; i++)
		{
			// Check if 0 rows affected, if so it means there was no table entry, so add one.
			if (batchRowsAffected[i] == 0)
			{
				// Set the values
				prep.setInt(1, econShop.getSqlID());
				prep.setInt(2, econShop.getInventory().get(i).getItemsID());
				prep.setInt(3, econShop.getInventory().get(i).getAmount());
				prep.setBoolean(4, econShop.getInventory().get(i).isInfinite());
				prep.setInt(5, econShop.getInventory().get(i).getPriceBuy());
				prep.setInt(6, econShop.getInventory().get(i).getPriceSell());
				
				// Add it to the batch
				prep.addBatch();
			}
		}
		
		// Execute the batch
		prep.executeBatch();
		
		/*
		 * Assign the new ID numbers
		 */

		// Setup ID select query
		query = "SELECT ID,ItemID FROM ShopItems WHERE ShopID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set the values
		prep.setInt(1, econShop.getSqlID());
		
		// Execute and store the results
		rs = prep.executeQuery();
		
		// Loop through the results
		while (rs.next())
		{
			// For each result, we loop through the EconShops inventory and check the items ItemID against the results.
			// A match means it is our item
			for (EconItemStack item : econShop.getInventory())
			{
				// Check for match
				if (rs.getInt("ItemID") == item.getItemsID())
				{
					// Set the sqlID to the one found
					item.setSqlID(rs.getInt("ID"));
				}
			}
		}
		
		// Close the result set
		rs.close();
		
		//		query = "";
		//
		//		for (EconItemStack item : econShop.getInventory())
		//		{
		//			/*
		//			 * Attempt to update the existing entry
		//			 */
		//
		//			// Setup the query
		//			query = "UPDATE items SET CurrentStock = ? WHERE ID = ?;";
		//
		//			// Prepare it
		//			prep = prepare(query);
		//
		//			// Set the values
		//			prep.setInt(1, item.getAmount());
		//			prep.setInt(2, item.getSqlID());
		//
		//			// Execute the update, store the rows affected
		//			rowsAffected = prep.executeUpdate();
		//
		//			/*
		//			 * Check for 0 affected rows, as this means there is no entry.
		//			 * If it is then add the new entry
		//			 */
		//
		//			if (rowsAffected == 0)
		//			{
		//				/*
		//				 * Add a new entry
		//				 */
		//
		//				// Setup the query
		//				query = "INSERT INTO items (ShopID,ItemID,CurrentStock," + "IsInfinite,BuyPrice,SellPrice) " +
		//						"VALUES (?,?,?,?,?,?);";
		//
		//				// Prepare it
		//				prep = prepare(query);
		//
		//				// Set the values
		//				prep.setInt(1, econShop.getSqlID());
		//				prep.setInt(2, item.getItemsID());
		//				prep.setInt(3, item.getAmount());
		//				prep.setBoolean(4, item.isInfinite());
		//				prep.setInt(5, item.getPriceBuy());
		//				prep.setInt(6, item.getPriceSell());
		//
		//				// Execute the update
		//				prep.executeUpdate();
		//
		//				/*
		//				 * Get and store their id for later use
		//				 */
		//
		//				// Setup the query
		//				query = "SELECT ID FROM ShopItems WHERE ShopID = ? AND ItemID = ?;";
		//
		//				// Prepare it
		//				prep = prepare(query);
		//
		//				// Set the values
		//				prep.setInt(1, econShop.getSqlID());
		//				prep.setInt(2, item.getItemsID());
		//
		//				// Execute query and get results
		//				rs = prep.executeQuery();
		//
		//				// Double check there is a result
		//				if (rs.next())
		//				{
		//					// Set the id
		//					item.setSqlID(rs.getInt("ID"));
		//				}
		//
		//				// Close the result set
		//				rs.close();
		//			}
		//		}
	}
	
	/**
	 * Populates the given ArrayList with all items in the Items table.
	 * 
	 * @param rawItems
	 */
	public void populateRawItems(ArrayList<EconItemStack> rawItems) throws SQLException
	{
		// Declare variables
		String query = null;
		PreparedStatement prep = null;
		ResultSet rs = null;
		
		// Setup query
		query = "SELECT * FROM Items;";
		
		// Prepare it
		prep = prepare(query);
		
		// Execute and store the results
		rs = prep.executeQuery();
		
		// Loop through the results
		while (rs.next())
		{
			// Add the result to the array
			rawItems.add(new EconItemStack(rs.getInt("ID"), rs.getString("Name"), rs.getInt("ItemID"),
				rs.getBoolean("IsSubtyped"), rs.getShort("Subtype"), rs.getInt("BuyPrice"), rs.getInt("SellPrice"),
				rs.getInt("BaseValue"), rs.getFloat("Slope"), plugin));
		}
		
		// Close the result set
		rs.close();
	}
	
	/**
	 * Logs the given EconItemStack as a buy attached to the given econPlayer. Updates and adds to tables.
	 * 
	 * @param econPlayer
	 * @param item
	 */
	public void logBuy(EconPlayer econPlayer, EconItemStack item) throws SQLException
	{
		// Declare variables
		String query = null;
		PreparedStatement prep = null;
		
		/*
		 * Insert buy into the table
		 */

		// Setup query
		query = "INSERT INTO ShopTransactions (PlayerID,ShopItemID,ItemAmount,MoneyAmount,PlayerBought) " +
					"VALUES (?,?,?,?,?);";
		
		// Prepare it
		prep = prepare(query);
		
		// Set values
		prep.setInt(1, econPlayer.getSQLID());
		prep.setInt(2, item.getSqlID());
		prep.setInt(3, item.getAmount());
		prep.setInt(4, item.getTotalBuy());
		prep.setBoolean(5, true);
		
		// Execute the update
		prep.executeUpdate();
		
		/*
		 * Update the players last shop transaction
		 */

		// Setup query
		query = "UPDATE Players SET LastShopTransaction = (SELECT MAX(ID) FROM ShopTransactions) WHERE Players.ID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set values
		prep.setInt(1, econPlayer.getSQLID());
		
		// Execute and check output, if 0 show error
		if (prep.executeUpdate() == 0)
		{
			plugin.getLog().log(Level.WARNING, "ShopTransaction Buy update for: " + econPlayer.getPlayer().getName() +
				" failed.");
		}
	}
	
	/**
	 * Logs the given EconItemStack as a sell attached to the given econPlayer. Updates and adds to tables.
	 * 
	 * @param econPlayer
	 * @param item
	 */
	public void logSell(EconPlayer econPlayer, EconItemStack item) throws SQLException
	{
		// Declare variables
		String query = null;
		PreparedStatement prep = null;
		
		// Setup query
		query = "INSERT INTO ShopTransactions (PlayerID,ShopItemID,ItemAmount,MoneyAmount,PlayerBought) "
						+ "VALUES (?,?,?,?,?);";
		
		// Prepare it
		prep = prepare(query);
		
		// Set values
		prep.setInt(1, econPlayer.getSQLID());
		prep.setInt(2, item.getSqlID());
		prep.setInt(3, item.getAmount());
		prep.setInt(4, item.getTotalSell());
		prep.setBoolean(5, false);
		
		// Execute the update
		prep.executeUpdate();
		
		/*
		 * Update the players last shop transaction
		 */

		// Setup query
		query = "UPDATE Players SET LastShopTransaction = (SELECT MAX(ID) FROM ShopTransactions) WHERE Players.ID = ?;";
		
		// Prepare it
		prep = prepare(query);
		
		// Set values
		prep.setInt(1, econPlayer.getSQLID());
		
		// Execute and check output, if 0 show error
		if (prep.executeUpdate() == 0)
		{
			plugin.getLog().log(Level.WARNING, "ShopTransaction Sell update for: " + econPlayer.getPlayer().getName() +
				" failed.");
		}
	}
}
