package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Review implements Parcelable {

    @SerializedName("reviewIdx")
    @Expose
    private Integer reviewIdx;
    @SerializedName("userEmail")
    @Expose
    private String userEmail;
    @SerializedName("userName")
    @Expose
    private String userName;
    @SerializedName("classID")
    @Expose
    private Integer classID;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("content")
    @Expose
    private String content;
    @SerializedName("rating")
    @Expose
    private Float rating;

    public final static Parcelable.Creator<Review> CREATOR = new Creator<Review>() {
        @SuppressWarnings({
                "unchecked"
        })
        public Review createFromParcel(Parcel in) { return new Review(in); }

        public Review[] newArray(int size) { return (new Review[size]); }
    };

    protected Review(Parcel in) {
        this.userEmail = ((String) in.readValue((String.class.getClassLoader())));
        this.userName = ((String) in.readValue((String.class.getClassLoader())));
        this.classID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.title = ((String) in.readValue((String.class.getClassLoader())));
        this.content = ((String) in.readValue((String.class.getClassLoader())));
        this.rating = ((Float) in.readValue((Float.class.getClassLoader())));
    }

    public Review(){
        
    }

    public Review(Integer classID, String title, String content, Float rating) {
        this.userEmail = userEmail;
        this.userName = userName;
        this.classID = classID;
        this.title = title;
        this.content = content;
        this.rating = rating;
    }

    public Integer getreviewIdx() {
        return reviewIdx;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public String getUserName() {
        return userName;
    }

    public Integer getClassID() {
        return classID;
    }

    public String getTitle() {
        return title;
    }

    public String getContent() {
        return content;
    }

    public Float getRating() {
        return rating;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(userEmail);
        dest.writeValue(classID);
        dest.writeValue(title);
        dest.writeValue(content);
        dest.writeValue(rating);
        dest.writeValue(reviewIdx);
    }

    public int describeContents() { return 0; }

}