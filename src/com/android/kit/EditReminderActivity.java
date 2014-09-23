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
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;

import com.android.kit.model.CheckBoxListItemModel;
import com.android.kit.model.ContactType;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

public class EditReminderActivity extends Activity {
	public final static String EXTRA_EDITED_REMINDER = "EDITED_REMINDER";
	
	private final static int MIN_NUMBER_PICKER_VALUE = 1;
	private final static int MAX_NUMBER_PICKER_VALUE = 100;
	
	private final static List<CheckBoxListItemModel<ContactType>> TYPE_ITEMS = Lists.newArrayList(
			new CheckBoxListItemModel<ContactType>(ContactType.PHONE_CALL, ContactType.PHONE_CALL.toString()),
			new CheckBoxListItemModel<ContactType>(ContactType.SMS, ContactType.SMS.toString())
		);
		
	private Reminder reminderToEdit;
	
	private TextView nameTextView;
	private NumberPicker numberPicker;
	private NumberPicker unitPicker;
	private CheckBoxListAdapter<ContactType> contactTypeListAdapter;
	private ListView contactTypeListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_reminder);
		
		Intent fromIntent = getIntent();
		reminderToEdit = (Reminder)fromIntent.getParcelableExtra(ReminderListActivity.REMINDER_TO_EDIT);
		nameTextView = (TextView)findViewById(R.id.activity_edit_reminder_name_textview);
		numberPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_number_picker);
		unitPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_unit_picker);
		
		setupNumberPicker();
		setupUnitPicker();
		setupContactTypeListView();
		
		populateViews();
	}
	
	private void setupNumberPicker()  {
		numberPicker.setMinValue(MIN_NUMBER_PICKER_VALUE);
		numberPicker.setMaxValue(MAX_NUMBER_PICKER_VALUE);
	}
	
	private void setupUnitPicker()  {
		TimeUnit[] timeUnitValues = TimeUnit.class.getEnumConstants();
		String[] timeUnitStrings = new String[timeUnitValues.length];
		for (int i = 0; i < timeUnitValues.length; i++)  {
			timeUnitStrings[i] = timeUnitValues[i].toString();
		}
		unitPicker.setMinValue(0);
		unitPicker.setMaxValue(timeUnitValues.length - 1);
		unitPicker.setDisplayedValues(timeUnitStrings);
	}
	
	private void setupContactTypeListView()  {
		List<CheckBoxListItemModel<ContactType>> options = Lists.newArrayList();
		for (CheckBoxListItemModel<ContactType> item : TYPE_ITEMS)  {
			options.add(item);	
		}
		contactTypeListAdapter = new CheckBoxListAdapter<ContactType>(this, options);
		contactTypeListView = (ListView)findViewById(R.id.activity_edit_reminder_contact_type_listview);
		contactTypeListView.setAdapter(contactTypeListAdapter);
	}
	
	private void populateViews()  {
		String name = reminderToEdit.getName();
		int frequency = reminderToEdit.getFrequency();
		TimeUnit units = reminderToEdit.getFrequencyUnit();
		Set<ContactType> checkedTypes = reminderToEdit.getContactTypes();
		
		nameTextView.setText(name);
		numberPicker.setValue(frequency);
		unitPicker.setValue(units.getId());
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
				saveContact();
				Intent intent = new Intent();
				intent.putExtra(EXTRA_EDITED_REMINDER, reminderToEdit);
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
		int frequency = numberPicker.getValue();
		TimeUnit units = TimeUnit.getTimeUnitFromId(unitPicker.getValue());
		//TODO: This isn't correct. Need to adjust so that we don't always add from current date/time.
		DateTime nextReminderDate = calculateNextReminderDate(DateTime.now(), frequency, units);
		Set<ContactType> contactTypes = Sets.newHashSet(contactTypeListAdapter.getSelectedItems());
		reminderToEdit.setFrequency(frequency);
		reminderToEdit.setFrequencyUnit(units);
		reminderToEdit.setNextReminderDate(nextReminderDate);
		reminderToEdit.setContactTypes(contactTypes);
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
