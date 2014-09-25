package com.android.kit.model;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Sets;

public class Reminder implements Parcelable {
	private final static int DEFAULT_REMINDER_FREQUENCY = 1;
	private final static TimeUnit DEFAULT_REMINDER_UNIT = TimeUnit.WEEKS;
	private final static int DEFAULT_REMINDER_HOUR = 18;
	private final static Set<ContactType> DEFAULT_CONTACT_TYPES = Sets.newHashSet(ContactType.PHONE_CALL);

	private static DateTime getDefaultReminderDate()  {
		DateTime defaultReminder = DateTime.now()
				.withHourOfDay(DEFAULT_REMINDER_HOUR)
				.withMinuteOfHour(0);
		switch (DEFAULT_REMINDER_UNIT)  {
			case DAYS: return defaultReminder.plusDays(DEFAULT_REMINDER_FREQUENCY);
			case WEEKS: return defaultReminder.plusWeeks(DEFAULT_REMINDER_FREQUENCY);
			case MONTHS: return defaultReminder.plusMonths(DEFAULT_REMINDER_FREQUENCY);
			case YEARS: return defaultReminder.plusYears(DEFAULT_REMINDER_FREQUENCY);
			default:
				throw new UnsupportedOperationException("Error setting default reminder.  Unsupported TimeUnit: " + DEFAULT_REMINDER_UNIT);
		}
	}
	
	private int id;
	private String name;
	private int reminderFrequency;
	private TimeUnit reminderFrequencyUnit;
	private DateTime nextReminderDate;
	private Set<ContactType> contactTypes;
	
	public Reminder(int id)  {
		this.id = id;
		this.reminderFrequency = DEFAULT_REMINDER_FREQUENCY;
		this.reminderFrequencyUnit = DEFAULT_REMINDER_UNIT;
		this.nextReminderDate = Reminder.getDefaultReminderDate();
		this.contactTypes = Sets.newHashSet(DEFAULT_CONTACT_TYPES);
	}
	
	public Reminder(Parcel in)  {
		this.id = in.readInt();
		this.name = in.readString();
		this.reminderFrequency = in.readInt();
		this.reminderFrequencyUnit = TimeUnit.getTimeUnitFromId(in.readInt());
		this.nextReminderDate = new DateTime(in.readLong());
		this.contactTypes = ContactType.convertContactTypeValue(in.readInt());
	}
	
	public int getId()  {
		return id;
	}
	
	public String getName()  {
		return name;
	}
	
	public void setName(String name)  {
		this.name = name;
	}
	
	public int getFrequency()  {
		return reminderFrequency;
	}
	
	public void setFrequency(int frequency)  {
		this.reminderFrequency = frequency;
	}
	
	public TimeUnit getFrequencyUnit()  {
		return reminderFrequencyUnit;
	}
	
	public void setFrequencyUnit(TimeUnit unit)  {
		//TODO: Apache has a NullArgumentException.
		if (unit == null)  {
			throw new UnsupportedOperationException("Cannot set reminder frequency unit to null");
		}
		this.reminderFrequencyUnit = unit;
	}
	
	public DateTime getNextReminderDate()  {
		return nextReminderDate;
	}
	
	public void setNextReminderDate(DateTime date)  {
		//TODO: Apache has a NullArgumentException.
		if (date == null)  {
			throw new UnsupportedOperationException("Cannot set next reminder date to null");
		}
		this.nextReminderDate = date;
	}
	
	public Set<ContactType> getContactTypes()  {
		return Collections.unmodifiableSet(contactTypes);
	}
	
	public void setContactTypes(Set<ContactType> types)  {
		this.contactTypes.clear();
		this.contactTypes.addAll(types);
	}
	
	@Override
	public String toString()  {
		return String.format("{id:%d, name:%s, reminderFrequency:%d, reminderFrequencyUnit:%s, nextReminderDate:%s}", 
				id, name, reminderFrequency, 
				reminderFrequencyUnit != null ? reminderFrequencyUnit.toString() : "null", 
				nextReminderDate != null ? nextReminderDate.toString() : "null");
	}
	
	public void copy(Reminder reminder)  {
		setFrequency(reminder.getFrequency());
		setFrequencyUnit(reminder.getFrequencyUnit());
		setNextReminderDate(reminder.getNextReminderDate());
		setContactTypes(reminder.getContactTypes());
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
		dest.writeInt(ContactType.convertContactTypeCollection(contactTypes));
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
			
}
