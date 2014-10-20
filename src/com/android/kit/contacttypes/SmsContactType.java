package com.android.kit.contacttypes;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.android.kit.R;

public class SmsContactType implements IContactType {
	private final static IntentFilter filter = new IntentFilter("");
	private Context context;
	private int flag;
	private boolean isDefaultSelected;
	private BroadcastReceiver receiver;
	
	public SmsContactType(Context context, int flag, boolean isDefaultSelected)  {
		this.context = context;
		this.flag = flag;
		this.receiver = new SmsReceiver();
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
	public BroadcastReceiver getReceiver() {
		return receiver;
	}

	@Override
	public IntentFilter getIntentFilter() {
		return filter;
	}

	@Override
	public String toString() {
		return context.getString(R.string.contact_type_text_message);
	}
	
	private static class SmsReceiver extends BroadcastReceiver  {

		@Override
		public void onReceive(Context context, Intent intent) {
			//TODO: Implement SmsReceiver
		}
		
	}
}
