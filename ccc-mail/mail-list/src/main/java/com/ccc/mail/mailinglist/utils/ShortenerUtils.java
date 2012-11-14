package com.ccc.mail.mailinglist.utils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.util.UUID;

import org.springframework.security.crypto.codec.Base64;

import com.ccc.util.strings.StringUtils;

public class ShortenerUtils {

	/**
	 * This will return a shortened url id 
	 * and the number relative to the number of characters
	 * to take off of the id
	 * @return a shortened url id
	 * @throws Exception if one occurs
	 */
	public static String generateId() throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		UUID uuid = UUID.randomUUID();
		dos.writeLong(uuid.getMostSignificantBits());
		String encoded = new String(Base64.encode(baos.toByteArray()), "ISO-8859-1");
		String shortUrlKey = StringUtils.left(encoded, 6); // returns the leftmost 6 characters
		
		return shortUrlKey;
	}
	/**
	 * This will return a shortened url id 
	 * and the number relative to the number of characters
	 * to take off of the id
	 * @return a shortened url id
	 * @throws Exception if one occurs
	 */
	public static String generateId(int charactersToShave) throws Exception {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(baos);
		UUID uuid = UUID.randomUUID();
		dos.writeLong(uuid.getMostSignificantBits());
		String encoded = new String(Base64.encode(baos.toByteArray()), "ISO-8859-1");
		String shortUrlKey = StringUtils.left(encoded, charactersToShave); // returns the leftmost 6 characters
		
		return shortUrlKey;
	}
}
