/**
 *
 */
package com.Vandolis.CodeRedLite.Runnable;

import com.Vandolis.CodeRedLite.CodeRedLite;

/**
 * @author Vandolis
 */
public class AutoRestock implements Runnable
{
	private CodeRedLite	plugin	= null;
	
	/**
	 * @param codeRedLite
	 */
	public AutoRestock(CodeRedLite codeRedLite)
	{
		plugin = codeRedLite;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run()
	{
		// TODO: Shop restocking of non infinite items
	}
	
}
