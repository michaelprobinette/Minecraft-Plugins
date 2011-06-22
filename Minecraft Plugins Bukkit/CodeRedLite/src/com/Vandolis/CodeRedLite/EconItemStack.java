/**
 * 
 */
package com.Vandolis.CodeRedLite;

import org.bukkit.inventory.ItemStack;

/**
 * @author Vandolis
 */
public class EconItemStack extends ItemStack {
	private int			priceBuy	= 0;
	private int			priceSell	= 0;
	private int			totalBuy	= 0;
	private int			totalSell	= 0;
	private String		name		= "";
	private String		compactName	= "";
	private boolean		isInfinite	= false;
	private boolean		isSubtyped	= false;
	private int			itemsID		= 0;
	private int			sqlID		= 0;
	private float		slope		= 1.0f;
	private int			basePrice	= 250;
	private CodeRedLite	plugin		= null;
	
	public EconItemStack(EconItemStack item, int amount, CodeRedLite codeRed) {
		super(item.getType());
		
		if (item.isSubtyped) {
			setDurability(item.getDurability());
		}
		
		name = item.getName();
		compactName = item.getCompactName();
		
		isSubtyped = item.isSubtyped;
		priceBuy = item.getPriceBuy();
		priceSell = item.getPriceSell();
		itemsID = item.getItemsID();
		sqlID = item.getSqlID();
		
		plugin = codeRed;
		
		changeAmount(amount);
	}
	
	public EconItemStack(int itemsID, String name, int itemID, boolean isSubtyped, short subtype, int buyPrice, int sellPrice,
			int baseValue, float slope, CodeRedLite codeRed) {
		super(itemID);
		
		this.isSubtyped = isSubtyped;
		
		if (isSubtyped) {
			super.setDurability(subtype);
		}
		
		this.name = name;
		compactName = name.replaceAll(" ", "");
		priceBuy = buyPrice;
		priceSell = sellPrice;
		this.itemsID = itemsID;
		
		basePrice = baseValue;
		this.slope = slope;
		
		plugin = codeRed;
		
		changeAmount(0);
	}
	
	public EconItemStack(int sqlID, int itemID, boolean isSubtyped, short subtype, String name, int currentStock, boolean isInfinite,
			int buyPrice, int sellPrice, int maxBuy, int maxSell, int itemsID, int baseValue, float slope, CodeRedLite codeRed) {
		super(itemID);
		
		this.isSubtyped = isSubtyped;
		
		if (isSubtyped) {
			super.setDurability(subtype);
		}
		
		this.name = name;
		compactName = name.replaceAll(" ", "");
		
		this.isInfinite = isInfinite;
		priceBuy = buyPrice;
		priceSell = sellPrice;
		this.itemsID = itemsID;
		this.sqlID = sqlID;
		
		basePrice = baseValue;
		this.slope = slope;
		
		plugin = codeRed;
		
		changeAmount(currentStock);
	}
	
	/**
	 * @return the basePrice
	 */
	public int getBasePrice() {
		return basePrice;
	}
	
	/**
	 * @param basePrice
	 *            the basePrice to set
	 */
	public void setBasePrice(int basePrice) {
		this.basePrice = basePrice;
	}
	
	/**
	 * @return the isSubtyped
	 */
	public boolean isSubtyped() {
		return isSubtyped;
	}
	
	/**
	 * @return the compactName
	 */
	public String getCompactName() {
		return compactName;
	}
	
	/**
	 * @return the sqlID
	 */
	public int getSqlID() {
		return sqlID;
	}
	
	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return the isInfinite
	 */
	public boolean isInfinite() {
		return isInfinite;
	}
	
	/**
	 * @return the itemsID
	 */
	public int getItemsID() {
		return itemsID;
	}
	
	/**
	 * @return the priceBuy
	 */
	public int getPriceBuy() {
		return priceBuy;
	}
	
	/**
	 * @param priceBuy
	 *            the priceBuy to set
	 */
	//	public void setPriceBuy(int priceBuy) {
	//		this.priceBuy = priceBuy;
	//		
	//		//totalBuy = getAmount() * priceBuy;
	//	}
	
	/**
	 * @return the priceSell
	 */
	public int getPriceSell() {
		return priceSell;
	}
	
	/**
	 * @param priceSell
	 *            the priceSell to set
	 */
	//	public void setPriceSell(int priceSell) {
	//		this.priceSell = priceSell;
	//		
	//		//totalSell = getAmount() * priceSell;
	//	}
	
	/**
	 * @return the totalBuy
	 */
	public int getTotalBuy() {
		return totalBuy;
	}
	
	/**
	 * @return the totalSell
	 */
	public int getTotalSell() {
		return totalSell;
	}
	
	public void changeAmount(int amount) {
		setAmount(amount);
		
		if (plugin.getProperties().isDynamicPrices()) {
			priceSell = Math.round(basePrice / ((amount * slope) + 1));
			priceBuy = Math.round(basePrice / (amount * slope));
		}
		else {
			totalBuy = getAmount() * priceBuy;
			totalSell = getAmount() * priceSell;
		}
	}
	
	public int quoteBuy(int amount) {
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++) {
			runningTotal += Math.round((basePrice / ((getAmount() - x) * slope)));
		}
		
		if (plugin.getProperties().isDynamicPrices()) {
			return runningTotal;
		}
		else {
			return priceBuy * getAmount();
		}
	}
	
	public int quoteSell(int amount) {
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++) {
			runningTotal += Math.round((basePrice / (((getAmount() + x) * slope) + 1)));
		}
		
		if (plugin.getProperties().isDynamicPrices()) {
			return runningTotal;
		}
		else {
			return priceSell * getAmount();
		}
	}
	
	/**
	 * @param totalBuy
	 *            the totalBuy to set
	 */
	public void setTotalBuy(int totalBuy) {
		this.totalBuy = totalBuy;
	}
	
	/**
	 * @param totalSell
	 *            the totalSell to set
	 */
	public void setTotalSell(int totalSell) {
		this.totalSell = totalSell;
	}
	
	/**
	 * @return the slope
	 */
	public float getSlope() {
		return slope;
	}
	
	/**
	 * @param slope
	 *            the slope to set
	 */
	public void setSlope(float slope) {
		this.slope = slope;
	}
}
