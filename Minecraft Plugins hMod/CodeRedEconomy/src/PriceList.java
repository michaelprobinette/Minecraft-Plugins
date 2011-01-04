import java.util.ArrayList;

public class PriceList {
	private static final ArrayList<String[]>	pages	= new ArrayList<String[]>();
	
	public PriceList() {
	}
	
	public void populate(Player player) {
		ArrayList<ShopItem> items = new ArrayList<ShopItem>();
		for (Group giter : DataManager.getGroups()) {
			if (player.isInGroup(giter.getGroupName())) {
				for (int i : giter.getAllowed()) {
					items.add(new ShopItem(i));
				}
			}
		}
		// 7 lines to each page
		int count = 0;
		String page[] = new String[7];
		for (ShopItem iter : items) {
			String temp = "";
			temp += iter.getName() + ": " + iter.getPrice() + " " + Money.getMoneyName();
			page[count] = temp;
			count++;
			if (count == 7) {
				pages.add(page);
				count = 0;
				page = new String[7];
			}
		}
	}
	
	public static void priceList(Player player, int page) {
		if (pages.size() >= page) {
			player.sendMessage(DataManager.getPluginMessage() + "Price List: (Page " + page + " of " + pages.size() + ")");
			for (String iter : pages.get(page - 1)) {
				if (iter != null) {
					// Send the line
					player.sendMessage(DataManager.getPluginMessage() + "   " + iter);
				}
			}
		}
	}
	
	public static int getNumPages() {
		return pages.size();
	}
}
