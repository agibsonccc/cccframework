package com.ccc.util.string.tests;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.util.strings.CSVUtils;

import junit.framework.TestCase;

public class CSVTests extends TestCase {

	@Test
	public void testCSV() {
		Object[] o={"Hello",1};
		String arrayCSV=CSVUtils.arrayToCSV(o);
		String expected="Hello,1";
		Assert.isTrue(arrayCSV.equals(expected));
	}
	@Test
	public void testCSVCollection() {
		List test = new ArrayList();
		test.add("Hello");
		test.add("1");
		String collectionCSV=CSVUtils.toCSV(test);
		
		String expected="Hello,1";
		Assert.isTrue(collectionCSV.equals(expected));
	}
}
