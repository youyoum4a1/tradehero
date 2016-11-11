package com.androidth.general.persistence.live;

import android.support.annotation.NonNull;

import com.androidth.general.api.live1b.AccountBalanceResponseDTO;
import com.androidth.general.fragments.security.LiveQuoteDTO;

import javax.inject.Singleton;

import rx.Observable;
import rx.subjects.BehaviorSubject;

@Singleton
public class Live1BResponseDTO {

    public static BehaviorSubject<AccountBalanceResponseDTO> accountBalanceResponseDTOBehaviorSubject;
    public static BehaviorSubject<LiveQuoteDTO> liveQuoteDTOBehaviorSubject;

    @NonNull
    public static Observable<AccountBalanceResponseDTO> getAccountBalanceObservable(){
        accountBalanceResponseDTOBehaviorSubject = accountBalanceResponseDTOBehaviorSubject.create();
        return accountBalanceResponseDTOBehaviorSubject.asObservable();
    }

    @NonNull
    public static Observable<LiveQuoteDTO> getLiveQuoteObservable(){
        liveQuoteDTOBehaviorSubject = liveQuoteDTOBehaviorSubject.create();
        return liveQuoteDTOBehaviorSubject.asObservable();
    }
}
