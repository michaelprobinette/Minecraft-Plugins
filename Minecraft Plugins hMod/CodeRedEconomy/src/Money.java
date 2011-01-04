public class Money {
	private int				amount	= 0;
	private boolean			valid	= true;
	private static String	name	= "";
	
	public Money() {
		name = DataManager.getMoneyName();
	}
	
	public Money(int amount) {
		name = DataManager.getMoneyName();
		this.amount = amount;
		if (amount < 0) {
			valid = false;
		}
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean setAmount(int amount) {
		this.amount = amount;
		if (amount < 0) {
			valid = false;
		}
		return valid;
	}
	
	public void addAmount(int amount) {
		this.amount += amount;
	}
	
	public void removeAmount(int amount) {
		this.amount -= amount;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public static String getMoneyName() {
		return name;
	}
	
	@Override
	public String toString() {
		return amount + " " + name;
	}
}
