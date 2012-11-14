/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web;




import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import java.util.Map;
import java.util.concurrent.Semaphore;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import com.ccc.util.filesystem.PathManipulator;
import com.ccc.util.web.spider.unbounded.SubUrlMachine;



//import javax.servlet.http.HttpServletRequest;
/**
 * This manipulates web addresses in various ways.
 * @author Adam Gibson
 *
 */
public class URLManipulator extends PathManipulator{
	/**
	 * This appends request parameters on to a given url.
	 * @param url the url to append on to.
	 * @param params the params to add
	 * @return the URL with request params attached.
	 */
	public String addRequestParams(String url,String[] paramValues,String[]paramNames){
		checkNull(url);
		checkNull(paramValues);
		checkNull(paramNames);
		String ret=url;
		ret+="?"+paramNames[0]+"="+paramNames[0];
		if(paramNames.length >1)
			for(int i=1;i<paramNames.length;i++){
				if(i<paramNames.length-1)
					ret+="&"+paramNames[i];
				ret+="="+paramValues[i];
			}

		return ret;
	}//end addRequestParams
	public Map<String,String> getRequestNames(String url) throws IllegalArgumentException{
		checkNull(url);
		int index=0;
		for(int j=0;j<url.length();j++){
			if(url.charAt(j)=='?'){
				j++;
				index=j;
				break;

			}
			if(j==url.length()-1)
				throw new IllegalArgumentException("Illegal url entered.");

		}
		String newUrl=url.substring(index);
		System.out.println("New url:" + newUrl);
		String part1="";
		String part2="";
		Map<String,String> ret = new HashMap<String,String>(10);
		boolean val=false;
		for(int i=0;i<newUrl.length();i++){


			if(newUrl.charAt(i)=='&' || i==newUrl.length()-1){
				if(i==newUrl.length()-1){
					part2+=url.charAt(url.length()-1);

					ret.put(part1, part2);
					return ret;
				}
				ret.put(part1, part2);
				part1="";
				part2="";
				i++;
				val=false;
			}
			if(newUrl.charAt(i)!='=' && !val)
				part1+=""+newUrl.charAt(i);

			else if(newUrl.charAt(i)=='=')
				val=true;

			if(val)
				part2+=newUrl.charAt(i);

		}
		return ret;
	}
	/**

	This checks request parameters based on white space, you can't check for null, so check for all the white space characters. 
	public boolean isWhiteSpace(String test,HttpServletRequest request){
		try {
			return !(Character.isWhitespace((request.getParameter(test).charAt(0))));
		}
		//Empty string, it was white space.
		catch(Exception e){
			return true;
		}
	}//end isWhiteSpace
	 */
	/**
	 * This returns whether the given 
	 * string is an url or not.
	 * 
	 */
	public boolean isUrl(String urlToCheck){
		if(urlToCheck==null || urlToCheck.equals("") || urlToCheck.equals(""))
			throw new IllegalArgumentException("Given url can't be null or empty.");
		try {
			URL test = new URL(urlToCheck);
			return true;
		} catch (MalformedURLException e) {
			return false;
		}
	}
	/* Invalid termination character for urls */
	private static boolean invalidTermCharacter(char c){
		return c=='<' || c=='>' || c=='"' || c=='\'' || c=='%' || c=='&' || c=='*' ||c=='@' || c=='#' || c==')' || c=='(' || c=='{' || c=='}' || c==' ';
	}


