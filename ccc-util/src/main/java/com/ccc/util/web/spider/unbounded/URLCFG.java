/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;
import java.util.HashMap;



import java.util.Map;

import com.ccc.util.filesystem.PathManipulator;
import com.ccc.util.web.URLManipulator;

/**
 * This is an url context free grammar that checks for valid urls.
 * @author Adam Gibson
 *
 */
public class URLCFG {


	public URLCFG(){
		initHost();
		initRequestParams();
	}
	/**
	 * This takes in a valid url, and sees if the file directory structure is valid, and if so,
	 * will return true.
	 * @param toCheck the valid url to check
	 * @return whether the url directory structure is valid.
	 */
	private boolean isValidUrlDirectory(String toCheck){
		//System.out.println("toCheck: " + toCheck);
		if(toCheck.contains("https")){
			int i=toCheck.indexOf("https");
			String sub=null;

			try{
				sub=toCheck.substring(i+7);
			}catch(StringIndexOutOfBoundsException e){

			}
			return isValidUrlDirectory(sub);
		}
		else if(toCheck.contains("http")){
			int i=toCheck.indexOf("http");
			String sub=null;

			try{
				sub=toCheck.substring(i+7);
				
			}catch(StringIndexOutOfBoundsException e){

			}
			return isValidUrlDirectory(sub);

		}
		else {
			int i=toCheck.indexOf('/');
			try {
				if(toCheck.charAt(i)==' ')
					return true;
			}catch(StringIndexOutOfBoundsException e){
				return true;
			}
			try {
				StringBuffer sb = new StringBuffer();
				for(int j=i+1;j<toCheck.length()-1;j++){
					if(sb.charAt(j)=='/')
						return isValidUrlDirectory(sb.toString());
					sb.append(toCheck.charAt(j));
				}
			}catch(StringIndexOutOfBoundsException e)
			{return true;}
		}
		return true;
	}
	public boolean isUrl(String toCheck){
		int i=toCheck.indexOf("http");
		if(i < 0)
			return false;

		toCheck=toCheck.substring(i,toCheck.length());


		//Secure url.
		if(toCheck.contains("https")){
			String init=host.get('I');

			if(toCheck.contains("www.")){
				init+=host.get('W');
				init+=PathManipulator.getHost(toCheck);
				int urlLength=init.length();
				//No directories, after, valid url.
				if(toCheck.charAt(urlLength)=='/')
					if(toCheck.charAt(urlLength)==' ')
						return true;
			}
			else {
				init+=PathManipulator.getHost(toCheck);
				int urlLength=init.length();
				//No directories, after, valid url.
				if(toCheck.charAt(urlLength)=='/')
					if(toCheck.charAt(urlLength)==' ')
						return true;

				if(isValidUrlDirectory(toCheck) && !toCheck.contains("?"))
					return true;
				//Check, for the request values, if they're valid return true.
				Map<String,String> requestVals=new URLManipulator().getRequestNames(toCheck);
				return true;

			}
		}
		else if(toCheck.contains("http")){
			String init=host.get('H');
			if(toCheck.contains("www.")){
				init+=host.get('W');
				int urlLength=init.length();
				try {
				//No directories, after, valid url.
				if(toCheck.charAt(urlLength)=='/')
					if(toCheck.charAt(urlLength)==' ')
						return true;
				}catch(StringIndexOutOfBoundsException e)
				{return false;}
				//No request params, and valid directory structure.
				if(isValidUrlDirectory(toCheck) && !toCheck.contains("?"))
					return true;

				//Check, for the request values, if they're valid return true.
				Map<String,String> requestVals=new URLManipulator().getRequestNames(toCheck);
				return true;
			}
			else {
				init+=PathManipulator.getHost(toCheck);
				int urlLength=init.length();
				try {
				//No directories, after, valid url.
				if(toCheck.charAt(urlLength)=='/')
				
					if(toCheck.charAt(urlLength)==' ')
						return true;
				
				}
				catch(StringIndexOutOfBoundsException e)
				{return true;}
				}
				//No request params, and valid directory structure.
				if(isValidUrlDirectory(toCheck) && !toCheck.contains("?"))
					return true;

				//Check, for the request values, if they're valid return true.
				Map<String,String> requestVals=new URLManipulator().getRequestNames(toCheck);
				return true;
			}
		

		return false;
	}

	


	private void initHost(){
		host = new HashMap<Character,String>(3);
		host.put('H',"http://");
		host.put('I',"https//");
		host.put('W',"www.");
		host.put('S',"HWXE|WXE|HXE|IXE");
	}
	private void initRequestParams(){
		requestParams = new HashMap<Character,String>();
		requestParams.put('S',"AWB|AWBD");
		requestParams.put('A',"?");
		requestParams.put('C',"WC|W");
		requestParams.put('D',"&CB");
		requestParams.put('W',"w|wW");
	}


	private Map<Character,String> host;
	private char[] w={'~','`','1','!','2','@','3','#','4','$','5','%','6','^','7','&','8','*','9',
			'(','0',')','-','_','+','=','q','w','e','Q','W','E','R','r','t','T','y','Y','u','U','i','I','o',
			'O','p','P','{','}','[' , ']','|','\\','a','A','s','S','D','d','f','F','g','G','h','H','j','k','l',
			'J','K','L',';',':','"','\'','z','Z','x','Z','X','c','C','v','V','b','B','n','m','N','M'};
	public  final static String[] E={".com",".net",".edu",".gov",".org",".ac",".af",".as",".dz",".cn",".ar",".htm",".do"};

	private Map<Character,String> requestParams;

}
