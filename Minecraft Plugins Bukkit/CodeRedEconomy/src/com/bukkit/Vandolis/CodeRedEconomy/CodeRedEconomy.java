/*
 * Economy made for the Redstrype Minecraft Server. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute
 * it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of
 * the License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin file, registers {@link Event} and loads {@link DataManager}.
 * 
 * @author Vandolis
 */
public class CodeRedEconomy extends JavaPlugin {
	private final CodeRedPlayerListener	playerListener	= new CodeRedPlayerListener(this);
	private final CodeRedBlockListener	blockListener	= new CodeRedBlockListener(this);
	private final DataManager			dataMan			= new DataManager(this);
	private ArrayList<String>			ops				= new ArrayList<String>();
	
	public CodeRedEconomy(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin,
			ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		
		loadOps();
	}
	
	/**
	 * 
	 */
	private void loadOps() {
		try {
			BufferedReader reader = new BufferedReader(new FileReader(new File("ops.txt")));
			
			String raw = "";
			
			while ((raw = reader.readLine()) != null) {
				if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
					ops.add(raw);
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onDisable() {
		
	}
	
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_DAMAGED, blockListener, Priority.Normal, this);
		
		// Load the datamanager
		//		DataManager.load(this);
		
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
	}
	
	/**
	 * @param name
	 * @return
	 */
	public boolean isOp(String name) {
		for (String iter : ops) {
			if (iter.equalsIgnoreCase(name)) {
				return true;
			}
		}
		
		return false;
	}
	
	public DataManager getDataManager() {
		return dataMan;
	}
}
