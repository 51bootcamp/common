package uncommon.common.models;

import com.google.gson.annotations.SerializedName;

public class Image {

    @SerializedName("imageIdx")
    private String imageIdx;

    @SerializedName("title")
    private String title;

    @SerializedName("coverImage")
    private String coverImage;

    @SerializedName("ImageType")
    private String ImageType;

    @SerializedName("classID_id")
    private String ClassID_id;

    @SerializedName("Response")
    private String Response;

    public String getResponse(){
        return Response;
    }
}
