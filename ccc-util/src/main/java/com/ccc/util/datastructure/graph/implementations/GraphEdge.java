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



public class GraphEdge<E> implements Edge<E>,Pointer<Edge<E>> {
	public GraphEdge(Pointer<Edge<E>> pos,E e){
		setPos(pos);
		setE(e);
		decorations = new HashMap<Object,Object>(10);
		
		
	}
	public GraphEdge(){
		this(null,null);
	}
	@Override
	public Object get(Object o) {
		return decorations.get(o);
	}

	@Override
	public Object put(Object key, Object val) {
		return decorations.put(key, val);
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

	/**
	 * @return the pos
	 */
	public Pointer<Edge<E>> getPos() {
		return pos;
	}
	/**
	 * @param pos the pos to set
	 */
	public void setPos(Pointer<Edge<E>> pos) {
		this.pos = pos;
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
	public Edge<E> getValue() {
		return pos.getValue();
	}

	@Override
	public Vertex[] endVertices() {
		Vertex[] ret = new Vertex[2];
		ret[0]=startPos;
		ret[1]=endPos;
		return ret;
	}
	public void setE(E e) {
		this.e = e;
	}

	public E getE() {
		return e;
	}
	public void setStartAdjEdgePos(Pointer<Edge> pointer) {
		this.startAdjEdgePos = pointer;
	}

	public Pointer<Edge> getStartAdjEdgePos() {
		return startAdjEdgePos;
	}
	public void setEndAdjEdgePos(Pointer<Edge> endAdjEdgePos) {
		this.endAdjEdgePos = endAdjEdgePos;
	}

	public Pointer<Edge> getEndAdjEdgePos() {
		return endAdjEdgePos;
	}
	/**
	 * @return the startPos
	 */
	public Vertex getStartPos() {
		return startPos;
	}
	/**
	 * @param startPos the startPos to set
	 */
	public void setStartPos(Vertex startPos) {
		this.startPos = startPos;
	}
	/**
	 * @return the endPos
	 */
	public Vertex getEndPos() {
		return endPos;
	}
	/**
	 * @param endPos the endPos to set
	 */
	public void setEndPos(Vertex endPos) {
		this.endPos = endPos;
	}
	private Map<Object,Object> decorations;
	private Pointer<Edge<E>> pos;
	Pointer<Edge> startAdjEdgePos,endAdjEdgePos;
	private Vertex startPos,endPos;
	private E e;
}
