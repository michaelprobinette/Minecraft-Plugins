/*
 * Proof of concept for a /follow command. Copyright (C) 2010 Michael Robinette This program is free software: you can redistribute it
 * and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version. This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY;
 * without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details. You should have received a copy of the GNU General Public License along with this program. If not, see
 * <http://www.gnu.org/licenses/>
 */

package bukkit.Vandolis;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerMoveEvent;

/**
 * Handle events for all Player related events
 * 
 * @author Vandolis
 */
public class FollowPlayerListener extends PlayerListener {
	private final Follow	plugin;
	
	public FollowPlayerListener(Follow instance) {
		plugin = instance;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		String[] split = event.getMessage().split(" ");
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/follow")) {
				HashMap<Player, ArrayList<Player>> players = plugin.getPlayers();
				Player player = event.getPlayer();
				if (split.length == 1) {
					// Stop following
					for (Entry<Player, ArrayList<Player>> entry : players.entrySet()) {
						for (Player iter : entry.getValue()) {
							if (iter.getName().equalsIgnoreCase(player.getName())) {
								// System.out.println(player.getName() + " has stopped following " + entry.getKey().getName());
								player.sendMessage("§eYou stopped following " + entry.getKey().getName());
								entry.getValue().remove(iter);
							}
						}
					}
					player.sendMessage("§eYou weren't following anybody.");
				}
				else if (split.length >= 2) {
					String name = split[1].trim();
					
					Player target = plugin.getServer().getPlayer(name);
					if (target != null) {
						// Check the distance between the two to disallow teleporting using it
						double distance = Math.sqrt(Math.pow((target.getLocation().getX() - player.getLocation().getX()), 2)
								+ Math.pow((target.getLocation().getY() - player.getLocation().getY()), 2)
								+ Math.pow((target.getLocation().getZ() - player.getLocation().getZ()), 2));
						if (distance > 4) {
							player.sendMessage("§eYou are too far away.");
						}
						
						for (Entry<Player, ArrayList<Player>> entry : players.entrySet()) {
							for (Player iter : entry.getValue()) {
								if (iter.getName().equalsIgnoreCase(player.getName())) {
									// System.out.println(player.getName() + " forcibly stopped following " + entry.getKey().getName());
									entry.getValue().remove(player);
									break;
								}
							}
						}
						
						// Find the player
						boolean foundTarget = false;
						boolean foundPlayer = false;
						for (Player iter : players.keySet()) {
							if (iter.getName().equalsIgnoreCase(name)) {
								foundTarget = true;
								for (Player iter2 : players.get(target)) {
									if (iter2.getName().equalsIgnoreCase(player.getName())) {
										// Already following, don't add
										foundPlayer = true;
										// System.out.println(player.getName() + " is already following " + target.getName());
										player.sendMessage("You are already following " + target.getName());
									}
								}
							}
						}
						if (!foundTarget) {
							// System.out.println("New player to follow. Adding " + target.getName() + " to the follow list and adding "
							// + player.getName() + " to follow them.");
							// Make a new follow list and tie it to the player to follow
							ArrayList<Player> temp = new ArrayList<Player>();
							temp.add(player);
							players.put(target, temp);
						}
						if (!foundPlayer) {
							// System.out.println("Player not found on the following list, adding them");
							players.get(target).add(player);
							player.sendMessage("§eYou are now following " + target.getName());
						}
					}
					else {
						player.sendMessage("§ePlease give a valid player name.");
					}
				}
				else {
					player.sendMessage("§eUsage is /follow [player]");
				}
				event.setCancelled(true);
			}
		}
	}
	
	// Insert Player related code here
	public void OnPlayerMove(PlayerMoveEvent event) {
		// System.out.println(player.getName() + " moved.");
		// Check if the player is in the list, remove if they are
		HashMap<Player, ArrayList<Player>> players = plugin.getPlayers();
		Player player = event.getPlayer();
		Location from = event.getFrom();
		ArrayList<Player> noFollowers = new ArrayList<Player>();
		for (Player iter : players.keySet()) {
			if (iter.getName().equalsIgnoreCase(event.getPlayer().getName())) {
				ArrayList<Player> delete = new ArrayList<Player>();
				// The player that moved is one of the players being followed, move all of the players
				for (Player p : players.get(player)) {
					double distance = Math.sqrt(Math.pow((from.getX() - p.getLocation().getX()), 2)
							+ Math.pow((from.getY() - p.getLocation().getY()), 2) + Math.pow((from.getZ() - p.getLocation().getZ()), 2));
					// System.out.println("Found " + p.getName() + " on " + player.getName() + "'s follow list. Updating location");
					if (distance <= 4) {
						p.getLocation().setX(from.getX());
						p.getLocation().setY(from.getY());
						p.getLocation().setZ(from.getZ());
					}
					else {
						// Too far, remove them
						delete.add(p);
						if (players.get(player).size() == 1) {
							noFollowers.add(iter);
						}
					}
				}
				for (Player del : delete) {
					// System.out.println(del.getName() + " has stopped following " + player.getName());
					del.sendMessage("§eYou stopped following " + player.getName());
					players.get(player).remove(del);
				}
			}
		}
		// Clean up the hashmap
		for (Player iter : noFollowers) {
			players.remove(iter);
		}
	}
}