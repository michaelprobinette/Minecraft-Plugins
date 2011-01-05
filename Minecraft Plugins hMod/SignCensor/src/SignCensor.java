/*
 * Minecraft plugin that allows signs to be changed or destoryed if they contain a given keyword. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

import java.util.logging.Logger;

/**
 * @author Vandolis
 */
public class SignCensor extends Plugin {
	private Listener				l			= new Listener(this);
	protected static final Logger	log			= Logger.getLogger("Minecraft");
	private String					name		= "SignCensor";
	private String					version		= "v1.0.1";
	private PropertiesFile			props		= new PropertiesFile("SignCensor.properties");
	private String[]				bannedWords	= null;
	private String					replaceWith	= "";
	private boolean					destroy		= false;
	
	public void enable() {
		if (!props.containsKey("bannedwords")) {
			props.setString("bannedwords", "");
		}
		if (!props.containsKey("destroy")) {
			props.setBoolean("destroy", true);
		}
		if (!props.containsKey("replacewith")) {
			props.setString("replacewith", "");
		}
		replaceWith = props.getString("replacewith");
		destroy = props.getBoolean("destroy");
		bannedWords = props.getString("bannedwords").split(",");
		for (int i = 0; i < bannedWords.length; i++) {
			bannedWords[i] = bannedWords[i].trim();
		}
	}
	
	public void disable() {
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		etc.getLoader().addListener(PluginLoader.Hook.SIGN_CHANGE, l, this, PluginListener.Priority.MEDIUM);
	}
	
	// Sends a message to all players!
	public void broadcast(String message) {
		for (Player p : etc.getServer().getPlayerList()) {
			p.sendMessage(message);
		}
	}
	
	public class Listener extends PluginListener {
		SignCensor	p;
		
		// This controls the accessability of functions / variables from the main class.
		public Listener(SignCensor plugin) {
			p = plugin;
		}
		
		public boolean onSignChange(Player player, Sign s) {
			// Get lines of the sign
			String line1 = s.getText(0).toLowerCase(), line2 = s.getText(1).toLowerCase(), line3 = s.getText(2).toLowerCase(), line4 = s
					.getText(3).toLowerCase();
			for (String temp : bannedWords) {
				temp = temp.toLowerCase();
				if (line1.contains(temp)) {
					if (destroy) {
						etc.getServer().setBlockAt(0, s.getX(), s.getY(), s.getZ());
					}
					else {
						line1 = line1.replace(temp, replaceWith);
						s.setText(0, line1);
						s.update();
					}
				}
				if (line2.contains(temp)) {
					if (destroy) {
						etc.getServer().setBlockAt(0, s.getX(), s.getY(), s.getZ());
					}
					else {
						line2 = line2.replace(temp, replaceWith);
						s.setText(1, line2);
						s.update();
					}
				}
				if (line3.contains(temp)) {
					if (destroy) {
						etc.getServer().setBlockAt(0, s.getX(), s.getY(), s.getZ());
					}
					else {
						line3 = line3.replace(temp, replaceWith);
						s.setText(2, line3);
						s.update();
					}
				}
				if (line4.contains(temp)) {
					if (destroy) {
						etc.getServer().setBlockAt(0, s.getX(), s.getY(), s.getZ());
					}
					else {
						line4 = line4.replace(temp, replaceWith);
						s.setText(3, line4);
						s.update();
					}
				}
			}
			return false;
		}
	}
}
