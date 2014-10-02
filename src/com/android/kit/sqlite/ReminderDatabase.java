package com.android.kit.sqlite;

import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.support.v4.content.LocalBroadcastManager;

import com.android.kit.model.ContactType;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;

public class ReminderDatabase {
	public final static String ACTION_REMINDER_DB_UPDATED = "com.android.kit.sqlite.ACTION_REMINDER_DB_UPDATED"; 
	public final static String EXTRA_REMINDER = "EXTRA_REMINDER";
	
	private final static String[] ALL_COLUMNS = {
		RemindersContract.COLUMN_CONTACT_ID,
		RemindersContract.COLUMN_CONTACT_NAME,
		RemindersContract.COLUMN_FREQUENCY,
		RemindersContract.COLUMN_TIME_UNIT,
		RemindersContract.COLUMN_NEXT_REMINDER,
		RemindersContract.COLUMN_CONTACT_TYPES
	};
	
	private Context context;
	private ReminderDatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	private static ReminderDatabase db;
	
	public static ReminderDatabase getInstance(Context context)  {
		if (db == null)  {
			db = new ReminderDatabase(context.getApplicationContext());
			db.open();
		}
		return db;
	}
	
	private ReminderDatabase(Context context)  {
		dbHelper = new ReminderDatabaseHelper(context);
		this.context = context;
	}
	
	public void open() throws SQLiteException  {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()  {
		if (database != null)  {
			database.close();
		}
	}
	
	public List<Reminder> readAllReminders()  {
		List<Reminder> contacts = Lists.newArrayList();
		Cursor cursor = database.query(RemindersContract.TABLE_REMINDERS, ALL_COLUMNS, 
				null, null, null, null, null);
		while (cursor.moveToNext())  {
			Reminder contact = new Reminder(cursor.getInt(0));
			contact.setName(cursor.getString(1));
			contact.setFrequency(cursor.getInt(2));
			contact.setFrequencyUnit(TimeUnit.getTimeUnitFromId(cursor.getInt(3)));
			contact.setNextReminderDate(new DateTime(cursor.getLong(4)));
			contact.setContactTypes(ContactType.convertContactTypeValue(cursor.getInt(5)));
			contacts.add(contact);
		}
		cursor.close();
		return contacts;
	}
	
	public void insert(Reminder reminder) throws SQLException {
		ContentValues values = convertReminderToContentValues(reminder);
		database.insertOrThrow(RemindersContract.TABLE_REMINDERS, null, values);
		sendDbChangedBroadcast(ACTION_REMINDER_DB_UPDATED, reminder);
	}
	
	public void update(Reminder reminder)  {
		ContentValues values = convertReminderToContentValues(reminder);
		database.update(RemindersContract.TABLE_REMINDERS,
				values,
				RemindersContract.COLUMN_CONTACT_ID + " = ?",
				new String[] {Integer.toString(reminder.getContactId())});
		//TODO: Error check
		sendDbChangedBroadcast(ACTION_REMINDER_DB_UPDATED, reminder);
	}
	
	public void delete(Reminder reminder)  {
		database.delete(RemindersContract.TABLE_REMINDERS,
				RemindersContract.COLUMN_CONTACT_ID + " = ?",
				new String[] { Integer.toString(reminder.getContactId()) });
		sendDbChangedBroadcast(ACTION_REMINDER_DB_UPDATED, null);
	}
	
	private ContentValues convertReminderToContentValues(Reminder contact)  {
		ContentValues values = new ContentValues();
		values.put(RemindersContract.COLUMN_CONTACT_ID, contact.getContactId());
		values.put(RemindersContract.COLUMN_CONTACT_NAME, contact.getName());
		values.put(RemindersContract.COLUMN_FREQUENCY, contact.getFrequency());
		values.put(RemindersContract.COLUMN_TIME_UNIT, contact.getFrequencyUnit().getId());
		values.put(RemindersContract.COLUMN_NEXT_REMINDER, contact.getNextReminderDate().getMillis());
		values.put(RemindersContract.COLUMN_CONTACT_TYPES, ContactType.convertContactTypeCollection(contact.getContactTypes()));
		return values;
	}
	
	//TODO: Currently always sending ACTION_REMINDER_DB_UPDATED.  Possible to be more efficient to
	//specify which rows were updated.
	private void sendDbChangedBroadcast(String action, Reminder reminder)  {
		Intent broadcastIntent = new Intent(action);
		broadcastIntent.putExtra(EXTRA_REMINDER, reminder);
		LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
	}
}
