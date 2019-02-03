package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reservation implements Parcelable
{
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("expertName")
    @Expose
    private String expertName;
    @SerializedName("classID")
    @Expose
    private Integer classID;
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
    @SerializedName("guestCount")
    @Expose
    private Integer guestCount;
    @SerializedName("price")
    @Expose
    private Float totalResPrice;
    @SerializedName("coverImg")
    @Expose
    private String coverImg;

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
        this.userEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.expertName = ((String) in.readValue((String.class.getClassLoader())));
        this.classID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.className = ((String) in.readValue((String.class.getClassLoader())));
        this.date = ((String) in.readValue((String.class.getClassLoader())));
        this.startTime = ((String) in.readValue((String.class.getClassLoader())));
        this.endTime = ((String) in.readValue((String.class.getClassLoader())));
        this.guestCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.totalResPrice = ((Float) in.readValue((Float.class.getClassLoader())));
        this.coverImg = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Reservation() {
    }

    public String getUserEmail() { return userEmail; }

    public String getExpertName() {
        return expertName;
    }

    public Integer getClassId() { return classID; }

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

    public Integer getGuestCount(){
        return guestCount;
    }

    public Float getTotalResPrice() {
        return totalResPrice;
    }

    public String getCoverImg() { return coverImg; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userEmail);
        dest.writeValue(expertName);
        dest.writeValue(classID);
        dest.writeValue(className);
        dest.writeValue(date);
        dest.writeValue(startTime);
        dest.writeValue(endTime);
        dest.writeValue(guestCount);
        dest.writeValue(totalResPrice);
        dest.writeValue(coverImg);
    }

    public int describeContents() {
        return 0;
    }

    public boolean isValid(){
        if (expertName == null) return false;
        else return true;
    }
}