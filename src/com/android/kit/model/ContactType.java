package com.android.kit.model;


public enum ContactType {
	PHONE_CALL(0x1, "Phone call"),
	SMS(0x10,"Text message");
	
	private int flag;
	private String toString;
	private ContactType(int flag, String toString)  {
		this.flag = flag;
		this.toString = toString;
	}
	
	public int getFlag()  {
		return flag;
	}
	
	@Override
	public String toString()  {
		return toString;
	}
}
