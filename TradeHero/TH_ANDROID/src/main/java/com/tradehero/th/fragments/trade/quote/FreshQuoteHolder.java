package com.tradehero.th.fragments.trade.quote;

import android.content.Context;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.rx.os.CountDownTimerOperator;
import java.io.IOException;
import java.io.InputStream;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import rx.Observable;
import rx.Subscription;
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
    private FreshQuoteListener quoteListener;
    private CountDownTimer nextQuoteCountDownTimer;
    private boolean refreshing = false;

    @Inject Converter converter;
    @Inject QuoteServiceWrapper quoteServiceWrapper;
    private MiddleCallback<Response> quoteMiddleCallback;
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

    public void destroy()
    {
        detachQuoteMiddleCallback();
        if (nextQuoteCountDownTimer != null)
        {
            nextQuoteCountDownTimer.cancel();
        }
        nextQuoteCountDownTimer = null;
        quoteListener = null;
    }

    private void detachQuoteMiddleCallback()
    {
        if (quoteMiddleCallback != null)
        {
            quoteMiddleCallback.setPrimaryCallback(null);
        }
        quoteMiddleCallback = null;
    }

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

    //<editor-fold desc="Accessors">
    public SecurityId getSecurityId()
    {
        return securityId;
    }

    public boolean isRefreshing()
    {
        return refreshing;
    }
    //</editor-fold>

    //<editor-fold desc="Listener Handling">
    public void setListener(FreshQuoteListener listener)
    {
        this.quoteListener = listener;
    }

    private void notifyListenerCountDown(long milliSecToRefresh)
    {
        FreshQuoteListener listener = quoteListener;
        if (listener != null)
        {
            listener.onMilliSecToRefreshQuote(milliSecToRefresh);
        }
    }

    private void notifyListenerRefreshing()
    {
        FreshQuoteListener listener = quoteListener;
        if (listener != null)
        {
            listener.onIsRefreshing(refreshing);
        }
    }

    private void notifyListenerOnFreshQuote(QuoteDTO quoteDTO)
    {
        FreshQuoteListener listener = quoteListener;
        if (listener != null)
        {
            listener.onFreshQuote(quoteDTO);
        }
    }
    //</editor-fold>

    public void start()
    {
        refreshQuote();
    }

    private void refreshQuote()
    {
        if (!refreshing)
        {
            refreshing = true;
            notifyListenerRefreshing();
            detachQuoteMiddleCallback();
            quoteMiddleCallback = quoteServiceWrapper.getRawQuote(securityId, createCallbackForRawResponse());
        }
    }

    private Callback<Response> createCallbackForRawResponse()
    {
        return new FreshQuoteHolderResponseCallback();
    }

    @SuppressWarnings("unchecked")
    private void handleReceivedQuote(Response response)
    {
        QuoteDTO quoteDTO = null;
        TypedInput body = response.getBody();
        InputStream is = null;
        try
        {
            if (body != null && body.mimeType() != null)
            {
                if (!(body instanceof TypedByteArray))
                {
                    // TODO this thing should not be done in UI thread :(
                    is = body.in();
                    byte[] bodyBytes = IOUtils.streamToBytes(is);

                    body = new TypedByteArray(body.mimeType(), bodyBytes);
                }

                QuoteSignatureContainer signatureContainer = (QuoteSignatureContainer) converter.fromBody(body, QuoteSignatureContainer.class);
                if (signatureContainer != null)
                {
                    quoteDTO = signatureContainer.signedObject;
                    if (quoteDTO != null)
                    {
                        quoteDTO.rawResponse = new String(((TypedByteArray) body).getBytes());
                    }
                }
            }
        }
        catch (Exception ex)
        {
            THToast.show(R.string.error_fetch_quote);
        } finally
        {
            if (is != null)
            {
                try
                {
                    is.close();
                }
                catch (IOException ignored)
                {
                }
            }
        }

        notifyListenerOnFreshQuote(quoteDTO);
        scheduleNextQuoteRequest();
    }

    private void scheduleNextQuoteRequest()
    {
        if (nextQuoteCountDownTimer != null)
        {
            nextQuoteCountDownTimer.cancel();
        }
        nextQuoteCountDownTimer = new CountDownTimer(milliSecQuoteRefresh, millisecQuoteCountdownPrecision)
        {
            @Override public void onTick(long millisUntilFinished)
            {
                notifyListenerCountDown(millisUntilFinished);
            }

            @Override public void onFinish()
            {
                refreshQuote();
            }
        };
        nextQuoteCountDownTimer.start();
    }

    /**
     * Implementers should be strongly referenced elsewhere because the FreshQuoteHolder only keeps weak references.
     */
    public static interface FreshQuoteListener
    {
        void onMilliSecToRefreshQuote(long milliSecToRefresh);

        void onIsRefreshing(boolean refreshing);

        void onFreshQuote(QuoteDTO quoteDTO);
    }

    private static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO>
    {
    }

    protected class FreshQuoteHolderResponseCallback implements Callback<Response>
    {
        @Override public void success(Response response, Response dumpResponse)
        {
            notifyNotRefreshing();
            handleReceivedQuote(response);
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.e("Failed to get quote", error);
            notifyNotRefreshing();
        }

        private void notifyNotRefreshing()
        {
            refreshing = false;
            notifyListenerRefreshing();
        }
    }
}
