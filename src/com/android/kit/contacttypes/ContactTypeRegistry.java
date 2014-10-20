package com.android.kit.contacttypes;

import java.util.Collections;
import java.util.List;

import android.content.Context;

import com.google.common.collect.Lists;

public class ContactTypeRegistry {
	private final List<IContactType> contactTypes;
	private final int defaultFlag;
	
	public ContactTypeRegistry(Context context, List<IContactType> contactTypes)  {
		this.contactTypes = Lists.newArrayList(contactTypes);
		this.defaultFlag = calculateDefaultFlag();
		registerReceivers(context);
	}
	
	private int calculateDefaultFlag()  {
		int flag = 0;
		for (IContactType type : contactTypes)  {
			flag |= type.isDefaultSelected() ? type.getFlag() : 0;
		}
		return flag;
	}
	
	private void registerReceivers(Context context)  {
		for (IContactType type : contactTypes)  {
			context.registerReceiver(type.getReceiver(), type.getIntentFilter());
		}
	}
	
	public List<IContactType> getTypes()  {
		return Collections.unmodifiableList(contactTypes);
	}
	
	public int getDefaultFlag()  {
		return defaultFlag;
	}
}
