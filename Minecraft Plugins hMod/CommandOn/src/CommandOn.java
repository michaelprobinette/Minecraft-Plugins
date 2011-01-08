/*
 * Minecraft plugin that allows commands to be ran on events. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;

/*
 * 12/01/2010 Mind the idiot style of programming, as this is what a program that is not planned out looks like. Pretty much all of it was
 * added on as I went, so it is NOT the best solution and I plan on rewritting it for Bukkit. If this code helps you in any way be sure to
 * let me know!
 */

/**
 * @author Vandolis
 */
public class CommandOn extends Plugin {
	private Listener					l					= new Listener(this);
	protected static final Logger		log					= Logger.getLogger("Minecraft");
	private static final String			name				= "CommandOn";
	private static final String			version				= "v1.1.7";
	private static final String[]		COMMAND_TEMPLATE	= {
			"## OnLogin", "default:", "## OnLogout", "default:", "## OnDeath", "default:", "## OnRespawn", "default:"
															};
	private static final String[]		PLAYERS_TEMPLATE	= {
			"## PlayerName", "OnLogin:", "OnLogout:", "OnDeath:", "OnRespawn:"
															};
	private static final String			TAGS[]				= {
			"OnLogin", "OnLogout", "OnDeath", "OnDeathFall", "OnDeathCactus", "OnDeathWater", "OnDeathMob", "OnDeathFire", "OnDeathLava",
			"OnDeathCreeper", "OnDeathExplosion", "OnRespawn", "OnServerStart", "OnPvPKill", "OnPvPDeath", "OnPvPEnd", "OnPvPStart",
			"OnDeathZombie", "OnDeathSpider", "OnDeathSkeleton", "OnDeathPigZombie", "OnDeathGhast", "OnDeathSlime", "OnDeathOther",
			"OnDeathSuicide"
															};
	private static final String			SECTIONS[]			= {
			"Group", "Name", "Default", "All", "NoGroup"
															};
	private static final String			VARS[]				= {
			"[NAME]", "[PLAYERX]", "[PLAYERY]", "[PLAYERZ]", "[ATTACKER]", "[DEFENDER]", "[WINNER]", "[LOSER]", "[CAUSE]"
															};
	
	private static PropertiesFile		props				= new PropertiesFile("CommandOn.properties");
	private static File					command_file		= new File("CommandOn.txt");
	private static File					player_file			= new File("CommandOnPlayers.txt");
	private static int					gCdim[]				= {
			1, 1, 1
															};
	private static String				gC[][][]			= new String[gCdim[0]][gCdim[1]][gCdim[2]];
	private static int					pIdim[]				= {
			1, 1, 1
															};
	private static String				pI[][][]			= new String[pIdim[0]][pIdim[1]][pIdim[2]];
	private static boolean				debug				= false;
	private static String				editListGroups[]	= null;
	private static String				editIgnoreGroups[]	= null;
	private static String[]				startCommandQue		= null;
	private static ArrayList<Death>		deaths				= new ArrayList<Death>();
	
	// Hacked vars
	private static String				login				= "";
	private static ArrayList<Battle>	currentBattles		= new ArrayList<Battle>();
	
	public void enable() {
		etc.getInstance().addCommand("/co", "- CommandOn");
	}
	
	public void disable() {
		etc.getInstance().removeCommand("/co");
	}
	
	public void initialize() {
		log.info(name + " " + version + " initialized");
		etc.getLoader().addListener(PluginLoader.Hook.LOGIN, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.HEALTH_CHANGE, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.DISCONNECT, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.COMMAND, l, this, PluginListener.Priority.MEDIUM);
		etc.getLoader().addListener(PluginLoader.Hook.DAMAGE, l, this, PluginListener.Priority.MEDIUM);
		readFiles();
		
		checkCommands(new Player(), "onserverstart", "", null);
	}
	
