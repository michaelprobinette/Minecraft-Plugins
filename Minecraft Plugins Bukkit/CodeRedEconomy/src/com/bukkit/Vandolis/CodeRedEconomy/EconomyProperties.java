/**
 * 
 */
package com.bukkit.Vandolis.CodeRedEconomy;

import java.io.File;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author Mike
 */
public class EconomyProperties {
	private static File				DIR					= null;
	private static PropertiesFile	props				= null;
	private static String			moneyName			= "Strypes";
	private static boolean			debug				= false;
	private static long				restockTime			= 60000;
	private static String			pluginMessage		= "[§cCodeRedEcon§f] ";
	private static int				infValue			= -1;
	private static boolean			useStats			= false;
	private static long				autoDepositTime		= 60000;
	private static int				autoDepositAmount	= 50;
	private static boolean			blockBadWords		= false;
	private static boolean			messageOnBadWord	= false;
	private static long				maxBuySellTime		= 60000;
	private static boolean			useSQL				= false;
	private static CodeRedEconomy	plugin				= null;
	private static String			DB					= "jdbc:sqlite:CodeRedEconomy.sqlite";
	private static boolean			autoPay				= true;
	private static int				pageLength			= 8;
	private static Connection		conn				= null;
	
	/**
	 * @return the pageLength
	 */
	public static int getPageLength() {
		return pageLength;
	}
	
	/**
	 * @return the dB
	 */
	public static String getDB() {
		return DB;
	}
	
	/**
	 * @return the plugin
	 */
	protected CodeRedEconomy getPlugin() {
		return plugin;
	}
	
	/**
	 * @param plugin
	 *            the plugin to set
	 */
	protected static void setPlugin(CodeRedEconomy instance) {
		plugin = instance;
	}
	
	/**
	 * @param dIR
	 *            the dIR to set
	 */
	protected static void setDIR(File dIR) {
		DIR = dIR;
		DB = "jdbc:sqlite:" + DIR.getPath() + "/CodeRedEconomy.sqlite";
	}
	
