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

import com.google.common.collect.Lists;

public class ContactListActivity extends Activity {
	
	public final static String KIT_CONTACT_TO_EDIT = "com.android.kit.editcontact";
	private final static int PICK_CONTACT_REQUEST = 1001;
	private final static int EDIT_CONTACT_REQUEST = 1002;
	private final static int ADD_CONTACT_REQUEST = 1003;
	
	private TextView noContactsTextView;
	private ListView listView;
	
	private List<KitContact> contactList;
	private KitContactListAdapter listAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_contact_list);
		getActionBar().setDisplayShowTitleEnabled(false);
		
		populateContacts();
		setupListView();
		setupAddContactButton();
		
		noContactsTextView = (TextView)findViewById(R.id.activity_contact_list_no_contacts_textview);
		updateNoContactsVisibility();
	}
	
	private void populateContacts()  {
		contactList = Lists.newArrayList();
		//KitContact test = new KitContact(4);
		//test.setName("Test Man");
		//contactList.add(test);
		//TODO: Read list of contacts from storage
	}
	
	private void setupListView()  {
		listAdapter = new KitContactListAdapter(this, contactList);
		listView = (ListView)findViewById(R.id.activity_contact_list_listview);
		listView.setAdapter(listAdapter);
		listView.setOnItemClickListener(new OnItemClickListener()  {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				KitContact contactToEdit = contactList.get(position);
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
				}
				break;
		}
	}
	
	private void handlePickContactActivityRequest(Intent data)  {
		KitContact newContact = null;
		Uri contactData = data.getData();
		Cursor c = getContentResolver().query(contactData, null, null, null, null);
		while(c.moveToNext())  {
			int id = c.getInt(c.getColumnIndex("_id"));
			String name = c.getString(c.getColumnIndex("display_name"));
			newContact = new KitContact(id);
			newContact.setName(name);
			Log.i("KIT", "Created contact " + newContact.toString());
		}
		c.close();
		launchEditContactActivity(newContact, true);		
	}
	
	private void handleAddContactActivityRequest(Intent data)  {
		KitContact contactToEdit = (KitContact)data.getParcelableExtra(KIT_CONTACT_TO_EDIT);
		contactList.add(contactToEdit);
		listAdapter.notifyDataSetChanged();
		updateNoContactsVisibility();
	}
	
	private void updateNoContactsVisibility()  {
		boolean noContacts = contactList.isEmpty();
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
