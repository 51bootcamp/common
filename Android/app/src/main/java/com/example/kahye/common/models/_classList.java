package com.example.kahye.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class _classList implements Parcelable {

    @SerializedName("classList")
    @Expose
    private List<_class> classList = new ArrayList<_class>();

    public List<_class> getClassList() {
        return classList;
    }

    public void setClassList(List<_class> classList) {
        this.classList = classList;
    }

    public final static Creator<_classList> CREATOR = new Creator<_classList>() {


        @SuppressWarnings({
                "unchecked"
        })
        public _classList createFromParcel(Parcel in) {
            return new _classList(in);
        }

        public _classList[] newArray(int size) {
            return (new _classList[size]);
        }

    };

    protected _classList(Parcel in) {
        in.readList(this.classList, (com.example.kahye.common.models._classList.class.getClassLoader()));
    }

    public _classList() {
    }


    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(classList);
    }

    public int describeContents() {
        return 0;
    }


}


