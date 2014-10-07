package com.android.kit.model;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

public class Reminder implements Parcelable {
	private int id;
	private String name;
	private int reminderFrequency;
	private TimeUnit reminderFrequencyUnit;
	private DateTime nextReminderDate;
	private int contactTypeFlags;

	public Reminder(int id, String name, int reminderFrequency, TimeUnit unit, DateTime nextReminderDate, int contactTypeFlags)  {
		this.id = id;
		this.name = name;
		this.reminderFrequency = reminderFrequency;
		this.reminderFrequencyUnit = unit;
		this.nextReminderDate = nextReminderDate;
		this.contactTypeFlags = contactTypeFlags;
	}
	
	public Reminder(Parcel in)  {
		this.id = in.readInt();
		this.name = in.readString();
		this.reminderFrequency = in.readInt();
		this.reminderFrequencyUnit = TimeUnit.getTimeUnitFromId(in.readInt());
		this.nextReminderDate = new DateTime(in.readLong());
		this.contactTypeFlags = in.readInt();
	}
	
	public int getContactId()  {
		return id;
	}
	
	public String getName()  {
		return name;
	}
	
	public int getFrequency()  {
		return reminderFrequency;
	}
	
	public TimeUnit getFrequencyUnit()  {
		return reminderFrequencyUnit;
	}
	
	public DateTime getNextReminderDate()  {
		return nextReminderDate;
	}
	
	public int getContactTypeFlags()  {
		return contactTypeFlags;
	}
	
	public Reminder withFrequency(int newFrequency)  {
		return new Reminder(id, name, newFrequency, reminderFrequencyUnit, nextReminderDate, contactTypeFlags);
	}
	
	public Reminder withTimeUnit(TimeUnit newUnit)  {
		return new Reminder(id, name, reminderFrequency, newUnit, nextReminderDate, contactTypeFlags);	
	}

	public Reminder withNextReminderDate(DateTime newDate)  {
		return new Reminder(id, name, reminderFrequency, reminderFrequencyUnit, newDate, contactTypeFlags);
	}
	
	public Reminder withContactTypeFlags(int newFlags)  {
		return new Reminder(id, name, reminderFrequency, reminderFrequencyUnit, nextReminderDate, newFlags);
	}
	
	@Override
	public String toString()  {
		return String.format("{id:%d, name:%s, reminderFrequency:%d, reminderFrequencyUnit:%s, " +
				"nextReminderDate:%s, contactTypeFlags:%d}", 
				id, name, reminderFrequency, 
				reminderFrequencyUnit != null ? reminderFrequencyUnit.toString() : "null", 
				nextReminderDate != null ? nextReminderDate.toString() : "null",
				contactTypeFlags);
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(reminderFrequency);
		dest.writeInt(reminderFrequencyUnit.getId());
		dest.writeLong(nextReminderDate.getMillis());
		dest.writeInt(contactTypeFlags);
	}
	
	public static final Parcelable.Creator<Reminder> CREATOR = new Parcelable.Creator<Reminder>()  {
		@Override
		public Reminder createFromParcel(Parcel source) {
			return new Reminder(source);
		}

		@Override
		public Reminder[] newArray(int size) {
			return new Reminder[size];
		} 
	};
	
	public static DateTime calculateNextReminderDate(Reminder reminder)  {
		int frequency = reminder.getFrequency();
		TimeUnit unit = reminder.getFrequencyUnit();
		DateTime newReminderDate = reminder.getNextReminderDate();
		do  {
			switch (unit)  {
				case DAYS: newReminderDate = newReminderDate.plusDays(frequency); break;
				case WEEKS: newReminderDate = newReminderDate.plusWeeks(frequency); break;
				case MONTHS: newReminderDate = newReminderDate.plusMonths(frequency); break;
				case YEARS: newReminderDate = newReminderDate.plusYears(frequency); break;
				default:
					throw new UnsupportedOperationException("Unknown TimeUnit: " + unit);
			}
		} while (newReminderDate.isBeforeNow());
		return newReminderDate;
//		return DateTime.now().plusSeconds(20);
	}
}
