/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.implementations;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccc.util.datastructure.graph.interfaces.Edge;
import com.ccc.util.datastructure.graph.interfaces.Entry;
import com.ccc.util.datastructure.graph.interfaces.Pointer;
import com.ccc.util.datastructure.graph.interfaces.Vertex;


public class GraphVertex<V> implements Vertex<V>,Pointer<Vertex<V>> {
	public GraphVertex(V v,Pointer<Vertex<V>> pos){
		setPos(pos);
		setElement(v);
		incidentEdges = new LinkedPointerList<Edge>();
		decorations = new HashMap<Object,Object>(10);
	}
	public GraphVertex(){
		this(null,null);
	}
	@Override
	public Object get(Object o) {
		return decorations.get(o);
	}

	@Override
	public Object put(Object key, Object val) {
		return decorations.put(key,val);
	}

	@Override
	public Object remove(Object key) {
		return decorations.remove(key);
	}

	@Override
	public List<Entry<Object, Object>> entries() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<Object> keys() {
		return decorations.keySet();
	}

	@Override
	public Collection<Object> values() {
		return decorations.values();
	}

	@Override
	public Map<Object, Object> decorations() {
		return decorations;
	}

	@Override
	public boolean hasDecorations() {
		return decorations.isEmpty();
	}

	@Override
	public Vertex<V> getValue() {
		return pos.getValue();
	}

	@Override
	public LinkedPointerList<Edge> incidentEdges() {
		return incidentEdges;
	}

	@Override
	public Pointer<Vertex<V>> getPos() {
		return pos;
	}
	public void setElement(V element) {
		this.element = element;
	}
	

	public V getElement() {
		return element;
	}
	public void setPos(Pointer<Vertex<V>> pos) {
		this.pos = pos;
	}
	private Map<Object,Object> decorations;
	private LinkedPointerList<Edge> incidentEdges;
	private V element;
	private Pointer<Vertex<V>> pos;
}
