package com.android.kit.model;

import java.util.Collection;
import java.util.Set;

import com.google.common.collect.Sets;

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
	
	public static int convertContactTypeCollection(Collection<ContactType> contactTypes)  {
		int flags = 0;
		for (ContactType c : contactTypes)  {
			flags = flags | c.getFlag();
		}
		return flags;
	}
	
	public static Set<ContactType> convertContactTypeValue(int flag)  {
		Set<ContactType> types = Sets.newHashSet();
		ContactType[] enumValues = ContactType.class.getEnumConstants();
		for (int i = 0; i < enumValues.length; i++)  {
			if ((enumValues[i].getFlag() & flag) > 0)  {
				types.add(enumValues[i]);
			}
		}
		return types;
	}
}
