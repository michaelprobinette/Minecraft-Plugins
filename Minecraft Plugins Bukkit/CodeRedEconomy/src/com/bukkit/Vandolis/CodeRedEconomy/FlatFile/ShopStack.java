/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy.FlatFile;

import org.bukkit.inventory.ItemStack;

/**
 * @author Mike
 */
public class ShopStack extends ItemStack {
	private Money	buyPrice		= new Money();
	private Money	sellPrice		= new Money();
	private Money	buyPriceStack	= new Money();
	private Money	sellPriceStack	= new Money();
	private int		shopMaxSell		= 0;
	private int		playerMaxBuy	= 0;
	private int		playerMaxSell	= 0;
	private int		breakValue		= 0;
	private String	itemName		= "";
	
	/**
	 * @param type
	 */
	public ShopStack(int type) {
		super(type);
		// TODO Auto-generated constructor stub
	}
	
	/**
	 * @param name
	 * @param id
	 * @param damage
	 * @param buyPrice
	 * @param sellPrice
	 * @param shopMaxSell
	 * @param playerMaxBuy
	 * @param playerMaxSell
	 * @param breakValue
	 */
	public ShopStack(String name, int id, byte damage, long buyPrice, long sellPrice, int shopMaxSell, int playerMaxBuy, int playerMaxSell,
			int breakValue) {
		super(id);
		setDurability(damage);
		this.buyPrice = new Money((int) buyPrice);
		itemName = name;
		
	}
	
	/**
	 * @param type
	 * @param amount
	 */
	public ShopStack(int type, int amount) {
		super(type, amount);
	}
	
	/**
	 * @param type
	 * @param amount
	 * @param buyPrice
	 * @param sellPrice
	 */
	public ShopStack(int type, int amount, long buyPrice, long sellPrice) {
		super(type, amount);
	}
	
	/**
	 * @return the buyPrice
	 */
	protected Money getBuyPrice() {
		return buyPrice;
	}
	
	/**
	 * @param buyPrice
	 *            the buyPrice to set
	 */
	protected void setBuyPrice(Money buyPrice) {
		this.buyPrice = buyPrice;
	}
	
	/**
	 * @return the sellPrice
	 */
	protected Money getSellPrice() {
		return sellPrice;
	}
	
	/**
	 * @param sellPrice
	 *            the sellPrice to set
	 */
	protected void setSellPrice(Money sellPrice) {
		this.sellPrice = sellPrice;
	}
	
	/**
	 * @return the buyPriceStack
	 */
	protected Money getBuyPriceStack() {
		return buyPriceStack;
	}
	
	/**
	 * @param buyPriceStack
	 *            the buyPriceStack to set
	 */
	protected void setBuyPriceStack(Money buyPriceStack) {
		this.buyPriceStack = buyPriceStack;
	}
	
	/**
	 * @return the sellPriceStack
	 */
	protected Money getSellPriceStack() {
		return sellPriceStack;
	}
	
	/**
	 * @param sellPriceStack
	 *            the sellPriceStack to set
	 */
	protected void setSellPriceStack(Money sellPriceStack) {
		this.sellPriceStack = sellPriceStack;
	}
	
	/**
	 * @return the itemName
	 */
	protected String getItemName() {
		return itemName;
	}
	
	/**
	 * @param itemName
	 *            the itemName to set
	 */
	protected void setItemName(String itemName) {
		this.itemName = itemName;
	}
	
}
