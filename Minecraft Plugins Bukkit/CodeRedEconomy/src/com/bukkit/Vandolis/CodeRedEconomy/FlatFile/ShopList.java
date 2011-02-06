/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import java.util.ArrayList;


/**
 * Class used to display shops to the user
 * 
 * @author Vandolis
 */
public class ShopList {
	private static ArrayList<ArrayList<String>>	pages			= new ArrayList<ArrayList<String>>();
	private static ArrayList<String>			shops			= new ArrayList<String>();
	private static long							lastPopulate	= 0;
	
	/**
	 * Populates the shop list with the shop names.
	 */
	public static void populate() {
		if (DataManager.getDebug()) {
			System.out.println("Populating the shops list.");
		}
		
		lastPopulate = DataManager.getServer().getTime();
		pages = new ArrayList<ArrayList<String>>();
		shops = new ArrayList<String>();
		for (Shop iter : DataManager.getShops()) {
			/*
			 * Only add shops that are set to be shown.
			 */
			if (!iter.isHidden()) {
				shops.add(iter.toString());
				if (shops.size() == 7) {
					/*
					 * That page is full
					 */
					pages.add(shops);
					shops = new ArrayList<String>();
				}
			}
		}
		
		if (shops.size() > 0) {
			/*
			 * Add the leftovers
			 */
			pages.add(shops);
		}
	}
	
	/**
	 * Displays the given page to the user.
	 * 
	 * @param user
	 *            {@link User} to send the messages to
	 * @param page
	 *            The page number to show (Default 1)
	 */
	public static void showPage(User user, int page) {
		/*
		 * Check if valid page number
		 */
		if ((page <= 0) || (page > pages.size())) {
			if (pages.size() == 0) {
				user.sendMessage("There are no shops to list.");
			}
			else {
				showPage(user, 1);
			}
		}
		else {
			/*
			 * Show the desired page
			 */
			user.sendMessage("Shop List: (Page " + page + " of " + pages.size() + ")");
			for (String iter : pages.get(page - 1)) {
				user.sendMessage("    " + iter);
			}
		}
		
	}
}
