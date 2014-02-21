package com.tradehero.th.fragments.trade;

import android.os.CountDownTimer;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.QuoteServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import retrofit.mime.TypedInput;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 4:49 PM To change this template use File | Settings | File Templates. */
public class FreshQuoteHolder
{
    public final static long DEFAULT_MILLI_SEC_QUOTE_REFRESH = 30000;
    public final static long DEFAULT_MILLI_SEC_QUOTE_COUNTDOWN_PRECISION = 50;

    private final SecurityId securityId;
    private final long milliSecQuoteRefresh;
    private final long millisecQuoteCountdownPrecision;
    private final List<WeakReference<FreshQuoteListener>> listeners;
    private CountDownTimer nextQuoteCountDownTimer;
    private boolean refreshing = false;
    public String identifier = "noId";

    @Inject Converter converter;
    @Inject Lazy<QuoteServiceWrapper> quoteServiceWrapper;

    //<editor-fold desc="Constructors">
    public FreshQuoteHolder(SecurityId securityId)
    {
        this(securityId, DEFAULT_MILLI_SEC_QUOTE_REFRESH, DEFAULT_MILLI_SEC_QUOTE_COUNTDOWN_PRECISION);
    }

    public FreshQuoteHolder(SecurityId securityId, long milliSecQuoteRefresh, long millisecQuoteCountdownPrecision)
    {
        this.securityId = securityId;
        this.milliSecQuoteRefresh = milliSecQuoteRefresh;
        this.millisecQuoteCountdownPrecision = millisecQuoteCountdownPrecision;
        this.listeners = new ArrayList<>();
        DaggerUtils.inject(this);
    }
    //</editor-fold>

    //<editor-fold desc="Accessors">
    public long getMillisecQuoteCountdownPrecision()
    {
        return millisecQuoteCountdownPrecision;
    }

