public class Group {
	private String	group			= "";
	private int[]	allowedBlocks	= new int[1];
	
	public Group(String group) {
		this.group = group;
	}
	
	public Group(String group, int[] allowed) {
		this.group = group;
		allowedBlocks = allowed;
	}
	
	public String getGroupName() {
		return group;
	}
	
	public int[] getAllowed() {
		return allowedBlocks;
	}
}
