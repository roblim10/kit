package com.android.kit.model;

import java.util.Collections;
import java.util.Set;

import org.joda.time.DateTime;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.common.collect.Sets;

public class KitContact implements Parcelable {
	private int id;
	private String name;
	private int reminderFrequency = 1;
	private TimeUnit reminderFrequencyUnit = TimeUnit.WEEKS;
	private DateTime nextReminderDate = DateTime.now().plusWeeks(1);
	private Set<ContactType> contactTypes = Sets.newHashSet(ContactType.PHONE_CALL);
	
	public KitContact(int id)  {
		this.id = id;
	}
	
	public KitContact(Parcel in)  {
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
