package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation implements Parcelable
{
    @SerializedName("expertName")
    @Expose
    private String expertName;
    @SerializedName("className")
    @Expose
    private String className;
    @SerializedName("date")
    @Expose
    private String date;
    @SerializedName("startTime")
    @Expose
    private String startTime;
    @SerializedName("endTime")
    @Expose
    private String endTime;
    public final static Parcelable.Creator<Reservation> CREATOR = new Creator<Reservation>() {

        @SuppressWarnings({
                "unchecked"
                })
        public Reservation createFromParcel(Parcel in) {
            return new Reservation(in);
        }
        public Reservation[] newArray(int size) {
            return (new Reservation[size]);
        }
    };

    protected Reservation(Parcel in) {
        this.expertName = ((String) in.readValue((String.class.getClassLoader())));
        this.className = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Reservation() {
    }

    public String getExpertName() {
        return expertName;
    }

    public void setExpertName(String expertName) {
        this.expertName = expertName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(expertName);
        dest.writeValue(className);
        dest.writeValue(date);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
    }

    public int describeContents() {
        return 0;
    }

}