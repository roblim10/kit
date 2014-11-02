package com.android.kit.contacttypes;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.google.common.collect.Lists;

public class ContactTypeRegistry {
	private final List<ContactType> contactTypes;
	private final int defaultFlag;
	
	public ContactTypeRegistry(Context context, List<ContactType> contactTypes)  {
		this.contactTypes = Lists.newArrayList(contactTypes);
		this.defaultFlag = calculateDefaultFlag();
		registerReceivers(context);
	}
	
	private int calculateDefaultFlag()  {
		int flag = 0;
		for (ContactType type : contactTypes)  {
			flag |= type.isDefaultSelected() ? type.getFlag() : 0;
		}
		return flag;
	}
	
	private void registerReceivers(Context context)  {
		for (final ContactType type : contactTypes)  {
			type.register();
		}
	}
	
	public List<ContactType> getTypes()  {
		return Collections.unmodifiableList(contactTypes);
	}
	
	public int getDefaultFlag()  {
		return defaultFlag;
	}
}
