package com.android.kit;


import java.util.Collection;
import java.util.List;

import android.app.Activity;
import android.app.ListActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.ContactsContract;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.ListView;
import android.widget.Toast;

import com.android.kit.model.Reminder;
import com.android.kit.service.CreateRemindersOnBootReceiver;
import com.android.kit.sqlite.ReminderDatabase;
import com.google.common.collect.Lists;

public class ReminderListActivity extends ListActivity {
	
	public final static String REMINDER_TO_EDIT = "com.android.kit.editcontact";
	private final static int PICK_CONTACT_REQUEST = 1001;
	private final static int EDIT_REMINDER_REQUEST = 1002;
	private final static int ADD_REMINDER_REQUEST = 1003;
	
	private ReminderListAdapter listAdapter;
	private ReminderDatabase reminderDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_reminder_list);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		reminderDb = ReminderDatabase.getInstance(this);
		
		setupListAdapter();
		setupListView();
		setupDatabaseSync();
		
		//Just a test.  Uncomment to test reboot.
		CreateRemindersOnBootReceiver test = new CreateRemindersOnBootReceiver();
		test.onReceive(this, null);
	}
	
	private void setupListAdapter()  {
		Parcelable[] reminderParcels = getIntent().getParcelableArrayExtra(SplashScreenActivity.EXTRA_REMINDER_ARRAY);
		List<Reminder> reminders = Lists.newArrayList();
		for (int i = 0; i < reminderParcels.length; i++)  {
			reminders.add((Reminder)reminderParcels[i]);
		}
		listAdapter = new ReminderListAdapter(this, reminders);
	}
	
	private void setupListView()  {
		ListView listView = getListView();
		setListAdapter(listAdapter);
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener()  {
			@Override
			public boolean onCreateActionMode(ActionMode mode, Menu menu) {
				MenuInflater inflater = mode.getMenuInflater();
				inflater.inflate(R.menu.activity_reminder_list_item_menu, menu);
				return true;
			}

			@Override
			public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
				return false;
			}

			@Override
			public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
				switch (item.getItemId())  {
					case R.id.action_delete:
						List<Reminder> toDelete = listAdapter.getSelectedItems();
						deleteReminders(toDelete);
						mode.finish();
						return false;
					default:
						throw new UnsupportedOperationException("Operation not supported");
				}
			}

			@Override
			public void onDestroyActionMode(ActionMode mode) {
				listAdapter.clearSelection();
			}

			@Override
			public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
				listAdapter.setSelected(position, checked);
				int numSelected = listAdapter.getSelectedItemsCount();
				String title = ReminderListActivity.this.getString(R.string.activity_reminder_list_cab_title, numSelected);
				mode.setTitle(title);
			}
		});
	}
	
	private void setupDatabaseSync()  {
		LocalBroadcastManager.getInstance(this).registerReceiver(new BroadcastReceiver()  {
			@Override
			public void onReceive(Context context, Intent intent) {
				refreshUi();
			}
		}, new IntentFilter(ReminderDatabase.ACTION_REMINDER_DB_UPDATED));
	}
	
	@Override
	public void onListItemClick(ListView listview, View v, int position, long id)  {
		Reminder reminderToEdit = (Reminder)getListAdapter().getItem(position);
		launchEditReminderActivity(reminderToEdit, false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)  {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.activity_reminder_list_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)  {
		switch (menuItem.getItemId())  {
			case R.id.action_add_reminder:
				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST);
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
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
				}
				break;
			case EDIT_REMINDER_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handleEditReminderActivityRequest(data);
				}
				break;
		}
	}
	
	private void handlePickContactActivityRequest(Intent data)  {
		
		Uri contactData = data.getData();
		String[] projection = {
				ContactsContract.Contacts._ID,
				ContactsContract.Contacts.DISPLAY_NAME
		};
		Cursor c = getContentResolver().query(contactData, projection, null, null, null);
		c.moveToFirst();
		int id = c.getInt(0);
		String name = c.getString(1);
		c.close();
		
		Reminder existingReminder = listAdapter.getReminderForContactId(id);
		if (existingReminder == null)  {
			Reminder newReminder = null;
			newReminder = new Reminder(id);
			newReminder.setName(name);
			launchEditReminderActivity(newReminder, true);
			Log.i("KIT", "Created contact " + newReminder.toString());
		}
		else  {
			launchEditReminderActivity(existingReminder, false);
			Toast.makeText(this, R.string.activity_reminder_list_existing_reminder, Toast.LENGTH_LONG).show();
		}
	}
	
	private void handleAddReminderActivityRequest(Intent data)  {
		Reminder editedContact = data.getParcelableExtra(EditReminderActivity.EXTRA_EDITED_REMINDER);
		reminderDb.insert(editedContact);
	}
	
	private void handleEditReminderActivityRequest(Intent data)  {
		Reminder editedReminder = data.getParcelableExtra(EditReminderActivity.EXTRA_EDITED_REMINDER);
		reminderDb.update(editedReminder);
	}
	
	private void deleteReminders(Collection<Reminder> toDelete)  {
		for (Reminder reminder : toDelete)  {
			reminderDb.delete(reminder);
		}
	}
	
	private void refreshUi()  {
		new AsyncTask<Void,Void,List<Reminder>>()  {
			@Override
			protected List<Reminder> doInBackground(Void... params) {
				return reminderDb.readAllReminders();
			}
			
			@Override
			protected void onPostExecute(List<Reminder> result)  {
				listAdapter.clear();
				listAdapter.addAll(result);
			}
		}.execute();
	}
	
	private void launchEditReminderActivity(Reminder reminderToEdit, boolean isNewReminder)  {
		Intent editReminderIntent = new Intent(this, EditReminderActivity.class);
		editReminderIntent.putExtra(REMINDER_TO_EDIT, reminderToEdit);
		startActivityForResult(editReminderIntent, 
				isNewReminder ? ADD_REMINDER_REQUEST : EDIT_REMINDER_REQUEST);
	}
}
