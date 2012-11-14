/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.interfaces;


import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface Decorable {
public Object get(Object o);
public Object put(Object key,Object val);
public Object remove(Object key);
public List<Entry<Object,Object>> entries();
public Set<Object> keys();
public Collection<Object> values();
public Map<Object,Object> decorations();
public boolean hasDecorations();
}
