package com.androidth.general.persistence.live;

import android.support.annotation.NonNull;

import com.androidth.general.api.live1b.AccountBalanceResponseDTO;

import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;

@Singleton
public class Live1BResponseDTO {

    public static BehaviorSubject<AccountBalanceResponseDTO> accountBalanceResponseDTOBehaviorSubject;

    @NonNull
    public static Observable<AccountBalanceResponseDTO> getAccountBalanceObservable(){
        accountBalanceResponseDTOBehaviorSubject = accountBalanceResponseDTOBehaviorSubject.create();
        return accountBalanceResponseDTOBehaviorSubject.asObservable();
    }
}
