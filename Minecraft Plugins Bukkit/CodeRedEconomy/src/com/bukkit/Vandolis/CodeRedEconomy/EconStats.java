/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

package com.bukkit.Vandolis.CodeRedEconomy;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * A class for handling all of the buy/sell/pay stats going on.
 * 
 * @author Vandolis
 */
public class EconStats {
	private static int											numTrans	= 0;
	private static ArrayList<ShopItemStack>						itemsBought	= new ArrayList<ShopItemStack>();
	private static ArrayList<ShopItemStack>						itemsSold	= new ArrayList<ShopItemStack>();
	private static ArrayList<Transaction>						playersPaid	= new ArrayList<Transaction>();
	private static final String									regex		= DataManager.getStatsRegex();
	private static final String									regex2		= DataManager.getStats2Regex();
	private static HashMap<EconEntity, ArrayList<Transaction>>	shopStats	= null;
	
	/**
	 * Experimental, going to try this out and see how it goes.
	 * 
	 * @param trans
	 */
	public static void log(Transaction trans) {
		ShopItemStack stack = trans.getStack();
		if (trans.cashOnly()) {
			/*
			 * The transaction is a /pay
			 */
			if (DataManager.getDebug()) {
				System.out.println("Stats is processing a /pay transaction");
			}
			
			boolean found = false;
			
			for (Transaction iter : playersPaid) {
				if (iter.getBuyer().getName().equalsIgnoreCase(trans.getBuyer().getName())
						&& iter.getSeller().getName().equalsIgnoreCase(trans.getSeller().getName())) {
					/*Same two people, add to the amount*/
					iter.getAmount().addAmount(trans.getAmount().getAmount());
					found = true;
				}
			}
			
			if (!found) {
				playersPaid.add(trans);
			}
		}
		else if (trans.getSeller().getUser() == null) {
			/*
			 * The seller is a shop, see if we can find them in the list of shopStats
			 */

			if (shopStats == null) {
				shopStats = new HashMap<EconEntity, ArrayList<Transaction>>();
			}
			
			boolean found = false;
			
			for (EconEntity iter : shopStats.keySet()) {
				if (iter.getName().equalsIgnoreCase(trans.getSeller().getName())) {
					found = true;
					shopStats.get(iter).add(trans);
				}
			}
			
			if (!found) {
				ArrayList<Transaction> temp = new ArrayList<Transaction>();
				temp.add(trans);
				shopStats.put(trans.getSeller(), temp);
			}
		}
		else if (trans.getBuyer().getUser() == null) {
			/*
			 * Buyer is a shop
			 */

			if (shopStats == null) {
				shopStats = new HashMap<EconEntity, ArrayList<Transaction>>();
			}
			
			boolean found = false;
			
			for (EconEntity iter : shopStats.keySet()) {
				if (iter.getName().equalsIgnoreCase(trans.getBuyer().getName())) {
					found = true;
					shopStats.get(iter).add(trans);
				}
			}
			
			if (!found) {
				ArrayList<Transaction> temp = new ArrayList<Transaction>();
				temp.add(trans);
				shopStats.put(trans.getBuyer(), temp);
			}
		}
	}
	
	/**
	 * Process the given stack and adds the amount
	 * 
	 * @param stack
	 */
	public static void bought(ShopItemStack stack) {
		if (DataManager.getDebug()) {
			System.out.println("Stats is processing a bought transaction");
		}
		numTrans++;
		// Find if it is in the bought array
		boolean found = false;
		for (ShopItemStack iter : itemsBought) {
			if (stack != null) {
				if (iter.getItemId() == stack.getItemId()) {
					found = true;
					iter.addAmountAvail(stack.getAmountAvail());
					break;
				}
			}
		}
		if (!found) {
			itemsBought.add(stack);
		}
	}
	
