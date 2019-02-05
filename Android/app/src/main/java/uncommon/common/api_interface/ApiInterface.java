package uncommon.common.api_interface;

import org.json.simple.JSONObject;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import uncommon.common.models.Class;
import uncommon.common.models.ClassList;
import uncommon.common.models.Image;
import uncommon.common.models.Reservation;
import uncommon.common.models.ReservationList;
import uncommon.common.models.Review;
import uncommon.common.models.ReviewList;
import uncommon.common.models.User;

public interface ApiInterface {
    @GET("class/{date}")
    Call<ClassList> getClassList(@Path("date") String date);

    @GET("class/{date}/{classID}")
    Call<Class> getClassInfo(@Path("date") String date,
                             @Path("classID") Integer classID);

    @GET("invite/{inviteCode}")
    Call<Void> getInviteCode(@Path("inviteCode") String inviteCode);

    @POST("login/")
    Call<JSONObject> login(@Body JSONObject userEmail);

    @GET("reserve/upcoming")
    Call<Reservation> getUpcoming();

    @GET("reserve/{reservationID}")
    Call<Reservation> getReservation(@Path("reservationID") Integer reservationID);

    @POST("reserve/")
    Call<Reservation> makeReservation(@Body JSONObject reservation);

    @GET("reserveList")
    Call<ReservationList> getReservationList();

    @POST("review/")
    Call<Review> writeReview(@Body Review review);

    @GET("review/{classID}")
    Call<ReviewList> getReviewList(@Path("classID") Integer classID);

    @POST("signup/")
    Call<JSONObject> signup(@Body User user);

    @POST("upload/{classID}/")
    Call <Image> uploadImage(@Body RequestBody coverImage, @Path("classID") Integer classID);

    @POST("makeClass/")
    Call<Class> makeClass(@Body JSONObject makeClass);

    @POST("sendMail/")
    Call<Integer> sendMail(@Body JSONObject makeClass);

}