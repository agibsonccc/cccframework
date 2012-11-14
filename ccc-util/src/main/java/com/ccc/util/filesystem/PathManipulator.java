/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.filesystem;



import java.io.File;
import java.io.IOException;


/**
 * This is an auxiliary class that holds methods for various methods of file path manipulation, some
 * things may be redundant with that of similar methods of the file class.
 * @author Adam Gibson
 *
 */
public class PathManipulator {

	public static String[] imgExtensions={"jpg", "jpeg", "jpe",
		"png","gif"};
	private final static String http="http:"+"/"+"/";
	private final static String  https="https:"+"/"+"/";
	private final static String  httpwww=http+"www.";
	private final static String  httpswww=https+"www.";
	/**
	 * This checks what braces are used in the given path.
	 * @param path the path to check the braces of.
	 * @throws IllegalArgumentException if it's null, or doesn't contain braces.
	 */
	public static String getBraces(String path) throws IllegalArgumentException{
		checkNull(path);
		if(path.contains("/"))
			return "/";
		else if(path.contains("\\"))
			return "\\";
		else
			throw new IllegalArgumentException("No braces found in path.");
	}//end getBraces
	/**
	 * Given a relative path this will try to get an absolute path out of the given 
	 * path.
	 * @param relativePath the path to try to get an absolute path from.
	 * @return the absolute path derived from the given path, if any.
	 */
	public String getFullPath(String relativePath) throws IllegalArgumentException{
		if(relativePath==null)
			throw new IllegalArgumentException("relativePath can't be null.");
		File file = new File("../"+relativePath);
		String fullPath="";
		try {
			fullPath= file.getCanonicalPath();


		} catch (IOException e) {
			return null;
		}
		return fullPath;
	}
	/**
	 * This gets the absolute path subtracted from the name of the file.
	 * This is an auxiliary method for putting the xml file corresponding
	 * to the image in the same folder as the image.
	 * @param path The full path name to extract from
	 * @throws IllegalArgumentException path can't be null.
	 * @return the path without the name
	 */
	public static String extractPath(String path) throws IllegalArgumentException {
		checkNull(path);
		String s="";

		//Get the file name's length.
		String name=getName(path);

		//Loop until the name of the file has been hit.
		for(int i=0;i<path.length()-name.length();i++)
			s+=path.charAt(i);

		return s;
	}//end extractPath

