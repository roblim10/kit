package com.android.kit;


import java.util.EnumSet;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.kit.model.CheckBoxListItemModel;
import com.android.kit.model.ContactType;
import com.android.kit.model.KitContact;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EditContactActivity extends Activity {
	private KitContact contactToEdit;
	
	private EditText numberEditText;
	private Spinner unitsSpinner;
	private CheckBoxListAdapter<ContactType> contactTypeListAdapter;
	private ListView contactTypeListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);
		
		Intent fromIntent = getIntent();
		contactToEdit = (KitContact)fromIntent.getParcelableExtra(ContactListActivity.KIT_CONTACT_TO_EDIT);
		Log.i("KIT", "Editing contact: " + contactToEdit.toString());
		
		numberEditText = (EditText)findViewById(R.id.activity_edit_contact_number_edittext);
		
		unitsSpinner = (Spinner)findViewById(R.id.activity_edit_contact_units_spinner);
		TextView nameTextView = (TextView)findViewById(R.id.activity_edit_contact_name_textview);
		
		nameTextView.setText(contactToEdit.getName());
		
		setupContactTypeListView();
	}
	
	private void setupContactTypeListView()  {
		List<CheckBoxListItemModel<ContactType>> options = Lists.newArrayList();
		options.add(new CheckBoxListItemModel<ContactType>(ContactType.PHONE_CALL, 
				ContactType.PHONE_CALL.toString(), true));
		options.add(new CheckBoxListItemModel<ContactType>(ContactType.SMS, ContactType.SMS.toString()));
		contactTypeListAdapter = new CheckBoxListAdapter<ContactType>(this, options);
		contactTypeListView = (ListView)findViewById(R.id.activity_edit_contact_contact_type_listview);
		contactTypeListView.setAdapter(contactTypeListAdapter);
		
		contactTypeListView.setOnItemClickListener(new OnItemClickListener()  {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckBoxListItemModel<ContactType> model = 
						(CheckBoxListItemModel<ContactType>)parent.getItemAtPosition(position);
				model.setIsChecked(true);
			}
		});
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
				//TODO: Need to validate input
				saveContact();
				Intent intent = new Intent();
				intent.putExtra(ContactListActivity.KIT_CONTACT_TO_EDIT, contactToEdit);
				setResult(RESULT_OK, intent);
				finish();
				return true;
			case R.id.action_cancel:
				setResult(RESULT_CANCELED);
				finish();
				return true;
			default:
				return super.onOptionsItemSelected(menuItem);
		}
	}
	
	private void saveContact()  {
		int frequency = Integer.parseInt(numberEditText.getText().toString());
		TimeUnit units = getUnitSpinnerValue();
		//TODO: This isn't correct. Need to adjust so that we don't always add from current date/time.
		DateTime nextReminderDate = calculateNextReminderDate(DateTime.now(), frequency, units);
		Set<ContactType> contactTypes = getSelectedContactTypes();
		
		contactToEdit.setReminderFrequency(frequency);
		contactToEdit.setReminderFrequencyUnit(units);
		contactToEdit.setNextReminderDate(nextReminderDate);
		contactToEdit.setContactTypes(contactTypes);
	}
	
	//TODO: Two switch statements in the next two methods.  Can we do something better?
	private TimeUnit getUnitSpinnerValue()  {
		int spinnerPosition = unitsSpinner.getSelectedItemPosition();
		switch(spinnerPosition)  {
			case 0: return TimeUnit.DAYS;
			case 1: return TimeUnit.WEEKS;
			case 2: return TimeUnit.MONTHS;
			case 3: return TimeUnit.YEARS;
			default: throw new UnsupportedOperationException(
					"User selected TimeUnit that is not supported.  TimeUnit position = " + spinnerPosition);
		}
	}
	
	private DateTime calculateNextReminderDate(DateTime fromDate, int frequency, TimeUnit units)  {
		switch(units)  {
			case DAYS: return fromDate.plusDays(frequency);
			case WEEKS: return fromDate.plusWeeks(frequency);
			case MONTHS: return fromDate.plusMonths(frequency);
			case YEARS: return fromDate.plusYears(frequency);
			default: throw new UnsupportedOperationException(
					"Unknown time unit when calculating reminder date.  TimeUnit = " + units.toString());
		}
	}
	
	private Set<ContactType> getSelectedContactTypes()  {
		Set<ContactType> contactTypes = Sets.newHashSet();
		for (int i = 0; i < contactTypeListAdapter.getCount(); i++)  {
			CheckBoxListItemModel<ContactType> model = contactTypeListAdapter.getItem(i);
			if (model.isChecked())  {
				contactTypes.add(model.getData());
			}
		}
		return contactTypes;
	}
}
