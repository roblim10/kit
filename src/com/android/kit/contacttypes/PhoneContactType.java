package com.android.kit.contacttypes;

import java.util.Set;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;

import com.android.kit.R;
import com.android.kit.model.Reminder;
import com.android.kit.sqlite.ReminderDatabase;
import com.google.common.collect.Sets;

public class PhoneContactType implements IContactType  {

	private final static IntentFilter filter = new IntentFilter();
	
	static  {
		filter.addAction(TelephonyManager.ACTION_PHONE_STATE_CHANGED);
		filter.addAction(Intent.ACTION_NEW_OUTGOING_CALL);
	}
	
	private Context context;
	private int flag;
	private boolean isDefaultSelected;
	private BroadcastReceiver receiver;
	
	public PhoneContactType(Context context, int flag, boolean isDefaultSelected)  {
		this.context = context;
		this.flag = flag;
		this.isDefaultSelected = isDefaultSelected;
		receiver = new PhoneCallReceiver();
	}
	
	@Override
	public int getFlag()  {
		return flag;
	}
	
	@Override
	public boolean isDefaultSelected()  {
		return isDefaultSelected;
	}

	@Override
	public BroadcastReceiver getReceiver() {
		return receiver;
	}

	@Override
	public IntentFilter getIntentFilter() {
		return filter;
	}
	
	@Override
	public String toString() {
		return context.getString(R.string.contact_type_phone_call);
	}
	
	public static class PhoneCallReceiver extends BroadcastReceiver  {
		@Override
		public void onReceive(Context context, Intent intent) {
			//For outgoing calls
			if (Intent.ACTION_NEW_OUTGOING_CALL.equals(intent.getAction()))  {
				String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
				Set<Integer> contactIds = getContactId(context, phoneNum);
				for (int id : contactIds)  {
					Reminder reminder = ReminderDatabase.getInstance(context).readReminder(id);
					if (reminder != null)  {
						//TODO: Ugly.  Please fix.
						reminder = reminder.withNextReminderDate(Reminder.calculateNextReminderDate(reminder));
						ReminderDatabase.getInstance(context).update(reminder);
					}
				}

			}
		}
		
		private Set<Integer> getContactId(Context context, String phoneNumber)  {
			ContentResolver cr = context.getContentResolver();
			Uri uri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(phoneNumber));
			String[] projection = {ContactsContract.PhoneLookup._ID};
			Cursor cursor = cr.query(uri, projection, null, null, null);
			Set<Integer> contactIds = Sets.newHashSet();
			while (cursor.moveToNext())  {
				contactIds.add(cursor.getInt(0));
			}
			return contactIds;
		}
	}
}
