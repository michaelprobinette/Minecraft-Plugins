public class Death {
	private Player	player	= null;
	private Battle	batt	= null;
	private String	name	= "";
	private long	time	= 0;
	
	public Death(Player p, long time, String cause) {
		this.player = p;
		this.name = p.getName();
		this.time = time;
		batt = new Battle(p, cause, time);
	}
	
	public Death(Player p, long time, Battle batt) {
		this.player = p;
		this.name = p.getName();
		this.time = time;
		this.batt = batt;
	}
	
	public Death(Player p, long time, Mob mob) {
		this.player = p;
		this.name = p.getName();
		this.time = time;
		batt = new Battle(p, mob, time);
	}
	
	public Battle getBattle() {
		return batt;
	}
	
	public Player getPlayer() {
		return player;
	}
	
	public String getName() {
		return name;
	}
	
	public long getTime() {
		return time;
	}
}
