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
import com.android.kit.model.KitContact;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;

public class KitContactDatabase {
	private final static String[] ALL_COLUMNS = {
		KitContactDatabaseHelper.COLUMN_CONTACT_ID,
		KitContactDatabaseHelper.COLUMN_FREQUENCY,
		KitContactDatabaseHelper.COLUMN_TIME_UNIT,
		KitContactDatabaseHelper.COLUMN_NEXT_REMINDER,
		KitContactDatabaseHelper.COLUMN_CONTACT_TYPES
	};
	
	private KitContactDatabaseHelper dbHelper;
	private SQLiteDatabase database;
	
	public KitContactDatabase(Context context)  {
		dbHelper = new KitContactDatabaseHelper(context);
	}
	
	public void open() throws SQLiteException  {
		database = dbHelper.getWritableDatabase();
	}
	
	public void close()  {
		if (database != null)  {
			database.close();
		}
	}
	
	public List<KitContact> readAllContacts()  {
		List<KitContact> contacts = Lists.newArrayList();
		Cursor cursor = database.query(KitContactDatabaseHelper.TABLE_CONTACTS, ALL_COLUMNS, 
				null, null, null, null, null);
		cursor.moveToFirst();
		while (cursor.moveToNext())  {
			//TODO: We are not getting the contact's name here.  Should we store it here or should we read it from
			//the native Contacts database?
			KitContact contact = new KitContact(cursor.getInt(0));
			contact.setReminderFrequency(cursor.getInt(1));
			contact.setReminderFrequencyUnit(TimeUnit.getTimeUnitFromId(cursor.getInt(2)));
			contact.setNextReminderDate(new DateTime(cursor.getLong(3)));
			contact.setContactTypes(ContactType.convertContactTypeValue(cursor.getInt(4)));
			contacts.add(contact);
		}
		return contacts;
	}
	
	public void insert(KitContact contact) throws SQLException {
		ContentValues values = convertKitContactToContentValues(contact);
		database.insertOrThrow(KitContactDatabaseHelper.TABLE_CONTACTS, null, values);
	}
	
	public void update(KitContact contact)  {
		ContentValues values = convertKitContactToContentValues(contact);
		database.update(KitContactDatabaseHelper.TABLE_CONTACTS,
				values,
				KitContactDatabaseHelper.COLUMN_CONTACT_ID + " = ?",
				new String[] {Integer.toString(contact.getId())});
		//TODO: Error check
	}
	
	private ContentValues convertKitContactToContentValues(KitContact contact)  {
		ContentValues values = new ContentValues();
		values.put(KitContactDatabaseHelper.COLUMN_CONTACT_ID, contact.getId());
		values.put(KitContactDatabaseHelper.COLUMN_FREQUENCY, contact.getReminderFrequency());
		values.put(KitContactDatabaseHelper.COLUMN_TIME_UNIT, contact.getReminderFrequencyUnit().getId());
		values.put(KitContactDatabaseHelper.COLUMN_NEXT_REMINDER, contact.getNextReminderDate().getMillis());
		values.put(KitContactDatabaseHelper.COLUMN_CONTACT_TYPES, ContactType.convertContactTypeCollection(contact.getContactTypes()));
		return values;
	}
}
