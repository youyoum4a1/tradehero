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
    public static BehaviorSubject<LiveQuoteDTO> liveFXQuoteDTOBehaviorSubject;

    public static BehaviorSubject<AccountBalanceResponseDTO> getAccountBalanceResponseDTOBehaviorSubject() {
        if(accountBalanceResponseDTOBehaviorSubject==null)
            accountBalanceResponseDTOBehaviorSubject = BehaviorSubject.create();
        return accountBalanceResponseDTOBehaviorSubject;
    }

    public static BehaviorSubject<LiveQuoteDTO> getLiveQuoteDTOBehaviorSubject() {
        if(liveQuoteDTOBehaviorSubject==null){
            liveQuoteDTOBehaviorSubject = BehaviorSubject.create();
        }
        return liveQuoteDTOBehaviorSubject;
    }

    public static BehaviorSubject<LiveQuoteDTO> getLiveFXQuoteDTOBehaviorSubject() {
        if(liveFXQuoteDTOBehaviorSubject==null)
            liveFXQuoteDTOBehaviorSubject = BehaviorSubject.create();
        return liveFXQuoteDTOBehaviorSubject;
    }

    @NonNull
    public static Observable<AccountBalanceResponseDTO> getAccountBalanceObservable(){
        return getAccountBalanceResponseDTOBehaviorSubject().asObservable();
    }

    @NonNull
    public static Observable<LiveQuoteDTO> getLiveQuoteObservable(){

        return getLiveQuoteDTOBehaviorSubject().asObservable();
    }

    @NonNull
    public static Observable<LiveQuoteDTO> getLiveFXQuoteObservable(){
        return getLiveFXQuoteDTOBehaviorSubject().asObservable();
    }
}
