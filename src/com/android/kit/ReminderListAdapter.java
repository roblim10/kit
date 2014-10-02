package com.android.kit;

import java.text.DateFormat;
import java.util.List;

import org.joda.time.DateTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.kit.model.Reminder;
import com.android.kit.util.LoadContactImageTask;

//Use ArrayAdapter over CursorAdapter since there will most likely be very few items in ReminderDatabase.
//Working with POJOs (Reminder) is much more readable...
public class ReminderListAdapter extends SelectableListAdapter<Reminder>  {

	public ReminderListAdapter(Context context, List<Reminder> reminderList)  {
		super(context, R.layout.reminder_list_item, reminderList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		ViewHolder viewHolder;
		if (convertView == null)  {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.reminder_list_item, parent, false);
			
			ImageView imageView = (ImageView)convertView.findViewById(R.id.reminder_list_item_profile_imageview);
			viewHolder.imageView = imageView;
			
			TextView nameTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_name_textview);
			viewHolder.nameTextView = nameTextView;
			
			TextView subTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_subtitle_textview);
			viewHolder.subTextView = subTextView;
			
			TextView reminderTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_date_textview);
			viewHolder.reminderTextView = reminderTextView;
			
			convertView.setTag(viewHolder);
		}
		else  {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Context context = getContext();
		Reminder currentReminder = getItem(position);
		
		//Load contact image asynchronously
		viewHolder.imageView.setImageResource(R.drawable.no_photo);
		LoadContactImageTask contactImageTask = new LoadContactImageTask(
				viewHolder.imageView, currentReminder.getContactId());
		contactImageTask.execute();
		
		viewHolder.nameTextView.setText(currentReminder.getName());
		String reminderFrequencyText = context.getString(R.string.reminder_list_item_reminder_frequency,
				currentReminder.getFrequency(), 
				currentReminder.getFrequencyUnit().toString());
		viewHolder.subTextView.setText(reminderFrequencyText);
		
		String reminderText = context.getString(R.string.reminder_list_item_next_reminder,
				getDateAsString(currentReminder.getNextReminderDate()));
		viewHolder.reminderTextView.setText(reminderText);
		
		int backgroundRes = isSelected(position) ? android.R.color.holo_green_light : android.R.color.transparent;
		convertView.setBackground(context.getResources().getDrawable(backgroundRes));
		return convertView;
	}
	
	private String getDateAsString(DateTime date)  {
		DateFormat dateFormat = android.text.format.DateFormat.getMediumDateFormat(getContext());
		return dateFormat.format(date.toDate());
	}
	
	//Recommended pattern for managing UI components in custom ListAdapters.  This caches the view
	//so that every call to getView() doesn't require several findViewById() call.
	private static class ViewHolder  {
		public ImageView imageView;
		public TextView nameTextView;
		public TextView subTextView;
		public TextView reminderTextView;
	}
}