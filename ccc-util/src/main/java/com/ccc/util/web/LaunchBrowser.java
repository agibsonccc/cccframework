package com.ccc.util.web;

import java.lang.reflect.Method;
/**
 * This is is a util class for launching a browser in various OSes.
 * @author Adam Gibson
 *
 */
public class LaunchBrowser{

	/**
	 * Method to Open the Browser with Given URL
	 * @param url the url to open
	 */
	public static void openUrl(String url){
		String os = System.getProperty("os.name");
		Runtime runtime=Runtime.getRuntime();
		try{
			// Block for Windows Platform
			if (os.startsWith("Windows")){
				String cmd = "rundll32 url.dll,FileProtocolHandler "+ url;
				Process p = runtime.exec(cmd);
			}

			//Block for Mac OS
			else if(os.startsWith("Mac OS")){
				Class fileMgr = Class.forName("com.apple.eio.FileManager");
				Method openURL = fileMgr.getDeclaredMethod("openURL", new Class[] {String.class});
				openURL.invoke(null, new Object[] {url});
			}

			//Block for UNIX Platform
			else {
				String browser = null;
				for (int count = 0; count < browsers.length && browser == null; count++)
					if (runtime.exec(new String[] {"which", browsers[count]}).waitFor() == 0)
						browser = browsers[count];
				if (browser == null)
					throw new Exception("Could not find web browser");
				else
					runtime.exec(new String[] {browser, url});
			}//end else
		}catch(Exception x){
			System.err.println("Exception occured while invoking Browser!");
			x.printStackTrace();
		}
	}

	public final static String[] browsers={"firefox", "opera", "konqueror", "epiphany", "mozilla", "netscape" };

}//end LaunchBrowser
