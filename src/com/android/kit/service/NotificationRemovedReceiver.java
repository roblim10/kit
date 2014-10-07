package com.android.kit.service;

import org.joda.time.DateTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class NotificationRemovedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("KIT", "Notification removed!");
		Reminder reminder = intent.getParcelableExtra(AlarmService.EXTRA_REMINDER);
		
		DateTime nextReminder = Reminder.calculateNextReminderDate(reminder);
		Reminder newReminder = reminder.withNextReminderDate(nextReminder);
		ReminderDatabase.getInstance(context).update(newReminder);
	}
}
