public abstract class EconEntity {
	protected Money	money	= new Money();
	
	public EconEntity(Money money) {
		this.money = money;
	}
	
	public EconEntity() {
	}
	
	public Money getMoney() {
		return money;
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
