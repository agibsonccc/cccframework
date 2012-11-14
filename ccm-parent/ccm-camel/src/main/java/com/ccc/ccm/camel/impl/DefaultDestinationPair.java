package com.ccc.ccm.camel.impl;

import org.springframework.util.Assert;

import com.ccc.ccm.camel.core.DestinationPair;
/**
 * This is a default destination used for setting a from and to.
 * @author Adam Gibson
 *
 */
public class DefaultDestinationPair implements DestinationPair {
	/**
	 * Field based constructor.
	 * @param from the from of this pair
	 * @param to the to destination of this pair
	 */
	public DefaultDestinationPair(String from,String to) {
		Assert.notNull(from);
		Assert.notNull(to);
		Assert.hasLength(from);
		Assert.hasLength(to);
		setFrom(from);
		setTo(to);
	}
	
	@Override
	public String to() {
		return to;
	}

	@Override
	public String from() {
	return from;
	}

	@Override
	public void setFrom(String from) {
		this.from=from;
	}

	@Override
	public void setTo(String to) {
		this.to=to;
	}
	
	private String from;
	
	private String to;
}//end DefaultDestinationPair
