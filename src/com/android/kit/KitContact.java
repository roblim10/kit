package com.android.kit;

import android.os.Parcel;
import android.os.Parcelable;

public class KitContact implements Parcelable {
	private int id;
	private String name;
	
	public KitContact(int id)  {
		this.id = id;
	}
	
	public KitContact(Parcel in)  {
		this.id = in.readInt();
		this.name = in.readString();
	}
	
	public String getName()  {
		return name;
	}
	
	public void setName(String name)  {
		this.name = name;
	}
	
	public int getId()  {
		return id;
	}
	
	@Override
	public String toString()  {
		return String.format("{id:%d, name:%s}", id, name);
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
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
