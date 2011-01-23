/**
 * 
 */
package com.Vandolis.TheWall;

import com.bukkit.Vandolis.CodeRedEconomy.Money;
import com.bukkit.Vandolis.CodeRedEconomy.ShopItem;
import com.bukkit.Vandolis.CodeRedEconomy.ShopItemStack;
import com.bukkit.Vandolis.CodeRedEconomy.User;

/**
 * @author Vandolis
 */
public class SignPost {
	private String			owner	= "";
	private ShopItemStack	stack	= null;
	
	public SignPost(String owner, ShopItemStack stack) {
		this.owner = owner;
		this.stack = stack;
	}
	
	/**
	 * @param user
	 * @param trim
	 */
	public SignPost(User user, String trim) {
		owner = user.getName();
		
		int amount = 0;
		String itemName = "";
		int price = 0;
		
		for (String iter : trim.split(" ")) {
			try {
				if (amount == 0) {
					amount = Integer.valueOf(iter.trim());
				}
				else {
					price = Integer.valueOf(iter.trim());
				}
			}
			catch (Exception e) {
				itemName += iter + " ";
			}
		}
		itemName = itemName.trim();
		
		stack = new ShopItemStack(ShopItem.getId(itemName), price, price, amount);
		stack.setTotalBuyPrice(new Money(price));
		stack.setTotalSellPrice(new Money(price));
	}
	
	/**
	 * @return the owner
	 */
	protected String getOwner() {
		return owner;
	}
	
	/**
	 * @param owner
	 *            the owner to set
	 */
	protected void setOwner(String owner) {
		this.owner = owner;
	}
	
	/**
	 * @return the stack
	 */
	protected ShopItemStack getStack() {
		return stack;
	}
	
	/**
	 * @param stack
	 *            the stack to set
	 */
	protected void setStack(ShopItemStack stack) {
		this.stack = stack;
	}
}
