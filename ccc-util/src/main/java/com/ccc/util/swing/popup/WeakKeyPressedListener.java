package com.ccc.util.swing.popup;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.WindowListener;
import java.lang.ref.WeakReference;
import java.lang.reflect.Method;

public class WeakKeyPressedListener implements KeyListener {
	WeakReference listenerRef; 
	Object src; 
	
	public WeakKeyPressedListener(KeyListener listener, Object src){ 
		listenerRef = new WeakReference(listener); 
		this.src = src; 
	} 
	
	
	private void removeListener(){ 
		try{ 
			Method method = src.getClass().getMethod("removeKeyListener" 
					, new Class[] {KeyListener.class}); 
			method.invoke(src, new Object[]{ this }); 
		} catch(Exception e){ 
			e.printStackTrace(); 
		} 
	}
	
	
	@Override
	public void keyPressed(KeyEvent evt) {
		KeyListener listener = (KeyListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.keyPressed(evt);					
	}

	@Override
	public void keyReleased(KeyEvent evt) {
		KeyListener listener = (KeyListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.keyReleased(evt);		
	}

	@Override
	public void keyTyped(KeyEvent evt) {
		KeyListener listener = (KeyListener)listenerRef.get(); 
		if(listener==null){ 
			removeListener(); 
		}else 
			listener.keyTyped(evt);				
	}

}
