package com.ccc.camelcomponents.processors;

import java.io.File;
import java.util.List;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.exception.OAuthCommunicationException;
import oauth.signpost.exception.OAuthExpectationFailedException;
import oauth.signpost.exception.OAuthMessageSignerException;

import org.apache.camel.Exchange;
import org.apache.camel.Message;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;

import com.ccc.util.filesystem.FileMoverUtil;
import com.ccc.util.filesystem.PathManipulator;
/**
 * This is a file processor that handles oauth authentication
 * @author Adam Gibson
 *
 */
public class OAuth1FileProcessor extends FileProcessor {

	public OAuth1FileProcessor(OAuthConsumer consumer) {
		this.consumer=consumer;
	}

	@Override
	public void process(Exchange exchange) throws Exception {
		super.process(exchange);
	}

	
	
	@Override
	public HttpUriRequest createRequest(Exchange exchange) {
		String uri=getUri(exchange);
		Message in=exchange.getIn();
		Map<String,Object> params=(Map<String,Object>)in.getBody();

		

		if(params==null || params.isEmpty() || consumer==null)
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
			try {
				
				 consumer.sign(post);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
			return post;
			//String response = EntityUtils.toString( client.execute( post ).getEntity(), "UTF-8" );

		}

		else if(method.equalsIgnoreCase("put")) {
			HttpPut        post   = new HttpPut( uri );
			MultipartEntity entity = new MultipartEntity( HttpMultipartMode.BROWSER_COMPATIBLE );

			for(File f : files) {
				String fileFormat=PathManipulator.getFormat(f);
				fileFormat=fileFormat.replace(".","");
				String mimeType=mimeTypes.get(fileFormat);
				// For File parameters
				entity.addPart( fileParam, new FileBody((( File ) f ), mimeType ));
				// Here we go!

			}
			
			post.setEntity( entity );
			
			try {
				
				 consumer.sign(post);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
			return post;
			//String response = EntityUtils.toString( client.execute( post ).getEntity(), "UTF-8" );
		}

		else if(method.equalsIgnoreCase("get")) {
			HttpGet get = new HttpGet(uri);
			
			try {
				
				 consumer.sign(get);
			} catch (OAuthMessageSignerException e) {
				e.printStackTrace();
			} catch (OAuthExpectationFailedException e) {
				e.printStackTrace();
			} catch (OAuthCommunicationException e) {
				e.printStackTrace();
			}
			
			return get;

		}
		return null;
		/*For usual String parameters
		entity.addPart( paramName, new StringBody( paramValue.toString(), "text/plain",
			Charset.forName( "UTF-8" )));*/	

		//client.getConnectionManager().shutdown();
	}

	
	
	
	private OAuthConsumer consumer;
}
