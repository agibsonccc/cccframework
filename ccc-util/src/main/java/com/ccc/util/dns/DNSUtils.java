package com.ccc.util.dns;

import java.io.IOException;
import java.util.Enumeration;
import java.util.Hashtable;



import org.xbill.DNS.DClass;
import org.xbill.DNS.ExtendedResolver;
import org.xbill.DNS.Message;
import org.xbill.DNS.Name;
import org.xbill.DNS.Record;
import org.xbill.DNS.Resolver;
import org.xbill.DNS.ReverseMap;
import org.xbill.DNS.Section;
import org.xbill.DNS.Type;
/**
 * This covers some various utilities in dns.
 * @author Adam Gibson
 *
 */
public class DNSUtils {
	/**
	 * This will perform a revese dns lookup from the given ip address.
	 * @param hostIp
	 * @return null on bad input or the reverse ip if one is found.
	 * @throws IOException if one occurs
	 */
	public static String reverseDns(String hostIp) throws IOException {
		if(hostIp==null || hostIp.isEmpty())
			return null;
		
		Record opt = null;
		Resolver res = new ExtendedResolver();

		Name name = ReverseMap.fromAddress(hostIp);
		int type = Type.PTR;
		int dclass = DClass.IN;
		Record rec = Record.newRecord(name, type, dclass);
		Message query = Message.newQuery(rec);
		Message response = res.send(query);

		Record[] answers = response.getSectionArray(Section.ANSWER);
		if (answers.length == 0)
			return hostIp;
		else
			return answers[0].rdataToString();
	}
}//end DNSUtils
