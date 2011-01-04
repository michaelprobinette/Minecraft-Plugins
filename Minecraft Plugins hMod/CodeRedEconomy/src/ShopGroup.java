public class ShopGroup {
	private String	groupName		= "";
	private int[]	allowedBlocks	= new int[1];
	
	public ShopGroup(String group) {
		this.groupName = group;
	}
	
	public ShopGroup(String group, int[] allowed) {
		this.groupName = group;
		allowedBlocks = allowed;
	}
	
	public String getGroupName() {
		return groupName;
	}
	
	public int[] getAllowed() {
		return allowedBlocks;
	}
}
