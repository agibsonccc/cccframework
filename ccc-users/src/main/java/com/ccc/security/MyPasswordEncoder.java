package com.ccc.security;
import org.springframework.security.authentication.encoding.ShaPasswordEncoder;
public class MyPasswordEncoder extends ShaPasswordEncoder implements java.io.Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 130214532304103009L;

	@Override
	public boolean isPasswordValid(String encrypted,String raw,Object salt){
			String test=super.encodePassword(raw, salt);
			return test.equals(encrypted);
	}
}
