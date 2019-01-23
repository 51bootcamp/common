package uncommon.common.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class User {
    @Expose
    public String userEmail;
    @SerializedName("userName")
    @Expose
    public String userName;
    public String accountType;
    public boolean isLecturer;
}
