import java.util.ArrayList;

public class PriceList {
	private static ArrayList<String[]>	pages	= new ArrayList<String[]>();
	
	public PriceList() {
	}
	
	private static void populate(Player player) {
		pages = new ArrayList<String[]>();
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		for (ShopGroup giter : DataManager.getGroups()) {
			if (player.isInGroup(giter.getGroupName())) {
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
			temp += iter.getName() + ": " + iter.getBuyPrice() + " " + Money.getMoneyName();
			// temp += iter.getName() + " Buy: " + iter.getBuyPrice() + " Sell: " + iter.getSellPrice() + " " + Money.getMoneyName();
			page[count] = temp;
			count++;
			if (count == 7 || iter.equals(items.get(items.size() - 1))) {
				// System.out.println("Adding a page.");
				pages.add(page);
				count = 0;
				page = new String[7];
			}
		}
	}
	
	public static void priceList(Player player, int page) {
		populate(player);
		if (pages.size() >= page && page > 0) {
			player.sendMessage(DataManager.getPluginMessage() + "Price List: (Page " + page + " of " + pages.size() + ")");
			for (String iter : pages.get(page - 1)) {
				if (iter != null) {
					// Send the line
					player.sendMessage(DataManager.getPluginMessage() + "   " + iter);
				}
			}
		}
		else {
			player.sendMessage(DataManager.getPluginMessage() + "Page numbers are 1 - " + pages.size());
		}
	}
	
	public static int getNumPages() {
		return pages.size();
	}
}
