package com.ccc.util.filesystem;
/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/




import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Console;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.SocketException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;





/**
 * This is a util class for files.
 * @author Adam Gibson
 *
 */
public class FileMoverUtil   {

	
	
	/**
	 * Loads and returns a file from a resource stream with a classloader
	 * specified by the passed in class
	 * @param resourceName the name of the resource to get
	 * @param destination the destination directory to put the file
	 * @param loadFrom the class to use as a resource stream
	 * @return the file loaded or null on error
	 * @throws IOException
	 */
	public static File loadZipEntryFromResource(String resourceName,File destination,Class loadFrom) throws IOException {
		
		if(destination==null || !destination.exists())
			throw new IllegalStateException("Directory must exist!");
		File f = new File(destination.getAbsolutePath() + File.separator + resourceName);
		createFile(f,false);
		InputStream stream=loadFrom.getResourceAsStream(resourceName);
		if(stream==null)
			throw new IllegalStateException("Resource " + resourceName + " not found!");
		BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(f));
		FileMoverUtil.copyInputStream(stream, bos);
		stream.close();
		bos.close();
		return f;
	}
	
	/**
	 * This takes a line by line file, and writes out a new file with quotes on 
	 * each line
	 * @param filePath the path of the file to read
	 * @param destination the path of the output file
	 */
	public static void quotify(String filePath,String destination) {
		File f = new File(filePath);
		if(!f.exists() || f.isDirectory())
			throw new IllegalStateException("File: " + filePath + " was a directory or didn't exist");
		File f1 = new File(destination);
		try {
			createFile(f1,false);
		} catch (IOException e) {
			log.error("Error creating destination file: " + destination);
		}
		BufferedReader br = null;
		BufferedWriter bw = null;
		try {
			br = new BufferedReader(new FileReader(f));
			bw = new BufferedWriter(new FileWriter(f1));
			String line;
			while((line=br.readLine())!=null) {
				bw.write("\"" + line+ "\"" + "\n");
			}
			bw.flush();
		} catch (FileNotFoundException e) {
			log.error("Error creating destination file: " + destination);

		} catch (IOException e) {
			log.error("Error creating destination file: " + destination,e);

		}
		finally {
			try {
				br.close();
				bw.flush();
				bw.close();
			} catch (IOException e) {
				log.error("Error creating destination file: " + destination,e);

			}

		}

	}

	/**
	 * Split fileToSplit in to n sub files relative to linesSplitBy in to 
	 * the destination directory
	 * @param fileToSplit the base file to split
	 * @param destinationDir the destination directory for the split files (doesn't have to exist)
	 * @param baseSplitName the base name of each generated file such that:
	 * baseSplitName_1,baseSplitName_2
	 * up to the number of linesSplitBy
	 * @param linesSplitBy the number of lines to write to a file before creating a new one
	 * @throws IOException 
	 */
	public static void splitFiles(String fileToSplit,String destinationDir,String baseSplitName,int linesSplitBy) throws IOException {
		File input = new File(fileToSplit);
		if(!input.exists())
			throw new IllegalStateException("File " + fileToSplit + " doesn't exist");
		if(input.isDirectory())
			throw new IllegalStateException("File " + fileToSplit + " is a directory");

		//check exists or create directory for partitioned files
		File destination = new File(destinationDir);
		if(!destination.exists())
			createFile(destination,true);
		else if(destination.exists() && destination.isFile())
			throw new IllegalStateException("Given file exists and is not a directory");
		BufferedReader reader = new BufferedReader(new FileReader(input));
		String line;
		//track number of lines read for creating a new file, increment count when necessary
		int linesRead=0,currentFile=1;
		String firstFilePath=destinationDir + File.separatorChar + baseSplitName + "_" + currentFile;
		File first = new File(firstFilePath);
		createFile(first,false);


		BufferedWriter writer = new BufferedWriter(new FileWriter(first));
		while((line=reader.readLine())!=null) {
			if(linesRead <= linesSplitBy) {
				writer.append(line);
				linesRead++;
			}
			//create a new file
			else {
				currentFile++;
				String newFilePath=destinationDir + File.separatorChar + baseSplitName + "_" + currentFile;
				File newFile = new File(newFilePath);
				createFile(newFile,false);
				//ensure contents are there and close stream
				writer.flush();
				IOUtils.closeQuietly(writer);
				//start anew with new file
				writer = new BufferedWriter(new FileWriter(newFile));
				writer.append(line);
				//reset line count
				linesRead=1;
			}
		}
	}


	/**
	 * for all elements of java.class.path get a Collection of resources
	 * Pattern pattern = Pattern.compile(".*"); gets all resources
	 * @param pattern the pattern to match
	 * @return the resources in the order they are found 
	 */
	public static Collection<String> getResources(Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		String classPath = System.getProperty("java.class.path",".");
		String[] classPathElements = classPath.split(":");
		for (String element : classPathElements) {
			retval.addAll(getResources(element, pattern));
		}
		return retval;
	}

	public static Collection<String> getResources(String element, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		File file = new File(element);
		if (file.isDirectory()) {
			retval.addAll(getResourcesFromDirectory(file, pattern));
		} else {
			retval.addAll(getResourcesFromJarFile(file, pattern));
		}
		return retval;
	}

	public static Collection<String> getResourcesFromJarFile(File file, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		ZipFile zf;
		try {
			zf = new ZipFile(file);
		} catch (ZipException e) {
			throw new Error(e);
		} catch (IOException e) {
			throw new Error(e);
		}
		Enumeration e = zf.entries();
		while (e.hasMoreElements()) {
			ZipEntry ze = (ZipEntry) e.nextElement();	
			String fileName = ze.getName();
			boolean accept = pattern.matcher(fileName).matches();
			if (accept) {
				retval.add(fileName);
			}
		}
		try {
			zf.close();
		} catch (IOException e1) {
			throw new Error(e1);
		}
		return retval;
	}

	public static Collection<String> getResourcesFromDirectory(File directory, Pattern pattern) {
		ArrayList<String> retval = new ArrayList<String>();
		File[] fileList = directory.listFiles();
		for (File file : fileList) {
			if (file.isDirectory()) {
				retval.addAll(getResourcesFromDirectory(file, pattern));
			} else {
				try {
					String fileName = file.getCanonicalPath();
					boolean accept = pattern.matcher(fileName).matches();
					if (accept) {
						retval.add(fileName);
					}
				} catch (IOException e) {
					throw new Error(e);
				}
			}
		}
		return retval;
	}



	/**
	 * This will copy a zip file to the given directory with the same name.
	 * @param file the file to copy from
	 * @param directory the directory of the new file.
	 */
	public static void copyZipFile(File file,String directory) throws IllegalArgumentException {
		Assert.notNull(file);
		Assert.notNull(directory);
		Assert.hasLength(directory);

		File newZip = new File(directory+File.separator+PathManipulator.getOnlyName(file.getAbsolutePath()));
		try {createFile(newZip,false);} 
		catch (IOException e1) 
		{e1.printStackTrace();}

		ZipInputStream zin=null;
		ZipOutputStream zout =null;
		try {
			zin = new ZipInputStream(new FileInputStream(file));
			zout = new ZipOutputStream(new FileOutputStream(newZip));
			ZipEntry entry=zin.getNextEntry();
			while(entry!=null){
				// Add ZIP entry to output stream.
				zout.putNextEntry(new ZipEntry(entry.getName()));

				// Transfer bytes from the ZIP file to the output file
				int len;
				byte[] buf = new byte[1024];
				while ((len = zin.read(buf)) >= 0) 
					zout.write(buf, 0, len);

				entry=zin.getNextEntry();
			}
		} 

		catch (FileNotFoundException e) 
		{e.printStackTrace();} 

		catch (IOException e) 
		{e.printStackTrace();}


		finally {

			try {
				if(zin!=null)
					zin.close();
				if(zout!=null){
					zout.flush();
					zout.close();
				}
			} 

			catch (IOException e) 
			{e.printStackTrace();}

		}//end finally
	}//end copyZipFile




	/**
	 * This will compress an array of files
	 * to the given destination and name of and format of zip file
	 * @param format the format for the zip file
	 * @param destination the destination directory for the zip file
	 * @param toCompress the files to compress
	 * @param nameOfZipFile the name of the zip file
	 * @throws IllegalArgumentException if to any of the arguments are null,
	 *  any of the given strings have no length, or any of the given files don't exist
	 */
	public static void compressFiles(String format,String destination, File[] toCompress,String nameOfZipFile){
		Assert.notNull(format);
		Assert.notNull(destination);
		Assert.notNull(toCompress);
		Assert.notNull(nameOfZipFile);
		Assert.hasLength(format);
		Assert.hasLength(destination);
		Assert.hasLength(nameOfZipFile);
		Assert.notEmpty(toCompress);
		File dest = new File(destination);

		if(!dest.exists())
			throw new IllegalArgumentException("Destination must exist.");

		for(File f : toCompress)
			if(!f.exists())
				throw new IllegalArgumentException("Files given to compress must exist.");
		File newZipFile=null;
		//Check for trailing slash
		if(destination.indexOf(File.separator)==destination.length()-1)
			newZipFile = new File(destination + nameOfZipFile + format);
		else
			newZipFile = new File(destination + File.separator +  nameOfZipFile + format);

		try {createFile(newZipFile,false);} 
		catch (IOException e1) 
		{e1.printStackTrace();}

		for(File f : toCompress)
			FileMoverUtil.addZipEntry(newZipFile, f);
	}

	/**
	 * This will compress an array of files
	 * to the given destination and name of and format of zip file
	 * @param format the format for the zip file
	 * @param destination the destination directory for the zip file
	 * @param toCompress the files to compress
	 * @param nameOfZipFile the name of the zip file
	 *  @throws IllegalArgumentException if to any of the arguments are null,
	 *  any of the given strings have no length, or any of the given files don't exist
	 */

	public static void compressFiles(String format,String destination, List<File> toCompress,String nameOfZipFile){
		Assert.notNull(format);
		Assert.notNull(destination);
		Assert.notNull(toCompress);
		Assert.notNull(nameOfZipFile);
		Assert.hasLength(format);
		Assert.hasLength(destination);
		Assert.hasLength(nameOfZipFile);
		Assert.notEmpty(toCompress);
		File dest = new File(destination);

		if(!dest.exists())
			throw new IllegalArgumentException("Destination must exist.");

		for(File f : toCompress)
			if(!f.exists())
				throw new IllegalArgumentException("Files given to compress must exist.");
		File newZipFile=null;
		//Check for trailing slash
		if(destination.indexOf(File.separator)==destination.length()-1)
			newZipFile = new File(destination + nameOfZipFile + format);
		else
			newZipFile = new File(destination + File.separator +  nameOfZipFile + format);

		try {createFile(newZipFile,false);} 
		catch (IOException e1) 
		{e1.printStackTrace();}

		for(File f : toCompress)
			FileMoverUtil.addZipEntry(newZipFile, f);
	}

	/**
	 * This will compress an array of files
	 * to the given destination and name of and format of zip file
	 * @param format the format for the zip file
	 * @param destination the destination directory for the zip file
	 * @param toCompress the files to compress
	 * @param nameOfZipFile the name of the zip file
	 * @throws IllegalArgumentException if to any of the arguments are null,
	 *  any of the given strings have no length, or any of the given files don't exist
	 */

	public static void compressFiles(String format,String destination, Set<File> toCompress,String nameOfZipFile){
		Assert.notNull(format);
		Assert.notNull(destination);
		Assert.notNull(toCompress);
		Assert.notNull(nameOfZipFile);
		Assert.hasLength(format);
		Assert.hasLength(destination);
		Assert.hasLength(nameOfZipFile);
		Assert.notEmpty(toCompress);
		File dest = new File(destination);

		if(!dest.exists())
			throw new IllegalArgumentException("Destination must exist.");

		for(File f : toCompress)
			if(!f.exists())
				throw new IllegalArgumentException("Files given to compress must exist.");
		File newZipFile=null;
		//Check for trailing slash
		if(destination.indexOf(File.separator)==destination.length()-1)
			newZipFile = new File(destination + nameOfZipFile + format);
		else
			newZipFile = new File(destination + File.separator +  nameOfZipFile + format);

		try {createFile(newZipFile,false);} 
		catch (IOException e1) 
		{e1.printStackTrace();}

		for(File f : toCompress)
			FileMoverUtil.addZipEntry(newZipFile, f);
	}


	/**
	 * THis takes a given URL, downloads it, and puts it in to a file
	 * @param toConvert the url to convert to a file
	 * @param outputDirectory the output directory this will go in
	 * @return the file that was created, or null i ther was an error
	 */
	public static File URLtoFile(URL toConvert,File outputDirectory){
		try {
			List<String> contents=readURL(toConvert);
			writeStringListToFile(contents,outputDirectory.getAbsolutePath());
			File ret = new File(outputDirectory.getAbsolutePath() + File.separator + toConvert.getFile());
			return ret;

		} 
		catch (UnknownHostException e) 
		{e.printStackTrace();} 
		catch (SocketException e) 
		{e.printStackTrace();} 
		catch (FileNotFoundException e) 
		{e.printStackTrace();}
		return null;

	}
	/**
	 * This reads from a  url, and returns the contents in a list of strings
	 * @return the url contents in a list of strings
	 * @throws UnknownHostException if there is no host to connect to
	 * @throws SocketException if the connection is reset
	 * @throws FileNotFoundException if the file wasn't found on the server
	 */
	private static List<String> readURL(URL toRead) throws UnknownHostException,SocketException,FileNotFoundException {
		List<String> ret = new ArrayList<String>();
		//Connect to the url
		URLConnection conn=null;
		try {conn=toRead.openConnection();} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		//Read in the url and copy the list
		BufferedInputStream bi =null;

		try {bi= new BufferedInputStream(conn.getInputStream());} 

		catch (IOException e) 
		{e.printStackTrace();};

		//Problem reading from the connnection, return ret
		if(bi==null)
			return ret;

		//Translate the connections input stram
		BufferedReader br = null;
		br = new BufferedReader(new InputStreamReader(bi));
		//Read the url line by line
		String line="";
		try {
			while((line=br.readLine())!=null)
				ret.add(line);
		} 
		catch (IOException e) 
		{e.printStackTrace();}

		return ret;
	}//end readURL


	/**
	 * Search all zip entries for the given text starting from a directory or file
	 * @param text the text to search for
	 * @param startingPoint the starting file or directory
	 * @param recursive whether to recurse all the way or not
	 * @return the zip entries with the given text in them
	 */
	public static Set<File> zipEntriesWithText(String text,File startingPoint,boolean recursive) {
		Set<File> files = loadZipEntries(startingPoint,recursive);
		Set<File> ret = new HashSet<File>();
		for(File  f : files) {
			if(FileMoverUtil.fileHasText(text, f))
				ret.add(f);
		}
		return ret;

	}//end zipEntriesWithText
	/**
	 * Search all zip entries for the given text starting from a directory or file
	 * @param text the text to search for
	 * @param startingPoint the starting file or directory
	 * @param recursive whether to recurse all the way or not
	 * @param fileFilter a file filter for filtering zip entries to search for text
	 * @return the zip entries with the given text in them
	 */
	public static Set<File> zipEntriesWithText(String text,File startingPoint,boolean recursive,String fileFilter) {
		Set<File> files = loadZipEntries(startingPoint,recursive,fileFilter);
		Set<File> ret = new HashSet<File>();
		for(File  f : files) {
			if(FileMoverUtil.fileHasText(text, f))
				ret.add(f);
		}
		return ret;

	}//end zipEntriesWithText
	/**
	 * This will return a list of zip entries starting from a given directory
	 * @param startingPoint file as a starting point
	 * @param recursive whether all files should be scanned starting from starting point
	 * @param fileFilter a filter fo only loading files with the names containig fileFilter
	 * @return a list of loaded zip entries 
	 */
	public static Set<File> loadZipEntries(File startingPoint,boolean recursive,String fileFilter) {
		Assert.isTrue(startingPoint.exists(), "Given file doesn't exist");
		Set<File> files = new HashSet<File>();
		if(startingPoint.isDirectory()) {
			Set<File> subdirs=subDirectories(startingPoint,recursive,new HashSet<File>());
			for(File f : subdirs) {
				for(File f1 : f.listFiles()) {
					if(isZip(f1)) {
						try {
							ZipFile zip = new ZipFile(f1);

							Enumeration<? extends ZipEntry> entries=zip.entries();
							while(entries.hasMoreElements()) {
								ZipEntry entry=entries.nextElement();
								String name=entry.getName();
								if(name.contains(fileFilter)) {
									File load=FileMoverUtil.loadZipEntry(f1.getAbsolutePath(), f.getParent(), name);
									files.add(load);
								}


							}
							zip.close();
						} catch (ZipException e) {

							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}//end if
		else if(isZip(startingPoint)){
			try {
				ZipFile zip = new ZipFile(startingPoint);
				Enumeration<? extends ZipEntry> entries=zip.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry=entries.nextElement();
					String name=entry.getName();
					if(name.contains(fileFilter)) {
						File load=FileMoverUtil.loadZipEntry(startingPoint.getAbsolutePath(), startingPoint.getParent() + "/" + name, name);
						files.add(load);
					}


				}
				zip.close();
			} catch (ZipException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return files;
	}//end findZipEntries
	/**
	 * This will return a list of zip entries starting from a given directory
	 * @param startingPoint file as a starting point
	 * @param recursive whether all files should be scanned starting from starting point
	 * @return a list of loaded zip entries 
	 */
	public static Set<File> loadZipEntries(File startingPoint,boolean recursive) {
		Assert.isTrue(startingPoint.exists(), "Given file doesn't exist");
		Set<File> files = new HashSet<File>();
		if(startingPoint.isDirectory()) {
			Set<File> subdirs=subDirectories(startingPoint,recursive,new HashSet<File>());
			for(File f : subdirs) {
				for(File f1 : f.listFiles()) {
					if(isZip(f1)) {
						try {
							ZipFile zip = new ZipFile(f1);

							Enumeration<? extends ZipEntry> entries=zip.entries();
							while(entries.hasMoreElements()) {
								ZipEntry entry=entries.nextElement();
								String name=entry.getName();
								File load=FileMoverUtil.loadZipEntry(f1.getAbsolutePath(), f.getParent(), name);
								files.add(load);

							}
							zip.close();
						} catch (ZipException e) {

							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}//end if
		else if(isZip(startingPoint)){
			try {
				ZipFile zip = new ZipFile(startingPoint);
				Enumeration<? extends ZipEntry> entries=zip.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry=entries.nextElement();
					String name=entry.getName();

					File load=FileMoverUtil.loadZipEntry(startingPoint.getAbsolutePath(), startingPoint.getParent() + "/" + name, name);
					files.add(load);

				}
				zip.close();
			} catch (ZipException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return files;
	}//end findZipEntries


	/**
	 * This will return a list of files based on the given query
	 * @param query the query for the list of files
	 * @param startingPoint file as a starting point
	 * @param recursive whether all files should be scanned starting from starting point
	 * @return a list of loaded zip entries based on the given query
	 */
	public static Set<File> findZipEntries(String query,File startingPoint,boolean recursive) {
		Assert.isTrue(startingPoint.exists(), "Given file doesn't exist");
		Set<File> files = new HashSet<File>();
		if(startingPoint.isDirectory()) {
			Set<File> subdirs=subDirectories(startingPoint,recursive,new HashSet<File>());
			for(File f : subdirs) {
				for(File f1 : f.listFiles()) {
					if(isZip(f1)) {
						try {
							ZipFile zip = new ZipFile(f1);
							Enumeration<? extends ZipEntry> entries=zip.entries();
							while(entries.hasMoreElements()) {
								ZipEntry entry=entries.nextElement();
								String name=entry.getName();
								if(name.matches(query)) {
									File load=FileMoverUtil.loadZipEntry(f1.getAbsolutePath(), f.getAbsolutePath() + "/" + name, name);
									files.add(load);
								}
							}
							zip.close();
						} catch (ZipException e) {

							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}//end if
		else if(isZip(startingPoint)){
			try {
				ZipFile zip = new ZipFile(startingPoint);
				Enumeration<? extends ZipEntry> entries=zip.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry=entries.nextElement();
					String name=entry.getName();
					if(name.matches(query)) {
						File load=FileMoverUtil.loadZipEntry(startingPoint.getAbsolutePath(), startingPoint.getParent() + "/" + name, name);
						files.add(load);
					}
				}
				zip.close();
			} catch (ZipException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return files;
	}//end findZipEntries







	/**
	 * This will return a list of files based on the given query and only search the file names
	 * containing the text with file filter
	 * @param query the query for the list of files
	 * @param startingPoint file as a starting point
	 * @param recursive whether all files should be scanned starting from starting point
	 * @param fileFilter a contains check for entry names f
	 * @return a list of loaded zip entries based on the given query
	 */
	public static Set<File> findZipEntries(String query,File startingPoint,boolean recursive,String fileFilter) {
		Assert.isTrue(startingPoint.exists(), "Given file doesn't exist");
		Set<File> files = new HashSet<File>();
		if(startingPoint.isDirectory()) {
			Set<File> subdirs=subDirectories(startingPoint,recursive,new HashSet<File>());
			for(File f : subdirs) {
				for(File f1 : f.listFiles()) {
					if(isZip(f1)) {
						try {
							ZipFile zip = new ZipFile(f1);
							Enumeration<? extends ZipEntry> entries=zip.entries();
							while(entries.hasMoreElements()) {
								ZipEntry entry=entries.nextElement();
								String name=entry.getName();
								if(name.matches(query) && name.contains(fileFilter)) {
									File load=FileMoverUtil.loadZipEntry(f1.getAbsolutePath(), f.getAbsolutePath() + "/" + name, name);
									files.add(load);
								}
							}
							zip.close();
						} catch (ZipException e) {

							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}//end if
		else if(isZip(startingPoint)){
			try {
				ZipFile zip = new ZipFile(startingPoint);
				Enumeration<? extends ZipEntry> entries=zip.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry=entries.nextElement();
					String name=entry.getName();
					if(name.matches(query)) {
						File load=FileMoverUtil.loadZipEntry(startingPoint.getAbsolutePath(), startingPoint.getParent() + "/" + name, name);
						files.add(load);
					}
				}
				zip.close();
			} catch (ZipException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return files;
	}//end findZipEntries

	/**
	 * Return the list of files that contain files matching the specified query
	 * @param query the query to use
	 * @param recursive whether the search is recursive or not
	 * @param startingPoint the starting file
	 * @return the list of files containing files matching the specified query
	 * 
	 */
	public static Set<File> filesContainingZipEntries(String query,boolean recursive,File startingPoint) {
		Assert.isTrue(startingPoint.exists(), "Given file doesn't exist");
		Set<File> files = new HashSet<File>();
		if(startingPoint.isDirectory()) {
			Set<File> subdirs=subDirectories(startingPoint,recursive,new HashSet<File>());
			for(File f : subdirs) {
				for(File f1 : f.listFiles()) {
					if(isZip(f1)) {
						try {
							ZipFile zip = new ZipFile(f1);
							Enumeration<? extends ZipEntry> entries=zip.entries();
							while(entries.hasMoreElements()) {
								ZipEntry entry=entries.nextElement();
								String name=entry.getName();
								if(name.contains(query)) {
									files.add(f1);
								}
							}
							zip.close();
						} catch (ZipException e) {

							e.printStackTrace();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}


			}
		}//end if
		else if(isZip(startingPoint)){
			try {
				ZipFile zip = new ZipFile(startingPoint);
				Enumeration<? extends ZipEntry> entries=zip.entries();
				while(entries.hasMoreElements()) {
					ZipEntry entry=entries.nextElement();
					String name=entry.getName();
					if(name.matches(query)) {
						File load=FileMoverUtil.loadZipEntry(startingPoint.getAbsolutePath(), startingPoint.getParent() + "/" + name, name);
						files.add(load);
					}
				}
				zip.close();
			} catch (ZipException e) {

				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}


		return files;
	}



	/**
	 * Attempts to detect an archive based on the format
	 * @param file the file to test
	 * @return true if the file is an archive, false otherwise
	 */
	public static boolean isZip(File file) {
		String format=PathManipulator.getFormat(file);
		if(format==null) return false;
		//replace beginning dots
		if(format.charAt(0)=='.')
			format=format.substring(1);
		for(String s : zipFormats) {
			if(s.equals(format))
				return true;
		}
		return false;
	}//end isZip


	/**
	 * This loads a specified zip entry from a zip file
	 * @param zipFrom the zip file to extract from
	 * @param destination the destination directory of the inflated entry
	 * @param entryName the entry name to search for
	 * @return the loaded file or null, if not successful
	 * @throws IllegalArgumentException if any of the given strings are null,zero length, or if 
	 * the zip file or directory given don't exist
	 */
	public static File loadZipEntry(String zipFrom,String destination, String entryName) throws IllegalArgumentException {
		Assert.notNull(zipFrom);
		Assert.notNull(destination);
		Assert.notNull(entryName);
		Assert.hasLength(zipFrom);
		Assert.hasLength(destination);
		Assert.hasLength(entryName);


		File destinationDir = new File(destination);
		File zip = new File(zipFrom);
		File destFile=null;
		if(!destinationDir.exists())
			throw new IllegalArgumentException("Directory doesn't exist.");

		if(!destinationDir.canWrite())
			throw new IllegalArgumentException("can't write to this directory");

		if(!destinationDir.isDirectory())
			throw new IllegalArgumentException("estination isn't a directory");
		if(!zip.exists())
			throw new IllegalArgumentException("Zip file doesn't exist.");

		if(!zip.canRead())
			throw new IllegalArgumentException("Can't read from this zip file");

		ZipInputStream zin=null;
		try {
			zin = new ZipInputStream(new FileInputStream(zip));
			//Loop through the zip file until the proper archive with the given name is found
			ZipEntry entry=zin.getNextEntry();
			while(entry!=null){
				String name=entry.getName();
				if(entryName.equals(name))
				{
					//File was found, make the proper directories, to 	 the file, and inflate it
					destFile = new File(destination + File.separator + entryName);


					createFile(destFile,entry.isDirectory());
					if(!destFile.isDirectory()) {
						BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
						copyInputStream(zin,bos);
						return destFile;
					}
					else break;
				}
				//Keep going
				entry=zin.getNextEntry();
			}
		} 

		catch (FileNotFoundException e) 
		{e.printStackTrace();} 
		catch (IOException e) 
		{e.printStackTrace();}

		return destFile;
	}//end loadZipEntry

	/**
	 * This adds a zip entry to a given zip file
	 * @param addTo the zip file to add to
	 * @param toAdd the file to add to the zip file
	 * @throws IllegalArgumentException if addto or to add is null or doesn't exist
	 */
	public static void addZipEntry(File addTo,File toAdd) throws IllegalArgumentException{
		Assert.notNull(addTo);
		Assert.notNull(toAdd);

		if(!addTo.exists() || !toAdd.exists())
			throw new IllegalArgumentException("Files given must exist.");
		BufferedInputStream bis=null;
		ZipOutputStream zout =null;
		try {
			bis = new BufferedInputStream(new FileInputStream(toAdd));
			zout = new ZipOutputStream(new FileOutputStream(addTo));
			copyInputStream(bis,zout);
		} 

		catch (FileNotFoundException e) 
		{e.printStackTrace();} 

		catch (IOException e) 
		{e.printStackTrace();}


		finally {

			try {

				if(zout!=null){
					zout.flush();
					zout.close();
				}
			} 

			catch (IOException e) 
			{e.printStackTrace();}

		}//end finally


	}//end addZipEntry

	/**
	 * This will extract a zip file to the given destination
	 * @param destination the destination of where to extract from
	 * @param zipFrom the zip file to retrieve entries from
	 * @return the destination directory or file
	 * @throws IllegalArgumentException if destination or zipFrom is null
	 * or if destination or zipFrom have 0 length or if destination directory or zipFrom don't exist
	 * or if destinationDir doesn't have write permissions
	 */
	public static File extract(String destination, String zipFrom) throws IllegalArgumentException{
		Assert.notNull(destination);
		Assert.notNull(zipFrom);
		Assert.hasLength(destination);
		Assert.hasLength(zipFrom);

		File destinationDir = new File(destination);
		File zip = new File(zipFrom);

		if(!destinationDir.exists())
			throw new IllegalArgumentException("Directory doesn't exist.");

		if(!destinationDir.canWrite())
			throw new IllegalArgumentException("can't write to this directory");

		if(!destinationDir.isDirectory())
			throw new IllegalArgumentException("estination isn't a directory");
		if(!zip.exists())
			throw new IllegalArgumentException("Zip file doesn't exist. " + zip.getAbsolutePath());

		if(!zip.canRead())
			throw new IllegalArgumentException("Can't read from this zip file");


		ZipInputStream zin=null;

		File destFile=null;
		try {
			zin = new ZipInputStream(new FileInputStream(zip));
			ZipEntry entry=zin.getNextEntry();
			//Find the zip file's name without format
			int i=zipFrom.indexOf(".");
			zipFrom=zipFrom.substring(0,i);
			destFile = new File(destination + File.separator + entry.getName());
			//Iterate through the zip file, until a name is found, or all the entries have been iterated over
			//Extract and create any directories needed to put the file where it needs to go
			while(entry!=null){
				String name=entry.getName();
				if(entry.isDirectory()) {
					destFile = new File(destination + File.separator + name);
					destFile.mkdirs();
					entry=zin.getNextEntry();
					continue;
				}
				destFile = new File(destination + File.separator + entry.getName());

				createFile(destFile,entry.isDirectory());
				BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(destFile));
				copyInputStream(zin,bos);
				IOUtils.closeQuietly(bos);
				destFile=null;
				if(log.isDebugEnabled())
					log.debug("Extracting entry: " + entry.getName());
				entry=zin.getNextEntry();
			}
		} 

		catch (FileNotFoundException e) 
		{e.printStackTrace();} 
		catch (IOException e) 
		{e.printStackTrace();}
		IOUtils.closeQuietly(zin);

		return destFile;

	}//end extract

	/**
	 * This takes a list of strings and writes it to a file at the given destination
	 * @param toWrite the list of strings to write
	 * @param destination the destination to write
	 * @return true if the file is written to successfully false otherwise
	 * @throws IllegalArgumentException if toWRite is null or has no elements or if destination is null or 
	 * has no characters
	 */
	public static boolean writeStringListToFile(List<String> toWrite,String destination) throws IllegalArgumentException{
		Assert.notNull(destination);
		Assert.notNull(toWrite);
		Assert.hasLength(destination);
		Assert.notEmpty(toWrite);
		File dest = new File(destination);
		BufferedWriter br = null;
		try {
			createFile(dest,false);
			br = new BufferedWriter(new FileWriter(dest));
			for(String s :toWrite)
				br.write(s + "\n");
			
		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(br!=null){
				try {br.flush(); br.close();} 
				catch (IOException e)
				{e.printStackTrace();}
			}

		}
		return true;
	}//end writeStringListToFile

	/**
	 * This takes an array of strings and writes it to a file at the given destination
	 * @param toWrite the list of strings to write
	 * @param destination the destination to write
	 * @return true if the file is written to successfully false otherwise
	 * @throws IllegalArgumentException if toWRite is null or has no elements or if destination is null or 
	 * has no characters
	 */
	public static boolean writeStringListToFile(String[] toWrite,String destination) throws IllegalArgumentException{
		Assert.notNull(toWrite);
		Assert.notNull(destination);
		Assert.hasLength(destination);

		File dest = new File(destination);
		BufferedWriter br = null;
		try {
			createFile(dest,false);
			br = new BufferedWriter(new FileWriter(dest));
			for(String s :toWrite)
				br.write(s + "\n");

		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(br!=null){
				try {br.close();} 
				catch (IOException e)
				{e.printStackTrace();}
			}

		}
		return true;
	}//end writeStringListToFile
	/**
	 * This takes an array of strings and writes it to a file at the given destination
	 * @param toWrite the list of strings to write
	 * @param destination the destination to write
	 * @return true if the file is written to successfully false otherwise
	 * @throws IllegalArgumentException if toWRite is null or has no elements or if destination is null or 
	 * has no characters
	 */
	public static boolean writeStringListToFile(Set<String> toWrite,String destination) throws IllegalArgumentException {
		Assert.notNull(destination);
		Assert.notNull(toWrite);
		Assert.hasLength(destination);
		Assert.notEmpty(toWrite);


		File dest = new File(destination);

		BufferedWriter br = null;
		try {
			createFile(dest,false);
			br = new BufferedWriter(new FileWriter(dest));
			for(String s :toWrite)
				br.write(s + "\n");

		} 
		catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		finally {
			if(br!=null){
				try {br.close();} 
				catch (IOException e)
				{e.printStackTrace();}
			}

		}
		return true;
	}//end writeStringListToFile


	/**
	 * This returns a list of files with the given target string.
	 * @param target the string to search for
	 * @param startDirectory the start directory to search from
	 * @return a list of files with the given target string
	 */
	public static Map<File,File> grep(String target, String startDirectory){


		File file = new File(startDirectory);

		if(!file.exists())
			throw new IllegalArgumentException("File must exist.");

		if(!file.isDirectory())
			throw new IllegalArgumentException("Given file must be a directory.");
		Map<File,File> ret = new HashMap<File,File>(10);

		Set<File> subDirs=subDirectories(file,true, new HashSet<File>());
		for(File f: subDirs){
			if(f.isDirectory())
				continue;
			if(fileHasText(target,f))
				ret.put(f,f);

		}
		return ret;
	}//end grep

	/**
	 * This writes the contents of a file to a list of strings
	 * @param toRead the file to read
	 * @return a list of strings with the file contents
	 * @throws IllegalArgumentException if file is null,or if file doesn't exist, and if the file is a directory
	 * 
	 */
	public static List<String> fileContents(File toRead){
		Assert.notNull(toRead);
		if(!toRead.exists())
			throw new IllegalArgumentException("Given file must exist.");
		if(toRead.isDirectory())
			throw new IllegalArgumentException("Given file must be a file, not a directory.");
		String line="";
		List<String> ret = new ArrayList<String>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(toRead));
			while((line=br.readLine())!=null){
				ret.add(line);
			}
		} 
		catch (FileNotFoundException e)	{
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}//end fileContents
	/**
	 * This reads a CSV file in to a multi dimensional list of strings
	 * @param toRead the file to read
	 * @return a multi dimensional list of strings such that each list is a line
	 * and each index of each row is an entity separated by commas
	 */
	public static List<List<String>> readCSV(File toRead) {
		Assert.notNull(toRead);
		if(!toRead.exists())
			throw new IllegalArgumentException("Given file must exist.");
		if(toRead.isDirectory())
			throw new IllegalArgumentException("Given file must be a file, not a directory.");
		String line="";
		List<List<String>> ret = new ArrayList<List<String>>();
		try {
			BufferedReader br = new BufferedReader(new FileReader(toRead));
			while((line=br.readLine())!=null){
				List<String> list = new ArrayList<String>();
				String[] sep=line.split(",");
				for(String s : sep)
					list.add(s);
				ret.add(list);
			}
		} 
		catch (FileNotFoundException e)	{
			e.printStackTrace();
			return null;
		} 
		catch (IOException e) {
			e.printStackTrace();
			return null;
		}
		return ret;
	}//end readCSV

	/**
	 * This returns whether a file has a given line of text or not
	 * @param regex the regular expression to search for
	 * @param toSearch the file to search
	 * @return true if the file contained the given regex, false otherwise
	 * @throws IllegalArgumentException if given regex or file was null, or file doesn't exist, or is a directory
	 */
	public static boolean fileHasText(String regex, File toSearch) throws IllegalArgumentException{


		if(!toSearch.exists())
			return false;
		if(toSearch.isDirectory())
			return false;
		String line="";
		System.out.println("Searching: " + toSearch.getAbsolutePath());
		try {
			BufferedReader br = new BufferedReader(new FileReader(toSearch));
			while((line=br.readLine())!=null){
				if(line.contains(regex))
					return true;
			}
		} 
		catch (FileNotFoundException e) 
		{e.printStackTrace();} 
		catch (IOException e) 
		{e.printStackTrace();}
		return false;
	}//end fileHasText



	/**
	 * This gathers a list of all possible directories below the given file.
	 * @param file the file to check sub directories for
	 * @return a list of subdirectories for the given file.
	 */
	public static Set<File> subDirectories(File file,boolean recursive,Set<File> soFar) throws IllegalArgumentException{
		Assert.notNull(file);

		if(!file.exists())
			throw new IllegalArgumentException("File must exist.");

		if(!file.isDirectory())
			return soFar;
		else {
			soFar.add(file);
			for(File f : file.listFiles()){
				if(f.isDirectory()) {
					soFar.add(f);
					if(f.isDirectory() && recursive) {
						soFar=subDirectories(f,recursive,soFar);
					}
				}

			}
			return soFar;
		}
	}//end subDirectories


	/**
	 * This returns whether the given absolute path has write permissions or not.
	 * @param directoryToTest the file or directory to test for
	 * @return if the file has write permissions or not
	 * @throws IllegalArgumentException if the given directory is null,or given
	 * directory doesn't exist.
	 */
	public boolean hasWritePermissions(String directoryToTest) throws IllegalArgumentException {
		Assert.notNull(directoryToTest);
		Assert.hasLength(directoryToTest);
		File test = new File(directoryToTest);
		if(!test.exists())
			throw new IllegalArgumentException("File must exist.");
		return test.canWrite();

	}
	/**
	 * This will create a new file from a given file.
	 * It will first check if there is a file that already exists, if so
	 * it will delete the already existing file and create a new one. 
	 * @param toCreate the file to create
	 * @throws IOException if there is something that goes wrong with the creation of the file
	 * such as a malformed path.
	 */
	public static void createFile(File toCreate,boolean isDirectory) throws IOException{
		Assert.notNull(toCreate);
		boolean alreadyDirectory=false;
		if(toCreate.exists() && !toCreate.isDirectory()) {
			alreadyDirectory=toCreate.isDirectory();
			toCreate.delete();
		}
		if(!toCreate.exists() && isDirectory) {
			toCreate.mkdirs();
			return;
		}

		File parentDir=toCreate.getParentFile();
		
		if(parentDir!=null && !parentDir.exists())
			parentDir.mkdirs();
		if(!alreadyDirectory && !isDirectory) {
			try {
				toCreate.createNewFile();
			}catch(IOException e) {
				throw new IOException("Failed to create file: "  + toCreate.getAbsolutePath());
			}
		}
		else toCreate.mkdirs();
	}//end createFile


	/**
	 * This gets the properties from a file in a format with a regex (separator character chosen by the user).
	 * It will read the characters from a file and place them and return an array with all of the various components.
	 * @param file the file to extract the properties from.
	 * @param regex the character separating the strings in the text file.
	 * @return a string array of all the parts separated by the given regex.
	 * @throws IllegalArgumentException if file or regex is null.
	 */
	public static  String[] getProperties(File file, String regex) throws IllegalArgumentException{

		PathManipulator.checkNull(file);
		PathManipulator.checkNull(regex);
		Assert.hasLength(regex);
		try {
			BufferedReader in = new BufferedReader(new FileReader(file));
			String line=in.readLine();
			String[] ret=line.split(regex);
			return ret;

		} catch (FileNotFoundException e) {} 
		catch (IOException e) {}
		return null;
	}//end getProperties


	/**
	 * This lists out all of the zip entries of a given zip file.
	 * It will print to standard out, normally used for debugging.
	 * @param zipFile the file to list out all entries for.
	 * @throws IllegalArgumentException, if file is null,or if the file doesn't exist.
	 */
	public static void listZipEntries(File zipFile){
		if(zipFile==null)
			throw new IllegalArgumentException("File can't be null.");
		if(!zipFile.exists())
			throw new IllegalArgumentException("File doesn't exist.");
		ZipInputStream zin=null;
		try {
			zin = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry entry=zin.getNextEntry();
			while(entry!=null){
				System.out.println(entry.getName());
				entry=zin.getNextEntry();
			}
			zin.close();

		} 
		catch (FileNotFoundException e) 
		{e.printStackTrace();} 
		catch (IOException e) 
		{e.printStackTrace();}
	}//end listZipEntries


	/**
	 * This deletes a zip entry from a given zip file.
	 * @param zipFile the zip file to delete entries from.
	 * @param files the files to delete
	 * @throws IOException if there is a problem with the zip file.
	 * @throws IllegalArgumentException if file doesn't exist.
	 */
	public static void deleteZipEntryPrompt(File zipFile,
			String[] files) throws IOException {
		if(!zipFile.exists())
			throw new IllegalArgumentException("File doesn't exist.");
		// get a temp file
		File tempFile = File.createTempFile(zipFile.getName(), null);
		// delete it, otherwise you cannot rename your existing zip to it.
		tempFile.delete();
		tempFile.deleteOnExit();
		boolean renameOk=zipFile.renameTo(tempFile);
		if (!renameOk)
		{
			throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			System.out.println("Current: " + name);
			boolean toBeDeleted = false;
			for (String f : files) {
				if(name.contains(f)){
					Console c=System.console();
					String delete=c.readLine("Would you like to delete: " + name + "? (y/n)");
					if(delete.contains("y"))
						toBeDeleted=true;

					break;
				}
				/*	
				if (f.contains(name)) {
					toBeDeleted = true;
					System.out.println("Deleting: " + name);
					continue;
				}
				 */
			}
			if (!toBeDeleted) {
				// Add ZIP entry to output stream.
				zout.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) >= 0) {
					zout.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		// Close the streams		
		zin.close();
		// Compress the files
		// Complete the ZIP file
		zout.close();
		tempFile.delete();
	}//end deleteZipEntry




	/**
	 * This deletes a zip entry from a given zip file.
	 * @param zipFile the zip file to delete entries from.
	 * @param files the files to delete
	 * @throws IOException if there is a problem with the zip file.
	 * @throws IllegalArgumentException if file doesn't exist.
	 */
	public static void deleteZipEntry(File zipFile,
			String[] files) throws IOException {
		if(!zipFile.exists())
			throw new IllegalArgumentException("File doesn't exist.");
		// get a temp file
		File tempFile = File.createTempFile(zipFile.getName(), null);
		// delete it, otherwise you cannot rename your existing zip to it.
		tempFile.delete();
		tempFile.deleteOnExit();
		boolean renameOk=zipFile.renameTo(tempFile);
		if (!renameOk)
		{
			throw new RuntimeException("could not rename the file "+zipFile.getAbsolutePath()+" to "+tempFile.getAbsolutePath());
		}
		byte[] buf = new byte[1024];

		ZipInputStream zin = new ZipInputStream(new FileInputStream(tempFile));
		ZipOutputStream zout = new ZipOutputStream(new FileOutputStream(zipFile));

		ZipEntry entry = zin.getNextEntry();
		while (entry != null) {
			String name = entry.getName();
			System.out.println("Current: " + name);
			boolean toBeDeleted = false;
			for (String f : files) {


				if (f.equals(name)) {
					toBeDeleted = true;
					System.out.println("Deleting: " + name);
					continue;
				}

			}


			if (!toBeDeleted) {
				// Add ZIP entry to output stream.
				zout.putNextEntry(new ZipEntry(name));
				// Transfer bytes from the ZIP file to the output file
				int len;
				while ((len = zin.read(buf)) >= 0) {
					zout.write(buf, 0, len);
				}
			}
			entry = zin.getNextEntry();
		}
		// Close the streams		
		zin.close();
		// Compress the files
		// Complete the ZIP file
		zout.close();
		tempFile.delete();
	}//end deleteZipEntry


	/**
	 * 
	 * Given a value, this will output something in the form of:
	 * <property name=\"somePropertyName\"/>
	 * <value>valueToWrite</value>
	 * </property>
	 * @param line the line currently being read.
	 * @param propName he property name to write.
	 * @param valueToWrite the value to write
	 * @param in the reader to use.
	 * @param bos the outputstream to use.
	 * @throws IOException if any of the i/o from the reader, or the output stream throws an exception.
	 */
	public static  void writeProperty(String line,String propName,String valueToWrite,BufferedReader in,BufferedOutputStream bos) throws IOException{

		if(!line.contains("value") && !line.contains(propName))
			bos.write(line.getBytes());
		String property="<property name=\"" + propName+ "\">";
		System.out.println("About to write: " + property);
		bos.write(property.getBytes());
		String toWrite=("<value>" + valueToWrite + "</value>");
		bos.write("\n".getBytes());

		System.out.println("About to write: " + toWrite);

		bos.write(toWrite.getBytes());
		//System.out.println("Writing: " + toWrite);

		bos.write("\n".getBytes());

	}
	/**
	 * This returns a target file based on a search with the drive
	 * the user specifies.
	 * @param drive the drive the user wants to start the search with.
	 * @param target the target string to look for
	 * @return the file with which the search found, null otherwise.
	 */
	public static  File findDirectory(char drive,String target){

		if( target==null)
			throw new IllegalArgumentException("Neither of the parameters can be null.");
		String topDrive=drive+ ":/";
		File file = new File(topDrive);
		return findDirectory(target,file);
	}//end findDirectory
	/**
	 * This returns a target file based on a search with the drive
	 * the user specifies.
	 * @param topLevelDir the top level directory the user wants to start the search with.
	 * @param target the target string to look for
	 * @return the file with which the search found, null otherwise.
	 */
	public static  File findDirectory(String topLevelDir,String target){
		if(topLevelDir==null ||  target==null)
			throw new IllegalArgumentException("Neither of the parameters can be null.");

		if(topLevelDir.length() > 1)
			throw new IllegalArgumentException("Please input one letter for the drive letter.");

		File file = new File(topLevelDir);
		return findDirectory(target,file);
	}//end findDirectory

	/**
	 * This is a recursive algorithm for finding a specific file based on the given regex.
	 * @param regex the target name of the file to find.
	 * @param current the current file, user would input starting file. If current is null
	 * the method returns null
	 * @return the file with the given regex,null otherwise.
	 * @throws IllegalArgumentException if regex is null
	 */
	public static  File findDirectory(String regex,File current) throws IllegalArgumentException{
		Assert.notNull(regex);
		Assert.hasLength(regex);
		if(current==null)
			return null;
		System.out.println("Current: " + current.getAbsolutePath());
		if(current.getAbsolutePath().contains(regex))
			return current;
		else if(current.isFile())
			return null;
		else {

			File[] subDirs=current.listFiles();
			File ret=null;
			if(subDirs!=null){
				for(File f: subDirs){
					if(f!=null && f.isDirectory()){
						File[] fSubSDirs=f.listFiles();
						//If there were any sub directories.
						if(fSubSDirs!=null){
							for(File f1: fSubSDirs){
								//recursively find other directories the target could be in.
								ret=findDirectory(regex,f1);
								if(ret!=null && ret.getAbsolutePath().contains(regex))
									return ret;
							}
						}
					}
					//recursively find other directories the target could be in.
					else 	
						ret=findTarget(regex,f);
				}
			}

			if(ret!=null)
				return ret;
			else
				return findDirectory(regex,ret);
		}//end else
	}//end findDirectory

	/**
	 * This returns a file containing the target string.
	 * @param target the target string to search for
	 * @param regex the File to search with
	 * @return the file if found, or null if not.
	 * @throws IllegalArgumentException if target is null or empty, or if regex is empty.
	 */
	private static File findTarget(String target,File regex)
			throws IllegalArgumentException {
		if(target==null || regex ==null)
			throw new IllegalArgumentException("Neither of the given paramters can be null.");
		if(target.equals("") || target.equals(" "))
			throw new IllegalArgumentException("Target can't be empty.");
		if(regex.getAbsolutePath().contains(target))
			return regex;

		else {
			File curr=null;
			//Iterate over all of the files in the directory.
			if(regex.isDirectory()){
				for(File f:regex.listFiles()){
					curr=findTarget(target,f);
					if(curr!=null && curr.getAbsolutePath().contains(target))
						return curr;
				}
			}
		}
		return null;
	}//end findTarget


	/**
	 * Given a file, it will copy it and move it to a directory specified
	 * by user.
	 * @param destination directory for copy
	 * @throws IllegalArgumentException if source or destination is null
	 * @throws RuntimeException if targetFile is null by the end.
	 * @throws IOException if the file can't be found
	 */
	public static  File copyFile(File source,String destination) throws IllegalArgumentException,RuntimeException,IOException {

		File targetFile=null;
		BufferedReader in =null;
		BufferedOutputStream bos =null;
		try {
			//Create the file.
			targetFile = new File(destination);
			if(targetFile.exists())
				targetFile.delete();
			try {
				if(!targetFile.exists()){
					//Race condition
					Thread.sleep(20);
					targetFile.createNewFile();

				}
				else 
					targetFile.createNewFile();
			}

			catch(IOException e)
			{e.printStackTrace();}

			in = new BufferedReader(new FileReader(source));
			bos = new BufferedOutputStream(new FileOutputStream(targetFile), 4096);
			String line="";
			while((line=in.readLine())!=null){
				bos.write(line.getBytes());
				bos.write("\n".getBytes());
			}


		}
		catch (Exception ex) 
		{ex.printStackTrace();}
		finally {
			if(in!=null)
				in.close();

			if(bos!=null){
				bos.flush();
				bos.close();
			}
		}//end finally
		return targetFile;
	}//end copyFile


	/**
	 * This writes a value with a given variable and property value to a properties file in the format of
	 * <property name=propVar = \"propVal\" />
	 * It will then automatically write to the given out put stream along with a new line character written
	 * to he stream.
	 * @param propVar the variable of the property to write
	 * @param propVal the value of the property to write.
	 * @param bos the outputstream to write to
	 * @throws IOException if any of the write attempts throw an exception
	 * @throws IllegalArgumentException, if any of the arguments are null.
	 */
	public static  void writeProperty(String propVar,String propVal,BufferedOutputStream bos) throws IOException,IllegalArgumentException {
		if(propVar==null || propVal ==null || bos ==null)
			throw new IllegalArgumentException("None of the arguments can be null.");

		String write="<property name= " + propVar + " value= " + propVal + "/>";
		bos.write(write.getBytes());
		bos.write("\n".getBytes());
	}
	/**
	 * This writes a value with a given variable and property value to a properties file in the format of
	 * propVar = propVal
	 * It will then automatically write to the given out put stream along with a new line character written
	 * to he stream.
	 * @param propVar the variable of the property to write
	 * @param propVal the value of the property to write.
	 * @param bos the outputstream to write to
	 * @throws IOException if any of the write attempts throw an exception
	 * @throws IllegalArgumentException, if any of the arguments are null.
	 */
	public static void writePropertyFileProperty(String propVar,String propVal,BufferedOutputStream bos) throws IOException,IllegalArgumentException {
		if(propVar==null || propVal ==null || bos ==null)
			throw new IllegalArgumentException("None of the arguments can be null.");
		String write=propVar + "="+propVal;
		bos.write(write.getBytes());
		bos.write("\n".getBytes());

	}
	/**
	 * This returns a string array of a given property file line with the property variable of
	 * the given line in arr[0] and the property value of the given line in arr[1].
	 * @param line the line to extract the value and variable from.
	 * @return a string array containing the property value and property value of the given line.
	 * @throws IllegalArgumentException if line is null or doesn't contain an '=', or is empty.
	 */
	public static  String[] getPropValAndVar(String line){
		System.out.println("Line is: " + line);
		if(line==null)
			throw new IllegalArgumentException("Line can't be null.");
		if(line.equals("") || !line.contains("="))
			throw new IllegalArgumentException("Line is ill formatted.");
		String[] ret=line.split("=");
		return ret;
	}//end getPropValAndVar

	public static  void writeXMLEntries(String root,String[] subTrees,String[] values,BufferedOutputStream bos) throws IOException{
		String rootXML="<"+root+">";
		bos.write(rootXML.getBytes());
		bos.write("\n".getBytes());

		for(int i=0;i<subTrees.length;i++){

		}
	}//end writeXMLEntries
	/**
	 * This will count the number of files of a given file.
	 * @return the number of files to be had.
	 */
	public static  int countSubFiles(File file){
		Assert.notNull(file);
		int ret=0;
		if(file==null)
			throw new IllegalArgumentException("File name can't be null.");
		if(file.isDirectory()){
			File[] subFiles =file.listFiles();
			if(subFiles.length >0){
				for(int i=0;i<subFiles.length;i++){
					ret+=countSubFiles(subFiles[i]);
				}
			}
		}
		ret++;
		return ret;
	}//end countSubFiles
	/**
	 * This will extract a jar file.
	 * @param fileName the name of the jar file to unzip.
	 * @throws IllegalArgumentException if fileName is null or empty, or doesn't contain .jar
	 * @throws FileNotFoundException if the given file name isn't found.
	 * @return the number of files unzipped.
	 */
	public static  int unZipJar(String fileName) throws IllegalArgumentException,FileNotFoundException {
		int count=0;
		Assert.notNull(fileName);
		Assert.hasLength(fileName);
		Assert.hasText(".jar");

		try {
			File jarFile = new File(fileName);
			if(!jarFile.exists())
				throw new IllegalArgumentException("Jar file doesn't exist.");
			JarFile jar = new JarFile(jarFile);
			Enumeration<JarEntry> jarEntries=jar.entries();

			List<String> files = new ArrayList<String>();

			while(jarEntries.hasMoreElements()){
				JarEntry currEntry=(JarEntry)jarEntries.nextElement();
				System.out.println("File name: " + currEntry.getName() );
				if(currEntry.isDirectory()){
					File directory = new File(currEntry.getName());
					directory.mkdir();
					count++;
				}
				else
					files.add(currEntry.getName());


				continue;
			}

			for(String s: files){
				System.out.println("Getting file from jar: " + s);
				count++;

				copyInputStream(jar.getInputStream(jar.getEntry(s)),
						new BufferedOutputStream(new FileOutputStream(s)));
			}


			jar.close();
		} 
		catch (IOException e) {
			e.printStackTrace();
			return -1;
		}
		return count;
	}//end unZipJar

	/**
	 * This will return a file array of all of the given file containing the File of every
	 * thing with the given name.
	 * @param query the name of file to search by
	 * @param startDirectory the directory to start searching with
	 * @param recursive, whether this will be a recursive search or not.
	 * @return a file array of the found files, null otherwise.
	 * @throws IllegalArgumentException if startDirectory or query is null, or startDirectory isn't a directory.
	 * 
	 */
	public static  File[] search(String startDirectory,String query,boolean recursive)
			throws IllegalArgumentException {
		//File from the given startDirectory
		File start = new File(startDirectory);
		List<File> ret = new ArrayList<File>();
		if(startDirectory == null || query ==null)
			throw new IllegalArgumentException("Can't have null arguments.");
		if(!start.exists())
			throw new IllegalArgumentException("Start directory must exist.");
		if(!start.isDirectory())
			throw new IllegalArgumentException("Start directory must be a directory.");
		//Recur until something is found or every file in every sub directory of the given start directory
		//searched.
		if(recursive){
			//Recur on all of the files within the directory, if any.
			if(start.isDirectory()){
				for(File f: start.listFiles()){
					File[]iter=null;
					if(f.isDirectory())
						iter=search(f.getAbsolutePath(),query,true);
					if(iter !=null){
						for(File f1: iter)
							ret.add(f1);
					}
					else if(f.getAbsolutePath().contains(query))
						ret.add(f);


				}
			}
			if(ret.size()>0){
				File[] ret1 = new File[ret.size()];
				for(int i=0;i<ret.size();i++){
					ret1[i] = new File("");
					ret1[i]=ret.get(i);
				}
				return ret1;
			}
			return null;
		}

		//Not recursive, only search the first subdirectory.
		else {
			for(File f: start.listFiles())
				if(f.getAbsolutePath().contains(query) && f.isFile())
					ret.add(f);

			File[] ret1 = new File[ret.size()];
			for(int i=0;i<ret.size();i++){
				ret1[i] = new File("");
				ret1[i]=ret.get(i);
			}
			return ret1;

		}

	}//end search

	/**
	 * This will return a file array of all of the given file containing the File of every
	 * thing with the given name. Note that an ignore size() of 0 will be the equivalent of:
	 * search(String startDirectory,String query,boolean recursive)
	 * @param query the name of file to search by
	 * @param startDirectory the directory to start searching with
	 * @param recursive, whether this will be a recursive search or not.
	 * @param ignore a list of strings to ignore
	 * @return a file array of the found files, null otherwise.
	 * @throws IllegalArgumentException if startDirectory or query is null, or startDirectory isn't a directory.
	 * 
	 */
	public static  File[] search(String startDirectory,String query,List<String> ignore, boolean recursive){
		//File from the given startDirectory
		File start = new File(startDirectory);
		List<File> ret = new ArrayList<File>();

		if(startDirectory == null || query ==null)
			throw new IllegalArgumentException("Can't have null arguments.");

		if(!start.exists())
			throw new IllegalArgumentException("Start directory must exist.");
		//Recursive, search every sub directory and every file in those sub directories.
		if(recursive){
			//Search each of the files in those sub directories of the start file, if any.
			if(start.isDirectory()){
				for(File f: start.listFiles()){
					System.out.println("File: " + f.getAbsolutePath());
					File[]iter=null;
					if(f.isDirectory())
						iter=search(f.getAbsolutePath(),query,ignore,true);
					if(iter !=null){
						for(File f1: iter)
							ret.add(f1);
					}
					else if(f.getAbsolutePath().contains(query) && !stringContainsListEntries(f.getAbsolutePath(),ignore))
						ret.add(f);


				}
			}
			if(ret.size()>0){
				File[] ret1 = new File[ret.size()];
				for(int i=0;i<ret.size();i++){
					ret1[i] = new File("");
					ret1[i]=ret.get(i);
				}
				return ret1;
			}
			return null;
		}

		//Search only the first sub directory for files.
		else {
			for(File f: start.listFiles()){
				if(f.getAbsolutePath().contains(query) && f.isFile())
					ret.add(f);
			}
			File[] ret1 = new File[ret.size()];
			for(int i=0;i<ret.size();i++){
				ret1[i] = new File("");
				ret1[i]=ret.get(i);
			}
			return ret1;

		}

	}//end search
	/* Helper method for ignoring paths */
	private static boolean stringContainsListEntries(String toCheck,List<String> ignore){
		System.out.println("toCheck: " + toCheck);

		for(String s: ignore){
			System.out.println("s: " + s);
			if(toCheck.contains(s))
				return true;
		}
		return false;
	}// end stringContainsListEntries

	/**
	 * This iterates through a zip file and counts the entries in it.
	 * @param file the zip file to be counted.
	 * @throws IllegalArgumentException file can't be null.
	 * @return the number of entries in the zip file.
	 */
	public static  int countZipEntries(ZipFile file) throws IllegalArgumentException{
		if(file==null)
			throw new IllegalArgumentException("File can't be null.");
		int count=0;
		@SuppressWarnings("unused")
		ZipEntry entry=null;
		Enumeration<? extends ZipEntry> entries=file.entries();
		while(entries.hasMoreElements()){
			entry=entries.nextElement();
			System.out.println(count);
			count++;
		}
		return count;
	}//end countZipEntries

	/**
	 * From the given input stream it will to the given output stream.
	 * @param in the input stream to copy from.
	 * @param out the output stream to write to
	 * @throws IOException if something goes wrong with 
	 * the input or output.
	 */
	public static  final void copyInputStream(InputStream in, OutputStream out)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		out.flush();
	}//end copyInputStream

	/**
	 * From the given input stream it will to the given output stream.
	 * @param in the input stream to copy from.
	 * @param out the output stream to write to
	 * @param close whether to 	close the stream for the user
	 * @throws IOException if something goes wrong with 
	 * the input or output.
	 */
	public static  final void copyInputStream(InputStream in, OutputStream out,boolean close)
			throws IOException {
		byte[] buffer = new byte[1024];
		int len;

		while((len = in.read(buffer)) >= 0)
			out.write(buffer, 0, len);
		out.flush();
		if(close) {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(out);
		}
	}//end copyInputStream
	/**
	 * This is a recursive algorithm which deletes all the files of a given sub directory.
	 * @param folder the file to delete.
	 * @throws IllegalArgumentException, if folder is null, doesn't exist.
	 */
	public static  void deleteSubDirectories(File folder) throws IllegalArgumentException {
		if(folder==null)
			throw new IllegalArgumentException("Folder can't be null.");

		if(!folder.exists())
			throw new IllegalArgumentException("Folder doesn't exist.");
		if(folder.isFile())
			folder.delete();
		else {
			File[] subFiles =folder.listFiles();

			for(int i=0;i<subFiles.length;i++)
				deleteSubDirectories(subFiles[i]);
			folder.delete();

		}
	}//end deleteSubDirectories

	/**
	 * This detects whether a file is in a directory.
	 * @param string the path to test.
	 * @return whether the given path has a parent directory or not.
	 */
	public static  boolean fileHasParent(String string){
		try{
			return string.contains("\\") || string.contains("/");
		}catch(Exception e){
			return false;
		}
	}//end fileHasParent

	/**
	 * This copies a file from the given source file to the given target file.
	 * @param source the source file to copy from.
	 * @param targetFile the file to write to
	 * 
	 */
	public static  void writeFile(File source,  File targetFile)
	{
		BufferedOutputStream bos=null;
		BufferedInputStream bis = null;
		System.out.println("Writing the file..");
		try
		{
			bos = new BufferedOutputStream(new FileOutputStream(targetFile), 4096);
			bis = new BufferedInputStream(new FileInputStream(source));
		}
		catch(IOException e){
			e.printStackTrace();
			System.out.println("Not writing file.");
			return;
		}
		try
		{
			int theChar;
			while((theChar = bis.read()) != -1) 
				bos.write(theChar);
			bos.close();
			bis.close();
		}
		catch(IOException e)
		{
			System.out.println("Failed on the copy");
			e.printStackTrace();
		}
	}
	/**
	 * Based on a given start directory, this will wipe every directory with the specified name in in it, 
	 * and all of it's contents.
	 * @param startDirectory the directory to wipe a specified directory out of
	 * @param directoryToDelete the directory string to search for
	 * @throws IllegalArgumentException if startDirectory or directoryToDelete is null,or if startDirectory doesn't
	 * exist
	 */
	public static void deleteSpecificDirectory(String startDirectory, String directoryToDelete) throws IllegalArgumentException {

		if(startDirectory==null || directoryToDelete==null)
			throw new IllegalArgumentException("Given parameters can't be null.");


		File startDir = new File(startDirectory);

		if(!startDir.exists())
			throw new IllegalArgumentException("Given startDirectory doesn't exist.");
		if(startDir.isFile())
			return;

		File[] listFiles=search(startDirectory,directoryToDelete,true);
		for(File f:listFiles){
			deleteSubDirectories(f);
			f.delete();
		}
		startDir.deleteOnExit();
	}
	public  static Map<String,String> getMimeTypes(){
		Map<String,String> ret = new HashMap<String,String>();

		for(int i=1;i<formatToMimeType.length;i++){
			ret.put(formatToMimeType[i-1],formatToMimeType[i].trim());

		}
		return ret;
	}//end deleteSpecificDirectory



	public static String[] getPossibleActions() {
		return possibleActions;
	}
	public static void main(String[] args) {
		FileMoverUtil.quotify("/home/agibsonccc/Downloads/fsi.csv","/home/agibsonccc/Downloads/newfsi.csv");
	}

	private static String[] zipFormats={"zip","jar","ear","tar","tar.gz","gz","war","rar"};


	/* String[i] is the format name String[i+1] is the mime type */
	private static String[] formatToMimeType={
		"ai" ,"	application/postscript" ,
		"ai" ,"	application/postscript" ,
		"aif" ,"	audio/x-aiff" ,
		"aifc" ,"	audio/x-aiff" ,
		"aiff" ,"	audio/x-aiff" ,
		"asc" ,"	text/plain" ,
		"atom" ,"	application/atom+xml" ,
		"au" ,"	audio/basic" ,
		"avi" ,"	video/x-msvideo" ,
		"bcpio" ,"	application/x-bcpio" ,
		"bin" ,"	application/octet-stream" ,
		"bmp" ,"	image/bmp" ,
		"cdf" ,"	application/x-netcdf" ,
		"cgm" ,"	image/cgm" ,
		"class" ,"	application/octet-stream" ,
		"cpio" ,"	application/x-cpio" ,
		"cpt" ,"	application/mac-compactpro" ,
		"csh" ,"	application/x-csh" ,
		"css" ,"	text/css" ,
		"dcr" ,"	application/x-director" ,
		"dif" ,"	video/x-dv" ,
		"dir" ,"	application/x-director" ,
		"djv" ,"	image/vnd.djvu" ,
		"djvu" ,"	image/vnd.djvu" ,
		"dll" ,"	application/octet-stream" ,
		"dmg" ,"	application/octet-stream" ,
		"dms" ,"	application/octet-stream" ,
		"doc" ,"	application/msword" ,
		"dtd" ,"	application/xml-dtd" ,
		"dv" ,"	video/x-dv" ,
		"dvi" ,"	application/x-dvi" ,
		"dxr" ,"	application/x-director" ,
		"eps" ,"	application/postscript" ,
		"etx" ,"	text/x-setext" ,
		"exe" ,"	application/octet-stream" ,
		"ez" ,"	application/andrew-inset" ,
		"gif" ,"	image/gif" ,
		"gram" ,"	application/srgs" ,
		"grxml" ,"	application/srgs+xml" ,
		"gtar" ,"	application/x-gtar" ,
		"hdf" ,"	application/x-hdf" ,
		"hqx" ,"	application/mac-binhex40" ,
		"htm" ,"	text/html" ,
		"html" ,"	text/html" ,
		"ice" ,"	x-conference/x-cooltalk" ,
		"ico" ,"	image/x-icon" ,
		"ics" ,"	text/calendar" ,
		"ief" ,"	image/ief" ,
		"ifb" ,"	text/calendar" ,
		"iges" ,"	model/iges" ,
		"igs" ,"	model/iges" ,
		"jnlp" ,"	application/x-java-jnlp-file" ,
		"jp2" ,"	image/jp2" ,
		"jpe" ,"	image/jpeg" ,
		"jpeg" ,"	image/jpeg" ,
		"jpg" ,"	image/jpeg" ,
		"js" ,"	application/x-javascript" ,
		"kar" ,"	audio/midi" ,
		"latex" ,"	application/x-latex" ,
		"lha" ,"	application/octet-stream" ,
		"lzh" ,"	application/octet-stream" ,
		"m3u" ,"	audio/x-mpegurl" ,
		"m4a" ,"	audio/mp4a-latm" ,
		"m4b" ,"	audio/mp4a-latm" ,
		"m4p" ,"	audio/mp4a-latm" ,
		"m4u" ,"	video/vnd.mpegurl" ,
		"m4v" ,"	video/x-m4v" ,
		"mac" ,"	image/x-macpaint" ,
		"man" ,"	application/x-troff-man" ,
		"mathml" ,"	application/mathml+xml" ,
		"me" ,"	application/x-troff-me" ,
		"mesh" ,"	model/mesh" ,
		"mid" ,"	audio/midi" ,
		"midi" ,"	audio/midi" ,
		"mif" ,"	application/vnd.mif" ,
		"mov" ,"	video/quicktime" ,
		"movie" ,"	video/x-sgi-movie" ,
		"mp2" ,"	audio/mpeg" ,
		"mp3" ,"	audio/mpeg" ,
		"mp4" ,"	video/mp4" ,
		"mpe" ,"	video/mpeg" ,
		"mpeg" ,"	video/mpeg" ,
		"mpg" ,"	video/mpeg" ,
		"mpga" ,"	audio/mpeg" ,
		"ms" ,"	application/x-troff-ms" ,
		"msh" ,"	model/mesh" ,
		"mxu" ,"	video/vnd.mpegurl" ,
		"nc" ,"	application/x-netcdf" ,
		"oda" ,"	application/oda" ,
		"ogg" ,"	application/ogg" ,
		"pbm" ,"	image/x-portable-bitmap" ,
		"pct" ,"	image/pict" ,
		"pdb" ,"	chemical/x-pdb" ,
		"pdf" ,"	application/pdf" ,
		"pgm" ,"	image/x-portable-graymap" ,
		"pgn" ,"	application/x-chess-pgn" ,
		"pic" ,"	image/pict" ,
		"pict" ,"	image/pict" ,
		"png" ,"	image/png" ,
		"pnm" ,"	image/x-portable-anymap" ,
		"pnt" ,"	image/x-macpaint" ,
		"pntg" ,"	image/x-macpaint" ,
		"ppm" ,"	image/x-portable-pixmap" ,
		"ppt" ,"	application/vnd.ms-powerpoint" ,
		"ps" ,"	application/postscript" ,
		"qt" ,"	video/quicktime" ,
		"qti" ,"	image/x-quicktime" ,
		"qtif" ,"	image/x-quicktime" ,
		"ra" ,"	audio/x-pn-realaudio" ,
		"ram" ,"	audio/x-pn-realaudio" ,
		"ras" ,"	image/x-cmu-raster" ,
		"rdf" ,"	application/rdf+xml" ,
		"rgb" ,"	image/x-rgb" ,
		"rm" ,"	application/vnd.rn-realmedia" ,
		"roff" ,"	application/x-troff" ,
		"rtf" ,"	text/rtf" ,
		"rtx" ,"	text/richtext" ,
		"sgm" ,"	text/sgml" ,
		"sgml" ,"	text/sgml" ,
		"sh" ,"	application/x-sh" ,
		"shar" ,"	application/x-shar" ,
		"silo" ,"	model/mesh" ,
		"sit" ,"	application/x-stuffit" ,
		"skd" ,"	application/x-koan" ,
		"skm" ,"	application/x-koan" ,
		"skp" ,"	application/x-koan" ,
		"skt" ,"	application/x-koan" ,
		"smi" ,"	application/smil" ,
		"smil" ,"	application/smil" ,
		"snd" ,"	audio/basic" ,
		"so" ,"	application/octet-stream" ,
		"spl" ,"	application/x-futuresplash" ,
		"src" ,"	application/x-wais-source" ,
		"sv4cpio" ,"	application/x-sv4cpio" ,
		"sv4crc" ,"	application/x-sv4crc" ,
		"svg" ,"	image/svg+xml" ,
		"swf" ,"	application/x-shockwave-flash" ,
		"t" ,"	application/x-troff" ,
		"tar" ,"	application/x-tar" ,
		"tcl" ,"	application/x-tcl" ,
		"tex" ,"	application/x-tex" ,
		"texi" ,"	application/x-texinfo" ,
		"texinfo" ,"	application/x-texinfo" ,
		"tif" ,"	image/tiff" ,
		"tiff" ,"	image/tiff" ,
		"tr" ,"	application/x-troff" ,
		"tsv" ,"	text/tab-separated-values" ,
		"txt" ,"	text/plain" ,
		"ustar" ,"	application/x-ustar" ,
		"vcd" ,"	application/x-cdlink" ,
		"vrml" ,"	model/vrml" ,
		"vxml" ,"	application/voicexml+xml" ,
		"wav" ,"	audio/x-wav" ,
		"wbmp" ,"	image/vnd.wap.wbmp" ,
		"wbmxl" ,"	application/vnd.wap.wbxml" ,
		"wml" ,"	text/vnd.wap.wml" ,
		"wmlc" ,"	application/vnd.wap.wmlc" ,
		"wmls" ,"	text/vnd.wap.wmlscript" ,
		"wmlsc" ,"	application/vnd.wap.wmlscriptc" ,
		"wrl" ,"	model/vrml" ,
		"xbm" ,"	image/x-xbitmap" ,
		"xht" ,"	application/xhtml+xml" ,
		"xhtml" ,"	application/xhtml+xml" ,
		"xls" ,"	application/vnd.ms-excel" ,
		"xml" ,"	application/xml" ,
		"xpm" ,"	image/x-xpixmap" ,
		"xsl" ,"	application/xml" ,
		"xslt" ,"	application/xslt+xml" ,
		"xul" ,"	application/vnd.mozilla.xul+xml" ,
		"xwd" ,"	image/x-xwindowdump" ,
		"xyz" ,"	chemical/x-xyz" ,
		"zip" ,"	application/zip" ,

	};
	private static String[] possibleActions={"copyZipFile","compressFiles","URLtoFile","loadZipEntry","addZipEntry","extract","writeStringListToFile","grep","fileHasText","subDirectories","hasWritePermissions","createFile","getProperties","listZipEntries","deleteZipEntry","writeProperty","findDirectory","copyFile","writerProperty","writePropertyFileProperty","writeXMLENtries","countSubFiles","unzipJar","search","countZIpEntries","copyInputStream","deleteSubDirectories","fileHasParent","writeFile","deleteSpecificDirectory"};
	private static Logger log=LoggerFactory.getLogger(FileMoverUtil.class);
}//end FileMoverUtil
