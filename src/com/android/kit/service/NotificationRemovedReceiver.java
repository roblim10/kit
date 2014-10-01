package com.android.kit.service;

import org.joda.time.DateTime;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.android.kit.sqlite.ReminderDatabase;

public class NotificationRemovedReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		Log.i("KIT", "Notification removed!");
		Reminder reminder = intent.getParcelableExtra(AlarmService.EXTRA_REMINDER);
		
		setNextAlarm(context, reminder);
	}

	
	private void setNextAlarm(Context context, Reminder reminder)  {
		DateTime nextReminder = calculateNextReminderDate(reminder);
		reminder.setNextReminderDate(nextReminder);
		ReminderDatabase.getInstance(context).update(reminder);
	}
	
	//TODO:Fix
	private DateTime calculateNextReminderDate(Reminder reminder)  {
//		int frequency = reminder.getFrequency();
//		TimeUnit unit = reminder.getFrequencyUnit();
//		DateTime newReminderDate = reminder.getNextReminderDate();
//		do  {
//			switch (unit)  {
//				case DAYS: newReminderDate = newReminderDate.plusDays(frequency); break;
//				case WEEKS: newReminderDate = newReminderDate.plusWeeks(frequency); break;
//				case MONTHS: newReminderDate = newReminderDate.plusMonths(frequency); break;
//				case YEARS: newReminderDate = newReminderDate.plusYears(frequency); break;
//				default:
//					throw new UnsupportedOperationException("Unknown TimeUnit: " + unit);
//			}
//		} while (newReminderDate.isBeforeNow());
//		return newReminderDate;
		return DateTime.now().plusSeconds(20);
	}
}
