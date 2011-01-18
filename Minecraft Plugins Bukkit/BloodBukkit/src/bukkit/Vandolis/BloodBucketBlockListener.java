package bukkit.Vandolis;

/**
 * 
 */

import org.bukkit.Material;
import org.bukkit.Player;
import org.bukkit.event.block.BlockListener;
import org.bukkit.event.block.BlockRightClickedEvent;

/**
 * BloodBucket block listener
 * 
 * @author Vandolis
 */
public class BloodBucketBlockListener extends BlockListener {
	private final BloodBucket	plugin;
	
	public BloodBucketBlockListener(final BloodBucket plugin) {
		this.plugin = plugin;
	}
	
	// put all Block related code here
	public void onBlockRightClicked(BlockRightClickedEvent event) {
		Player player = event.getPlayer();
		if (event.getItemInHand().getType().equals(Material.CHEST)) {
			CommandInfo comm = null;
			// Check the players list
			for (Player iter : plugin.getPlayers().keySet()) {
				if (iter.equals(player)) {
					comm = plugin.getPlayers().get(iter);
				}
			}
			if (comm != null) {
				// Execute it
				
			}
		}
	}
}