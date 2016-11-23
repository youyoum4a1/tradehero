package com.androidth.general.models.live;

import android.app.Activity;
import android.icu.util.TimeUnit;
import android.support.annotation.NonNull;
import android.util.Log;

import com.androidth.general.common.persistence.RealmManager;
import com.androidth.general.fragments.security.LiveQuoteDTO;
import com.androidth.general.fragments.security.SignatureContainer2;
import com.androidth.general.network.LiveNetworkConstants;
import com.androidth.general.network.service.QuoteServiceWrapper;
import com.androidth.general.network.service.SignalRManager;
import com.androidth.general.persistence.live.Live1BResponseDTO;

import microsoft.aspnet.signalr.client.hubs.SubscriptionHandler1;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action0;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;
import rx.subjects.Subject;

/**
 * Created by diana on 17/11/16.
 */

public class THPriceManager {

    private long securityIdNumber;
//    private LiveQuoteDTO liveQuoteDTO;
    private int virtualPricingPollIntervalSecs; // if >0 then poll, else signalR
    private BehaviorSubject<LiveQuoteDTO> liveQuoteDTOBehaviorSubject;
    private QuoteServiceWrapper quoteServiceWrapper;
    private Subscription liveQuoteSubscription;
    private SignalRManager signalRManager;
    private Boolean isFX;
    private Activity mActivity;

    // for FX
    public THPriceManager(String userCurrency, String securityCurrency, SignalRManager signalRManager, Activity mActivity)
    {
        liveQuoteDTOBehaviorSubject = BehaviorSubject.create();
        this.signalRManager = signalRManager;
        startLiveQuoteSignalRListening();
        InvokeFXSignalR(userCurrency,securityCurrency);
        this.isFX = true;
        this.mActivity = mActivity;
    }

    public long getSecurityIdNumber() { return securityIdNumber; }

    // for securities
    public THPriceManager(long securityIdNumber,
                          int virtualPricingPollIntervalSecs,
                          QuoteServiceWrapper quoteServiceWrapper,
                          SignalRManager signalRManager,
                          Activity mActivity)
    {
        this.securityIdNumber = securityIdNumber;
        liveQuoteDTOBehaviorSubject = BehaviorSubject.create();
        this.virtualPricingPollIntervalSecs = virtualPricingPollIntervalSecs;
        this.quoteServiceWrapper = quoteServiceWrapper;
        this.signalRManager = signalRManager;
        this.isFX = false;
        this.mActivity = mActivity;

        if(this.virtualPricingPollIntervalSecs>0)
        {

            startLiveQuotePollingSubscription();
        }
        else
        {
            Log.v(".java","THPriceManager invoking non FX");
            startLiveQuoteSignalRListening();
            InvokeLiveQuoteSignalR();
        }
    }
    private void startLiveQuoteSignalRListening()
    {
        Log.v("SignalRListen.java","Start listening THPriceManager signalR");
        if(signalRManager!=null) {
            signalRManager.getCurrentProxy().on("UpdateQuote", new SubscriptionHandler1<SignatureContainer2>() {

                @Override
                public void run(SignatureContainer2 signatureContainer2) {
                    Log.v("hmm.java","SIGNALR THPriceManager SignatureContainer = " + signatureContainer2.signedObject);
                    mActivity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LiveQuoteDTO liveQuote = signatureContainer2.signedObject;
                            if (signatureContainer2.signedObject == null || signatureContainer2.signedObject.id == 121234) {
                                return;
                            } else {

                                Log.v("SignalRListen.java", "Fra THPriceManager signalR liveQuote= " + liveQuote + ", isFX = " + isFX);
                                liveQuoteDTOBehaviorSubject.onNext(liveQuote);
                            }
                        }
                    });


                }
            }, SignatureContainer2.class);
        }
    }

    private void startLiveQuotePollingSubscription()
    {
        Log.v("haha.java","THPriceManager getPollingLiveQuote subscribing...");
        liveQuoteSubscription = getPollingLiveQuote()
                .delay(virtualPricingPollIntervalSecs, java.util.concurrent.TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .distinctUntilChanged()
                .doOnError(
                        throwable -> Log.v("Error.java","THPriceManager error: " + throwable.toString())
                )
                .doOnNext(new Action1<LiveQuoteDTO>() {
                    @Override
                    public void call(@NonNull LiveQuoteDTO liveQuoteDTO) {
                        Log.v("THPM.java","isfx = " + isFX + ", Fra THPriceManager API liveQuoteDTO " + liveQuoteDTO);
                        liveQuoteDTOBehaviorSubject.onNext(liveQuoteDTO);
                    }
                }).subscribe();
    }

    // this would have to be called for the particular securityId that changed,
    // then the view/whatever that subscribe to it will have to update itself
//    public void UpdateLiveQuoteChange(LiveQuoteDTO liveQuoteDTO)
//    {
//        this.securityIdNumber = liveQuoteDTO.id;
//    //    this.liveQuoteDTO = liveQuoteDTO;
//        liveQuoteDTOBehaviorSubject.onNext(liveQuoteDTO);
//    }

    @NonNull
    public Observable<LiveQuoteDTO> getLiveQuoteSubjectObservable(){

        return liveQuoteDTOBehaviorSubject.asObservable().distinctUntilChanged();
    }

    // get LiveQuote from api
    public Observable<LiveQuoteDTO> getPollingLiveQuote()
    {

        return quoteServiceWrapper.getQuoteRx((int)securityIdNumber);
    }

    public void InvokeFXSignalR(String userCurrency, String securityCurrency)
    {
        if(userCurrency==null) userCurrency = "USD";
        Log.v("SignalR","Invoking FXRates portfolioCompactDTO.currencyISO " + userCurrency + " securityCompactDTO.currencyISO = " + securityCurrency );

        signalRManager.startConnectionWithoutUserID(LiveNetworkConstants.PROXY_METHOD_FX_RATE,
                new String[] { userCurrency, securityCurrency } );

    }

    public void InvokeLiveQuoteSignalR()
    {
        Log.v("SignalR.java","THPriceManager livequote  "+ LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUP + ", securityID: " + this.securityIdNumber);
        signalRManager.startConnectionWithUserId(LiveNetworkConstants.PROXY_METHOD_ADD_TO_GROUP,
         Long.toString(this.securityIdNumber));
    }
}
