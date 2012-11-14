package com.ccc.mail.analytics.clickthrough;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.apache.http.HttpResponse;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.mailinglist.mailclient.MailingListMailClient;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.MessageUrlTracking;
import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.mail.mailinglist.model.UrlTracking;
import com.ccc.mail.mailinglist.services.impl.UrlTrackingService;
/**
 * This is a mail client that knows how to replace urls with shortened urls.
 * It will go through all of the content, and leveraging a REST endpoint specified by the user
 * will go through and shorten every url. The expected output however is json for the variable
 * @author Adam Gibson
 *
 */
public class ClickThroughMailClient extends MailingListMailClient {


	/* replace content of links with shortened urls wherever a url is mentioned */
	private void adjustContent(Map<String,String> headers) {
		String content=headers.get(CONTENT);
		String mailingListAddress=headers.get("mailingListEmail");
		UniqueMessage message=null;
		if(mailingListAddress!=null) {
			message = new UniqueMessage();
			List<MailingList> lists=getMailingListService().listsWithEmail(mailingListAddress);
			MailingList list=lists.get(0);
			message.setMaiingList(list);
			message.setSubsAtSent(list.getSubscribers().size());
			headers.put(MailClient.FROM_ADDRESS,mailingListAddress);
		}
		if(content!=null) {
			Pattern pattern=Pattern.compile(regex);				
			Matcher matcher=pattern.matcher(content);
			//loop for all links in email content
			while(matcher.find()) {
				//the next link that was found
				String current=matcher.group();

				//path variable or request parameter?
				String url= path ? idUrl + pathName + current: idUrl + "?" + requestParamName + "=" +  current;
				//url=url.replace("//","/");
				//get request
				if(methodRequest.toLowerCase().equals("get")) {
					Assert.notNull(requestParamName,"Request param name not allowed to be null");
					HttpGet get = new HttpGet(url);
					try {
						content=replaceContent(content,get,current);
					} catch (ParseException e) {
						log.error("Error executing http get when replacuing url ",e);

					}
					//track urls
					if(message!=null) {
						MessageUrlTracking tracker = new MessageUrlTracking();
						tracker.setUniqueMessage(message);
						UrlTracking tracking =urlTrackingService.trackerWithUrl(current);
						if(tracking==null) {
							tracking = new UrlTracking();
							tracking.setLongUrl(current);
							int indexOfLastSlash=content.lastIndexOf('/');
							String id=content.substring(indexOfLastSlash+1);
							tracking.setUrlId(id);
						}
						tracker.setUrlTracking(tracking);
						Assert.isTrue(urlTrackingService.saveMessageUrlTracking(tracker),"Couldn't save url tracker");
					}
				}
				//post request
				else if(methodRequest.toLowerCase().equals("post")) {
					HttpPost post = new HttpPost(url);
					post.setHeader("Accept", "application/json");
					post.setHeader("Content-type", "application/json");
					try {
						Map<String,String> wrap =Collections.singletonMap("id", current);
						DefaultHttpClient client = new DefaultHttpClient();

						JSONObject obj = new JSONObject(wrap);
						StringEntity ent = new StringEntity(obj.toString());
						post.setEntity(ent);
						HttpResponse resp=client.execute(post);
						if(resp.getStatusLine().getStatusCode() >=400) {
							throw new IOException("Invalid response from server: " +resp.getStatusLine());
						}
						JSONObject obj2 = new JSONObject(EntityUtils.toString(resp.getEntity()));

						if(shortenedUrl.endsWith("/"))
							shortenedUrl=shortenedUrl.substring(0,shortenedUrl.length()-1);
						content=content.replace(current,shortenedUrl + "/" + obj2.getString(idVariable));
					} catch (ParseException e) {
						log.error("Error executing http post when replacuing url ",e);

					} catch (UnsupportedEncodingException e) {
						log.error("Error executing http post when replacuing url ",e);

					} catch (ClientProtocolException e) {
						log.error("Error executing http post when replacuing url ",e);

					} catch (IOException e) {
						log.error("Error executing http post when replacuing url ",e);

					} catch (JSONException e) {
						log.error("Error executing http post when replacuing url ",e);

					}
				}
				//put request
				else if(methodRequest.toLowerCase().equals("put")) {
					HttpPut put = new HttpPut(url);
					try {
						content=replaceContent(content,put,current);
					} catch (ParseException e) {
						log.error("Error executing http put when replacuing url ",e);

					}
				}
				else return;
			}

			headers.put(CONTENT,content);
		}
	}

