/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;
import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.Semaphore;

import com.ccc.util.filesystem.FileMoverUtil;




public class UrlCrawlerController {
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + MAX_NUMBER_CRAWLERS;
		result = prime * result + ((allDirs == null) ? 0 : allDirs.hashCode());
		result = prime
		* result
		+ ((controlledDirectory == null) ? 0 : controlledDirectory
				.hashCode());
		result = prime * result + (done ? 1231 : 1237);
	
		result = prime * result + ((threads == null) ? 0 : threads.hashCode());
		result = prime * result
		+ ((watcherMap == null) ? 0 : watcherMap.hashCode());
		result = prime
		* result
		+ ((watchersToDeploy == null) ? 0 : watchersToDeploy.hashCode());
		result = prime
		* result
		+ ((watchersToRotate == null) ? 0 : watchersToRotate.hashCode());
		return result;
	}

	

	public UrlCrawlerController(Location controlledDirectory){
		this.controlledDirectory=controlledDirectory;
		if(controlledDirectory== null || controlledDirectory.equals("") || controlledDirectory.equals(" "))
			throw new IllegalArgumentException("Illegal string given: " + controlledDirectory);
		System.out.println("Created: " + controlledDirectory.getCurrentLocation());
		
		init();
	}

	private void init(){
		//Current controlled directory
		File controlledDir = new File(controlledDirectory.getCurrentLocation());
		threads = new ArrayList<UrlCrawlerThread>(MAX_NUMBER_CRAWLERS);
		watchersToDeploy = new ArrayDeque<String>();
		for(int i=0;i<MAX_NUMBER_CRAWLERS;i++){
			UrlCrawlerThread w = new UrlCrawlerThread(MAX_NUMBER_CRAWLERS);
			w.setName("Crawler  " + i + " " + controlledDirectory.getCurrentLocation());
		//	w.setController(this);
			threads.add(w);
			w.start();
			System.out.println("Started: " + w.getName());
		}

		//List of all the subdirectories of the controlled directory
		final Set<File> dirsToWatch = FileMoverUtil.subDirectories(controlledDir,true,new HashSet<File>());

		//Iterate over all of the files, deploy anything under the number of max watchers, enqueue everything else.
		dirs=dirsToWatch;
		dirsTaken=false;
		synchronized(dirs) {
			dirsTaken=true;
			Semaphore s = new Semaphore(1);
			try {
				s.acquire();

			} catch (InterruptedException e1) {

			}
			do {
				try {
					dirs.wait(200);
					System.out.println("Waiting...");
					dirs.notifyAll();
					dirsTaken=false;
				} catch (InterruptedException e) {

				}
			}while(dirsTaken);

			dirs.notify();
			Thread t = new Thread(new Runnable(){
				public void run(){
					//Watch for number of watchers deployed, enqueue anything beyond MAX_NUMBER_WATCHERS
					int deploy=0;
					do {
						try {
							for(File f : dirs){
								dirsTaken=false;

								dirsToWatch.remove(f);
								File f1=f;
								if(f1!=null){
									if(deploy < MAX_NUMBER_CRAWLERS){
										Location l = new Location();
										l.setCurrentLocation(f1.getAbsolutePath());
										l.setStartPoint(f.getAbsolutePath());
										deploySpider(l);
										System.out.println("Deploying: " + l.getCurrentLocation());
										deploy++;
									}
									else 
										watchersToDeploy.add(f1.getAbsolutePath());
								}
								else if(dirsToWatch.isEmpty())
									break;
							}
						}catch(ConcurrentModificationException e) {
							dirsTaken=true;


						}

					}
					while(dirsTaken);
				}
			});
			t.start();
			dirsTaken=false;
			dirs.notifyAll();
			s.release();
		}
		//Deploy left over spiders.
		while(!watchersToDeploy.isEmpty()){
			String curr=watchersToDeploy.remove();

			for(UrlCrawlerThread t : threads){
				if(t.getCurrentActiveCrawlers() < MAX_NUMBER_CRAWLERS){
					Location l = new Location();
					l.setCurrentLocation(curr);
					l.setStartPoint(curr);
					deploySpider(l);
				}
			}
		}

		watchFiles();
	}//end init


	
	public void deploySpider(Location toDeploy) {
		boolean deployed=false;//Spider was deployed
		Semaphore s1 = new Semaphore(1);
		synchronized(threads){
			System.out.println("Thread is empty on: " + controlledDirectory.getCurrentLocation());
			try {s1.acquire();} 
			catch (InterruptedException e) {}			//Try to find a watcher thread with available threads.
			
			for(UrlCrawlerThread t : threads){
				//Found an available spot
				if(t.getCurrentActiveCrawlers() < MAX_NUMBER_CRAWLERS)
				{
					Spider s=null;
					try {s = new UrlCrawler(toDeploy);} 
					catch (MalformedURLException e) 
					{e.printStackTrace();} 
					
					catch (IllegalArgumentException e) 
					{e.printStackTrace();} 
					catch (UnknownHostException e) 
					{e.printStackTrace();} 
					catch (SocketException e) 
					{e.printStackTrace();} 
					catch (FileNotFoundException e) 
					{e.printStackTrace();}
					if(UrlCrawler.getCurrentCrawling().get(toDeploy.getCurrentLocation())==null)
					{
						t.getUrlCrawlers().add((UrlCrawler) s);
						UrlCrawler.getCurrentCrawling().put(toDeploy.getCurrentLocation(),(UrlCrawler) s);
					}
					deployed=true;
					System.out.println("Watchers size: " +  "Name " + t.getName() + " " + t.getUrlCrawlers().size());
					System.out.println("In deploy spider: " + toDeploy.getCurrentLocation());
				}
			}
			s1.release();
		}

		//Race condition, threads may not be fully populated, wait a short period of time.
		while(threads.isEmpty())
		{
			System.out.println("Thread is empty on: " + controlledDirectory.getCurrentLocation());
			try {
				synchronized(threads){
					s1.acquire();
				}
			} catch (InterruptedException e) {}
		}
		try {Thread.sleep(20);} 
		catch (InterruptedException e) {}
		s1.release();
		//None were available, just enqueue the watcher.
		if(!deployed)
			try {threads.get(0).getUrlCrawlers().add(new UrlCrawler(toDeploy));
			}
		catch (MalformedURLException e) 
		{e.printStackTrace();} 
		catch (IllegalArgumentException e) 
		{e.printStackTrace();} 
		catch (UnknownHostException e) 
		{e.printStackTrace();} 
		catch (SocketException e) 
		{e.printStackTrace();} 
		catch (FileNotFoundException e) 
		{e.printStackTrace();}
	}//end deploySpider
	/**
	 * This will monitor any changes picked up by any of the watchers deployed, and deploy
	 * any watchers if there are any directories created.
	 */
	private void watchFiles(){

		Thread t = new Thread(new Runnable(){
			public void run(){
				while(!done){
					boolean threadsTaken=false;
					//Multiple threads will try to access this resource, throw in a synchronized block, and use semaphores to wait.
					synchronized(threads) {
						do {
							try {
								for(UrlCrawlerThread t1 : threads){

									if(t1.getCurrentActiveCrawlers()<1)
										threads.remove(t1);
									//Loop till all threads have been exhausted.
									if(threads.isEmpty())
										break;
								}
								//Keep rotating spiders
								while(!watchersToDeploy.isEmpty()){
									String s=watchersToDeploy.remove();
									final Location l = new Location();
									l.setCurrentLocation(s);
									l.setStartPoint(s);
									Thread t = new Thread(new Runnable(){
										public void run(){deploySpider(l);}
									});
									t.start();

								}

							}catch(ConcurrentModificationException e){
								//Threads was taken
								threadsTaken=true;
								try {
									threads.wait(200);
								} catch (InterruptedException e1) {
									threadsTaken=false;
								}
							}
						}while(threadsTaken);
					}
					//This controller is done processing.
					done=true;

				}
			}
		});
		t.start();

	}//end watchFiles



	/**
	 * @return the controlledDirectory
	 */
	public Location getControlledDirectory() {
		return controlledDirectory;
	}

	/**
	 * @param controlledDirectory the controlledDirectory to set
	 */
	public void setControlledDirectory(Location controlledDirectory) {
		this.controlledDirectory = controlledDirectory;
	}

	/**
	 * @return the allDirs
	 */
	public Set<String> getAllDirs() {
		return allDirs;
	}



	
	public Map<Location, Spider> getSpiders() {
		return watcherMap;
	}

	
	public Queue<Spider> rotateList() {
		return watchersToRotate;
	}

	
	public Queue<String> toDeploy() {
		return watchersToDeploy;
	}

	
	public boolean done() {
		return done;
	}
	
	public Location getLocation() {
		return controlledDirectory;
	}



	private Map<Location, Spider> watcherMap = new HashMap<Location,Spider>(10);//All of the watchers currently deployed, identified by their absolute paths.
	private Set<String> allDirs;
	private Queue<Spider> watchersToRotate;
	private Queue<String> watchersToDeploy;
	private Location controlledDirectory;
	private final int MAX_NUMBER_CRAWLERS=8;
	private boolean done;
	//List of active watcher threads
	private List<UrlCrawlerThread> threads;
	
	private static boolean dirsTaken;
	private static Iterable<File> dirs;
}
