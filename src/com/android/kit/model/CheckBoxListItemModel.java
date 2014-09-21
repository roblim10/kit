package com.android.kit.model;

public class CheckBoxListItemModel<T> {
	private T data;
	private String displayString;
	private boolean isChecked;
	
	public CheckBoxListItemModel(T data, String displayString)  {
		this(data, displayString, false);
	}
	
	public CheckBoxListItemModel(T data, String displayString, boolean isDefaultChecked)  {
		this.data = data;
		this.displayString = displayString;
		this.isChecked = isDefaultChecked;
	}
	
	public T getData()  {
		return data;
	}
	
	public String getDisplayString()  {
		return displayString;
	}
	
	public boolean isChecked()  {
		return isChecked;
	}
	
	public void setChecked(boolean isChecked)  {
		this.isChecked = isChecked;
	}
}
