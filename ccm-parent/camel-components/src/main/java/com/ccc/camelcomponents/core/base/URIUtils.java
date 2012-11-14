package com.ccc.camelcomponents.core.base;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import com.ccc.util.web.URLManipulator;

public class URIUtils {
	/**
	 * This will return url encoded parameters that are appended to the end of an ical uri
	 * @return url encoded parameters for a camel uri, or null if parameters are null 
	 * or empty
	 * @throws JSONException if the params object isn't valid json
	 */
	public static String urlEncodedParams(String params) throws JSONException {
		if(params==null || params.isEmpty())
			return null;
		else {
			JSONObject object = new JSONObject(params);
			return URLManipulator.jsonToUrlEncode(object);
		}
	}//end urlEncodedParams
	
	

	/**
	 * This will return the uri of this end point with only 
	 * the params in the params json string appended as uri 
	 * encoded
	 * @param params the json encoded params
	 * @param address  the camel uri
	 * @param componentPrefix the prefix for the component uri
	 * @return the uri of this end point with only 
	 * the params in the params json string appended as uri
	 *  
	 * @throws JSONException
	 */
	public static String urlWithOnlyParams(String params,String address,String componentPrefix) throws JSONException {
		if(params==null || params.isEmpty())
			return null;
		else {
			int questionIdx=address.indexOf('?');
			int indexOfEndOfIcal=address.indexOf(componentPrefix + "://") + (componentPrefix + "://").length();
			String baseUrl=address.substring(indexOfEndOfIcal,questionIdx);

			String encodedParams=urlEncodedParams(params);
			Assert.notNull(encodedParams);
			StringBuffer sb = new StringBuffer();
			sb.append(baseUrl);
			sb.append("?");
			sb.append(encodedParams);
			return sb.toString();
		}
	}//end urlWithOnlyParams
	
	
	/**
	 * This will return  map of the json encoded parameters
	 * @return a map of the parameters in key : value format, or null
	 * if the params string is null or empty
	 * @throws JSONException if the parameter isn't a valid json 
	 * string
	 */
	public  static Map<String,String> params(String params) throws JSONException {
		if(params ==null || params.isEmpty())
			return null;
		else {
			JSONObject object = new JSONObject(params);
			Map<String,String> ret = new HashMap<String,String>();
			JSONArray array=object.names();
			int length=array.length();

			for(int i=0;i<length;i++) {
				String name=array.getString(i);
				String objectToString=object.get(name).toString();
				ret.put(name,objectToString);

			}
			return ret;
		}
	}//end params
	
}
