/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

/**
 * Main class for anything money related. Includes name and amount
 * 
 * @author Vandolis
 */
public class Money {
	private int				amount	= 0;
	private boolean			valid	= true;
	private static String	name	= "";
	private int				spent	= 0;
	private int				gained	= 0;
	
	/**
	 * Makes a new {@link Money} with all 0.
	 */
	public Money() {
		name = DataManager.getMoneyName();
	}
	
	/**
	 * Makes a new {@link Money} with the given amount.
	 * 
	 * @param amount
	 */
	public Money(int amount) {
		name = DataManager.getMoneyName();
		this.amount = amount;
		if (amount < 0) {
			valid = false;
		}
	}
	
	/**
	 * @param amount
	 *            to add
	 */
	public void addAmount(int amount) {
		/*
		 * Check to make sure they don't accidently go into inf range
		 */
		if ((this.amount + amount == DataManager.getInfValue()) && (this.amount != DataManager.getInfValue())) {
			this.amount += (amount + 1);
		}
		else if (this.amount != DataManager.getInfValue()) {
			this.amount += amount;
		}
		
		if (amount > 0) {
			gained += amount;
		}
		else {
			spent += amount;
		}
	}
	
	/**
	 * @return Name of the {@link Money}
	 */
	public static String getMoneyName() {
		return name;
	}
	
	/**
	 * @return Amount of {@link Money}
	 */
	public int getAmount() {
		return amount;
	}
	
	/**
	 * @return Valid money, false if negative
	 */
	public boolean isValid() {
		return valid;
	}
	
	/**
	 * @param amount
	 *            to remove
	 */
	public void removeAmount(int amount) {
		/*
		 * Check to make sure they don't accidently go into inf range
		 */
		if ((this.amount - amount == DataManager.getInfValue()) && (this.amount != DataManager.getInfValue())) {
			this.amount -= (amount + 1);
		}
		else if (this.amount != DataManager.getInfValue()) {
			this.amount -= amount;
		}
		
		if (amount > 0) {
			gained += amount;
		}
		else {
			spent += amount;
		}
	}
	
	/**
	 * Sets the amount of money to the given amount
	 * 
	 * @param amount
	 * @return
	 */
	public boolean setAmount(int amount) {
		this.amount = amount;
		if ((amount < 0) && (amount != DataManager.getInfValue())) {
			valid = false;
		}
		return valid;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return amount + " " + name;
	}
	
	/**
	 * @return the amount of {@link Money} that has been spent
	 */
	public int getSpent() {
		return spent;
	}
	
	/**
	 * @return the amount of {@link Money} that has been gained
	 */
	public int getGained() {
		return gained;
	}
}
