package com.ccc.util.web.spider.unbounded;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;


public class UrlCrawlerThread extends Thread {
	/**
	 * For a UrlCrawler thread of execution, a max number of UrlCrawlers allowed to be active must be specified.
	 * @param activeUrlCrawlers the number of active UrlCrawlers allowed in this thread.
	 */
	public UrlCrawlerThread(int activeUrlCrawlers){
		setCrawlers(new ArrayDeque<UrlCrawler>());
		setNumCrawlers(activeUrlCrawlers);
		this.activeCrawlers = new ArrayList<UrlCrawler>(numCrawlers);
		crawlerSet = new HashSet<UrlCrawler>();

	}
	@Override
	public void run(){
		//Constantly watch for new UrlCrawlers to deploy, monitor the current active UrlCrawlers, and if they're done crawling, kill them off and deploy another one.
		while(true) {
			if(!crawlers.isEmpty()) {
				//Check for inactive crawlers.
				for(UrlCrawler w : crawlers){
					if(w.doneCrawling())
					{
						//Remove from the active list.
						activeCrawlers.remove(w);
						//Free up a spot.
						currentActiveCrawlers--;
						//Add the UrlCrawler to the queue to be redeployed later.
						controller.toDeploy().add(w.getUrlToWatchLocation().getCurrentLocation());
						System.out.println("Enqueueing: " + w.getUrlToWatchLocation().getCurrentLocation());
						crawlerSet.remove(w);
						
						
					}

			
				}
				
				//Enough room to deploy another one, deploy it.
				if((currentActiveCrawlers < numCrawlers) || currentActiveCrawlers==0) {
					UrlCrawler toDeploy=crawlers.remove();
					System.out.println("CURRENT ACTIVE UrlCrawler: " + toDeploy.getUrlToWatchLocation().getCurrentLocation());
					if(!crawlerSet.contains(toDeploy))
					{
						Thread t = new Thread(toDeploy);
						t.start();
						System.out.println("UrlCrawlerThread deploying: " + toDeploy.getUrlToWatchLocation().getCurrentLocation());
						currentActiveCrawlers++;
						crawlerSet.add(toDeploy);
						activeCrawlers.add(toDeploy);
					}
					
				}
				

			}

		}


	}//end run
	public void setCrawlers(Queue<UrlCrawler> crawlers) {
		this.crawlers = crawlers;
	}
	public Queue<UrlCrawler> getUrlCrawlers() {
		return crawlers;
	}
	public void setNumCrawlers(int numCrawlers) {
		this.numCrawlers = numCrawlers;
	}

	/**
	 * @return the currentActiveUrlCrawlers
	 */
	public int getCurrentActiveCrawlers() {
		return currentActiveCrawlers;
	}
	public int getNumUrlCrawlers() {
		return numCrawlers;
	}
	public void setActiveUrlCrawlers(List<UrlCrawler> activeCrawlers) {
		this.activeCrawlers = activeCrawlers;
	}
	public List<UrlCrawler> getActiveUrlCrawlers() {
		return activeCrawlers;
	}
	
	private Queue<UrlCrawler> crawlers;//UrlCrawlers to be deployed
	private int numCrawlers;//max number of UrlCrawlers allowed to be deployed
	private int currentActiveCrawlers;//number of current active UrlCrawlers
	private List<UrlCrawler> activeCrawlers;//list of currently active UrlCrawlers
	private UrlCrawlerController controller;
	private static Set<UrlCrawler> crawlerSet;
}
