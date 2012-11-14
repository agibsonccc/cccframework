package com.ccc.util.swing.popup;

/**
 * Interface describes a class to equip a component window to popup in an animated fashion 
 * 
 * @author darkpbj
 * @version July 12 2011
 */
public interface SwingPopupInterface {
	
	public int getScreenHeight();
	public int getScreenWidth();
	/**
	 * Sets pop-up location to be in one of four corners of the screen. The 
	 * code used should correspond with the quadrant of the screen, defaulting
	 * to quadrant 4;
	 * 
	 * i.e.
	 * 	1 = top, right
	 * 	2 = top, left
	 * 	3 = bottom, left
	 * 	4 = bottom, right
	 *  
	 * @param code The quadrant of the display to pop-up from
	 * 
	 */
	public void setPopupLocation(int code);
	public int getWidth();
	public void setWidth(int w);
	public int getHeight();
	public void setHeight(int h);
	/**
	 * Method to pop-up the component in a particular corner of the screen. 
	 */
	public void popup();
	
	public final int TOP_RIGHT    = 1;
	public final int TOP_LEFT     = 2;
	public final int BOTTOM_LEFT  = 3;
	public final int BOTTOM_RIGHT = 4;
}
