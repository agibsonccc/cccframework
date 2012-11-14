package com.ccc.camelcomponents.ical;

import java.net.URISyntaxException;

import com.ccc.camelcomponents.ical.util.ICalUtils;

import net.fortuna.ical4j.model.Date;
import net.fortuna.ical4j.model.TimeZone;
/**
 * This is a POJO fore representing meeting information.
 * @author Adam Gibson
 *
 */
public class MeetingInfo {

	
	public String toString() {
		try {
			return ICalUtils.fromMeeting(this).toString();
		} catch (URISyntaxException e) {
		}
		return null;
	}
	
	public TimeZone getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(TimeZone timeZone) {
		this.timeZone = timeZone;
	}

	public Date getMeetingStart() {
		return meetingStart;
	}

	public void setMeetingStart(Date meetingStart) {
		this.meetingStart = meetingStart;
	}

	public Date getMeetingEnd() {
		return meetingEnd;
	}

	public void setMeetingEnd(Date meetingEnd) {
		this.meetingEnd = meetingEnd;
	}

	public String getMeetingId() {
		return meetingId;
	}

	public void setMeetingId(String meetingId) {
		this.meetingId = meetingId;
	}

	public String getMeetingSubject() {
		return meetingSubject;
	}

	public void setMeetingSubject(String meetingSubject) {
		this.meetingSubject = meetingSubject;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	
	
	public String[] getAttendees() {
		return attendees;
	}

	public void setAttendees(String[] attendees) {
		this.attendees = attendees;
	}



	public String getOrganizer() {
		return organizer;
	}

	public void setOrganizer(String organizer) {
		this.organizer = organizer;
	}



	private String  organizer;
	private String[] attendees;
	
	private TimeZone timeZone;
	
	private Date meetingStart;
	
	private Date meetingEnd;
	
	private String meetingId;
	
	private String meetingSubject;
	
	private String content;
}
