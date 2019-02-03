package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReviewList implements Parcelable{

    @SerializedName("reviewList")
    @Expose
    private List<Review> reviewList = new ArrayList<Review>();

    public final static Parcelable.Creator<ReviewList> CREATOR = new Creator<ReviewList>() {
        @SuppressWarnings({
                "unchecked"
        })
        public ReviewList createFromParcel(Parcel in) {
            return new ReviewList(in);
        }

        public ReviewList[] newArray(int size) {
            return (new ReviewList[size]);
        }
    };

    protected ReviewList(Parcel in) {
        in.readList(this.reviewList, (ReviewList.class.getClassLoader()));
    }

    public ReviewList() {
    }

    public List<Review> getReviewList() {
        return reviewList;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(reviewList);
    }

    public int describeContents() {
        return 0;
    }

}