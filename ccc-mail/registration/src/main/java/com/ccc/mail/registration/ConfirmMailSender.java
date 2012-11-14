package com.ccc.mail.registration;

import java.util.Map;

import javax.mail.MessagingException;
import javax.mail.internet.AddressException;

import org.springframework.beans.factory.annotation.Autowired;

import com.ccc.mail.core.MailClient;
import com.ccc.mail.core.mailbox.ServerMailStoreBridge;
import com.ccc.mail.core.servers.SMTPServer;
/**
 * This is a utility bean for sending confirmation emails.
 * @author Adam Gibson
 *
 */
public class ConfirmMailSender {







	/**
	 * This will send a confirmation email to the specified email.
	 * @param emailToConfirm the email to send to
	 * @param headersToUse the connection headers to use
	 * @return true if the message was sent, false on error
	 * @throws AddressException if one occurs 
	 * @throws MessagingException if one occurs
	 */
	public boolean sendConfirmationEmail(String emailToConfirm,Map<String,String> headersToUse,EmailRegistrationListener emailRegistrationListener,boolean isHtml) throws AddressException, MessagingException {
		//Append server headers
		/*
		Map<String,String> headersForServer=ServerMailStoreBridge.headersForServer(serverToUse);
		headersToUse.putAll(headersForServer);
		 */
		if(emailRegistrationListener!=null)
			emailRegistrationListener.setEmail(emailToConfirm);
		headersToUse.put(MailClient.TO_ADDRESSES, emailToConfirm);
		headersToUse.put(MailClient.FROM_ADDRESS,emailFrom);

		StringBuffer sb = new StringBuffer();
		sb.append(emailContent + "\n");

		//Append a generated registration link.

		sb.append("Please click the link below to confirm:\n");
		
		
		Object newUserName=headersToUse.get(NEW_USER_NAME);
		Object newPassword=headersToUse.get(NEW_PASSWORD);
		
		if(newUserName!=null && newPassword!=null) {
			sb.append("Here are your login credentials for your records:\n");
			sb.append("Username: " + newUserName.toString() + "\n");
			sb.append("Password: " + newPassword.toString()+ "\n");
		}
		
		
		sb.append(linkGenerator.generateLink(emailRegistrationListener));

		headersToUse.put(MailClient.CONTENT,sb.toString());


		//cc and bcc if specified
		if(bcc!=null && !bcc.isEmpty())
			headersToUse.put(MailClient.BCC_ADDRESSES, bcc);
		if(cc!=null && !cc.isEmpty())
			headersToUse.put(MailClient.CC_ADDRESSES,cc);

		return  mailClient.sendMail(headersToUse,isHtml);
	}//end sendConfirmationEmail




	/**
	 * This will send a confirmation email to the specified email.
	 * @param emailToConfirm the email to send to
	 * @param headersToUse the connection headers to use
	 * @return true if the message was sent, false on error
	 * @throws AddressException if one occurs 
	 * @throws MessagingException if one occurs
	 */
	public boolean sendConfirmationEmail(String emailToConfirm,Map<String,String> headersToUse,boolean isHtml) throws AddressException, MessagingException {
		//Append server headers
		/*
		Map<String,String> headersForServer=ServerMailStoreBridge.headersForServer(serverToUse);
		headersToUse.putAll(headersForServer);
		 */
		headersToUse.put(MailClient.TO_ADDRESSES, emailToConfirm);
		headersToUse.put(MailClient.FROM_ADDRESS,emailFrom);

		StringBuffer sb = new StringBuffer();
		sb.append(emailContent + "\n");

		//Append a generated registration link.

		sb.append("Please click the link below to confirm:\n");
		sb.append(linkGenerator.generateLink());

		headersToUse.put(MailClient.CONTENT,sb.toString());


		//cc and bcc if specified
		if(bcc!=null && !bcc.isEmpty())
			headersToUse.put(MailClient.BCC_ADDRESSES, bcc);
		if(cc!=null && !cc.isEmpty())
			headersToUse.put(MailClient.CC_ADDRESSES,cc);

		return  mailClient.sendMail(headersToUse,isHtml);
	}//end sendConfirmationEmail

	/**
	 * This will send a confirmation email to the given email address.
	 * @param emailToConfirm the email to confirm
	 * @param headersToUse the connection headers to use
	 * @param body the body of the mail
	 * @return true if the message was sent, false otherwise
	 * @throws AddressException if one occurs
	 * @throws MessagingException if one occurs
	 */
	public boolean sendConfirmationEmail(String emailToConfirm,Map<String,String> headersToUse,String body,boolean isHtml) throws AddressException, MessagingException {
		//Append server headers
		Map<String,String> headersForServer=ServerMailStoreBridge.headersForServer(serverToUse);
		headersToUse.putAll(headersForServer);
		headersToUse.put(MailClient.TO_ADDRESSES, emailToConfirm);
		headersToUse.put(MailClient.FROM_ADDRESS,emailFrom);

		//Append a generated registration link.

		if(bcc!=null && !bcc.isEmpty())
			headersToUse.put(MailClient.BCC_ADDRESSES, bcc);
		if(cc!=null && !cc.isEmpty())
			headersToUse.put(MailClient.CC_ADDRESSES,cc);
		body+="\n";

		body+="Please click the link below to confirm:\n";
		body+=linkGenerator.generateLink();
		headersToUse.put(MailClient.CONTENT,body);

		return mailClient.sendMail(headersToUse,isHtml);
	}//end sendConfirmationEmail

	public SMTPServer getServerToUse() {
		return serverToUse;
	}

	public void setServerToUse(SMTPServer serverToUse) {
		this.serverToUse = serverToUse;
	}

	public String getEmailFrom() {
		return emailFrom;
	}

	public void setEmailFrom(String emailFrom) {
		this.emailFrom = emailFrom;
	}

	public String getEmailContent() {
		return emailContent;
	}

	public void setEmailContent(String emailContent) {
		this.emailContent = emailContent;
	}

	public ConfirmLinkGenerator getLinkGenerator() {
		return linkGenerator;
	}

	public void setLinkGenerator(ConfirmLinkGenerator linkGenerator) {
		this.linkGenerator = linkGenerator;
	}

	public String getCc() {
		return cc;
	}

	public void setCc(String cc) {
		this.cc = cc;
	}

	public String getBcc() {
		return bcc;
	}

	public void setBcc(String bcc) {
		this.bcc = bcc;
	}

	public MailClient getMailClient() {
		return mailClient;
	}

	public void setMailClient(MailClient mailClient) {
		this.mailClient = mailClient;
	}

	@Autowired(required=false)
	private SMTPServer serverToUse;

	private String emailFrom;

	private String emailContent;
	@Autowired(required=false)
	private ConfirmLinkGenerator linkGenerator;

	private String cc;

	private String bcc;
	@Autowired(required=false)
	private MailClient mailClient;
	
	
	public final static String NEW_USER_NAME="newuser";
	
	public final static String NEW_PASSWORD="newpassword";
}//end ConfirmMailSender
