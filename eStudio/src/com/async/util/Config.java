package com.async.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import org.apache.log4j.Logger;

public class Config {
	private static final Logger LOG = Logger.getLogger(Config.class);
	private static final String CONFIG_FILE_NAME = "config.properties";
	private static Properties properties = null;

	static {
		properties = new Properties();
		try {
			 String currentClasspath = getCurrentClasspath();
			 String fullConfigFilePath = currentClasspath + File.separator + CONFIG_FILE_NAME;
			 properties.load(new FileInputStream(new File(fullConfigFilePath)));
			
			//HARINDRA (test this)
			//InputStream inputStream = Config.class.getResourceAsStream(File.separator + CONFIG_FILE_NAME);
			//properties.load(inputStream);
			
		} catch (Exception e) {
			LOG.error(e);
		}
	}

	public static String getProperty(String key) {
		try {
			return properties.getProperty(key);
		} catch (Exception e) {
			LOG.error(e);
		}
		return null;
	}

	public static String getCurrentClasspath() throws UnsupportedEncodingException {
		ClassLoader loader = Thread.currentThread().getContextClassLoader();
		URL resource = loader.getResource("");
		LOG.info(resource.getPath());
		return URLDecoder.decode(resource.getPath(), "UTF-8");
	}

	public static void main(String[] args) throws Exception {
		String sPath = "C:\\Program%20Files\\config.properties";
		String sDecode = URLDecoder.decode(sPath, "UTF-8");
		Properties p = new Properties();
		p.load(new FileInputStream(new File(sDecode)));
		System.out.println("DONE");
	}
}
