package com.android.kit.service;

import org.joda.time.DateTime;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class AlarmService extends IntentService  {
	public final static String ACTION_CREATE_OR_UPDATE_REMINDER = "CREATE_UPDATE";
	public final static String ACTION_DELETE_REMINDER = "DELETE";
	public final static String ACTION_UPDATE_REMINDER = "UPDATE";
	public final static String EXTRA_REMINDER = "REMINDER";
	
	private ReminderDatabase reminderDb;
	
	public AlarmService() {
		super("ContactService");
		reminderDb = ReminderDatabase.getInstance(this);
		reminderDb.open();
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		String action = intent.getAction();
		if (ACTION_CREATE_OR_UPDATE_REMINDER.equals(action))  {
			createAlarm(intent);
		}
		else if (ACTION_DELETE_REMINDER.equals(action))  {
			cancelAlarm(intent);
		}
	}
	
	private void createAlarm(Intent intent)  {
		Reminder reminder = intent.getParcelableExtra(EXTRA_REMINDER);
		DateTime nextReminder = reminder.getNextReminderDate();
		
		AlarmManager alarmManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
		PendingIntent operation = createPendingIntent(reminder);
		Log.i("KIT", "Creating alarm for " + reminder);
		
		//Uncomment to quickly test alarm
		//alarmManager.set(AlarmManager.RTC_WAKEUP, DateTime.now().plusMinutes(1).getMillis(), operation);
		alarmManager.set(AlarmManager.RTC_WAKEUP, nextReminder.getMillis(), operation);
	}
	
	private void cancelAlarm(Intent intent)  {
		
	}
	
	private PendingIntent createPendingIntent(Reminder reminder)  {
		Intent intent = new Intent(this, ReminderAlarmReceiver.class);
		intent.putExtra(EXTRA_REMINDER, reminder);
		return PendingIntent.getBroadcast(this, reminder.getId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}

}
