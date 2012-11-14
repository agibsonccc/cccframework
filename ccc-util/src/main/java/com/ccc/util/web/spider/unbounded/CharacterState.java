/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;

public class CharacterState {
	private boolean accepting;
	private String c;
	public void setAccepting(boolean accepting) {
		this.accepting = accepting;
	}

	public boolean isAccepting() {
		return accepting;
	}

	public void setC(String c) {
		this.c = c;
	}

	public String getC() {
		return c;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "CharacterState [accepting=" + accepting + ", c=" + c + "]";
	}
	

}
