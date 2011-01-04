public class ShopItemStack {
	private int			itemID			= 0;
	private int			amountAvail		= 0;
	private ShopItem	shopItem		= null;
	private int			totalBuyPrice	= 0;
	private int			totalSellPrice	= 0;
	
	/**
	 * @deprecated
	 * @param itemID
	 * @param amountAvail
	 */
	public ShopItemStack(int itemID, int amountAvail) {
		this.itemID = itemID;
		this.amountAvail = amountAvail;
	}
	
	/**
	 * @deprecated
	 * @param itemID
	 * @param amountAvail
	 * @param privLevel
	 */
	public ShopItemStack(int itemID, int amountAvail, int privLevel) {
		this.itemID = itemID;
		this.amountAvail = amountAvail;
	}
	
	public ShopItemStack(ShopItem shopItem, int amountAvail) {
		itemID = shopItem.getItemID();
		this.shopItem = shopItem;
		this.amountAvail = amountAvail;
		this.totalBuyPrice = shopItem.getBuyPrice() * amountAvail;
		this.totalSellPrice = shopItem.getSellPrice() * amountAvail;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getAmountAvail() {
		return amountAvail;
	}
	
	public ShopItem getShopItem() {
		return shopItem;
	}
	
	public int getTotalBuyPrice() {
		return totalBuyPrice;
	}
	
	public int getTotalSellPrice() {
		return totalSellPrice;
	}
	
	public void addAmountAvail(int amount) {
		amountAvail += amount;
	}
	
	public void setAmountAvail(int amount) {
		amountAvail = amount;
	}
}
