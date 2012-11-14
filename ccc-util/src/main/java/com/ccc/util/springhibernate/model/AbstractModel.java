package com.ccc.util.springhibernate.model;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/**
 * A base class for models.
 * @author Adam Gibson
 *
 */
@MappedSuperclass
@JsonIgnoreProperties({ "columnNames"})
public abstract class AbstractModel implements Serializable {

	
	
	
	public String columnNameForValue(Object value) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields=getClass().getDeclaredFields();
		for(Field f : fields) {
			if(f.get(this).equals(value)) {
				Annotation[] annotations=f.getAnnotations();
				for(Annotation ann : annotations) {
					if(ann instanceof Column) {
						Column c=(Column) ann;
						String name=c.name();
						if(name==null) {
							name=f.getName();
						}
						return name;
						
					}
				}
			}
		}
		return null;
	}
	
	public Field fieldWithValue(Object value) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields=getClass().getDeclaredFields();
		for(Field f : fields) {
			if(f.get(this).equals(value)) {
				return f;
			}
		}
		return null;
	}
	
	public Object getValueForColumn(String columnName) throws IllegalArgumentException, IllegalAccessException {
		Field[] fields=getClass().getDeclaredFields();
		for(Field field : fields) {
			Annotation[] annotations=field.getAnnotations();
			for(Annotation ann : annotations) {
				if(ann instanceof Column) {
					Column c=(Column) ann;
					String name=c.name();
					if(name==null) {
						name=field.getName();
					}
					if(name.equals(columnName)) {
						return field.get(this);
					}
					
				}
			}
		}
		return null;
	}
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 5561189516221616580L;

	public Object getId() throws IllegalArgumentException, IllegalAccessException {
		Field[] fields=getClass().getDeclaredFields();
		Object ret=null;
		for(Field field : fields) {
			Annotation[] annotations=field.getAnnotations();
			for(Annotation ann : annotations) {
				if(ann instanceof Id) {
					Class clazz=field.getClass();
					String name=field.getName();
					ret=field.get(this);
				}
			}
		}
		return ret;
	}
	
	public String[] getColumnNames() {
		Field[] fields=getClass().getDeclaredFields();
		List<String> fieldsToGet = new ArrayList<String>();
		for(Field field : fields) {
			Annotation[] annotations=field.getAnnotations();
			for(Annotation ann : annotations) {
				if(ann instanceof Column) {
					String name=((Column) ann).name();
					fieldsToGet.add(name);
				}
			}
		}
		String[] arr = new String[fieldsToGet.size()];
		for(int i=0;i<arr.length;i++) arr[i]=fieldsToGet.get(i);
		return arr;
	}
	
	
}
