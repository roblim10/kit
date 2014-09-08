package com.android.kit;


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
import android.widget.ArrayAdapter;
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
	private final static int DEFAULT_FREQUENCY = 1;
	private final static int DEFAULT_UNIT = 1;
	private final static TimeUnit[] SPINNER_ITEMS = {
		TimeUnit.DAYS,
		TimeUnit.WEEKS,
		TimeUnit.MONTHS,
		TimeUnit.YEARS
	};
	
	private KitContact contactToEdit;
	private boolean isNewContact;
	
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
		isNewContact = (boolean)fromIntent.getBooleanExtra(ContactListActivity.KIT_CONTACT_IS_NEW, false);
		Log.i("KIT", "Editing contact: " + contactToEdit.toString());
		
		TextView nameTextView = (TextView)findViewById(R.id.activity_edit_contact_name_textview);
		nameTextView.setText(contactToEdit.getName());
		
		numberEditText = (EditText)findViewById(R.id.activity_edit_contact_number_edittext);
		numberEditText.setText(
				Integer.toString(!isNewContact ? contactToEdit.getReminderFrequency() : DEFAULT_FREQUENCY));
		
		setupSpinner();
		
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
	
	private void setupSpinner()  {
		//TODO: Fast way of doing this, but the enum strings are not localizable. Fix later.
		ArrayAdapter<TimeUnit> timeUnitAdapter = new ArrayAdapter<TimeUnit>(this, 
				android.R.layout.simple_spinner_item, SPINNER_ITEMS);
		timeUnitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitsSpinner = (Spinner)findViewById(R.id.activity_edit_contact_units_spinner);
		unitsSpinner.setAdapter(timeUnitAdapter);
		if(!isNewContact)  {
			int pos = timeUnitAdapter.getPosition(contactToEdit.getReminderFrequencyUnit());
			unitsSpinner.setSelection(pos);
		}
		else  {
			unitsSpinner.setSelection(DEFAULT_UNIT);
		}
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
		TimeUnit units = (TimeUnit)unitsSpinner.getSelectedItem();
		//TODO: This isn't correct. Need to adjust so that we don't always add from current date/time.
		DateTime nextReminderDate = calculateNextReminderDate(DateTime.now(), frequency, units);
		Set<ContactType> contactTypes = getSelectedContactTypes();
		
		contactToEdit.setReminderFrequency(frequency);
		contactToEdit.setReminderFrequencyUnit(units);
		contactToEdit.setNextReminderDate(nextReminderDate);
		contactToEdit.setContactTypes(contactTypes);
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
