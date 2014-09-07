package com.android.kit;

import java.util.List;

import com.android.kit.model.KitContact;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class KitContactListAdapter extends ArrayAdapter<KitContact>  {
	
	public KitContactListAdapter(Context context, List<KitContact> contactList)  {
		super(context, R.layout.contact_list_item, contactList);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		ViewHolder viewHolder;
		if (convertView == null)  {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.contact_list_item, parent, false);
			
			TextView nameTextView = (TextView)convertView.findViewById(R.id.contact_list_item_name_textview);
			viewHolder.nameTextView = nameTextView;
			
			TextView subTextView = (TextView)convertView.findViewById(R.id.contact_list_item_subtitle_textview);
			viewHolder.subTextView = subTextView;
			
			convertView.setTag(viewHolder);
		}
		else  {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		KitContact currentContact = getItem(position);
		viewHolder.nameTextView.setText(currentContact.getName());
		viewHolder.subTextView.setText("This is a test");
		
		return convertView;
	}
	
	//Recommended pattern for managing UI components in custom ListAdapters.  This caches the view
	//so that every call to getView() doesn't require several findViewById() call.
	private static class ViewHolder  {
		public TextView nameTextView;
		public TextView subTextView;
	}
}