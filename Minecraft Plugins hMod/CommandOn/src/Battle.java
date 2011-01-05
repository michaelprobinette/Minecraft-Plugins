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

public class Battle {
	private Player	attacker	= null;
	private Player	defender	= null;
	private Mob		mob			= null;
	private Player	winner		= null;
	private Player	looser		= null;
	private String	attName		= "";
	private String	defName		= "";
	private String	winName		= "";
	private String	looseName	= "";
	private String	cause		= "";
	private long	startTime	= 0;
	private long	lastAction	= 0;
	private boolean	pve			= false;
	private boolean	pvp			= false;
	private boolean	pvw			= false;
	
	public Battle(final Player attacker, final Player defender, long startTime) {
		this.attacker = attacker;
		this.defender = defender;
		this.startTime = startTime;
		lastAction = startTime;
		attName = attacker.getName();
		defName = defender.getName();
		pvp = true;
	}
	
	public Battle(final Player defender, final String cause, long startTime) {
		this.defender = defender;
		this.cause = cause;
		this.startTime = startTime;
		pvw = true;
	}
	
	public Battle(final Player defender, final Mob mob, long startTime) {
		this.defender = defender;
		this.mob = mob;
		this.startTime = startTime;
		pve = true;
	}
	
	public boolean getPvE() {
		return pve;
	}
	
	public boolean getPvW() {
		return pvw;
	}
	
	public Mob getMob() {
		return mob;
	}
	
	public boolean getPvP() {
		return pvp;
	}
	
	public String getAtt() {
		return attName;
	}
	
	public String getDef() {
		return defName;
	}
	
	public String getCause() {
		return cause;
	}
	
	public String getWin() {
		return winName;
	}
	
	public String getLooser() {
		return looseName;
	}
	
	public void setWin(String win) {
		winName = win;
	}
	
	public void setLoose(String loose) {
		looseName = loose;
	}
	
	public void setWinner(Player p) {
		this.winner = p;
		this.winName = p.getName();
	}
	
	public void setLooser(Player p) {
		this.looser = p;
		this.looseName = p.getName();
	}
	
	public Player getAttackerP() {
		return attacker;
	}
	
	public Player getDefenderP() {
		return defender;
	}
	
	public Player getWinnerP() {
		return winner;
	}
	
	public Player getLooserP() {
		return looser;
	}
	
	public long getStartTime() {
		return startTime;
	}
	
	public long getLastAction() {
		return lastAction;
	}
	
	public void setLastAction(long time) {
		this.lastAction = time;
	}
}
