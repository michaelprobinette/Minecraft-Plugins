/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.ArrayList;

import org.bukkit.entity.Player;

import com.bukkit.Vandolis.CodeRedEconomy.EconomyProperties;

/**
 * @author Vandolis
 */
public class PriceList {
	private static ArrayList<String[]>	pages		= new ArrayList<String[]>();
	private static String				currentShop	= "";
	private static int					pageLength	= EconomyProperties.getPageLength();
	
	/**
	 * @return the currentShop
	 */
	public static String getCurrentShop() {
		return currentShop;
	}
	
	/**
	 * @param currentShop
	 *            the currentShop to set
	 */
	public static void setCurrentShop(String currentShop) {
		PriceList.currentShop = currentShop;
	}
	
	public static void populate(String shopName) {
		Date timeStart, timeEnd;
		int timeShop = 0;
		int timeShopItems = 0;
		
		timeStart = EconomyProperties.getDate();
		
		currentShop = shopName;
		
		int shopID = Shops.getId(shopName);
		
		Shops shop = null;
		
		if (shopID == 0) {
			shop = new Shops("The Shop");
		}
		else {
			shop = new Shops(shopID);
		}
		
		timeEnd = EconomyProperties.getDate();
		timeShop = (int) (timeEnd.getTime() - timeStart.getTime());
		
		if (EconomyProperties.isDebug()) {
			System.out.println("ShopID: " + shop.getID());
			System.out.println("Getting shop took " + timeShop + " miliseconds.");
		}
		
		String[] page = new String[pageLength];
		
		try {
			timeStart = EconomyProperties.getDate();
			
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs =
					stat.executeQuery("select ShopItems.*,Items.Name from ShopItems,Items where ShopID = " + shop.getID()
							+ " AND Items.ID = ShopItems.ItemID;");
			
			Date singleStart, singleEnd;
			while (rs.next()) {
				ShopItems iter =
						new ShopItems(rs.getInt("ID"), rs.getInt("ShopID"), rs.getInt("ItemID"), rs.getInt("CurrentStock"),
								rs.getInt("MinimumStock"), rs.getInt("MaximumStock"), rs.getBoolean("IsInfinite"),
								rs.getBoolean("IsDynamicPrice"), rs.getDouble("DynamicPriceFactor"), rs.getDouble("BuyPrice"),
								rs.getDouble("SellPrice"), rs.getFloat("BuyMultiplier"), rs.getFloat("SellMultiplier"),
								rs.getInt("MaxSellAmount"), rs.getInt("MaxBuyAmount"), rs.getInt("MaxSellInterval"),
								rs.getInt("MaxBuyInterval"));
				int pos = 0;
				singleStart = EconomyProperties.getDate();
				if (iter.isInfinite() || shop.isAllItemsInfinite() || (iter.getCurrentStock() == EconomyProperties.getInfValue())) {
					page[pos] = rs.getString("Name") + " §a" + (int) iter.getBuyPrice() + " §c" + (int) iter.getSellPrice() + " §eInfinite";
				}
				else {
					page[pos] =
							rs.getString("Name") + " §a" + (int) iter.getBuyPrice() + " §c" + (int) iter.getSellPrice() + " §e"
									+ iter.getCurrentStock();
				}
				singleEnd = EconomyProperties.getDate();
				if (EconomyProperties.isDebug()) {
					System.out.println("Loaded line: " + page[pos] + " time taken: " + (singleEnd.getTime() - singleStart.getTime())
							+ " miliseconds.");
				}
				pos++;
				if (pos == pageLength) {
					pages.add(page);
					
					page = new String[pageLength];
					pos = 0;
				}
			}
			
			timeEnd = EconomyProperties.getDate();
			timeShopItems = (int) (timeEnd.getTime() - timeStart.getTime());
			
			if (EconomyProperties.isDebug()) {
				System.out.println("Populating shopIitems took " + timeShopItems + " miliseconds.");
			}
			
			rs.close();
			stat.close();
			
			if (EconomyProperties.isDebug()) {
				double totalTime = timeShop + timeShopItems;
				double percent;
				DecimalFormat two = new DecimalFormat("#.##");
				System.out.println("Total time spent: " + totalTime + " milliseconds.");
				percent = (timeShop / totalTime) * 100;
				System.out.println("Getting shop: " + two.format(percent) + "% at " + timeShop + " miliseconds");
				percent = (timeShopItems / totalTime) * 100;
				System.out.println("Populating ShopItems: " + two.format(percent) + "% at " + timeShopItems + " miliseconds");
			}
			
			if (page[0] != null) {
				pages.add(page);
			}
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void priceList(Player player, String shopName, int pageNum) {
		if (!currentShop.equalsIgnoreCase(shopName) || (pages.size() == 0)) {
			if (EconomyProperties.isDebug()) {
				System.out.println("Populating the price list...");
			}
			
			populate(shopName);
		}
		
		if ((pageNum <= pages.size()) && (pageNum > 0)) {
			player.sendMessage(EconomyProperties.getPluginMessage() + "Price List for " + shopName + ": (Page " + pageNum + " of "
					+ pages.size() + ")");
			
			for (String iter : pages.get(pageNum - 1)) {
				if (iter != null) {
					player.sendMessage(EconomyProperties.getPluginMessage() + "   " + iter);
				}
			}
		}
		else if (pages.size() == 0) {
			player.sendMessage(EconomyProperties.getPluginMessage() + shopName + " has no items to list.");
		}
		else {
			priceList(player, shopName, 1);
		}
	}
	
	public static void priceSingleItem(Player player, String shopName, String itemName) {
		Items item = new Items(Items.getId(itemName));
		Shops shop = new Shops(Shops.getId(shopName));
		
		if (EconomyProperties.isDebug()) {
			System.out.println("ItemID: " + item.getID());
			System.out.println("ShopID: " + shop.getID());
		}
		
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs =
					stat.executeQuery("select ShopItems.ItemID, ShopItems.CurrentStock, ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice"
							+ " from ShopItems where ShopID = " + shop.getID() + " and ItemID = " + item.getID() + ";");
			
			if (rs.next()) {
				if (rs.getBoolean("IsInfinite") || shop.isAllItemsInfinite()
						|| (rs.getInt("CurrentStock") == EconomyProperties.getInfValue())) {
					player.sendMessage(EconomyProperties.getPluginMessage() + item.getName() + " §a" + (int) rs.getDouble("BuyPrice")
							+ " §c" + (int) rs.getDouble("SellPrice") + " §eInfinite");
				}
				else {
					player.sendMessage(EconomyProperties.getPluginMessage() + item.getName() + " §a" + (int) rs.getDouble("BuyPrice")
							+ " §c" + (int) rs.getDouble("SellPrice") + " §e" + rs.getInt("CurrentStock"));
				}
			}
			else {
				player.sendMessage(EconomyProperties.getPluginMessage() + "Invalid item.");
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
