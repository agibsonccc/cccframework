package com.ccc.mail.james.smtphandler;

import java.util.Set;

import org.apache.james.lifecycle.api.Configurable;
import org.apache.james.protocols.api.handler.ConnectHandler;
import org.apache.james.protocols.smtp.SMTPSession;
/**
 * This defines an interface for banning ips that connect after a certain number of times
 * after a certain threshold
 * @author Adam Gibson
 *
 */
public interface ConnectionBanHandler  extends ConnectHandler<SMTPSession>,Configurable {
	
	/**
	 * Returns the file path of where banned emails are stored
	 * @return the file path of wher banned emails are stored
	 */
	public String emailFilePath();
	/**
	 * Returns the file path of where the banned ips are stored
	 * @return the file path of where the banned ips are stored
	 */
	public String ipFilePath();
	
	/**
	 * This will return the number of times an smtp server is allowed to connect
	 * within a certain threshold
	 * @return the number of times an smtpserver is allowed to connect before a threshold
	 */
	public int numTmesConnectedAllowedWithinThreshold();
	/**
	 * The timeout before banning an IP
	 * @return
	 */
	public long getConnectionThreshold();
	/**
	 * This will ban the passed in IP
	 * @param ip the ip address to ban
	 */
	public void banIP(String ip);
	/**
	 * This will ban the passed in email
	 * @param email the email to ban
	 */
	public void banEmail(String email);
	/**
	 * This returns the set of banned IPs
	 * @return the set of banned IPs
	 */
	public Set<String> getBannedIps();
	
	/**
	 * Returns the file path of the white listed ip file csv
	 * @return the file path of the white listed ip file csv
	 */
	public String whiteListedIPRegexFilePath();
	/**
	 * Returns the file path of the white listed email csv
	 * @return the file path of the white listed email csv
	 */
	public String whiteListedEmailRegexFilePath();
	
}
