package com.ccc.util.swing.popup;

import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class WeakWindowListener implements WindowListener {
	WeakReference listenerRef; 
	Object src; 

	public WeakWindowListener(WindowListener listener, Object src){ 
		listenerRef = new WeakReference(listener); 
		this.src = src; 
	} 


	private void removeListener(){ 
		try{ 
			Method method = src.getClass().getMethod("removeWindowListener" 
					, new Class[] {WindowListener.class}); 
			method.invoke(src, new Object[]{ this }); 
		} catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}

	@Override
	public void windowActivated(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowActivated(evt);			
	}

	@Override
	public void windowClosed(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowClosed(evt);			
	}

	@Override
	public void windowClosing(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowClosing(evt);			
	}

	@Override
	public void windowDeactivated(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowDeactivated(evt);			
	}

	@Override
	public void windowDeiconified(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowDeiconified(evt);			
	}

	@Override
	public void windowIconified(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowIconified(evt);			
	}

	@Override
	public void windowOpened(WindowEvent evt) {
		WindowListener listener = (WindowListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.windowOpened(evt);			
	} 

}
