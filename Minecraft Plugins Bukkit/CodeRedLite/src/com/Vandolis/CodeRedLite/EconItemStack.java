/**
 *
 */
package com.Vandolis.CodeRedLite;

import org.bukkit.inventory.ItemStack;

/**
 * @author Vandolis
 */
public class EconItemStack extends ItemStack
{
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
	
	public EconItemStack(EconItemStack item, int amount, CodeRedLite codeRed)
	{
		super(item.getType());
		
		if (item.isSubtyped)
		{
			setDurability(item.getDurability());
		}
		
		name = item.getName();
		compactName = item.getCompactName();
		
		isSubtyped = item.isSubtyped;
		priceBuy = item.getPriceBuy();
		priceSell = item.getPriceSell();
		itemsID = item.getItemsID();
		sqlID = item.getSqlID();
		basePrice = item.getBasePrice();
		slope = item.getSlope();
		
		plugin = codeRed;
		
		changeAmount(amount);
	}
	
	public EconItemStack(int itemsID, String itemName, int itemID, boolean subtyped, short subtype, int buyPrice,
		int sellPrice, int baseValue, float dynamicSlope, CodeRedLite codeRed)
	{
		super(itemID);
		
		isSubtyped = subtyped;
		
		if (subtyped)
		{
			super.setDurability(subtype);
		}
		
		name = itemName;
		compactName = itemName.replaceAll(" ", "");
		priceBuy = buyPrice;
		priceSell = sellPrice;
		this.itemsID = itemsID;
		
		basePrice = baseValue;
		slope = dynamicSlope;
		
		plugin = codeRed;
		
		changeAmount(0);
	}
	
	public EconItemStack(int sqlId, int itemID, boolean subtyped, short subtype, String itemName, int currentStock,
		boolean infinite, int buyPrice, int sellPrice, int maxBuy, int maxSell, int itemsId, int baseValue,
		float dyanmicSlope, CodeRedLite codeRed)
	{
		super(itemID);
		
		isSubtyped = subtyped;
		
		if (subtyped)
		{
			super.setDurability(subtype);
		}
		
		name = itemName;
		compactName = itemName.replaceAll(" ", "");
		
		isInfinite = infinite;
		priceBuy = buyPrice;
		priceSell = sellPrice;
		itemsID = itemsId;
		sqlID = sqlId;
		
		basePrice = baseValue;
		slope = dyanmicSlope;
		
		plugin = codeRed;
		
		changeAmount(currentStock);
	}
	
	/**
	 * @return the basePrice
	 */
	public int getBasePrice()
	{
		return basePrice;
	}
	
	/**
	 * @param newBase
	 *            the basePrice to set
	 */
	public void setBasePrice(int newBase)
	{
		basePrice = newBase;
	}
	
	/**
	 * @return the isSubtyped
	 */
	public boolean isSubtyped()
	{
		return isSubtyped;
	}
	
	/**
	 * @return the compactName
	 */
	public String getCompactName()
	{
		return compactName;
	}
	
	/**
	 * @return the sqlID
	 */
	public int getSqlID()
	{
		return sqlID;
	}
	
	/**
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 * @return the isInfinite
	 */
	public boolean isInfinite()
	{
		return isInfinite;
	}
	
	/**
	 * @return the itemsID
	 */
	public int getItemsID()
	{
		return itemsID;
	}
	
	/**
	 * @return the priceBuy
	 */
	public int getPriceBuy()
	{
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
	public int getPriceSell()
	{
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
	public int getTotalBuy()
	{
		return totalBuy;
	}
	
	/**
	 * @return the totalSell
	 */
	public int getTotalSell()
	{
		return totalSell;
	}
	
	public void changeAmount(int amount)
	{
		setAmount(amount);
		
		updatePrice();
	}
	
	public void updatePrice()
	{
		if (plugin.getProperties().isDynamicPrices())
		{
			priceSell = Math.round(basePrice / ((getAmount() * slope) + 1));
			priceBuy = Math.round(basePrice / (getAmount() * slope));
		}
		else
		{
			totalBuy = getAmount() * priceBuy;
			totalSell = getAmount() * priceSell;
		}
	}
	
	public int quoteBuy(int amount)
	{
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++)
		{
			runningTotal += Math.round((basePrice / ((getAmount() - x) * slope)));
		}
		
		if (plugin.getProperties().isDynamicPrices())
		{
			return runningTotal;
		}
		else
		{
			return priceBuy * getAmount();
		}
	}
	
	public int quoteSell(int amount)
	{
		int runningTotal = 0;
		
		for (int x = 0; x < amount; x++)
		{
			runningTotal += Math.round((basePrice / (((getAmount() + x) * slope) + 1)));
		}
		
		if (plugin.getProperties().isDynamicPrices())
		{
			return runningTotal;
		}
		else
		{
			return priceSell * getAmount();
		}
	}
	
	/**
	 * @param newTotal
	 *            the totalBuy to set
	 */
	public void setTotalBuy(int newTotal)
	{
		totalBuy = newTotal;
	}
	
	/**
	 * @param newTotal
	 *            the totalSell to set
	 */
	public void setTotalSell(int newTotal)
	{
		totalSell = newTotal;
	}
	
	/**
	 * @return the slope
	 */
	public float getSlope()
	{
		return slope;
	}
	
	/**
	 * @param newSlope
	 *            the slope to set
	 */
	public void setSlope(float newSlope)
	{
		slope = newSlope;
	}
	
	/**
	 * @param sqlID
	 *            the sqlID to set
	 */
	public void setSqlID(int sqlID)
	{
		this.sqlID = sqlID;
	}
}
