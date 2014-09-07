package com.android.kit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;


public class KitContactListAdapter extends ArrayAdapter<KitContact>  {
	private final static int CONTACT_LIST_ITEM_RES_ID = 0;
	
	private Context context;
	private List<KitContact> contactList;
	
	public KitContactListAdapter(Context context, List<KitContact> contactList)  {
		super(context, CONTACT_LIST_ITEM_RES_ID, contactList);
		this.context = context;
		this.contactList = contactList;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		ViewHolder viewHolder;
		if (convertView == null)  {
			viewHolder = new ViewHolder();
			LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.contact_list_item, parent, false);
			
			TextView nameTextView = (TextView)convertView.findViewById(R.id.contact_list_item_name_textview);
			viewHolder.setNameTextView(nameTextView);
			
			TextView subTextView = (TextView)convertView.findViewById(R.id.contact_list_item_subtitle_textview);
			viewHolder.setSubTextView(subTextView);
			
			convertView.setTag(viewHolder);
		}
		else  {
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		viewHolder.getNameTextView().setText(contactList.get(position).getName());
		viewHolder.getSubTextView().setText("This is a test");
		
		return convertView;
	}
	
	//Recommended pattern for managing UI components in custom ListAdapters.  This caches the view
	//so that every call to getView() doesn't require several findViewById() call.
	private static class ViewHolder  {
		private TextView nameTextView;
		private TextView subTextView;
		
		public TextView getNameTextView()  { return nameTextView; }
		public void setNameTextView(TextView textView)  { nameTextView = textView; }
		
		public TextView getSubTextView()  { return subTextView; }
		public void setSubTextView(TextView textView)  { subTextView = textView; }
	}
}