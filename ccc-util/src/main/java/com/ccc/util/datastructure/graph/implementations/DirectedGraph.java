/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.implementations;

import java.util.ArrayList;
import java.util.List;

import com.ccc.util.datastructure.graph.exceptions.EmptyListException;
import com.ccc.util.datastructure.graph.exceptions.InvalidPointerException;
import com.ccc.util.datastructure.graph.interfaces.Edge;
import com.ccc.util.datastructure.graph.interfaces.Graph;
import com.ccc.util.datastructure.graph.interfaces.Pointer;
import com.ccc.util.datastructure.graph.interfaces.Vertex;

public class DirectedGraph<V,E> implements Graph<V,E> {
	public DirectedGraph(){
		vertices = new LinkedPointerList<Vertex<V>>();
		edges = new LinkedPointerList<Edge<E>>();
	}
	@Override
	public int numVertices() {
		return vertices.size();
	}

	@Override
	public int numEdges() {
		return edges.size();
	}

	@Override
	public List<Edge<E>> edges() {
		return edges;
	}

	@Override
	public List<Vertex<V>> vertices() {
		return vertices;
	}

	@Override
	public Vertex<V> insertVertex(V v) throws IllegalArgumentException {
		GraphVertex<V> v1 = new GraphVertex<V>(v,null);
		vertices.addLast(v1);
		try {
			v1.setPos(vertices.last());
		} catch (EmptyListException e) {}
		return v1;
	}

