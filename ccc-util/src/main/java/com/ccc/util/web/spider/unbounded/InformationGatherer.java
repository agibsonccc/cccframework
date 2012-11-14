/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;

import java.util.Set;

import com.ccc.util.datastructure.graph.interfaces.Graph;


public interface InformationGatherer extends Spider {
public void gatherInfo(Location start);
public Set<String> getGatheredInfo();
public void saveInfo(Location destination);
public Graph<Location,Location> traveledTrail();
}
