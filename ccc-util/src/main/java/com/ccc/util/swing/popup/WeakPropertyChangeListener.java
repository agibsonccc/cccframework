package com.ccc.util.swing.popup;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;
/**
 * http://www.javalobby.org/java/forums/t19468.html
 * 
 * Used to prevent memory leaks
 * @author Adam Gibson
 *
 */
public class WeakPropertyChangeListener implements PropertyChangeListener{ 
	WeakReference listenerRef; 
	Object src; 

	public WeakPropertyChangeListener(PropertyChangeListener listener, Object src){ 
		listenerRef = new WeakReference(listener); 
		this.src = src; 
	} 

	public void propertyChange(PropertyChangeEvent evt){ 
		PropertyChangeListener listener = (PropertyChangeListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.propertyChange(evt); 
	} 

	private void removeListener(){ 
		try{ 
			Method method = src.getClass().getMethod("removePropertyChangeListener" 
					, new Class[] {PropertyChangeListener.class}); 
			method.invoke(src, new Object[]{ this }); 
		} catch(Exception e){ 
			e.printStackTrace(); 
		} 
	} 
}