package com.ccc.util.web.spider.unbounded;


import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;

public class Main {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		/*
		Location l = new Location();
		//l.setCurrentLocation("http://www.dleg.state.mi.us/dms/results.asp?docowner=BCSC&doccat=LLC&Search=Search");
		//l.setStartPoint("http://www.dleg.state.mi.us/dms/results.asp?docowner=BCSC&doccat=LLC&Search=Search");
		l.setCurrentLocation(args[0]);
		l.setStartPoint(l.getCurrentLocation());
		try {
			URL u = new URL(l.getCurrentLocation());
			Downloader.download(u,args[1],args[2]);
		} 
		catch (MalformedURLException e) 
		{e.printStackTrace();}
		*/
		
		
		Location l = new Location();
		l.setCurrentLocation("http://www.google.com");
		l.setStartPoint(l.getCurrentLocation());
		try {
			UrlCrawler u = new UrlCrawler(l);
			Thread t = new Thread(u);
			u.crawl(l);
			t.start();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
