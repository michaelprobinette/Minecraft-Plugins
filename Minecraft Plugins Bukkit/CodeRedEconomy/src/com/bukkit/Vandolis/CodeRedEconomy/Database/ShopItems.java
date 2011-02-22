/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class ShopItems {
	private int		ID;
	private int		shopID			= 0;
	private int		itemID			= 0;
	private int		currentStock;
	private int		minimumStock	= 0;
	private int		maximumStock	= 0;
	private boolean	infinite		= false;
	private boolean	dynamicPrice	= false;
	private double	dynamicPriceFactor;
	private double	buyPrice		= 0;
	private double	sellPrice		= 0;
	private float	buyMultiplier	= 1.0f;
	private float	sellMultiplier	= 1.0f;
	private int		maxSellAmount	= EconomyProperties.getInfValue();
	private int		maxBuyAmount	= EconomyProperties.getInfValue();
	private int		maxSellInterval	= 0;
	private int		maxBuyInterval	= 0;
	private boolean	newEntry		= false;
	
	/**
	 * @param shopItemID
	 */
	public ShopItems(int shopItemID) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs = stat.executeQuery("select * from ShopItems where ID = " + shopItemID + ";");
			
			if (rs.next()) {
				ID = rs.getInt("ID");
				shopID = rs.getInt("ShopID");
				itemID = rs.getInt("ItemID");
				currentStock = rs.getInt("CurrentStock");
				minimumStock = rs.getInt("MinimumStock");
				maximumStock = rs.getInt("MaximumStock");
				infinite = rs.getBoolean("IsInfinite");
				dynamicPrice = rs.getBoolean("IsDynamicPrice");
				dynamicPriceFactor = rs.getDouble("DynamicPriceFactor");
				buyPrice = rs.getDouble("BuyPrice");
				sellPrice = rs.getDouble("SellPrice");
				buyMultiplier = rs.getFloat("BuyMultiplier");
				sellMultiplier = rs.getFloat("SellMultiplier");
				maxSellAmount = rs.getInt("MaxSellAmount");
				maxBuyAmount = rs.getInt("MaxBuyAmount");
				maxSellInterval = rs.getInt("MaxSellInterval");
				maxBuyInterval = rs.getInt("MaxBuyInterval");
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public ShopItems(int _ID, int _shopID, int _itemID, int _currentStock, int _minimumStock, int _maximumStock, boolean _infinite, boolean _dynamicPrice,
			double _dynamicPriceFactor, double _buyPrice, double _sellPrice, float _buyMultiplier, float _sellMultiplier, int _maxSellAmount, int _maxBuyAmount,
			int _maxSellInterval, int _maxBuyInterval) {

				ID = _ID;
				shopID = _shopID;
				itemID = _itemID;
				currentStock = _currentStock;
				minimumStock = _minimumStock;
				maximumStock = _maximumStock;
				infinite = _infinite;
				dynamicPrice = _dynamicPrice;
				dynamicPriceFactor = _dynamicPriceFactor;
				buyPrice = _buyPrice;
				sellPrice = _sellPrice;
				buyMultiplier = _buyMultiplier;
				sellMultiplier = _sellMultiplier;
				maxSellAmount = _maxSellAmount;
				maxBuyAmount = _maxBuyAmount;
				maxSellInterval = _maxSellInterval;
				maxBuyInterval = _maxBuyInterval;
			
	}
	
	/**
	 * @param id2
	 * @param id3
	 * @param amountAvail
	 * @param b
	 * @param buyPrice2
	 * @param sellPrice2
	 */
	public ShopItems(int id2, int id3, int amountAvail, boolean b, int buyPrice2, int sellPrice2) {
		shopID = id2;
		itemID = id3;
		currentStock = amountAvail;
		infinite = b;
		buyPrice = buyPrice2;
		sellPrice = sellPrice2;
		
		newEntry = true;
		
		update();
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
		update();
	}
	
	/**
	 * @return the shopID
	 */
	public int getShopID() {
		return shopID;
	}
	
	/**
	 * @param shopID
	 *            the shopID to set
	 */
	public void setShopID(int shopID) {
		this.shopID = shopID;
		update();
	}
	
	/**
	 * @return the itemID
	 */
	public int getItemID() {
		return itemID;
	}
	
	/**
	 * @param itemID
	 *            the itemID to set
	 */
	public void setItemID(int itemID) {
		this.itemID = itemID;
		update();
	}
	
	/**
	 * @return the currentStock
	 */
	public int getCurrentStock() {
		return currentStock;
	}
	
	/**
	 * @param currentStock
	 *            the currentStock to set
	 */
	public void setCurrentStock(int currentStock) {
		if ((this.currentStock != EconomyProperties.getInfValue()) && !infinite) {
			this.currentStock = currentStock;
			update();
		}
	}
	
	/**
	 * @return the minimumStock
	 */
	public int getMinimumStock() {
		return minimumStock;
	}
	
	/**
	 * @param minimumStock
	 *            the minimumStock to set
	 */
	public void setMinimumStock(int minimumStock) {
		this.minimumStock = minimumStock;
		update();
	}
	
	/**
	 * @return the maximumStock
	 */
	public int getMaximumStock() {
		return maximumStock;
	}
	
	/**
	 * @param maximumStock
	 *            the maximumStock to set
	 */
	public void setMaximumStock(int maximumStock) {
		this.maximumStock = maximumStock;
		update();
	}
	
	/**
	 * @return the infinite
	 */
	public boolean isInfinite() {
		return infinite;
	}
	
	/**
	 * @param infinite
	 *            the infinite to set
	 */
	public void setInfinite(boolean infinite) {
		this.infinite = infinite;
		update();
	}
	
	/**
	 * @return the dynamicPrice
	 */
	public boolean isDynamicPrice() {
		return dynamicPrice;
	}
	
	/**
	 * @param dynamicPrice
	 *            the dynamicPrice to set
	 */
	public void setDynamicPrice(boolean dynamicPrice) {
		this.dynamicPrice = dynamicPrice;
		update();
	}
	
	/**
	 * @return the dynamicPriceFactor
	 */
	public double getDynamicPriceFactor() {
		return dynamicPriceFactor;
	}
	
	/**
	 * @param dynamicPriceFactor
	 *            the dynamicPriceFactor to set
	 */
	public void setDynamicPriceFactor(double dynamicPriceFactor) {
		this.dynamicPriceFactor = dynamicPriceFactor;
		update();
	}
	
	/**
	 * @return the buyPrice
	 */
	public double getBuyPrice() {
		return buyPrice;
	}
	
	/**
	 * @return the sellPrice
	 */
	public double getSellPrice() {
		return sellPrice;
	}
	
	/**
	 * @param price
	 *            the buyPrice to set
	 */
	public void setBuyPrice(double buyPrice) {
		this.buyPrice = buyPrice;
		update();
	}
	
	/**
	 * @param price
	 *            the sellPrice to set
	 */
	public void setSellPrice(double sellPrice) {
		this.sellPrice = sellPrice;
		update();
	}
	
	/**
	 * @return the buyMultiplier
	 */
	public float getBuyMultiplier() {
		return buyMultiplier;
	}
	
	/**
	 * @param buyMultiplier
	 *            the buyMultiplier to set
	 */
	public void setBuyMultiplier(float buyMultiplier) {
		this.buyMultiplier = buyMultiplier;
		update();
	}
	
	/**
	 * @return the sellMultiplier
	 */
	public float getSellMultiplier() {
		return sellMultiplier;
	}
	
	/**
	 * @param sellMultiplier
	 *            the sellMultiplier to set
	 */
	public void setSellMultiplier(float sellMultiplier) {
		this.sellMultiplier = sellMultiplier;
		update();
	}
	
	/**
	 * @return the maxSellAmount
	 */
	public int getMaxSellAmount() {
		return maxSellAmount;
	}
	
	/**
	 * @param maxSellAmount
	 *            the maxSellAmount to set
	 */
	public void setMaxSellAmount(int maxSellAmount) {
		this.maxSellAmount = maxSellAmount;
		update();
	}
	
	/**
	 * @return the maxBuyAmount
	 */
	public int getMaxBuyAmount() {
		return maxBuyAmount;
	}
	
	/**
	 * @param maxBuyAmount
	 *            the maxBuyAmount to set
	 */
	public void setMaxBuyAmount(int maxBuyAmount) {
		this.maxBuyAmount = maxBuyAmount;
		update();
	}
	
	/**
	 * @return the maxSellInterval
	 */
	public int getMaxSellInterval() {
		return maxSellInterval;
	}
	
	/**
	 * @param maxSellInterval
	 *            the maxSellInterval to set
	 */
	public void setMaxSellInterval(int maxSellInterval) {
		this.maxSellInterval = maxSellInterval;
		update();
	}
	
	/**
	 * @return the maxBuyInterval
	 */
	public int getMaxBuyInterval() {
		return maxBuyInterval;
	}
	
	/**
	 * @param maxBuyInterval
	 *            the maxBuyInterval to set
	 */
	public void setMaxBuyInterval(int maxBuyInterval) {
		this.maxBuyInterval = maxBuyInterval;
		update();
	}
	
	/**
	 * @param shopId2
	 * @param itemId2
	 * @return SQL id
	 */
	public static int getShopItemId(int shopId2, int itemId2) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs =
					stat.executeQuery("select ShopItems.ID from ShopItems where ShopID = " + shopId2 + " and ItemID = " + itemId2 + ";");
			
			int id = 0;
			
			if (rs.next()) {
				id = rs.getInt("ID");
			}
			
			if (EconomyProperties.isDebug()) {
				System.out.println("Returning shopItemID: " + id);
			}
			
			rs.close();
			stat.close();
			
			return id;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * @param shopItemID
	 * @return the shopID
	 */
	public static int getShopID(int shopItemID) {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			ResultSet rs = stat.executeQuery("select ShopItems.ShopID from ShopItems where ID = " + shopItemID + ";");
			
			int ID = 0;
			
			if (rs.next()) {
				rs.getInt("ShopID");
			}
			
			rs.close();
			stat.close();
			
			return ID;
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return -1;
	}
	
	/**
	 * Updates the shopItems entry in SQL, adds if new entry
	 */
	public void update() {
		try {
			if (newEntry) {
				PreparedStatement prep =
						EconomyProperties.getConn().prepareStatement(
								"insert into ShopItems (ShopID, ItemID, CurrentStock, MinimumStock, MaximumStock, "
										+ "IsInfinite, IsDynamicPrice, DynamicPriceFactor, BuyPrice, SellPrice, BuyMultiplier, "
										+ "SellMultiplier, MaxSellAmount, MaxBuyAmount, MaxSellInterval, MaxBuyInterval) values "
										+ "(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);");
				
				prep.setInt(1, shopID);
				prep.setInt(2, itemID);
				prep.setInt(3, currentStock);
				prep.setInt(4, minimumStock);
				prep.setInt(5, maximumStock);
				prep.setBoolean(6, infinite);
				prep.setBoolean(7, dynamicPrice);
				prep.setDouble(8, dynamicPriceFactor);
				prep.setDouble(9, buyPrice);
				prep.setDouble(10, sellPrice);
				prep.setFloat(11, buyMultiplier);
				prep.setFloat(12, sellMultiplier);
				prep.setInt(13, maxSellAmount);
				prep.setInt(14, maxBuyAmount);
				prep.setInt(15, maxSellInterval);
				prep.setInt(16, maxBuyInterval);
				
				prep.execute();
				
				prep.close();
				
				newEntry = false;
			}
			else {
				PreparedStatement prep =
						EconomyProperties
								.getConn()
								.prepareStatement(
										"update ShopItems set ShopID = ?, ItemID = ?, CurrentStock = ?, MinimumStock = ?, MaximumStock = ?, "
												+ "IsInfinite = ?, IsDynamicPrice = ?, DynamicPriceFactor = ?, BuyPrice = ?, SellPrice = ?, "
												+ "BuyMultiplier = ?, SellMultiplier = ?, MaxSellAmount = ?, MaxBuyAmount = ?, MaxSellInterval = ?, "
												+ "MaxBuyInterval = ? where ID = " + ID + ";");
				
				prep.setInt(1, shopID);
				prep.setInt(2, itemID);
				prep.setInt(3, currentStock);
				prep.setInt(4, minimumStock);
				prep.setInt(5, maximumStock);
				prep.setBoolean(6, infinite);
				prep.setBoolean(7, dynamicPrice);
				prep.setDouble(8, dynamicPriceFactor);
				prep.setDouble(9, buyPrice);
				prep.setDouble(10, sellPrice);
				prep.setFloat(11, buyMultiplier);
				prep.setFloat(12, sellMultiplier);
				prep.setInt(13, maxSellAmount);
				prep.setInt(14, maxBuyAmount);
				prep.setInt(15, maxSellInterval);
				prep.setInt(16, maxBuyInterval);
				
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
	 * Removes the ShopItems from the table
	 */
	public void remove() {
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			stat.executeUpdate("delete from ShopItems where ID = " + ID + ";");
			
			stat.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
