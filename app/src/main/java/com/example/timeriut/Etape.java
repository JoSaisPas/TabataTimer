package com.example.timeriut;

import android.os.Parcel;
import android.os.Parcelable;

public class Etape implements Parcelable{
    private String name;
    private int duration;

    public Etape(String n, int d){
        this.name = n;
        this.duration = d;
    }

    protected Etape(Parcel in) {
        name = in.readString();
        duration = in.readInt();
    }

    public static final Creator<Etape> CREATOR = new Creator<Etape>() {
        @Override
        public Etape createFromParcel(Parcel in) {
            return new Etape(in);
        }

        @Override
        public Etape[] newArray(int size) {
            return new Etape[size];
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
        if(this.name != "Repos" && this.name != "Travail"){
            if(this.duration >= 15){
                this.duration = 15;
            }
        }
    }

    public void subOneDuration(){
        this.duration--;

        if(this.duration <= 0){
            this.duration = 0;
        }

        if(this.name == "Repos" || this.name == "Travail" || this.name =="Preparation"){
            if(this.duration <= 3){
                this.duration = 3;
            }
        }
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
