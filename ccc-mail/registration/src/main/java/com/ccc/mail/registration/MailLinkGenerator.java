package com.ccc.mail.registration;

import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.registration.ConfirmLinkGenerator;
import com.ccc.util.generators.SessionIdentifierGenerator;
/**
 * This is a mail sender for confirming registration
 * @author Adam Gibson
 *
 */
public class MailLinkGenerator implements ConfirmLinkGenerator {

	@Override
	public String generateLink() {
		String id= idGen.nextSessionId();
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("?id=");
		sb.append(id);
		return sb.toString();
	}
	@Override
	public String generateLink(RegistrationListener listener) {
		String id= idGen.nextSessionId();
		listener.setId(id);
		StringBuffer sb = new StringBuffer();
		sb.append(url);
		sb.append("?id=");
		sb.append(id);
		return sb.toString();
	}
	
	
	public SessionIdentifierGenerator getIdGen() {
		return idGen;
	}

	public void setIdGen(SessionIdentifierGenerator idGen) {
		this.idGen = idGen;
	}


	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	private String url;

	@Autowired
	private SessionIdentifierGenerator idGen;
	
	
	@Autowired
	private MailClient mailClient;

	
	
	
}//end MailLinkGenerator
