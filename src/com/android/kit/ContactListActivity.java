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

import com.android.kit.model.KitContact;
import com.android.kit.sqlite.KitContactDatabase;

public class ContactListActivity extends Activity {
	
	public final static String KIT_CONTACT_TO_EDIT = "com.android.kit.editcontact";
	private final static int PICK_CONTACT_REQUEST = 1001;
	private final static int EDIT_CONTACT_REQUEST = 1002;
	private final static int ADD_CONTACT_REQUEST = 1003;
	
	private TextView noContactsTextView;
	private ListView listView;
	
	private KitContactListAdapter listAdapter;
	private KitContactDatabase contactsDb;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		setupDatabase();
		setupListAdapter();
		setupListView();
		setupAddContactButton();
		
		noContactsTextView = (TextView)findViewById(R.id.activity_contact_list_no_contacts_textview);
		updateViewVisibility();
	}
	
	private void setupDatabase()  {
		contactsDb = new KitContactDatabase(this);
		contactsDb.open();
	}
	
	private void setupListAdapter()  {
		//TODO: Is this efficient?  Should we do one query and then modify the contacts?
		List<KitContact> contacts = contactsDb.readAllContacts();
		for (KitContact contact : contacts)  {
			Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, 
					null, 
					ContactsContract.CommonDataKinds.Identity._ID + " = ?", 
					new String[] {Integer.toString(contact.getId())}, 
					null);
			cursor.moveToNext();
			String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
			contact.setName(name);
		}
		listAdapter = new KitContactListAdapter(this,contacts);
	}
	
	private void setupListView()  {
		listView = (ListView)findViewById(R.id.activity_contact_list_listview);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()  {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KitContact contactToEdit = (KitContact)parent.getItemAtPosition(position);
				launchEditContactActivity(contactToEdit, false);
			}
		});
	}
	
	private void setupAddContactButton()  {
		Button addContactButton = (Button)findViewById(R.id.activity_contact_list_add_contact_button);
		addContactButton.setOnClickListener(new OnClickListener()  {
			@Override
			public void onClick(View v) {
				Intent contactPickerIntent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
				startActivityForResult(contactPickerIntent, PICK_CONTACT_REQUEST);
			}
		});
	}
	
	@Override
	protected void onDestroy()  {
		Log.d("KIT", "closing database");
		contactsDb.close();
		super.onStop();
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
			case ADD_CONTACT_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handleAddContactActivityRequest(data);
					refreshUi();
				}
				break;
			case EDIT_CONTACT_REQUEST:
				if (resultCode == Activity.RESULT_OK)  {
					handleEditContactActivityRequest(data);
					refreshUi();
				}
				break;
		}
	}
	
	private void handlePickContactActivityRequest(Intent data)  {
		KitContact newContact = null;
		Uri contactData = data.getData();
		Cursor c = getContentResolver().query(contactData, null, null, null, null);
		while(c.moveToNext())  {
			int id = c.getInt(c.getColumnIndex(ContactsContract.CommonDataKinds.Identity._ID));
			String name = c.getString(c.getColumnIndex(ContactsContract.CommonDataKinds.Identity.DISPLAY_NAME));
			newContact = new KitContact(id);
			newContact.setName(name);
			Log.i("KIT", "Created contact " + newContact.toString());
		}
		c.close();
		listAdapter.add(newContact);
		contactsDb.insert(newContact);
		launchEditContactActivity(newContact, true);		
	}
	
	private void handleAddContactActivityRequest(Intent data)  {
		KitContact editedContact = data.getParcelableExtra(EditContactActivity.EXTRA_EDITED_CONTACT);
		listAdapter.add(editedContact);
		contactsDb.insert(editedContact);
	}
	
	private void handleEditContactActivityRequest(Intent data)  {
		KitContact editedContact = data.getParcelableExtra(EditContactActivity.EXTRA_EDITED_CONTACT);
		KitContact existingContact = listAdapter.getContactById(editedContact.getId());
		existingContact.setReminderFrequency(editedContact.getReminderFrequency());
		existingContact.setReminderFrequencyUnit(editedContact.getReminderFrequencyUnit());
		existingContact.setNextReminderDate(editedContact.getNextReminderDate());
		existingContact.setContactTypes(editedContact.getContactTypes());
		contactsDb.update(existingContact);
		
	}
	
	private void refreshUi()  {
		listAdapter.notifyDataSetChanged();
		updateViewVisibility();
	}
	
	private void updateViewVisibility()  {
		boolean noContacts = listAdapter.isEmpty();
		noContactsTextView.setVisibility(noContacts ? View.VISIBLE : View.GONE);
		listView.setVisibility(noContacts ? View.GONE : View.VISIBLE);
	}
	
	private void launchEditContactActivity(KitContact contactToEdit, boolean isNewContact)  {
		Intent editContactIntent = new Intent(this, EditContactActivity.class);
		editContactIntent.putExtra(KIT_CONTACT_TO_EDIT, contactToEdit);
		startActivityForResult(editContactIntent, 
				isNewContact ? ADD_CONTACT_REQUEST : EDIT_CONTACT_REQUEST);
	}
}
