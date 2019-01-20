package uncommon.common.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;


public class ClassList implements Parcelable {

    @SerializedName("classList")
    @Expose
    private List<Class> classList = new ArrayList<Class>();

    public List<Class> getClassList() {
        return classList;
    }

    public final static Creator<ClassList> CREATOR = new Creator<ClassList>() {

        @SuppressWarnings({
                "unchecked"
        })
        public ClassList createFromParcel(Parcel in) {
            return new ClassList(in);
        }

        public ClassList[] newArray(int size) {
            return (new ClassList[size]);
        }

    };

    protected ClassList(Parcel in) {
        in.readList(this.classList, (ClassList.class.getClassLoader()));
    }

    public ClassList() {
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeList(classList);
    }

    public int describeContents() {
        return 0;
    }

}