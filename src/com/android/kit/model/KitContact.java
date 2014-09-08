package com.android.kit.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

public class KitContact implements Parcelable {
	private int id;
	private String name;
	private int reminderFrequency;
	private TimeUnit reminderFrequencyUnit;
	private DateTime nextReminderDate;
	private HashSet<ContactType> contactTypes;
	
	public KitContact(int id)  {
		this.id = id;
		contactTypes = new HashSet<ContactType>();
	}
	
	public KitContact(Parcel in)  {
		this.id = in.readInt();
		this.name = in.readString();
		this.reminderFrequency = in.readInt();
		this.reminderFrequencyUnit = (TimeUnit)in.readSerializable();
		Long nextReminderDateLong = (Long)in.readValue(null);
		this.nextReminderDate = nextReminderDateLong != null ? new DateTime(nextReminderDateLong) : null;
		this.contactTypes = (HashSet<ContactType>)in.readSerializable();
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
	
	public int getReminderFrequency()  {
		return reminderFrequency;
	}
	
	public void setReminderFrequency(int frequency)  {
		this.reminderFrequency = frequency;
	}
	
	public TimeUnit getReminderFrequencyUnit()  {
		return reminderFrequencyUnit;
	}
	
	public void setReminderFrequencyUnit(TimeUnit unit)  {
		this.reminderFrequencyUnit = unit;
	}
	
	public DateTime getNextReminderDate()  {
		return nextReminderDate;
	}
	
	public void setNextReminderDate(DateTime date)  {
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

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeInt(reminderFrequency);
		dest.writeSerializable(reminderFrequencyUnit);
		dest.writeValue(nextReminderDate != null ? nextReminderDate.getMillis() : null);
		dest.writeSerializable(contactTypes);
	}
	
	public static final Parcelable.Creator<KitContact> CREATOR = new Parcelable.Creator<KitContact>()  {
		@Override
		public KitContact createFromParcel(Parcel source) {
			return new KitContact(source);
		}

		@Override
		public KitContact[] newArray(int size) {
			return new KitContact[size];
		} 
	};
			
}
