package com.android.kit;

import java.util.List;

import android.app.Application;

import com.android.kit.contacttypes.ContactTypeRegistry;
import com.android.kit.contacttypes.IContactType;
import com.android.kit.contacttypes.PhoneContactType;
import com.android.kit.contacttypes.SmsContactType;
import com.google.common.collect.Lists;

public class KitApplication extends Application {
	
	private ContactTypeRegistry contactTypeRegistry;
	
	@Override
	public void onCreate()  {
		contactTypeRegistry = new ContactTypeRegistry(this, createContactTypeList());
	}
	
	private List<IContactType> createContactTypeList()  {
		List<IContactType> contactTypes = Lists.newArrayList();
		contactTypes.add(new PhoneContactType(this, 1 << 0, true));
		contactTypes.add(new SmsContactType(this, 1 << 1, false));
		return contactTypes;
	}
	
	//TODO: Do this by dependency injection (e.g., Spring, Guice)
	public ContactTypeRegistry getContactTypeRegistry()  {
		return contactTypeRegistry;
	}
}
