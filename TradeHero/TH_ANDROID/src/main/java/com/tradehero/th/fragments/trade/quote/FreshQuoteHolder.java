package com.tradehero.th.fragments.trade.quote;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.rx.os.CountDownTimerOperator;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

public class FreshQuoteHolder
{
    public final static long DEFAULT_MILLI_SEC_QUOTE_REFRESH = 30000;
    public final static long DEFAULT_MILLI_SEC_QUOTE_COUNTDOWN_PRECISION = 50;

    @NonNull private final SecurityId securityId;
    private final long milliSecQuoteRefresh;
    private final long millisecQuoteCountdownPrecision;

    @Inject QuoteServiceWrapper quoteServiceWrapper;
    @NonNull private BehaviorSubject<FreshQuoteInfo> sender;
    @Nullable private Subscription countDownSubscription;
    @Nullable private Subscription quoteSubscription;

    //<editor-fold desc="Constructors">
    public FreshQuoteHolder(@NonNull Context context, @NonNull SecurityId securityId)
    {
        this(context, securityId, DEFAULT_MILLI_SEC_QUOTE_REFRESH, DEFAULT_MILLI_SEC_QUOTE_COUNTDOWN_PRECISION);
    }

    public FreshQuoteHolder(@NonNull Context context,
            @NonNull SecurityId securityId,
            long milliSecQuoteRefresh,
            long millisecQuoteCountdownPrecision)
    {
        this.securityId = securityId;
        this.milliSecQuoteRefresh = milliSecQuoteRefresh;
        this.millisecQuoteCountdownPrecision = millisecQuoteCountdownPrecision;
        HierarchyInjector.inject(context, this);
        sender = BehaviorSubject.create(new FreshQuoteInfo(true));
    }
    //</editor-fold>

    public Observable<FreshQuoteInfo> startObs()
    {
        return sender.doOnSubscribe(this::refreshQuoteObs)
                .doOnUnsubscribe(this::clearSubscriptions)
                .publish().refCount();
    }

    protected void startCountingDown()
    {
        Timber.d("FreshQuote startCounting");
        unsubscribe(countDownSubscription);
        countDownSubscription = Observable.create(new CountDownTimerOperator(milliSecQuoteRefresh, millisecQuoteCountdownPrecision))
                .subscribeOn(AndroidSchedulers.mainThread())
                .doOnNext(tick -> sender.onNext(new FreshQuoteInfo(tick)))
                .map(FreshQuoteInfo::new)
                .subscribe(new EmptyObserver<FreshQuoteInfo>()
                {
                    @Override public void onNext(FreshQuoteInfo args)
                    {
                        sender.onNext(args);
                    }

                    @Override public void onCompleted()
                    {
                        refreshQuoteObs();
                    }

                    @Override public void onError(Throwable e)
                    {
                        sender.onError(e);
                    }
                });
    }

    private void refreshQuoteObs()
    {
        Timber.d("FreshQuote refreshQuote");
        sender.onNext(new FreshQuoteInfo(true));
        unsubscribe(quoteSubscription);
        quoteSubscription = quoteServiceWrapper.getQuoteRx(securityId)
                .map(FreshQuoteInfo::new)
                .subscribe(new EmptyObserver<FreshQuoteInfo>()
                {
                    @Override public void onNext(FreshQuoteInfo args)
                    {
                        sender.onNext(args);
                    }

                    @Override public void onCompleted()
                    {
                        // TODO conditional if there are subscribers
                        startCountingDown();
                    }

                    @Override public void onError(Throwable e)
                    {
                        sender.onError(e);
                    }
                });
    }

    private void clearSubscriptions()
    {
        unsubscribe(quoteSubscription);
        unsubscribe(countDownSubscription);
    }

    private void unsubscribe(@Nullable Subscription subscription)
    {
        if (subscription != null)
        {
            subscription.unsubscribe();
        }
    }
}
