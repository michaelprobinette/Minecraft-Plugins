/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.Vandolis.CodeRedEconomy.Database.Items;
import com.bukkit.Vandolis.CodeRedEconomy.Database.PlayerTransactions;
import com.bukkit.Vandolis.CodeRedEconomy.Database.Players;
import com.bukkit.Vandolis.CodeRedEconomy.Database.ShopItems;
import com.bukkit.Vandolis.CodeRedEconomy.Database.ShopTransactions;
import com.bukkit.Vandolis.CodeRedEconomy.Database.Shops;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.DataManager;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconStats;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Money;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.PriceList;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Shop;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.ShopItem;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.ShopItemStack;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.ShopList;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Transaction;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.User;

/**
 * @author Vandolis
 */
public class CommandInterpreter {
	public enum Command {
		BUY, SELL, PAY, UNDO, ECON, PRICES, BALANCE, ECON_SQL, ECON_RESET, ECON_SHOPS, ECON_SETSHOP, RESTOCK, BLOCK_BROKEN, BAD_WORD
	};
	
	private static HashMap<String, String>	playerShops	= new HashMap<String, String>();
	private static CodeRedEconomy			plugin		= null;
	
	public static boolean interpret(Command cmd, Event event) {
		Player player = null;
		Block block = null;
		String[] split = null;
		
		if (cmd == Command.BLOCK_BROKEN) {
			player = ((BlockDamageEvent) event).getPlayer();
			block = ((BlockDamageEvent) event).getBlock();
		}
		else if ((event.getType() == Event.Type.PLAYER_COMMAND) || (event.getType() == Type.PLAYER_CHAT)) {
			player = ((PlayerChatEvent) event).getPlayer();
			split = ((PlayerChatEvent) event).getMessage().split(" ");
		}
		
		String shopName = playerShops.get(player.getName());
		
		if (shopName == null) {
			/*
			 * Add them to default shop, "The Shop"
			 */
			shopName = "The Shop";
			
			playerShops.put(player.getName(), "The Shop");
		}
		
		switch (cmd) {
			case BUY:
				if (EconomyProperties.isUseSQL()) {
					buyDB(player, split);
				}
				else {
					buyFlatfile(player, split);
				}
				break;
			case SELL:
				if (EconomyProperties.isUseSQL()) {
					sellDB(player, split);
				}
				else {
					sellFlatfile(player, split);
				}
				break;
			case PAY:
				if (EconomyProperties.isUseSQL()) {
					payDB(player, split);
				}
				else {
					payFlatfile(player, split);
				}
				break;
			case UNDO:
				if (EconomyProperties.isUseSQL()) {
					undoDB(player, split);
				}
				else {
					undoFlatfile(player, split);
				}
				break;
			case ECON:
				if (EconomyProperties.isUseSQL()) {
					econDB(player, event);
				}
				else {
					econFlatfile(player, split);
				}
				break;
			case ECON_RESET:
				if (EconomyProperties.isUseSQL()) {
					econResetDB(player, split);
				}
				else {
					econFlatfile(player, split);
				}
				break;
			case ECON_SETSHOP:
				if (EconomyProperties.isUseSQL()) {
					econSetShopDB(player, split);
				}
				else {
					econFlatfile(player, split);
				}
				break;
			case ECON_SHOPS:
				if (EconomyProperties.isUseSQL()) {
					econShopsDB(player, split);
				}
				else {
					econFlatfile(player, split);
				}
				break;
			case ECON_SQL:
				if (EconomyProperties.isUseSQL()) {
					econSQLDB(player, split);
				}
				else {
					econFlatfile(player, split);
				}
				break;
			case PRICES:
				if (EconomyProperties.isUseSQL()) {
					pricesDB(player, split);
				}
				else {
					pricesFlatfile(player, split);
				}
				break;
			case BALANCE:
				if (EconomyProperties.isUseSQL()) {
					balanceDB(player, split);
				}
				else {
					balanceFlatfile(player, split);
				}
				break;
			case RESTOCK:
				if (EconomyProperties.isUseSQL()) {
					restockDB(player, split);
				}
				else {
					restockFlatfile(player, split);
				}
				break;
			case BLOCK_BROKEN:
				if (EconomyProperties.isUseSQL()) {
					blockBrokenDB(player, block);
				}
				else {
					blockBrokenFlatfile(player, block);
				}
				break;
			case BAD_WORD:
				if (EconomyProperties.isUseSQL()) {
					badWordDB(player, split);
				}
				else {
					badWordFlatfile(player, split);
				}
				break;
			default:
				return false;
		}
		
		return true;
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void badWordFlatfile(Player player, String[] split) {
		String message = "";
		
		for (String iter : split) {
			message += " " + iter;
		}
		
		message = message.trim();
		
		String word = "";
		
		do {
			word = DataManager.getBadWord(message);
			if (!word.equalsIgnoreCase("")) {
				User user = DataManager.getUser(player);
				User badWordEnt = DataManager.getUser("BadWord-" + word);
				
				Transaction.process(new Transaction(badWordEnt, user, new Money(DataManager.getBadWords().get(word))), false);
			}
			
		} while (!word.equalsIgnoreCase(""));
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void badWordDB(Player player, String[] split) {
		
	}
	
	/**
	 * @param player
	 * @param block
	 */
	private static void blockBrokenFlatfile(Player player, Block block) {
		ShopItem broken = DataManager.getItem(block.getTypeId());
		
		if (broken != null) {
			Money breakValue = broken.getBreakValue();
			
			if (breakValue.getAmount() != 0) {
				DataManager.getUser(player).getMoney().addAmount(breakValue.getAmount());
			}
		}
	}
	
	/**
	 * @param player
	 * @param block
	 */
	private static void blockBrokenDB(Player player, Block block) {
		int playerID = Players.getID(player);
		
		Players p = null;
		
		if (playerID == 0) {
			p = new Players(player.getName());
		}
		else {
			p = new Players(playerID);
		}
		
		Items item = new Items(Items.getID(block.getTypeId(), block.getData()));
		
		p.setBalance(p.getBalance() + item.getBreakValue());
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void restockFlatfile(Player player, String[] split) {
		player.sendMessage(EconomyProperties.getPluginMessage() + "Forcing all shops to restock.");
		
		System.out.println(player.getName() + " is force restocking all shops.");
		
		for (Shop iter : DataManager.getShops()) {
			iter.restock(true);
		}
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void restockDB(Player player, String[] split) {
		try {
			Statement stat = DriverManager.getConnection(EconomyProperties.getDB()).createStatement();
			ResultSet rs = stat.executeQuery("select * from Items;");
			Shops shop = null;
			
			int shopID = Shops.getId("The Shop");
			
			if (shopID == -1) {
				shop = new Shops("The Shop");
			}
			else {
				shop = new Shops(shopID);
			}
			
			while (rs.next()) {
				shop.addItem(new Items(rs.getInt("ItemID"), rs.getBoolean("IsSubtyped"), rs.getInt("Subtype"), rs.getString("Name"), rs
						.getInt("BuyPrice"), rs.getInt("SellPrice"), rs.getInt("BreakValue")));
			}
			
			rs.close();
			stat.close();
		}
		catch (SQLException e) {
			
		}
	}
	
	/**
	 * Loads the SQL with data from the flatfile
	 * 
	 * @param player
	 * @param split
	 */
	private static void econSQLDB(Player player, String[] split) {
		DataManager.load(plugin);
		
		/*
		 * Items, drop table data to prevent copies
		 */
		try {
			Statement drop = EconomyProperties.getConn().createStatement();
			drop.executeUpdate("delete from Items;");
			drop.executeUpdate("delete from ShopItems;");
			drop.close();
		}
		catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for (ShopItem iter : DataManager.getItemList()) {
			int itemId = Items.getId(iter.getName());
			
			if (itemId == 0) {
				Items item =
						new Items(iter.getItemId(), false, 0, iter.getName(), iter.getBuyPrice(), iter.getSellPrice(), iter.getBreakValue()
								.getAmount());
			}
			else {
				Items item = new Items(itemId);
				item.setBuyPrice(iter.getBuyPrice());
				item.setSellPrice(iter.getSellPrice());
				item.setBreakValue(iter.getBreakValue().getAmount());
				item.update();
			}
		}
		
		for (User iter : DataManager.getUsers()) {
			int playerId = Players.getID(iter.getName());
			
			if (playerId == 0) {
				Players p = new Players(iter.getName());
				p.setBalance(iter.getMoney().getAmount());
			}
			else {
				Players p = new Players(playerId);
				p.setBalance(iter.getMoney().getAmount());
			}
		}
		
		for (Shop iter : DataManager.getShops()) {
			Shops shop = null;
			
			int shopId = Shops.getId(iter.getName());
			
			if (shopId == 0) {
				shop = new Shops(iter.getName());
			}
			else {
				shop = new Shops(shopId);
			}
			
			shop.setBalance(iter.getMoney().getAmount());
			shop.setAllItemsInfinite(iter.getInfItems());
			shop.setCanRestock(iter.isRestock());
			shop.setAllowBuying(iter.isUsersCanBuy());
			shop.setAllowSelling(iter.isUsersCanSell());
			shop.setHidden(iter.isHidden());
			
			for (ShopItemStack stack : iter.getAvailItems()) {
				ShopItems temp =
						new ShopItems(Shops.getId(iter.getName()), Items.getID(stack.getItemId(), (short) 0), stack.getAmountAvail(),
								stack.getAmountAvail() == EconomyProperties.getInfValue(), stack.getBuyPrice(), stack.getSellPrice());
				//				shop.addItem(new ShopItems(Shops.getId(iter.getName()), Items.getID(stack.getItemId(), (short) 0), stack.getAmountAvail(),
				//						stack.getAmountAvail() == EconomyProperties.getInfValue(), stack.getBuyPrice(), stack.getSellPrice()));
			}
		}
		com.bukkit.Vandolis.CodeRedEconomy.Database.PriceList.setCurrentShop("");
		DataManager.destroy();
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void econShopsDB(Player player, String[] split) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void econSetShopDB(Player player, String[] split) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void econResetDB(Player player, String[] split) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void balanceFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		user.showBalance();
	}
	
	/**
	 * @param player
	 * @param split
	 */
	private static void balanceDB(Player player, String[] split) {
		Players p = new Players(Players.getID(player));
		
		player.sendMessage(EconomyProperties.getPluginMessage() + "Your balance is " + p.getBalance() + " "
				+ EconomyProperties.getMoneyName());
	}
	
	private static void pricesDB(Player player, String[] split) {
		//		Players p = new Players(Players.getID(player));
		boolean done = false;
		
		if (split.length == 1) {
			com.bukkit.Vandolis.CodeRedEconomy.Database.PriceList.priceList(player, playerShops.get(player.getName()), 1);
			
			done = true;
		}
		else {
			String itemName = "";
			for (String iter : split) {
				try {
					if (!iter.equalsIgnoreCase(split[0])) {
						int page = Integer.valueOf(iter.trim());
						
						com.bukkit.Vandolis.CodeRedEconomy.Database.PriceList.priceList(player, playerShops.get(player.getName()), page);
						
						done = true;
					}
				}
				catch (NumberFormatException e) {
					itemName += " " + iter;
				}
				catch (Exception e1) {
					e1.printStackTrace();
				}
			}
			
			itemName = itemName.trim();
			
			if (!done) {
				if (EconomyProperties.isDebug()) {
					System.out.println("Pricing single item: " + itemName);
				}
				
				com.bukkit.Vandolis.CodeRedEconomy.Database.PriceList.priceSingleItem(player, playerShops.get(player.getName()), itemName);
			}
		}
	}
	
	private static void econDB(Player player, Event event) {
		String[] split = ((PlayerChatEvent) event).getMessage().split(" ");
		
		if (split.length >= 2) {
			if (split[1].equalsIgnoreCase("reset") && CodeRedEconomy.isOp(player.getName())) {
				interpret(Command.ECON_RESET, event);
			}
			else if (split[1].equalsIgnoreCase("sql") && CodeRedEconomy.isOp(player.getName())) {
				interpret(Command.ECON_SQL, event);
			}
			else if (split[1].equalsIgnoreCase("shops")) {
				interpret(Command.ECON_SHOPS, event);
			}
			else if (split[1].equalsIgnoreCase("setshop")) {
				interpret(Command.ECON_SETSHOP, event);
			}
		}
	}
	
	private static void undoDB(Player player, String[] split) {
		Players p = new Players(Players.getID(player));
		
		ShopTransactions undone = p.getLastShopTransaction();
		
		com.bukkit.Vandolis.CodeRedEconomy.Database.TransactionManager.undo(undone);
	}
	
	/**
	 * @param user
	 * @param split
	 */
	private static void pricesFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		if (split.length >= 2) {
			try {
				int page = Integer.valueOf(split[1]);
				
				PriceList.priceList(user, page, DataManager.getShop(playerShops.get(player.getName())));
			}
			catch (Exception e) {
				String itemName = "";
				for (int i = 1; i < split.length; i++) {
					itemName += " " + split[i];
				}
				itemName = itemName.trim();
				PriceList.priceSingleItem(DataManager.getUser(player), itemName, DataManager.getShop(playerShops.get(player.getName())));
			}
		}
		else {
			PriceList.priceList(user, 1, DataManager.getShop(playerShops.get(player.getName())));
		}
	}
	
	/**
	 * @param user
	 * @param split
	 */
	private static void econFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		if (split.length >= 2) {
			if (split[1].equalsIgnoreCase("reset") && CodeRedEconomy.isOp(user.getName())) {
				if (split.length >= 3) {
					if (split[2].equalsIgnoreCase("shops") || split[2].equalsIgnoreCase("shop")) {
						
					}
					else if (split[2].equalsIgnoreCase("stats")) {
						user.sendMessage("Resetting stats.");
						EconStats.reset();
					}
				}
				else {
					user.sendMessage("Useage is /econ reset [items,stats]");
				}
			}
			else if (split[1].equalsIgnoreCase("sql") && (CodeRedEconomy.isOp(user.getName()) || DataManager.getDebug())) {
				EconomyProperties.setUseSQL(true);
				DataManager.save();
			}
			else if (split[1].equalsIgnoreCase("shops")) {
				
				/*
				 * Display the shops to the user
				 */
				if (split.length >= 3) {
					int page = Integer.valueOf(split[2]);
					ShopList.showPage(user, page);
				}
				else {
					ShopList.showPage(user, 1);
				}
			}
			else if (split[1].equalsIgnoreCase("setshop")) {
				
				if (split.length >= 3) {
					String shopName = "";
					for (int i = 2; i < split.length; i++) {
						shopName += split[i] + " ";
					}
					
					if (DataManager.getDebug()) {
						System.out.println("Searching shop names for: \"" + shopName.trim() + "\"");
					}
					
					boolean found = false;
					
					for (Shop shopIter : DataManager.getShops()) {
						if (shopIter.getName().equalsIgnoreCase(shopName.trim())) {
							/*
							 * Valid shop
							 */
							playerShops.put(player.getName(), shopIter.getName());
							found = true;
							user.sendMessage("Set your active shop to: " + shopIter.getName());
							break;
						}
					}
					
					if (!found) {
						user.sendMessage("Please enter a valid shop name. Type /econ shops for a list.");
					}
				}
				else {
					String shopName = playerShops.get(user.getName());
					
					if (shopName == null) {
						/*
						 * Add them to default shop, "The Shop"
						 */
						shopName = "The Shop";
						
						playerShops.put(user.getName(), "The Shop");
					}
					user.sendMessage("Active shop is: " + shopName + ". To change use /econ setshop [shop name] Type /shops for a list.");
				}
			}
		}
	}
	
	/**
	 * @param user
	 * @param split
	 */
	private static void undoFlatfile(Player player, String[] split) {
		DataManager.getUser(player).undoLastTrans();
	}
	
	private static void buyDB(Player player, String[] split) {
		String itemName = "";
		String shopName = "";
		
		int amount = 0;
		
		for (String iter : split) {
			if (!iter.equalsIgnoreCase(split[0])) {
				try {
					amount = Integer.valueOf(iter);
				}
				catch (Exception e) {
					itemName += " " + iter;
				}
			}
		}
		
		itemName = itemName.trim();
		
		shopName = playerShops.get(player.getName());
		
		if (EconomyProperties.isDebug()) {
			System.out.println("Shop Name: " + shopName);
			System.out.println("Item Name: " + itemName);
		}
		
		int shopId = Shops.getId(shopName);
		int itemId = Items.getId(itemName);
		
		int shopItemID = ShopItems.getShopItemId(shopId, itemId);
		try {
			if (shopItemID != 0) {
				com.bukkit.Vandolis.CodeRedEconomy.Database.TransactionManager.add(new ShopTransactions(player, playerShops.get(player
						.getName()), itemName, amount, true));
			}
			else {
				throw new EconException("Invalid item.", "");
			}
		}
		catch (EconException e) {
			player.sendMessage(EconomyProperties.getPluginMessage() + e.getBuyMsg());
		}
	}
	
	private static void sellDB(Player player, String[] split) {
		String itemName = "";
		int amount = 0;
		
		for (String iter : split) {
			if (!iter.equalsIgnoreCase(split[1])) {
				try {
					amount = Integer.valueOf(iter);
				}
				catch (Exception e) {
					itemName += " " + iter;
				}
			}
		}
		
		itemName = itemName.trim();
		
		int shopId = Shops.getId(playerShops.get(player.getName()));
		int itemId = Items.getId(itemName);
		
		int shopItemID = ShopItems.getShopItemId(shopId, itemId);
		try {
			if (shopItemID != 0) {
				com.bukkit.Vandolis.CodeRedEconomy.Database.TransactionManager.add(new ShopTransactions(player, playerShops.get(player
						.getName()), itemName, amount, false));
			}
			else {
				throw new EconException("Invalid item.", "");
			}
		}
		catch (EconException e) {
			player.sendMessage(EconomyProperties.getPluginMessage() + e.getBuyMsg());
		}
	}
	
	private static void payDB(Player player, String[] split) {
		try {
			int amount = Integer.valueOf(split[2]);
			
			if (amount > 0) {
				com.bukkit.Vandolis.CodeRedEconomy.Database.TransactionManager.add(new PlayerTransactions(player, CodeRedEconomy
						.getPlayer(split[1]), amount));
			}
			else {
				player.sendMessage(EconomyProperties.getPluginMessage() + "Please enter a valid amount.");
			}
		}
		catch (NumberFormatException e) {
			player.sendMessage(EconomyProperties.getPluginMessage() + "Please enter a valid amount.");
		}
	}
	
	/**
	 * Function ran when a player tries to buy an item. Parses the command used to get the item name as well as the amount. If no amount is
	 * given the default value is 1.
	 * 
	 * @param user
	 * @param split
	 */
	private static void buyFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		String itemName = "";
		int amount = 1;
		
		/*
		 * Check the command length to make sure it is at least 2. [0] = /buy [1] = itemName
		 */
		if (split.length >= 2) {
			try {
				/*
				 * Loop through the split and grab the itemName as well as the amount.
				 */
				for (String iter : split) {
					/*
					 * Skip the first value as it is the /buy
					 */
					if (!iter.equalsIgnoreCase(split[0])) {
						/*
						 * Try and convert into the amount, if that fails it must be part of the name.
						 */
						try {
							amount = Integer.valueOf(iter);
						}
						catch (NumberFormatException e) {
							/*
							 * Not a number, add to name.
							 */
							itemName += iter + " ";
						}
					}
				}
				
				/*
				 * Trim the name for use and show debug info.
				 */
				itemName = itemName.trim();
				
				if (DataManager.getDebug()) {
					System.out.println("Item name is: " + itemName);
				}
				
				/*
				 * Check to make sure the amount is nonnegative as well as the itemName has a value.
				 */
				if ((amount > 0) && !itemName.equalsIgnoreCase("")) {
					if (DataManager.getDebug()) {
						System.out.println("Valid, processing.");
					}
					
					ShopItemStack stack = new ShopItemStack(ShopItem.getId(itemName), amount);
					
					com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Transaction.process(new Transaction(DataManager.getShop(playerShops
							.get(player.getName())), user, stack));
				}
				else {
					user.sendMessage("Please enter a valid amount and/or item name.");
				}
			}
			catch (Exception e2) {
				user.sendMessage("The correct use is /buy [item name] [amount]");
			}
		}
		else {
			user.sendMessage("The correct use is /buy [item name] [amount]");
		}
	}
	
	/**
	 * Function ran when a player tries to sell an item. Parses the command used to get the item name as well as the amount. If no amount is
	 * given the default value is 1.
	 * 
	 * @param player
	 * @param split
	 */
	private static void sellFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		String itemName = "";
		int amount = 1;
		
		/*
		 * Check the command length to make sure it is at least 2. [0] = /sell [1] = itemName
		 */
		if (split.length >= 2) {
			try {
				/*
				 * Loop through the split and grab the itemName as well as the amount.
				 */
				for (String iter : split) {
					/*
					 * Skip the first one as it is /sell
					 */
					if (!iter.equalsIgnoreCase(split[0])) {
						/*
						 * Try and convert it into the amount, if that fails it must be part of the name
						 */
						try {
							amount = Integer.valueOf(iter);
						}
						catch (NumberFormatException e) {
							/*
							 * Not a number, add to the name.
							 */
							itemName += iter + " ";
						}
					}
				}
				
				/*
				 * Trim the name for use and print debug info
				 */
				itemName = itemName.trim();
				if (DataManager.getDebug()) {
					System.out.println("Item name is: " + itemName);
				}
				
				/*
				 * Check to make sure the amount is nonnegative and the itemName is not empty.
				 */
				if ((amount > 0) && !itemName.equalsIgnoreCase("")) {
					/*
					 * Process a new transaction with the parsed data
					 */
					try {
						Transaction.process(new Transaction(user, DataManager.getShop(playerShops.get(player.getName())),
								new ShopItemStack(DataManager.getItemId(itemName), amount)));
					}
					catch (NullPointerException e1) {
						user.sendMessage(itemName + " cannot be sold.");
					}
				}
				else {
					user.sendMessage("Please enter a valid amount and/or item name.");
				}
			}
			catch (NumberFormatException e1) {
				user.sendMessage("The correct use is /sell [item name] [amount]");
			}
		}
		else {
			user.sendMessage("The correct use is /sell [item name] [amount]");
		}
	}
	
	private static void payFlatfile(Player player, String[] split) {
		User user = DataManager.getUser(player);
		
		if (split.length >= 3) {
			Player pTarget = CodeRedEconomy.getEconServer().getPlayer(split[1].trim());
			if (pTarget != null) {
				User target = DataManager.getUser(pTarget);
				
				try {
					int amount = 0;
					amount = Integer.valueOf(split[2]);
					Money money = new Money(amount);
					
					if (amount > 0) {
						/*
						 * The way it is formatted the person receiving money is the seller, and the person loosing money is the buyer,
						 * so this is formatted target, user so the money goes from the user to the target
						 */
						Transaction.process(new Transaction(target, user, money));
					}
					else {
						user.sendMessage(money + " is not a valid amount.");
					}
				}
				catch (NumberFormatException e) {
					user.sendMessage("The correct use is /pay [name] [amount]");
				}
			}
			else {
				user.sendMessage("Player not online.");
			}
		}
		else {
			user.sendMessage("Correct use is /pay [player] [amount]");
		}
	}
	
	/**
	 * @param codeRedEconomy
	 */
	public static void setPlugin(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
}
