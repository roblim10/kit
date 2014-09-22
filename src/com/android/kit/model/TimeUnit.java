package com.android.kit.model;

public enum TimeUnit {
	DAYS(0,"day(s)"),
	WEEKS(1,"week(s)"),
	MONTHS(2,"month(s)"),
	YEARS(3,"year(s)");
	
	private int id;
	private String displayString;
	
	private TimeUnit(int id, String displayString)  {
		this.id = id;
		this.displayString = displayString;
	}
	
	public int getId()  {
		return id;
	}
	
	@Override
	public String toString()  {
		return displayString;
	}
	
	public static TimeUnit getTimeUnitFromId(int id)  {
		switch (id)  {
			case 0: return TimeUnit.DAYS;
			case 1: return TimeUnit.WEEKS;
			case 2: return TimeUnit.MONTHS;
			case 3: return TimeUnit.YEARS;
		}
		throw new UnsupportedOperationException("Cannot convert to TimeUnit for id = " + id);
	}
}
