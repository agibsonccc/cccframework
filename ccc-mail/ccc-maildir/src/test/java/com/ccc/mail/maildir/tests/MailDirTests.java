package com.ccc.mail.maildir.tests;
import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.mail.MessagingException;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.servers.Server;
import com.ccc.mail.maildir.MailDirService;
import com.ccc.mail.ssl.exceptions.SSLErrorException;
import com.ccc.mail.tests.utils.TestUtils;
import com.ccc.util.filesystem.FileMoverUtil;


public class MailDirTests {
	
	@Test
	public void testMailDir() throws SSLErrorException, MessagingException, IOException {
		MailDirService service = new MailDirService();
		service.setMailClient(mailClient);
		String user="agibson";
		String mailDir="/home/"+user+"/Maildir";
		Map<String,String> headers=TestUtils.headers("aegibson@mtu.edu", "goblinhacker%5", "imap.gmail.com", incoming);
		headers.put(MailClient.IS_SSL,"true");
		FileMoverUtil.createFile(new File("/home/agibson/MailDir"), true);
		service.syncMailDir(incoming, mailDir,headers );
		
	}
	@Autowired
	private MailClient mailClient;
	@Autowired
	private Server incoming;
}
