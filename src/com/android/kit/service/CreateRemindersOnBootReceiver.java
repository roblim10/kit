package com.android.kit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CreateRemindersOnBootReceiver extends BroadcastReceiver  {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		Intent createIntent = new Intent(context, AlarmService.class);
		createIntent.setAction(Intent.ACTION_SYNC);
		context.startService(createIntent);
	}
}
