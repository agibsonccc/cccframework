package com.ccc.mail.mailinglist.services.impl;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.ccc.mail.mailinglist.dao.MessageUrlTrackingManager;
import com.ccc.mail.mailinglist.dao.UrlRequestManager;
import com.ccc.mail.mailinglist.dao.UrlTrackingManager;
import com.ccc.mail.mailinglist.model.MessageUrlTracking;
import com.ccc.mail.mailinglist.model.UniqueMessage;
import com.ccc.mail.mailinglist.model.UrlRequest;
import com.ccc.mail.mailinglist.model.UrlTracking;
import com.ccc.mail.mailinglist.utils.ShortenerUtils;



@Service("urlTrackingService")
public class UrlTrackingService {

	public boolean saveUrlRequest(UrlRequest request) {
		return urlRequestManager.saveE(request);
	}
	
	public boolean deleteUrl(UrlTracking url) {
		return urlTrackingManager.deleteE(url);
	}
	
	public UrlTracking urlWithId(String id) {
		List<UrlTracking> url=urlTrackingManager.elementsWithValue("id", id);
		if(url==null || url.isEmpty())
			return null;
		
		else {
			return url.get(0);
		}
	}
	
	public UrlTracking trackerWithUrl(String url) {
		List<UrlTracking> list=urlTrackingManager.elementsWithValue("long_url", url);
		if(list!=null && !list.isEmpty())
			return list.get(0);
		return null;
	}
	
	
	/**
	 * This will save the long url
	 * @param longUrl the long url to save
	 * @return the generated id of the url or null on failure
	 */
	public String saveWithUniqueId(String longUrl) {
		UrlTracking tracking = new UrlTracking();
		tracking.setLongUrl(longUrl);
		int num=6;
		try {
			String id=ShortenerUtils.generateId(num);
			UrlTracking comp=urlWithId(id);
			while(comp!=null) {
				num++;
				id=ShortenerUtils.generateId(num);
				comp=urlWithId(id);
			}
			tracking.setUrlId(id);
			Assert.isTrue(saveUrl(tracking), "Failed to save url");
			return id;

		} catch (Exception e) {
			log.error("Error generating url id",e);
		}
		return null;
	}//end saveWithUniqueId
	
	
	public List<MessageUrlTracking> trackingForMessage(UniqueMessage message) {
		String id=message.getId();
		List<MessageUrlTracking> tracking=messageUrlTrackingManager.elementsWithValue("message_id", id);
		return tracking;
	}
	
	public boolean saveMessageUrlTracking(MessageUrlTracking save) {
		return messageUrlTrackingManager.saveE(save);
	}
	
	public boolean deleteMessageUrlTracking(MessageUrlTracking delete) {
		return messageUrlTrackingManager.deleteE(delete);
	}
	
	public boolean saveUrl(UrlTracking toSave) {
		return urlTrackingManager.saveE(toSave);
	}
	
	public List<UrlRequest> requestsWithUrl(UrlTracking tracking) {
		return urlRequestManager.elementsWithValue("url_id", tracking.getUrlId());
	}
	
	public UrlTrackingManager getUrlTrackingManager() {
		return urlTrackingManager;
	}
	public void setUrlTrackingManager(UrlTrackingManager urlTrackingManager) {
		this.urlTrackingManager = urlTrackingManager;
	}
	public UrlRequestManager getUrlRequestManager() {
		return urlRequestManager;
	}
	public void setUrlRequestManager(UrlRequestManager urlRequestManager) {
		this.urlRequestManager = urlRequestManager;
	}

	private static Logger log=LoggerFactory.getLogger(UrlTrackingService.class);
	@Autowired
	private UrlTrackingManager urlTrackingManager;
	@Autowired
	private UrlRequestManager urlRequestManager;
	@Autowired
	private MessageUrlTrackingManager messageUrlTrackingManager;
	
}
