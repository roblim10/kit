package com.android.kit.service;

import org.joda.time.DateTime;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;

import com.android.kit.NotificationHandlerActivity;
import com.android.kit.R;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.android.kit.sqlite.ReminderDatabase;

public class ReminderAlarmReceiver extends BroadcastReceiver {
	@Override
	public void onReceive(Context context, Intent intent) {
		Reminder reminder = intent.getParcelableExtra(AlarmService.EXTRA_REMINDER);
		Log.i("KIT", "ReminderAlarmReceiver received: " + reminder);
		ReminderAlarmReceiver.handleExpiredReminder(context, reminder);
	}
	
	public static void handleExpiredReminder(Context context, Reminder reminder)  {
		sendNotification(context, reminder);
		setNextAlarm(context, reminder);
	}
	
	private static void sendNotification(Context context, Reminder reminder)  {
		//TODO:Fill out content text
		Notification n = new Notification.Builder(context)
			.setContentTitle(context.getString(R.string.notification_content_title, reminder.getName()))
			.setContentText(context.getString(R.string.notification_content_text, "TODO"))
			.setSmallIcon(R.drawable.ic_launcher)
			.setTicker(context.getString(R.string.notification_content_title, reminder.getName()))
			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
			.setContentIntent(createPendingIntent(context, reminder))
			.build();

		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(Long.toString(reminder.getNextReminderDate().getMillis()), reminder.getContactId(), n);			
	}
	
	private static PendingIntent createPendingIntent(Context context, Reminder reminder)  {
		Intent intent = new Intent(context, NotificationHandlerActivity.class);
		return PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private static void setNextAlarm(Context context, Reminder reminder)  {
		DateTime nextReminder = calculateNextReminderDate(reminder);
		reminder.setNextReminderDate(nextReminder);
		ReminderDatabase.getInstance(context).update(reminder);
	}
	
	private static DateTime calculateNextReminderDate(Reminder reminder)  {
		int frequency = reminder.getFrequency();
		TimeUnit unit = reminder.getFrequencyUnit();
		DateTime newReminderDate = reminder.getNextReminderDate();
		do  {
			switch (unit)  {
				case DAYS: newReminderDate = newReminderDate.plusDays(frequency); break;
				case WEEKS: newReminderDate = newReminderDate.plusWeeks(frequency); break;
				case MONTHS: newReminderDate = newReminderDate.plusMonths(frequency); break;
				case YEARS: newReminderDate = newReminderDate.plusYears(frequency); break;
				default:
					throw new UnsupportedOperationException("Unknown TimeUnit: " + unit);
			}
		} while (newReminderDate.isBeforeNow());
		return newReminderDate;
	}

}
