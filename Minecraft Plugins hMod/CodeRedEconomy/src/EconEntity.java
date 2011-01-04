public abstract class EconEntity {
	protected Money	money		= new Money();
	protected int	privLevel	= 0;
	
	public EconEntity(Money money, int privLevel) {
		this.money = money;
		this.privLevel = privLevel;
	}
	
	public EconEntity() {
	}
	
	public Money getMoney() {
		return money;
	}
	
	public int getPrivLevel() {
		return privLevel;
	}
	
	public Money giveMoney(int amount) {
		money.setAmount(money.getAmount() - amount);
		if (money.getAmount() >= 0) {
			return new Money(amount);
		}
		else {
			// Not enough, send money that is invalid
			return new Money(-1);
		}
	}
	
	public void recieveMoney(Money money) {
		money.addAmount(money.getAmount());
	}
}
