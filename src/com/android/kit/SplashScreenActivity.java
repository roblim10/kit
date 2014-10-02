package com.android.kit;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class SplashScreenActivity extends Activity {

	public final static String EXTRA_REMINDER_ARRAY = "com.android.kit.EXTRA_REMINDER_ARRAY";
	
	@Override
	public void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_splash_screen);
		
		new AsyncTask<Void, Void, Reminder[]>()  {
			@Override
			protected Reminder[] doInBackground(Void... params) {
				List<Reminder> reminders = ReminderDatabase.getInstance(SplashScreenActivity.this).readAllReminders();
				Reminder[] remindersArray = new Reminder[reminders.size()];
				reminders.toArray(remindersArray);
				return remindersArray;
			}
			
			@Override
			protected void onPostExecute(Reminder[] reminders)  {
				Intent intent = new Intent(SplashScreenActivity.this, ReminderListActivity.class);
				intent.putExtra(EXTRA_REMINDER_ARRAY, reminders);
				startActivity(intent);
				SplashScreenActivity.this.finish();
			}
		}.execute();
	}
}
