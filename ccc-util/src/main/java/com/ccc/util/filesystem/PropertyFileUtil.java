package com.ccc.util.filesystem;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.net.URL;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

/**
 * Util class with static methods to deal with properties files
 * 
 * @author Michael Godfrey
 * @version July 12 2011
 */
public class PropertyFileUtil {
	/**
	 * This will load properties from the given file.
	 * @param fileName the file to load from
	 * @return the properties from this file
	 */
	public static Properties loadProperties(String fileName) {
		File f = new File(fileName);
		Assert.notNull(fileName);
		
		InputStream inPropFile;
		
		
		Properties tempProp = new Properties();
		try {
		if(f.exists())
			inPropFile = new FileInputStream(fileName);
		else inPropFile=PropertyFileUtil.class.getResourceAsStream(fileName);
		if(inPropFile==null)
			inPropFile=PropertyFileUtil.class.getResourceAsStream("/" + fileName);
		if(inPropFile==null)
			tempProp=urlProperties(fileName);
		if(tempProp==null)
			throw new IOException("Failed to load file: " + fileName);
			if(log.isDebugEnabled())
				log.debug("Loading file: " + fileName);
			tempProp.load(inPropFile);
			if(log.isDebugEnabled())
				log.debug("Loaded file: " + fileName);
			inPropFile.close();
		} catch (IOException ioe) {
			//System.out.println("I/O Exception : " + e);
			log.error("IO ERRORS : " , ioe);
			//ioe.printStackTrace();
		}
		return tempProp;
	}//end loadProperties
	
	
	private static Properties urlProperties(String fileName) {
		ClassLoader cl = ClassLoader.getSystemClassLoader();
		 Properties props = null;
		if (cl != null) {
	        URL url = cl.getResource(fileName);
	        props = new Properties();
	        if (null == url) {
	            url = cl.getResource("/" + fileName);
	        }
	        if (null != url) {
	            try {
	                InputStream in = url.openStream();
	                props = new Properties();
	                props.load(in);
	            } catch (IOException e) {
	                e.printStackTrace();
	            }

	        }
	     
	    }
		return props;

	}
	
	/**
	 * This will save the properties file.
	 * @param p the properties to save
	 * @param fileName the file name to save to
	 */
	public static void saveProperties(Properties p, String fileName) {
		Assert.notNull(p);
		validateFile(fileName);
		OutputStream outPropFile;

		try {
			FileMoverUtil.createFile(new File(fileName),false);

			outPropFile = new FileOutputStream(fileName);
			p.store(outPropFile, "Properties File to the Test Application");
			outPropFile.close();
		} catch (IOException ioe) {
			ioe.printStackTrace();
			if(log.isErrorEnabled())
				log.error("Save properties failed", ioe);
			System.exit(0);
		}

	}//end saveProperties
	/**
	 * This will delete a property from a properties file.
	 * @param key the key of the property to delete
	 * @param fileName the file to delete from
	 */
	public static void deleteProperty(Object key, String fileName){
		Assert.notNull(key);
		validateFile(fileName);
		Properties p = loadProperties(fileName);
		p.remove(key);
		if(log.isDebugEnabled())
			log.debug("Adding: key: " + key + " to file: " + fileName);
		saveProperties(p, fileName);
	}//end deleteProperty
	
	
	/**
	 * THis will add a property to the given properties file.
	 * @param key the key to add
	 * @param value the value to add
	 * @param fileName the file name to add to
	 */
	public static void addProperty(Object key, Object value, String fileName) {
		Assert.notNull(key);
		Assert.notNull(value);
		Assert.notNull(fileName);

		Properties p = loadProperties(fileName);
		p.put(key, value);
		if(log.isDebugEnabled())
			log.debug("Adding property: " + key.toString() + "  Value: " + value + " to file : " + fileName);
		saveProperties(p, fileName);
	}//end addProperty

	/* This is just validates the file */
	private static void validateFile(String fileName) {
		Assert.notNull(fileName);
		Assert.hasLength(fileName);
		File f = new File(fileName);

		Assert.isTrue(f.exists());
	}//end validateFile
	/**
	 * This returns whether the given property exists.
	 * @param key the key of the value to check
	 * @param fileName the name of the file to retrieve the property from
	 * @return true if the property exists, false otherwise
	 */
	public static boolean propertyExists(Object key,String fileName ) {
		Assert.notNull(key);
		validateFile(fileName);

		return loadProperties(fileName).get(key)!=null;
	}//end propertyExists

	/**
	 * This will replace the given property key with the given value.
	 * @param key the key to replace
	 * @param value the value to replace with
	 * @param fileName the name of the file
	 */
	public static void replaceProperty(Object key, Object value, String fileName) {
		validateFile(fileName);
		Assert.notNull(key);
		Assert.notNull(value);
		Properties p = loadProperties(fileName);
		p.remove(key);
		p.put(key, value);
		saveProperties(p, fileName);
	}//end replaceProperty

	public static Logger log=LoggerFactory.getLogger(PropertyFileUtil.class);
}//end PropertyFileUtil
