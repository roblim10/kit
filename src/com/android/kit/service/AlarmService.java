package com.android.kit.service;

import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;
import com.google.common.collect.Sets;

public class AlarmService extends IntentService  {
	public final static String EXTRA_REMINDER = "REMINDER";
	
	private ReminderDatabase reminderDb;
	private AlarmManager alarmManager;
	
	public AlarmService() {
		super("ContactService");
		
	}

	@Override
	public void onCreate()  {
		super.onCreate();
		alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		reminderDb = ReminderDatabase.getInstance(this);
		setupDatabaseSync();
	}
	
	private void setupDatabaseSync()  {
//		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver()  {
//			@Override
//			public void onReceive(Context context, Intent intent) {
//				refreshAllAlarms();
//			}
//		}, new IntentFilter(ReminderDatabase.ACTION_REMINDER_DB_UPDATED));
		DatabaseChangedReceiver receiver = new DatabaseChangedReceiver();
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_INSERT));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_EDIT));
		LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(Intent.ACTION_DELETE));
	}
	
	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if(Intent.ACTION_SYNC.equals(action))  {
			refreshAllAlarms();
		}
	}
	
	private void refreshAllAlarms()  {
		List<Reminder> reminders = reminderDb.readAllReminders();
		Set<Reminder> pastReminders = updateExpiredReminders(reminders);
		for (Reminder reminder : reminders)  {
			if (pastReminders.contains(reminder))  {
				ReminderNotificationHelper.sendNotification(this, reminder);
			}
			else  {
				cancelAlarm(reminder.getContactId());
				createAlarm(reminder);
			}
		}
	}

	private Set<Reminder> updateExpiredReminders(Collection<Reminder> allReminders)  {
		Set<Reminder> expiredReminders = Sets.newHashSet();
		for (Reminder reminder : allReminders)  {
			DateTime nextReminder = reminder.getNextReminderDate();
			if (nextReminder.isBeforeNow())  {
				ReminderNotificationHelper.sendNotification(this, reminder);
				expiredReminders.add(reminder);
			}
		}
		return expiredReminders;
	}
	
	private void createAlarm(Reminder reminder)  {
		DateTime nextReminder = reminder.getNextReminderDate();
		PendingIntent operation = createAlarmPendingIntent(reminder);
		Log.i("KIT", "Creating alarm for " + reminder);
		//Uncomment to quickly test alarm
		//alarmManager.set(AlarmManager.RTC_WAKEUP, DateTime.now().plusSeconds(10).getMillis(), operation);
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextReminder.getMillis(), operation);
		
	}
	
	private void cancelAlarm(int contactId)  {
		Intent intent = new Intent(this, ReminderAlarmReceiver.class);
		PendingIntent pi = PendingIntent.getBroadcast(this, contactId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(pi);
	}
	
	private PendingIntent createAlarmPendingIntent(Reminder reminder)  {
		Intent intent = new Intent(this, ReminderAlarmReceiver.class);
		intent.putExtra(EXTRA_REMINDER, reminder);
		return PendingIntent.getBroadcast(this, reminder.getContactId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private class DatabaseChangedReceiver extends BroadcastReceiver  {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			int contactId = intent.getIntExtra(ReminderDatabase.EXTRA_REMINDER_ID, -1);
			
			if (Intent.ACTION_INSERT.equals(action))  {
				Reminder reminder = ReminderDatabase.getInstance(AlarmService.this).readReminder(contactId);
				createAlarm(reminder);
			}
			else if (Intent.ACTION_EDIT.equals(action))  {
				Reminder reminder = ReminderDatabase.getInstance(AlarmService.this).readReminder(contactId);
				cancelAlarm(contactId);
				createAlarm(reminder);
			}
			else if (Intent.ACTION_DELETE.equals(action))  {
				cancelAlarm(contactId);
				ReminderNotificationHelper.cancelNotification(AlarmService.this, contactId);
			}
		}
	}
}
