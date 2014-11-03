package com.android.kit.contacttypes;

import java.util.Set;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.service.ReminderNotificationHelper;
import com.android.kit.sqlite.ReminderDatabase;
import com.google.common.collect.Sets;


public abstract class ContactType  {
	
	protected Context context;
	
	public ContactType(Context context)  {
		this.context = context;
	}
	
	public abstract int getFlag();
	public abstract boolean isDefaultSelected();
	public abstract void register();
	
	protected void tryResetReminder(String phoneNumber)  {
		Set<Integer> contactIds = getContactId(phoneNumber);
		for (Integer id : contactIds)  {
			Reminder reminder = ReminderDatabase.getInstance(context).readReminder(id);
			if (reminder != null && reminder.hasContactType(this) && reminder.getStartReminderDate().isBeforeNow())  {
				Reminder nextReminder = Reminder.createNextReminder(reminder);
				ReminderDatabase.getInstance(context).update(nextReminder);
				ReminderNotificationHelper.cancelNotification(context, id);
				Log.i("KIT", "Reset reminder: " + reminder);
			}
		}
	}
	
	private Set<Integer> getContactId(String phoneNumber)  {
		ContentResolver cr = context.getContentResolver();
		Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
		String[] projection = {ContactsContract.PhoneLookup._ID};
		Cursor cursor = cr.query(uri, projection, null, null, null);
		Set<Integer> contactIds = Sets.newHashSet();
		while (cursor.moveToNext())  {
			contactIds.add(cursor.getInt(0));
		}
		return contactIds;
	}
}
