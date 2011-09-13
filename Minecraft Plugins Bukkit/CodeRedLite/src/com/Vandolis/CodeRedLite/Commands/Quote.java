/**
 *
 */
package com.Vandolis.CodeRedLite.Commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconItemStack;

/**
 * @author Vandolis
 */
public class Quote implements CommandExecutor
{
  private CodeRedLite plugin = null;
  
  /**
   * @param codeRedLite
   */
  public Quote(CodeRedLite codeRedLite)
  {
    plugin = codeRedLite;
  }
  
  /*
   * (non-Javadoc)
   * 
   * @see org.bukkit.command.CommandExecutor#onCommand(org.bukkit.command.CommandSender, org.bukkit.command.Command, java.lang.String,
   * java.lang.String[])
   */
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] split)
  {
    if (split.length >= 2)
    {
      StringBuffer buf = new StringBuffer();
      String itemName = "";
      int amount = 1;
      
      for (String iter : split)
      {
        try
        {
          amount = Integer.parseInt(iter);
        }
        catch (Exception e)
        {
          //itemName += iter;
          buf.append(iter);
        }
      }
      
      itemName = buf.toString().trim();
      itemName = itemName.toLowerCase();
      
      short subtype = 0;
      
      if (itemName.contains(":"))
      {
        String[] args = itemName.split(":");
        itemName = args[0];
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
      
      EconItemStack roughItem = null;
      
      List<EconItemStack> tmp = plugin.getShop().getItemsManual(itemName, subtype);
      if (tmp.size() > 1)
      {
        StringBuilder builder = new StringBuilder();
        for (EconItemStack iter : tmp)
        {
          builder.append(iter.getName() + ":" + iter.getDurability() + " ");
        }
        sender.sendMessage(plugin.getPluginMessage() + "Multiple Matches: " + builder.toString());
      }
      else
      {
        roughItem = (tmp.size() == 1) ? tmp.get(0) : null;
      }
      //      roughItem = plugin.getShop().getItem(itemName, subtype);
      
      if ((roughItem == null) && !plugin.getProperties().isEnforceItemWhitelist())
      {
        sender.sendMessage(plugin.getPluginMessage() + "Invalid item name.");
        return true;
      }
      else if (roughItem == null)
      {
        // Check whitelist
        for (EconItemStack iter : plugin.getRawItems())
        {
          if (iter.getCompactName().equalsIgnoreCase(itemName))
          {
            if (iter.getDurability() == subtype)
            {
              roughItem = iter;
            }
          }
        }
      }
      
      if (roughItem == null)
      {
        sender.sendMessage(plugin.getPluginMessage() + plugin.getShop().getName() + " will not buy that item.");
        return true;
      }
      
      if (roughItem.isSubtyped())
      {
        sender.sendMessage(plugin.getPluginMessage() + "Price results for " + amount + " "
          + roughItem.getName() + ":" + subtype);
      }
      else
      {
        sender.sendMessage(plugin.getPluginMessage() + "Price results for " + amount + " "
          + roughItem.getName());
      }
      if (roughItem.isInfinite() || (roughItem.getAmount() == -1))
      {
        if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
        {
          sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
            + "    Sell: "
            + roughItem.quoteSell(amount) + "    Stock: Infinite    Base: " + roughItem.getBasePrice()
            + "    Slope: "
            + roughItem.getSlope());
        }
        else
        {
          sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
            + "    Sell: "
            + roughItem.quoteSell(amount) + "    Stock: Infinite");
        }
      }
      else
      {
        if (amount > roughItem.getAmount())
        {
          amount = roughItem.getAmount();
        }
        if (plugin.isDebugging((Player) sender) && plugin.getProperties().isDynamicPrices())
        {
          sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
            + "    Sell: "
            + roughItem.quoteSell(amount) + "    Stock: " + roughItem.getAmount() + "    Base: "
            + roughItem.getBasePrice()
            + "    Slope: " + roughItem.getSlope());
        }
        else
        {
          sender.sendMessage(plugin.getPluginMessage() + "    Buy: " + roughItem.quoteBuy(amount)
            + "    Sell: "
            + roughItem.quoteSell(amount) + "    Stock: " + roughItem.getAmount());
        }
      }
    }
    
    return true;
  }
}
