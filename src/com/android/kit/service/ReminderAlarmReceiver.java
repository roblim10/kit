package com.android.kit.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.kit.model.Reminder;

public class ReminderAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		//TODO: Display a notification
		//TODO: Set new alarm
		Reminder reminder = intent.getParcelableExtra(AlarmService.EXTRA_REMINDER);
		Log.i("KIT", "ReminderAlarmReceiver received: " + reminder);
	}

}
