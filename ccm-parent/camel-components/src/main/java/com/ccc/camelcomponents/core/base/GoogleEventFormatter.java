package com.ccc.camelcomponents.core.base;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Transp;

import com.ccc.camelcomponents.core.api.EventFormatter;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.client.util.DateTime;
import com.google.api.services.calendar.model.Event;
import com.google.api.services.calendar.model.EventAttendee;
import com.google.api.services.calendar.model.EventDateTime;
import com.google.api.services.calendar.model.EventOrganizer;

public class GoogleEventFormatter implements EventFormatter {

	@Override
	public String formatEvent(Object event) {
		if(event instanceof VEvent) {
			return eventForVEvent((VEvent) event).toPrettyString();
		}

		return null;
	}


	private Event eventForVEvent(VEvent vEvent) {
		Event event = new Event();
		event.setFactory(new JacksonFactory());
		if(vEvent.getCreated()!=null) {
			Created created=vEvent.getCreated();
			net.fortuna.ical4j.model.DateTime time=created.getDateTime();
			DateTime set = new DateTime(time);
			event.setCreated(set);

		}
		if(vEvent.getSummary()!=null) {
			String summary=vEvent.getSummary().getValue();
			event.setSummary(summary);
		}
		else event.setSummary("No summary specified");
		
		if(vEvent.getDescription()!=null) {
			event.setDescription(vEvent.getDescription().getValue());
		}
		if(vEvent.getLocation()!=null) {
			event.setLocation(vEvent.getLocation().getValue());
		}
		else event.setLocation("Location not specified");
		if(vEvent.getStartDate()!=null) {
			EventDateTime start = new EventDateTime();
			DateTime set = new DateTime(vEvent.getStartDate().getDate(),TimeZone.getTimeZone("UTC"));
			start.setDateTime(set);
			event.setStart(start);
		}
		else {
			return null;
		}
		
		if(vEvent.getEndDate()!=null) {
			EventDateTime end = new EventDateTime();
			DateTime set = new DateTime(vEvent.getEndDate().getDate(),TimeZone.getTimeZone("UTC"));
			end.setDateTime(set);
			event.setEnd(end);

		}
		else {
			return null;
		}
		if(vEvent.getEndDate().getDate().before(vEvent.getStartDate().getDate()))
			return null;
		
		if(vEvent.getOrganizer()!=null) {
			Organizer org=vEvent.getOrganizer();
			EventOrganizer organizer = new EventOrganizer();
			organizer.setDisplayName(org.getCalAddress().toString());
			organizer.setEmail(org.getCalAddress().toString().replace("mailto:", ""));

			event.setOrganizer(organizer);

		}
		

		if(vEvent.getUrl()!=null) {
			event.setEtag(vEvent.getUrl().toString());
			event.setHtmlLink(vEvent.getUrl().toString());
		}
		if(vEvent.getUid()!=null) {
			event.setICalUID(vEvent.getUid().getValue());
		}
		/*
		if(vEvent.getTransparency()!=null) {
			Transp trans=vEvent.getTransparency();
			String val=trans.getValue();
			event.setTransparency(val);
		}
		*/
		PropertyList attendees=vEvent.getProperties(Attendee.ATTENDEE);
		if(attendees!=null) {
			Iterator iter=attendees.iterator();
			ArrayList<EventAttendee> googAtt = new ArrayList<EventAttendee>();

			while(iter.hasNext()) {
				Attendee at=(Attendee) iter.next();
				EventAttendee newAtt = new EventAttendee();
				newAtt.setEmail(at.getCalAddress().toString().replace("mailto:",""));
				if(vEvent.getOrganizer()!=null) {
					Organizer org=vEvent.getOrganizer();
					if(org.getCalAddress().equals(at.getCalAddress()))
						newAtt.setOrganizer(true);
					EventAttendee at2 = new EventAttendee();
					String email=org.getCalAddress().toString().replace("mailto:","");
					at2.setEmail(email);
					at2.setDisplayName(email);
					googAtt.add(at2);
				}
			}
			EventAttendee at2 = new EventAttendee();
			at2.setEmail("agibson@clevercloudcomputing.com");
			at2.setDisplayName("agibson@clevercloudcomputing.com");
			googAtt.add(at2);
			event.setAttendees(googAtt);
		}


		return event;
	}

	@Override
	public String mimeType() {
		return "application/json";
	}


	@Override
	public String formatMultipleEvents(Object[] events)  {
		JSONArray arr = new JSONArray();
		try {
			for(int i=0;i<events.length;i++) {
				Object o=events[i];
				String formatted=formatEvent(o);
				if(formatted==null) continue;
				JSONObject obj1 = new JSONObject(formatted);
				arr.put(i,obj1);
			}
			//validate
			return arr.toString();
		}catch(JSONException e) {
			e.printStackTrace();
		}
		return null;
	}


	@Override
	public String format() {
		return ".json";
	}

}
