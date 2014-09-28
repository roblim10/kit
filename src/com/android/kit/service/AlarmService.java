package com.android.kit.service;

import java.util.List;

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
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver()  {
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshAllAlarms();
			}
		}, new IntentFilter(ReminderDatabase.ACTION_REMINDER_DB_UPDATED));
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
		for (Reminder reminder : reminders)  {
			cancelAlarm(reminder);
			createAlarm(reminder);
		}
	}

	private void createAlarm(Reminder reminder)  {
		DateTime nextReminder = reminder.getNextReminderDate();
		PendingIntent operation = createPendingIntent(reminder);
		Log.i("KIT", "Creating alarm for " + reminder);
		
		//Uncomment to quickly test alarm
		//alarmManager.set(AlarmManager.RTC_WAKEUP, DateTime.now().plusSeconds(10).getMillis(), operation);
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextReminder.getMillis(), operation);
	}
	
	private void cancelAlarm(Reminder reminder)  {
		PendingIntent pi = createPendingIntent(reminder);
		alarmManager.cancel(pi);
	}
	
	private PendingIntent createPendingIntent(Reminder reminder)  {
		Intent intent = new Intent(this, ReminderAlarmReceiver.class);
		intent.putExtra(EXTRA_REMINDER, reminder);
		return PendingIntent.getBroadcast(this, reminder.getContactId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
}