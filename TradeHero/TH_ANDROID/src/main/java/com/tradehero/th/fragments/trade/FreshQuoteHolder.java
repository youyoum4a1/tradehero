package com.tradehero.th.fragments.trade;

import android.os.CountDownTimer;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.api.SignatureContainer;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.service.QuoteService;
import com.tradehero.th.network.service.QuoteServiceUtil;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.io.IOException;
import java.io.StringWriter;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.apache.commons.io.IOUtils;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/** Created with IntelliJ IDEA. User: xavier Date: 10/8/13 Time: 4:49 PM To change this template use File | Settings | File Templates. */
public class FreshQuoteHolder
{
    public final static String TAG = FreshQuoteHolder.class.getSimpleName();

    public final static long DEFAULT_MILLI_SEC_QUOTE_REFRESH = 30000;
    public final static long DEFAULT_MILLI_SEC_QUOTE_COUNTDOWN_PRECISION = 50;

    private final SecurityId securityId;
    private final long milliSecQuoteRefresh;
    private final long millisecQuoteCountdownPrecision;
    private final List<WeakReference<FreshQuoteListener>> listeners; // TODO weak references?
    private CountDownTimer nextQuoteCountDownTimer;
    private boolean refreshing = false;
    public String identifier = "noId";

    @Inject protected Lazy<QuoteService> quoteService;

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
            QuoteServiceUtil.getQuote(quoteService.get(), securityId, createCallback());
        }
    }

    private Callback<SignatureContainer<QuoteDTO>> createCallback()
    {
        return  new retrofit.Callback<SignatureContainer<QuoteDTO>>()
        {
            @Override public void success(SignatureContainer<QuoteDTO> signedQuoteDTO, Response response)
            {
                notifyNotRefreshing();
                handleReceivedQuote(signedQuoteDTO, response);
            }

            @Override public void failure(RetrofitError error)
            {
                THLog.e(TAG, "Failed to get quote", error);
                notifyNotRefreshing();
            }

            private void notifyNotRefreshing()
            {
                refreshing = false;
                notifyListenersRefreshing();
            }
        };
    }

    private void handleReceivedQuote(SignatureContainer<QuoteDTO> signedQuoteDTO, Response response)
    {
        QuoteDTO quoteDTO = null;
        if (signedQuoteDTO != null && signedQuoteDTO.signedObject != null)
        {
            try
            {
                StringWriter writer = new StringWriter();
                IOUtils.copy(response.getBody().in(), writer, "UTF-8");
                signedQuoteDTO.signedObject.rawResponse = writer.toString();
                quoteDTO = signedQuoteDTO.signedObject;
            }
            catch (IOException e)
            {
                THLog.e(TAG, "Failed to get signature", e);
            }
        }
        notifyListenersOnFreshQuote(quoteDTO);
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
}
