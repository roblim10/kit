package com.android.kit.service;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class CreateRemindersOnBootReceiver extends BroadcastReceiver  {
	
	@Override
	public void onReceive(Context context, Intent intent) {
		ReminderDatabase reminderDb = ReminderDatabase.getInstance(context);
		reminderDb.open();
		List<Reminder> reminders = reminderDb.readAllReminders();
		for (Reminder r : reminders)  {
			//TODO: Is it ok to send so many service requests in a row?
			createReminder(context, r);
			Log.i("KIT", "Creating reminder from CreateRemindersOnBootReceiver: " + r);
		}
	}
	
	private void createReminder(Context context, Reminder r)  {
		Intent createIntent = new Intent(context, AlarmService.class);
		createIntent.setAction(AlarmService.ACTION_CREATE_OR_UPDATE_REMINDER);
		createIntent.putExtra(AlarmService.EXTRA_REMINDER, r);
		context.startService(createIntent);
	}
}
