import java.util.ArrayList;

public class EconStats {
	private static int						numTrans	= 0;
	private static ArrayList<ShopItemStack>	itemsBought	= new ArrayList<ShopItemStack>();
	private static ArrayList<ShopItemStack>	itemsSold	= new ArrayList<ShopItemStack>();
	
	public static void bought(ShopItemStack stack) {
		numTrans++;
		// Find if it is in the bought array
		boolean found = false;
		for (ShopItemStack iter : itemsBought) {
			if (iter.getItemID() == stack.getItemID()) {
				found = true;
				iter.addAmountAvail(stack.getAmountAvail());
				break;
			}
		}
		if (!found) {
			itemsBought.add(stack);
		}
	}
	
	public static void sold(ShopItemStack stack) {
		numTrans++;
		boolean found = false;
		for (ShopItemStack iter : itemsSold) {
			if (iter.getItemID() == stack.getItemID()) {
				found = true;
				iter.addAmountAvail(stack.getAmountAvail());
				break;
			}
		}
		if (!found) {
			itemsSold.add(stack);
		}
	}
	
	public static void undoSell(ShopItemStack stack) {
		numTrans--;
		for (ShopItemStack iter : itemsSold) {
			if (iter.getItemID() == stack.getItemID()) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	public static void undoBuy(ShopItemStack stack) {
		numTrans--;
		for (ShopItemStack iter : itemsBought) {
			if (iter.getItemID() == stack.getItemID()) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	public static void loadStats(ArrayList<String> data) {
		for (String si : data) {
			// Safety check
			if (si.length() >= 1) {
				if (si.charAt(0) == '%') {
					si = si.replace("%", "");
					numTrans = Integer.valueOf(si);
				}
				else if (si.charAt(0) != '#') {
					// Data line, read and add it
					String ssplit[] = si.split(":");
					
					// Safety check
					if (ssplit.length == 3) {
						int ID = Integer.valueOf(ssplit[0]);
						int buyAmount = Integer.valueOf(ssplit[1]);
						int sellAmount = Integer.valueOf(ssplit[2]);
						if (buyAmount != 0) {
							// Add it to the bought
							itemsBought.add(new ShopItemStack(new ShopItem(ID), buyAmount));
						}
						if (sellAmount != 0) {
							// Add it to the bought
							itemsSold.add(new ShopItemStack(new ShopItem(ID), sellAmount));
						}
					}
				}
			}
		}
	}
	
	public static ArrayList<String> statString() {
		// # <-- Used as a comment, easy to parse version will be at the bottom without
		
		// ItemName Bought: Amount bought Sold: Amount Sold TotalSellPrice: totalSell TotalBuyPrice: totalBuy
		ArrayList<String> results = new ArrayList<String>();
		String temp = "";
		int netGained = 0;
		
		for (ShopItem iter : DataManager.getItemList()) {
			String itemName = iter.getName();
			int amountBought = 0;
			int amountSold = 0;
			int sellPrice = 0;
			int buyPrice = 0;
			for (ShopItemStack iter2 : itemsBought) {
				if (iter2.getItemID() == iter.getItemID()) {
					amountBought = iter2.getAmountAvail();
					buyPrice = iter2.getTotalBuyPrice().getAmount();
					netGained -= buyPrice;
				}
			}
			for (ShopItemStack iter3 : itemsSold) {
				if (iter3.getItemID() == iter.getItemID()) {
					amountSold = iter3.getAmountAvail();
					sellPrice = iter3.getTotalSellPrice().getAmount();
					netGained += sellPrice;
				}
			}
			String part1 = "#" + itemName;
			String part2mod = "";
			String part2 = "Purchased: " + amountBought;
			String part3mod = "";
			String part3 = "Sold: " + amountSold;
			String part4mod = "";
			String part4 = "Total Money Spent: " + buyPrice;
			String part5mod = "";
			String part5 = "Total Money Gained: " + sellPrice;
			
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
			
			if (part4.length() < 24) {
				part5mod = "\t\t\t";
			}
			else if (part4.length() < 32) {
				part5mod = "\t\t";
			}
			else {
				part5mod = "\t";
			}
			
			temp = part1 + part2mod + part2 + part3mod + part3 + part4mod + part4 + part5mod + part5;
			results.add(temp);
		}
		
		// Give the Summary
		// Total Transactions: tTot Total Undos: uTot NetMoneyGained: netGain
		temp = "#Total Transactions (Does not include undone): " + numTrans + "\t\tNet Money Gained: " + netGained;
		results.add(0, temp);
		results.add(1, ""); // Empty line after summary
		
		// Add the parsable stuff
		// blockID : amount bought : amount sold
		// 1:3:4 <-- blockID = 1, amountBought = 3, amountSold = 4
		
		// Empty lines
		results.add("");
		results.add("");
		results.add("Used for loading the stats again. Delete and then reload plugin if you want to start from scratch.");
		
		results.add("%" + numTrans);
		for (ShopItem iter : DataManager.getItemList()) {
			int itemID = iter.getItemID();
			int amountSold = 0;
			int amountBought = 0;
			for (ShopItemStack iter2 : itemsBought) {
				if (iter2.getItemID() == iter.getItemID()) {
					amountBought = iter2.getAmountAvail();
				}
			}
			for (ShopItemStack iter3 : itemsSold) {
				if (iter3.getItemID() == iter.getItemID()) {
					amountSold = iter3.getAmountAvail();
				}
			}
			temp = itemID + ":" + amountBought + ":" + amountSold;
			results.add(temp);
		}
		
		return results;
	}
}
