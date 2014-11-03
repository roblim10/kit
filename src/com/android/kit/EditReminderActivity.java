package com.android.kit;


import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.android.kit.contacttypes.ContactTypeRegistry;
import com.android.kit.contacttypes.ContactType;
import com.android.kit.model.Reminder;
import com.android.kit.model.TimeUnit;
import com.android.kit.util.LoadContactImageTask;
import com.android.kit.view.HyperlinkView;
import com.android.kit.view.HyperlinkView.ClickableAction;

public class EditReminderActivity extends Activity {
	
	//Input to launch this activity
	public final static String EXTRA_CONTACT_ID = "com.android.kit.EXTRA_CONTACT_ID";
	public final static String EXTRA_CONTACT_NAME = "com.android.kit.EXTRA_CONTACT_NAME";
	public final static String EXTRA_FREQUENCY = "com.android.kit.EXTRA_FREQUENCY";
	public final static String EXTRA_TIME_UNIT = "com.android.kit.TIME_UNIT";
	public final static String EXTRA_START_REMINDER = "com.android.kit.EXTRA_START_REMINDER";
	public final static String EXTRA_NEXT_REMINDER = "com.android.kit.EXTRA_NEXT_REMINDER";
	public final static String EXTRA_CONTACT_TYPES = "com.android.kit.EXTRA_CONTACT_TYPES";
	
	//Output for this activity
	public final static String EXTRA_REMINDER_TO_RETURN = "com.android.kit.EXTRA_REMINDER_TO_RETURN";
	
	private final static int MIN_NUMBER_PICKER_VALUE = 1;
	private final static int MAX_NUMBER_PICKER_VALUE = 100;
	
	private final static int DEFAULT_FREQUENCY = 1;
	private final static int DEFAULT_UNIT = TimeUnit.WEEKS.getId();
	private final static int DEFAULT_REMINDER_HOUR = 18;
	private final static long DEFAULT_REMINDER_DATE = DateTime.now()
			.plusWeeks(DEFAULT_FREQUENCY)
			.withTime(DEFAULT_REMINDER_HOUR, 0, 0, 0)
			.getMillis();

	private ContactTypeRegistry contactTypeRegistry;
	
	private int reminderContactId;
	private String reminderContactName;
	private DateTime startReminder;
	private DateTime nextReminder;
	
	private ImageView contactImageView;
	private TextView nameTextView;
	private NumberPicker numberPicker;
	private NumberPicker unitPicker;
	private HyperlinkView reminderDateHyperlinkView;
	private HyperlinkView reminderTimeHyperlinkView;
	private CheckBoxListAdapter<ContactType> contactTypeListAdapter;
	private ListView contactTypeListView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		KitApplication app = (KitApplication)getApplication();
		contactTypeRegistry = app.getContactTypeRegistry();
		
		setContentView(R.layout.activity_edit_reminder);
		
