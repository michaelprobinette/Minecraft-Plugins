package bukkit.Vandolis;
/**
 * 
 */


import java.util.ArrayList;

/**
 * @author Vandolis
 */
public class CommandInfo {
	private boolean				priv	= false;
	private boolean				list	= false;
	private ArrayList<String>	names	= new ArrayList<String>();
	
	public CommandInfo(int level, String names) {
		if (level == 0) {
			priv = false;
		}
		else if (level == 1) {
			priv = true;
		}
		for (String iter : names.split(" ")) {
			this.names.add(iter);
		}
	}
	
	public void addNames(String nameList) {
		if (names.size() == 0) {
			for (String iter : nameList.split(" ")) {
				names.add(iter);
			}
		}
		else {
			for (String striter : nameList.split(" ")) {
				boolean found = false;
				for (String iter : names) {
					if (iter.equalsIgnoreCase(striter)) {
						found = true;
					}
				}
				if (!found) {
					// Add it
					names.add(striter);
				}
			}
		}
	}
	
	public boolean getList() {
		return list;
	}
	
	public ArrayList<String> getNames() {
		return names;
	}
	
	public boolean getPriv() {
		return priv;
	}
	
	public void setList(boolean list) {
		this.list = list;
	}
	
	public void setNames(String nameList) {
		names = new ArrayList<String>();
		for (String iter : nameList.split(" ")) {
			names.add(iter);
		}
	}
	
	public void setPriv(int level) {
		if (level == 0) {
			priv = false;
		}
		else if (level == 1) {
			priv = true;
		}
	}
}
