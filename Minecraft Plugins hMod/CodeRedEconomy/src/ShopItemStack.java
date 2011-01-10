/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

public class ShopItemStack {
	private int			itemID			= 0;
	private int			amountAvail		= 0;
	private ShopItem	shopItem		= null;
	private Money		totalBuyPrice	= new Money();
	private Money		totalSellPrice	= new Money();
	
	public ShopItemStack(ShopItem shopItem, int amountAvail) {
		if (shopItem == null) {
			itemID = 0;
			this.shopItem = new ShopItem();
			this.amountAvail = 0;
			this.totalBuyPrice.setAmount(0);
			this.totalSellPrice.setAmount(0);
		}
		else {
			itemID = shopItem.getItemID();
			this.shopItem = shopItem;
			this.amountAvail = amountAvail;
			this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
			this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
		}
	}
	
	public int getItemID() {
		return itemID;
	}
	
	public int getAmountAvail() {
		return amountAvail;
	}
	
	public ShopItem getShopItem() {
		return shopItem;
	}
	
	public Money getTotalBuyPrice() {
		return totalBuyPrice;
	}
	
	public Money getTotalSellPrice() {
		return totalSellPrice;
	}
	
	public void addAmountAvail(int amount) {
		amountAvail += amount;
		this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
		this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
	}
	
	public void setAmountAvail(int amount) {
		amountAvail = amount;
		this.totalBuyPrice.setAmount(shopItem.getBuyPrice() * amountAvail);
		this.totalSellPrice.setAmount(shopItem.getSellPrice() * amountAvail);
	}
}
