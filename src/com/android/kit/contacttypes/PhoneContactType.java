package com.android.kit.contacttypes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.kit.R;

public class PhoneContactType extends ContactType  {
	private int flag;
	private boolean isDefaultSelected;
	
	public PhoneContactType(Context context, int flag, boolean isDefaultSelected)  {
		super(context);
		this.context = context;
		this.flag = flag;
		this.isDefaultSelected = isDefaultSelected;
	}
	
	@Override
	public int getFlag()  {
		return flag;
	}
	
	@Override
	public boolean isDefaultSelected()  {
		return isDefaultSelected;
	}
	
	@Override
	public String toString() {
		return context.getString(R.string.contact_type_phone_call);
	}

	@Override
	public void register() {
		context.registerReceiver(new OutgoingPhoneCallReceiver(), 
				new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL));
	}
	
	private class OutgoingPhoneCallReceiver extends BroadcastReceiver  {
		@Override
		public void onReceive(Context context, Intent intent) {
			String phoneNumber = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			tryResetReminder(phoneNumber);
		}
	}
}
