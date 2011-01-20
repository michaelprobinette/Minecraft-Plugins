/**
 * 
 */
package bukkit.Vandolis;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * @author Vandolis
 */
public class TeleportPlayerListener extends PlayerListener {
	private final PinPointTeleport	plugin;
	
	/**
	 * @param teleport
	 */
	public TeleportPlayerListener(PinPointTeleport teleport) {
		plugin = teleport;
	}
	
	public void onPlayerCommand(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String[] split = event.getMessage().split(" ");
		if (split.length >= 1) {
			if (split[0].equalsIgnoreCase("/ppt")) {
				if (split.length >= 2) {
					plugin.telePlayer(player, split);
				}
				else {
					player.sendMessage("Usage is /ppt [x] [z] [y]. Leave blank to try and use current.");
				}
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/up")) {
				// Try to move the player up
				plugin.caveElevator(player, true);
				event.setCancelled(true);
			}
			else if (split[0].equalsIgnoreCase("/down")) {
				// Try to move the player down
				plugin.caveElevator(player, false);
				event.setCancelled(true);
			}
		}
	}
}
