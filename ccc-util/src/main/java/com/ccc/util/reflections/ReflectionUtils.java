package com.ccc.util.reflections;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
/**
 * An extension of reflection utils that include getting the parameterized type of an object
 * @author agibson
 *
 */
public class ReflectionUtils extends org.springframework.util.ReflectionUtils {

	
	public static Type getParameterizedTypeFor(Object o) {
		Type superclass = o.getClass().getGenericSuperclass();
		Type type=null;
		if (superclass instanceof Class) {
			throw new RuntimeException("Missing type parameter.");
		}
		if(superclass instanceof ParameterizedType) {
			ParameterizedType supParammed=(ParameterizedType) superclass;
			Type parammed=supParammed.getActualTypeArguments()[0];
			return parammed;
		}
		return null;
	}
	
	
}
