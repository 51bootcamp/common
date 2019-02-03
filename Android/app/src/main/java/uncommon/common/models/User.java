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

    public String getUserEmail(){ return this.userEmail; }
    public String getUserName() { return this.userName; }

    public User(String userEmail, String userName){
        this.userEmail = userEmail;
        this.userName = userName;
        this.accountType = "FACEBOOK";
    }
}
