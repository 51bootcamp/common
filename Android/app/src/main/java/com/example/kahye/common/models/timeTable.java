package com.example.kahye.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class timeTable implements Parcelable {

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

    public void setTimeTableIdx(Integer timeTableIdx) {
        this.timeTableIdx = timeTableIdx;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public final static Creator<timeTable> CREATOR = new Creator<timeTable>() {

        @SuppressWarnings({
                "unchecked"
        })
        public timeTable createFromParcel(Parcel in) {
            return new timeTable(in);
        }

        public timeTable[] newArray(int size) {
            return (new timeTable[size]);
        }

    };

    protected timeTable(Parcel in) {
        this.timeTableIdx = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    public timeTable() {
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
