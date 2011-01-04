public class User extends EconEntity {
	private Player	player		= null;
	private String	name		= "";
	private String	groupName	= "";
	
	/**
	 * New player
	 * 
	 * @param player
	 */
	public User(Player player) {
		super();
		this.player = player;
		name = player.getName();
		if (player.getGroups().length >= 1) {
			groupName = player.getGroups()[0];
		}
		else
			groupName = "nogroup";
		DataManager.addUser(this);
	}
	
	/**
	 * Used for loading from the file
	 * 
	 * @param user
	 */
	public User(User user) {
		super(user.getMoney());
		this.player = user.getPlayer();
		name = user.getPlayer().getName();
	}
	
	public User(String saveString) {
		// Split it and grab the data
		String split[] = saveString.split(":");
		if (split.length >= 2) {
			name = split[0];
			int temp = Integer.valueOf(split[1]);
			money.setAmount(temp);
		}
	}
	
	public User() {
		super();
	}
	
	public void setPlayer(Player player) {
		this.player = player;
		if (player.getGroups().length >= 1) {
			groupName = player.getGroups()[0];
		}
		else
			groupName = "nogroup";
	}
	
	public String getName() {
		return name;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public boolean canBuy(ShopItem item) {
		if (item.getPrice() > money.getAmount()) {
			return false;
		}
		
		// else if (item.getPrivLevel() > privLevel) {
		// return false;
		// }
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
		if ((player.isInGroup(DataManager.getReqGroup(shopItemStack.getShopItem().getItemID())))
				&& shopItemStack.getTotalPrice() <= money.getAmount()) {
			return true;
		}
		// else if (shopItemStack.getShopItem().getPrivLevel() < privLevel) {
		// return false;
		// }
		return false;
	}
	
	public void showBalance() {
		player.sendMessage(DataManager.getPluginMessage() + "Your balance is: " + money.getAmount() + " " + Money.getMoneyName());
	}
	
	public void sendMessage(String message) {
		player.sendMessage(message);
	}
	
	@Override
	public String toString() {
		return name + ":" + money.getAmount();
	}
}
