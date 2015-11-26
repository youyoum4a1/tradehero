package com.tradehero.th.network.service;

import com.tradehero.chinabuild.data.AppInfoDTO;
import com.tradehero.chinabuild.data.FollowStockForm;
import com.tradehero.chinabuild.data.LoginContinuallyTimesDTO;
import com.tradehero.chinabuild.data.RecommendItems;
import com.tradehero.chinabuild.data.VideoDTOList;
import com.tradehero.chinabuild.fragment.stocklearning.QuestionDTO;
import com.tradehero.th.api.form.UserFormDTO;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.social.UserFriendsDTOList;
import com.tradehero.th.api.users.UpdateReferralCodeDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.api.users.password.BindBrokerDTO;
import com.tradehero.th.api.users.password.ForgotPasswordDTO;
import com.tradehero.th.api.users.password.ForgotPasswordFormDTO;
import com.tradehero.th.api.users.password.PhoneNumberBindDTO;
import com.tradehero.th.api.users.password.PhoneNumberVerifyDTO;
import com.tradehero.th.api.users.password.ResetPasswordDTO;
import com.tradehero.th.api.users.password.ResetPasswordFormDTO;
import com.tradehero.th.api.watchlist.WatchlistPositionDTO;
import com.tradehero.th.fragments.social.friend.FollowFriendsForm;
import java.util.List;
import retrofit.Callback;
import retrofit.client.Response;
import retrofit.http.Body;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.Multipart;
import retrofit.http.POST;
import retrofit.http.PUT;
import retrofit.http.Part;
import retrofit.http.Path;
import retrofit.http.Query;
import retrofit.mime.TypedOutput;

