package com.android.kit;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;

import com.android.kit.model.CheckBoxListItemModel;

public class CheckBoxListAdapter<T> extends ArrayAdapter<CheckBoxListItemModel<T>>  {
	
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
			convertView.setTag(holder);
		}
		else  {
			holder = (ViewHolder)convertView.getTag();
		}
		CheckBoxListItemModel<T> item = getItem(position);
		holder.checkbox.setText(item.getDisplayString());
		holder.checkbox.setChecked(item.isChecked());
		return convertView;
	}
	
	private static class ViewHolder  {
		public CheckBox checkbox;
	}
}
