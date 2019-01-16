package com.example.kahye.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class _class implements Parcelable {
    @SerializedName("classID")
    @Expose
    private Integer classID;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("expertName")
    @Expose
    private String expertName;
    @SerializedName("minGuestCount")
    @Expose
    private Integer minGuestCount;
    @SerializedName("maxGuestCount")
    @Expose
    private Integer maxGuestCount;
    @SerializedName("availableTimeTable")
    @Expose
    private List<timeTable> availableTimeTable = new ArrayList<timeTable>();

    public Integer getClassID() {
        return classID;
    }

    public void setClassID(Integer classID) {
        this.classID = classID;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public Integer getminGuestCount() {
        return minGuestCount;
    }

    public void setminGuestCount(Integer minGuestCount) {
        this.minGuestCount = minGuestCount;
    }

    public Integer getmaxGuestCount() {
        return maxGuestCount;
    }

    public void setmaxGuestCount(Integer maxGuestCount) {
        this.maxGuestCount = maxGuestCount;
    }

    public List<timeTable> getAvailableTimeTable() {
        return availableTimeTable;
    }

    public void setAvailableTimeTable(List<timeTable> availableTimeTable) {
        this.availableTimeTable = availableTimeTable;
    }

    public final static Creator<_class> CREATOR = new Creator<_class>() {


        @SuppressWarnings({
                "unchecked"
        })
        public _class createFromParcel(Parcel in) {
            return new _class(in);
        }

        public _class[] newArray(int size) {
            return (new _class[size]);
        }

    };

    protected _class(Parcel in) {
        this.classID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.className = ((String) in.readValue((String.class.getClassLoader())));
        this.expertName = ((String) in.readValue((String.class.getClassLoader())));
        this.minGuestCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.maxGuestCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        in.readList(this.availableTimeTable, (com.example.kahye.common.models.timeTable.class.getClassLoader()));
    }

    public _class() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(classID);
        dest.writeValue(className);
        dest.writeValue(expertName);
        dest.writeValue(minGuestCount);
        dest.writeValue(maxGuestCount);
        dest.writeList(availableTimeTable);
    }

    public int describeContents() {
        return 0;
    }

}
