/**
 *
 */
package com.Vandolis.CodeRedLite.Commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class PriceList implements CommandExecutor
{
  private CodeRedLite       plugin       = null;
  private static final int  MAX_PER_PAGE = 8;
  private ArrayList<String> shopRaw      = new ArrayList<String>();
  
  /**
   * @param codeRedLite
   */
  public PriceList(CodeRedLite codeRedLite)
  {
    plugin = codeRedLite;
  }
  
  public void sort()
  {
    shopRaw = new ArrayList<String>();
    
    for (EconItemStack iter : plugin.getShop().getInv())
    {
      shopRaw.add(iter.getCompactName());
    }
    
    Collections.sort(shopRaw);
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command,
   * java.lang.String, java.lang.String[])
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
  {
    EconPlayer econPlayer = plugin.getEconPlayer((Player) sender);
    
    sort();
    
    int pageNumber = 1;
    
    if (split.length == 1)
    {
      try
      {
        pageNumber = Integer.parseInt(split[0]);
      }
      catch (Exception e)
      {
        sender.sendMessage(plugin.getPluginMessage() + "An error occured.");
      }
    }
    
    ArrayList<String> processed = new ArrayList<String>();
    
    for (String iter : shopRaw)
    {
      short subtype = 0;
      String itemName = "";
      
      itemName = iter;
      if (iter.contains(":"))
      {
        String[] args = iter.split(":");
        if (args.length == 2)
        {
          subtype = Short.parseShort(args[1]);
        }
        else
        {
          sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
          return true;
        }
      }
      
      EconItemStack item = null;
      
      //plugin.getLog().info("Item: " + itemName + " " + subtype);
      item = plugin.getShop().getDirectItem(itemName);
      //            item = plugin.getShop().getItem(itemName, subtype);
      
      String str = "";
      
      if (item.getAmount() != 0)
      {
        if (subtype != 0)
        {
          str = "   " + itemName + ":" + subtype;
        }
        else
        {
          str = "   " + iter;
        }
        
        processed.add(str);
        
        if (item.isInfinite() || (item.getAmount() == -1) || plugin.getShop().isAllItemsInfinite())
        {
          if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
          {
            str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() +
              "   Stock: Infinite    Base: " + item.getBasePrice() + "    Slope: " + item.getSlope();
          }
          else
          {
            str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() +
              "   Stock: Infinite";
          }
        }
        else
        {
          if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
          {
            str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() + "   Stock: " +
              item.getAmount() + "    Base: " + item.getBasePrice() + "    Slope: " + item.getSlope();
          }
          else
          {
            str = "        Buy: " + item.getPriceBuy() + "   Sell: " + item.getPriceSell() + "   Stock: " +
              item.getAmount();
          }
        }
        
        processed.add(str);
      }
    }
    
    HashMap<Integer, ArrayList<String>> map = new HashMap<Integer, ArrayList<String>>();
    
    int count = 0;
    ArrayList<String> page = new ArrayList<String>();
    
    while (!processed.isEmpty())
    {
      page.add(processed.get(0));
      //plugin.getLog().info("Added: " + processed.get(0));
      processed.remove(0);
      if (page.size() == MAX_PER_PAGE)
      {
        //plugin.getLog().info("Adding page to map");
        map.put(count, page);
        count++;
        page = new ArrayList<String>();
      }
    }
    
    if (!page.isEmpty())
    {
      map.put(count, page);
    }
    
    if ((pageNumber <= 0) || (pageNumber > map.size()))
    {
      pageNumber = 1;
    }
    
    page = map.get(pageNumber - 1);
    if (page == null)
    {
      sender.sendMessage(plugin.getPluginMessage() + "There are no items for sale!");
    }
    else
    {
      sender.sendMessage(plugin.getPluginMessage() + "PriceList for " + plugin.getShop().getName() + " (Page "
        + pageNumber + " of "
        + map.size() + ")");
      
      for (String iter : page)
      {
        sender.sendMessage(plugin.getPluginMessage() + iter);
      }
    }
    
    return true;
  }
}