    public long getMilliSecQuoteRefresh()
    {
        return milliSecQuoteRefresh;
    }

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
    public boolean hasListener(FreshQuoteListener listener)
    {
        if (listener != null)
        {
            for (WeakReference<FreshQuoteListener> weakListener : listeners)
            {
                if (weakListener != null && weakListener.get() == listener)
                {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * The listener should be strongly referenced elsewhere
     * @param listener
     */
    public void registerListener(FreshQuoteListener listener)
    {
        if (listener != null && !hasListener(listener))
        {
            listeners.add(new WeakReference<>(listener));
        }
    }

    public void unRegisterListener(FreshQuoteListener listener)
    {
        for (WeakReference<FreshQuoteListener> weakListener: listeners)
        {
            if (weakListener.get() == listener)
            {
                listeners.remove(weakListener);
                break; // To avoid async
            }
        }
    }

    private void notifyListenersCountDown(long milliSecToRefresh)
    {
        FreshQuoteListener listener;
        for (WeakReference<FreshQuoteListener> weakListener: listeners)
        {
            listener = weakListener.get();
            if (listener != null)
            {
                listener.onMilliSecToRefreshQuote(milliSecToRefresh);
            }
        }
    }

    private void notifyListenersRefreshing()
    {
        FreshQuoteListener listener;
        for (WeakReference<FreshQuoteListener> weakListener: listeners)
        {
            listener = weakListener.get();
            if (listener != null)
            {
                listener.onIsRefreshing(refreshing);
            }
        }
    }

    private void notifyListenersOnFreshQuote(QuoteDTO quoteDTO)
    {
        FreshQuoteListener listener;
        for (WeakReference<FreshQuoteListener> weakListener: listeners)
        {
            listener = weakListener.get();
            if (listener != null)
            {
                listener.onFreshQuote(quoteDTO);
            }
        }
    }
    //</editor-fold>

    public void start()
    {
        refreshQuote();
    }

    private void refreshQuote()
    {
        if (this.securityId != null && !refreshing)
        {
            refreshing = true;
            notifyListenersRefreshing();
            quoteServiceWrapper.get().getRawQuote(securityId, createCallbackForRawResponse());
        }
    }

    private Callback<Response> createCallbackForRawResponse()
    {
        return  new retrofit.Callback<Response>()
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
                notifyListenersRefreshing();
            }
        };
    }

    @SuppressWarnings("unchecked")
    private void handleReceivedQuote(Response response)
    {
        QuoteDTO quoteDTO = null;
        TypedInput body = response.getBody();
        InputStream is = null;
        try
        {
            // TODO this thing should not be done in UI thread :(
            is = body.in();
            byte[] bodyBytes = IOUtils.streamToBytes(is);

            body = new TypedByteArray(body.mimeType(), bodyBytes);

            QuoteSignatureContainer signatureContainer = (QuoteSignatureContainer) converter.fromBody(body, QuoteSignatureContainer.class);
            quoteDTO = signatureContainer.signedObject;
            quoteDTO.rawResponse = new String(bodyBytes);
        }
        catch (Exception ex)
        {
            THToast.show(R.string.error_fetch_quote);
        }
        finally
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

        notifyListenersOnFreshQuote(quoteDTO);
        scheduleNextQuoteRequest();
    }

    //<editor-fold desc="Handle callback from quote service which return SignatureContainer<QuoteDTO>">
    //private Callback<SignatureContainer<QuoteDTO>> createCallback()
    //{
    //    return  new retrofit.Callback<SignatureContainer<QuoteDTO>>()
    //    {
    //        @Override public void success(SignatureContainer<QuoteDTO> signedQuoteDTO, Response response)
    //        {
    //            notifyNotRefreshing();
    //            handleReceivedQuote(signedQuoteDTO, response);
    //        }
    //
    //        @Override public void failure(RetrofitError error)
    //        {
    //            Timber.e("Failed to get quote", error);
    //            notifyNotRefreshing();
    //        }
    //
    //        private void notifyNotRefreshing()
    //        {
    //            refreshing = false;
    //            notifyListenersRefreshing();
    //        }
    //    };
    //}
    //
    //private void handleReceivedQuote(SignatureContainer<QuoteDTO> signedQuoteDTO, Response response)
    //{
    //    QuoteDTO quoteDTO = null;
    //    if (signedQuoteDTO != null && signedQuoteDTO.signedObject != null)
    //    {
    //        quoteDTO = signedQuoteDTO.signedObject;
    //
    //        TypedInput body = response.getBody();
    //
    //        if (body instanceof TypedByteArray)
    //        {
    //            signedQuoteDTO.signedObject.rawResponse = new String(((TypedByteArray)body).getBytes());
    //        }
    //        else
    //        {
    //            InputStream is = null;
    //            try
    //            {
    //                if (body != null && body.mimeType() != null)
    //                {
    //                    is = body.in();
    //                    byte[] responseBytes = IOUtils.streamToBytes(is);
    //                    signedQuoteDTO.signedObject.rawResponse = new String(responseBytes);
    //                }
    //            }
    //            catch (IOException e)
    //            {
    //                Timber.e("Failed to get signature", e);
    //            }
    //            finally
    //            {
    //                if (is != null)
    //                {
    //                    try
    //                    {
    //                        is.close();
    //                    }
    //                    catch (IOException ignored)
    //                    {
    //                    }
    //                }
    //            }
    //        }
    //    }
    //    notifyListenersOnFreshQuote(quoteDTO);
    //    scheduleNextQuoteRequest();
    //}
    //</editor-fold>

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
                notifyListenersCountDown(millisUntilFinished);
            }

            @Override public void onFinish()
            {
                refreshQuote();
            }
        };
        nextQuoteCountDownTimer.start();
    }

    public void cancel()
    {
        if (nextQuoteCountDownTimer != null)
        {
            nextQuoteCountDownTimer.cancel();
        }
        nextQuoteCountDownTimer = null;
        listeners.clear();
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

    private static class QuoteSignatureContainer extends SignatureContainer<QuoteDTO> { }
}
