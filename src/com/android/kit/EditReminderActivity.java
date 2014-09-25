package com.android.kit;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import org.joda.time.DateTime;

import android.app.Activity;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.kit.model.CheckBoxListItemModel;
import com.android.kit.model.ContactType;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.android.kit.view.HyperlinkView;
import com.android.kit.view.HyperlinkView.ClickableAction;
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
	
	//Model for reminder TextViews.  We use Calendar because DatePicker/TimePicker index using Calendar.
	private Calendar nextReminder;
	
	private TextView nameTextView;
	private NumberPicker numberPicker;
	private NumberPicker unitPicker;
	private HyperlinkView reminderDateTextView;
	private HyperlinkView reminderTimeTextView;
	private CheckBoxListAdapter<ContactType> contactTypeListAdapter;
	private ListView contactTypeListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_edit_reminder);
		
		Intent fromIntent = getIntent();
		reminderToEdit = (Reminder)fromIntent.getParcelableExtra(ReminderListActivity.REMINDER_TO_EDIT);
		nextReminder = reminderToEdit.getNextReminderDate().toCalendar(Locale.getDefault()); 
				
		nameTextView = (TextView)findViewById(R.id.activity_edit_reminder_name_textview);
		numberPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_number_picker);
		unitPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_unit_picker);
		reminderDateTextView = (HyperlinkView)findViewById(R.id.activity_edit_reminder_reminder_date_hyperlinkview);
		reminderTimeTextView = (HyperlinkView)findViewById(R.id.activity_edit_reminder_reminder_time_hyperlinkview);
		
		setupNumberPicker();
		setupUnitPicker();
		setupReminderTextViews();
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
	
	private void setupReminderTextViews()  {
		reminderDateTextView.setClickableAction(new ClickableAction()  {
			@Override
			public void onClick(View widget) {
				OnDateSetListener listener = createReminderDateSetListener();
				int year = nextReminder.get(Calendar.YEAR);
				int month = nextReminder.get(Calendar.MONTH);
				int day = nextReminder.get(Calendar.DAY_OF_MONTH);
				DatePickerFragment datePicker = new DatePickerFragment(listener, year, month, day);
				datePicker.show(getFragmentManager(), "reminderDatePicker");
			}
		});
		
		reminderTimeTextView.setClickableAction(new ClickableAction()  {

			@Override
			public void onClick(View widget) {
				OnTimeSetListener listener = createReminderTimeSetListener();
				int hour = nextReminder.get(Calendar.HOUR_OF_DAY);
				int minute = nextReminder.get(Calendar.MINUTE);
				TimePickerFragment timePicker = new TimePickerFragment(listener, hour, minute);
				timePicker.show(getFragmentManager(), "reminderTimePicker");				
			}
			
		});
//		reminderDateTextView.setMovementMethod(LinkMovementMethod.getInstance());
//		reminderDateTextView.setOnClickListener(new OnClickListener()  {
//			@Override
//			public void onClick(View v) {
//				OnDateSetListener listener = createReminderDateSetListener();
//				int year = nextReminder.get(Calendar.YEAR);
//				int month = nextReminder.get(Calendar.MONTH);
//				int day = nextReminder.get(Calendar.DAY_OF_MONTH);
//				DatePickerFragment datePicker = new DatePickerFragment(listener, year, month, day);
//				datePicker.show(getFragmentManager(), "reminderDatePicker");
//			}
//		});
//		
//		reminderTimeTextView.setOnClickListener(new OnClickListener()  {
//			@Override
//			public void onClick(View v) {
//				OnTimeSetListener listener = createReminderTimeSetListener();
//				int hour = nextReminder.get(Calendar.HOUR_OF_DAY);
//				int minute = nextReminder.get(Calendar.MINUTE);
//				TimePickerFragment timePicker = new TimePickerFragment(listener, hour, minute);
//				timePicker.show(getFragmentManager(), "reminderTimePicker");
//			}
//		});
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
		refreshReminderDateTextView();
		refreshReminderTimeTextView();
		for (int i = 0; i < contactTypeListAdapter.getCount(); i++)  {
			CheckBoxListItemModel<ContactType> model = contactTypeListAdapter.getItem(i);
			boolean isChecked = checkedTypes.contains(model.getData());
			model.setChecked(isChecked);
		}
	}
	
	private void refreshReminderDateTextView()  {
		DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
		String reminderDateText = dateFormat.format(nextReminder.getTime());
		reminderDateTextView.setClickableText(reminderDateText);
		
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		String reminderTimeText = timeFormat.format(nextReminder.getTime());
		reminderTimeTextView.setClickableText(reminderTimeText);
	}
	
	private void refreshReminderTimeTextView()  {
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		String reminderTimeText = timeFormat.format(nextReminder.getTime());
		reminderTimeTextView.setClickableText(reminderTimeText);
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
		Set<ContactType> contactTypes = Sets.newHashSet(contactTypeListAdapter.getSelectedItems());
		reminderToEdit.setFrequency(frequency);
		reminderToEdit.setFrequencyUnit(units);
		reminderToEdit.setNextReminderDate(new DateTime(nextReminder.getTimeInMillis()));
		reminderToEdit.setContactTypes(contactTypes);
	}
	
	private OnDateSetListener createReminderDateSetListener()  {
		return new OnDateSetListener()  {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				//Convert to calendar first since DatePickers index according to Calendar, not DateTime
				nextReminder = new GregorianCalendar(
						year, 
						monthOfYear, 
						dayOfMonth, 
						nextReminder.get(Calendar.HOUR_OF_DAY), 
						nextReminder.get(Calendar.MINUTE));
				refreshReminderDateTextView();
			}
		};
	}
	
	private OnTimeSetListener createReminderTimeSetListener()  {
		return new OnTimeSetListener()  {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				nextReminder = new GregorianCalendar(
						nextReminder.get(Calendar.YEAR), 
						nextReminder.get(Calendar.MONTH), 
						nextReminder.get(Calendar.DAY_OF_MONTH),
						hourOfDay, 
						minute);
				refreshReminderTimeTextView();
			}
		};
	}
}