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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.google.common.collect.Lists;

public class ContactListActivity extends Activity {
	
	public final static String KIT_CONTACT_TO_EDIT = "com.android.kit.editcontact";
	private final static int PICK_CONTACT_REQUEST = 1001;
	
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
		listAdapter = new KitContactListAdapter(this, contactList);
		
		listView = (ListView)findViewById(R.id.activity_contact_list_listview);
		listView.setAdapter(listAdapter);
		
		noContactsTextView = (TextView)findViewById(R.id.activity_contact_list_no_contacts_textview);
		updateNoContactsVisibility();
		
		setupAddContactButton();
	}
	
	private void populateContacts()  {
		contactList = Lists.newArrayList();
		//KitContact test = new KitContact(4);
		//test.setName("Test Man");
		//contactList.add(test);
		//TODO: Read list of contacts from storage
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
					KitContact newContact = null;
					Uri contactData = data.getData();
					Cursor c = getContentResolver().query(contactData, null, null, null, null);
					while(c.moveToNext())  {
						int id = c.getInt(c.getColumnIndex("_id"));
						String name = c.getString(c.getColumnIndex("display_name"));
						newContact = new KitContact(id);
						newContact.setName(name);
						contactList.add(newContact);
						Log.i("KIT", "New contact added");
						Log.i("KIT", newContact.toString());
					}
					c.close();
					listAdapter.notifyDataSetChanged();
					updateNoContactsVisibility();
					launchEditContactActivity(newContact);
				}
				break;
			
		}
	}
	
	private void updateNoContactsVisibility()  {
		boolean noContacts = contactList.isEmpty();
		noContactsTextView.setVisibility(noContacts ? View.VISIBLE : View.GONE);
		listView.setVisibility(noContacts ? View.GONE : View.VISIBLE);
	}
	
	private void launchEditContactActivity(KitContact contactToEdit)  {
		Intent editContactIntent = new Intent(this, EditContactActivity.class);
		editContactIntent.putExtra(KIT_CONTACT_TO_EDIT, contactToEdit);
		startActivity(editContactIntent);
	}
}
