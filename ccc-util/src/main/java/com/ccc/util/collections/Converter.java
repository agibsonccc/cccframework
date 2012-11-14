package com.ccc.util.collections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Ravi Mohan
 * @author Adam Gibson
 */
public class Converter<T> {

	public List<T> setToList(Set<T> set) {
		List<T> retVal = new ArrayList<T>(set);
		return retVal;
	}

	public Set<T> listToSet(List<T> l) {
		Set<T> retVal = new HashSet<T>(l);
		return retVal;
	}
	
	public List<T> toList(Collection<T> collection) {
		if(collection instanceof List)
			return (List<T>) collection;
		List<T> ret = new ArrayList<T>();
		for(T t : collection) {
			ret.add(t);
		}
		return ret;
	}
	public Set<T> toSet(Collection<T> collection) {
		if(collection instanceof Set)
			return (Set<T>) collection;
		Set<T> ret = new HashSet<T>();
		for(T t : collection) {
			ret.add(t);
		}
		return ret;
	}
	
}
