/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.interfaces;

import java.util.List;



public interface Vertex<V> extends Decorable {
public  List<Edge>   incidentEdges();
public Pointer<Vertex<V>> getPos();
}
