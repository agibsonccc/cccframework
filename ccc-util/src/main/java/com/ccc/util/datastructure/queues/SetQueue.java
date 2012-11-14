package com.ccc.util.datastructure.queues;
import java.util.Collection;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
public class SetQueue<E> extends LinkedBlockingQueue<E> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8308470380839029780L;

	@Override
	public boolean add(E e) {
		if(!contains(e))
			return super.add(e);
		return false;
	}

	@Override
	public void put(E e) throws InterruptedException {
		if(!contains(e))
			super.put(e);
	}

	@Override
	public boolean offer(E e) {
		if(!contains(e))
			return super.offer(e);
		return false;
	}

	@Override
	public boolean addAll(Collection<? extends E> c) {
		for(E e : c) 
			if(!contains(e))
				add(e);
		return false;
	}

	@Override
	public boolean offer(E e, long timeout, TimeUnit unit)
			throws InterruptedException {
		if(!contains(e))
			return super.offer(e, timeout, unit);
		return false;
	}




}