	/**
	 * Loads the {@link EconStats} from the given data
	 * 
	 * @param data
	 */
	public static void loadStats(ArrayList<String> data) {
		for (String si : data) {
			/*Safety check*/
			if (si.length() >= 1) {
				if (si.charAt(0) == '&') {
					/*Player data*/
					String colonSplit[] = si.split(regex);
					
					if (colonSplit.length >= 2) {
						String buyerName = "";
						
						for (String iter : colonSplit) {
							if (DataManager.getDebug()) {
								System.out.println("Loading stats. Player string is: " + iter);
							}
							
							if (iter.length() >= 1) {
								if (iter.charAt(0) == '&') {
									buyerName = iter.replace("&", "");
									
									if (DataManager.getDebug()) {
										System.out.println("Buyer name is: " + buyerName);
									}
								}
								else {
									/*Seller data*/
									String spaceSplit[] = iter.split(regex2);
									int amount = 0;
									String sellerName = "";
									
									for (String spaceIter : spaceSplit) {
										try {
											amount = Integer.valueOf(spaceIter.trim());
										}
										catch (NumberFormatException e) {
											sellerName += spaceIter + regex2;
										}
									}
									
									sellerName = sellerName.trim();
									
									if (DataManager.getDebug()) {
										System.out.println("Loading stats. Adding new transaction: " + buyerName + regex2 + sellerName
												+ " amount: " + amount);
									}
									playersPaid.add(new Transaction(DataManager.getUser(sellerName), DataManager.getUser(buyerName),
											new Money(amount)));
								}
							}
						}
					}
				}
				else if (si.charAt(0) == '%') {
					/*Num of transactions*/
					si = si.replace("%", "");
					
					numTrans = Integer.valueOf(si);
				}
				else if (si.charAt(0) != '#') {
					/*Data line, read and add it*/
					String ssplit[] = si.split(regex);
					
					/*Safety check*/
					if (ssplit.length == 3) {
						int id = Integer.valueOf(ssplit[0]);
						int buyAmount = Integer.valueOf(ssplit[1]);
						int sellAmount = Integer.valueOf(ssplit[2]);
						
						if (buyAmount != 0) {
							/*Add it to the bought*/
							itemsBought.add(new ShopItemStack(id, buyAmount));
						}
						if (sellAmount != 0) {
							/*Add it to the bought*/
							itemsSold.add(new ShopItemStack(id, sellAmount));
						}
					}
				}
			}
		}
	}
	
	/**
	 * Process a player to player transaction
	 * 
	 * @param trans
	 */
	public static void paid(Transaction trans) {
		if (DataManager.getDebug()) {
			System.out.println("Stats is processing a /pay transaction");
		}
		
		boolean found = false;
		
		for (Transaction iter : playersPaid) {
			if (iter.getBuyer().getName().equalsIgnoreCase(trans.getBuyer().getName())
					&& iter.getSeller().getName().equalsIgnoreCase(trans.getSeller().getName())) {
				/*Same two people, add to the amount*/
				iter.getAmount().addAmount(trans.getAmount().getAmount());
				found = true;
			}
		}
		
		if (!found) {
			playersPaid.add(trans);
		}
	}
	
	/**
	 * Process a sold stack and add it to current count
	 * 
	 * @param stack
	 */
	public static void sold(ShopItemStack stack) {
		if (DataManager.getDebug()) {
			System.out.println("Stats is processing a sold transaction");
		}
		
		numTrans++;
		boolean found = false;
		
		for (ShopItemStack iter : itemsSold) {
			if (iter.getItemId() == stack.getItemId()) {
				found = true;
				
				iter.addAmountAvail(stack.getAmountAvail());
				
				break;
			}
		}
		
		if (!found) {
			itemsSold.add(stack);
		}
	}
	
