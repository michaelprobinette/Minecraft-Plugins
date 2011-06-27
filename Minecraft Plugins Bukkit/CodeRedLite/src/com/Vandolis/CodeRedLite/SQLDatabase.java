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
import java.util.logging.Level;

import org.bukkit.entity.Player;

/**
 * @author Vandolis
 */
public class SQLDatabase
{
	private String		dataLocation	= null;
	private Connection	conn			= null;
	private CodeRedLite	plugin			= null;
	
	protected SQLDatabase(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
		
		dataLocation = "jdbc:sqlite:" + plugin.getDataFolder() + "/CodeRedLite.sqlite";
		
		try
		{
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection(dataLocation);
		}
		catch (Exception e)
		{
			plugin.getLog().log(Level.SEVERE, "CodeRedLite could not connect to the database. " + e.getLocalizedMessage());
		}
	}
	
	protected EconPlayer getEconPlayer(Player player) throws SQLException
	{
		PreparedStatement prep = conn.prepareStatement("SELECT ID, Balance, LastShopTransaction FROM Players WHERE Name = ?");
		prep.setString(1, player.getName());
		ResultSet rs = prep.executeQuery();
		
		EconPlayer econPlayer = new EconPlayer(player);
		
		if (rs.next())
		{
			econPlayer.setSQLID(rs.getInt("ID"));
			econPlayer.setBalance(rs.getInt("Balance"));
			econPlayer.setLastShopTransaction(rs.getInt("LastShopTransaction"));
		}
		
		rs.close();
		prep.close();
		
		return econPlayer;
	}
	
	/**
	 * @param econPlayer
	 */
	public void update(EconPlayer econPlayer) throws SQLException
	{
		if (econPlayer.getSQLID() == 0)
		{
			// New player
			PreparedStatement prep = conn.prepareStatement("INSERT INTO Players (Name,Balance,LastShopTransaction) VALUES (?,?,?);");
			prep.setString(1, econPlayer.getPlayer().getName());
			prep.setInt(2, econPlayer.getBalance());
			prep.setInt(3, econPlayer.getLastShopTransaction());
			
			prep.executeUpdate();
			
			prep.close();
		}
		else
		{
			// Old player
			PreparedStatement prep = conn.prepareStatement("UPDATE Players SET Balance = ?, LastShopTransaction = ? WHERE ID = ?;");
			prep.setInt(1, econPlayer.getBalance());
			prep.setInt(2, econPlayer.getLastShopTransaction());
			prep.setInt(3, econPlayer.getSQLID());
			
			prep.executeUpdate();
			
			prep.close();
		}
	}
	
	public EconShop getEconShop(String name) throws SQLException
	{
		EconShop econShop = new EconShop(name, plugin);
		
		PreparedStatement prep =
				conn.prepareStatement("SELECT ID, Balance, AllItemsInfinite, CanRestock, AllowBuying, AllowSelling, UseMoney FROM Shops WHERE Name = ?;");
		prep.setString(1, name);
		ResultSet rs = prep.executeQuery();
		
		if (rs.next())
		{
			econShop.setSQLID(rs.getInt("ID"));
			econShop.setBalance(rs.getInt("Balance"));
			econShop.setAllItemsInfinite(rs.getBoolean("AllItemsInfinite"));
			econShop.setCanRestock(rs.getBoolean("CanRestock"));
			econShop.setAllowBuying(rs.getBoolean("AllowBuying"));
			econShop.setAllowSelling(rs.getBoolean("AllowSelling"));
			econShop.setUseMoney(rs.getBoolean("UseMoney"));
		}
		
		prep.close();
		rs.close();
		
		//plugin.LOG.info("Loaded shop basics.");
		
		// Items
		prep =
				conn.prepareStatement("SELECT Items.ItemID, Items.IsSubtyped, Items.Subtype, Items.Name, ShopItems.CurrentStock, "
						+ "ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice, ShopItems.MaxSellAmount, ShopItems.MaxBuyAmount, "
						+ "ShopItems.ItemID AS ItemsID, ShopItems.ID, Items.BaseValue, Items.Slope " + "FROM Items,ShopItems "
						+ "WHERE ShopItems.ShopID = ? AND Items.ID = ShopItems.ItemID;");
		prep.setInt(1, econShop.getSqlID());
		
		//plugin.LOG.info("Constructed statement.");
		
		rs = prep.executeQuery();
		
		//plugin.LOG.info("Stored Results");
		
		while (rs.next())
		{
			econShop.getInventory().add(
					new EconItemStack(rs.getInt("ID"), rs.getInt("ItemID"), rs.getBoolean("IsSubtyped"), rs.getShort("Subtype"), rs
							.getString("Name"), rs.getInt("CurrentStock"), rs.getBoolean("IsInfinite"), rs.getInt("BuyPrice"), rs
							.getInt("SellPrice"), rs.getInt("MaxSellAmount"), rs.getInt("MaxBuyAmount"), rs.getInt("ItemsID"), rs
							.getInt("BaseValue"), rs.getFloat("Slope"), plugin));
			
			//plugin.LOG.info("Loaded item: " + rs.getString("Name"));
		}
		
		rs.close();
		prep.close();
		
		return econShop;
	}
	
