/*
 * Minecraft plugin that allows commands to be ran on events. Copyright (C) 2010 Michael Robinette
 * 
 * This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License along with this program. If not, see <http://www.gnu.org/licenses/>
 */

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
