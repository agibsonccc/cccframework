package com.ccc.camelcomponents.ical.util.tests;
import java.io.IOException;
import java.net.URISyntaxException;

import junit.framework.TestCase;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.DateTime;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.Attendee;
import net.fortuna.ical4j.model.property.Created;
import net.fortuna.ical4j.model.property.Description;
import net.fortuna.ical4j.model.property.DtEnd;
import net.fortuna.ical4j.model.property.DtStart;
import net.fortuna.ical4j.model.property.Organizer;
import net.fortuna.ical4j.model.property.Summary;
import net.fortuna.ical4j.model.property.Uid;

import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.camelcomponents.ical.MeetingInfo;
import com.ccc.camelcomponents.ical.util.ICalUtils;
import com.ccc.camelcomponents.ical.util.api.MethodServicemapper;
import com.ccc.camelcomponents.ical.util.api.ServerMapper;
import com.ccc.camelcomponents.ical.util.impl.DefaultMethodServiceMapper;
import com.ccc.camelcomponents.ical.util.impl.DefaultServerMapper;
public class ICalUtilTests extends TestCase {

	@Test
	public void testInviteTypes() {
		String email="agibson@clevercloudcomputing.com";
		String url="http://www.clevercloudcomputing.com";

		Assert.isTrue(ICalUtils.isEmailInvite(email));
		Assert.isTrue(ICalUtils.isHttpInvite(url));

	}

	@Test
	public void testServiceMappers() {
		MethodServicemapper serviceMapper = new DefaultMethodServiceMapper();
		ServerMapper serverMapper = new DefaultServerMapper();
		String url="http://www.google.com";
		String zimbra="http://www.huskymail.mtu.edu/zimbra";

		String type=serverMapper.serviceFor(url);
		Assert.isTrue(type.equals(ServerMapper.GOOGLE_DATA));
		/*
		type=serviceMapper.methodFor(zimbra);

		Assert.isTrue(type.equals(ServerMapper.ZIMBRA));
		 */
		String service=serviceMapper.methodFor(url);
		String zimbraString=serviceMapper.methodFor(zimbra);
		Assert.notNull(service);

		Assert.isTrue(service.equals(MethodServicemapper.POST));
		Assert.notNull(zimbraString);
		Assert.isTrue(zimbraString.equals(MethodServicemapper.GET));


	}
	@Test
	public void testMeetingFromEvent() throws URISyntaxException {
		VEvent vEvent = new VEvent();
		vEvent.getProperties().add(new Uid("asdf"));
		vEvent.getProperties().add(new Summary("asdf"));
		vEvent.getProperties().add(new Description("asdf"));
		vEvent.getProperties().add(new DtStart(new Date(System.currentTimeMillis())));
		vEvent.getProperties().add(new DtEnd(new Date(System.currentTimeMillis())));
		vEvent.getProperties().add(new Created(new DateTime(System.currentTimeMillis())));
		vEvent.getProperties().add(new Attendee("aegibson"));
		vEvent.getProperties().add(new Organizer("aegibson"));
		MeetingInfo info=ICalUtils.fromEvent(vEvent);
		Assert.notNull(info,"Meeting from event was null");
	}

	@Test
	public void testEventFromMeeting() throws URISyntaxException {

		MeetingInfo ret = getInfo();

		
		VEvent test=ICalUtils.fromMeeting(ret);
		Assert.notNull(test);

	}

	private MeetingInfo getInfo() {
		MeetingInfo ret = new MeetingInfo();

		DtStart start=new DtStart(new Date(System.currentTimeMillis()));
		DtEnd end=new DtEnd(new Date(System.currentTimeMillis()));
		Date startDate = start.getDate();
		Date endDate=end.getDate();
		ret.setMeetingStart(startDate);
		ret.setMeetingEnd(endDate);
		ret.setContent(ret.getMeetingId());
		ret.setMeetingSubject("asdf");
		ret.setTimeZone(ICalUtils.getRandTimeZone());
		ret.setAttendees(new String[]{"aegibson"});
		ret.setOrganizer("aegibson");
		return ret;
	}

	

	private VEvent getEvent() {
		VEvent vEvent = new VEvent();
		vEvent.getProperties().add(new Uid("asdf"));
		vEvent.getProperties().add(new Summary("asdf"));
		vEvent.getProperties().add(new Description("asdf"));
		vEvent.getProperties().add(new DtStart(new Date(System.currentTimeMillis())));
		vEvent.getProperties().add(new DtEnd(new Date(System.currentTimeMillis())));
		return vEvent;
	}


	@Test
	public void testCalendar() throws IOException, ParserException, URISyntaxException {
		Calendar calendar=ICalUtils.getBasicCalendar();
		Assert.notNull(calendar);
		Calendar fromFile=ICalUtils.buildCalendar("/home/agibson/workspace4/camel-components/src/main/resources/calendar.ics");
		Assert.notNull(fromFile);
	}
}