interface UserServiceAsync
{
    //<editor-fold desc="Sign-Up With Email">
    @FormUrlEncoded @POST("/SignupWithEmail")
    void signUpWithEmail(@Header("Authorization") String authorization,
            @Field("biography") String biography,
            @Field("deviceToken") String deviceToken,
            @Field("displayName") String displayName,
            @Field("inviteCode") String inviteCode,
            @Field("email") String email,
            @Field("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Field("firstName") String firstName,
            @Field("lastName") String lastName,
            @Field("location") String location,
            @Field("password") String password,
            @Field("passwordConfirmation") String passwordConfirmation,
            @Field("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Field("username") String username,
            @Field("website") String website,
            @Field("phone_number") String phoneNumber,
            @Field("verify_code") String verifyCode,
            @Field("device_access_token") String deviceAccessToken,
            Callback<UserProfileDTO> cb);

    @Multipart @POST("/SignupWithEmail")
    void signUpWithEmail(
            @Header("Authorization") String authorization,
            @Part("biography") String biography,
            @Part("deviceToken") String deviceToken,
            @Part("displayName") String displayName,
            @Part("inviteCode") String inviteCode,
            @Part("email") String email,
            @Part("emailNotificationsEnabled") Boolean emailNotificationsEnabled,
            @Part("firstName") String firstName,
            @Part("lastName") String lastName,
            @Part("location") String location,
            @Part("password") String password,
            @Part("passwordConfirmation") String passwordConfirmation,
            @Part("pushNotificationsEnabled") Boolean pushNotificationsEnabled,
            @Part("username") String username,
            @Part("website") String website,
            @Part("phone_number") String phoneNumber,
            @Part("verify_code") String verifyCode,
            @Part("device_access_token") String deviceAccessToken,
            @Part("profilePicture") TypedOutput profilePicture,
            Callback<UserProfileDTO> cb);
    //</editor-fold>

    //<editor-fold desc="Signup">
    @POST("/users")
    void signUp(
            @Header("Authorization") String authorization,
            @Body UserFormDTO user,
            Callback<UserProfileDTO> callback);
    //</editor-fold>

    @Multipart @PUT("/users/{userId}/updateUser")
    void updatePhoto(
            @Path("userId") int userId,
            @Part("profilePicture") TypedOutput profilePicture,
            Callback<UserProfileDTO> cb);

    @Multipart @PUT("/users/{userId}/updateUser")
    void updateName(
            @Path("userId") int userId,
            @Part("displayName") String displayName,
            Callback<UserProfileDTO> cb);

    @Multipart @PUT("/users/{userId}/updateUser")
    void updateSign(
            @Path("userId") int userId,
            @Part("signature") String displayName,
            Callback<UserProfileDTO> cb);

    @Multipart @PUT("/users/{userId}/updateUser")
    void uploadCollege(
            @Path("userId") int userId,
            @Part("school") String college,
            Callback<UserProfileDTO> cb);

    @Multipart @PUT("/users/{userId}/updateUser")
    void updateAccount(
            @Path("userId") int userId,
            @Part("email") String email,
            @Part("password") String password,
            @Part("passwordConfirmation") String passwordConfirmation,
            Callback<UserProfileDTO> cb);
    //</editor-fold>

    //<editor-fold desc="Forgot Password">
    @POST("/forgotPassword")
    void forgotPasswordEmail(
            @Body ForgotPasswordFormDTO forgotPasswordFormDTO,
            Callback<ForgotPasswordDTO> callback);

    @POST("/resetPassword")
    void resetPasswordMobile(@Body ResetPasswordFormDTO resetPasswordFormDTO,
                              Callback<ResetPasswordDTO> callback);
    //</editor-fold>


    @GET("/users/{userId}/SearchFriends")
    void searchSocialFriends(
            @Path("userId") int userId,
            @Query("socialNetwork") SocialNetworkEnum socialNetwork,
            @Query("q")String query,
            Callback<UserFriendsDTOList> callback);
    //</editor-fold>

    @POST("/users/BatchFollow/free")
    void followBatchFree(@Body FollowFriendsForm followFriendsForm, Callback<UserProfileDTO> callback);

    @POST("/BatchCreateWatchlistPositions")
    void followStock(@Body FollowStockForm followStockForm, Callback<List<WatchlistPositionDTO>> callback);

    //<editor-fold desc="Invite Friends">
    @POST("/users/{userId}/inviteFriends")
    void inviteFriends(
            @Path("userId") int userId,
            @Body InviteFormDTO inviteFormDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Follow Hero">
    @POST("/users/{userId}/follow")
    void follow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);

    @POST("/users/{userId}/follow/free")
    void freeFollow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);


    //<editor-fold desc="Unfollow Hero">
    @POST("/users/{userId}/unfollow")
    void unfollow(
            @Path("userId") int userId,
            Callback<UserProfileDTO> callback);
    //</editor-fold>


    //<editor-fold desc="Update Referral Code">
    @POST("/users/{userId}/updateInviteCode")
    void updateReferralCode(
            @Path("userId") int userId,
            @Body UpdateReferralCodeDTO updateReferralCodeDTO,
            Callback<Response> callback);
    //</editor-fold>

    //<editor-fold desc="Send Verify code">
    @POST("/sendCode")
    void sendCode(
            @Query("phoneNumber") String phoneNumber,
            Callback<Response> cb);
    //</editor-fold>

    //<editor-fold desc="Send Verify code">
    @POST("/cn/v2/users/bindPhoneNumber")
    @FormUrlEncoded
    void bindPhoneNumber(
            @Field("phone_number") String phoneNumber,
            @Field("verify_code") String verifyCode,
            Callback<PhoneNumberBindDTO> cb);
    //</editor-fold>

    //<editor_fold desc="Download App Version Info">
    @GET("/checkVersion")
    void downloadAppVersion(
            Callback<AppInfoDTO> cb);
    //</editor-fold>

    //<editor_fold desc="Track Share">
    @GET("/social/trackShare")
    void trackShare(@Query("eventName") String eventName, Callback<Response> cb);
    //</editor-fold>

    //<editor_fold desc="Login Times">
    @GET("/social/shareLogin")
    void getContinuallyLoginTimes(@Query("userId")String userId, Callback<LoginContinuallyTimesDTO> cb);
    //</editor-fold>

    //<editor_fold desc="Retrieve Recommend Items">
    @GET("/recommend")
    void downloadRecommendItems(Callback<RecommendItems> cb);
    //</editor-fold>

    //<editor_fold desc="video list">
    @GET("/videos/cnvideos")
    void downloadVideoList(Callback<VideoDTOList> cb);
    //</editor-fold>

    @GET("/stockQuestions/download")
    void downloadQuestions(@Query("updatedAtTicks")long updatedAtTicks, Callback<QuestionDTO> questionDTOCallback);

    @GET("/cn/v2/users/verifyCode")
    void verifyPhoneNumber(
            @Query("phoneNumber") String phoneNumber,
            @Query("code") String verifyCode,
            Callback<PhoneNumberVerifyDTO> cb);

    @GET("/cn/v2/users/verifyBroker")
    void checkPhoneNumberAccountStatus(
            @Query("brokerName") String phoneNumber,
            @Query("phoneNumber") String verifyCode,
            Callback<PhoneNumberVerifyDTO> cb);

    @POST("/cn/v2/users/bindBroker")
    @FormUrlEncoded
    void bindBroker(
            @Field("phoneNumber") String phoneNumber,
            @Field("brokerName") String brokerName,
            @Field("brokerUserId") String brokerUserId,
            Callback<BindBrokerDTO> cb
    );

}
