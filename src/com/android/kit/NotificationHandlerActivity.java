package com.android.kit;

import java.util.Map;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.kit.model.Reminder;
import com.android.kit.service.AlarmService;
import com.android.kit.service.NotificationRemovedReceiver;
import com.android.kit.service.ReminderNotificationManager;
import com.android.kit.util.LoadContactImageTask;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;

public class NotificationHandlerActivity extends ListActivity {

	private Reminder reminder;
	
	private ImageView profileImageView;
	private TextView nameTextView;
	private TextView instructionsTextView;
	
	@Override
	public void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		
		reminder = getIntent().getParcelableExtra(AlarmService.EXTRA_REMINDER);
		Log.i("KIT", "NotificationHandlerActivity started with " + reminder);
		
		setContentView(R.layout.activity_notification_handler);
		profileImageView = (ImageView)findViewById(R.id.activity_notification_handler_profile_imageview);
		nameTextView = (TextView)findViewById(R.id.activity_notification_handler_name_textview);
		instructionsTextView = (TextView)findViewById(R.id.activity_notification_handler_instructions_textview);
		
		setupListView();
		setupImageView();
		setupTextViews();
	}
	
	private void setupListView()  {
		//TODO: Customize list to see what contact types were checked.
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
				getResources().getStringArray(R.array.notification_actions));
		setListAdapter(adapter);
	}
	
	private void setupImageView()  {
		profileImageView.setImageResource(R.drawable.no_photo);
		new LoadContactImageTask(profileImageView, reminder.getContactId()).execute();
	}
	
	private void setupTextViews()  {
		nameTextView.setText(reminder.getName());
		instructionsTextView.setText(getString(R.string.activity_notification_handler_instructions, reminder.getName()));
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)  {
		switch(position)  {
			case 0:
				tryCallContact();
				break;
			case 1:
				snoozeNotification();
				break;
			case 2:
				dismissNotification();
				finish();
				break;
			default:
				break;
		}
	}
	
	private void tryCallContact()  {
		Map<String, String> phoneTypeMap = loadPhoneNumbersForContact(reminder.getContactId());
		if (phoneTypeMap.isEmpty())  {
			String error = getString(R.string.activity_notification_handler_no_phone_error);
			Toast.makeText(this, error, Toast.LENGTH_SHORT);
		}
		else if (phoneTypeMap.size() == 1)  {
			String firstKey = Iterables.getOnlyElement(phoneTypeMap.keySet());
			makePhoneCall(firstKey);
			dismissNotification();
			finish();
		}
		else  {
			String[] displayChoices = new String[phoneTypeMap.size()];
			final String[] phoneChoices = new String[phoneTypeMap.size()];
			int i = 0;
			for (Map.Entry<String,String> entry : phoneTypeMap.entrySet())  {
				String displayChoice = entry.getValue() + ": " + entry.getKey();
				displayChoices[i] = displayChoice;
				phoneChoices[i] = entry.getKey();
				i++;
			}
			AlertDialog dialog = new AlertDialog.Builder(this)
				.setTitle(R.string.activity_notification_handler_multi_phone_title)
				.setItems(displayChoices, new OnClickListener()  {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						makePhoneCall(phoneChoices[which]);
						dismissNotification();
						finish();
					}
				}).create();
			dialog.show();
		}
	}
	
	private void snoozeNotification()  {
		//TODO: Set new alarm for some time
		//TODO: Update notification to come some time later
	}
	
	private void dismissNotification()  {
		ReminderNotificationManager.getInstance().cancelNotification(this, reminder);
		//cancelNotification() does not trigger deleteIntent, so trigger it with below Intent
		Intent i = new Intent(this, NotificationRemovedReceiver.class);
		i.putExtra(AlarmService.EXTRA_REMINDER, reminder);
		sendBroadcast(i);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)  {
		getMenuInflater().inflate(R.menu.activity_notification_handler_menu, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)  {
		switch (item.getItemId())  {
			case R.id.action_cancel:
				finish();
				return true;
			default: 
				return super.onOptionsItemSelected(item);
		}
	}
	
	private Map<String, String> loadPhoneNumbersForContact(int contactId)  {
		Map<String,String> phoneLabelMap = Maps.newHashMap(); 
		ContentResolver cr = getContentResolver();
		String[] projection = {
			ContactsContract.CommonDataKinds.Phone.NUMBER,
			ContactsContract.CommonDataKinds.Phone.TYPE,
			ContactsContract.CommonDataKinds.Phone.LABEL
		};
		String selection = ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?";
		String[] selectArgs = {Integer.toString(contactId)};
		Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, projection, selection, selectArgs, null);
		while (cursor.moveToNext())  {
			String phone = cursor.getString(0);
			String type = ContactsContract.CommonDataKinds.Phone.getTypeLabel(getResources(), cursor.getInt(1), cursor.getString(2)).toString();
			phoneLabelMap.put(phone, type);
		}
		cursor.close();
		return phoneLabelMap;
	}
	
	private void makePhoneCall(String number)  {
		Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + number));
		startActivity(intent);
	}
}
