package com.example.timeriut;

import android.os.Parcel;
import android.os.Parcelable;

public class Activity implements Parcelable {
    private String name;
    private int duration;

    public Activity(String n, int d){
        this.name = n;
        this.duration = d;
    }

    protected Activity(Parcel in) {
        name = in.readString();
        duration = in.readInt();
    }

    public static final Creator<Activity> CREATOR = new Creator<Activity>() {
        @Override
        public Activity createFromParcel(Parcel in) {
            return new Activity(in);
        }

        @Override
        public Activity[] newArray(int size) {
            return new Activity[size];
        }
    };

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDuration() {
        return duration;
    }

    public String getStringDuration() {
        return String.valueOf(this.duration);
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public void addOneDuration(){
        this.duration++;
    }

    public void subOneDuration(){
        this.duration--;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeInt(duration);
    }
}
