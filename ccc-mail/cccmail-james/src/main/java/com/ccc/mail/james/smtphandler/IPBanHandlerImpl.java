package com.ccc.mail.james.smtphandler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalConfiguration;
import org.apache.james.protocols.api.Response;
import org.apache.james.protocols.smtp.SMTPResponse;
import org.apache.james.protocols.smtp.SMTPRetCode;
import org.apache.james.protocols.smtp.SMTPSession;

import com.ccc.util.filesystem.FileMoverUtil;
/**
 * Handles the banning of IP addresses and emails that connect too many times within a specified
 * period.
 * @author Adam Gibson
 *
 */
public class IPBanHandlerImpl implements ConnectionBanHandler {

	@Override
	public Response onConnect(SMTPSession session) {
		incrementSessionConnected(session);
		String email=session.getUser();
		if(email!=null && !email.isEmpty()) {
			if(!validateEmail(session)) {
				banEmail(email);
				session.setRelayingAllowed(false);
				return session.newFatalErrorResponse();
			}
			for(String s : bannedEmails) {
				if(email.matches(s) || email.equals(s))
					session.setRelayingAllowed(false);
			}
		}

		InetAddress ip=session.getRemoteAddress().getAddress();
		if(ip!=null) {
			String address=ip.getHostAddress();
			if(!validateIP(session)) {
				
				banIP(address);
				session.setRelayingAllowed(false);
				//HookResult.declined();
				return session.newFatalErrorResponse();
			}
			for(String s1 : bannedIps) {
				if(address.matches(s1))
					session.setRelayingAllowed(false);
			}
		}



		return new SMTPResponse(SMTPRetCode.MAIL_OK,"IP address ok");
	}

	private boolean validateEmail(SMTPSession session) {
		String email=session.getUser();
		if(email!=null && !email.isEmpty()) {
			Integer numConnections=emailConnections.get(email);
			Long time=emailConnectedTime.get(email);
			if(time==null) {
				emailConnectedTime.put(email,System.currentTimeMillis());
				return true;
			}
			if(time < connectionThreshold && numConnections > numTmesConnectedAllowedWithinThreshold) {
				return false;
			}
		}
		return true;
	}

	private boolean validateIP(SMTPSession session) {
		InetAddress ip=session.getRemoteAddress().getAddress();
		if(ip!=null) {
			String address=ip.getHostAddress();
			Integer numConnections=ipConnections.get(address);
			Long time=ipConnectedTime.get(address);
			if(time==null) {
				ipConnectedTime.put(address,System.currentTimeMillis());
				return true;
			}
			if(time!=null && time < connectionThreshold && numConnections > numTmesConnectedAllowedWithinThreshold) {
				return false;
			}
		}
		return true;
	}
	private boolean validateSession(SMTPSession session) {
		return validateIP(session) && validateEmail(session);
	}


	@Override
	public void configure(HierarchicalConfiguration conf)
			throws ConfigurationException {
		filePath=conf.getString("filePath","bannedips");
		try {
			connectionThreshold=Long.parseLong(conf.getString("connectionThreshold", "1000"));
		}catch(NumberFormatException e) {
			connectionThreshold=1000;
		}
		try {
			numTmesConnectedAllowedWithinThreshold=Integer.parseInt(conf.getString("numTmesConnectedAllowedWithinThreshold","2"));
		}catch(NumberFormatException e) {
			numTmesConnectedAllowedWithinThreshold=2;
		}

		whiteListedEmailFilePath=conf.getString("whiteListedEmailFilePath","whitelistedemails.csv");
		whiteListedIPFilePath=conf.getString("whiteListedIPFilePath","whiteListedips.csv");
		banScriptPath=conf.getString("banScriptPath","banip.sh");
		loadConf(bannedIps,filePath);
		loadConf(bannedEmails,bannedEmailFilePath);
		loadConf(whiteListedEmails,whiteListedEmailFilePath);
		loadConf(whiteListedIps,whiteListedIPFilePath);
		//don't ban white listed
		bannedIps.removeAll(whiteListedIps);
		bannedEmails.removeAll(whiteListedEmails);

	}

	private void setupFile(String filePath) {
		File f = new File(filePath);
		if(!f.exists()) {
			try {
				FileMoverUtil.createFile(f, false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	private void loadConf(Set<String> set,String path) {
		setupFile(path);
		File load = new File(path);
		List<String> contents=FileMoverUtil.fileContents(load);
		if(contents!=null && !contents.isEmpty()) {
			String content=contents.get(0);
			String[] split=content.split(",");
			for(String s : split)
				set.add(s);
		}
	}



	@Override
	public void banEmail(String email) {
		BufferedWriter writer;
		try {
			writer = new BufferedWriter(new FileWriter(new File(bannedEmailFilePath)));
			writer.append(",");
			writer.append(email);
			bannedEmails.add(email);
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}



	}

	private void incrementSessionConnected(SMTPSession session) {
		String email=session.getUser();
		InetAddress ip=session.getRemoteAddress().getAddress();
		if(ip!=null) {
			String address=ip.getHostAddress();
			incrementConnected(address);
		}
		if(email!=null && !email.isEmpty()) {
			incrementEmailConnected(email);
		}
	}

	private void incrementConnected(String ip) {
		Integer numConnected=ipConnections.get(ip);
		if(numConnected==null)
			numConnected=1;
		else numConnected++;
		long newTime=System.currentTimeMillis();
		ipConnectedTime.put(ip, newTime);
		ipConnections.put(ip,numConnected);
	}

	private void incrementEmailConnected(String email) {
		Integer numConnected=ipConnections.get(email);
		if(numConnected==null)
			numConnected=1;
		else numConnected++;
		long newTime=System.currentTimeMillis();
		emailConnectedTime.put(email,newTime);
		emailConnections.put(email,numConnected);
	}

	@Override
	public int numTmesConnectedAllowedWithinThreshold() {
		return numTmesConnectedAllowedWithinThreshold;
	}

	@Override
	public long getConnectionThreshold() {
		return connectionThreshold;
	}

	@Override
	public void banIP(String ip) {
		Runtime r = Runtime.getRuntime();
		Process p = null;
		String cmd[]  = {"/bin/bash", banScriptPath + " " + ip};
		try {
			//./banschellscript.sh ip
			p=r.exec(cmd, new String[0]);
			int processExit=p.exitValue();

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public Set<String> getBannedIps() {
		return bannedIps;
	}

	@Override
	public String ipFilePath() {
		return filePath;
	}
	@Override
	public String emailFilePath() {
		return emailFilePath;
	}
	@Override
	public String whiteListedIPRegexFilePath() {
		return whiteListedIPFilePath;
	}

	@Override
	public String whiteListedEmailRegexFilePath() {
		return whiteListedEmailFilePath;
	}
	private String emailFilePath;
	private String filePath;
	private long connectionThreshold;
	private int numTmesConnectedAllowedWithinThreshold;
	private Map<String,Integer> ipConnections = new HashMap<String,Integer>();
	private Map<String,Integer> emailConnections = new HashMap<String,Integer>();
	private Set<String> bannedIps = new HashSet<String>();
	private Set<String> whiteListedIps = new HashSet<String>();
	private Set<String> bannedEmails = new HashSet<String>();
	private Set<String> whiteListedEmails = new HashSet<String>();
	private String whiteListedEmailFilePath;
	private String whiteListedIPFilePath;
	private String bannedEmailFilePath;
	private Map<String,Long> emailConnectedTime = new HashMap<String,Long>();
	private Map<String,Long> ipConnectedTime = new HashMap<String,Long>();
	private String banScriptPath;

}
