/**
 *
 */
package com.Vandolis.CodeRedLite;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Vandolis
 */
public class EconShop
{
	private ArrayList<EconItemStack>	inventory			= new ArrayList<EconItemStack>();
	private int							balance				= 0;
	private int							sqlID				= 0;
	private String						name				= "";
	private boolean						allItemsInfinite	= true;
	private boolean						canRestock			= true;
	
	private boolean						allowBuying			= true;
	private boolean						allowSelling		= true;
	private boolean						isUseMoney			= false;
	private CodeRedLite					plugin				= null;
	
	public EconShop(String name, CodeRedLite codeRed)
	{
		this.name = name;
		plugin = codeRed;
	}
	
	public EconItemStack getItem(String compactName)
	{
		for (EconItemStack iter : inventory)
		{
			if (iter.getCompactName().equalsIgnoreCase(compactName))
			{
				return iter;
			}
		}
		return null;
	}
	
	/**
	 * @return the inventory
	 */
	public ArrayList<EconItemStack> getInventory()
	{
		return inventory;
	}
	
	/**
	 * @return the balance
	 */
	public int getBalance()
	{
		return balance;
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
	 * @return the allItemsInfinite
	 */
	public boolean isAllItemsInfinite()
	{
		return allItemsInfinite;
	}
	
	/**
	 * @return the canRestock
	 */
	public boolean isCanRestock()
	{
		return canRestock;
	}
	
	/**
	 * @return the allowBuying
	 */
	public boolean isAllowBuying()
	{
		return allowBuying;
	}
	
	/**
	 * @return the allowSelling
	 */
	public final boolean isAllowSelling()
	{
		return allowSelling;
	}
	
	/**
	 * @param int1
	 */
	public final void setBalance(int int1)
	{
		balance = int1;
	}
	
	/**
	 * @param boolean1
	 */
	public final void setAllItemsInfinite(boolean boolean1)
	{
		allItemsInfinite = boolean1;
	}
	
	/**
	 * @param boolean1
	 */
	public final void setCanRestock(boolean boolean1)
	{
		canRestock = boolean1;
	}
	
	/**
	 * @param boolean1
	 */
	public final void setAllowBuying(boolean boolean1)
	{
		allowBuying = boolean1;
	}
	
	/**
	 * @param boolean1
	 */
	public final void setAllowSelling(boolean boolean1)
	{
		allowSelling = boolean1;
	}
	
	/**
	 * @param int1
	 */
	public final void setSQLID(int int1)
	{
		sqlID = int1;
	}
	
	/**
	 * @param amount
	 */
	public final void addMoney(int amount)
	{
		if ((balance != -1) && isUseMoney)
		{
			balance += amount;
		}
	}
	
	/**
	 * @param amount
	 */
	public final void removeMoney(int amount)
	{
		if ((balance != -1) && isUseMoney)
		{
			balance -= amount;
		}
	}
	
	public final void update()
	{
		try
		{
			plugin.getSQL().update(this);
		}
		catch (SQLException e)
		{
			plugin.getLog().log(Level.WARNING, "Shop \"" + name + "\" could not update.");
		}
	}
	
	/**
	 * @param boolean1
	 */
	public final void setUseMoney(boolean boolean1)
	{
		isUseMoney = boolean1;
	}
	
	/**
	 * @return the isUseMoney
	 */
	public final boolean isUseMoney()
	{
		return isUseMoney;
	}
	
	/**
	 * @param item
	 */
	public void removeItem(EconItemStack item)
	{
		plugin.getLog().info("Infinite status: " + isAllItemsInfinite());
		
		if (!isAllItemsInfinite())
		{
			plugin.getLog().info("Inventory is: " + inventory.size());
			for (EconItemStack iter : inventory)
			{
				if (iter.getTypeId() == item.getTypeId())
				{
					plugin.getLog().info("TypeID found");
					if (iter.isSubtyped())
					{
						if (iter.getDurability() == item.getDurability())
						{
							if (iter.isInfinite() || (iter.getAmount() == -1))
							{
								plugin.getLog().info("S Remove item is infinte.");
								break;
							}
							else
							{
								plugin.getLog().info("S Removing item, setting new amount to:" + (iter.getAmount() - item.getAmount()));
								iter.changeAmount(iter.getAmount() - item.getAmount());
								break;
							}
						}
					}
					else
					{
						if (iter.isInfinite() || (iter.getAmount() == -1))
						{
							plugin.getLog().info("Remove item is infinte.");
							break;
						}
						else
						{
							plugin.getLog().info("Removing item, setting new amount to:" + (iter.getAmount() - item.getAmount()));
							iter.changeAmount(iter.getAmount() - item.getAmount());
							break;
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param item
	 */
	public void updateItem(EconItemStack item)
	{
		for (EconItemStack iter : inventory)
		{
			if (iter.getType().equals(item.getType()))
			{
				if (iter.isSubtyped())
				{
					if (iter.getDurability() == item.getDurability())
					{
						try
						{
							plugin.getSQL().update(sqlID, iter);
						}
						catch (SQLException e)
						{
							plugin.getLog().log(Level.WARNING, "CodeRedLite could not update an item. " + e.getLocalizedMessage());
						}
						break;
					}
				}
				else
				{
					try
					{
						plugin.getSQL().update(sqlID, iter);
					}
					catch (SQLException e)
					{
						plugin.getLog().log(Level.WARNING, "CodeRedLite could not update an item. " + e.getLocalizedMessage());
					}
					break;
				}
			}
		}
	}
	
	/**
	 * @param itemName
	 * @param subtype
	 * @return
	 */
	public EconItemStack getItem(String compactName, short subtype)
	{
		for (EconItemStack iter : inventory)
		{
			if (iter.getCompactName().equalsIgnoreCase(compactName))
			{
				if (iter.getDurability() == subtype)
				{
					return iter;
				}
			}
		}
		return null;
	}
	
	/**
	 * @param item
	 */
	public void addItem(EconItemStack item)
	{
		boolean done = false;
		for (EconItemStack iter : inventory)
		{
			if (iter.getTypeId() == item.getTypeId())
			{
				if (item.isSubtyped())
				{
					if (iter.getDurability() == item.getDurability())
					{
						if ((iter.getAmount() != -1) && !iter.isInfinite())
						{
							iter.changeAmount(iter.getAmount() + item.getAmount());
						}
						done = true;
						break;
					}
				}
				else
				{
					if ((iter.getAmount() != -1) && !iter.isInfinite())
					{
						iter.changeAmount(iter.getAmount() + item.getAmount());
					}
					done = true;
					break;
				}
			}
		}
		
		if (!done)
		{
			inventory.add(item);
			update();
		}
	}
}
