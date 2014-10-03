package com.android.kit;

import java.text.DateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.kit.model.Reminder;
import com.android.kit.util.LoadContactImageTask;
import com.google.common.collect.Maps;

//Use ArrayAdapter over CursorAdapter since there will most likely be very few items in ReminderDatabase.
//Working with POJOs (Reminder) is much more readable...
public class ReminderListAdapter extends SelectableListAdapter<Reminder>  {

	private Map<Integer, Reminder> idReminderMap;
	
	public ReminderListAdapter(Context context, List<Reminder> reminderList)  {
		super(context, R.layout.reminder_list_item, reminderList);
		idReminderMap = Maps.newHashMap();
		for (Reminder reminder : reminderList)  {
			idReminderMap.put(reminder.getContactId(), reminder);
		}
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		ViewHolder viewHolder;
		if (convertView == null)  {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.reminder_list_item, parent, false);
			
			viewHolder.imageView = (ImageView)convertView.findViewById(R.id.reminder_list_item_profile_imageview);
			viewHolder.nameTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_name_textview);
			viewHolder.subTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_subtitle_textview);
			viewHolder.reminderTextView = (TextView)convertView.findViewById(R.id.reminder_list_item_date_textview);
			convertView.setTag(viewHolder);
		}
		else  {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		Context context = getContext();
		Reminder currentReminder = getItem(position);
		
		//Load contact image asynchronously
		LoadContactImageTask contactImageTask = 
				new LoadContactImageTask(context, viewHolder.imageView, currentReminder.getContactId());
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
	
	@Override
	public void add(Reminder reminder)  {
		super.add(reminder);
		idReminderMap.put(reminder.getContactId(), reminder);
	}
	
	@Override
	public void addAll(Collection<? extends Reminder> reminders)  {
		super.addAll(reminders);		
		for (Reminder reminder : reminders)  {
			idReminderMap.put(reminder.getContactId(), reminder);
		}
	}
	
	public void removeByContactId(int contactId)  {
		Reminder reminder = idReminderMap.get(contactId);
		idReminderMap.remove(contactId);
		super.remove(reminder);
	}
	
	
	public void update(Reminder reminder)  {
		Reminder originalReminder = idReminderMap.get(reminder.getContactId());
		remove(originalReminder);
		add(reminder);
	}
	
	@Override
	public void clear()  {
		super.clear();
		idReminderMap.clear();
	}
	
	public Reminder getReminderForContactId(int contactId)  {
		return idReminderMap.get(contactId);
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