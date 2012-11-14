package com.ccc.util.dns.tests;

import java.io.IOException;

import junit.framework.TestCase;

import org.junit.*;

import org.junit.Test;

import com.ccc.util.dns.DNSUtils;

public class DnsTester extends TestCase {

	@Test
	public void dnsTest() throws IOException {
		String dns=DNSUtils.reverseDns("50.57.101.26");
		Assert.assertEquals("clevercloudcomputing.com",dns);
	}
}
