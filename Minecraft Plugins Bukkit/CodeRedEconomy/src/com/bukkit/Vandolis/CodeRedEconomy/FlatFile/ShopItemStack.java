/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import org.bukkit.inventory.ItemStack;

/**
 * Class that extends a {@link ShopItem} and adds a amount as well as total buy/sell prices
 * 
 * @author Vandolis
 */
public class ShopItemStack extends ShopItem {
	private int		amountAvail		= 0;
	private Money	totalBuyPrice	= new Money();
	private Money	totalSellPrice	= new Money();
	
	/**
	 * Creates a ShopItemStack from the given {@link ShopItem} and the amount given.
	 * 
	 * @param shopItem
	 * @param amountAvail
	 */
	public ShopItemStack(int itemId, int amountAvail) {
		super(itemId);
		
		/*
		 * Set the total buy and sell price.
		 */
		this.amountAvail = amountAvail;
		totalBuyPrice.setAmount(getBuyPrice() * amountAvail);
		totalSellPrice.setAmount(getSellPrice() * amountAvail);
		setItem(new ItemStack(itemId, amountAvail));
	}
	
	/**
	 * Used by the undo transaction to make an inverted item.
	 * 
	 * @param itemId
	 * @param buyPrice
	 * @param sellPrice
	 * @param amountAvail2
	 */
	public ShopItemStack(int itemId, int buyPrice, int sellPrice, int amountAvail) {
		super(itemId, buyPrice, sellPrice);
		
		/*
		 * Set the total buy and sell price.
		 */
		this.amountAvail = amountAvail;
		totalBuyPrice.setAmount(getBuyPrice() * amountAvail);
		totalSellPrice.setAmount(getSellPrice() * amountAvail);
		setItem(new ItemStack(getItemId(), amountAvail));
	}
	
	/**
	 * Used to add to the amount available or subtract with a negative. Updates the totalBuy/SellPrice as well.
	 * 
	 * @param amount
	 */
	public void addAmountAvail(int amount) {
		/*
		 * Add or subtract from the current amount and update the buy and sell price with the new amount.
		 */
		amountAvail += amount;
		totalBuyPrice.setAmount(getBuyPrice() * amountAvail);
		totalSellPrice.setAmount(getSellPrice() * amountAvail);
		setItem(new ItemStack(getItemId(), amountAvail));
	}
	
	/**
	 * Returns the amount in the stack.
	 * 
	 * @return
	 */
	public int getAmountAvail() {
		return amountAvail;
	}
	
	/**
	 * Returns the amount of {@link Money} required to buy.
	 * 
	 * @return
	 */
	public Money getTotalBuyPrice() {
		return totalBuyPrice;
	}
	
	/**
	 * Returns the amount of {@link Money} required to sell.
	 * 
	 * @return
	 */
	public Money getTotalSellPrice() {
		return totalSellPrice;
	}
	
	/**
	 * Used to set the amount of the stack to the given amount. Updates the totalBuy/SellPrice as well.
	 * 
	 * @param amount
	 */
	public void setAmountAvail(int amount) {
		/*
		 * Change the amount, and update the buy and sell price with the new amount.
		 */
		amountAvail = amount;
		totalBuyPrice.setAmount(getBuyPrice() * amountAvail);
		totalSellPrice.setAmount(getSellPrice() * amountAvail);
		setItem(new ItemStack(getItemId(), amountAvail));
	}
	
	/* (non-Javadoc)
	 * @see bukkit.Vandolis.ShopItem#toString()
	 */
	@Override
	public String toString() {
		return amountAvail + " " + getName();
	}
	
	/**
	 * @param money
	 */
	public void setTotalBuyPrice(Money money) {
		totalBuyPrice = money;
	}
	
	/**
	 * @param money
	 */
	public void setTotalSellPrice(Money money) {
		totalSellPrice = money;
	}
}
