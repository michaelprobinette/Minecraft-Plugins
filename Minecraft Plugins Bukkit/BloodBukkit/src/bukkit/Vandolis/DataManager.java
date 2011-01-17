package bukkit.Vandolis;
/**
 * 
 */


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Location;

/**
 * @author Vandolis
 */
public class DataManager {
	public static void addChest(Chest add) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:BloodBucket");
		PreparedStatement prep = conn.prepareStatement("insert into chestprotect values (?, ?, ?, ?, ?, ?);");
		prep.setInt(1, add.getLoc().getBlockX());
		prep.setInt(2, add.getLoc().getBlockY());
		prep.setInt(3, add.getLoc().getBlockZ());
		prep.setString(4, add.getPlayer());
		prep.setBoolean(5, add.getPriv());
		String temp = "";
		for (String iter : add.getNames()) {
			temp += iter + " ";
		}
		temp = temp.trim();
		prep.setString(6, temp);
		prep.addBatch();
		
		conn.setAutoCommit(false);
		prep.executeBatch();
		conn.setAutoCommit(true);
	}
	
	public static Chest getChest(Location loc) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:BloodBucket");
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from chestprotect where LocationX = " + loc.getBlockX() + " and LocationY = "
				+ loc.getBlockY() + " and LocationZ = " + loc.getBlockZ() + ";");
		Chest returnChest = new Chest(rs.getString(4), loc, rs.getBoolean(5), rs.getString(6));
		rs.close();
		conn.close();
		return returnChest;
	}
	
	public static void init() throws ClassNotFoundException, SQLException {
		Class.forName("org.sqlite.JDBC");
		Connection conn = DriverManager.getConnection("jdbc:sqlite:BloodBucket");
		Statement stat = conn.createStatement();
		// stat.executeUpdate("drop table if exists chestprotect;");
		stat.executeUpdate("create table if not exists chestprotect (LocationX, LocationY, LocationZ, Player, Private, Allowed);");
		
		conn.close();
	}
	
	public static void main(String[] args) {
		try {
			init();
			// test();
			
			System.out.println("Searching for a chest 20,20,20: ");
			try {
				System.out.println("Chest was found! " + getChest(new Location(null, 20, 20, 20)));
			}
			catch (SQLException e1) {
				System.out.println("Chest was not found.");
			}
			System.out.println("Searching for a chest 1,1,1: ");
			try {
				System.out.println("Chest was found! " + getChest(new Location(null, 1, 1, 1)));
			}
			catch (SQLException e2) {
				System.out.println("Chest was not found.");
			}
			
			printTable();
			
		}
		catch (ClassNotFoundException e) {
			System.out.println("No SQLite found, please install.");
		}
		catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void printTable() throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:BloodBucket");
		Statement stat = conn.createStatement();
		ResultSet rs = stat.executeQuery("select * from chestprotect;");
		while (rs.next()) {
			System.out.println("Chest X: " + rs.getInt(1) + "\tY: " + rs.getInt(2) + "\tZ: " + rs.getInt(3) + "\tCreator: "
					+ rs.getString(4) + "\tPrivate: " + rs.getBoolean(5) + "\tAllowed Users: " + rs.getString(6));
		}
		rs.close();
		conn.close();
	}
	
	public static void removeChest(Location loc) throws SQLException {
		Connection conn = DriverManager.getConnection("jdbc:sqlite:BloodBucket");
		Statement stat = conn.createStatement();
		stat.executeUpdate("delete from chestprotect where LocationX = " + loc.getBlockX() + " and LocationY = " + loc.getBlockY()
				+ " and LocationZ = " + loc.getBlockZ() + ";");
	}
	
	public static void test() throws SQLException {
		addChest(new Chest("test", new Location(null, 20, 20, 20), true, ""));
		addChest(new Chest("test", new Location(null, -10, 15, 23), true, "test2 test3 test4"));
		addChest(new Chest("test", new Location(null, 18, -100, 30), true, "test5 test6"));
		addChest(new Chest("test", new Location(null, 32, 40, -10), true, "test7"));
	}
}
