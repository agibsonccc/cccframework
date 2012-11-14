package com.ccc.mail.james.smtphandler;
import org.apache.james.protocols.smtp.MailAddress;
import org.apache.james.protocols.smtp.SMTPSession;
import org.apache.james.protocols.smtp.hook.HookResult;
import org.apache.james.protocols.smtp.hook.RcptHook; 

public class WhiteListHandler implements RcptHook {

	@Override
	public HookResult doRcpt(SMTPSession arg0, MailAddress arg1,
			MailAddress arg2) {
		arg0.setRelayingAllowed(true);
		arg0.setUser(arg2.toString());
		return HookResult.ok();
	}

}
