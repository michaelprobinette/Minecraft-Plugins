package bukkit.Vandolis;
/**
 * 
 */


import org.bukkit.Location;

/**
 * @author Vandolis
 */
public class Chest {
	private final boolean	priv;
	private final Location	loc;
	private final String	player;
	private final String[]	names;
	
	public Chest(String player, Location loc, boolean priv, String... names) {
		this.player = player;
		this.priv = priv;
		this.loc = loc;
		this.names = names;
	}
	
	public Location getLoc() {
		return loc;
	}
	
	public String[] getNames() {
		return names;
	}
	
	public String getPlayer() {
		return player;
	}
	
	public boolean getPriv() {
		return priv;
	}
	
	public String toString() {
		String temp = "";
		for (String iter : names) {
			temp += iter + " ";
		}
		temp = temp.trim();
		return loc.getBlockX() + " " + loc.getBlockY() + " " + loc.getBlockZ() + " " + player + " " + temp;
	}
}
