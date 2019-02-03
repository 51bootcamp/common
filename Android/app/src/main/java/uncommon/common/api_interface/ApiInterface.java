package uncommon.common.api_interface;

import org.json.simple.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;
import uncommon.common.models.Class;
import uncommon.common.models.ClassList;
import uncommon.common.models.InviteCode;
import uncommon.common.models.Reservation;
import uncommon.common.models.ReservationList;
import uncommon.common.models.Review;
import uncommon.common.models.ReviewList;
import uncommon.common.models.User;

public interface ApiInterface {
    @GET("class/{date}/")
    Call<ClassList> getClassList(@Path("date") String date);

    @GET("class/{date}/{classID}")
    Call<Class> getClassInfo(@Path("date") String date,
                             @Path("classID") Integer classID);

    @POST("login/")
    Call<User> login(@Body JSONObject userEmail);

    @POST("signup/")
    Call<Void> signup(@Body User user);

    @POST("reserve/")
    Call<Reservation> makeReservation(@Body JSONObject reservation);

    @GET("reserve/{userEmail}")
    Call<Reservation> getReservation(@Path("userEmail") String userEmail);

    @GET("invite/{inviteCode}")
    Call<Void> getInviteCode(@Path("inviteCode") String inviteCode);

    @GET("reserveList/{userEmail}")
    Call<ReservationList> getReservationList(@Path("userEmail") String userEmail);

    @POST("review/")
    Call<Review> writeReview(@Body Review review);

    @GET("review/{classID}")
    Call<ReviewList> getReviewList(@Path("classID") Integer classID);
}