	/**
	 * This reads from a  url, and returns the contents in a list of strings
	 * @return the url contents in a list of strings
	 * @throws UnknownHostException if there is no host to connect to
	 * @throws SocketException if the connection is reset
	 * @throws FileNotFoundException if the file wasn't found on the server
	 */
	private static List<String> readURL(URL urlToWatch) throws UnknownHostException,SocketException,FileNotFoundException {
		URLConnection conn=null;
		List<String> ret = new ArrayList<String>();
		//Connect to the url
		try {conn=urlToWatch.openConnection();} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//Read in the url and copy the list
		BufferedInputStream bi =null;

		try {bi= new BufferedInputStream(conn.getInputStream());} 

		catch (IOException e) 
		{/*e.printStackTrace(); */}
		//Problem reading from the	 connnection, return ret
		if(bi==null)
			return ret;

		//Translate the connections input stram
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(bi));
		//Read the url line by line
		String line="";
		try {
			while((line=br.readLine())!=null)
				ret.add(line);
		} 
		catch (IOException e) 
		{e.printStackTrace();}

		return ret;
	}//end readURL

	/**
	 * This will scrub all of the urls from a page given a url
	 * @param urlToScrub the url to get urls from
	 * @return a set of urls scrubbed from the given url
	 */
	public static Set<String> scrubURLs(URL urlToScrub){
		Assert.notNull(urlToScrub);
		try {
			List<String> urlContents=readURL(urlToScrub);
			Set<String> ret = new HashSet<String>();

			for(String s : urlContents){
				Set<String> add=scanLine(s,urlToScrub);
				if(add!=null && !add.isEmpty())
					ret.addAll(add);
			}
			return ret;
		} 
		catch (UnknownHostException e) {
			e.printStackTrace();
			return null;
		} 
		catch (SocketException e) {
			e.printStackTrace();
			return null;
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			return null;
		}
	}//end scrubURLs


	/**
	 * This removes invalid strings from the list
	 * @param ret the list to remove things from
	 * @return the list
	 */
	private static Set<String> removeInvalidStrings(Set<String> ret){
		//Ret is shared resource with strings, add a semaphore to protect it
		try {s1.acquire();} 
		catch (InterruptedException e) {}
		synchronized(s1){	
			try {
				//Check the string for invalid URLs that may have gotten through
				for(String s : ret)
					if(!s.contains("/")){
						//System.out.println("Removing: "  + s);
						ret.remove(s);
					}

			}
			catch(ConcurrentModificationException e){
				try {
					s1.wait(200);

					s1.release();


				} 
				catch (InterruptedException e1) {}
			}
			//Release the lock
			synchronized(s1){
				s1.release();

				s1.notifyAll();
			}
		}
		return ret;
	}//end removeInvalidStrings


	/**
	 * This will take a json object and turn it in to a url encoded string
	 * @param url the url to load
	 * @return a url encoded json object,or null if url is null
	 * @throws JSONException if the json object isn't valid
	 */
	public static String jsonToUrlEncode(JSONObject url) throws JSONException {
		return jsonToUrlEncode(url,new StringBuffer());
	}//end jsonToUrlEncode
	
	/**
	 * This will take a json object and turn it in to a url encoded string
	 * @param url the url to load
	 * @param already the stringbuffer to use
	 * @return a url encoded json object,or null if url is null
	 * @throws JSONException if the json object isn't valid
	 */
	public static String jsonToUrlEncode(JSONObject url,StringBuffer already) throws JSONException {
		if(url==null)
			return null;
		
		JSONArray names=url.names();
		
		if(names!=null && names.length() > 1) {
			for(int i=0;i<names.length();i++) {
				String name=names.getString(i);
				Object o=url.get(name);
				if(o instanceof JSONObject) {
					JSONObject obj=(JSONObject)o;
					return jsonToUrlEncode(obj,already);
				}
				else {
					already.append(name);
					already.append("=");
					already.append(o.toString());
					String ampersand= i < names.length() - 1 ? "&" : "";
					already.append(ampersand);
				}
			}
		}
		else {
			String name=names.getString(0);
			Object obj=url.get(name);
			
			if(obj instanceof JSONObject) {
				JSONObject json=(JSONObject) obj;
				return jsonToUrlEncode(json,already);
			}
			else {
				already.append(name);
				already.append("=");
				already.append(obj.toString());
			}
			
			
		}
		return already.toString();
	}//end jsonToUrlEncode

	/**
	 * 
	 * @param line the line to check and return a set of possible urls for
	 * @return the set of possible urls in the given line
	 * @throws IllegalArgumentException if line is empty or null
	 */
	public static Set<String> scanLine(String line,URL urlToWatch){
		if(line==null || line.length() < 1)
			return null;
		//No URL contents to break apart
		if(!(line.contains("http") || line.contains("src=\"")|| line.contains("href=\"") || line.contains("www") || !line.contains(":url(")))
			return null;

		else {
			//if(line.contains("</html>")){
			//System.out.println("Arrived.");
			//}
			//if(line.contains("pdf")){
			//System.out.println("Here");
			//}
			//Build a set based on scanning the line for various urls,
			//Scan for relative URLS in CSS, the beginnings of URLs in www, http, or any src or href links
			Set<String> ret = new HashSet<String>();
			if(line.contains("src") || line.contains("SRC"))
				ret.addAll(scanSource(line));



			//Check for href=
			if(line.contains("href") || line.contains("HREF"))
				ret.addAll(scanHref(line));			

			//Check for http or https
			if(line.contains("http"))
				ret.addAll(scanHttp(line,urlToWatch));

			//Relative url without http could have been found
			if(line.contains("www"))
				ret.addAll(scanWww(line));	


			//Capture css urls
			if(line.contains(":url("))
				ret.addAll(scanCss(line,urlToWatch));

			//Check for style urls
			if(line.contains("background="))
				ret.addAll(scanBackground(line));

			//Remove invalid strings, if any are left
			if(!ret.isEmpty())
				ret=removeInvalidStrings(ret);

			return ret;
		}

	}//end scanLine

	/* Scan for http within the line and return any possible urls found */
	private static Set<String> scanHttp(String line,URL urlToWatch){
		Set<String> ret = new HashSet<String>();
		//Makes an assumption h will be the first character.
		String toSplit=line.substring(line.indexOf('h'));;
		String[] split=toSplit.split("http");

		//Split up the string according to http
		for(String s : split){
			//Prepend http to look for valid url combinations
			//Valid combinations will always be at the beginning of the split
			s="http" + s;
			//FOund a possible valid url combination, continue
			if(s.contains("https") || line.contains("http:")){
				//SSL URL
				if(s.contains("https")){
					//Start at the beginning of http, and go to the end of the url based on an invalidTermCharacter for a url
					//Append everything it finds, and add to ret
					StringBuffer sb = new StringBuffer();
					sb.append("https");
					for(int i=s.indexOf("https");i<s.length();i++){
						if(invalidTermCharacter(s.charAt(i)))
							break;
						sb.append(s.charAt(i));
					}
					ret.add(sb.toString());
				}//end https if

				//Normal url
				if(s.contains("http:")){
					//Start at the beginning of http, and go to the end of the url based on an invalidTermCharacter for a url
					//Append everything it finds, and add to ret
					StringBuffer sb = new StringBuffer();
					for(int i=s.indexOf("http:");i<s.length();i++){
						if(invalidTermCharacter(s.charAt(i)))
							break;
						sb.append(s.charAt(i));

					}
					ret.add(sb.toString());
				}//end http: if

			}
		}	

		return ret;
	}//end scanHttp

	/* Scan for any css urls */
	private static Set<String> scanCss(String line,URL urlToWatch){
		Set<String> ret = new HashSet<String>();
		//Test for more than one
		int i=line.indexOf(":url(")+1;
		int j=line.lastIndexOf(":url(");

		if(j==i-1){
			i+=":url(".length();
			StringBuffer sb = new StringBuffer();
			//Start after the (
			for(;i<line.length();i++){
				if(line.charAt(i)==')')
					break;
				sb.append(line.charAt(i));
			}
			//Add the host + relative path for new url
			String s=urlToWatch.getHost() + sb.toString();
			ret.add(s);
		}
		//More than one, split the line up by :url( and look for the relative paths, make an assumption that the beginngin characters are:
		//:url(/
		else {
			String[] split=line.split(":url");
			for(String s : split){
				String check=":url" + s;
				if(!check.contains(check + "/"))
					continue;
				StringBuffer sb = new StringBuffer();
				for(int k=0;k<s.length();k++){
					if(s.charAt(i)==')')
						break;
					sb.append(line.charAt(k));
				}
				String add=urlToWatch.getHost() + sb.toString();
				ret.add(add);
			}
		}
		return ret;
	}//end scanCss


	/* Scan for any urls starting with www */
	private static Set<String> scanWww(String line){
		Set<String> ret = new HashSet<String>();
		//Last iteration, no need to make a copy
		//Split up the line looking for valid www. combinations that will lead to a url
		line=line.substring(line.indexOf("www"));
		String[] split=line.split("www");
		//Iterate through split looking for valid combinations
		for(String s : split){
			//Prepend wwww to the string to see if a valid combination exists, it will always be at the beginning
			s="www" + s;
			///www. not at the beginning continue
			if(!s.contains("www."))
				continue;
			StringBuffer sb = new StringBuffer();
			//Iterate through the string until an invalid term character is found,, add the result to ret
			for(int i=0;i<s.length();i++){
				if(invalidTermCharacter(s.charAt(i)))
					break;
				sb.append(s.charAt(i));

			}
			if(new SubUrlMachine().isUrl(sb.toString()))
				ret.add(sb.toString());
		}
		return ret;
	}//end scanWww

	/* Scan for any background style urls */
	private static Set<String> scanBackground(String line){
		Set<String> ret = new HashSet<String>();
		StringBuffer sb = new StringBuffer();
		int i=line.indexOf("background=");
		i+="background=".length();
		i+=2;
		for(;i<line.length();i++){
			if(line.charAt(i)=='\'')
				break;
			sb.append(line.charAt(i));
		}
		ret.add(sb.toString());
		return ret;
	}//end scanBackground

	/* Scan for anythhing with src in the line */
	private static Set<String> scanSource(String line){
		Set<String> ret = new HashSet<String>();
		//Find the src part of the string
		int i=line.indexOf("src=")+1;
		//Look for more than one
		int j=line.lastIndexOf("src");
		//There was only one.
		if(j==i-1){
			StringBuffer sb = new StringBuffer();
			//Skip to the beginning of the url and go to the end quote
			i+=4;

			for(;i<line.length();i++){
				if(line.charAt(i)=='\"' || line.charAt(i)=='\'')
					break;
				sb.append(line.charAt(i));
			}
			ret.add(sb.toString());

		}

		else if(line.contains("src")){
			String[] split=line.split("src");
			//Multiple instances, split up according to src=, find all of them
			for(String s : split){
				//Prepend src to look for avalid combinations
				s="src" + s;
				if(s.contains("src=")){
					//Append to a buffer for the string that contains src="
					StringBuffer sb = new StringBuffer();
					//Check if the string contains src=", start at the quote and go till the next quote for the url
					int l=s.indexOf("src=\"");
					//src=" wasn't found
					if(l<0)
						l=s.indexOf("src=\'");
					//src=' wasn't found either, continue
					if(l<0)
						continue;
					//Relative path assumed
					l=s.indexOf('/');
					if(l < 0)
						break;
					//Go until a terminator character is found, and then add the result to ret
					for(;l<s.length();l++){
						if(s.charAt(l)=='\"' || s.charAt(l)=='\'')
							break;
						sb.append(s.charAt(l));
					}
					ret.add(sb.toString());

				}
			}
		}//end else
		//Check for all caps
		else if(line.contains("SRC")){
			String[] split=line.split("SRC");
			//Multiple instances, split up according to src=, find all of them
			for(String s : split){
				//Prepend src to look for avalid combinations
				s="SRC" + s;
				if(s.contains("SRC=")){
					//Append to a buffer for the string that contains src="
					StringBuffer sb = new StringBuffer();
					//Check if the string contains src=", start at the quote and go till the next quote for the url
					int l=s.indexOf("SRC=\"");
					//src=" wasn't found
					if(l<0)
						l=s.indexOf("SRC=\'");
					//src=' wasn't found either, continue
					if(l<0)
						continue;
					//Relative path assumed
					l=s.indexOf('/');
					if(l < 0)
						break;
					//Go until a terminator character is found, and then add the result to ret
					for(;l<s.length();l++){
						if(s.charAt(l)=='\"' || s.charAt(l)=='\'')
							break;
						sb.append(s.charAt(l));
					}
					ret.add(sb.toString());

				}
			}
		}//end else
		return ret;
	}//end scanSource

	/* Scan for any href links */
	private static Set<String> scanHref(String line){
		Set<String> ret = new HashSet<String>();
		//Find the href part of the string
		int i=line.contains("href=")? line.indexOf("href=")+1 : line.indexOf("HREF=")+1;

		//Look for more than one
		int j=line.contains("href=") ? line.lastIndexOf("href=") : line.lastIndexOf("HREF=");
		//There was more than one
		if(j==i-1){
			//Skip over href-
			i+="href=".length();
			//Loop from the after the beginning quote till the end quote is found, and add the result to ret
			StringBuffer sb = new StringBuffer();
			for(;i<line.length();i++){
				if(line.charAt(i)=='\"' || line.charAt(i)=='\'')
					break;
				sb.append(line.charAt(i));
			}
			ret.add(sb.toString());

		}
		else if(line.contains("href")){
			String[] split=line.split("href=");
			//Multiple instances, split up according to src=, find all of them
			for(String s : split){
				s="href=" + s;
				if(s.contains("href=\"") || s.contains("href='")){
					//Append to a buffer for the string that contains src="
					StringBuffer sb = new StringBuffer();
					//Check if the string contains src=", start at the quote and go till the next quote for the url
					int l=s.indexOf("href=\"");
					//src=" wasn't found
					if(l<0)
						l=s.indexOf("href='");
					//src=' wasn't found ither, keep iterating
					if(l<0)
						continue;
					//Go after the beginning quote
					l=s.indexOf("=")+2;
					//Go till the ending quote
					for(;l<s.length();l++){
						if(s.charAt(l)=='\"' || s.charAt(l)=='\'')
							break;
						sb.append(s.charAt(l));
					}//end for
					ret.add(sb.toString());

				}
			}//end for
		}//end else
		else if(line.contains("HREF")){
			String[] split=line.split("HREF=");
			//Multiple instances, split up according to src=, find all of them
			for(String s : split){
				s="HREF=" + s;
				if(s.contains("HREF=\"") || s.contains("HREF='")){
					//Append to a buffer for the string that contains src="
					StringBuffer sb = new StringBuffer();
					//Check if the string contains src=", start at the quote and go till the next quote for the url
					int l=s.indexOf("HREF=\"");
					//src=" wasn't found
					if(l<0)
						l=s.indexOf("HREF='");
					//src=' wasn't found ither, keep iterating
					if(l<0)
						continue;
					//Go after the beginning quote
					l=s.indexOf("=")+2;
					//Go till the ending quote
					for(;l<s.length();l++){
						if(s.charAt(l)=='\"' || s.charAt(l)=='\'')
							break;
						sb.append(s.charAt(l));
					}//end for
					ret.add(sb.toString());

				}
			}//end for
		}//end else

		return ret;
	}//end scanHref






	public static String getFile(URL url){
		String file=url.getFile();
		if(file.contains("http://"))
			file=getHost(url.toString()) + ".html";
		return file;
	}

	private static Semaphore s1 = new Semaphore(1);
	public  final static String[] URL_ENDINGS={".com",".net",".edu",".gov",".org",".ac",".af",".as",".dz",".cn",".ar",".htm",".do"};

}//end URLManipulator
