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
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class Econ implements CommandExecutor
{
  private CodeRedLite plugin = null;
  
  public Econ(CodeRedLite codeRedLite)
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
    if (split.length >= 1)
    {
      String subCommand = split[0];
      
      if (subCommand.equalsIgnoreCase("pay") || subCommand.equalsIgnoreCase("buy") ||
        subCommand.equalsIgnoreCase("sell") || subCommand.equalsIgnoreCase("balance") ||
        subCommand.equalsIgnoreCase("quote") || subCommand.equalsIgnoreCase("price") ||
        subCommand.equalsIgnoreCase("pricelist"))
      {
        StringBuffer buf = new StringBuffer();
        
        for (String string : split)
        {
          buf.append(string + " ");
        }
        
        String cmd = buf.toString().trim();
        
        ((Player) sender).performCommand(cmd);
        return true;
      }
      else
      {
        if (subCommand.equalsIgnoreCase("Admin"))
        {
          boolean admin = false;
          
          if (plugin.getPermissionHandler() == null)
          {
            admin = plugin.isDebugging((Player) sender);
          }
          else
          {
            admin = plugin.getPermissionHandler().has((Player) sender, "coderedlite.admin");
          }
          
          if (!admin)
          {
            sender.sendMessage(plugin.getPluginMessage() + "You do not have permission.");
            return true;
          }
          else
          {
            if (split.length >= 2)
            {
              String adminCommand = split[1];
              
              if (adminCommand.equalsIgnoreCase("addmoney") || adminCommand.equalsIgnoreCase("am"))
              {
                // Add money
                if (split.length >= 4)
                {
                  EconPlayer econPlayer = plugin.getEconPlayer(split[2]);
                  int amount = 0;
                  
                  if (econPlayer == null)
                  {
                    sender.sendMessage(plugin.getPluginMessage() + "Player must be online.");
                    return true;
                  }
                  
                  try
                  {
                    amount = Integer.parseInt(split[3]);
                  }
                  catch (Exception e)
                  {
                    sender.sendMessage(plugin.getPluginMessage() + "Invalid amount.");
                    return true;
                  }
                  
                  econPlayer.addMoney(amount);
                  econPlayer.update();
                  
                  sender.sendMessage(plugin.getPluginMessage() + "Successfully changed the players balance.");
                  return true;
                }
                else
                {
                  sender.sendMessage(plugin.getPluginMessage() + "Proper syntax is: addmoney <player> <amount>");
                  return true;
                }
              }
              else if (adminCommand.equalsIgnoreCase("getbalance") || adminCommand.equalsIgnoreCase("gb"))
              {
                // Get balance
                if (split.length >= 3)
                {
                  EconPlayer econPlayer = plugin.getEconPlayer(split[2]);
                  
                  if (econPlayer == null)
                  {
                    sender.sendMessage(plugin.getPluginMessage() + "Player must be online.");
                    return true;
                  }
                  
                  sender.sendMessage(plugin.getPluginMessage() + "Balance for " + econPlayer.getPlayer().getName()
                    + ": " + econPlayer.getBalance());
                  return true;
                }
                else
                {
                  sender.sendMessage(plugin.getPluginMessage() + "Proper syntax is: getbalance <player>");
                  return true;
                }
              }
              else if (adminCommand.equalsIgnoreCase("clearshop") || adminCommand.equalsIgnoreCase("clear"))
              {
                for (EconItemStack iter : plugin.getShop().getInv())
                {
                  iter.changeAmount(0);
                }
                plugin.getShop().softUpdate();
                sender.sendMessage(plugin.getPluginMessage() + "All item amounts set to 0");
              }
              else if (adminCommand.equalsIgnoreCase("changestock") || adminCommand.equalsIgnoreCase("cs"))
              {
                // Change items stock
                if (split.length >= 4)
                {
                  String itemName = "";
                  int amount = 0;
                  short subtype = 0;
                  StringBuilder builder = new StringBuilder();
                  
                  for (int i = 2; i < split.length; i++)
                  {
                    // Try and convert into the amount, if that fails it must be part of the name.
                    try
                    {
                      amount = Integer.valueOf(split[i]); // Try and parse it
                    }
                    catch (NumberFormatException e)
                    {
                      // Not a number, add to name.
                      
                      builder.append(split[i]); // Append it to the buffer
                    }
                  }
                  
                  itemName = builder.toString().trim(); // Make the buffer a string, trim it
                  itemName = itemName.toLowerCase(); // Make the item lower case
                  
                  // Check for declared subtype
                  if (itemName.contains(":"))
                  {
                    // User is defining a subtype
                    
                    String[] args = itemName.split(":"); // Split around the colon
                    
                    itemName = args[0]; // Set the item name to the first half
                    
                    // Double check for the length
                    if (args.length == 2)
                    {
                      // Safe length, try to get the subtype
                      
                      try
                      {
                        subtype = Short.parseShort(args[1]);
                      }
                      catch (Exception e)
                      {
                        sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
                        return true;
                      }
                    }
                    else
                    {
                      sender.sendMessage(plugin.getPluginMessage() + "Invalid syntax. Correct is 'item:subtype#'");
                      return true;
                    }
                  }
                  
                  EconItemStack stock;
                  
                  List<EconItemStack> tmp = plugin.getShop().getItemsManual(itemName, subtype);
                  stock = (tmp.size() == 1) ? tmp.get(0) : null;
                  //                  stock = plugin.getShop().getItem(itemName, subtype);
                  
                  if (stock == null)
                  {
                    for (EconItemStack iter : plugin.getRawItems())
                    {
                      if (iter.getCompactName().equalsIgnoreCase(itemName))
                      {
                        plugin.getShop().addItem(new EconItemStack(iter, amount, plugin));
                        
                        sender.sendMessage(plugin.getPluginMessage() + "Successfully added the item to the shop.");
                        return true;
                      }
                    }
                    
                    sender.sendMessage(plugin.getPluginMessage()
                      + "Unable to add the given item to the shop, check item name?");
                    return true;
                  }
                  else
                  {
                    stock.changeAmount(amount);
                    
                    sender.sendMessage(plugin.getPluginMessage() + "Successfully changed the item amount.");
                    return true;
                  }
                  
                }
                else
                {
                  sender.sendMessage(plugin.getPluginMessage() + "Proper syntax is: changestock <item> <newamount>");
                  return true;
                }
              }
              else if (adminCommand.equalsIgnoreCase("changeprice") || adminCommand.equalsIgnoreCase("cp"))
              {
                // Change the price
                if (split.length >= 4)
                {
                  String itemName = "";
                  int amount = 0;
                  short subtype = 0;
                  StringBuilder builder = new StringBuilder();
                  
                  for (int i = 2; i < split.length; i++)
                  {
                    // Try and convert into the amount, if that fails it must be part of the name.
                    try
                    {
                      amount = Integer.valueOf(split[i]); // Try and parse it
                    }
                    catch (NumberFormatException e)
                    {
                      // Not a number, add to name.
                      
                      builder.append(split[i]); // Append it to the buffer
                    }
                  }
                  
                  itemName = builder.toString().trim(); // Make the buffer a string, trim it
                  itemName = itemName.toLowerCase(); // Make the item lower case
                  
                  // Check for declared subtype
                  if (itemName.contains(":"))
                  {
                    // User is defining a subtype
                    
                    String[] args = itemName.split(":"); // Split around the colon
                    
                    itemName = args[0]; // Set the item name to the first half
                    
                    // Double check for the length
                    if (args.length == 2)
                    {
                      // Safe length, try to get the subtype
                      
                      try
                      {
                        subtype = Short.parseShort(args[1]);
                      }
                      catch (Exception e)
                      {
                        sender.sendMessage(plugin.getPluginMessage() + "Invalid subtype.");
                        return true;
                      }
                    }
                    else
                    {
                      sender.sendMessage(plugin.getPluginMessage() + "Invalid syntax. Correct is 'item:subtype#'");
                      return true;
                    }
                  }
                  
                  EconItemStack stock = plugin.getShop().getItemsManual(itemName, subtype).get(0);
                  
                  if (stock == null)
                  {
                    sender.sendMessage(plugin.getPluginMessage() + "The shop does not have that item.");
                    return true;
                  }
                  else
                  {
                    stock.setBasePrice(amount);
                    
                    sender.sendMessage(plugin.getPluginMessage() + "Successfully changed the item price.");
                    return true;
                  }
                  
                }
                else
                {
                  sender.sendMessage(plugin.getPluginMessage() + "Proper syntax is: changeprice <item> <newprice>");
                  return true;
                }
              }
            }
            else
            {
              sender.sendMessage(plugin.getPluginMessage() + "Valid admin commands are: addmoney <player> <#> (am), " +
                "getbalance <player> (gb), changestock <item> <new#> (cs), changeprice <item> <new#> (cp)");
              return true;
            }
          }
          
          return true;
        }
        else
        {
          return true;
        }
      }
    }
    
    return true;
  }
}
