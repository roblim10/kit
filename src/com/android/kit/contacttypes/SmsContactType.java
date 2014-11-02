package com.android.kit.contacttypes;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;

import com.android.kit.R;

public class SmsContactType extends ContactType {
	//TODO: Can be replaced with Telephony.Sms if on API level 19 or higher
	private final static String SMS_CONTENT_URI = "content://sms";
	private final static String SMS_PROTOCOL = "protocol";
	private final static String SMS_TYPE = "type";
	private final static String SMS_ADDRESS = "address";
	private final static int SMS_TYPE_SENT = 2;
	
	private int flag;
	private boolean isDefaultSelected;
	
	public SmsContactType(Context context, int flag, boolean isDefaultSelected)  {
		super(context);
		this.flag = flag;
		this.isDefaultSelected = isDefaultSelected;
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
	public String toString() {
		return context.getString(R.string.contact_type_text_message);
	}
	
	@Override
	public void register()  {
		context.getContentResolver().registerContentObserver(Uri.parse(SMS_CONTENT_URI), 
				true, new SentSmsObserver(new Handler()));
	}
	
	private class SentSmsObserver extends ContentObserver  {
		public SentSmsObserver(Handler handler) {
			super(handler);
		}
		
		@Override
		public void onChange(boolean selfChange)  {
			Cursor cursor = context.getContentResolver().query(Uri.parse(SMS_CONTENT_URI),
					new String[] {SMS_PROTOCOL, SMS_TYPE, SMS_ADDRESS}, null, null, null);
			if (cursor.moveToNext()) {
				String protocol = cursor.getString(0);
				int type = cursor.getInt(1);
				// Only processing outgoing sms event that were sent successfully
				if (protocol == null && type == SMS_TYPE_SENT) {
					String to = cursor.getString(2);
					tryResetReminder(to);
				}
			}
		}
	}
}
