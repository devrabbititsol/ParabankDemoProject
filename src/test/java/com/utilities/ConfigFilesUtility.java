package com.utilities;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;
public class ConfigFilesUtility {
	/* Set the Configuration setUp path */
	File src;
	FileInputStream fis;
	Properties prop;
	public void loadPropertyFile(String configFileName) throws Exception {
		src = new File(System.getProperty("user.dir") + File.separator + "ConfigFiles" + File.separator + configFileName);
		if(src.exists()) {
		  fis = new FileInputStream(src);
		  prop = new Properties();
		  prop.load(fis);
		}
	}
	
	public String getProperty(String propKey) {
		return prop.getProperty(propKey, "");
	}
	
	public int getIntProperty(String propKey) {
		String value =  prop.getProperty(propKey, "0");
		return Integer.parseInt(value);
	}
	
	public boolean getBooleanProperty(String propKey) {
		String value =  prop.getProperty(propKey, "true");
		return Boolean.parseBoolean(value);
	}
	
	
}
