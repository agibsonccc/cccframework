package com.ccc.camelcomponents.mailstore;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailstore.BaseMailStore;
import com.ccc.mail.core.servers.SMTPServer;
import com.ccc.mail.core.servers.Server;

@Component("mailStore")
public class CamelMailStore extends BaseMailStore {
	
	@Override
	public List<SMTPServer> outGoingServers() {
		return Collections.singletonList(outgoing);

	}

	@Override
	public List<Server> incomingServers() {
		return Collections.singletonList(incoming);

	}

	@Override
	public List<SMTPServer> outgoingServers() {
		return Collections.singletonList(outgoing);

	}

	@Override
	public List<Server> getIncomingServers() {
		return Collections.singletonList(incoming);
	}

	@Override
	public List<SMTPServer> getOutgoingServers() {
		return Collections.singletonList(outgoing);
	}

	public Server getIncoming() {
		return incoming;
	}

	public void setIncoming(Server incoming) {
		this.incoming = incoming;
	}

	public Server getOutgoing() {
		return outgoing;
	}

	public void setOutgoing(SMTPServer outgoing) {
		this.outgoing = outgoing;
	}
	
	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	@Autowired(required=false)
	private MailClient mailClient;
	@Autowired(required=false)
	@Qualifier("cccIn")
	private Server incoming;
	@Autowired(required=false)
	@Qualifier("cccOut")
	private SMTPServer outgoing;
}
