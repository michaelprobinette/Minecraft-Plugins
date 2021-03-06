/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.EconEntity;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.Money;
import com.bukkit.Vandolis.CodeRedEconomy.FlatFile.ShopItemStack;

/**
 * Custom exception class for the economy. Contains messages for the buyer and seller, as well as an allowed stack size if needed
 * 
 * @author Vandolis
 */
public class EconException extends Exception {
	private static final long	serialVersionUID	= 1L;
	private String				buyMsg				= "";
	private String				sellMsg				= "";
	private ShopItemStack		stack				= null;
	private Money				amount				= null;
	
	/**
	 * Creates a {@link EconException} with the given messages.
	 * 
	 * @param buyMsg
	 * @param sellMsg
	 */
	public EconException(String buyMsg, String sellMsg) {
		this.buyMsg = buyMsg;
		this.sellMsg = sellMsg;
	}
	
	/**
	 * Creates a {@link EconException} with the given messages and the allowed stack to buy/sell.
	 * 
	 * @param buyMsg
	 * @param sellMsg
	 * @param stack
	 */
	public EconException(String buyMsg, String sellMsg, ShopItemStack stack) {
		this.buyMsg = buyMsg;
		this.sellMsg = sellMsg;
		this.stack = stack;
	}
	
	/**
	 * @param buyMsg
	 * @param sellMsg
	 * @param money
	 */
	public EconException(String buyMsg, String sellMsg, Money money) {
		this.buyMsg = buyMsg;
		this.sellMsg = sellMsg;
		amount = money;
	}
	
	/**
	 * @return The stack of item an {@link EconEntity} can buy/sell without the offending problem
	 */
	public ShopItemStack getStack() {
		return stack;
	}
	
	/**
	 * @return Message to send to the buyer
	 */
	public String getBuyMsg() {
		return buyMsg;
	}
	
	/**
	 * @return Message to send to the seller
	 */
	public String getSellMsg() {
		return sellMsg;
	}
	
	/**
	 * @return amount of money for new transaction
	 */
	public Money getAmount() {
		return amount;
	}
}
