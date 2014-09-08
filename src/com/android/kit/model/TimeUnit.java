package com.android.kit.model;

public enum TimeUnit {
	DAYS("day(s)"),
	WEEKS("week(s)"),
	MONTHS("month(s)"),
	YEARS("year(s)");
	
	private String displayString;
	
	private TimeUnit(String displayString)  {
		this.displayString = displayString;
	}
	
	@Override
	public String toString()  {
		return displayString;
	}
}
