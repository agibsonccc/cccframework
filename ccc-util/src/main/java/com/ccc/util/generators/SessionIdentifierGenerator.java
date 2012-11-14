package com.ccc.util.generators;

import java.math.BigInteger;
import java.security.SecureRandom;

import org.springframework.stereotype.Component;

@Component("sessionIdentifierGenerator")
public class SessionIdentifierGenerator {
	 private SecureRandom random = new SecureRandom();

	  public String nextSessionId()
	  {
	    return new BigInteger(130, random).toString(32);
	  }

}//end SessionIdentifierGenerator
