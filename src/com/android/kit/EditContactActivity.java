package com.android.kit;


import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
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
	public final static String EXTRA_EDITED_CONTACT = "CONTACT_EDITED_CONTACT";
	
	private final static TimeUnit[] SPINNER_ITEMS = {
		TimeUnit.DAYS,
		TimeUnit.WEEKS,
		TimeUnit.MONTHS,
		TimeUnit.YEARS
	};
	
	private final static List<CheckBoxListItemModel<ContactType>> TYPE_ITEMS = Lists.newArrayList(
			new CheckBoxListItemModel<ContactType>(ContactType.PHONE_CALL, ContactType.PHONE_CALL.toString()),
			new CheckBoxListItemModel<ContactType>(ContactType.SMS, ContactType.SMS.toString())
		);
		
	private KitContact contactToEdit;
	
	private TextView nameTextView;
	private EditText numberEditText;
	private ArrayAdapter<TimeUnit> spinnerAdapter;
	private Spinner unitsSpinner;
	private CheckBoxListAdapter<ContactType> contactTypeListAdapter;
	private ListView contactTypeListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_contact);
		
		Intent fromIntent = getIntent();
		contactToEdit = (KitContact)fromIntent.getParcelableExtra(ContactListActivity.KIT_CONTACT_TO_EDIT);
		nameTextView = (TextView)findViewById(R.id.activity_edit_contact_name_textview);
		numberEditText = (EditText)findViewById(R.id.activity_edit_contact_number_edittext);
		setupSpinner();
		setupContactTypeListView();
		
		populateViews();
	}
	
	private void setupContactTypeListView()  {
		List<CheckBoxListItemModel<ContactType>> options = Lists.newArrayList();
		for (CheckBoxListItemModel<ContactType> item : TYPE_ITEMS)  {
			options.add(item);	
		}
		contactTypeListAdapter = new CheckBoxListAdapter<ContactType>(this, options);
		contactTypeListView = (ListView)findViewById(R.id.activity_edit_contact_contact_type_listview);
		contactTypeListView.setAdapter(contactTypeListAdapter);
	}
	
	private void setupSpinner()  {
		//TODO: Fast way of doing this, but the enum strings are not localizable. Fix later.
		spinnerAdapter = new ArrayAdapter<TimeUnit>(this, 
				android.R.layout.simple_spinner_item, SPINNER_ITEMS);
		spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		unitsSpinner = (Spinner)findViewById(R.id.activity_edit_contact_units_spinner);
		unitsSpinner.setAdapter(spinnerAdapter);
	}
	
	private void populateViews()  {
		String name = contactToEdit.getName();
		int frequency = contactToEdit.getReminderFrequency();
		TimeUnit units = contactToEdit.getReminderFrequencyUnit();
		Set<ContactType> checkedTypes = contactToEdit.getContactTypes();
		
		nameTextView.setText(name);
		numberEditText.setText(Integer.toString(frequency));
		unitsSpinner.setSelection(spinnerAdapter.getPosition(units));
		for (int i = 0; i < contactTypeListAdapter.getCount(); i++)  {
			CheckBoxListItemModel<ContactType> model = contactTypeListAdapter.getItem(i);
			boolean isChecked = checkedTypes.contains(model.getData());
			model.setChecked(isChecked);
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
				intent.putExtra(EXTRA_EDITED_CONTACT, contactToEdit);
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
		Set<ContactType> contactTypes = Sets.newHashSet(contactTypeListAdapter.getSelectedItems());
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
}
