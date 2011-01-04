import java.util.ArrayList;

public abstract class EconEntity {
	protected Money						money			= new Money();
	protected ArrayList<ShopItemStack>	availableItems	= new ArrayList<ShopItemStack>();
	protected String					name			= "";
	protected boolean					isPlayer		= false;
	protected Transaction				lastTrans		= null;
	
	public EconEntity(Money money) {
		this.money = money;
	}
	
	public EconEntity() {
	}
	
	public Money getMoney() {
		return money;
	}
	
	public void recieveMoney(Money money) {
		money.addAmount(money.getAmount());
	}
	
	public ArrayList<ShopItemStack> getAvailItems() {
		return availableItems;
	}
	
	public boolean isPlayer() {
		return isPlayer;
	}
	
	/**
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	public boolean hasItems(ShopItemStack stack) {
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID() && iter.getAmountAvail() >= stack.getAmountAvail()) {
				return true;
			}
		}
		return false;
	}
	
	public void addShopItems(ShopItemStack stack) {
		boolean found = false;
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID()) {
				iter.addAmountAvail(stack.getAmountAvail());
				found = true;
				break;
			}
		}
		if (!found) {
			availableItems.add(stack);
		}
	}
	
	public void removeShopItems(ShopItemStack stack) {
		for (ShopItemStack iter : availableItems) {
			if (iter.getItemID() == stack.getItemID()) {
				iter.addAmountAvail(-stack.getAmountAvail());
				break;
			}
		}
	}
	
	public void setLastTrans(Transaction trans) {
		lastTrans = trans;
	}
}
