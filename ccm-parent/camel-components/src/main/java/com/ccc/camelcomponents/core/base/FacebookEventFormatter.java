package com.ccc.camelcomponents.core.base;

import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Organizer;

import com.ccc.camelcomponents.core.api.EventFormatter;
import com.google.api.client.json.jackson.JacksonFactory;

public class FacebookEventFormatter implements EventFormatter {

	@Override
	public String formatEvent(Object event) {
		if(event instanceof VEvent) {
			VEvent vEvent=(VEvent) event;
			FacebookEvent ret = new FacebookEvent();
			JacksonFactory factory = new JacksonFactory();
			if(vEvent.getCreated()!=null) {
				Created created=vEvent.getCreated();
				net.fortuna.ical4j.model.DateTime time=created.getDateTime();
				ret.setUpdatedTime(time);
			}

			if(vEvent.getDescription()!=null) {
				ret.setDescription(vEvent.getDescription().getValue());
			}
			if(vEvent.getLocation()!=null) {
				ret.setLocation(vEvent.getLocation().getValue());
			}
			else ret.setLocation("Location not specified");
			if(vEvent.getStartDate()!=null) {
				ret.setStartTime(vEvent.getStartDate().getDate());
			}
			else {
				return null;
			}

			if(vEvent.getEndDate()!=null) {
				ret.setEndTime(vEvent.getEndDate().getDate());

			}
			else {
				return null;
			}
			if(vEvent.getEndDate().getDate().before(vEvent.getStartDate().getDate()))
				return null;
			if(vEvent.getOrganizer()!=null) {
				Organizer org=vEvent.getOrganizer();
				ret.setOwner(org.getValue());

			}

			return	factory.toPrettyString(ret);

		}
		return null;
	}

	@Override
	public String formatMultipleEvents(Object[] events) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String mimeType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String format() {
		// TODO Auto-generated method stub
		return null;
	}

}
