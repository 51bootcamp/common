package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class Class implements Parcelable {
    @SerializedName("classID")
    @Expose
    private Integer classID;
    @SerializedName("price")
    @Expose
    private float price;
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
    private List<TimeTable> availableTimeTable = new ArrayList<TimeTable>();
    @SerializedName("coverImage")
    @Expose
    private List<String> coverImage = new ArrayList<String>();

    public Integer getClassID() {
        return classID;
    }

    public String getClassName() {
        return className;
    }

    public String getExpertName() {
        return expertName;
    }

    public Integer getMinGuestCount() {
        return minGuestCount;
    }

    public Integer getMaxGuestCount() {
        return maxGuestCount;
    }

    public Float getPrice() { return price; }

    public List<TimeTable> getAvailableTimeTable() {
        return availableTimeTable;
    }

    public List<String> getCoverImage() {
        return coverImage;
    }

    public final static Creator<Class> CREATOR = new Creator<Class>() {

        @SuppressWarnings({
                "unchecked"
        })
        public Class createFromParcel(Parcel in) {
            return new Class(in);
        }

        public Class[] newArray(int size) {
            return (new Class[size]);
        }

    };

    protected Class(Parcel in) {
        this.classID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.className = ((String) in.readValue((String.class.getClassLoader())));
        this.expertName = ((String) in.readValue((String.class.getClassLoader())));
        this.minGuestCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.maxGuestCount = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.price = ((float) in.readValue((float.class.getClassLoader())));

        in.readList(this.availableTimeTable,
                (uncommon.common.models.TimeTable.class.getClassLoader()));
        in.readList(this.coverImage, (java.lang.String.class.getClassLoader()));

    }

    public Class() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(classID);
        dest.writeValue(className);
        dest.writeValue(expertName);
        dest.writeValue(minGuestCount);
        dest.writeValue(maxGuestCount);
        dest.writeValue(price);
        dest.writeList(availableTimeTable);
        dest.writeList(coverImage);

    }

    public int describeContents() {
        return 0;
    }

}