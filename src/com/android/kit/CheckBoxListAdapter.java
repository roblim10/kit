package com.android.kit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class CheckBoxListAdapter<T> extends SelectableListAdapter<T>  {
	private OnCheckedChangeListener checkedListener = new OnCheckedChangeListener()  {
		@Override
		public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
			Integer position = (Integer)buttonView.getTag();
			setSelected(position, isChecked);
		}
	};
	
	public CheckBoxListAdapter(Context context, T[] options)  {
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
		
		T item = getItem(position);
		holder.checkbox.setText(item.toString());
		holder.checkbox.setChecked(isSelected(position));
		return convertView;
	}
	
	private static class ViewHolder  {
		public CheckBox checkbox;
	}
}
