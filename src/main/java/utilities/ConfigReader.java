/*
 * @Author : Deepak Mahapatra
 */

package utilities;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class ConfigReader {
	private static Properties prop;

	/** Loads the properties only once */
	public static Properties init_prop() {
		if (prop == null) {
			prop = new Properties();
			try {
				FileInputStream ip = new FileInputStream("src/test/resources/config/config.properties");
				prop.load(ip);
			} catch (IOException e) {
				e.printStackTrace();
				throw new RuntimeException("Failed to load config.properties file!");
			}
		}
		return prop;
	}

	/** Get a property value by key */
	public static String getProperty(String key) {
		if (prop == null) {
			init_prop();
		}
		return prop.getProperty(key);
	}

	/** Get a property as list (comma separated values) */
	public static List<String> getListProperty(String key) {
		String value = getProperty(key);
		if (value != null && !value.trim().isEmpty()) {
			return Arrays.asList(value.split("\\s*,\\s*")); // trims spaces
		}
		return null;
	}
}
