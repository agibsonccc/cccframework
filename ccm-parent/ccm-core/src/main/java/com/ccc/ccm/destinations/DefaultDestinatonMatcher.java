package com.ccc.ccm.destinations;

import javax.jms.Destination;

public class DefaultDestinatonMatcher implements DestinationMatcher {

	@Override
	public boolean matches(String destinationName) {
		
		return destinationName.contains(matchAgainst);
	}

	@Override
	public boolean matches(Destination destination) {
		return destination.toString().contains(matchAgainst);
	}
	
	public String getMatchAgainst() {
		return matchAgainst;
	}

	public void setMatchAgainst(String matchAgainst) {
		this.matchAgainst = matchAgainst;
	}

	private String matchAgainst;
}
