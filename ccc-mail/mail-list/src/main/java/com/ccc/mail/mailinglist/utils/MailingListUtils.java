package com.ccc.mail.mailinglist.utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.validator.EmailValidator;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.ccc.mail.mailinglist.model.BounceRate;
import com.ccc.mail.mailinglist.model.ListMessageTracking;
import com.ccc.mail.mailinglist.model.MailingList;
import com.ccc.mail.mailinglist.model.MessageSend;
import com.ccc.mail.mailinglist.model.MessageUrlTracking;
import com.ccc.mail.mailinglist.model.Subscriber;
import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.mail.mailinglist.model.UrlRequest;
import com.ccc.mail.mailinglist.model.UrlTracking;
import com.ccc.mail.mailinglist.services.api.MailingListService;
import com.ccc.mail.mailinglist.services.impl.UrlTrackingService;


public class MailingListUtils {
	private static EmailValidator emailValidator=EmailValidator.getInstance();
	/**
	 * This will take in a list of subscribers and verify they are actually subscribed using the given 
	 * service and build a comma separate list of emails to send to
	 * @param subscribers the subscribers to send to
	 * @param targetList the list to send to
	 * @param service the service to use to verify subscribers
	 * @return a csv string of emails who will allow you to send to them
	 */
	public static String toCSV(Collection<Subscriber> subscribers,MailingList targetList,MailingListService service) {
		StringBuffer sb = new StringBuffer();
		Iterator iter=subscribers.iterator();
		while(iter.hasNext()) {
			Subscriber o=(Subscriber) iter.next();
			if(!service.alreadyUnSubbed(o.getEmail(), targetList)) {
				sb.append(o.toString());
				if(iter.hasNext()) sb.append(",");
			}

		}
		return sb.toString();
	}//end toCSV


	/**
	 * This will take in a csv string and output a set of subscribers.
	 * Leveraging the passed in mailing list service, will save and do updates
	 * based on the subscribers
	 * @param csv a csv string
	 * @param mailingListService the mailing list service to use
	 * @return a set of subscribers from the csv string, or an empty set
	 */
	public static Set<Subscriber> getSubscribers(String csv,MailingListService mailingListService) {
		String[] split=csv.split(",");
		Set<Subscriber> ret = new HashSet<Subscriber>();
		for(String s : split) {
			if(emailValidator.isValid(s)) {
				List<Subscriber> sub=mailingListService.subscriberForEmail(s);
				if(sub!=null && !sub.isEmpty()) {
					ret.addAll(sub);
				}
				else {
					Subscriber subscriber = new Subscriber();
					subscriber.setEmail(s);
					ret.add(subscriber);

				}
			}
		}
		return ret;
	}

	/**
	 * This will turn a list of unique messages in to a series of data points in the form of:
	 * [{x:val,y:val},...]
	 * where val is relative to the metric passed in
	 * @param messages the messages to transform
	 * @param service the service to use to load data
	 * @param metric the metric to get
	 * @return a json array of x,y pairs in the form of an array of json objects
	 * @throws JSONException
	 */
	public static JSONArray dataFor(List<UniqueMessage> messages,MailingListService service,UrlTrackingService urlTrackingService,int metric) throws JSONException {
		JSONArray arr = new JSONArray();
		if(metric!=CLICKED_SINGLE && metric!=LINKS) {
			for(UniqueMessage message : messages) {
				long time=message.getMessage().getSent().getTime();
				int[] metrics={SENT,BOUNCE_RATE,NUM_SUBSCRIBERS,CLICKED,TOTAL_SENT_FROM_EMAIL,TOTAL_BOUNCED_FROM_EMAIL};
				String sender=message.getMaiingList().getMailingAddress();
				List<MessageSend> sends=service.sendsForEmail(sender);
				int sendAmount=sends==null ? 0 : sends.size();
				List<BounceRate> bounces=service.bouncesForEmail(sender);
				int bounceAmount=bounces==null ? 0 : bounces.size();
				List<ListMessageTracking> tracking=service.trackingForMessage(message);
				int numClicks=tracking==null ? 0 : tracking.size();
				JSONObject data=dataPointFor(message,bounceAmount,sendAmount,metric,numClicks);
				arr.put(data);
			}
		}
		else if(metric==CLICKED_SINGLE) {
			for(UniqueMessage message : messages) {
				List<ListMessageTracking> tracking=service.trackingForMessage(message);
				for(ListMessageTracking track : tracking) {
					JSONObject data = new JSONObject();
					data.accumulate("email", track.getEmail());
					data.accumulate("time", track.getClickedTime());
					data.accumulate("ip",track.getIp());
					arr.put(data);
				}
			}
		}
		else {
			for(UniqueMessage message : messages) {
				List<MessageUrlTracking> tracking=urlTrackingService.trackingForMessage(message);
				List<ListMessageTracking> tracking2=service.trackingForMessage(message);
				Map<String,String> ipToEmail=ipToEmail(tracking2);
				if(tracking!=null && !tracking.isEmpty()) {
					for(MessageUrlTracking track : tracking) {
						JSONObject obj = new JSONObject();
						Set<UrlRequest> requests=track.getRequests();
						for(UrlRequest request : requests) {
							String email=ipToEmail.get(request.getAccessingIp());
							if(email==null)
								email="unknown";
							obj.accumulate("email",email);
							obj.accumulate("url", request.getUrl().getLongUrl());

							obj.accumulate("ip",request.getAccessingIp());

							obj.accumulate("host", request.getAccessingHost());
							arr.put(obj);
						}
					}

				}
			}
		}
		return arr;
	}