	private String replaceContent(String original,HttpUriRequest request,String replaceWith) {
		String id=executeRequest(request);
		Assert.notNull(shortenedUrl,"Shortened url can't be null!");
		if(!shortenedUrl.endsWith("/"))
			shortenedUrl+="/";
		//generate shortened url with appropriate id from external http request
		String newUrl=shortenedUrl + id;
		//replace the original string with the new shortened url
		return original.replace(replaceWith,newUrl);
	}

	
	/* execute http request to get back id for shortened url */
	private String executeRequest(HttpUriRequest request) {
		DefaultHttpClient client = new DefaultHttpClient();

		String id=null;
		try {
			HttpResponse resp=client.execute(request);
			if(resp.getStatusLine().getStatusCode() >=400) {
				throw new IOException("Invalid response from server");
			}
			JSONObject obj = new JSONObject(EntityUtils.toString(resp.getEntity()));
			id=obj.getString(idVariable);

		} catch (ClientProtocolException e) {
			log.error("Error executing http get when replacuing url ",e);
		} catch (IOException e) {
			log.error("Error executing http get when replacuing url ",e);

		} catch (ParseException e) {
			log.error("Error executing http get when replacuing url ",e);

		} catch (JSONException e) {
			log.error("Error executing http get when replacing url ",e);

		}
		finally {
			client.getConnectionManager().shutdown();

		}
		return id;

	}

	@Override
	public boolean sendToMailingList(Map<String, String> headers,
			MailingList list, boolean isHtml) {
		adjustContent(headers);
		return super.sendToMailingList(headers, list, isHtml);
	}

	@Override
	public boolean sendToMailingListWithAttachments(
			Map<String, String> headers, MailingList list, File[] attachments,
			boolean isHtml) {
		adjustContent(headers);
		return super.sendToMailingListWithAttachments(headers, list, attachments,
				isHtml);
	}

	@Override
	public void sendMailWithServer(Server s, Map<String, String> headers,
			Message m) throws MessagingException {
		adjustContent(headers);
		super.sendMailWithServer(s, headers, m);
	}

	@Override
	public boolean sendStartTlsMail(Map<String, String> headers, boolean isHtml) {
		adjustContent(headers);
		return super.sendStartTlsMail(headers, isHtml);
	}

	@Override
	public boolean sendMailWithAttachments(Map<String, String> headers,
			File[] toAttach, boolean isHtml) throws AddressException,
			MessagingException {
		adjustContent(headers);
		return super.sendMailWithAttachments(headers, toAttach, isHtml);
	}

	@Override
	public boolean sendMail(Map<String, String> headers, boolean isHtml)
			throws AddressException, MessagingException {
		adjustContent(headers);
		return super.sendMail(headers, isHtml);
	}

	@Override
	public boolean sendMessageToServer(Map<String, String> headers, Server s,
			Message m) {
		adjustContent(headers);
		return super.sendMessageToServer(headers, s, m);
	}

	@Override
	public boolean sendSSLMail(Map<String, String> headers, boolean isHtml)
			throws AddressException, MessagingException {
		adjustContent(headers);
		return super.sendSSLMail(headers, isHtml);
	}

	@Override
	public boolean sendMailWithAttachments(Map<String, String> headers,
			File[] toAttach, Server s, boolean isHtml) throws AddressException,
			MessagingException {
		adjustContent(headers);
		return super.sendMailWithAttachments(headers, toAttach, s, isHtml);
	}

	public String getIdUrl() {
		return idUrl;
	}

	public void setIdUrl(String idUrl) {
		this.idUrl = idUrl;
	}

	public String getMethodRequest() {
		return methodRequest;
	}

	public void setMethodRequest(String methodRequest) {
		this.methodRequest = methodRequest;
	}

	public boolean isPath() {
		return path;
	}

	public void setPath(boolean isPath) {
		this.path = isPath;
	}

	public String getRequestParamName() {
		return requestParamName;
	}

	public void setRequestParamName(String requestParamName) {
		this.requestParamName = requestParamName;
	}



	public String getPathName() {
		return pathName;
	}

	public void setPathName(String pathName) {
		this.pathName = pathName;
	}



	public String getIdVariable() {
		return idVariable;
	}

	public void setIdVariable(String idVariable) {
		this.idVariable = idVariable;
	}


	public String getShortenedUrl() {
		return shortenedUrl;
	}

	public void setShortenedUrl(String shortenedUrl) {
		this.shortenedUrl = shortenedUrl;
	}

	@Autowired
	private UrlTrackingService urlTrackingService;
	/**
	 * The shortened url to replace content with
	 */
	private String shortenedUrl;

	/**
	 * The variable used to extract results for an id 
	 * to append to the shortened url
	 */
	private String idVariable;
	/**
	 * Optional: the path name for the request of an id
	 * for a rest end point
	 */
	private String pathName;
	/**
	 * Optional: the request parameter for the request of an id
	 * for a shortened url
	 */
	private String requestParamName;

	private boolean path=false;
	/**
	 * The requesting end point to get ids from
	 * for shortened urls
	 * 
	 */
	private String idUrl;
	/**
	 * get put, or post
	 */
	private String methodRequest;

	private static Logger log=LoggerFactory.getLogger(ClickThroughMailClient.class);
	/**
	 * regex for url
	 */
	private static String regex = "\\b(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]";

}
