/**
 * 
 */
package com.Vandolis.TheWall;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import org.bukkit.Server;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.Vandolis.CodeRedEconomy.CodeRedEconomy;
import com.bukkit.Vandolis.CodeRedEconomy.DataManager;

/**
 * @author Vandolis
 */
public class TheWall extends JavaPlugin {
	private final TheWallPlayerListener	playerListener	= new TheWallPlayerListener(this);
	private final TheWallBlockListener	blockListener	= new TheWallBlockListener(this);
	private Wall						wall			= null;
	private CodeRedEconomy				econ			= null;
	
	/**
	 * @param pluginLoader
	 * @param instance
	 * @param desc
	 * @param folder
	 * @param plugin
	 * @param cLoader
	 */
	public TheWall(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
		super(pluginLoader, instance, desc, folder, plugin, cLoader);
		load();
	}
	
	/**
	 * 
	 */
	private void load() {
		setEcon((CodeRedEconomy) getServer().getPluginManager().getPlugin("CodeRedEconomy"));
		
		String raw = "";
		ArrayList<String> loadData = new ArrayList<String>();
		
		try {
			getEcon().getDataManager();
			BufferedReader reader = new BufferedReader(new FileReader(new File(DataManager.getDataLoc() + "TheWall.txt")));
			while ((raw = reader.readLine()) != null) {
				if (!raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
					loadData.add(raw);
				}
			}
			reader.close();
			if (loadData.size() != 0) {
				setWall(new Wall(this, loadData));
			}
		}
		catch (FileNotFoundException e1) {
			/*
			 * File does not exist yet, make it
			 */
			BufferedWriter writer;
			try {
				getEcon().getDataManager();
				writer = new BufferedWriter(new FileWriter(new File(DataManager.getDataLoc() + "TheWall.txt")));
				writer.close();
			}
			catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		catch (Exception e2) {
			e2.printStackTrace();
		}
		
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onDisable()
	 */
	@Override
	public void onDisable() {
		System.out.println("TheWall has been disabled.");
	}
	
	/* (non-Javadoc)
	 * @see org.bukkit.plugin.Plugin#onEnable()
	 */
	@Override
	public void onEnable() {
		PluginManager pm = getServer().getPluginManager();
		
		pm.registerEvent(Type.PLAYER_COMMAND, playerListener, Priority.Normal, this);
		pm.registerEvent(Type.BLOCK_RIGHTCLICKED, blockListener, Priority.Normal, this);
		
		PluginDescriptionFile pdfFile = getDescription();
		System.out.println(pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!");
		if (getEcon() == null) {
			System.out.println("TheWall dependancy missing: CodeRedEconomy...\nClosing");
			setEnabled(false);
		}
	}
	
	/**
	 * @return
	 */
	public Wall getWall() {
		/*Gets the wall from save data*/
		return wall;
	}
	
	/**
	 * @param econ
	 *            the econ to set
	 */
	public void setEcon(CodeRedEconomy econ) {
		this.econ = econ;
	}
	
	/**
	 * @return the econ
	 */
	public CodeRedEconomy getEcon() {
		return econ;
	}
	
	/**
	 * @param wall2
	 */
	public void setWall(Wall wall) {
		this.wall = wall;
		
		System.out.println("Setting the wall to: " + wall);
		
		DataManager.addShop(wall);
	}
	
}
