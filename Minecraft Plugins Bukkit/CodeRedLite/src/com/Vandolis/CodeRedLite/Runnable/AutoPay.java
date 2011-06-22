/**
 * 
 */
package com.Vandolis.CodeRedLite.Runnable;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class AutoPay implements Runnable {
	private CodeRedLite	plugin	= null;
	
	public AutoPay(CodeRedLite codeRed) {
		plugin = codeRed;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		int payAmount = plugin.getProperties().getAutoPayAmount();
		String moneyName = plugin.getProperties().getMoneyName();
		
		for (EconPlayer econPlayer : plugin.getPlayers()) {
			econPlayer.getPlayer().sendMessage(plugin.getPluginMessage() + "You were auto paid " + payAmount + " " + moneyName);
			econPlayer.addMoney(payAmount);
		}
	}
}
