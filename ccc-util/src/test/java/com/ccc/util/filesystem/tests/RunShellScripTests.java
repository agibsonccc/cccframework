package com.ccc.util.filesystem.tests;

import java.io.File;

import org.junit.Test;

import com.ccc.util.filesystem.RunShellScript;

import junit.framework.TestCase;

public class RunShellScripTests extends TestCase {
	@Test
	public void testScript() {
		String osName= System.getProperty("os.name");
		File f = new File(".");
		String parent=f.getParent();
		if(!osName.toLowerCase().contains("win"))
			RunShellScript.run(parent + File.pathSeparator + "run.sh", null);
		else {
			RunShellScript.run(parent + File.pathSeparator + "run.bat",null);
		}
	}

}
