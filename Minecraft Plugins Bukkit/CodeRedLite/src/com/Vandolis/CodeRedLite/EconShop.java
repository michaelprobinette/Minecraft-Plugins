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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * @author Vandolis
 */
public class EconShop
{
  private ArrayList<EconItemStack> inventory        = new ArrayList<EconItemStack>();
  private int                      balance          = 0;
  private int                      sqlID            = 0;
  private String                   name             = "";
  private boolean                  allItemsInfinite = true;
  private boolean                  canRestock       = true;
  
  private boolean                  allowBuying      = true;
  private boolean                  allowSelling     = true;
  private boolean                  isUseMoney       = false;
  private CodeRedLite              plugin           = null;
  
  /**
   * Default ctor. Makes an EconShop with the given name and instance
   * 
   * @param name
   * @param codeRed
   */
  public EconShop(String name, CodeRedLite codeRed)
  {
    this.name = name;
    plugin = codeRed;
  }
  
  /**
   * Attempts to return the shops item based on the given compact name (no spaces). Returns null if not found.
   * 
   * @param compactName
   * @return EconItemStack if found, null if not.
   * @deprecated
   */
  @Deprecated
  public EconItemStack getItem(String compactName)
  {
    // Loop through the inventory
    for (EconItemStack iter : inventory)
    {
      // Check if correct item
      if (iter.getCompactName().equalsIgnoreCase(compactName))
      {
        return iter; // Correct item found, return it
      }
    }
    
    return null; // Not found, return null
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
   * @param newBalance
   */
  public final void setBalance(int newBalance)
  {
    balance = newBalance;
  }
  
  /**
   * @param itemsInfinite
   */
  public final void setAllItemsInfinite(boolean itemsInfinite)
  {
    allItemsInfinite = itemsInfinite;
  }
  
  /**
   * @param restock
   */
  public final void setCanRestock(boolean restock)
  {
    canRestock = restock;
  }
  
  /**
   * @param buying
   */
  public final void setAllowBuying(boolean buying)
  {
    allowBuying = buying;
  }
  
  /**
   * @param selling
   */
  public final void setAllowSelling(boolean selling)
  {
    allowSelling = selling;
  }
  
  /**
   * @param id
   */
  public final void setSQLID(int id)
  {
    sqlID = id;
  }
  
  /**
   * Add given amount to the shops balanceChecks if the shop uses money
   * or has an infinite amount (-1)
   * 
   * @param amount
   */
  public final void addMoney(int amount)
  {
    // Check for infinite balance and check if the shop uses it
    if ((balance != -1) && isUseMoney)
    {
      // Not infinite money and it is using money
      
      balance += amount; // Add to the balance
    }
  }
  
  /**
   * Subtract given amount from the shops balance. Checks if the shop uses money
   * or has an infinite amount (-1)
   * 
   * @param amount
   */
  public final void removeMoney(int amount)
  {
    // Check for infinite balance and check if the shop uses it
    if ((balance != -1) && isUseMoney)
    {
      // Not infinite money and it is using money
      
      balance -= amount; // Remove it from the balance
    }
  }
  
  /**
   * @deprecated
   */
  @Deprecated
  public final void hardUpdate()
  {
    try
    {
      plugin.getSQL().hardShopUpdate(this);
    }
    catch (SQLException e)
    {
      plugin.getLog().log(Level.WARNING, "Shop \"" + name + "\" could not update.");
    }
  }
  
  /**
   * @param useMoney
   */
  public final void setUseMoney(boolean useMoney)
  {
    isUseMoney = useMoney;
  }
  
  /**
   * @return the isUseMoney
   */
  public final boolean isUseMoney()
  {
    return isUseMoney;
  }
  
  /**
   * Removes the item from the shops inventory. Writes the new amount to the database.
   * 
   * @param item
   */
  public void removeItem(EconItemStack item)
  {
    plugin.getLog().info("Infinite status: " + isAllItemsInfinite());
    
    // Check for infinite items
    if (!isAllItemsInfinite())
    {
      // Non infinite items
      
      plugin.getLog().info("Inventory is: " + inventory.size());
      
      // Search for the item in the inventory
      for (EconItemStack iter : inventory)
      {
        // Check for id
        if (iter.getTypeId() == item.getTypeId())
        {
          // Id found
          
          plugin.getLog().info("TypeID found");
          
          // Check if item is subtyped
          if (iter.isSubtyped())
          {
            // Item is subtyped
            
            // Check the subtype
            if (iter.getDurability() == item.getDurability())
            {
              // Correct subtype
              
              // Check item infinite amounts
              if (iter.isInfinite() || (iter.getAmount() == -1))
              {
                // Item has infinite amounts
                
                plugin.getLog().info("S Remove item is infinte.");
              }
              else
              {
                // Non infinite amounts
                
                plugin.getLog().info(
                  "S Removing item, setting new amount to:" + (iter.getAmount() - item.getAmount()));
                
                // Change the amount of the item
                iter.changeAmount(iter.getAmount() - item.getAmount());
              }
              
              // Update and write to sql
              softUpdateItem(iter);
              break; // Done
            }
          }
          else
          {
            // Not subtyped
            
            // Check item infinite amounts
            if (iter.isInfinite() || (iter.getAmount() == -1))
            {
              // Item has infinite amounts
              
              plugin.getLog().info("Remove item is infinte.");
            }
            else
            {
              // Non infinite amounts
              
              plugin.getLog().info(
                "Removing item, setting new amount to:" + (iter.getAmount() - item.getAmount()));
              
              // Change the amount of the item
              iter.changeAmount(iter.getAmount() - item.getAmount());
            }
            
            // Update and write to sql
            softUpdateItem(iter);
            break; // Done
          }
        }
      }
    }
  }
  
  /**
   * @param item
   * @deprecated
   */
  @Deprecated
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
              plugin.getSQL().softShopUpdateItem(sqlID, iter);
            }
            catch (SQLException e)
            {
              plugin.getLog().log(Level.WARNING,
                "CodeRedLite could not update an item. " + e.getLocalizedMessage());
            }
            break;
          }
        }
        else
        {
          try
          {
            plugin.getSQL().softShopUpdateItem(sqlID, iter);
          }
          catch (SQLException e)
          {
            plugin.getLog().log(Level.WARNING,
              "CodeRedLite could not update an item. " + e.getLocalizedMessage());
          }
          break;
        }
      }
    }
  }
  
  /**
   * Attempts to return the item in the shops inventory based on the given compact name and subtype. Returns null
   * if not found
   * 
   * @param itemName
   * @param subtype
   * @return EconItemStack in the inventory
   */
  public EconItemStack getItem(String compactName, short subtype)
  {
    // Search through the inventory
    for (EconItemStack iter : inventory)
    {
      // Check the compact name
      if (iter.getCompactName().equalsIgnoreCase(compactName))
      {
        // Check the subtype
        if (iter.getDurability() == subtype)
        {
          return iter; // Found, return it
        }
      }
    }
    
    return null; // Not found, return null
  }
  
  /**
   * Adds the item to the shops inventory
   * 
   * @param item
   *          to add
   */
  public void addItem(EconItemStack item)
  {
    boolean itemAdded = false; // True if item added (already in inventory) False if not (not already in inventory)
    
    // Search through the inventory
    for (EconItemStack iter : inventory)
    {
      // Check the item id
      if (iter.getTypeId() == item.getTypeId())
      {
        // Same item id
        
        // Check if subtyped
        if (item.isSubtyped())
        {
          // Subtyped
          
          // Check subtype
          if (iter.getDurability() == item.getDurability())
          {
            // Same subtype
            
            // Check for infinite amounts
            if ((iter.getAmount() != -1) && !iter.isInfinite())
            {
              // Non infinite
              
              // Change the item amount
              iter.changeAmount(iter.getAmount() + item.getAmount());
            }
            
            itemAdded = true; // Item added, done
            softUpdateItem(item); // Write the item to sql
            
            break; // Done
          }
        }
        else
        {
          // Not subtyped
          
          // Check for infinite amounts
          if ((iter.getAmount() != -1) && !iter.isInfinite())
          {
            // Non infinite amounts
            
            // Change the item amount
            iter.changeAmount(iter.getAmount() + item.getAmount());
          }
          
          itemAdded = true; // Item added, done
          softUpdateItem(item); // Write item to sql
          
          break; // Done
        }
      }
    }
    
    // Check if item added
    if (!itemAdded)
    {
      inventory.add(item); // Add the item to the list
      softUpdateItem(item); // Write to sql
      //hardUpdate();
    }
  }
  
  /**
   * Writes the given item to the database. If the item is not there, adds it. Does not delete.
   * 
   * @param item
   *          to update
   */
  private void softUpdateItem(EconItemStack item)
  {
    try
    {
      // Call the database to update the item
      plugin.getSQL().softShopUpdateItem(sqlID, item);
    }
    catch (SQLException e)
    {
      plugin.getLog().log(Level.SEVERE, e.getLocalizedMessage());
      plugin.getLog().log(Level.SEVERE, "CodeRedLite could not execute softUpdateItem.");
    }
  }
  
  /**
   * Performs a full soft update. No deletes, adds the shop if not in table. Does updates otherwise. Deals with
   * inventory too.
   */
  public void softUpdate()
  {
    try
    {
      // Call the database to update the shop
      plugin.getSQL().softShopUpdate(this);
    }
    catch (SQLException e)
    {
      plugin.getLog().log(Level.SEVERE, e.getLocalizedMessage());
      plugin.getLog().log(Level.SEVERE, "CodeRedLite could not execute softUpdate.");
    }
  }
}
