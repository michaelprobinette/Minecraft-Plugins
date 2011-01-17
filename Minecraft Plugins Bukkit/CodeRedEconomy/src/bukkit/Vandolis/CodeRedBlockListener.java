package bukkit.Vandolis;
/**
 * 
 */


import org.bukkit.event.block.BlockListener;

/**
 * @author Vandolis
 */
public class CodeRedBlockListener extends BlockListener {
	private final CodeRedEconomy	plugin;
	
	/**
	 * @param codeRedEconomy
	 */
	public CodeRedBlockListener(CodeRedEconomy codeRedEconomy) {
		plugin = codeRedEconomy;
	}
}
