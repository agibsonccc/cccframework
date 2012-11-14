package com.ccc.util.datastructure.queues;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

public class StackQueue<E> implements Queue<E> {

	@Override
	public boolean addAll(Collection<? extends E> arg0) {
		return list.addAll(arg0);
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public boolean contains(Object arg0) {
		return list.contains(arg0);
	}

	@Override
	public boolean containsAll(Collection<?> arg0) {
		return list.containsAll(arg0);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public boolean remove(Object arg0) {
		throw new UnsupportedOperationException("Illegal operation on a queue.");
	}

	@Override
	public boolean removeAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Illegal operation on a queue.");

	}

	@Override
	public boolean retainAll(Collection<?> arg0) {
		throw new UnsupportedOperationException("Illegal operation on a queue.");

	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public Object[] toArray() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public <T> T[] toArray(T[] arg0) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean add(E arg0) {
		return list.add(arg0);
	}

	@Override
	public E element() {
		return peek();
	}

	@Override
	public boolean offer(E arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public E peek() {
		return list.element();
	}

	@Override
	public E poll() {
		return list.removeLast();
	}

	@Override
	public E remove() {

		return poll();
	}
	private LinkedList<E> list = new LinkedList<E>();
}
