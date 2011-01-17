package bukkit.Vandolis;
/**
 * 
 */


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
		
	}
}