	public static String getWebApp(String address){
		checkNull(address);
		String ret="";
		boolean ishttp=address.contains(http);
		boolean ishttps=address.contains(https);
		boolean ishttpwww=(ishttp) && address.contains("www.");
		boolean ishttpswww=ishttps && address.contains("www.");

		String host=getHost(address);
		int start=0;
		if(ishttp){
			if(ishttpwww){
				start+=httpwww.length();
			}
			else start+=
				http.length();
		}
		else if(ishttps){
			if(ishttpswww)
				start+=httpswww.length();
			else
				start+=https.length();
		}
		start+=host.length();
		for(int j=start;j<address.length();j++){
			if(address.charAt(j)!='/')
				start++;
			if(address.charAt(j)=='/') break;
		}
		if(address.charAt(start)=='/')
			start++;
		for(int i=start;i<address.length();i++){
			if(address.charAt(i)=='/') break;
			ret+=address.charAt(i);

		}
		return ret;
	}
	/**
	 * This gets the host of a given web address
	 * @param address the address to parse for the host
	 * @return the host name of the given address
	 * @throws IllegalArgumentException if address is null or isn't a valid web address
	 */
	public static String getHost(String address) throws IllegalArgumentException {
		checkNull(address);
		if(address.length()<5)
			throw new IllegalArgumentException("Not a proper url, too few characters");

		boolean http=address.contains("http:"+"/"+"/");
		boolean https=address.contains("https:"+"/"+"/");
		boolean httpwww=(http) && address.contains("www.");
		boolean httpswww=https && address.contains("www.");
		if(!(http || https))
			throw new IllegalArgumentException("Address must be a valid web url: " + address);

		String ret="";
		int startIndex=0;
		if(http){
			//Check for http at the  beginning of the url
			String check="";

			for(int i=startIndex;i<address.length();i++){
				check+=address.charAt(i);
				if(i==6 && !check.equals("http:"+"/"+"/") )
					throw new IllegalArgumentException("http not at beginning not a valid url");

				if(!httpwww){
					if(i>=7){
						//next part of web domain, found the host name
						if(address.charAt(i)=='/' || address.charAt(i)==':')
							return ret;
						ret+=address.charAt(i);
					}
				}

				//Beginning of host name
				else if(httpwww){
					System.out.println("http://www");
					if(i>=11){
						//next part of web domain, found the host name
						if(address.charAt(i)=='/' || address.charAt(i)==':')
							return ret;
						ret+=address.charAt(i);
					}
				}
			}//end for
		}
		else if(https){

			//Check for http at the  beginning of the url
			String check="";

			for(int i=startIndex;i<address.length();i++){

				check+=address.charAt(i);
				System.out.println("check : " + check);
				if(i==7 && !check.equals("https:"+"/"+"/") )
					throw new IllegalArgumentException("http not at beginning not a valid url");

				if(!httpswww){
					if(i>=8){
						//next part of web domain, found the host name
						if(address.charAt(i)=='.' || address.charAt(i)==':')
							return ret;
						//Keep appending characters of the host name until the next part of
						//the address is run in to.
						ret+=address.charAt(i);

					}
				}

				//Beginning of host name
				else if(httpswww){

					if(i>=11){
						//next part of web domain, found the host name
						if(address.charAt(i)=='.' || address.charAt(i)==':')
							return ret;
						//Keep appending characters of the host name until the next part of
						//the address is run in to.
						ret+=address.charAt(i);
					}
				}
			}//end for
		}
		if(containsNumber(ret))
			return ret + ':' + getPortNumber(ret);
		return ret;
	}//end getHost
	/**
	 * This checks to see if a string contains a number.
	 * @param toCheck the string to check
	 * @return true if the string contains a number, false otherwise
	 * @throws IllegalArgumentException if toCheck is null
	 */
	public static boolean containsNumber(String toCheck) throws IllegalArgumentException{
		if(toCheck==null)
			throw new IllegalArgumentException("String can't be null.");
		for(int i=0;i<toCheck.length();i++){
			if(isNumber(toCheck.charAt(i)))
				return true;
		}
		return false;
	}//end containsNumber
	
	/*Helper function for seeing if a given character is a number */
	private static boolean isNumber(char c){
		String c1=c+"";
		try {
			Integer.parseInt(c1);
			return true;
		}
		catch(NumberFormatException e){
			return false;
		}
	}
	/**
	 * This gets the port number of a given url.
	 * @param url the url to get the port number from.
	 * @return the port number of the given url.
	 * @throws IllegalArgumentException if the given url doesn't contain http, is null, or doesn't contain a ':'.
	 */
	public static String getPortNumber(String url){
		if(url==null)
			throw new IllegalArgumentException("Url can't be null.");
		if(!url.contains(http))
			throw new IllegalArgumentException("Invalid url.");
		if(!url.contains(":"))
			throw new IllegalArgumentException("Invalid url: no port number.");

		StringBuffer sb = new StringBuffer();
		for(int i=url.indexOf(':');i<url.length();i++){
			if(url.charAt(i)=='/')
				break;
			sb.append(url.charAt(i));
		}
		return sb.toString();
	}
	/**
	 * This gets the file name from a given path.
	 * @param path a filePath get to get the file name from
	 * @return the name of the file from the given path.
	 */
	public static String getName(String path) throws IllegalArgumentException {
		checkNull(path);
		String ret="";
		ret=path.replace('\\', '/');
		String[] retArr=ret.split("/");
		return retArr[retArr.length-2]+getBraces(path)+retArr[retArr.length-1];

	}//end getName

