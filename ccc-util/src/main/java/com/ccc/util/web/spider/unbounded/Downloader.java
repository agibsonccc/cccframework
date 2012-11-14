package com.ccc.util.web.spider.unbounded;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.filesystem.PathManipulator;



public class Downloader {
	public static boolean download(URL toDownload, String destination,String query){
		Location l = new Location();
		l.setCurrentLocation(toDownload.toString());
		urlToWatch=toDownload;
		List<String> urlContents = null;
		try {urlContents = readURL();} 
		catch (UnknownHostException e1) 
		{e1.printStackTrace();} 
		catch (SocketException e1) 
		{e1.printStackTrace();} 
		catch (FileNotFoundException e1) 
		{e1.printStackTrace();}
		Set<String> urls = new HashSet<String>();
		for(String s : urlContents){
			System.out.println("S: " + s);
			Set<String> add=scanLine(s);
			if(add!=null && !add.isEmpty())
				urls.addAll(add);
		}
		for(String s2 : urls){
			if(s2.contains(query)) {
				try {
					String host=urlToWatch.toString();
					//Get it down to directory only, the thing we want to download will not be this actual  file but a subdirectory of this url.
					host=host.substring(0,host.lastIndexOf('/'));
					//Add a trailing slash
					if(host.charAt(host.length()-1)!='/')
						host+='/';
					//Get rid of any slashes at the beginning to ensure the trailing slash from the host and the beginning one don't conflict
					if(s2.charAt(0)=='/')
						s2=s2.substring(1);
					if(!s2.contains("http") && s2.charAt(0)!='w'){
						if(s2.charAt(0)=='.')
							s2=s2.substring(s2.indexOf('/'));
						s2=host + s2;
					}
					if(s2.charAt(0)=='w')
						s2="http://" + s2;

					URL u = new URL(s2);
					
					//System.out.println("Path: " + u.getPath());
					System.out.println("Content: " + u.getContent());
					System.out.println("File: " + u.getFile());
					download(u,destination);
				} 
				catch (MalformedURLException e) 
				{e.printStackTrace();} 
				catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}

		return true;
	}

	private static void download(URL toDownload,String destination){
		try {
			URLConnection conn1=null;
			try{conn1=toDownload.openConnection();}
			catch(IOException e)
			{e.printStackTrace();}


			String file=toDownload.getFile();
			File destDir = new File(destination);
			if(!destDir.exists() || !destDir.isDirectory())
				throw new IllegalArgumentException("Given destination wasn't a directory or didn't exist.");
			if(toDownload.getFile().length() < 1)
				file=PathManipulator.getHost(toDownload.toString());
			File dest = new File(destination  + file);
			System.out.println("Dest: " + dest.getAbsolutePath());
			do {
				try {
					FileMoverUtil.createFile(dest,false);
				}catch(Exception e){
					dest.mkdirs();
					try{FileMoverUtil.createFile(dest,false);}
					catch(IOException e1){
						try {
							Thread.sleep(200);
						} catch (InterruptedException e2) {}
						continue;
					}

				}
			}while(!dest.exists());
			boolean downloaded=false;
			do {
				BufferedInputStream bis = null;
				BufferedOutputStream bos = null;
				try {
					System.out.println("Download..." + dest.getAbsolutePath());
					bis = new BufferedInputStream(conn1 .getInputStream());
					bos = new BufferedOutputStream(new FileOutputStream(dest));
					FileMoverUtil.copyInputStream(bis, bos);
					downloaded=true;
					System.out.println("Copied: "   + dest.getAbsolutePath());
				}
				catch(Exception e){
					e.printStackTrace();
					if(e.toString().contains("files"))
						continue;
				}
			}while(!downloaded);
		}
		finally {}

	} 





	/**
	 * 
	 * @param line the line to check and return a set of possible urls for
	 * @return the set of possible urls in the given line
	 * @throws IllegalArgumentException if line is empty or null
	 */
	protected static Set<String> scanLine(String line){
		if(line==null || line.length() < 1)
			return null;
		//No URL contents to break apart
		if(!(line.contains("http") || line.contains("src=\"")|| line.contains("href=\"") || line.contains("www") || !line.contains(":url(")))
			return null;

		else {
			if(line.contains("</html>")){
				//System.out.println("Arrived.");
			}
			if(line.contains("pdf")){
				System.out.println("Here");
			}
			//Build a set based on scanning the line for various urls,
			//Scan for relative URLS in CSS, the beginnings of URLs in www, http, or any src or href links
			Set<String> ret = new HashSet<String>();
			if(line.contains("src") || line.contains("SRC")){
				//Find the src part of the string
				int i=line.indexOf("src=")+1;
				//Look for more than one
				int j=line.lastIndexOf("src");
				//There was only one.
				if(j==i-1){
					StringBuffer sb = new StringBuffer();
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


			}//end src if

			//Check for href=
			if(line.contains("href") || line.contains("HREF")){
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
			}//end href if


			if(line.contains("http")){
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
			}//end http if

			//Relative url without http could have been found
			if(line.contains("www")){
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

			}
			//Capture css urls
			if(line.contains(":url(")){
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
			}//end css if
			if(line.contains("background=")){
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
			}
			//Remove invalid strings, if any are left
			if(!ret.isEmpty())
				ret=removeInvalidStrings(ret);

			return ret;
		}

	}//end scanLine


	/* Invalid termination character for urls */
	private static boolean invalidTermCharacter(char c){
		return c=='<' || c=='>' || c=='"' || c=='\'' || c=='%' || c=='&' || c=='*' ||c=='@' || c=='#' || c==')' || c=='(' || c=='{' || c=='}' || c==' ';
	}


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
	 * This reads from a  url, and returns the contents in a list of strings
	 * @return the url contents in a list of strings
	 * @throws UnknownHostException if there is no host to connect to
	 * @throws SocketException if the connection is reset
	 * @throws FileNotFoundException if the file wasn't found on the server
	 */
	private static List<String> readURL() throws UnknownHostException,SocketException,FileNotFoundException {
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
	private static URLConnection conn;
	protected static Semaphore s1 = new Semaphore(1);
	private static URL urlToWatch;
}
