package com.androidth.general.network.service;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by ayushnvijay on 6/20/16.
 */
public interface KycServicesRx {

    @GET("api/email/confirmation/{userId}/{email}")
    Observable<Boolean> validatedEmail(
            @Path("userId") Integer userId,
            @Path("email") String email);


}
