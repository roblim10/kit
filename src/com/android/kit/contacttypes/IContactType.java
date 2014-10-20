package com.android.kit.contacttypes;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public interface IContactType  {
	public int getFlag();
	public boolean isDefaultSelected();
	public BroadcastReceiver getReceiver();
	public IntentFilter getIntentFilter();
}
