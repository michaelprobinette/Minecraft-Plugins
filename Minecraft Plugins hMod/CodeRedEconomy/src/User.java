public class User {
	private Player	player		= null;
	private Money	money		= null;
	private int		privLevel	= 0;
	
	public User(Player player) {
		this.player = player;
		this.money = new Money();
	}
	
	public int getPrivLevel() {
		return privLevel;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public Money getMoney() {
		return money;
	}
}
