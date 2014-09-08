package com.android.kit.model;

public enum ContactType {
	PHONE_CALL("Phone call"),
	SMS("Text message");
	
	private String toString;
	private ContactType(String toString)  {
		this.toString = toString;
	}
	
	@Override
	public String toString()  {
		return toString;
	}
}
