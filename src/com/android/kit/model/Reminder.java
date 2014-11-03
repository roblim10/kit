package com.android.kit.model;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

import com.android.kit.contacttypes.ContactType;

public class Reminder implements Parcelable {
	private int id;
	private String name;
	private int reminderFrequency;
	private TimeUnit reminderFrequencyUnit;
	private DateTime startReminderDate;
	private DateTime nextReminderDate;
	private int contactTypeFlags;

	public Reminder(int id, 
			String name, 
			int reminderFrequency, 
			TimeUnit unit, 
			DateTime startReminderDate, 
			DateTime nextReminderDate,
			int contactTypeFlags)  {
		this.id = id;
		this.name = name;
		this.reminderFrequency = reminderFrequency;
		this.reminderFrequencyUnit = unit;
		this.startReminderDate = startReminderDate;
		this.nextReminderDate = nextReminderDate;
		this.contactTypeFlags = contactTypeFlags;
	}
	
	public Reminder(Parcel in)  {
		this.id = in.readInt();
		this.name = in.readString();
		this.reminderFrequency = in.readInt();
		this.reminderFrequencyUnit = TimeUnit.getTimeUnitFromId(in.readInt());
		this.startReminderDate = new DateTime(in.readLong());
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
	
	public DateTime getStartReminderDate()  {
		return startReminderDate;
	}
	
	public DateTime getNextReminderDate()  {
		return nextReminderDate;
	}
	
	public int getContactTypeFlags()  {
		return contactTypeFlags;
	}
	
	public boolean hasContactType(ContactType contactType)  {
		return (contactType.getFlag() & contactTypeFlags) != 0;
	}
	
	public Reminder withFrequency(int newFrequency)  {
		return new Reminder(id, name, newFrequency, reminderFrequencyUnit, startReminderDate, nextReminderDate, contactTypeFlags);
	}
	
	public Reminder withTimeUnit(TimeUnit newUnit)  {
		return new Reminder(id, name, reminderFrequency, newUnit, startReminderDate, nextReminderDate, contactTypeFlags);	
	}

	public Reminder withReminderPeriod(DateTime startDate, DateTime endDate)  {
		return new Reminder(id, name, reminderFrequency, reminderFrequencyUnit, startDate, endDate, contactTypeFlags);
	}
	
	public Reminder withContactTypeFlags(int newFlags)  {
		return new Reminder(id, name, reminderFrequency, reminderFrequencyUnit, startReminderDate, nextReminderDate, newFlags);
	}
	
	@Override
	public String toString()  {
		return String.format("{id:%d, name:%s, reminderFrequency:%d, reminderFrequencyUnit:%s, " +
				"startReminderDate:%s, nextReminderDate:%s, contactTypeFlags:%d}", 
				id, name, reminderFrequency, 
				reminderFrequencyUnit,
				startReminderDate,
				nextReminderDate,
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
		dest.writeLong(startReminderDate.getMillis());
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
	
	
	public static Reminder createNextReminder(Reminder reminder)  {
		//newStartDate = max{nextReminderDate, now};
		DateTime newStartDate = reminder.getNextReminderDate();
		if(reminder.getNextReminderDate().isBeforeNow())  {
			newStartDate = DateTime.now();
		}
		DateTime newReminderDate = calculateNextDate(reminder.getFrequency(), reminder.getFrequencyUnit(), newStartDate);
		Reminder newReminder = reminder.withReminderPeriod(newStartDate, newReminderDate);
		return newReminder;
	}
	
	private static DateTime calculateNextDate(int frequency, TimeUnit unit, DateTime beginDate)  {
		DateTime newDate;
		switch (unit)  {
			case DAYS: newDate = beginDate.plusDays(frequency); break;
			case WEEKS: newDate = beginDate.plusWeeks(frequency); break;
			case MONTHS: newDate = beginDate.plusMonths(frequency); break;
			case YEARS: newDate = beginDate.plusYears(frequency); break;
			default:
				throw new UnsupportedOperationException("Unknown TimeUnit: " + unit);
		}
		return newDate;
//		return DateTime.now().plusSeconds(20);
	}
}