	public void readFiles() {
		try {
			gC = new String[gCdim[0]][gCdim[1]][gCdim[2]];
			pI = new String[pIdim[0]][pIdim[1]][pIdim[2]];
			BufferedReader command_reader = null;
			BufferedReader player_reader = null;
			BufferedWriter command_writer = null;
			BufferedWriter player_writer = null;
			if (!command_file.exists()) {
				command_file = new File("/Plugins/CommandOn.txt");
				if (!command_file.exists()) {
					log.log(Level.INFO, "No CommandOn.txt found, creating file.");
					command_file = new File("CommandOn.txt");
					command_writer = new BufferedWriter(new FileWriter(command_file));
					for (int wl = 0; wl < COMMAND_TEMPLATE.length; wl++) {
						command_writer.write(COMMAND_TEMPLATE[wl]);
						command_writer.newLine();
					}
					command_writer.close();
				}
			}
			if (!player_file.exists()) {
				player_file = new File("/Plugins/CommandOnPlayers.txt");
				if (!player_file.exists()) {
					log.log(Level.INFO, "No CommandOnPlayers.txt found, creating file.");
					player_file = new File("CommandOnPlayers.txt");
					player_writer = new BufferedWriter(new FileWriter(player_file));
					for (int wl = 0; wl < PLAYERS_TEMPLATE.length; wl++) {
						player_writer.write(PLAYERS_TEMPLATE[wl]);
						player_writer.newLine();
					}
					player_writer.close();
				}
			}
			
			player_reader = new BufferedReader(new FileReader(player_file));
			command_reader = new BufferedReader(new FileReader(command_file));
			String raw = "";
			int count = -1;
			int count2 = 0;
			while ((raw = command_reader.readLine()) != null && !raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
				if (raw != null) {
					// log.log(Level.INFO, raw);
					if (raw.contains("##")) {
						String temp[] = raw.split(" ");
						if (temp.length >= 2) {
							if (insideArr(TAGS, temp[1])) {
								count++;
								if (count == gC.length) {
									gCdim[0]++;
								}
								String tgC[][][] = new String[gCdim[0]][gCdim[1]][gCdim[2]];
								copyArray(gC, tgC);
								gC = tgC;
								gC[count][0][0] = temp[1];
								count2 = 0;
							}
						}
					}
					else if (count2 != -1) {
						String split[] = raw.split(":");
						count2++;
						if (count2 >= gC[count].length) {
							gCdim[1]++;
						}
						while (gCdim[2] < split.length) {
							gCdim[2]++;
						}
						String tgC[][][] = new String[gCdim[0]][gCdim[1]][gCdim[2]];
						copyArray(gC, tgC);
						for (int a = 0; a < split.length; a++) {
							tgC[count][count2][a] = split[a];
						}
						gC = tgC;
					}
				}
			}
			command_reader.close();
			count = -1;
			count2 = 0;
			while ((raw = player_reader.readLine()) != null && !raw.equalsIgnoreCase("") && !raw.equalsIgnoreCase(" ")) {
				if (raw != null) {
					// log.log(Level.INFO, raw);
					if (raw.contains("##")) {
						String temp[] = raw.split(" ");
						if (temp.length >= 2) {
							count++;
							if (count == pI.length) {
								pIdim[0]++;
							}
							String tpI[][][] = new String[pIdim[0]][pIdim[1]][pIdim[2]];
							copyArray(pI, tpI);
							pI = tpI;
							pI[count][0][0] = temp[1];
							count2 = 0;
						}
					}
					else if (count2 != -1) {
						String split[] = raw.split(":");
						count2++;
						if (count2 >= pI[count].length) {
							pIdim[1]++;
						}
						while (pIdim[2] < split.length) {
							pIdim[2]++;
						}
						String tpI[][][] = new String[pIdim[0]][pIdim[1]][pIdim[2]];
						copyArray(pI, tpI);
						for (int a = 0; a < split.length; a++) {
							tpI[count][count2][a] = split[a];
						}
						pI = tpI;
					}
				}
			}
			player_reader.close();
			
			removeNull(gC);
			removeNull(pI);
			
			// Read from properties file
			if (!props.containsKey("editlistgroups")) {
				props.setString("editlistgroups", "admins");
				props.save();
			}
			if (!props.containsKey("editignoregroups")) {
				props.setString("editignoregroups", "all");
				props.save();
			}
			/*
			 * if (!props.containsKey("debug")) { props.setBoolean("debug", false); props.setString("destroygroups", "admins");
			 * props.save(); }
			 */
			editListGroups = props.getString("editlistgroups").split(",");
			editIgnoreGroups = props.getString("editignoregroups").split(",");
			debug = props.getBoolean("debug");
			
			// Trim data
			for (int x = 0; x < editListGroups.length; x++) {
				editListGroups[x] = editListGroups[x].trim();
			}
			for (int x = 0; x < editIgnoreGroups.length; x++) {
				editIgnoreGroups[x] = editIgnoreGroups[x].trim();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void runServerCommands(String[] commands) {
		for (int x = 0; x < commands.length; x++) {
			if (commands[x].length() >= 1) {
				if (commands[x].charAt(0) == '#') {
					String serverCommand = commands[x];
					serverCommand = serverCommand.replaceFirst("#", "");
					if (!serverCommand.equalsIgnoreCase("")) {
						log.log(Level.INFO, "CommandOn running server start command: " + serverCommand);
						etc.getServer().useConsoleCommand(serverCommand);
					}
				}
				else {
					// log.log(Level.INFO, "Adding " + commands[x] + " to the que.");
					// Add to que
					if (startCommandQue != null) {
						String temp[] = new String[startCommandQue.length + 1];
						System.arraycopy(startCommandQue, 0, temp, 0, startCommandQue.length);
						temp[temp.length - 1] = commands[x];
						startCommandQue = temp;
					}
					else {
						startCommandQue = new String[1];
						startCommandQue[0] = commands[x];
					}
				}
			}
		}
	}
	
	public void copyArray(final String[][][] src, String[][][] dest) {
		for (int x = 0; x < src.length; x++) {
			for (int y = 0; y < src[x].length; y++) {
				System.arraycopy(src[x][y], 0, dest[x][y], 0, src[x][y].length);
			}
		}
	}
	
	public void printArray(final String[][][] arr) {
		log.log(Level.INFO, "Dims x: " + arr.length + " y:" + arr[0].length + " z:" + arr[0][0].length);
		String message = "";
		for (int x = 0; x < arr.length; x++) {
			if (arr[x][0][0] != "") {
				message += arr[x][0][0] + " - ";
			}
			for (int y = 0; y < arr[x].length; y++) {
				if ((arr[x][y][0] != "") && (!arr[x][y][0].equalsIgnoreCase(arr[x][0][0]))) {
					message += arr[x][y][0] + " - ";
				}
				for (int z = 0; z < arr[x][y].length; z++) {
					if ((arr[x][y][z] != "") && (!arr[x][y][z].equalsIgnoreCase(arr[x][y][0]))) {
						log.log(Level.INFO, message + arr[x][y][z]);
					}
					else {
						log.log(Level.INFO, message);
					}
				}
				message = arr[x][0][0] + " - ";
			}
			message = "";
		}
	}
	
	public void printArray(final String[] arr) {
		for (int x = 0; x < arr.length; x++) {
			log.log(Level.INFO, "String at x: " + x + " " + arr[x]);
		}
	}
	
	public void removeNull(String[][][] arr) {
		for (int x = 0; x < arr.length; x++) {
			for (int y = 0; y < arr[x].length; y++) {
				for (int z = 0; z < arr[x][y].length; z++) {
					if (arr[x][y][z] == null) {
						arr[x][y][z] = "";
					}
				}
			}
		}
	}
	
	public boolean insideArr(final String[] arr, final String s) {
		for (int x = 0; x < arr.length; x++) {
			if (s.equalsIgnoreCase(arr[x])) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canEditIgnore(Player player) {
		if (isAdmin(player)) {
			return true;
		}
		for (int x = 0; x < editIgnoreGroups.length; x++) {
			if (player.isInGroup(editIgnoreGroups[x])) {
				return true;
			}
			else if (editIgnoreGroups[x].equalsIgnoreCase("all")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean canEditList(Player player) {
		if (isAdmin(player)) {
			return true;
		}
		for (int x = 0; x < editListGroups.length; x++) {
			if (player.isInGroup(editListGroups[x])) {
				return true;
			}
			else if (editListGroups[x].equalsIgnoreCase("all")) {
				return true;
			}
		}
		return false;
	}
	
	public boolean isAdmin(Player player) {
		if (!player.hasNoGroups()) {
			String groups[] = player.getGroups();
			for (int x = 0; x < groups.length; x++) {
				if (groups[x].contains("admin")) {
					return true;
				}
			}
		}
		if (player.isAdmin()) {
			return true;
		}
		return false;
	}
	
	public void broadcast(final String[] tempMessage, final Player player, Battle batt) {
		String message = "";
		message = "";
		for (int m = 1; m < tempMessage.length; m++) {
			message += tempMessage[m];
			if ((m + 1) < tempMessage.length) {
				message += " ";
			}
		}
		// Insert variables
		message = message.replace("[NAME]", player.getName());
		message = message.replace("[PLAYERX]", Double.toString(player.getX()));
		message = message.replace("[PLAYERY]", Double.toString(player.getY()));
		message = message.replace("[PLAYERZ]", Double.toString(player.getZ()));
		if (batt != null) {
			message = message.replace("[ATTACKER]", batt.getAtt());
			message = message.replace("[DEFENDER]", batt.getDef());
			message = message.replace("[WINNER]", batt.getWin());
			message = message.replace("[LOSER]", batt.getLooser());
			message = message.replace("[CAUSE]", batt.getCause());
		}
		if (tempMessage[0].equalsIgnoreCase("%BLACK")) {
			etc.getServer().messageAll(Colors.Black + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%BLUE")) {
			etc.getServer().messageAll(Colors.Blue + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%DARKPURPLE")) {
			etc.getServer().messageAll(Colors.DarkPurple + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%GOLD")) {
			etc.getServer().messageAll(Colors.Gold + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%GRAY")) {
			etc.getServer().messageAll(Colors.Gray + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%GREEN")) {
			etc.getServer().messageAll(Colors.Green + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%LIGHTBLUE")) {
			etc.getServer().messageAll(Colors.LightBlue + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%LIGHTGRAY")) {
			etc.getServer().messageAll(Colors.LightGray + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%LIGHTGREEN")) {
			etc.getServer().messageAll(Colors.LightGreen + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%LIGHTPURPLE")) {
			etc.getServer().messageAll(Colors.LightPurple + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%NAVY")) {
			etc.getServer().messageAll(Colors.Navy + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%PURPLE")) {
			etc.getServer().messageAll(Colors.Purple + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%RED")) {
			etc.getServer().messageAll(Colors.Red + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%ROSE")) {
			etc.getServer().messageAll(Colors.Rose + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%WHITE")) {
			etc.getServer().messageAll(Colors.White + message);
		}
		else if (tempMessage[0].equalsIgnoreCase("%YELLOW")) {
			etc.getServer().messageAll(Colors.Yellow + message);
		}
		else {
			message = "";
			for (int m = 0; m < tempMessage.length; m++) {
				message += tempMessage[m];
				if ((m + 1) < tempMessage.length) {
					message += " ";
				}
			}
			// Insert variables
			message = message.replace("[NAME]", player.getName());
			message = message.replace("[PLAYERX]", Double.toString(player.getX()));
			message = message.replace("[PLAYERY]", Double.toString(player.getY()));
			message = message.replace("[PLAYERZ]", Double.toString(player.getZ()));
			if (batt != null) {
				message = message.replace("[ATTACKER]", batt.getAtt());
				message = message.replace("[DEFENDER]", batt.getDef());
				message = message.replace("[WINNER]", batt.getWin());
				message = message.replace("[LOSER]", batt.getLooser());
				message = message.replace("[CAUSE]", batt.getCause());
			}
			message = message.replaceFirst("%", ""); // Removes the % from the
			// beginning of the
			// string
			etc.getServer().messageAll(message);
		}
	}
	
	public void runCommands(Player player, final String[] commandList, Battle batt) {
		for (int x = 1; x < commandList.length; x++) {
			String playersCommands[] = player.getCommands();
			boolean couldBuild = player.canBuild();
			String tempCommand[] = null;
			tempCommand = commandList[x].split(" ");
			if ((tempCommand.length >= 1) && (tempCommand[0] != "")) {
				if ((tempCommand.length > 1) && !(tempCommand[0].charAt(0) == '@') && !(tempCommand[0].charAt(0) == '%')) {
					player.setCommands(new String[] {
						tempCommand[0]
					});
					String command = "";
					for (int e = 0; e < tempCommand.length; e++) {
						command += tempCommand[e];
						if ((e + 1) < tempCommand.length) {
							command += " ";
						}
					}
					// Insert variables
					command = command.replace("[NAME]", player.getName());
					command = command.replace("[PLAYERX]", Double.toString(player.getX()));
					command = command.replace("[PLAYERY]", Double.toString(player.getY()));
					command = command.replace("[PLAYERZ]", Double.toString(player.getZ()));
					if (batt != null) {
						command = command.replace("[ATTACKER]", batt.getAtt());
						command = command.replace("[DEFENDER]", batt.getDef());
						command = command.replace("[WINNER]", batt.getWin());
						command = command.replace("[LOSER]", batt.getLooser());
						command = command.replace("[CAUSE]", batt.getCause());
					}
					// log.log(Level.INFO, "Running: " + command);
					player.command(command);
				}
				else if (tempCommand[0].charAt(0) == '#') {
					String serverCommand = "";
					for (int loop = 0; loop < tempCommand.length; loop++) {
						serverCommand += tempCommand[loop] + " ";
					}
					serverCommand = serverCommand.replaceFirst("#", "");
					if (!serverCommand.equalsIgnoreCase("")) {
						log.log(Level.INFO, "CommandOn running server start command: " + serverCommand);
						etc.getServer().useConsoleCommand(serverCommand);
					}
				}
				else if (tempCommand[0].charAt(0) == '@') {
					String message = "";
					for (int m = 1; m < tempCommand.length; m++) {
						message += tempCommand[m];
						if ((m + 1) < tempCommand.length) {
							message += " ";
						}
					}
					// Insert variables
					message = message.replace("[NAME]", player.getName());
					message = message.replace("[PLAYERX]", Double.toString(player.getX()));
					message = message.replace("[PLAYERY]", Double.toString(player.getY()));
					message = message.replace("[PLAYERZ]", Double.toString(player.getZ()));
					if (batt != null) {
						message = message.replace("[ATTACKER]", batt.getAtt());
						message = message.replace("[DEFENDER]", batt.getDef());
						message = message.replace("[WINNER]", batt.getWin());
						message = message.replace("[LOSER]", batt.getLooser());
						message = message.replace("[CAUSE]", batt.getCause());
					}
					if (tempCommand[0].equalsIgnoreCase("@BLACK")) {
						player.sendMessage(Colors.Black + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@BLUE")) {
						player.sendMessage(Colors.Blue + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@DARKPURPLE")) {
						player.sendMessage(Colors.DarkPurple + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@GOLD")) {
						player.sendMessage(Colors.Gold + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@GRAY")) {
						player.sendMessage(Colors.Gray + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@GREEN")) {
						player.sendMessage(Colors.Green + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@LIGHTBLUE")) {
						player.sendMessage(Colors.LightBlue + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@LIGHTGRAY")) {
						player.sendMessage(Colors.LightGray + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@LIGHTGREEN")) {
						player.sendMessage(Colors.LightGreen + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@LIGHTPURPLE")) {
						player.sendMessage(Colors.LightPurple + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@NAVY")) {
						player.sendMessage(Colors.Navy + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@PURPLE")) {
						player.sendMessage(Colors.Purple + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@RED")) {
						player.sendMessage(Colors.Red + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@ROSE")) {
						player.sendMessage(Colors.Rose + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@WHITE")) {
						player.sendMessage(Colors.White + message);
					}
					else if (tempCommand[0].equalsIgnoreCase("@YELLOW")) {
						player.sendMessage(Colors.Yellow + message);
					}
					else {
						message = "";
						for (int m = 0; m < tempCommand.length; m++) {
							message += tempCommand[m];
							if ((m + 1) < tempCommand.length) {
								message += " ";
							}
						}
						// Insert variables
						message = message.replace("[NAME]", player.getName());
						message = message.replace("[PLAYERX]", Double.toString(player.getX()));
						message = message.replace("[PLAYERY]", Double.toString(player.getY()));
						message = message.replace("[PLAYERZ]", Double.toString(player.getZ()));
						if (batt != null) {
							message = message.replace("[ATTACKER]", batt.getAtt());
							message = message.replace("[DEFENDER]", batt.getDef());
							message = message.replace("[WINNER]", batt.getWin());
							message = message.replace("[LOSER]", batt.getLooser());
							message = message.replace("[CAUSE]", batt.getCause());
						}
						message = message.replaceFirst("@", "");
						player.sendMessage(message);
					}
				}
				else if (tempCommand[0].charAt(0) == '%') {
					broadcast(tempCommand, player, batt);
				}
				else if ((tempCommand.length == 1) && !(tempCommand[0].charAt(0) == '@') && !(tempCommand[0].charAt(0) == '%')) {
					player.setCommands(new String[] {
						tempCommand[0]
					});
					player.command(tempCommand[0]);
				}
			}
			player.setCommands(playersCommands); // Return old commands
			player.setCanModifyWorld(couldBuild); // Just in case!
		}
	}
	
	public String[][][] shrink(String[][][] arr) {
		int ax = arr.length;
		int ay = arr[0].length;
		int az = arr[0][0].length;
		
		// Check x values
		for (int x = 0; x < ax; x++) {
			if (arr[x][0][0].equalsIgnoreCase("") || arr[x][0][0].trim().equalsIgnoreCase("##")) {
				if (x + 1 == ax) {
					if (ax == 1) {
						arr = new String[1][1][1];
						removeNull(arr);
					}
					else {
						ax--;
						String[][][] tarr = new String[ax][ay][az];
						for (int loopx = 0; loopx < tarr.length; loopx++) {
							for (int loopy = 0; loopy < tarr[0].length; loopy++) {
								for (int loopz = 0; loopz < tarr[0][0].length; loopz++) {
									tarr[loopx][loopy][loopz] = arr[loopx][loopy][loopz];
								}
							}
						}
						arr = tarr;
						removeNull(arr);
					}
				}
				else {
					ax--;
					String[][][] tarr = new String[ax][ay][az];
					int mod = 0;
					for (int loopx = 0; loopx < tarr.length; loopx++) {
						for (int loopy = 0; loopy < tarr[0].length; loopy++) {
							for (int loopz = 0; loopz < tarr[0][0].length; loopz++) {
								if (loopx == x) {
									mod = 1;
								}
								tarr[loopx][loopy][loopz] = arr[loopx + mod][loopy][loopz];
							}
						}
					}
					arr = tarr;
					removeNull(arr);
				}
			}
		}
		// Move data down if needed
		// log.log(Level.INFO, "arr before y switch");
		// printArray(arr);
		for (int x = 0; x < ax; x++) {
			for (int y = 0; y < ay; y++) {
				if (arr[x][y][0].equalsIgnoreCase("")) {
					if ((y + 1) != ay) {
						int mod = 0;
						String[][][] tarr = new String[ax][ay][az];
						for (int loopx = 0; loopx < tarr.length; loopx++) {
							for (int loopy = 0; (loopy + mod) < tarr[0].length; loopy++) {
								for (int loopz = 0; loopz < tarr[0][0].length; loopz++) {
									if ((loopy == y) && (loopx == x)) {
										mod = 1;
									}
									if ((mod == 1) && (loopx != x)) {
										mod = 0;
									}
									tarr[loopx][loopy][loopz] = arr[loopx][loopy + mod][loopz];
								}
							}
						}
						arr = tarr;
						removeNull(arr);
					}
				}
			}
		}
		// log.log(Level.INFO, "arr before z switch");
		// printArray(arr);
		for (int x = 0; x < ax; x++) {
			for (int y = 0; y < ay; y++) {
				for (int z = 0; z < az; z++) {
					if (arr[x][y][z].equalsIgnoreCase("")) {
						if ((z + 1) != az) {
							int mod = 0;
							String[][][] tarr = new String[ax][ay][az];
							for (int loopx = 0; loopx < tarr.length; loopx++) {
								for (int loopy = 0; loopy < tarr[0].length; loopy++) {
									for (int loopz = 0; (loopz + mod) < tarr[0][0].length; loopz++) {
										if ((loopz == z) && (loopy == y) && (loopx == x)) {
											mod = 1;
										}
										if ((mod == 1) && (loopy != y)) {
											mod = 0;
										}
										tarr[loopx][loopy][loopz] = arr[loopx][loopy][loopz + mod];
									}
								}
							}
							arr = tarr;
							removeNull(arr);
						}
					}
				}
			}
		}
		// log.log(Level.INFO, "arr before y resize");
		// printArray(arr);
		// Check y values
		int largeY = 0;
		int emptyY = 0;
		for (int x = 0; x < arr.length; x++) {
			emptyY = 0;
			for (int y = 0; y < arr[0].length; y++) {
				if (!arr[x][y][0].equalsIgnoreCase("") && ((y + 1) > largeY)) {
					largeY = y + 1;
				}
				else if (arr[x][y][0].equalsIgnoreCase("")) {
					emptyY++;
				}
				if ((emptyY == (arr[0].length - 1)) && !arr[x][0][0].equalsIgnoreCase("")) {
					arr = removeYZ(arr[x][0][0], "all", "", arr);
					ax = arr.length;
					if (arr.length >= 1) {
						ay = arr[0].length;
						if (arr[0].length >= 1) {
							az = arr[0][0].length;
						}
						else {
							az = 0;
						}
					}
					else {
						ay = 0;
					}
					emptyY = 0;
				}
			}
		}
		if (ay > largeY) {
			ay = largeY;
			if (ay < 1) {
				ay = 1;
			}
			String tarr[][][] = new String[ax][ay][az];
			for (int loopx = 0; loopx < tarr.length; loopx++) {
				for (int loopy = 0; loopy < tarr[0].length; loopy++) {
					for (int loopz = 0; loopz < tarr[0][0].length; loopz++) {
						tarr[loopx][loopy][loopz] = arr[loopx][loopy][loopz];
					}
				}
			}
			arr = tarr;
			removeNull(arr);
		}
		// Check z values
		// log.log(Level.INFO, "arr before z resize");
		// printArray(arr);
		int largeZ = 0;
		int emptyZ = 0;
		for (int x = 0; x < arr.length; x++) {
			emptyZ = 0;
			for (int y = 0; y < arr[0].length; y++) {
				emptyZ = 0;
				for (int z = 0; z < arr[0][0].length; z++) {
					// log.log(Level.INFO, "X: " + x + " Y:" + y + " Z:" + z);
					if (!arr[x][y][z].equalsIgnoreCase("") && ((z + 1) > largeZ)) {
						largeZ = z + 1;
					}
					else if (arr[x][y][z].equalsIgnoreCase("")) {
						emptyZ++;
					}
					boolean file = false; // False = Ignore, True = Command
					if (insideArr(TAGS, arr[x][0][0])) {
						file = true;
					}
					if ((emptyZ == (arr[0][0].length - 1)) && (y != 0) && !arr[x][y][0].equalsIgnoreCase("")
							&& (!insideArr(SECTIONS, arr[x][y][0]) || file)) {
						// log.log(Level.INFO, "688 removing x:" + arr[x][0][0]
						// + " y:" + arr[x][y][0]);
						arr = removeYZ(arr[x][0][0], arr[x][y][0], "", arr);
						ax = arr.length;
						if (arr.length >= 1) {
							ay = arr[0].length;
							if (arr[0].length >= 1) {
								az = arr[0][0].length;
							}
							else {
								az = 0;
							}
						}
						else {
							ay = 0;
						}
						emptyZ = 0;
					}
				}
			}
		}
		if (az > largeZ) {
			az = largeZ;
			if (az < 1) {
				az = 1;
			}
			String tarr[][][] = new String[ax][ay][az];
			for (int loopx = 0; loopx < tarr.length; loopx++) {
				for (int loopy = 0; loopy < tarr[0].length; loopy++) {
					for (int loopz = 0; loopz < tarr[0][0].length; loopz++) {
						tarr[loopx][loopy][loopz] = arr[loopx][loopy][loopz];
					}
				}
			}
			arr = tarr;
			removeNull(arr);
		}
		// log.log(Level.INFO, "arr after z resize");
		// printArray(arr);
		return arr;
	}
	
	public boolean checkIgnore(Player player, final String tag, final String section) {
		for (int ix = 0; ix < pI.length; ix++) {
			if (pI[ix][0][0].equalsIgnoreCase(player.getName())) {
				for (int iy = 0; iy < pI[ix].length; iy++) {
					if (pI[ix][iy][0].equalsIgnoreCase(tag) && (!tag.equalsIgnoreCase(""))) {
						for (int iz = 0; iz < pI[ix][iy].length; iz++) {
							if (pI[ix][iy][iz].equalsIgnoreCase(section) && (!section.equalsIgnoreCase(""))) {
								return true;
							}
							else if (pI[ix][iy][iz].equalsIgnoreCase("all")) {
								return true;
							}
							else if (pI[ix][iy][iz].equalsIgnoreCase("") && insideArr(SECTIONS, pI[ix][iy][0])) {
								return true;
							}
						}
					}
					else if (pI[ix][iy][0].equalsIgnoreCase(section) && (!section.equalsIgnoreCase(""))) {
						return true;
					}
					else if (pI[ix][iy][0].equalsIgnoreCase("all")) {
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public String[][][] removeYZ(final String x, final String y, final String z, String[][][] arr) {
		for (int ix = 0; ix < arr.length; ix++) {
			if (arr[ix][0][0].equalsIgnoreCase(x)) {
				for (int iy = 0; iy < arr[ix].length; iy++) {
					if (arr[ix][iy][0].equalsIgnoreCase(y)) {
						// log.log(Level.INFO, "751");
						if (z.equalsIgnoreCase("") || y.equalsIgnoreCase("all")) {
							// log.log(Level.INFO, "753");
							arr[ix][iy][0] = "";
						}
						else {
							// log.log(Level.INFO, "757");
							for (int iz = 0; iz < arr[ix][iy].length; iz++) {
								if (arr[ix][iy][iz].equalsIgnoreCase(z) || y.equalsIgnoreCase("all")) {
									arr[ix][iy][iz] = "";
								}
							}
						}
					}
					else if (arr[ix][iy][0].equalsIgnoreCase(z) || y.equalsIgnoreCase("all")) {
						// log.log(Level.INFO, "766");
						arr[ix][iy][0] = "";
					}
					if (z.equalsIgnoreCase("") && !insideArr(TAGS, y)) {
						// log.log(Level.INFO, "770");
						for (int iz = 0; iz < arr[ix][iy].length; iz++) {
							if (arr[ix][iy][iz].equalsIgnoreCase(y) || y.equalsIgnoreCase("all")) {
								arr[ix][iy][iz] = "";
							}
						}
					}
				}
			}
		}
		return shrink(arr);
	}
	
	public boolean printCommands(Player player, final String tag, final String section, String[] arr) {
		boolean isCom = false;
		String msg = "";
		if (arr.length >= 2) {
			for (int x = 1; x < arr.length; x++) {
				msg += arr[x];
				if ((x + 1) != arr.length) {
					if (!arr[x + 1].equalsIgnoreCase("")) {
						msg += ":";
					}
				}
			}
		}
		if (section.equalsIgnoreCase("name") && !msg.equalsIgnoreCase("")) {
			isCom = true;
			if (tag.equalsIgnoreCase("onlogin")) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     OnLogin: " + msg);
			}
			else if (tag.equalsIgnoreCase("onlogout")) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     OnLogout: " + msg);
			}
			else if (tag.equalsIgnoreCase("ondeath")) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     OnDeath: " + msg);
			}
			else if (tag.equalsIgnoreCase("onrespawn")) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     OnRespawn: " + msg);
			}
		}
		else if (!msg.equalsIgnoreCase("")) {
			isCom = true;
			if (insideArr(TAGS, tag)) {
				player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "]     " + arr[0] + ":" + msg);
			}
		}
		return isCom;
	}
	
	public void test(Player p) {
		for (String s : p.getGroups()) {
			p.sendMessage("Testing, " + s);
		}
	}
	
	public boolean checkCommands(Player player, final String tag, final String list, final Battle batt) {
		boolean isCom = false;
		try {
			for (int x = 0; x < gC.length; x++) {
				if (gC[x][0][0].equalsIgnoreCase(tag)) {
					for (int y = 0; y < gC[x].length; y++) {
						if (tag.equalsIgnoreCase("onserverstart")) {
							if (gC[x][y][0].equalsIgnoreCase("default") && (!list.equalsIgnoreCase("all"))) {
								runServerCommands(gC[x][y]);
							}
							else if (list.equalsIgnoreCase("all")) {
								printCommands(player, tag, "all", gC[x][y]);
							}
							else if (!list.equalsIgnoreCase("")) {
								log
										.log(
												Level.INFO,
												("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Invalid section for OnServerStart, only use default."));
							}
						}
						else {
							if (player.getGroups().length >= 1) {
								if ((player.getGroups()[0].equalsIgnoreCase(gC[x][y][0]) && (!gC[x][y][0].equalsIgnoreCase("default")) && !list
										.equalsIgnoreCase("name"))
										|| (list.equalsIgnoreCase("all"))) {
									if (list.equalsIgnoreCase("all")) {
										printCommands(player, tag, "all", gC[x][y]);
									}
									else if (!checkIgnore(player, tag, "group")) {
										runCommands(player, gC[x][y], batt);
									}
								}
							}
							else {
								if ((gC[x][y][0].equalsIgnoreCase("nogroup") && !list.equalsIgnoreCase("all") && !list
										.equalsIgnoreCase("name"))) {
									if (list.equalsIgnoreCase("nogroup")) {
										printCommands(player, tag, "nogroup", gC[x][y]);
									}
									else {
										runCommands(player, gC[x][y], batt);
									}
								}
							}
							if ((gC[x][y][0].equalsIgnoreCase("default") && !list.equalsIgnoreCase("all") && !list.equalsIgnoreCase("name"))) {
								if (list.equalsIgnoreCase("default")) {
									printCommands(player, tag, "default", gC[x][y]);
								}
								else if (!checkIgnore(player, tag, "default")) {
									runCommands(player, gC[x][y], batt);
								}
							}
							if ((gC[x][y][0].equalsIgnoreCase(player.getName())
									&& ((list.equalsIgnoreCase("name")) || (list.equalsIgnoreCase(""))) && !list.equalsIgnoreCase("all"))) {
								if (list.equalsIgnoreCase("name") || list.equalsIgnoreCase("pname")) {
									isCom = printCommands(player, tag, "name", gC[x][y]);
								}
								else if (!checkIgnore(player, tag, "name")) {
									runCommands(player, gC[x][y], batt);
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
		return isCom;
	}
	
	public void list(Player player, final String tag) {
		// List the commands for each tag
		if (tag.equalsIgnoreCase("all")) {
			for (String tagIter : TAGS) {
				player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Listing commands for " + tagIter);
				checkCommands(player, tagIter, "all", null);
			}
		}
		else if (insideArr(TAGS, tag)) {
			for (int i = 0; i < TAGS.length; i++) {
				if (TAGS[i].equalsIgnoreCase(tag)) {
					player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Listing commands for " + TAGS[i]);
					checkCommands(player, tag, "all", null);
				}
			}
		}
		else {
			help(player, "coa list");
		}
	}
	
	public void help(Player player, final String command) {
		if (command.equalsIgnoreCase("general")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Usage is: \"/co [me,ignore,allow,help]\"");
		}
		else if (command.equalsIgnoreCase("help")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Usage is: \"/co help [tag,section,ignore,allow,me]");
		}
		else if (command.equalsIgnoreCase("tag") || command.equalsIgnoreCase("tags")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] A tag relates to an event's commands");
			String t = "";
			for (int i = 0; i < TAGS.length; i++) {
				t += TAGS[i];
				if (i + 1 < TAGS.length) {
					t += ", ";
				}
			}
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Valid tags are: " + t);
		}
		else if (command.equalsIgnoreCase("section")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] A section is a group, or a player name.");
			String t = "";
			for (int i = 0; i < TAGS.length; i++) {
				t += SECTIONS[i];
				if (i + 1 < SECTIONS.length) {
					t += ", ";
				}
			}
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Valid sections are: " + t);
		}
		else if (command.equalsIgnoreCase("ignore")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Ignores commands from a given tag and section.");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Useage is \"/co ignore [tag] [section]\"");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] For a list of tags or sections \"/co help [tag,section]");
		}
		else if (command.equalsIgnoreCase("allow")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Allows commands from a given tag and section to run.");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Useage is \"/co allow [tag] [section]\"");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] For a list of tags or sections \"/co help [tag,section]");
		}
		else if (command.equalsIgnoreCase("me")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] Displays the plugins info on you. This includes ignored commands, and any name commands.");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Useage is \"/co me [ignore,command]\"");
		}
		else if (command.equalsIgnoreCase("admin")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] /co admin [add,remove,list]");
		}
		else if (command.equalsIgnoreCase("vars")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
					+ "] Variables are for use in commands and are case sensitive.");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Valid variables for commands are:");
			String temp = "";
			for (int i = 0; i < VARS.length; i++) {
				temp += VARS[i];
				if (i + 1 < VARS.length) {
					temp += ", ";
				}
			}
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "]     " + temp);
		}
		else if (command.equalsIgnoreCase("coa wipe") || command.equalsIgnoreCase("wipe")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Clears the file specified.");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Usage \"/co admin wipe [ignore,command]\"");
		}
		else if (command.equalsIgnoreCase("coa list") || command.equalsIgnoreCase("list")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Lists the command sections for a given tag.");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Usage is \"/co admin list [tag,all]\"");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] For a list of tags \"/co help tag");
		}
		else if (command.equalsIgnoreCase("coa add") || command.equalsIgnoreCase("add")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Adds the given command to the command list.");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
					+ "] Usage is \"/co admin add [tag] [section] [command]\"");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
					+ "] For a list of tags or sections \"/co help [tag,section]");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] For a list of useable variables \"/co help vars\"");
		}
		else if (command.equalsIgnoreCase("coa remove") || command.equalsIgnoreCase("remove")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
					+ "] Removes the given command from the command list. Spell exactly.");
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
					+ "] Usage is \"/co admin remove [tag] [section] [command]\"");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] For a list of tags or sections \"/co help [tag,section]");
		}
		else if (command.equalsIgnoreCase("coa read") || command.equalsIgnoreCase("read")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Reads the array data from the file.");
		}
		else if (command.equalsIgnoreCase("coa write") || command.equalsIgnoreCase("write")) {
			player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Writes the array data to the file.");
		}
		else {
			help(player, "general");
		}
	}
	
	public String[][][] addYZ(final String x, final String y, final String z, String[][][] arr) {
		int xpos = -1;
		int ypos = -1;
		int zpos = -1;
		int xdim = arr.length;
		int ydim = arr[0].length;
		int zdim = arr[0][0].length;
		for (int xl = 0; xl < arr.length; xl++) {
			if (arr[xl][0][0].equalsIgnoreCase(x)) {
				xpos = xl;
				if (debug)
					log.log(Level.INFO, "Xpos is: " + xpos);
				for (int yl = 0; yl < arr[xl].length; yl++) {
					if (arr[xl][yl][0].equalsIgnoreCase(y) && (ypos == -1)) {
						ypos = yl;
						if (debug)
							log.log(Level.INFO, "990 Ypos is: " + ypos);
						for (int zl = 0; zl < arr[xl][yl].length; zl++) {
							if (arr[xl][yl][zl].equalsIgnoreCase("")) {
								zpos = zl;
							}
						}
						if (zpos == -1) {
							zpos = zdim;
							zdim++;
						}
					}
					else if (arr[xl][yl][0].equalsIgnoreCase("") && (ypos == -1)) {
						ypos = yl;
						if (debug)
							log.log(Level.INFO, "1003 Ypos is: " + ypos);
						for (int zl = 0; zl < arr[xl][yl].length; zl++) {
							if (arr[xl][yl][zl].equalsIgnoreCase("")) {
								zpos = zl;
							}
						}
						if (zpos == -1) {
							zpos = zdim;
							zdim++;
						}
					}
					else if ((yl == arr[xl].length - 1) && (ypos == -1)) {
						ypos = ydim;
						if (debug)
							log.log(Level.INFO, "1016 Ypos is: " + ypos);
						ydim++;
						if (zdim < 2) {
							zdim++;
						}
						zpos = 1;
					}
				}
			}
			else if ((xpos == -1) && (xl == arr.length - 1)) {
				xpos = xdim;
				xdim++;
				if (ydim < 2) {
					ydim++;
				}
				if ((zdim < 2) && ((z != "") || (zdim == 1))) {
					zdim++;
				}
				ypos = 1;
				// log.log(Level.INFO, "1038 Ypos is: " + ypos);
				zpos = 1;
			}
		}
		String[][][] tarr = new String[xdim][ydim][zdim];
		copyArray(arr, tarr);
		tarr[xpos][0][0] = x;
		tarr[xpos][ypos][0] = y;
		tarr[xpos][ypos][zpos] = z;
		removeNull(tarr);
		// printArray(tarr);
		arr = tarr;
		if (debug)
			printArray(arr);
		return shrink(arr);
	}
	
	public void ignore(Player player, final String tag, final String section) {
		if (insideArr(SECTIONS, tag)) {
			if (tag.equalsIgnoreCase("all")) {
				if (!checkIgnore(player, "", "")) {
					player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are now set to ignore all sections.");
					pI = addYZ(player.getName(), tag, "", pI);
					// printArray(pI);
					writeToFile(pI, player_file);
				}
				else {
					player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are already set to ignore all sections.");
				}
			}
			else {
				if (!checkIgnore(player, tag, "")) {
					player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are now set to ignore all " + tag
							+ " sections.");
					// printArray(pI);
					pI = addYZ(player.getName(), tag, "", pI);
					writeToFile(pI, player_file);
				}
				else {
					player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are lready set to ignore this section.");
				}
			}
		}
		else if (insideArr(TAGS, tag)) {
			if (insideArr(SECTIONS, section)) {
				if (section.equalsIgnoreCase("all")) {
					if (!checkIgnore(player, tag, section)) {
						player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are now set to ignore the " + tag
								+ " tag.");
						// printArray(pI);
						pI = addYZ(player.getName(), tag, section, pI);
						writeToFile(pI, player_file);
					}
					else {
						player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are already set to ignore this tag.");
					}
				}
				else {
					if (!checkIgnore(player, tag, section)) {
						player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are now set to ignore the " + section
								+ " section for " + tag + ".");
						// printArray(pI);
						pI = addYZ(player.getName(), tag, section, pI);
						writeToFile(pI, player_file);
					}
					else {
						player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
								+ "] You are already set to ignore this section.");
					}
				}
			}
			else {
				help(player, "ignore");
			}
		}
		else {
			help(player, "ignore");
		}
		// printArray(pI);
	}
	
	public void allow(Player player, final String tag, final String section) {
		if (!checkIgnore(player, tag, section) && !tag.equalsIgnoreCase("all")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You already allow this.");
		}
		else {
			pI = removeYZ(player.getName(), tag, section, pI);
			// pI = shrink(pI);
			if (section.equalsIgnoreCase("")) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You have allowed " + tag + " commands.");
			}
			else {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You have allowed the " + section + " section of "
						+ tag + ".");
			}
			// printArray(pI);
			writeToFile(pI, player_file);
		}
	}
	
	public void writeToFile(String[][][] arr, File file) {
		BufferedWriter file_writer = null;
		try {
			if (file.exists()) {
				file_writer = new BufferedWriter(new FileWriter(file));
				for (int x = 0; x < arr.length; x++) {
					file_writer.write("## " + arr[x][0][0]);
					for (int y = 0; y < arr[x].length; y++) {
						if (!arr[x][y][0].equalsIgnoreCase("")) {
							for (int z = 0; z < arr[x][y].length; z++) {
								if (!arr[x][y][z].equalsIgnoreCase("") && (z == 0) && (!arr[x][y][z].equalsIgnoreCase(arr[x][0][0]))) {
									file_writer.newLine();
									file_writer.write(arr[x][y][z]);
								}
								else if (!arr[x][y][z].equalsIgnoreCase("") && (!arr[x][y][z].equalsIgnoreCase(arr[x][0][0]))) {
									file_writer.write(":" + arr[x][y][z]);
								}
							}
						}
					}
					file_writer.newLine();
				}
				file_writer.close();
			}
		}
		catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void me(Player player, final String com) {
		if (com.equalsIgnoreCase("")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] Use \"/co me ignore\" to list your ignored command lists.");
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White
					+ "] Use \"/co me command\" to list your name command lists.");
		}
		else if (com.equalsIgnoreCase("command")) {
			player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] Your name specific commands are: ");
			// List name commands
			boolean has = false;
			for (String temp : TAGS) {
				if (!temp.equalsIgnoreCase("onserverstart")) {
					if (checkCommands(player, temp, "name", null)) {
						has = true;
					}
				}
			}
			if (!has) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     No commands found.");
			}
		}
		else if (com.equalsIgnoreCase("ignore")) {
			// New way
			boolean first = true;
			boolean isIgnore = false;
			for (String tagIter : TAGS) {
				if (!tagIter.equalsIgnoreCase("onserverstart")) {
					String tempMsg = "";
					for (String sectionIter : SECTIONS) {
						if (checkIgnore(player, tagIter, sectionIter)) {
							if (tempMsg.equalsIgnoreCase("")) {
								tempMsg += " " + sectionIter;
							}
							else {
								tempMsg += ", " + sectionIter;
							}
						}
					}
					if (!tempMsg.equalsIgnoreCase("")) {
						if (first) {
							player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are set to ignore:");
							first = false;
						}
						isIgnore = true;
						player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "]     " + tagIter + ":" + tempMsg);
					}
				}
			}
			if (!isIgnore) {
				player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are not set to ignore anything.");
			}
		}
	}
	
	public boolean isAbb(final String s) {
		if (s.equalsIgnoreCase("/co")) {
			return true;
		}
		else if (s.equalsIgnoreCase("/commandon")) {
			return true;
		}
		else if (s.equalsIgnoreCase("/con")) {
			return true;
		}
		return false;
	}
	
	public void removeFromList(final String tag, final String section, final String command) {
		gC = removeYZ(tag, section, command, gC);
		writeToFile(gC, command_file);
	}
	
	public void addToList(final String tag, final String section, final String command) {
		gC = addYZ(tag, section, command, gC);
		writeToFile(gC, command_file);
	}
	
	public class Listener extends PluginListener {
		CommandOn	p;
		
		// This controls the accessibility of functions / variables from the
		// main class.
		public Listener(CommandOn plugin) {
			p = plugin;
		}
		
		public void onLogin(Player player) {
			login = player.getName();
			checkCommands(player, "OnLogin", "", null);
			if (startCommandQue != null) {
				runCommands(player, startCommandQue, null);
				startCommandQue = null;
			}
		}
		
		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			if ((oldValue <= 0) && (newValue == 20) && (!login.equalsIgnoreCase(player.getName()))) {
				checkCommands(player, "OnRespawn", "", null);
			}
			else {
				login = "";
			}
			if ((newValue <= 0) && (oldValue >= 1)) {
				// Specific on deaths
				for (Death td : deaths) {
					if (td.getName().equalsIgnoreCase(player.getName())) {
						// log.log(Level.INFO, "Found death, executing...");
						// Check if PvP kill
						if (td.getBattle().getPvP()) {
							if (td.getBattle().getAtt().equalsIgnoreCase(td.getBattle().getDef())) {
								// Suicide
								checkCommands(player, "OnDeathSuicide", "", td.getBattle());
								break;
							}
							else {
								// PvP kill
								if (debug)
									log.log(Level.INFO, "PvP battle line 1837");
								checkCommands(td.getBattle().getWinnerP(), "OnPvPKill", "", td.getBattle());
								checkCommands(td.getBattle().getLooserP(), "OnPvPDeath", "", td.getBattle());
								checkCommands(player, "OnPvPEnd", "", td.getBattle());
								currentBattles.remove(td.getBattle());
								deaths.remove(td);
								break; // Done
							}
						}
						else if (td.getBattle().getPvW()) {
							// Other
							if (td.getBattle().getCause().equalsIgnoreCase("cactus")) {
								checkCommands(td.getPlayer(), "OnDeathCactus", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("CREEPER_EXPLOSION")) {
								checkCommands(td.getPlayer(), "OnDeathcreeper", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("EXPLOSION")) {
								checkCommands(td.getPlayer(), "OnDeathexplosion", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("FALL")) {
								checkCommands(td.getPlayer(), "OnDeathfall", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("FIRE")
									|| td.getBattle().getCause().equalsIgnoreCase("FIRE_TICK")) {
								checkCommands(td.getPlayer(), "OnDeathfire", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("LAVA")) {
								checkCommands(td.getPlayer(), "OnDeathlava", "", td.getBattle());
							}
							else if (td.getBattle().getCause().equalsIgnoreCase("WATER")) {
								checkCommands(td.getPlayer(), "OnDeathwater", "", td.getBattle());
							}
							else {
								checkCommands(td.getPlayer(), "OnDeathOther", "", td.getBattle());
							}
							deaths.remove(td);
							break; // Done
						}
						else if (td.getBattle().getPvE()) {
							// Mob kill
							if (td.getBattle().getMob().getName().equalsIgnoreCase("Creeper")) {
								checkCommands(td.getPlayer(), "OnDeathcreeper", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("Zombie")) {
								checkCommands(td.getPlayer(), "OnDeathzombie", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("Skeleton")) {
								checkCommands(td.getPlayer(), "OnDeathskeleton", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("Spider")) {
								checkCommands(td.getPlayer(), "OnDeathspider", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("Ghast")) {
								checkCommands(td.getPlayer(), "OnDeathGhast", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("PigZombie")) {
								checkCommands(td.getPlayer(), "OnDeathPigZombie", "", td.getBattle());
							}
							else if (td.getBattle().getMob().getName().equalsIgnoreCase("Slime")) {
								checkCommands(td.getPlayer(), "OnDeathSlime", "", td.getBattle());
							}
							else {
								// Default
								checkCommands(td.getPlayer(), "OnDeathMob", "", td.getBattle());
							}
							deaths.remove(td);
							break; // Done
						}
					}
				}
				
				// Standard
				checkCommands(player, "OnDeath", "", null);
			}
			
			return false;
		}
		
		public boolean onDamage(PluginLoader.DamageType type, BaseEntity attacker, BaseEntity defender, int amount) {
			long time = etc.getServer().getTime();
			if (defender.isPlayer()) {
				Player def = defender.getPlayer();
				if (attacker != null) {
					if (attacker.isPlayer()) {
						// PvP kill
						Player att = attacker.getPlayer();
						if (def.getHealth() <= amount) {
							
							for (Battle temp : currentBattles) {
								if (((temp.getAtt().equalsIgnoreCase(att.getName()) && temp.getDef().equalsIgnoreCase(def.getName())) || (temp
										.getAtt().equalsIgnoreCase(def.getName()) && (temp.getDef().equalsIgnoreCase(att.getName()))))) {
									temp.setWinner(att);
									temp.setLooser(def);
									
									if (debug)
										log.log(Level.INFO, "Adding new death: " + def.getName() + " time: " + time + " pvp cause: "
												+ att.getName());
									
									// Remove time outs
									for (int i = 0; i < deaths.size(); i++) {
										if (time - deaths.get(i).getTime() >= 100) {
											deaths.remove(i);
										}
									}
									boolean found = false;
									for (Death iter : deaths) {
										if (iter.getName().equalsIgnoreCase(def.getName())) {
											// Same player
											found = true;
										}
									}
									if (!found) {
										if (debug)
											log.log(Level.INFO, "Death not found, adding.");
										deaths.add(new Death(def, time, temp));
									}
									break;
								}
							}
							
						}
						else {
							// Check if it is a new battle
							boolean found = false;
							// New way
							// Get rid of time outs
							for (int i = 0; i < currentBattles.size(); i++) {
								if (etc.getServer().getTime() - currentBattles.get(i).getLastAction() >= 3000) {
									currentBattles.remove(currentBattles.get(i)); // Remove it as it timed out
								}
							}
							// Cycle through the battles
							for (Battle temp : currentBattles) {
								if (((temp.getAtt().equalsIgnoreCase(att.getName()) && temp.getDef().equalsIgnoreCase(def.getName())) || (temp
										.getAtt().equalsIgnoreCase(def.getName()) && (temp.getDef().equalsIgnoreCase(att.getName()))))) {
									found = true; // Found the battle
									temp.setLastAction(time); // Update the battle
								}
							}
							if (!found) {
								// Add the battle
								Battle tempB = new Battle(att, def, etc.getServer().getTime());
								currentBattles.add(tempB);
								if (att.getName().equalsIgnoreCase(def.getName())) {
									// Maybe add something here?
								}
								else {
									checkCommands(attacker.getPlayer(), "OnPvPStart", "", tempB);
								}
							}
						}
					}
					else {
						if (def.getHealth() > 0 && def.getHealth() <= amount) {
							if (attacker.isMob()) {
								// Mob kill
								checkCommands(def, "OnDeathMob", "", null);
								Mob mob = null;
								for (Mob tm : etc.getServer().getMobList()) {
									if (tm.getId() == attacker.getId()) {
										mob = tm;
									}
								}
								
								// log.log(Level.INFO, "Adding new death: " + def.getName() + " time: " + time + " cause: " +
								// mob.getName());
								boolean found = false;
								// Remove time outs
								for (int i = 0; i < deaths.size(); i++) {
									if (time - deaths.get(i).getTime() >= 100) {
										deaths.remove(i);
									}
								}
								for (Death iter : deaths) {
									if (iter.getName().equalsIgnoreCase(def.getName())) {
										// Same player
										found = true;
									}
								}
								
								if (!found) {
									deaths.add(new Death(def, time, new Battle(def, mob, time)));
								}
							}
							else {
								// Don't get here
							}
						}
					}
				}
				else if (def.getHealth() <= amount) {
					// World kill
					boolean found = false;
					// Remove time outs
					for (int i = 0; i < deaths.size(); i++) {
						if (time - deaths.get(i).getTime() >= 100) {
							deaths.remove(i);
						}
					}
					for (Death iter : deaths) {
						if (iter.getName().equalsIgnoreCase(def.getName())) {
							// Same player
							found = true;
						}
					}
					
					if (!found) {
						Death td = new Death(def, time, new Battle(def, type.toString(), time));
						if (debug)
							log.log(Level.INFO, "Adding new death: " + td.getName() + " time: " + td.getTime() + " cause: "
									+ td.getBattle().getCause());
						deaths.add(td);
					}
				}
			}
			return false;
		}
		
		public void onDisconnect(Player player) {
			checkCommands(player, "OnLogout", "", null);
		}
		
		public boolean onCommand(Player player, String[] split) {
			if (split.length >= 1) {
				if (split[0].equalsIgnoreCase("/test") && debug) {
					test(player);
					return true;
				}
				if (split[0].equalsIgnoreCase("/st") && debug) {
					player.sendMessage("The server time is: " + etc.getServer().getTime());
					return true;
				}
				if (isAbb(split[0]) && (player.canUseCommand("/commandon") || player.canUseCommand("/con") || player.canUseCommand("/co"))) {
					if (split.length == 1) {
						help(player, "general");
					}
					else if (split.length >= 2) {
						if (split[1].equalsIgnoreCase("help") || split[1].equalsIgnoreCase("h")) {
							if (split.length >= 3) {
								String temp = "";
								for (int i = 2; i < split.length; i++) {
									temp += split[i];
									if (i + 1 < split.length) {
										temp += " ";
									}
								}
								help(player, temp);
							}
							else {
								help(player, "general");
							}
						}
						else if (split[1].equalsIgnoreCase("ignore") || split[1].equalsIgnoreCase("i")) {
							if (canEditIgnore(player)) {
								if (split.length >= 3) {
									if (split.length >= 4) {
										ignore(player, split[2], split[3]);
									}
									else {
										ignore(player, split[2], "");
									}
								}
								else {
									help(player, split[1]);
								}
							}
							else {
								player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are not allowed to ignore.");
							}
						}
						else if (split[1].equalsIgnoreCase("allow") || split[1].equalsIgnoreCase("a")) {
							if (canEditIgnore(player)) {
								if (split.length >= 3) {
									if (insideArr(TAGS, split[2])) {
										if (split.length >= 4) {
											if (insideArr(SECTIONS, split[3])) {
												allow(player, split[2], split[3]);
											}
											else {
												help(player, split[1]);
											}
										}
										else {
											help(player, split[1]);
										}
									}
									else if (insideArr(SECTIONS, split[2])) {
										allow(player, split[2], "");
									}
								}
								else {
									help(player, split[1]);
								}
							}
							else {
								player.sendMessage("[" + Colors.Yellow + "CommandOn" + Colors.White + "] You are not allowed to allow.");
							}
						}
						else if (split[1].equalsIgnoreCase("me") || split[1].equalsIgnoreCase("m")) {
							if (split.length >= 3) {
								if (split[2].equalsIgnoreCase("command")) {
									me(player, split[2]);
								}
								else if (split[2].equalsIgnoreCase("ignore")) {
									me(player, split[2]);
								}
								else {
									help(player, split[1]);
								}
							}
							else {
								me(player, "ignore");
								me(player, "command");
							}
						}
						else if (split[1].equalsIgnoreCase("admin")) {
							if (canEditList(player)) {
								if (split.length >= 3) {
									if ((split[2].equalsIgnoreCase("wipe") || split[2].equalsIgnoreCase("w")) && debug && isAdmin(player)) {
										if (split.length >= 4) {
											if (split[3].equalsIgnoreCase("ignore") || split[3].equalsIgnoreCase("i")) {
												String[][][] bleh = {
													{
														{
																"", ""
														}
													}
												};
												writeToFile(bleh, player_file);
												readFiles();
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Player File wiped!");
											}
											else if (split[3].equalsIgnoreCase("command") || split[3].equalsIgnoreCase("c")) {
												String[][][] bleh = {
													{
														{
																"", ""
														}
													}
												};
												writeToFile(bleh, command_file);
												readFiles();
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Command File wiped!");
											}
											else {
												help(player, "coa wipe");
											}
										}
										else {
											help(player, "coa wipe");
										}
									}
									else if ((split[2].equalsIgnoreCase("print") || split[2].equalsIgnoreCase("p")) && debug
											&& isAdmin(player)) {
										if (split.length >= 4) {
											if (split[3].equalsIgnoreCase("ignore")) {
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Printing the Player Ignore List to Server Console.");
												printArray(pI);
											}
											else if (split[3].equalsIgnoreCase("command")) {
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Printing the Command List to Server Console.");
												printArray(gC);
											}
										}
									}
									else if ((split[2].equalsIgnoreCase("list") || split[2].equalsIgnoreCase("l")) && canEditList(player)) {
										if (split.length >= 4) {
											if (insideArr(TAGS, split[3]) || split[3].equalsIgnoreCase("all")) {
												list(player, split[3]);
											}
											else {
												help(player, "coa list");
											}
										}
										else {
											help(player, "coa list");
										}
									}
									else if ((split[2].equalsIgnoreCase("add") || split[2].equalsIgnoreCase("a")) && canEditList(player)) {
										if (split.length >= 4) {
											if (insideArr(TAGS, split[3])) {
												if (split.length >= 6) {
													String command = "";
													for (int loop = 5; loop < split.length; loop++) {
														command += split[loop] + " ";
													}
													command = command.trim();
													player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Adding \""
															+ command + "\" to " + split[3] + ":" + split[4]);
													addToList(split[3], split[4], command);
												}
												else {
													help(player, "coa add");
												}
											}
											else {
												help(player, "coa add");
											}
										}
										else {
											help(player, "coa add");
										}
									}
									else if ((split[2].equalsIgnoreCase("remove") || split[2].equalsIgnoreCase("r")) && canEditList(player)) {
										if (split.length >= 6) {
											if (insideArr(TAGS, split[3])) {
												String command = "";
												for (int loop = 5; loop < split.length; loop++) {
													command += split[loop] + " ";
												}
												command = command.trim();
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Removing \""
														+ command + "\" from " + split[3] + ":" + split[4]);
												removeFromList(split[3], split[4], command);
											}
											else {
												help(player, "coa remove");
											}
										}
										else {
											help(player, "coa remove");
										}
									}
									else if (split[2].equalsIgnoreCase("read") && isAdmin(player)) {
										player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Reading files...");
										readFiles();
										player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Arrays updated!");
									}
									else if (split[2].equalsIgnoreCase("write") && isAdmin(player)) {
										player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
												+ "] Writing arrays to file...");
										writeToFile(gC, command_file);
										writeToFile(pI, player_file);
										player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White + "] Files updated!");
									}
									else if (split[2].equalsIgnoreCase("debug") && isAdmin(player)) {
										if (split.length >= 4) {
											if (split[3].equalsIgnoreCase("true")) {
												debug = true;
												props.setBoolean("debug", debug);
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Debug is now set to true.");
											}
											else {
												debug = false;
												props.setBoolean("debug", debug);
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Debug is now set to false.");
											}
										}
										else {
											if (debug) {
												debug = false;
												props.setBoolean("debug", debug);
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Debug is now set to false.");
											}
											else {
												debug = true;
												props.setBoolean("debug", debug);
												player.sendMessage("[" + Colors.Red + "CommandOnAdmin" + Colors.White
														+ "] Debug is now set to true.");
											}
										}
									}
									else {
										help(player, "admin");
									}
								}
								else {
									help(player, "admin");
								}
							}
							else {
								help(player, "general");
							}
						}
						else {
							help(player, "general");
						}
					}
					return true;
				}
				return false;
			}
			return false;
		}
	}
}