package com.ccc.util.web.tests;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.util.web.URLManipulator;

import junit.framework.TestCase;

public class TestUrlManipulator extends TestCase  {

	@Test
	public void testJsonEncode() throws JSONException {
		String toEncode="{\"test1\": value,\"value2\" : value}";
		JSONObject encoded = new JSONObject(toEncode);
		String url=URLManipulator.jsonToUrlEncode(encoded);
		Assert.notNull(URLManipulator.jsonToUrlEncode(encoded));
		String expected="test1=value&value2=value";
		Assert.isTrue(expected.equals(url));
	}
}
