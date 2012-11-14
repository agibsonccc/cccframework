package com.ccc.camelcomponents.processors;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.activation.DataHandler;


import org.apache.camel.Endpoint;
import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.camel.Processor;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.util.EntityUtils;

import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.filesystem.PathManipulator;

public class FileProcessor implements Processor {

	/**
	 * This will create an exchange based on the passed in parameters
	 * @param endpoint the end point used to create the exchange
	 * @param files the files to manipulate
	 * @param method the method to use for http requests
	 * @param fileParam the file parameter to use for http requests
	 * @return an exchange initialized with the given parameters
	 */
	public static Exchange getExchange(Endpoint endpoint,List<File> files,String method,boolean attach) {
		Exchange ret=endpoint.createExchange();
		Map<String,Object> parameters = new HashMap<String,Object>();
		if(method!=null)
			parameters.put(METHOD,method);
		if(files!=null && !files.isEmpty())
			parameters.put(FILE_KEY,files);
		parameters.put(ATTACH,attach);
		Message in=ret.getIn();
		in.setBody(parameters, Map.class);
		return ret;
	}//end getExchange

	/**
	 * This will create an exchange initialized for downloads
	 * @param endpoint the end point to use
	 * @return the exchange initialized for downloads
	 */
	public static Exchange getExchange(Endpoint endpoint,boolean attach) {
		return getExchange(endpoint,null,"get",attach);
	}


	/**
	 * Create an exchange with the given map
	 * @param endpoint the end point used to create the exchange
	 * @param parameters the parameters to use
	 * @return a created exchange
	 */
	public static Exchange getExchange(Endpoint endpoint,Map<String,Object> parameters) {
		Exchange ret=endpoint.createExchange();
		Message in=ret.getIn();
		in.setBody(parameters, Map.class);
		return ret;
	}

	public HttpUriRequest createRequest(Exchange exchange) {
		String uri=getUri(exchange);
		Map<String,Object> params=exchange.getIn(Map.class);
		if(params==null || params.isEmpty())
			return null;
		//http method for the uri
		String method=(String) params.get(METHOD);
		if(method==null) method="post";
		List<File> files=(List<File>) params.get(FILE_KEY);
		//post or putting nothing get has to be a download
		if((files==null || files.isEmpty()) && !method.equalsIgnoreCase("get")) return null;
		String fileParam=(String) params.get(FILE_PARAM_TYPE);
		//default parameter
		if(fileParam==null) fileParam="file";
		//mime types to handle different types of files
		Map<String,String> mimeTypes=FileMoverUtil.getMimeTypes();

		
		if(method.equalsIgnoreCase("post")) {
			HttpPost        post   = new HttpPost( uri );
			MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

			for(File f : files) {
				String fileFormat=PathManipulator.getFormat(f);
				String mimeType=mimeTypes.get(fileFormat);
				// For File parameters
				entity.addPart( fileParam, new FileBody((( File ) f ), mimeType ));
				// Here we go!

			}
			post.setEntity( entity );
			return  post;
			//String response = EntityUtils.toString( client.execute( post ).getEntity(), "UTF-8" );

		}

		else if(method.equalsIgnoreCase("put")) {
			HttpPut        post   = new HttpPut( uri );
			MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

			for(File f : files) {
				String fileFormat=PathManipulator.getFormat(f);
				String mimeType=mimeTypes.get(fileFormat);
				// For File parameters
				entity.addPart( fileParam, new FileBody((( File ) f ), mimeType ));
				// Here we go!

			}
			post.setEntity( entity );
			return  post;
			//String response = EntityUtils.toString( client.execute( post ).getEntity(), "UTF-8" );
		}

		else if(method.equalsIgnoreCase("get")) {
			HttpGet get = new HttpGet(uri);
			return  get;

		}
		return null;
		/*For usual String parameters
		entity.addPart( paramName, new StringBody( paramValue.toString(), "text/plain",
			Charset.forName( "UTF-8" )));*/	

		//client.getConnectionManager().shutdown();
	}
	
	@Override
	public void process(Exchange exchange) throws Exception {
		String uri=getUri(exchange);
		DefaultHttpClient client = new DefaultHttpClient();
		client.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, HttpVersion.HTTP_1_1);
		HttpUriRequest request=(HttpUriRequest) createRequest(exchange);
		if(request==null) return;
		HttpResponse response=client.execute(request);
		Map<String,Object> params=exchange.getIn(Map.class);
		
		 if(request instanceof HttpGet) {
			
			HttpEntity entity=response.getEntity();
			Message out=exchange.getOut();
			Boolean attach=(Boolean) params.get(ATTACH);
			if(attach==null) attach=false;
			URI u = new URI(uri);
			URL u1 =u.toURL();
			String fileName=u1.getFile();

			if(attach) out.addAttachment(fileName, new DataHandler(u1));
			else out.setBody(entity.getContent());

		}

		/*For usual String parameters
		entity.addPart( paramName, new StringBody( paramValue.toString(), "text/plain",
			Charset.forName( "UTF-8" )));*/	

		client.getConnectionManager().shutdown();
	}


	protected String getUri(Exchange exchange) {
		String uri=exchange.getFromEndpoint().getEndpointUri();
		//http client 4 uri..
		if(uri.contains("https4")) uri=uri.replace("https4","https");
		if(uri.contains("http4")) uri=uri.replace("http4","http");
		return uri;
	}
	/**
	 * content length header
	 */
	public final static String CONTENT_LENGTH="contentlength";

	/**
	 * This will determine whether to overwrite or not
	 */
	public static final String OVERWRITE="overwrite";

	/**
	 * Used for retrieving uploads
	 */
	public final static String FILE_KEY="file";

	public final static String FILE_PARAM_TYPE="fileparam";
	/**
	 * Method for http execution
	 */
	public final static String METHOD="method";

	/**
	 * Whether to attach a file or not for the body
	 * (only for use with downloads)
	 */
	public final static String ATTACH="attach";
}