	/**
	 * Used to generate a save string for the current {@link EconStats}
	 * 
	 * @return
	 */
	public static ArrayList<String> getSaveString() {
		/*
		 * # <-- Used as a comment, easy to parse version will be at the bottom without
		 */

		if (shopStats == null) {
			/*
			 * ItemName Bought: Amount bought Sold: Amount Sold TotalSellPrice: totalSell TotalBuyPrice: totalBuy
			 */
			ArrayList<String> results = new ArrayList<String>();
			String temp = "";
			int totGained = 0;
			int totSpent = 0;
			
			/*
			 * Count totals for %
			 */
			for (ShopItem iter : DataManager.getItemList()) {
				for (ShopItemStack iter2 : itemsBought) {
					if (iter2.getItemId() == iter.getItemId()) {
						totSpent += iter2.getTotalBuyPrice().getAmount();
					}
				}
				
				for (ShopItemStack iter3 : itemsSold) {
					if (iter3.getItemId() == iter.getItemId()) {
						totGained += iter3.getTotalSellPrice().getAmount();
					}
				}
			}
			
			/*
			 * Count the amount of each item
			 */
			for (ShopItem iter : DataManager.getItemList()) {
				String itemName = iter.getName();
				int amountBought = 0;
				int amountSold = 0;
				double sellPrice = 0;
				double buyPrice = 0;
				
				for (ShopItemStack iter2 : itemsBought) {
					if (iter2.getItemId() == iter.getItemId()) {
						amountBought = iter2.getAmountAvail();
						buyPrice = iter2.getTotalBuyPrice().getAmount();
					}
				}
				
				for (ShopItemStack iter3 : itemsSold) {
					if (iter3.getItemId() == iter.getItemId()) {
						amountSold = iter3.getAmountAvail();
						sellPrice = iter3.getTotalSellPrice().getAmount();
					}
				}
				
				/*
				 * Makes the % of total buy and sell
				 */
				DecimalFormat two = new DecimalFormat("#.##");
				double spentP = (buyPrice / totSpent) * 100;
				double gainedP = (sellPrice / totGained) * 100;
				
				/*
				 * Sets up the string parts for formating
				 */
				String part1 = "#" + itemName;
				String part2mod = "";
				String part2 = "Purchased: " + amountBought;
				String part3mod = "";
				String part3 = "Sold: " + amountSold;
				String part4mod = "";
				String part4 = "Spent: " + (int) buyPrice + " (" + two.format(spentP) + "%)";
				String part5mod = "";
				String part5 = "Gained: " + (int) sellPrice + " (" + two.format(gainedP) + "%)";
				
				/*
				 * Adjusts the string parts to keep indenting correct
				 */
				if (part1.length() < 8) {
					part2mod = "\t\t\t";
				}
				else if (part1.length() < 16) {
					part2mod = "\t\t";
				}
				else {
					part2mod = "\t";
				}
				
				if (part2.length() < 8) {
					part3mod = "\t\t\t";
				}
				else if (part2.length() < 16) {
					part3mod = "\t\t";
				}
				else {
					part3mod = "\t";
				}
				
				if (part3.length() < 8) {
					part4mod = "\t\t\t";
				}
				else if (part3.length() < 16) {
					part4mod = "\t\t";
				}
				else {
					part4mod = "\t";
				}
				
				if (part4.length() < 16) {
					part5mod = "\t\t\t";
				}
				else if (part4.length() < 24) {
					part5mod = "\t\t";
				}
				else {
					part5mod = "\t";
				}
				
				temp = part1 + part2mod + part2 + part3mod + part3 + part4mod + part4 + part5mod + part5;
				results.add(temp);
			}
			
			/*
			 * Give the Summary
			 * Total Transactions: tTot Total Undos: uTot NetMoneyGained: netGain
			 */
			results.add(0, ""); // Empty line after summary
			temp = "#Total Money Gained: " + totGained;
			results.add(0, temp);
			temp = "#Total Money Spent: " + totSpent;
			results.add(0, temp);
			temp = "#Total Transactions (Does not include undone or /pay): " + numTrans + "\t\tNet Money Gained: " + (totGained - totSpent);
			results.add(0, temp);
			
			ArrayList<Transaction> skipT = new ArrayList<Transaction>();
			results.add("");
			results.add("#Player to Player payments:");
			
			/*
			 * Player to player payment, lists only the people that paid somebody
			 */
			for (Transaction iter : playersPaid) {
				String payer = iter.getBuyer().getName();
				boolean skip = false;
				
				for (Transaction skipIter : skipT) {
					if (payer.equalsIgnoreCase(skipIter.getBuyer().getName())) {
						skip = true;
					}
				}
				
				if (!skip) {
					results.add("#\t" + payer + " has paid:");
					for (Transaction iter2 : playersPaid) {
						if (iter2.getBuyer().getName().equalsIgnoreCase(payer)) {
							results.add("#\t\t" + iter2.getSeller().getName() + regex2 + iter2.getAmount());
							
							skipT.add(iter2);
						}
					}
				}
			}
			
			/*Empty lines*/
			results.add("");
			results.add("");
			results.add("#Used for loading the stats again. Delete and then reload plugin if you want to start from scratch.");
			
			/*
			 * Add the parsable stuff
			 * blockID : amount bought : amount sold
			 * 1:3:4 <-- blockID = 1, amountBought = 3, amountSold = 4
			*/
			results.add("%" + numTrans);
			
			for (ShopItem iter : DataManager.getItemList()) {
				int itemID = iter.getItemId();
				int amountSold = 0;
				int amountBought = 0;
				
				for (ShopItemStack iter2 : itemsBought) {
					if (iter2.getItemId() == iter.getItemId()) {
						amountBought = iter2.getAmountAvail();
					}
				}
				
				for (ShopItemStack iter3 : itemsSold) {
					if (iter3.getItemId() == iter.getItemId()) {
						amountSold = iter3.getAmountAvail();
					}
				}
				
				temp = itemID + regex + amountBought + regex + amountSold;
				results.add(temp);
			}
			
			/*
			 * player stuff
			 * &buyer:seller1 200:seller2 300:seller3 2
			 * &buyer2:seller4 100
			 */
			skipT = new ArrayList<Transaction>();
			
			for (Transaction iter : playersPaid) {
				String payer = iter.getBuyer().getName();
				boolean skip = false;
				
				for (Transaction skipIter : skipT) {
					if (iter.getBuyer().getName().equalsIgnoreCase(skipIter.getBuyer().getName())) {
						skip = true;
					}
				}
				
				if (!skip) {
					temp = "";
					temp += "&" + iter.getBuyer().getName();
					
					for (Transaction iter2 : playersPaid) {
						if (iter2.getBuyer().getName().equalsIgnoreCase(payer)) {
							temp += regex + iter2.getSeller().getName() + regex2 + iter2.getAmount().getAmount();
							skipT.add(iter2);
						}
					}
					
					results.add(temp);
				}
			}
			
			return results;
		}
		else {
			ArrayList<String> results = new ArrayList<String>();
			
			/*
			 * Overview
			 */

			/*
			 * Individual Shops
			 */
			for (EconEntity econIter : shopStats.keySet()) {
				String shopName = econIter.getName();
				String temp = "";
				int totGained = 0;
				int totSpent = 0;
				
				/*
				 * Count totals for %
				 */
				for (ShopItem iter : DataManager.getItemList()) {
					for (Transaction transIter : shopStats.get(econIter)) {
						if (iter.getName().equalsIgnoreCase(transIter.getSeller().getName())) {
							/*
							 * The shop is the seller
							 */

						}
						else {
							/*
							 * The shop is the buyer
							 */

						}
					}
					
					for (ShopItemStack iter2 : itemsBought) {
						if (iter2.getItemId() == iter.getItemId()) {
							totSpent += iter2.getTotalBuyPrice().getAmount();
						}
					}
					
					for (ShopItemStack iter3 : itemsSold) {
						if (iter3.getItemId() == iter.getItemId()) {
							totGained += iter3.getTotalSellPrice().getAmount();
						}
					}
				}
				
				/*
				 * Count the amount of each item
				 */
				for (ShopItem iter : DataManager.getItemList()) {
					String itemName = iter.getName();
					int amountBought = 0;
					int amountSold = 0;
					double sellPrice = 0;
					double buyPrice = 0;
					
					for (ShopItemStack iter2 : itemsBought) {
						if (iter2.getItemId() == iter.getItemId()) {
							amountBought = iter2.getAmountAvail();
							buyPrice = iter2.getTotalBuyPrice().getAmount();
						}
					}
					
					for (ShopItemStack iter3 : itemsSold) {
						if (iter3.getItemId() == iter.getItemId()) {
							amountSold = iter3.getAmountAvail();
							sellPrice = iter3.getTotalSellPrice().getAmount();
						}
					}
					
					/*
					 * Makes the % of total buy and sell
					 */
					DecimalFormat two = new DecimalFormat("#.##");
					double spentP = (buyPrice / totSpent) * 100;
					double gainedP = (sellPrice / totGained) * 100;
					
					/*
					 * Sets up the string parts for formating
					 */
					String part1 = "#" + itemName;
					String part2mod = "";
					String part2 = "Purchased: " + amountBought;
					String part3mod = "";
					String part3 = "Sold: " + amountSold;
					String part4mod = "";
					String part4 = "Spent: " + (int) buyPrice + " (" + two.format(spentP) + "%)";
					String part5mod = "";
					String part5 = "Gained: " + (int) sellPrice + " (" + two.format(gainedP) + "%)";
					
					/*
					 * Adjusts the string parts to keep indenting correct
					 */
					if (part1.length() < 8) {
						part2mod = "\t\t\t";
					}
					else if (part1.length() < 16) {
						part2mod = "\t\t";
					}
					else {
						part2mod = "\t";
					}
					
					if (part2.length() < 8) {
						part3mod = "\t\t\t";
					}
					else if (part2.length() < 16) {
						part3mod = "\t\t";
					}
					else {
						part3mod = "\t";
					}
					
					if (part3.length() < 8) {
						part4mod = "\t\t\t";
					}
					else if (part3.length() < 16) {
						part4mod = "\t\t";
					}
					else {
						part4mod = "\t";
					}
					
					if (part4.length() < 16) {
						part5mod = "\t\t\t";
					}
					else if (part4.length() < 24) {
						part5mod = "\t\t";
					}
					else {
						part5mod = "\t";
					}
					
					temp = part1 + part2mod + part2 + part3mod + part3 + part4mod + part4 + part5mod + part5;
					results.add(temp);
				}
			}
			
			/*
			 * Player to player
			 */

			return results;
		}
	}
	
	/**
	 * Removes the stats for the given {@link ShopItemStack}. Called when a player undoes a {@link Transaction}
	 * 
	 * @param stack
	 */
	public static void undoBuy(ShopItemStack stack) {
		numTrans--;
		
		for (ShopItemStack iter : itemsBought) {
			if (iter.getItemId() == stack.getItemId()) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	/**
	 * Removes the stats for the given {@link ShopItemStack}. Called when a player undoes a {@link Transaction}
	 * 
	 * @param stack
	 */
	public static void undoSell(ShopItemStack stack) {
		numTrans--;
		
		for (ShopItemStack iter : itemsSold) {
			if (iter.getItemId() == stack.getItemId()) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	/**
	 * Resets all of the stat data
	 */
	public static void reset() {
		itemsBought = new ArrayList<ShopItemStack>();
		itemsSold = new ArrayList<ShopItemStack>();
		numTrans = 0;
		playersPaid = new ArrayList<Transaction>();
		shopStats = new HashMap<EconEntity, ArrayList<Transaction>>();
	}
}
