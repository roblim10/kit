package com.android.kit;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TextView;

public class EditContactActivity extends Activity {
	private KitContact contactToEdit;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);
		
		Intent fromIntent = getIntent();
		contactToEdit = (KitContact)fromIntent.getParcelableExtra(ContactListActivity.KIT_CONTACT_TO_EDIT);
		Log.i("KIT", "Editing contact: " + contactToEdit.toString());
		
		TextView nameTextView = (TextView)findViewById(R.id.activity_edit_contact_name_textview);
		nameTextView.setText(contactToEdit.getName());
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)  {
		MenuInflater menuInflater = getMenuInflater();
		menuInflater.inflate(R.menu.activity_edit_contact_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem menuItem)  {
		switch (menuItem.getItemId())  {
			case R.id.action_save:
				finish();
				return true;
			case R.id.action_cancel:
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
}
