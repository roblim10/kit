package com.android.kit.sqlite;

import java.util.List;

import org.joda.time.DateTime;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;

import com.android.kit.model.ContactType;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;

public class ReminderDatabase {
	private final static String[] ALL_COLUMNS = {
		ReminderDatabaseHelper.COLUMN_CONTACT_ID,
		ReminderDatabaseHelper.COLUMN_FREQUENCY,
		ReminderDatabaseHelper.COLUMN_TIME_UNIT,
		ReminderDatabaseHelper.COLUMN_NEXT_REMINDER,
		ReminderDatabaseHelper.COLUMN_CONTACT_TYPES
	};
	
	private ReminderDatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	public ReminderDatabase(Context context)  {
		dbHelper = new ReminderDatabaseHelper(context);
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
		Cursor cursor = database.query(ReminderDatabaseHelper.TABLE_REMINDERS, ALL_COLUMNS, 
				null, null, null, null, null);
		cursor.moveToFirst();
		while (cursor.moveToNext())  {
			//TODO: We are not getting the contact's name here.  Should we store it here or should we read it from
			//the native Contacts database?
			Reminder contact = new Reminder(cursor.getInt(0));
			contact.setFrequency(cursor.getInt(1));
			contact.setFrequencyUnit(TimeUnit.getTimeUnitFromId(cursor.getInt(2)));
			contact.setNextReminderDate(new DateTime(cursor.getLong(3)));
			contact.setContactTypes(ContactType.convertContactTypeValue(cursor.getInt(4)));
			contacts.add(contact);
		}
		return contacts;
	}
	
	public void insert(Reminder contact) throws SQLException {
		ContentValues values = convertKitContactToContentValues(contact);
		database.insertOrThrow(ReminderDatabaseHelper.TABLE_REMINDERS, null, values);
	}
	
	public void update(Reminder contact)  {
		ContentValues values = convertKitContactToContentValues(contact);
		database.update(ReminderDatabaseHelper.TABLE_REMINDERS,
				values,
				ReminderDatabaseHelper.COLUMN_CONTACT_ID + " = ?",
				new String[] {Integer.toString(contact.getId())});
		//TODO: Error check
	}
	
	private ContentValues convertKitContactToContentValues(Reminder contact)  {
		ContentValues values = new ContentValues();
		values.put(ReminderDatabaseHelper.COLUMN_CONTACT_ID, contact.getId());
		values.put(ReminderDatabaseHelper.COLUMN_FREQUENCY, contact.getFrequency());
		values.put(ReminderDatabaseHelper.COLUMN_TIME_UNIT, contact.getFrequencyUnit().getId());
		values.put(ReminderDatabaseHelper.COLUMN_NEXT_REMINDER, contact.getNextReminderDate().getMillis());
		values.put(ReminderDatabaseHelper.COLUMN_CONTACT_TYPES, ContactType.convertContactTypeCollection(contact.getContactTypes()));
		return values;
	}
}
