/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.datastructure.graph.interfaces;

import java.util.List;

public interface Graph<V,E> {
public int numVertices();
public int numEdges();
public List<Edge<E>> edges();
public List<Vertex<V>> vertices();
public Vertex<V> insertVertex(V v) throws IllegalArgumentException;
public Edge<E> insertEdge(Vertex<V> v,Vertex<V> u, E e) throws IllegalArgumentException;
public Edge<E> removeEdge(Pointer<Edge<E>> toRemove) throws IllegalArgumentException;
public Vertex<V> removeVertex(Pointer<Vertex<V>> toRemove) throws IllegalArgumentException;
public boolean areAdjacent(Vertex<V> v, Vertex<V> u) throws IllegalArgumentException;
public Vertex<V> opposite(Vertex<V> v,Edge<E> e) throws IllegalArgumentException;
public E replace(Edge<E> e ,E e1) throws IllegalArgumentException;
public V replace(Vertex<V> v, V v1) throws IllegalArgumentException;
public Edge<E> removeEdge(Edge<E> toRemove) throws IllegalArgumentException;
public Vertex<V> removeVertex(Vertex<V> toRemove) throws IllegalArgumentException;
public List<Edge<E>> outGoingEdges(Vertex<V> v);
public List<Edge<E>> inGoingEdges(Vertex<V> v);
}
