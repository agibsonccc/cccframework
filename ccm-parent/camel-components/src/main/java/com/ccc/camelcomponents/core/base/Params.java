package com.ccc.camelcomponents.core.base;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.springframework.util.Assert;

public class Params {


	public Params(String baseUrl) throws MalformedURLException {
		Assert.notNull(baseUrl);
		Assert.hasLength(baseUrl);
		//verify valid url
		URL u = new URL(baseUrl);
		this.baseUrl=baseUrl;
		//no parameters
		Assert.isTrue(!baseUrl.contains("?"),"No url parameters allowed");
	}
	/**
	 * This will return with these parameters.
	 * @return a url wth these parameters
	 * @throws UnsupportedEncodingException
	 */
	public String getUrl() throws UnsupportedEncodingException {
		StringBuffer sb = new StringBuffer();
		sb.append(baseUrl);
		sb.append("?");
		Iterator<String> keys=parameters.keySet().iterator();
		while(keys.hasNext()) {
			String s=keys.next();
			sb.append(s);
			sb.append("=");
			String val=parameters.get(s);
			sb.append(val);
			if(keys.hasNext())
				sb.append("&");
		}
		String sbString=sb.toString();
		return URLEncoder.encode(sbString, "UTF-8");
	}//end getUrl
	
	
	public void addParam(String key,String value) {
		parameters.put(key, value);
	}
	
	

	private String baseUrl;
	
	
	private Map<String,String> parameters = new HashMap<String,String>();
}
