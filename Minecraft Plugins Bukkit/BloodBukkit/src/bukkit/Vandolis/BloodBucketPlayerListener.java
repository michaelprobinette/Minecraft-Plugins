package bukkit.Vandolis;
/**
 * 
 */


import org.bukkit.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * Handle events for all Player related events
 * 
 * @author <yourname>
 */
public class BloodBucketPlayerListener extends PlayerListener {
	private final BloodBucket	plugin;
	
	public BloodBucketPlayerListener(BloodBucket instance) {
		plugin = instance;
	}
	
	// Insert Player related code here
	public void onPlayerCommand(PlayerChatEvent event) {
		String split[] = event.getMessage().split(" ");
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/cp")) {
				if (split.length >= 2) {
					int level = -1;
					try {
						level = Integer.valueOf(split[1].trim());
						if ((level == 1) || (level == 0)) {
							boolean found = false;
							String names = "";
							for (int x = 2; x < split.length; x++) {
								names += split[x] + " ";
							}
							names = names.trim();
							for (Player iter : plugin.getPlayers().keySet()) {
								if (iter.getName().equalsIgnoreCase(event.getPlayer().getName())) {
									// Same player, reset the commandinfo
									plugin.getPlayers().get(iter).setList(false);
									plugin.getPlayers().get(iter).setPriv(level);
									
									plugin.getPlayers().get(iter).setNames(names);
									found = true;
								}
							}
							if (!found) {
								plugin.getPlayers().put(event.getPlayer(), new CommandInfo(level, names));
							}
							if (!names.equalsIgnoreCase("")) {
								if (level == 0) {
									event.getPlayer().sendMessage("You will now set chests to Public");
								}
								else {
									event.getPlayer().sendMessage("You will now set chests to Private except for " + names);
								}
							}
							else {
								if (level == 0) {
									event.getPlayer().sendMessage("You will now set chests to Public");
								}
								else {
									event.getPlayer().sendMessage("You will now set chests to Private");
								}
							}
						}
						else {
							event.getPlayer().sendMessage("Valid options are 0 (Public) or 1 (Private)");
						}
					}
					catch (NumberFormatException e) {
						if (split[1].equalsIgnoreCase("add")) {
							if (split.length >= 3) {
								// We can assume it is private
								// Find the player in the hashmap
								String names = "";
								for (int x = 2; x < split.length; x++) {
									names += split[x] + " ";
								}
								names = names.trim();
								for (Player iter : plugin.getPlayers().keySet()) {
									if (iter.getName().equalsIgnoreCase(event.getPlayer().getName())) {
										// Same player
										plugin.getPlayers().get(iter).setList(false);
										plugin.getPlayers().get(iter).addNames(names);
									}
								}
							}
							else {
								// TODO Help
							}
						}
						else if (split[1].equalsIgnoreCase("remove")) {
							if (split.length >= 3) {
								// plugin.getPlayers().get(iter).setList(false);
							}
							else {
								// TODO Help
							}
						}
						else if (split[1].equalsIgnoreCase("list")) {
							boolean found = false;
							for (Player iter : plugin.getPlayers().keySet()) {
								if (iter.getName().equalsIgnoreCase(event.getPlayer().getName())) {
									found = true;
									plugin.getPlayers().get(iter).setList(true);
								}
							}
							if (!found) {
								CommandInfo temp = new CommandInfo(0, "");
								temp.setList(true);
								plugin.getPlayers().put(event.getPlayer(), temp);
							}
							event.getPlayer().sendMessage("You will now list the chest information on right click.");
						}
						else {
							// TODO Help
						}
					}
				}
				event.setCancelled(true); // We are done with this command
			}
		}
	}
}