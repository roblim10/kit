package com.android.kit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

import com.android.kit.model.CheckBoxListItemModel;
import com.google.common.collect.Lists;

public class CheckBoxListAdapter<T> extends ArrayAdapter<CheckBoxListItemModel<T>>  {
	private OnCheckedChangeListener checkedListener = new OnCheckedChangeListener()  {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Integer position = (Integer)buttonView.getTag();
			getItem(position).setChecked(isChecked);
		}
	};
	
	public CheckBoxListAdapter(Context context, List<CheckBoxListItemModel<T>> options)  {
		super(context, R.layout.checkbox_list_item, options);
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent)  {
		ViewHolder holder = null;
		if (convertView == null)  {
			LayoutInflater inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.checkbox_list_item, parent, false);
			holder = new ViewHolder();
			holder.checkbox = (CheckBox)convertView.findViewById(R.id.checkbox_list_item_checkbox);
			holder.checkbox.setOnCheckedChangeListener(checkedListener);
			convertView.setTag(holder);
		}
		else  {
			holder = (ViewHolder)convertView.getTag();
		}
		
		//Set tag here for checkedListener
		holder.checkbox.setTag(position);
		
		CheckBoxListItemModel<T> item = getItem(position);
		holder.checkbox.setText(item.getDisplayString());
		holder.checkbox.setChecked(item.isChecked());
		return convertView;
	}
	
	public List<T> getSelectedItems()  {
		List<T> selectedItems = Lists.newArrayList();
		for (int i = 0; i < getCount(); i++)  {
			CheckBoxListItemModel<T> item = getItem(i);
			if(item.isChecked())  {
				selectedItems.add(item.getData());
			}
		}
		return selectedItems;
	}
	
	private static class ViewHolder  {
		public CheckBox checkbox;
	}
}
