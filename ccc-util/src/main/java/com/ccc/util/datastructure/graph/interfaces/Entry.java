/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.interfaces;


public interface Entry<K,V> {
public K getKey();
public V getValue();
public void setVal(V val);
public void setKey(K key);

}
