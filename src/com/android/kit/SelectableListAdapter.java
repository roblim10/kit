package com.android.kit;

import java.util.List;

import android.content.Context;
import android.util.SparseBooleanArray;
import android.widget.ArrayAdapter;

import com.google.common.collect.Lists;

public class SelectableListAdapter<T> extends ArrayAdapter<T> {
	private SparseBooleanArray selection = new SparseBooleanArray();
	 
	public SelectableListAdapter(Context context, int resource, T[] objects) {
        super(context, resource, objects);
    }
	
    public SelectableListAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    public void setSelected(int position, boolean value) {
    	if (value)  {
    		selection.append(position, value);
    	}
    	else  {
    		selection.delete(position);
    	}
        notifyDataSetChanged();
    }

    public void clearSelection() {
        selection.clear();
        notifyDataSetChanged();
    }

    public boolean isSelected(int position)  {
    	return selection.get(position);
    }
    
    public List<T> getSelectedItems()  {
		List<T> selectedItems = Lists.newArrayList();
		for (int i = 0; i < getCount(); i++)  {
			if(isSelected(i))  {
				T item = getItem(i);
				selectedItems.add(item);
			}
		}
		return selectedItems;
    }
    
    public int getSelectedItemsCount()  {
    	return selection.size();
    }
    
    
}
