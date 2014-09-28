package com.android.kit.service;

import org.joda.time.DateTime;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.kit.R;
import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class ReminderAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Reminder reminder = intent.getParcelableExtra(AlarmService.EXTRA_REMINDER);
		Log.i("KIT", "ReminderAlarmReceiver received: " + reminder);
		sendNotification(context, reminder);
		setNextAlarm(context, reminder);
	}
	
	private void sendNotification(Context context, Reminder reminder)  {
		Notification n = new Notification.Builder(context)
			.setContentTitle(context.getString(R.string.notification_content_title, reminder.getName()))
			.setContentText(context.getString(R.string.notification_content_text, "TODO"))
			.setSmallIcon(R.drawable.ic_launcher)
			.setTicker(context.getString(R.string.notification_content_title, reminder.getName()))
			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
			.build();
		
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(Long.toString(reminder.getNextReminderDate().getMillis()), reminder.getContactId(), n);
	}
	
	private void setNextAlarm(Context context, Reminder reminder)  {
		DateTime nextReminder = calculateNextReminderDate(reminder);
		reminder.setNextReminderDate(nextReminder);
		ReminderDatabase.getInstance(context).update(reminder);
	}
	
	private DateTime calculateNextReminderDate(Reminder reminder)  {
		int frequency = reminder.getFrequency();
		DateTime originalReminderDate = reminder.getNextReminderDate();
		switch (reminder.getFrequencyUnit())  {
			case DAYS: return originalReminderDate.plusDays(frequency);
			case WEEKS: return originalReminderDate.plusWeeks(frequency);
			case MONTHS: return originalReminderDate.plusMonths(frequency);
			case YEARS: return originalReminderDate.plusYears(frequency);
			default:
				throw new UnsupportedOperationException("Unknown TimeUnit: " + reminder.getFrequencyUnit());
		}
		
	}

}
