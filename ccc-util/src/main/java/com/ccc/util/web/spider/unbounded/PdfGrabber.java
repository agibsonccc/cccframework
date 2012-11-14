package com.ccc.util.web.spider.unbounded;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.HashSet;
import java.util.Set;

public class PdfGrabber extends UrlCrawler {

	public PdfGrabber(Location toDeploy) throws MalformedURLException,
	IllegalArgumentException, UnknownHostException, SocketException,
	FileNotFoundException {
		super(toDeploy);
	}

	@Override
	public void harvestInfo(){
		Thread t = new Thread(new Runnable(){
			public void run(){
				File destDir = new File("/home/adam/Desktop/pdfs/");
				if(!destDir.exists())
					destDir.mkdir();
				if(urlToWatch.toString().contains("pdf")){
					System.out.println("Harvesting.");

					Downloader.download(urlToWatch, destDir.getAbsolutePath(),"pdf");
				}
			}
		});
		t.setPriority(2);
		t.start();
		
	}
	@Override
	public void crawl(Location location){
		try {
			bw.write(location.getCurrentLocation()+"\n");
		} 
		catch (IOException e1) 
		{e1.printStackTrace();}
		//Analyze the string and look for possible urls to crawl to
		Set<String> scan= new HashSet<String>();
		//Scan each of the lines in the url contents looking for links
		//System.out.println(urlContents);
		for(String s1 : urlContents){
			//System.out.println("S1: " + s1);
			if(!(s1.length() < 1)){
				Set<String> add=scanLine(s1);
				if(add!=null && !add.isEmpty())
					scan.addAll(add);
			}
		}
		//There were urls to crawl to
		if(scan!=null){
			for(String s1 : scan){
				//Ensure the links haven't already been visited
				if(visited.get(s1)==null){
					//Relative path, prepend the host
					if(!(s1.length() > 0))
						continue;
					if(s1.charAt(0)!='h' && s1.charAt(0)!='w'){
						s1=urlToWatch.getHost() + s1;
					}
					//Relative host
					if(!s1.contains("http")){
						s1="http://" + s1;
						//System.out.println("S1: " + s1);

					}
					final Location l = new Location();
					l.setStartPoint(urlToWatch.toString());

					l.setCurrentLocation(s1);
					//Ensure that there are no crawlers already at this location
					if(currentCrawling.get(s1)==null){
						//Check it's a valid url before tring to crawl to it
						//if(machine.isUrl(s1)){
							visited.put(s1, l);
							try {
								UrlCrawler u;
								try 
								{u = new PdfGrabber(l);} 
								catch (UnknownHostException e) 
								{continue;} 

								catch (SocketException e) 
								{continue;} 

								catch (FileNotFoundException e) 
								{continue;}

								//Add to the current crawling
								l.setCurrentLocation(s1);
								UrlCrawler.getCurrentCrawling().put(l.getCurrentLocation(), this);

								Thread t = new Thread(u);
								t.start();
							}

							catch (MalformedURLException e) 
							{continue;}
							catch (IllegalArgumentException e) 
							{continue;}
						//}

					}
				}

			}
		}
	}
}
