package com.androidth.general.network.service;

import retrofit.http.GET;
import retrofit.http.Path;
import rx.Observable;

/**
 * Created by ayushnvijay on 6/20/16.
 */
public interface KycServicesRx {

    @GET("/email/confirmation/{userId}/{email}")
    Observable<Boolean> validatedEmail(
            @Path("userId") Integer userId,
            @Path("email") String email);


}