	private static Map<String,String> ipToEmail(List<ListMessageTracking> tracking) {
		Map<String,String> ret = new HashMap<String,String>();
		for(ListMessageTracking track : tracking) {
			ret.put(track.getIp(), track.getEmail());
		}
		return ret;
	}

	

	private static JSONObject annotatedPoint(UniqueMessage message,int metric,MailingListService service) throws JSONException {
		JSONObject ret = new JSONObject();
		JSONArray other = new JSONArray();
		long time=message.getMessage().getSent().getTime();
		int[] metrics={SENT,BOUNCE_RATE,NUM_SUBSCRIBERS,CLICKED,TOTAL_SENT_FROM_EMAIL,TOTAL_BOUNCED_FROM_EMAIL};
		String sender=message.getMaiingList().getMailingAddress();
		List<MessageSend> sends=service.sendsForEmail(sender);
		int sendAmount=sends==null ? 0 : sends.size();
		List<BounceRate> bounces=service.bouncesForEmail(sender);
		int bounceAmount=bounces==null ? 0 : bounces.size();
		List<ListMessageTracking> tracking=service.trackingForMessage(message);
		int numClicks=tracking==null ? 0 : tracking.size();
		
		for(int i : metrics) {
			if(i!=metric) {
				JSONObject put = new JSONObject();
				String name=nameForMetric(i);
				put.accumulate("name",name	);
				int amount=amountForMetric(message,bounceAmount,sendAmount,metric,numClicks);
				put.accumulate("value" ,amount);
				other.put(put);
			}
			else {
				JSONObject data = new JSONObject();
				JSONArray series = new JSONArray();
				
				
				
				data.accumulate("x",time);
				data.accumulate("y", amountForMetric(message,bounceAmount,sendAmount,metric,numClicks));
				series.put(0,data);
				ret.put("data", series);
			}
		}
		ret.accumulate("other",other);
		String beginMessage=message.subject();
		ret.accumulate("name",beginMessage);
		ret.accumulate("color","blue");
		return ret;
	}

	private static String nameForMetric(int metric) {
		switch(metric) {
		case SENT:
			return "# of times Sent:";
		case BOUNCE_RATE:
			return "Unique Page Conusumptions:";
		case NUM_SUBSCRIBERS:
			return "Number of subscribers:";
		case CLICKED:
			return "Number of times clicked:";

		case TOTAL_SENT_FROM_EMAIL:
			return "Total sent from email:";
		case TOTAL_BOUNCED_FROM_EMAIL:
			return "Total bounces:";

			default: 
				return null;
		}
	}

	private static int amountForMetric(UniqueMessage message,int bounceRate,int send,int metric,int numTimesClicked) {
		switch(metric) {
		case SENT:
			return  send;
		case BOUNCE_RATE:
			return bounceRate;
		case NUM_SUBSCRIBERS:
			return message.getSubsAtSent();
		case CLICKED:
			return numTimesClicked;

		case TOTAL_SENT_FROM_EMAIL:
			 return send;
		case TOTAL_BOUNCED_FROM_EMAIL:
			return bounceRate;


		}
		return -1;
	}

	private static JSONObject dataPointFor(UniqueMessage message,int bounceRate,int send,int metric,int numTimesClicked) throws JSONException {
		JSONObject ret = new JSONObject();

		long time=message.getMessage().getSent().getTime();
		double log=Math.log(time);
		long round=Math.round(log);
		ret.accumulate("x", round);
		switch(metric) {
		case SENT:
			ret.accumulate("y", send);
			break;
		case BOUNCE_RATE:
			ret.accumulate("y", bounceRate);
			break;
		case NUM_SUBSCRIBERS:
			ret.accumulate("y", message.getSubsAtSent());
			break;
		case CLICKED:
			ret.accumulate("y", numTimesClicked);
			break;

		}
		return ret;
	}


	public final static int SENT=1;
	public final static int BOUNCE_RATE=2;
	public final static int NUM_SUBSCRIBERS=3;
	public final static int CLICKED=4;
	public final static int TOTAL_SENT_FROM_EMAIL=5;
	public final static int TOTAL_BOUNCED_FROM_EMAIL=6;
	public final static int CLICKED_SINGLE=7;
	public final static int LINKS=8;
}




