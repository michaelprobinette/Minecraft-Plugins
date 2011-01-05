import java.util.ArrayList;

public class PriceList {
	private static ArrayList<String[]>	pages	= new ArrayList<String[]>();
	
	public PriceList() {
	}
	
	private static void populate(User user, Shop shop) {
		pages = new ArrayList<String[]>();
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		for (ShopGroup giter : DataManager.getGroups()) {
			if (user.getPlayer().isInGroup(giter.getGroupName())) {
				for (int i : giter.getAllowed()) {
					items.add(new ShopItem(i));
				}
			}
		}
		// System.out.println("items is " + items.size());
		// 7 lines to each page
		int count = 0;
		String page[] = new String[7];
		for (ShopItem iter : items) {
			String temp = "";
			int amount = 0;
			for (ShopItemStack iters : shop.getAvailItems()) {
				if (iters.getItemID() == iter.getItemID()) {
					amount = iters.getAmountAvail();
				}
			}
			if (amount != DataManager.getInfValue()) {
				temp += iter.getName() + ": §a" + iter.getBuyPrice() + " §c" + iter.getSellPrice() + " §e" + amount;
			}
			else {
				temp += iter.getName() + ": §a" + iter.getBuyPrice() + " §c" + iter.getSellPrice() + " §eInfinite";
			}
			// temp += iter.getName() + ": " + iter.getBuyPrice() + " " + Money.getMoneyName();
			// temp += iter.getName() + " Buy: " + iter.getBuyPrice() + " Sell: " + iter.getSellPrice() + " " + Money.getMoneyName();
			page[count] = temp;
			count++;
			if (count == 7 || iter.equals(items.get(items.size() - 1))) {
				// System.out.println("Adding a page.");
				
				if (page[0] != null) {
					pages.add(page);
					count = 0;
					page = new String[7];
				}
			}
		}
	}
	
	public static void priceList(User user, int page, Shop shop) {
		populate(user, shop);
		if (pages.size() >= page && page > 0 && pages.size() != 0) {
			user.sendMessage(DataManager.getPluginMessage() + "Price List: (Page " + page + " of " + pages.size() + ")");
			for (String iter : pages.get(page - 1)) {
				if (iter != null) {
					// Send the line
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
	
	public static int getNumPages() {
		return pages.size();
	}
}
