package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class InviteCode implements Parcelable {

    @SerializedName("inviteCodeID")
    @Expose
    private Integer inviteCodeID;
    @SerializedName("randomCode")
    @Expose
    private String randomCode;
    @SerializedName("isExpired")
    @Expose
    private Boolean isExpired;

    public final static Parcelable.Creator<InviteCode> CREATOR = new Creator<InviteCode>() {

        @SuppressWarnings({
                "unchecked"
        })
        public InviteCode createFromParcel(Parcel in) {
            return new InviteCode(in);
        }
        public InviteCode[] newArray(int size) { return (new InviteCode[size]);
        }
    };

    protected InviteCode(Parcel in) {
        this.inviteCodeID = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.randomCode = ((String) in.readValue((String.class.getClassLoader())));
        this.isExpired = ((Boolean) in.readValue((Boolean.class.getClassLoader())));

    }

    public InviteCode() {
    }

    public Integer getInviteCodeID() { return inviteCodeID; }

    public String getRandomCode() {
        return randomCode;
    }

    public Boolean getIsExpired() { return isExpired; }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(inviteCodeID);
        dest.writeValue(randomCode);
        dest.writeValue(isExpired);
    }

    public int describeContents() {
        return 0;
    }

}