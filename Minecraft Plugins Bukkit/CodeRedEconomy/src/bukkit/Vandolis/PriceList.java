/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

import java.util.ArrayList;

/**
 * Class used for displaying a shops stock to the player. Generates pages of items the player can look through
 * 
 * @author Vandolis
 */
public class PriceList {
	private static ArrayList<String[]>	pages	= new ArrayList<String[]>();
	
	/**
	 * @return the number of pages the {@link PriceList} is currently at.
	 */
	public static int getNumPages() {
		return pages.size();
	}
	
	/**
	 * Populates a price list based around the given {@link User} and {@link Shop}.
	 * 
	 * @param user
	 * @param shop
	 */
	private static void populate(User user, Shop shop) {
		/*
		 * Check a shop restock as well as reset the pages
		 */
		shop.restock(); // Restock the shop if needed
		pages = new ArrayList<String[]>();
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		
		// FIXME chance once bukkit implements permissions
		for (ShopItem iter : DataManager.getItemList()) {
			items.add(iter);
		}
		
		// for (ShopGroup giter : DataManager.getGroups()) {
		// if (user.getPlayer().isInGroup(giter.getGroupName())) {
		// for (int i : giter.getAllowed()) {
		// items.add(new ShopItem(i));
		// }
		// }
		// }
		
		/*
		 * System.out.println("items is " + items.size());
		 * 7 lines to each page
		 */
		int count = 0;
		String page[] = new String[7];
		
		for (ShopItem iter : items) {
			if (iter.getItemId() != 0) {
				String temp = "";
				int amount = 0;
				
				for (ShopItemStack iters : shop.getAvailItems()) {
					if (iters.getItemId() == iter.getItemId()) {
						amount = iters.getAmountAvail();
					}
				}
				
				if (amount != DataManager.getInfValue()) {
					temp += iter.getName() + ": §a" + iter.getBuyPrice() + " §c" + iter.getSellPrice() + " §e" + amount;
				}
				else {
					temp += iter.getName() + ": §a" + iter.getBuyPrice() + " §c" + iter.getSellPrice() + " §eInfinite";
				}
				
				page[count] = temp;
				count++;
				
				if ((count == 7) || iter.equals(items.get(items.size() - 1))) {
					// System.out.println("Adding a page.");
					
					if (page[0] != null) {
						pages.add(page);
						count = 0;
						page = new String[7];
					}
				}
			}
		}
	}
	
	/**
	 * Populates and displays a priceList to the given {@link User}. Shows the given page.
	 * 
	 * @param user
	 * @param page
	 * @param shop
	 */
	public static void priceList(User user, int page, Shop shop) {
		populate(user, shop);
		
		if ((pages.size() >= page) && (page > 0) && (pages.size() != 0)) {
			user.sendMessage("Price List: (Page " + page + " of " + pages.size() + ")");
			
			for (String iter : pages.get(page - 1)) {
				if (iter != null) {
					user.sendMessage("   " + iter);
				}
			}
		}
		else if (pages.size() == 0) {
			user.sendMessage("There are no items you can buy.");
		}
		else if (pages.size() >= 1) {
			user.sendMessage("Page numbers are 1 - " + pages.size());
		}
		else {
			System.out.println("Uknown situation in priceList");
		}
	}
}
