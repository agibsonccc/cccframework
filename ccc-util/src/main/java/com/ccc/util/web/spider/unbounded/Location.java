/*******************************************************************************
 * THIS IS THE INTELLECTUAL PROPERTY OF Clever Cloud Computing.
 * 
 * Developer: Adam Gibson
 * 
 * You may not posess this software in any way unless otherwise noted by owner.
 ******************************************************************************/
package com.ccc.util.web.spider.unbounded;

public class Location {
/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((currentLocation == null) ? 0 : currentLocation.hashCode());
		result = prime * result
				+ ((startPoint == null) ? 0 : startPoint.hashCode());
		return result;
	}
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof Location)) {
			return false;
		}
		Location other = (Location) obj;
		if (currentLocation == null) {
			if (other.currentLocation != null) {
				return false;
			}
		} else if (!currentLocation.equals(other.currentLocation)) {
			return false;
		}
		if (startPoint == null) {
			if (other.startPoint != null) {
				return false;
			}
		} else if (!startPoint.equals(other.startPoint)) {
			return false;
		}
		return true;
	}
private String startPoint;
private String currentLocation;
public void setStartPoint(String startPoint) {
	this.startPoint = startPoint;
}
public String getStartPoint() {
	return startPoint;
}
public void setCurrentLocation(String currentLocation) {
	this.currentLocation = currentLocation;
}
public String getCurrentLocation() {
	return currentLocation;
}
}
