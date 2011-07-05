/**
 *
 */
package com.Vandolis.CodeRedLite.Runnable;

import com.Vandolis.CodeRedLite.CodeRedLite;
import com.Vandolis.CodeRedLite.EconPlayer;

/**
 * @author Vandolis
 */
public class AutoSave implements Runnable
{
	private CodeRedLite	plugin	= null;
	
	/**
	 * Default ctor, sets the plugin to the given instance
	 * 
	 * @param codeRed
	 */
	public AutoSave(CodeRedLite codeRed)
	{
		plugin = codeRed;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		plugin.getLog().info("CodeRedLite is saving data...");
		
		for (EconPlayer econPlayer : plugin.getLoadedPlayers())
		{
			econPlayer.update();
		}
		
		plugin.getShop().softUpdate();
	}
}
