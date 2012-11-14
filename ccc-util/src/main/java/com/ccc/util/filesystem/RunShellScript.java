package com.ccc.util.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class RunShellScript {
	public static  void run(String fileName,String...args){
		Runtime p=Runtime.getRuntime();
		//String[] command={"/bin/bash",context.getRootDirectory()+File.separator + "WEB-INF" + File.separator + "run.sh"};
		
		List<String> list=(args==null || args.length < 1) ? new ArrayList<String>() : Arrays.asList(args);
	
		String[] command={"/bin/sh",fileName};
		List<String> commands=Arrays.asList(command);
		commands.addAll(list);
		String[] commandsArr = new String[commands.size()];
		for(int i=0;i<commandsArr.length;i++) commandsArr[i]=commands.get(i);
		if(log.isDebugEnabled()) {
			log.debug("Executing: " + Arrays.toString(commandsArr));
		}
		
		try {
			Process p1=p.exec(commandsArr);
			BufferedWriter outCommand = new BufferedWriter(new
					OutputStreamWriter(p1.getOutputStream()));
			File f =null;
			f = new File(fileName);
			BufferedReader bis = new BufferedReader(new FileReader(f));
			String write="";
			while((write=bis.readLine())!=null){
				outCommand.write(write);
			}
			outCommand.flush();
			
			try {
				p1.waitFor();
			} catch (InterruptedException e) {
			}
			int code=p1.exitValue();
			if(log.isDebugEnabled()) {
				log.debug("Process ran exited with a code of: " + code);
			}
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	
	public static void main(String[] args) {
		String osName= System.getProperty("os.name");
		File f = new File(".");
		String parent=f.getAbsolutePath();
		if(!osName.toLowerCase().contains("win"))
			RunShellScript.run("run.sh", null);
		else {
			RunShellScript.run("run.bat",null);
		}
	}
	
	private static Logger log=LoggerFactory.getLogger(RunShellScript.class);
	
}
