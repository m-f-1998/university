package uk.co.coryalexander.pedalpay.uk.co.coryalexander.pedalpay.network;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface PostDataService {
    @FormUrlEncoded
    @POST("login/login.php")
    Call<LoginResponse> userLogin(
      @Field("email") String email,
      @Field("password") String password
    );


    @FormUrlEncoded
    @POST("userFunctions/makeBooking.php")
    Call<BookingResponse> makeBooking(
        @Field("email") String email,
        @Field("password") String password,
        @Field("bookStart") String startTime,
        @Field("bookEnd") String endTime,
        @Field("hub") String hubName,
        @Field("bikeType") String bikeType
        );

    @POST("userFunctions/locations.php")
    Call<LocationResponse> getLocs();

    @FormUrlEncoded
    @POST("userFunctions/getBookings.php")
    Call<GetBookingsResponse> getBookings(
            @Field("email") String email,
            @Field("password") String password
    );
}