	/**
	 * This gets the file name from a given path.
	 * @param path a filePath get to get the file name from
	 * @return the name of the file from the given path.
	 */
	public static String getOnlyName(String path) throws IllegalArgumentException {
		checkNull(path);
		String ret="";
		ret=path.replace('\\', '/');
		String[] retArr=ret.split("/");
		return retArr[retArr.length-1];

	}//end getName

	/**
	 * This gets the file name from a given path.
	 * @param path a filePath get to get the file name from
	 * @return the name of the file from the given path.
	 */
	public static String getParentFolder(String path) throws IllegalArgumentException {
		checkNull(path);
		String ret="";
		ret=path.replace('\\', '/');
		String[] retArr=ret.split("/");
		return retArr[retArr.length-2];

	}//end getParentFolder
	/**
	 * This gets the format of the copied file so the applet knows
	 * what to rename the copy.
	 * @param path the path of the file to check for
	 * @return the format of the file
	 */
	public static String getFormat(String path) throws IllegalArgumentException {
		checkNull(path);
		String s="";
		for(int i=path.length()-1;i>0;i--)
			if(path.charAt(i)=='.')
				break;
			else
				s+=path.charAt(i);

		//String is backwards, reverse it.
		s=reverse(s);
		//Make sure the extension is one that can be safely used in a web browser..
		s=s.replace(".","");
		return s;


	}//end getFormat

	/**
	 * This reverses the given string
	 * @param path the string to reverse
	 * @return the reversed version of the string
	 */
	public static String reverse(String path) throws IllegalArgumentException {
		checkNull(path);
		String ret="";
		for(int i=path.length()-1;i>=0;i--)
			ret+=path.charAt(i);

		return ret;
	}//end reverse

	/**
	 * Helper method to ensure that no null files are passed to any of the methods.
	 * @param file object to check
	 */
	public static void checkNull(Object file) throws IllegalArgumentException {
		if(file ==null)
			throw new IllegalArgumentException("File can't be null");
	}//end checkNull

	/**
	 * This extracts the actual url from an abstract path starting from the http and going
	 * to the end of the string
	 * @param s the given path to extract the url from
	 * @return the url from the given string
	 * @throws IllegalArgumentException if s is null, or it didn't find a valid http address, or
	 * if the string given was too short.
	 */
	public static String extractAddress(String s) throws IllegalArgumentException {
		checkNull(s);
		if(s.length()<=4)
			throw new IllegalArgumentException("String needs to be a valid url");
		for(int i=3;i<s.length();i++){
			if(s.charAt(i-3)=='h' && s.charAt(i-2)=='t' && s.charAt(i-1)=='t' && s.charAt(i)=='p')
				return s.substring(i-3);
		}
		throw new IllegalArgumentException("Illegal url was given");
	}//end extractAddress

	/**
	 * This cleans up a string that has mixed slashes.
	 * @param destination the string to change
	 * @return the path will all one type of slash that is used as the proper one on the system.
	 * @throws IllegalArgumentException if destination is null or doesn't have mixed slashes, or if destination is null.
	 */
	public static String addSlashes(String destination) throws IllegalArgumentException {
		checkNull(destination);//May throw exception
		if(destination.contains("/") && destination.contains("\\")){
			String ret="";
			for(int i=0;i<destination.length();i++){
				if(destination.charAt(i)=='/' || destination.charAt(i)=='\\')
					ret+=File.separatorChar;
				else
					ret+=destination.charAt(i);
			}
			return ret;
		}
		throw new IllegalArgumentException("Didn't contain slashes ");
	}//end addSlashes
	
	/**
	 * This will attempt to parse the format of the given file
	 * @param file the file to parse
	 * @return the format of the file or none if it couldn't be found
	 */
	public static String getFormat(File file) {
		String name=file.getName();
		if(!name.contains(".")) return null;
		return name.substring(name.lastIndexOf("."));
	}//end getFormat

}//end PathManipulator
