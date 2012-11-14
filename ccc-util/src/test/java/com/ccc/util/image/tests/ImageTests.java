package com.ccc.util.image.tests;

import java.io.File;
import java.io.IOException;

import org.apache.sanselan.ImageReadException;
import org.apache.sanselan.ImageWriteException;
import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.util.filesystem.PathManipulator;
import com.ccc.util.image.ImageUtils;

import junit.framework.TestCase;

public class ImageTests extends TestCase {

	@Test
	public void testFormats() throws ImageReadException, ImageWriteException, IOException {
		String format="gif";
		Assert.notNull(ImageUtils.formatFromString(format));
		String gif2=".gif";
		Assert.notNull(ImageUtils.formatFromString(gif2));
		File file = new File("cal.gif");
		String fileFormat=PathManipulator.getFormat(file);
		Assert.notNull(fileFormat);
		Assert.notNull(ImageUtils.formatFromString(file));
		writeFiles();
		
	}
	@Test
	public void writeFiles() throws ImageReadException, ImageWriteException, IOException {
		Assert.isTrue(ImageUtils.convertImage("src/test/resources/cal.gif", "src/test/resources/cal.png"), "failed to convert image");
	}
}
