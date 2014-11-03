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

import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;

public class ReminderDatabase {
	public final static String ACTION_REMINDER_DB_UPDATED = "com.android.kit.sqlite.ACTION_REMINDER_DB_UPDATED"; 
	public final static String EXTRA_REMINDER_ID = "EXTRA_REMINDER_ID";
	
	private final static String[] ALL_COLUMNS = {
		RemindersContract.COLUMN_CONTACT_ID,
		RemindersContract.COLUMN_CONTACT_NAME,
		RemindersContract.COLUMN_FREQUENCY,
		RemindersContract.COLUMN_TIME_UNIT,
		RemindersContract.COLUMN_START_REMINDER,
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
		List<Reminder> reminders = Lists.newArrayList();
		Cursor cursor = database.query(RemindersContract.TABLE_REMINDERS, ALL_COLUMNS, 
				null, null, null, null, null);
		while (cursor.moveToNext())  {
			Reminder reminder = convertCursorRowToReminder(cursor);
			reminders.add(reminder);
		}
		cursor.close();
		return reminders;
	}
	
	public Reminder readReminder(int contactId)  {
		Cursor cursor = database.query(RemindersContract.TABLE_REMINDERS, ALL_COLUMNS, 
				RemindersContract.COLUMN_CONTACT_ID + " = ?", new String[] {Integer.toString(contactId)}, null, null, null);
		Reminder reminder = null;
		if (cursor.moveToFirst())  {
			reminder = convertCursorRowToReminder(cursor);
		}
		cursor.close();
		return reminder;
	}
	
	private Reminder convertCursorRowToReminder(Cursor cursor)  {
		Reminder reminder = new Reminder(cursor.getInt(0),
				cursor.getString(1),
				cursor.getInt(2),
				TimeUnit.getTimeUnitFromId(cursor.getInt(3)),
				new DateTime(cursor.getLong(4)),
				new DateTime(cursor.getLong(5)),
				cursor.getInt(6));
		return reminder;
	}
	
	public void insert(Reminder reminder) throws SQLException {
		ContentValues values = convertReminderToContentValues(reminder);
		database.insertOrThrow(RemindersContract.TABLE_REMINDERS, null, values);
		sendDbChangedBroadcast(Intent.ACTION_INSERT, reminder.getContactId());
	}
	
	public int update(Reminder reminder)  {
		ContentValues values = convertReminderToContentValues(reminder);
		int numUpdated = database.update(RemindersContract.TABLE_REMINDERS,
				values,
				RemindersContract.COLUMN_CONTACT_ID + " = ?",
				new String[] {Integer.toString(reminder.getContactId())});
		//TODO: Error check
		sendDbChangedBroadcast(Intent.ACTION_EDIT, reminder.getContactId());
		return numUpdated;
	}
	
	public void delete(Reminder reminder)  {
		database.delete(RemindersContract.TABLE_REMINDERS,
				RemindersContract.COLUMN_CONTACT_ID + " = ?",
				new String[] { Integer.toString(reminder.getContactId()) });
		sendDbChangedBroadcast(Intent.ACTION_DELETE, reminder.getContactId());
	}
	
	private ContentValues convertReminderToContentValues(Reminder reminder)  {
		ContentValues values = new ContentValues();
		values.put(RemindersContract.COLUMN_CONTACT_ID, reminder.getContactId());
		values.put(RemindersContract.COLUMN_CONTACT_NAME, reminder.getName());
		values.put(RemindersContract.COLUMN_FREQUENCY, reminder.getFrequency());
		values.put(RemindersContract.COLUMN_TIME_UNIT, reminder.getFrequencyUnit().getId());
		values.put(RemindersContract.COLUMN_START_REMINDER, reminder.getStartReminderDate().getMillis());
		values.put(RemindersContract.COLUMN_NEXT_REMINDER, reminder.getNextReminderDate().getMillis());
		values.put(RemindersContract.COLUMN_CONTACT_TYPES, reminder.getContactTypeFlags());
		return values;
	}
	
	private void sendDbChangedBroadcast(String action, int contactId)  {
		Intent broadcastIntent = new Intent(action);
		broadcastIntent.putExtra(EXTRA_REMINDER_ID, contactId);
		LocalBroadcastManager.getInstance(context).sendBroadcast(broadcastIntent);
	}
}
