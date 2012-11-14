package com.ccc.camelcomponents.ical;

import java.util.HashMap;
import java.util.Map;

import org.apache.camel.Component;
import org.apache.camel.Consumer;
import org.apache.camel.Processor;
import org.apache.camel.Producer;
import org.apache.camel.impl.DefaultEndpoint;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.util.Assert;

import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.clevmail.mailheaders.MailHeaders;
import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.storage.MailConstants;
import com.ccc.util.web.URLManipulator;
/**
 * This represents an ical end point for sending and receiving calendar invites and updates either
 * at a specified url or an email address
 * @author Adam Gibson
 *
 */
public class ICalEndPoint extends DefaultEndpoint implements MailConstants {




	public ICalEndPoint(ICalConfig config,String endPointUri,Component component) {
		super(endPointUri,component);
		this.config=config;
	}

	private void ensureAuthReady() {
		if(userName!=null)
			config.getMailHeaders().getHeaders().put(USER_NAME, userName);
		if(password!=null)
			config.getMailHeaders().getHeaders().put(PASSWORD, password);
		config.getMailHeaders().getHeaders().put(IS_SSL,ssl==null?"false" : ssl);
	}

	@Override
	public Consumer createConsumer(Processor processor) throws Exception {
		
		ensureAuthReady();
		return new ICalConsumer(config,this,processor);
	}

	@Override
	public Producer createProducer() throws Exception {
	
		ensureAuthReady();

		return new ICalProducer(this,config);
	}


	/**
	 * The url of this end point without the compponent and the 
	 * end parameters as url encoded rather than json
	 * @return null if the params are null or empty, or
	 * the straight url with the json encoded parameters as url encoded
	 * @throws JSONException 
	 */
	public String urlWithParams() throws JSONException {
		if(params==null || params.isEmpty())
			return null;
		else {
			String encodedParams=ICalUtils.urlEncodedParams(params);
			Assert.notNull(encodedParams);
			//obtain index of params to isolate
			int paramsIndex=config.getAddress().indexOf("params");
			//end brace of json string: encoded
			int endBrace=config.getAddress().indexOf("%7D");
			Assert.isTrue(!(endBrace < 0));
			//strip out params
			String sub=config.getAddress().substring(paramsIndex,endBrace);
			//address without parameters
			String withoutParams=config.getAddress().replace(sub,"");
			//any duplicate ampersands left over from stripping out params
			withoutParams=withoutParams.replace("&&","&");
			//auto converted in to :// after component uri
			int firstColon=withoutParams.indexOf("://");
			//first colon wasn't found, wasn't part of http:
			if(firstColon < 0 && firstColon > 5 && firstColon > encodedParams.indexOf("http"))
				firstColon=withoutParams.indexOf(":");
			StringBuffer sb = new StringBuffer();
			//without the ical:// at beginning
			String mid=withoutParams.substring(firstColon+3);

			//strip out beginning //
			if(mid.indexOf("//")<2)
				mid=mid.substring(2);
			sb.append(mid);
			sb.append("&");
			sb.append(encodedParams);
			return sb.toString();
		}
	}//end urlWithParams





	public ICalConfig getConfig() {
		return config;
	}

	public void setConfig(ICalConfig config) {
		this.config = config;
	}

	/**
	 * This will return  map of the json encoded parameters
	 * @return a map of the parameters in key : value format, or null
	 * if the params string is null or empty
	 * @throws JSONException if the parameter isn't a valid json 
	 * string
	 */
	public Map<String,String> params() throws JSONException {
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

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}



	public String getSsl() {
		return ssl;
	}

	public void setSsl(String ssl) {
		this.ssl = ssl;
	}

	@Override
	public boolean isSingleton() {
		return false;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String method) {
		this.method = method;
	}

	@Override
	protected String createEndpointUri() {
		return "ical:" + address;
	}


	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


	public String getParams() {
		return params;
	}

	public void setParams(String params) {
		this.params = params;
	}


	
	//method for http
	private String method;

	//user name to login
	private String userName;
	//password to login with
	private String password;
	/**
	 * Extra parameters are placed here: encoded in JSON
	 * Each extra parameter will be appended to the end of an 
	 * http url
	 */
	private String params;


	private String address;

	private String ssl;

	
	private ICalConfig config;
}//end ICalEndPoint
