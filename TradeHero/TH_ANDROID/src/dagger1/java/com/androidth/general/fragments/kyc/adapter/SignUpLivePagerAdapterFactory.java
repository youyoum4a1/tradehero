package com.androidth.general.fragments.kyc.adapter;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;

import com.androidth.general.api.kyc.ayondo.KYCAyondoForm;
import com.androidth.general.api.live.LiveBrokerSituationDTO;
import com.androidth.general.api.live.LiveTradingSituationDTO;
import com.androidth.general.fragments.kyc.adapter.SignUpLiveAyondoPagerAdapter;
import com.androidth.general.network.service.LiveServiceWrapper;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import javax.inject.Inject;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;

public class SignUpLivePagerAdapterFactory
{
    @NonNull private final LiveServiceWrapper liveServiceWrapper;

    @Inject public SignUpLivePagerAdapterFactory(@NonNull LiveServiceWrapper liveServiceWrapper)
    {
        this.liveServiceWrapper = liveServiceWrapper;
    }

    /**
     * You may want to make your PagerAdapter implement {@link PrevNextObservable}.
     */
    @NonNull @RxLogObservable public Observable<PagerAdapter> create(@NonNull final FragmentManager fm, @NonNull final Bundle args) {

        return liveServiceWrapper.getLiveTradingSituation()
                .observeOn(AndroidSchedulers.mainThread())
                .map(new Func1<LiveTradingSituationDTO, PagerAdapter>()
                {
                    @Override public PagerAdapter call(@NonNull LiveTradingSituationDTO liveTradingSituation)
                    {
                        for (LiveBrokerSituationDTO situation : liveTradingSituation.brokerSituations)
                        {
                            if (situation.kycForm instanceof KYCAyondoForm)
                            {
                                return new SignUpLiveAyondoPagerAdapter(fm, args);
                            }
                        }
                        throw new IllegalArgumentException("There is no available kycForm");
                    }
                });
    }
}
