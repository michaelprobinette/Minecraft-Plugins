/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.Database;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	
	private static void populate(String shopName) {
		currentShop = shopName;
		
		int shopID = Shops.getId(shopName);
		
		Shops shop = null;
		
		if (shopID == 0) {
			shop = new Shops("The Shop");
		}
		else {
			shop = new Shops(shopID);
		}
		
		if (EconomyProperties.isDebug()) {
			System.out.println("ShopID: " + shop.getID());
		}
		
		String[] page = new String[pageLength];
		
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs = stat.executeQuery("select ShopItems.ID from ShopItems where ShopID = " + shop.getID() + ";");
			
			//			ResultSet rs =
			//					stat.executeQuery("select ShopItems.ItemID, ShopItems.CurrentStock, ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice"
			//							+ " from ShopItems where ShopID = " + shop.getID() + ";");
			
			ArrayList<ShopItems> shopItems = new ArrayList<ShopItems>();
			
			while (rs.next()) {
				shopItems.add(new ShopItems(rs.getInt("ID")));
			}
			
			rs.close();
			stat.close();
			
			int pos = 0;
			
			for (ShopItems iter : shopItems) {
				Items item = new Items(iter.getItemID());
				
				if (iter.isInfinite() || shop.isAllItemsInfinite() || (iter.getCurrentStock() == EconomyProperties.getInfValue())) {
					page[pos] = item.getName() + " §a" + (int) iter.getBuyPrice() + " §c" + (int) iter.getSellPrice() + " §eInfinite";
				}
				else {
					page[pos] =
							item.getName() + " §a" + (int) iter.getBuyPrice() + " §c" + (int) iter.getSellPrice() + " §e"
									+ iter.getCurrentStock();
				}
				
				if (EconomyProperties.isDebug()) {
					System.out.println("Loaded line: " + page[pos]);
				}
				
				pos++;
				
				if (pos == pageLength) {
					pages.add(page);
					
					page = new String[pageLength];
					pos = 0;
				}
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
				if (rs.getBoolean("IsInfinite") || shop.isAllItemsInfinite()) {
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
