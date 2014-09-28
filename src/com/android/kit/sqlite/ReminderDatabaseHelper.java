package com.android.kit.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderDatabaseHelper extends SQLiteOpenHelper {
	public static final String TABLE_REMINDERS = "reminders";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_CONTACT_ID = "contact_id";
	public static final String COLUMN_CONTACT_NAME = "contact_name";
	public static final String COLUMN_FREQUENCY = "frequency";
	public static final String COLUMN_TIME_UNIT = "time_unit";
	public static final String COLUMN_NEXT_REMINDER = "next_reminder_date";
	public static final String COLUMN_CONTACT_TYPES = "contact_types";
	
	private static final String DATABASE_NAME = "reminders.db";
	private static final int VERSION = 1;
	
	private static final String CREATE_DATABASE = "CREATE TABLE " + TABLE_REMINDERS + "("
			+ COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ COLUMN_CONTACT_ID + " INTEGER,"
			+ COLUMN_CONTACT_NAME + " TEXT,"
			+ COLUMN_FREQUENCY + " INTEGER,"
			+ COLUMN_TIME_UNIT + " INTEGER,"
			+ COLUMN_NEXT_REMINDER + " INTEGER,"
			+ COLUMN_CONTACT_TYPES + " INTEGER"
			+ ")";
	
	
	public ReminderDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE_DATABASE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		Log.i("KIT", String.format("Upgrading database %s from %d to %d", DATABASE_NAME, oldVersion, newVersion));
		Log.w("KIT", "Upgrading not yet implemented.  Destroying all previous data.");
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDERS);
		onCreate(db);
	}

}
