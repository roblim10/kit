package com.android.kit.sqlite;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ReminderDatabaseHelper extends SQLiteOpenHelper {
	private static final String DATABASE_NAME = "reminders.db";
	private static final int VERSION = 1;
	
	private static final String CREATE_DATABASE = "CREATE TABLE " + RemindersContract.TABLE_REMINDERS + "("
			+ RemindersContract.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
			+ RemindersContract.COLUMN_CONTACT_ID + " INTEGER,"
			+ RemindersContract.COLUMN_CONTACT_NAME + " TEXT,"
			+ RemindersContract.COLUMN_FREQUENCY + " INTEGER,"
			+ RemindersContract.COLUMN_TIME_UNIT + " INTEGER,"
			+ RemindersContract.COLUMN_NEXT_REMINDER + " INTEGER,"
			+ RemindersContract.COLUMN_CONTACT_TYPES + " INTEGER"
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
		db.execSQL("DROP TABLE IF EXISTS " + RemindersContract.TABLE_REMINDERS);
		onCreate(db);
	}

}