	public void update(EconShop econShop) throws SQLException
	{
		if (econShop.getSqlID() != 0)
		{
			PreparedStatement prep = conn.prepareStatement("UPDATE Shops SET Balance = ? WHERE ID = ?;");
			prep.setInt(1, econShop.getBalance());
			prep.setInt(2, econShop.getSqlID());
			prep.executeUpdate();
			
			prep.close();
		}
		else
		{
			PreparedStatement prep =
					conn.prepareStatement("INSERT INTO Shops (Name, Balance, AllItemsInfinite, CanRestock, AllowBuying, AllowSelling, UseMoney) "
							+ "VALUES (?,?,?,?,?,?,?);");
			prep.setString(1, econShop.getName());
			prep.setInt(2, econShop.getBalance());
			prep.setBoolean(3, econShop.isAllItemsInfinite());
			prep.setBoolean(4, econShop.isCanRestock());
			prep.setBoolean(5, econShop.isAllowBuying());
			prep.setBoolean(6, econShop.isAllowSelling());
			prep.setBoolean(7, econShop.isUseMoney());
			prep.executeUpdate();
			
			prep.close();
			
			PreparedStatement prepID = conn.prepareStatement("SELECT ID FROM Shops WHERE Name = ?;");
			//prep = conn.prepareStatement("SELECT ID FROM Shops WHERE Name = ?;");
			prepID.setString(1, econShop.getName());
			ResultSet rs = prepID.executeQuery();
			if (rs.next())
			{
				econShop.setSQLID(rs.getInt("ID"));
			}
			rs.close();
		}
		
		// Items
		PreparedStatement prepI = conn.prepareStatement("DELETE FROM ShopItems WHERE ShopID = ?;");
		prepI.setInt(1, econShop.getSqlID());
		prepI.executeUpdate();
		
		prepI =
				conn.prepareStatement("INSERT INTO ShopItems (ShopID,ItemID,CurrentStock,IsInfinite,BuyPrice,SellPrice) VALUES (?,?,?,?,?,?);");
		
		for (EconItemStack iter : econShop.getInventory())
		{
			prepI.setInt(1, econShop.getSqlID());
			prepI.setInt(2, iter.getItemsID());
			prepI.setInt(3, iter.getAmount());
			prepI.setBoolean(4, iter.isInfinite());
			prepI.setInt(5, iter.getPriceBuy());
			prepI.setInt(6, iter.getPriceSell());
			prepI.addBatch();
		}
		
		prepI.executeBatch();
		
		prepI.close();
	}
	
	public void update(int shopID, EconItemStack shopItem) throws SQLException
	{
		PreparedStatement prep = conn.prepareStatement("UPDATE ShopItems SET CurrentStock = ? WHERE ShopID = ? AND ItemID = ?;");
		prep.setInt(1, shopItem.getAmount());
		prep.setInt(2, shopID);
		prep.setInt(3, shopItem.getItemsID());
		
		prep.executeUpdate();
		
		prep.close();
	}
	
	/**
	 * @param rawItems
	 */
	public void populateRawItems(ArrayList<EconItemStack> rawItems) throws SQLException
	{
		PreparedStatement prep = conn.prepareStatement("SELECT * FROM Items;");
		ResultSet rs = prep.executeQuery();
		
		while (rs.next())
		{
			rawItems.add(new EconItemStack(rs.getInt("ID"), rs.getString("Name"), rs.getInt("ItemID"), rs.getBoolean("IsSubtyped"), rs
					.getShort("Subtype"), rs.getInt("BuyPrice"), rs.getInt("SellPrice"), rs.getInt("BaseValue"), rs.getFloat("Slope"),
					plugin));
		}
		
		rs.close();
		prep.close();
	}
	
	/**
	 * @param econPlayer
	 * @param item
	 */
	public void logBuy(EconPlayer econPlayer, EconItemStack item) throws SQLException
	{
		PreparedStatement prep =
				conn.prepareStatement("INSERT INTO ShopTransactions (PlayerID,ShopItemID,ItemAmount,MoneyAmount,PlayerBought) "
						+ "VALUES (?,?,?,?,?);");
		prep.setInt(1, econPlayer.getSQLID());
		prep.setInt(2, item.getSqlID());
		prep.setInt(3, item.getAmount());
		prep.setInt(4, item.getTotalBuy());
		prep.setBoolean(5, true);
		
		prep.executeUpdate();
		
		prep.close();
	}
	
	/**
	 * @param econPlayer
	 * @param item
	 */
	public void logSell(EconPlayer econPlayer, EconItemStack item) throws SQLException
	{
		PreparedStatement prep =
				conn.prepareStatement("INSERT INTO ShopTransactions (PlayerID,ShopItemID,ItemAmount,MoneyAmount,PlayerBought) "
						+ "VALUES (?,?,?,?,?);");
		prep.setInt(1, econPlayer.getSQLID());
		prep.setInt(2, item.getSqlID());
		prep.setInt(3, item.getAmount());
		prep.setInt(4, item.getTotalSell());
		prep.setBoolean(5, false);
		
		prep.executeUpdate();
		
		prep.close();
	}
}
