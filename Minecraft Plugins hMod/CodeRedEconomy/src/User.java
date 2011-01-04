public class User extends EconEntity {
	private Player	player	= null;
	
	/**
	 * New player
	 * 
	 * @param player
	 */
	public User(Player player) {
		super();
		this.player = player;
		DataManager.addUser(this);
	}
	
	/**
	 * Used for loading from the file
	 * 
	 * @param user
	 */
	public User(User user) {
		super(user.getMoney(), user.getPrivLevel());
		this.player = user.getPlayer();
	}
	
	public User() {
		super();
	}
	
	public boolean canBuy(ShopItem item) {
		if (item.getPrice() > money.getAmount()) {
			return false;
		}
		else if (item.getPrivLevel() > privLevel) {
			return false;
		}
		return true;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Money getMoney() {
		return money;
	}
	
	public boolean pay(EconEntity econ, ShopItemStack inCart) {
		money.removeAmount(inCart.getTotalPrice());
		if (money.isValid()) {
			econ.recieveMoney(new Money(inCart.getTotalPrice()));
			player.giveItem(inCart.getShopItem().getItemID(), inCart.getAmountAvail());
			return true;
		}
		else {
			return false;
		}
	}
	
	public boolean canBuy(ShopItemStack shopItemStack) {
		if (shopItemStack.getTotalPrice() > money.getAmount()) {
			return false;
		}
		else if (shopItemStack.getShopItem().getPrivLevel() > privLevel) {
			return false;
		}
		return true;
	}
	
	public void showBalance() {
		player.sendMessage(DataManager.getPluginMessage() + "Your balance is: " + money.getAmount() + " " + money.getMoneyName());
	}
}