	@Override
	public Edge<E> insertEdge(Vertex<V> v, Vertex<V> u, E e)
			throws IllegalArgumentException {
		//TCJ:  All operations are O(1) time.

		GraphVertex<V> u1=null,v1=null;
		try {
			u1 = checkVertex(u);
			v1=checkVertex(v);
		} catch (InvalidPointerException e1) {
			e1.printStackTrace();
		}//May throw exception
		//Create list to pass to edge.
		LinkedPointerList<Vertex<V>> list = new LinkedPointerList<Vertex<V>>();
		list.addLast(u1);
		list.addLast(v1);

		//Create edge
		Edge<E> newEdge = new GraphEdge<E>(null,e);
		//Add to list.
		edges.addLast(newEdge);
		//Cast new edge.
		GraphEdge<E> newEdg=null;
		try {
			newEdg = checkEdge(newEdge);
		} catch (InvalidPointerException e1) {
			return null;
		}
		try {
			newEdg.setPos(edges.last());
		} catch (EmptyListException e1) {
			
		}
		newEdg.setStartPos(v1);
		newEdg.setEndPos(u1);
		//Add the edge to each vertex.
		u1.incidentEdges().addLast(newEdge);
		v1.incidentEdges().addLast(newEdge);
		//Update edge's position in each of the vertex's edge list.
		try {
			newEdg.setEndAdjEdgePos(v1.incidentEdges().last());
			newEdg.setStartAdjEdgePos(u1.incidentEdges().last());

		} catch (EmptyListException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return newEdge;
	}
	/*
	 * This checks for a valid vertex and returns a vert.
	 */
	protected GraphVertex<V> checkVertex(Vertex<V> v) throws InvalidPointerException{
		
		 if(v==null)
				throw new InvalidPointerException("Vertex can't be null");
		//TCJ: All operations are O(1).
		if(!(v instanceof GraphVertex))
			throw new InvalidPointerException("Not a valid vertex");
		
		GraphVertex<V> v1 = null;
		try {
			v1=(GraphVertex<V>) v;
			return v1;
		}catch(Exception e){

		}
		return v1;
	}//end checkVertex
	/*
	 * This checks for a valid edge and returns a Edg.
	 */
	protected GraphEdge<E> checkEdge(Edge<E> e) throws InvalidPointerException {
		//TCJ: All operations are O(1).
		if(!(e instanceof GraphEdge))
			throw new InvalidPointerException("Not a valid edge");
		else if(e==null)
			throw new InvalidPointerException("Edge can't be null");
		GraphEdge<E> e1 = null;
		try {
			e1=(GraphEdge<E>) e;
			return e1;
		}catch(Exception e2){

		}
		return e1;
	}//end checkEdge
	@Override
	public Edge<E> removeEdge(Edge<E> toRemove)
			throws IllegalArgumentException {
		GraphEdge<E> e1=null;
		try {
			e1 = checkEdge(toRemove);
		} catch (InvalidPointerException e) {
			
		}//May throw exception
		//TCJ: All method calls are O(1).
		if(e1.getEndPos()!=null){
			GraphVertex<V> endVert = null;
			try {
				endVert = checkVertex(e1.getEndPos());
			} catch (InvalidPointerException e) {
				
			}
			LinkedPointerList<Edge> incEdges =endVert.incidentEdges();
			incEdges.remove(e1.getEndAdjEdgePos());
		}
		if(e1.getStartPos()!=null){
			GraphVertex<V> startVert = null;
			try {
				startVert = checkVertex(e1.getStartPos());
			} catch (InvalidPointerException e) {
				
			}
			LinkedPointerList<Edge> incEdges =startVert.incidentEdges();
			incEdges.remove(e1.getStartAdjEdgePos());
		}
		//TCJ: Operation is O(1).
		return ((GraphEdge<E>) edges.remove(e1.getPos())).getValue();
	}

	public Vertex<V> removeVertex(Vertex<V> toRemove)
			throws IllegalArgumentException {
		//TCJ: All method calls besides getIncidentEdges are O(1).
		GraphVertex<V> v1=null;
		try {
			v1 = checkVertex(toRemove);
		} catch (InvalidPointerException e1) {
			
		}//May throw exception
		LinkedPointerList<Edge> incEdges=v1.incidentEdges();
		//TCJ:  The loop is proportional to the number of edges.
		for(Edge<E> e:incEdges)
			removeEdge(e);

		return ((GraphVertex<V>) vertices.remove(v1.getPos())).getValue();
	}

	@Override
	public boolean areAdjacent(Vertex<V> v, Vertex<V> u)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Vertex<V> opposite(Vertex<V> v,Edge<E> e) throws IllegalArgumentException {
		//TCJ: All method calls are O(1).
		GraphVertex<V> v1=null;
		try {
			v1 = checkVertex(v);
		} catch (InvalidPointerException e2) {
			
		}//May throw exception
		GraphEdge<E> e1=null;
		try {
			e1 = checkEdge(e);
		} catch (InvalidPointerException e2) {
			
		}
		if(e1.getStartPos()==v1)
			return e1.getEndPos();
		else if(e1.getEndPos()==v1)
			return e1.getStartPos();
		return null;
	}

	@Override
	public E replace(Edge<E> e, E e1) throws IllegalArgumentException {
		//TCJ: Method call is O(1).
		GraphEdge<E> e2=null;
		try {
			e2 = checkEdge(e);
		} catch (InvalidPointerException e3) {
			// TODO Auto-generated catch block
			e3.printStackTrace();
		}//May throw an exception
		E ret=e2.getE();
		e2.setE(e1);
		return ret;
	}

	@Override
	public V replace(Vertex<V> v, V v1) throws IllegalArgumentException {
		
		//TCJ: Method call is O(1).
		GraphVertex<V> v2=null;
		try {
			v2 = checkVertex(v);
		} catch (InvalidPointerException e) {
			
		}
		v1=v2.getElement();
		v2.setElement(v1);
		return v1;
	}
	private LinkedPointerList<Vertex<V>> vertices;
	private LinkedPointerList<Edge<E>> edges;
	@Override
	public Edge<E> removeEdge(Pointer<Edge<E>> toRemove)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Vertex<V> removeVertex(Pointer<Vertex<V>> toRemove)
			throws IllegalArgumentException {
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Edge<E>> outGoingEdges(Vertex<V> v) {
		List<Edge> edges=v.incidentEdges();
		List<Edge<E>> ret =  new ArrayList<Edge<E>>(10); 
		//Check each edge's end vertex, if this vertex is the start vertex, edge e is an outgoing edge of v. 
		for(Edge e: edges){
			Vertex test=e.endVertices()[0];
			if(test==v)
				ret.add(e);
		}
		return ret;
	}
	@Override
	public List<Edge<E>> inGoingEdges(Vertex<V> v) {
		List<Edge> edges=v.incidentEdges();
		List<Edge<E>> ret =  new ArrayList<Edge<E>>(10); 
		//Check each edge's end vertex, if this vertex is the start vertex, edge e is an outgoing edge of v. 
		for(Edge e: edges){
			Vertex test=e.endVertices()[1];
			if(test==v)
				ret.add(e);
		}
		return ret;
	}
	
}
