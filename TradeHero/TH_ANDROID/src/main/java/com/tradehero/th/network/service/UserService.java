package com.tradehero.th.network.service;

import com.tradehero.th.api.form.ForgotPasswordFormDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.users.ForgotPasswordDTO;
import com.tradehero.th.api.users.UserAvailabilityDTO;
import com.tradehero.th.api.users.UserLoginDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.UserSearchResultDTO;
import java.util.List;
import org.json.JSONObject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Body;
import retrofit.http.HEAD;
import retrofit.http.POST;
import retrofit.http.Path;
import retrofit.http.Query;

/** Created with IntelliJ IDEA. User: tho Date: 8/15/13 Time: 6:01 PM Copyright (c) TradeHero */

public interface UserService
{
    @POST("/users")
    void signUp(@Header("Authorization") String authorization, @Body UserFormDTO user, Callback<UserProfileDTO> cb);

    @POST("/login")
    void signIn(@Header("Authorization") String authorization, @Body UserFormDTO user, Callback<UserLoginDTO> cb);

    @GET("/checkDisplayNameAvailable")
    void checkDisplayNameAvailable(@Query("displayName") String username, Callback<UserAvailabilityDTO> callback);

    @POST("/forgotPassword")
    void forgotPassword(@Body ForgotPasswordFormDTO forgotPasswordFormDTO, Callback<ForgotPasswordDTO> callback);

    @GET("/users/search")
    void searchUsers(
            @Query("q") String searchString,
            @Query("page") int page,
            @Query("perPage") int perPage,
            Callback<List<UserSearchResultDTO>> callback);


    @GET("/users/GetUser/{userId}")
    UserProfileDTO getUser(int userId) throws RetrofitError;

}
