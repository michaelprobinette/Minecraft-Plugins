/**
 *
 */
package com.Vandolis.CommandOn;

import java.util.ArrayList;

/**
 * @author Vandolis
 */
public class COCmd
{
	private String				command	= "";
	private ArrayList<String>	params	= new ArrayList<String>();
	
	public COCmd(String command, ArrayList<String> params)
	{
		this.command = command;
		this.params = new ArrayList<String>(params);
	}
	
	/**
	 * @return the command
	 */
	public String getCommand()
	{
		return command;
	}
	
	/**
	 * @return the params
	 */
	public ArrayList<String> getParams()
	{
		return params;
	}
}
