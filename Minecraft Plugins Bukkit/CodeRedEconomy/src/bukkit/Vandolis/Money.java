/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package bukkit.Vandolis;

public class Money {
	private int				amount	= 0;
	private boolean			valid	= true;
	private static String	name	= "";
	private int				spent	= 0;
	private int				gained	= 0;
	
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
	
	public void addAmount(int amount) {
		// Check to make sure they don't accidently go into inf range
		if (this.amount + amount == DataManager.getInfValue() && this.amount != DataManager.getInfValue()) {
			this.amount += (amount + 1);
		}
		else if (this.amount != DataManager.getInfValue()) {
			this.amount += amount;
		}
	}
	
	public static String getMoneyName() {
		return name;
	}
	
	public int getAmount() {
		return amount;
	}
	
	public boolean isValid() {
		return valid;
	}
	
	public void removeAmount(int amount) {
		// Check to make sure they don't accidently go into inf range
		if (this.amount - amount == DataManager.getInfValue() && this.amount != DataManager.getInfValue()) {
			this.amount -= (amount + 1);
		}
		else if (this.amount != DataManager.getInfValue()) {
			this.amount -= amount;
		}
	}
	
	public boolean setAmount(int amount) {
		this.amount = amount;
		if (amount < 0) {
			valid = false;
		}
		return valid;
	}
	
	@Override
	public String toString() {
		return amount + " " + name;
	}
	
	/**
	 * @return
	 */
	public int getSpent() {
		return spent;
	}
	
	/**
	 * @return
	 */
	public int getGained() {
		return gained;
	}
}
