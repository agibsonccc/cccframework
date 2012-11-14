package com.ccc.util.swing.popup;

import javax.swing.*;
import java.awt.*;

/**
 * Class implements com.ccc.util.swing.popup.SwingPopupInterface to provide a cool animated 
 * popup effect to a swing JFrame
 * 
 * @author Michael Godfrey
 * @version July 12 2011
 */
public abstract class JFramePopupImplementation extends JFrame implements SwingPopupInterface{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3439626159943462340L;
	protected JFramePopupImplementation(){toolkit=Toolkit.getDefaultToolkit();}

	protected JFramePopupImplementation(String s){
		super(s);
		toolkit=ToolKitHolder.TOOLKIT;
		this.code = SwingPopupInterface.BOTTOM_RIGHT;
	}

	protected JFramePopupImplementation(String s, int code) {
		super(s);
		this.code = code;
	}

	@Override
	public int getScreenHeight(){

		return Toolkit.getDefaultToolkit().getScreenSize().height;
	}

	@Override
	public int getScreenWidth() {
		return Toolkit.getDefaultToolkit().getScreenSize().width;
	}

	@Override
	public void setPopupLocation(int code) {
		code = this.code;
	}

	//TODO: add functionality to pop up horizontally
	//  (this is as easy as switching widths for heights)
	private void PopupLocate() {
		switch(code){
		case SwingPopupInterface.TOP_RIGHT : 
			this.setLocation(getScreenWidth()-getWidth(), 0); 
			break;
		case SwingPopupInterface.TOP_LEFT : 
			this.setLocation(0, 0); 
			break;
		case SwingPopupInterface.BOTTOM_LEFT : 
			this.setLocation(0, getScreenHeight()-(30 + getHeight())); 
			break;
		case SwingPopupInterface.BOTTOM_RIGHT :
			this.setLocation(getScreenWidth()-getWidth(), getScreenHeight()-(30 + getHeight())); 
			break;
		default:;
		}
	}

	@Override
	public int getWidth() {
		return this.getSize().width;
	}

	@Override
	public void setWidth(int w) {
		width = w;
		this.setSize(w,height);
	}

	@Override
	public int getHeight() {
		return this.getSize().height;
	}

	@Override
	public void setHeight(int h) {
		height = h;
		this.setSize(width,h);
	}

	/**
	 * Wrapper method for this.setVisible(), because it cannot be called within 
	 * 	Thread declaration.
	 * 
	 * @param b Boolean for whether or not to set visible
	 */
	private void makeVisible(boolean b) {
		this.setVisible(b);
	}

	/**
	 * Method to pop-up this JFrame
	 */
	@Override
	public void popup() {
		//Make sure that local parameters are the same as actual JFrame params.
		setHeight(getHeight());
		setWidth(getWidth());

		//New Thread to run popup effect
		Thread t = new Thread(new Runnable() {
			public void run(){
				PopupLocate();
				makeVisible(true);

			}
		});
		t.start();
	}
	private int height;
	private int width;
	private Toolkit toolkit;
	private int code;
}
