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
		
		String[] page = new String[pageLength];
		
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs =
					stat.executeQuery("select ShopItems.ItemID, ShopItems.CurrentStock, ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice"
							+ " from ShopItems where ShopID = " + shop.getID() + ";");
			
			int pos = 0;
			
			while (rs.next()) {
				Items item = new Items(rs.getInt("ItemID"));
				
				if (rs.getBoolean("IsInfinite") || shop.isAllItemsInfinite()) {
					page[pos] =
							item.getName() + " §a" + (int) rs.getDouble("BuyPrice") + " §c" + (int) rs.getDouble("SellPrice")
									+ "§eInfinite";
				}
				else {
					page[pos] =
							item.getName() + " §a" + (int) rs.getDouble("BuyPrice") + " §c" + (int) rs.getDouble("SellPrice") + "§e"
									+ rs.getInt("CurrentStock");
				}
				
				pos++;
				
				if (pos == pageLength) {
					pages.add(page);
					
					page = new String[pageLength];
				}
			}
			
			if (page[0] != null) {
				pages.add(page);
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void priceList(Player player, String shopName, int pageNum) {
		if (!currentShop.equalsIgnoreCase(shopName)) {
			populate(shopName);
		}
		
		if ((pageNum <= pages.size()) && (pageNum > 0)) {
			player.sendMessage(EconomyProperties.getPluginMessage() + "Price List for " + shopName + ": (Page " + pageNum + " of "
					+ pages.size() + ")");
			
			for (String iter : pages.get(pageNum - 1)) {
				player.sendMessage(EconomyProperties.getPluginMessage() + "   " + iter);
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
		
		try {
			Statement stat = EconomyProperties.getConn().createStatement();
			
			ResultSet rs =
					stat.executeQuery("select ShopItems.ItemID, ShopItems.CurrentStock, ShopItems.IsInfinite, ShopItems.BuyPrice, ShopItems.SellPrice"
							+ " from ShopItems where ShopID = " + shop.getID() + " and ItemID = " + item.getID() + ";");
			
			if (rs.getBoolean("IsInfinite") || shop.isAllItemsInfinite()) {
				player.sendMessage(EconomyProperties.getPluginMessage() + item.getName() + " §a" + (int) rs.getDouble("BuyPrice") + " §c"
						+ (int) rs.getDouble("SellPrice") + "§eInfinite");
			}
			else {
				player.sendMessage(EconomyProperties.getPluginMessage() + item.getName() + " §a" + (int) rs.getDouble("BuyPrice") + " §c"
						+ (int) rs.getDouble("SellPrice") + "§e" + rs.getInt("CurrentStock"));
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
