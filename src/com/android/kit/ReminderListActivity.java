package com.android.kit;


import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;

public class ReminderListActivity extends Activity {
	
	public final static String REMINDER_TO_EDIT = "com.android.kit.editcontact";
	private final static int PICK_CONTACT_REQUEST = 1001;
	private final static int EDIT_REMINDER_REQUEST = 1002;
	private final static int ADD_REMINDER_REQUEST = 1003;
	
	private TextView noRemindersTextView;
	private ListView listView;
	
	private ReminderListAdapter listAdapter;
	private ReminderDatabase reminderDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder_list);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		setupDatabase();
		setupListAdapter();
		setupListView();
		setupAddContactButton();
		
		noRemindersTextView = (TextView)findViewById(R.id.activity_contact_list_no_reminders_textview);
		updateViewVisibility();
	}
	
	private void setupDatabase()  {
		reminderDb = new ReminderDatabase(this);
		Log.d("KIT", "Opening reminders DB");
		reminderDb.open();
	}
	
	private void setupListAdapter()  {
		//TODO: Is this efficient?  Should we do one query and then modify the reminders?
		List<Reminder> reminders = reminderDb.readAllReminders();
		for (Reminder reminder : reminders)  {
			Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
					null, 
					ContactsContract.CommonDataKinds.Identity._ID + " = ?", 
					new String[] {Integer.toString(reminder.getId())}, 
					null);
			cursor.moveToNext();
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
			reminder.setName(name);
		}
		listAdapter = new ReminderListAdapter(this,reminders);
	}
	
	private void setupListView()  {
		listView = (ListView)findViewById(R.id.activity_reminder_listview);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()  {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Reminder reminderToEdit = (Reminder)parent.getItemAtPosition(position);
				launchEditReminderActivity(reminderToEdit, false);
			}
		});
	}
	
	private void setupAddContactButton()  {
		Button addReminderButton = (Button)findViewById(R.id.activity_reminder_list_add_button);
		addReminderButton.setOnClickListener(new OnClickListener()  {
			@Override
			public void onClick(View v) {
				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST);
			}
		});
	}
	
	@Override
	protected void onDestroy()  {
		Log.d("KIT", "Closing reminders database");
		reminderDb.close();
		super.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)  {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode)  {
			case PICK_CONTACT_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handlePickContactActivityRequest(data);
				}
				break;
			case ADD_REMINDER_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handleAddReminderActivityRequest(data);
					refreshUi();
				}
				break;
			case EDIT_REMINDER_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handleEditReminderActivityRequest(data);
					refreshUi();
				}
				break;
		}
	}
	
	private void handlePickContactActivityRequest(Intent data)  {
		Reminder newReminder = null;
		Uri contactData = data.getData();
		Cursor c = getContentResolver().query(contactData, null, null, null, null);
		while(c.moveToNext())  {
			int id = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Identity._ID));
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
			newReminder = new Reminder(id);
			newReminder.setName(name);
			Log.i("KIT", "Created contact " + newReminder.toString());
		}
		c.close();
		launchEditReminderActivity(newReminder, true);		
	}
	
	private void handleAddReminderActivityRequest(Intent data)  {
		Reminder editedContact = data.getParcelableExtra(EditReminderActivity.EXTRA_EDITED_REMINDER);
		listAdapter.add(editedContact);
		reminderDb.insert(editedContact);
	}
	
	private void handleEditReminderActivityRequest(Intent data)  {
		Reminder editedReminder = data.getParcelableExtra(EditReminderActivity.EXTRA_EDITED_REMINDER);
		Reminder existingReminder = listAdapter.getReminderByContactId(editedReminder.getId());
		existingReminder.setFrequency(editedReminder.getFrequency());
		existingReminder.setFrequencyUnit(editedReminder.getFrequencyUnit());
		existingReminder.setNextReminderDate(editedReminder.getNextReminderDate());
		existingReminder.setContactTypes(editedReminder.getContactTypes());
		reminderDb.update(existingReminder);
		
	}
	
	private void refreshUi()  {
		listAdapter.notifyDataSetChanged();
		updateViewVisibility();
	}
	
	private void updateViewVisibility()  {
		boolean noReminders = listAdapter.isEmpty();
		noRemindersTextView.setVisibility(noReminders ? View.VISIBLE : View.GONE);
		listView.setVisibility(noReminders ? View.GONE : View.VISIBLE);
	}
	
	private void launchEditReminderActivity(Reminder reminderToEdit, boolean isNewReminder)  {
		Intent editReminderIntent = new Intent(this, EditReminderActivity.class);
		editReminderIntent.putExtra(REMINDER_TO_EDIT, reminderToEdit);
		startActivityForResult(editReminderIntent, 
				isNewReminder ? ADD_REMINDER_REQUEST : EDIT_REMINDER_REQUEST);
	}
}
