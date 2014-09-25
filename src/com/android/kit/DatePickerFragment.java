package com.android.kit;

import org.joda.time.DateTime;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.os.Bundle;

public class DatePickerFragment extends DialogFragment {
	private int day;
	private int month;
	private int year;
	private OnDateSetListener listener;
	
	public DatePickerFragment(OnDateSetListener listener, int year, int month, int day)  {
		this.listener = listener;
		this.year = year;
		this.month = month;
		this.day = day;
	}
	
	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState)  {
		DatePickerDialog dialog = new DatePickerDialog(getActivity(), listener, year, month, day);
		DateTime tomorrow = DateTime.now()
				.plusDays(1)
				.withTime(0, 0, 0, 0);
		dialog.getDatePicker().setMinDate(tomorrow.getMillis());
		return dialog;
	}
}
