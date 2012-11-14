package com.ccc.util.swing.popup;

public interface Performer {
	/**
	 * This will return the progress so far of a task
	 * @return the progress so far
	 */
	public int progressSoFar();
	
	/**
	 * This is a status message for this performer.
	 * @return a status message for this performer
	 */
	public String status();
	
	/**
	 * This will return whether ther performer is done or not
	 * @return
	 */
	public boolean doneYet();
	/**
	 * This will close the given progress bar
	 */
	public void closeProgressBar();
}
