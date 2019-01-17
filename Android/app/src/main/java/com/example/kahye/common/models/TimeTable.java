package com.example.kahye.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class TimeTable implements Parcelable {

    @SerializedName("timeTableIdx")
    @Expose
    private Integer timeTableIdx;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;

    public Integer getTimeTableIdx() {
        return timeTableIdx;
    }

    public String getStartTime() {
        return startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public final static Creator<TimeTable> CREATOR = new Creator<TimeTable>() {

        @SuppressWarnings({
                "unchecked"
        })
        public TimeTable createFromParcel(Parcel in) {
            return new TimeTable(in);
        }

        public TimeTable[] newArray(int size) {
            return (new TimeTable[size]);
        }

    };

    protected TimeTable(Parcel in) {
        this.timeTableIdx = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    public TimeTable() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(timeTableIdx);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
    }

    public int describeContents() {
        return 0;
    }
}
