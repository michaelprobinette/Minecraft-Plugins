public class ShopItemStack {
	private int	itemID		= 0;
	private int	amountAvail	= 0;
	private int	privLevel	= 0;
	
	public ShopItemStack(int itemID, int amountAvail) {
		this.itemID = itemID;
		this.amountAvail = amountAvail;
	}
	
	public ShopItemStack(int itemID, int amountAvail, int privLevel) {
		this.itemID = itemID;
		this.amountAvail = amountAvail;
		this.privLevel = privLevel;
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getAmountAvail() {
		return amountAvail;
	}
	
	public int getPrivLevel() {
		return privLevel;
	}
}
