package com.ccc.util.filesystem.tests;

import java.io.File;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import junit.framework.TestCase;

import org.junit.Test;
import org.springframework.util.Assert;

import com.ccc.util.filesystem.FileMoverUtil;

public class FileMoverUtilTests extends TestCase {
	@Test
	public void countSubDirs() {
		File f = new File("testfolder");
		Assert.isTrue(f.exists(), "Test file didn't exist");
		Assert.isTrue(f.isDirectory(), "File wasn't a directory");
		Set<File> subDirs=FileMoverUtil.subDirectories(f, true, new HashSet<File>());
		Assert.isTrue(subDirs.size()==3, "Sub directories was wrong file size");

	}

	public static void main(String[] args) {
		File dir = new File("/home/agibson/workspace4/current/app/target/apache-james-3.0-beta4-SNAPSHOT/WEB-INF/lib");
		Set<File> xmlFiles=FileMoverUtil.filesContainingZipEntries(".conf", true, dir);

		for(File f : xmlFiles) {
			System.out.println(f.getAbsolutePath());
		}

		///new FileMoverUtilTests().countSubDirs();
	}
}
