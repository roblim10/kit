package com.android.kit.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.provider.Settings;

import com.android.kit.NotificationHandlerActivity;
import com.android.kit.R;
import com.android.kit.model.Reminder;

public class ReminderNotificationHelper  {
	
	public static void sendNotification(Context context, Reminder reminder)  {
		//TODO:Fill out content text
		Notification n = new Notification.Builder(context)
			.setContentTitle(context.getString(R.string.notification_content_title, reminder.getName()))
			.setContentText(context.getString(R.string.notification_content_text, "TODO"))
			.setSmallIcon(R.drawable.ic_launcher_small)
			.setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_launcher))
			.setTicker(context.getString(R.string.notification_content_title, reminder.getName()))
			.setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
			.setContentIntent(createPendingIntent(context, reminder))
			.setDeleteIntent(createDeletePendingIntent(context, reminder))
			.build();

		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.notify(reminder.getContactId(), n);
	}
	
	private static PendingIntent createPendingIntent(Context context, Reminder reminder)  {
		Intent intent = new Intent(context, NotificationHandlerActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
		intent.putExtra(AlarmService.EXTRA_REMINDER, reminder);
		return PendingIntent.getActivity(context, reminder.getContactId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	private static PendingIntent createDeletePendingIntent(Context context, Reminder reminder)  {
		Intent intent = new Intent(context, NotificationRemovedReceiver.class);
		intent.putExtra(AlarmService.EXTRA_REMINDER,  reminder);
		return PendingIntent.getBroadcast(context, reminder.getContactId(), intent, PendingIntent.FLAG_UPDATE_CURRENT);
	}
	
	public static void cancelNotification(Context context, int contactId)  {
		NotificationManager manager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
		manager.cancel(contactId);
	}
}
