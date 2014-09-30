package com.android.kit;

import android.app.ListActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;

import com.android.kit.model.Reminder;
import com.android.kit.service.AlarmService;

public class NotificationHandlerActivity extends ListActivity {

	private Reminder reminder;
	
	@Override
	public void onCreate(Bundle savedInstanceState)  {
		super.onCreate(savedInstanceState);
		reminder = getIntent().getParcelableExtra(AlarmService.EXTRA_REMINDER);
		Log.i("KIT", "NotificationHandlerActivity started with " + reminder);
		setupListView();
	}
	
	private void setupListView()  {
		ListAdapter adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, 
				getResources().getStringArray(R.array.notification_actions));
		setListAdapter(adapter);
	}
	
	@Override
	protected void onListItemClick(ListView l, View v, int position, long id)  {
		ListAdapter adapter = getListAdapter();
		Log.i("KIT", "Hey!  You clicked on " + adapter.getItem(position));
	}
}
