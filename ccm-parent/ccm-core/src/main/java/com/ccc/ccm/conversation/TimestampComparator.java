package com.ccc.ccm.conversation;

import java.sql.Timestamp;
import java.util.Comparator;

public class TimestampComparator<V extends Timestamp> implements Comparator<Timestamp> {

	@Override
	public int compare(Timestamp what, Timestamp to) {
		if(what.after(to))
			return 1;
		else if(to.after(what))
			return -1;
		else return 0;
	}

}
