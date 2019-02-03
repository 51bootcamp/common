package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

public class ReservationList implements Parcelable {

    @SerializedName("reservationList")
    @Expose
    private List<Reservation> reservationList = new ArrayList<Reservation>();

    public static final Parcelable.Creator<ReservationList> CREATOR = new
            Creator<ReservationList>() {
        @Override
        public ReservationList createFromParcel(Parcel in) {
            return new ReservationList(in);
        }

        @Override
        public ReservationList[] newArray(int size) {
            return new ReservationList[size];
        }
    };

    protected ReservationList(Parcel in) {
        in.readList(this.reservationList, (ReservationList.class.getClassLoader()));
    }

    public ReservationList() {

    }

    public List<Reservation> getReservationList() {
        return reservationList;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(reservationList);
    }
}
