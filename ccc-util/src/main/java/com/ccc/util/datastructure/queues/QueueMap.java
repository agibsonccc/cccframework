package com.ccc.util.datastructure.queues;
import java.util.HashMap;
import java.util.Queue;
/**
 * This is a map that holds queues. It has a few special operations for 
 * doing direct operations on queues held within the map.
 * 
 * @author Adam Gibson
 *
 * @param <K>
 * @param <V>
 */
public class QueueMap<K,V> extends HashMap<K,Queue<V>> {

	/**
	 * This will add the given value to the given
	 * queue indicated by the key
	 * @param key the key of the queue to add to
	 * @param value the value to add
	 * @throws IllegalArgumentException if the queue
	 * for the given key is not found
	 */
	public  void pushToQueue(K key,V value) {
		Queue<V> queue=get(key);
		if(queue!=null)
			queue.add(value);
		else throw new IllegalArgumentException("Queue for: " + key + " did not exist");
	}//end pushToQueue

	/**
	 * This will poll from the queue indicated by this key
	 * @param key the key of the queue to poll from
	 * @return null if the queue for this key is not found, or queue.poll()
	 * returns null
	 */
	public V pollFromQueue(K key) {
		Queue<V> queue=get(key);
		if(queue!=null)
		return queue.poll();
		return null;
	}//end pollFromQueue

	/**
	 * This will do a peek operation on the 
	 * queue indicated by this key
	 * @param key the key indicating the queue
	 * to peek from
	 * @return the head of the queue without removing it,or
	 * null if the queue doesn't exist, or queue.peek() does
	 * 
	 */
	public V peekFromQueue(K key) {
		Queue<V> queue=get(key);
		if(queue!=null)
			return queue.peek();
		return null;
	}//end peekFromQueue
	
	/**
	 * This will return the head of this queue, or throw an exception
	 * if the queue is empty
	 * @param key the key of the queue to get an element from
	 * @return the element from this queue or null if the queue is null
	 */
	public V elementFromQueue(K key) {
		Queue<V> queue=get(key);
		if(queue!=null)
			return queue.element();
		return null;
	}//end elementFromQueue

	/**
	 * 
	 */
	private static final long serialVersionUID = -1138044880278161062L;


}//end QueueMap
