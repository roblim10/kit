package com.android.kit;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.TimePickerDialog;
import android.app.TimePickerDialog.OnTimeSetListener;
import android.os.Bundle;
import android.text.format.DateFormat;

public class TimePickerFragment extends DialogFragment {
	private int hour;
	private int minute;
	private OnTimeSetListener listener;
	
	public TimePickerFragment(OnTimeSetListener listener, int hour, int minute)  {
		this.listener = listener;
		this.hour = hour;
		this.minute = minute;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)  {
		TimePickerDialog dialog = new TimePickerDialog(getActivity(), 
				listener, 
				hour,
				minute,
				DateFormat.is24HourFormat(getActivity()));
		return dialog;
	}
}
