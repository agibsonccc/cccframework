package com.ccc.util.datastructure.graph.implementations;

import com.ccc.util.datastructure.graph.interfaces.Entry;

public class BasicEntry<K,V> implements Entry<K,V> {

	@Override
	public K getKey() {
		return key;
	}

	@Override
	public V getValue() {
		return val;
	}

	@Override
	public void setVal(V val) {
		this.val=val;
	}

	@Override
	public void setKey(K key) {
		this.key=key;
	}
	private K key;
	private V val;
}