		contactImageView = (ImageView)findViewById(R.id.activity_edit_reminder_imageview);
		nameTextView = (TextView)findViewById(R.id.activity_edit_reminder_name_textview);
		numberPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_number_picker);
		unitPicker = (NumberPicker)findViewById(R.id.activity_edit_reminder_unit_picker);
		reminderDateHyperlinkView = (HyperlinkView)findViewById(R.id.activity_edit_reminder_reminder_date_hyperlinkview);
		reminderTimeHyperlinkView = (HyperlinkView)findViewById(R.id.activity_edit_reminder_reminder_time_hyperlinkview);
		
		setupNumberPicker();
		setupUnitPicker();
		setupReminderTextViews();
		setupContactTypeListView();
		
		Intent fromIntent = getIntent();
		reminderContactId = fromIntent.getIntExtra(EXTRA_CONTACT_ID, -1);
		reminderContactName = fromIntent.getStringExtra(EXTRA_CONTACT_NAME);
		int frequency = fromIntent.getIntExtra(EXTRA_FREQUENCY, DEFAULT_FREQUENCY);
		TimeUnit unit = TimeUnit.getTimeUnitFromId(fromIntent.getIntExtra(EXTRA_TIME_UNIT, DEFAULT_UNIT));
		startReminder = new DateTime(fromIntent.getLongExtra(EXTRA_START_REMINDER, DateTime.now().getMillis()));
		nextReminder = new DateTime(fromIntent.getLongExtra(EXTRA_NEXT_REMINDER, DEFAULT_REMINDER_DATE));
		int contactTypeFlags = fromIntent.getIntExtra(EXTRA_CONTACT_TYPES, contactTypeRegistry.getDefaultFlag());
		
		populateViews(reminderContactId, reminderContactName, frequency, unit, nextReminder, contactTypeFlags);
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
		reminderDateHyperlinkView.setClickableAction(new ClickableAction()  {
			@Override
			public void onClick(View widget) {
				OnDateSetListener listener = createReminderDateSetListener();
				//Convert to calendar because DatePickerFragment works with Calendars
				Calendar calendar = nextReminder.toCalendar(Locale.getDefault());
				int year = calendar.get(Calendar.YEAR);
				int month = calendar.get(Calendar.MONTH);
				int day = calendar.get(Calendar.DAY_OF_MONTH);
				DatePickerFragment datePicker = new DatePickerFragment(listener, year, month, day);
				datePicker.show(getFragmentManager(), "reminderDatePicker");
			}
		});
		
		reminderTimeHyperlinkView.setClickableAction(new ClickableAction()  {
			@Override
			public void onClick(View widget) {
				OnTimeSetListener listener = createReminderTimeSetListener();
				//Convert to calendar because TimePickerFragment works with Calendars
				Calendar calendar = nextReminder.toCalendar(Locale.getDefault());
				int hour = calendar.get(Calendar.HOUR_OF_DAY);
				int minute = calendar.get(Calendar.MINUTE);
				TimePickerFragment timePicker = new TimePickerFragment(listener, hour, minute);
				timePicker.show(getFragmentManager(), "reminderTimePicker");				
			}
			
		});
	}
	
	private void setupContactTypeListView()  {
		
		contactTypeListAdapter = new CheckBoxListAdapter<ContactType>(this, contactTypeRegistry.getTypes());
		contactTypeListView = (ListView)findViewById(R.id.activity_edit_reminder_contact_type_listview);
		contactTypeListView.setAdapter(contactTypeListAdapter);
	}
	
	private void populateViews(int id, String name, int frequency, TimeUnit units, 
			DateTime nextReminder, int contactTypeFlags)  {
		LoadContactImageTask contactImageTask = 
				new LoadContactImageTask(this, contactImageView, id);
		contactImageTask.execute();
		
		nameTextView.setText(name);
		numberPicker.setValue(frequency);
		unitPicker.setValue(units.getId());
		refreshReminderDateTextView(nextReminder);
		refreshReminderTimeTextView(nextReminder);
		for (int i = 0; i < contactTypeListAdapter.getCount(); i++)  {
			ContactType contactType = contactTypeListAdapter.getItem(i);
			boolean isChecked = (contactType.getFlag() & contactTypeFlags) != 0;
			contactTypeListAdapter.setSelected(i, isChecked);
		}
	}
	
	private void refreshReminderDateTextView(DateTime reminderDate)  {
		DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(this);
		String reminderDateText = dateFormat.format(reminderDate.toDate());
		reminderDateHyperlinkView.setClickableText(reminderDateText);
	}
	
	private void refreshReminderTimeTextView(DateTime reminderDate)  {
		DateFormat timeFormat = android.text.format.DateFormat.getTimeFormat(this);
		String reminderTimeText = timeFormat.format(reminderDate.toDate());
		reminderTimeHyperlinkView.setClickableText(reminderTimeText);
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
				Reminder newReminder = saveContact();
				Intent intent = new Intent();
				intent.putExtra(EXTRA_REMINDER_TO_RETURN, newReminder);
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
	
	private Reminder saveContact()  {
		int frequency = numberPicker.getValue();
		TimeUnit units = TimeUnit.getTimeUnitFromId(unitPicker.getValue());
		int contactTypeFlags = 0;
		for (ContactType contactType : contactTypeListAdapter.getSelectedItems())  {
			contactTypeFlags |= contactType.getFlag();
		}
		Reminder newReminder = new Reminder(reminderContactId,
				reminderContactName,
				frequency,
				units,
				startReminder,
				nextReminder,
				contactTypeFlags);
		return newReminder;
	}
	
	private OnDateSetListener createReminderDateSetListener()  {
		return new OnDateSetListener()  {
			@Override
			public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
				//DateTimeFragment deals with Calendar, so we need to convert to DateTime
				//but preserve the time of nextReminder
				Calendar calendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);
				nextReminder = new DateTime(calendar.getTimeInMillis())
					.withTime(nextReminder.getHourOfDay(), nextReminder.getMinuteOfHour(), 
							nextReminder.getSecondOfMinute(), nextReminder.getMillisOfSecond());
				refreshReminderDateTextView(nextReminder);
			}
		};
	}
	
	private OnTimeSetListener createReminderTimeSetListener()  {
		return new OnTimeSetListener()  {
			@Override
			public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
				nextReminder = nextReminder.withTime(hourOfDay, minute, 0, 0);
				refreshReminderTimeTextView(nextReminder);
			}
		};
	}
}
