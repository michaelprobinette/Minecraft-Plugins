/*
 * Copyright (C) 2011 Vandolis <http://vandolis.com>
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.Vandolis.CodeRedLite;

import org.bukkit.inventory.ItemStack;

/**
 * @author Vandolis
 */
public class EconItemStack extends ItemStack
{
  private int         priceBuy    = 0;
  private int         priceSell   = 0;
  private int         totalBuy    = 0;
  private int         totalSell   = 0;
  private String      name        = "";
  private String      compactName = "";
  private boolean     isInfinite  = false;
  private boolean     isSubtyped  = false;
  private int         itemsID     = 0;
  private int         sqlID       = 0;
  private float       slope       = 1.0f;
  private int         basePrice   = 250;
  private CodeRedLite plugin      = null;
  
  public EconItemStack(EconItemStack item, int amount, CodeRedLite codeRed)
  {
    super(item.getType());
    
    if (item.isSubtyped)
    {
      setDurability(item.getDurability());
    }
    
    name = item.getName();
    compactName = item.getCompactName();
    compactName += ":" + getDurability();
    
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
    compactName += ":" + subtype;
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
    compactName += ":" + subtype;
    
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
   *          the basePrice to set
   */
  public void setBasePrice(int newBase)
  {
    basePrice = newBase;
    
    updatePrice();
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
   *          the priceBuy to set
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
   *          the priceSell to set
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
    if ((getAmount() == -1) || isInfinite)
    {
      return;
    }
    
    setAmount(amount);
    
    updatePrice();
  }
  
  public void updatePrice()
  {
    if (plugin.getProperties().isDynamicPrices())
    {
      priceSell = calculateSell(getAmount());
      priceBuy = calculateBuy(getAmount());
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
      runningTotal += calculateBuy(getAmount() - x);
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
      runningTotal += calculateSell(getAmount() + x);
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
   *          the totalBuy to set
   */
  public void setTotalBuy(int newTotal)
  {
    totalBuy = newTotal;
  }
  
  /**
   * @param newTotal
   *          the totalSell to set
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
   *          the slope to set
   */
  public void setSlope(float newSlope)
  {
    slope = newSlope;
  }
  
  /**
   * @param sqlID
   *          the sqlID to set
   */
  public void setSqlID(int sqlID)
  {
    this.sqlID = sqlID;
  }
  
  private int calculateBuy(int amount)
  {
    return Math.round(basePrice / (amount * slope));
  }
  
  private int calculateSell(int amount)
  {
    return Math.round(basePrice / ((amount * slope) + 1));
  }
  
  public boolean equals(Object o)
  {
    if (!(o instanceof EconItemStack))
    {
      return false;
    }
    
    EconItemStack tmp = (EconItemStack) o;
    
    return ((tmp.itemsID == itemsID) && tmp.name.equals(name) && (tmp.getDurability() == getDurability()));
  }
}