	/**
	 * Reads the {@link PropertiesFile}
	 */
	public static void readProps() {
		if ((props == null) && (DIR != null)) {
			props = new PropertiesFile(DIR.getPath() + "/data.properties");
		}
		
		if (props != null) {
			if (DIR.exists()) {
				System.out.println("Reading properties file...");
				
				if (props.containsKey("moneyname")) {
					moneyName = props.getString("moneyname");
				}
				else {
					props.setString("moneyname", moneyName);
				}
				
				if (props.containsKey("debug")) {
					debug = props.getBoolean("debug");
				}
				else {
					props.setBoolean("debug", debug);
				}
				
				if (props.containsKey("restocktime")) {
					restockTime = props.getLong("restocktime");
				}
				else {
					props.setLong("restocktime", restockTime);
				}
				
				if (props.containsKey("ingamemessage")) {
					pluginMessage = props.getString("ingamemessage");
				}
				else {
					props.setString("ingamemessage", pluginMessage);
				}
				
				if (props.containsKey("infvalue")) {
					infValue = props.getInt("infvalue");
				}
				else {
					props.setInt("infvalue", infValue);
				}
				
				if (props.containsKey("usestats")) {
					useStats = props.getBoolean("usestats");
				}
				else {
					props.setBoolean("usestats", useStats);
				}
				
				if (props.containsKey("autodeposittime")) {
					autoDepositTime = props.getLong("autodeposittime");
				}
				else {
					props.setLong("autodeposittime", autoDepositTime);
				}
				
				if (props.containsKey("autodepositamount")) {
					autoDepositAmount = props.getInt("autodepositamount");
				}
				else {
					props.setInt("autodepositamount", autoDepositAmount);
				}
				
				if (props.containsKey("blockbadwords")) {
					useStats = props.getBoolean("blockbadwords");
				}
				else {
					props.setBoolean("blockbadwords", blockBadWords);
				}
				
				if (props.containsKey("messageonbadword")) {
					messageOnBadWord = props.getBoolean("messageonbadword");
				}
				else {
					props.setBoolean("messageonbadword", messageOnBadWord);
				}
				
				if (props.containsKey("maxbuyselltimeout")) {
					maxBuySellTime = props.getLong("maxbuyselltimeout");
				}
				else {
					props.setLong("maxbuyselltimeout", maxBuySellTime);
				}
				
				if (props.containsKey("usesqlite")) {
					useSQL = props.getBoolean("usesqlite");
				}
				else {
					props.setBoolean("usesqlite", useSQL);
				}
				
				if (props.containsKey("autopay")) {
					autoPay = props.getBoolean("autopay");
				}
				else {
					props.setBoolean("autopay", autoPay);
				}
				
				if (props.containsKey("pagelength")) {
					pageLength = props.getInt("pagelength");
				}
				else {
					props.setInt("pagelength", pageLength);
				}
				
				if (debug) {
					System.out.println("AutoDepositAmount: " + autoDepositAmount);
					System.out.println("AutoDepositTime: " + autoDepositTime);
					System.out.println("AutoPay: " + autoPay);
					System.out.println("BlockBadWords: " + blockBadWords);
					System.out.println("InfValue: " + infValue);
					System.out.println("MaxBuySellTime: " + maxBuySellTime);
					System.out.println("MessageOnBadWord: " + messageOnBadWord);
					System.out.println("MoneyName: " + moneyName);
					System.out.println("PageLength: " + pageLength);
					System.out.println("PluginMessage: " + pluginMessage);
					System.out.println("RestockTime: " + restockTime);
					System.out.println("UseSQL: " + useSQL);
					System.out.println("UseStats: " + useStats);
				}
			}
			else {
				DIR.mkdir();
				readProps();
			}
		}
		
		if (useSQL) {
			try {
				Class.forName("org.sqlite.JDBC");
				
				conn = DriverManager.getConnection(DB);
			}
			catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	public static long getTime() {
		return System.currentTimeMillis();
	}
	
	/**
	 * @return the dIR
	 */
	public static File getDIR() {
		return DIR;
	}
	
	/**
	 * @return the moneyName
	 */
	public static String getMoneyName() {
		return moneyName;
	}
	
	/**
	 * @return the debug
	 */
	public static boolean isDebug() {
		return debug;
	}
	
	/**
	 * @return the restockTime
	 */
	public static long getRestockTime() {
		return restockTime;
	}
	
	/**
	 * @return the pluginMessage
	 */
	public static String getPluginMessage() {
		return pluginMessage;
	}
	
	/**
	 * @return the infValue
	 */
	public static int getInfValue() {
		return infValue;
	}
	
	/**
	 * @return the useStats
	 */
	public boolean isUseStats() {
		return useStats;
	}
	
	/**
	 * @return the autoDepositTime
	 */
	public static long getAutoDepositTime() {
		return autoDepositTime;
	}
	
	/**
	 * @return the autoDepositAmount
	 */
	public static int getAutoDepositAmount() {
		return autoDepositAmount;
	}
	
	/**
	 * @return the blockBadWords
	 */
	public static boolean isBlockBadWords() {
		return blockBadWords;
	}
	
	/**
	 * @return the messageOnBadWord
	 */
	public boolean isMessageOnBadWord() {
		return messageOnBadWord;
	}
	
	/**
	 * @return the maxBuySellTime
	 */
	public static long getMaxBuySellTime() {
		return maxBuySellTime;
	}
	
	/**
	 * @return the useSQL
	 */
	public static boolean isUseSQL() {
		return useSQL;
	}
	
	/**
	 * @param b
	 */
	public static void setUseSQL(boolean b) {
		useSQL = b;
	}
	
	public static Date getDate() {
		return new Date(System.currentTimeMillis());
	}
	
	/**
	 * @return the conn
	 */
	public static Connection getConn() {
		return conn;
	}
	
	/**
	 * @param conn
	 *            the conn to set
	 */
	public void setConn(Connection conn) {
		this.conn = conn;
	}
	
	/**
	 * @return the autoPay
	 */
	public static boolean isAutoPay() {
		return autoPay;
	}
}
