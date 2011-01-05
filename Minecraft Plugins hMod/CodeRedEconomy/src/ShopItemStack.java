public class ShopItemStack {
	private int			itemID			= 0;
	private int			amountAvail		= 0;
	private ShopItem	shopItem		= null;
	private Money		totalBuyPrice	= new Money();
	private Money		totalSellPrice	= new Money();
	
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
		if (shopItem == null) {
			itemID = 0;
			this.shopItem = new ShopItem();
			this.amountAvail = 0;
			this.totalBuyPrice.setAmount(0);
			this.totalSellPrice.setAmount(0);
		}
		else {
			itemID = shopItem.getItemID();
			this.shopItem = shopItem;
			this.amountAvail = amountAvail;
			this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
			this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
		}
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
	
	public Money getTotalBuyPrice() {
		return totalBuyPrice;
	}
	
	public Money getTotalSellPrice() {
		return totalSellPrice;
	}
	
	public void addAmountAvail(int amount) {
		amountAvail += amount;
		this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
		this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
	}
	
	public void setAmountAvail(int amount) {
		amountAvail = amount;
		this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
		this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
	}
}
