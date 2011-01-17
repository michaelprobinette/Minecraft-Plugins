/*
 * Borrowed from the now dead hMod. Still a handy way to save settings
 */
package bukkit.Vandolis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.logging.Logger;

public final class PropertiesFile {
	private static final Logger	log		= Logger.getLogger("Minecraft");
	private final String		fileName;
	private final Properties	props	= new Properties();
	
	public PropertiesFile(String fileName) {
		this.fileName = fileName;
		
		File file = new File(fileName);
		try {
			if (file.exists()) {
				load();
			}
			else {
				save();
			}
		}
		catch (IOException ex) {
			log.severe("[PropertiesFile] Unable to load " + fileName + "!");
		}
	}
	
	public boolean containsKey(String var) {
		return props.containsKey(var);
	}
	
	public boolean getBoolean(String key) {
		if (containsKey(key)) {
			return Boolean.parseBoolean(getProperty(key));
		}
		
		return false;
	}
	
	public boolean getBoolean(String key, boolean value) {
		if (containsKey(key)) {
			return Boolean.parseBoolean(getProperty(key));
		}
		
		setBoolean(key, value);
		return value;
	}
	
	public double getDouble(String key) {
		if (containsKey(key)) {
			return Double.parseDouble(getProperty(key));
		}
		
		return 0.0D;
	}
	
	public double getDouble(String key, double value) {
		if (containsKey(key)) {
			return Double.parseDouble(getProperty(key));
		}
		
		setDouble(key, value);
		return value;
	}
	
	public int getInt(String key) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}
		
		return 0;
	}
	
	public int getInt(String key, int value) {
		if (containsKey(key)) {
			return Integer.parseInt(getProperty(key));
		}
		
		setInt(key, value);
		return value;
	}
	
	public long getLong(String key) {
		if (containsKey(key)) {
			return Long.parseLong(getProperty(key));
		}
		
		return 0L;
	}
	
	public long getLong(String key, long value) {
		if (containsKey(key)) {
			return Long.parseLong(getProperty(key));
		}
		
		setLong(key, value);
		return value;
	}
	
	public String getProperty(String var) {
		return props.getProperty(var);
	}
	
	public String getString(String key) {
		if (containsKey(key)) {
			return getProperty(key);
		}
		
		return "";
	}
	
	public String getString(String key, String value) {
		if (containsKey(key)) {
			return getProperty(key);
		}
		
		setString(key, value);
		return value;
	}
	
	public boolean keyExists(String key) {
		return containsKey(key);
	}
	
	public void load() throws IOException {
		props.load(new FileInputStream(fileName));
	}
	
	public void removeKey(String var) {
		if (props.containsKey(var)) {
			props.remove(var);
			save();
		}
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> returnMap() throws Exception {
		return (Map<String, String>) props.clone();
	}
	
	public void save() {
		try {
			props.store(new FileOutputStream(fileName), null);
		}
		catch (IOException ex) {
		}
	}
	
	public void setBoolean(String key, boolean value) {
		props.put(key, String.valueOf(value));
		
		save();
	}
	
	public void setDouble(String key, double value) {
		props.put(key, String.valueOf(value));
		
		save();
	}
	
	public void setInt(String key, int value) {
		props.put(key, String.valueOf(value));
		
		save();
	}
	
	public void setLong(String key, long value) {
		props.put(key, String.valueOf(value));
		
		save();
	}
	
	public void setString(String key, String value) {
		props.put(key, value);
		save();
	}
}