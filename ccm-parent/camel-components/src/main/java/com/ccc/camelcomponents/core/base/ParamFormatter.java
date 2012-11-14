package com.ccc.camelcomponents.core.base;

import java.util.List;
import java.util.Map;

import com.ccc.oauth.apimanagement.model.Service;

import edu.emory.mathcs.backport.java.util.Collections;
/**
 * This is a param formatter that will handle the replacing of uri parameters depending on the service
 * @author Adam Gibson
 *
 */
public class ParamFormatter {
	/**
	 * This will format the passed in uri with the given parameters depending on the service
	 * @param service the service to replace values for
	 * @param replace the uri to replace values for
	 * @param values the target values to replace
	 * @return the new string with the replaced values
	 */
	public static String replace(Service service,String replace,Map<String,String> values) {
			List<String> params=paramsFor(service);
			if(params==null) return replace;
			
			else {
				for(String s : params) {
					String replaceWith=values.get(s);
					if(replaceWith==null) continue;
					replace=replace.replace(s, replaceWith);
				}
				return replace;
			}
	}
	
	
	/**
	 * This will return the list of params for a given service to replace
	 * @param service the service to check for
	 * @return the list of strings to look for when replacing
	 */
	public static List<String> paramsFor(Service service) {
		if(service.getServiceProvider().getName().toLowerCase().contains("facebook")) {
			return Collections.singletonList("{id}");
		}
		else if(service.getServiceProvider().getName().toLowerCase().contains("dropbox"))
			return Collections.singletonList("{path}");
		return null;
	}
	
	public static String FACEBOOKID="{id}";
	
}//end ParamFormatter
