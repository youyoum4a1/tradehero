package com.tradehero.th.network.service;

import android.os.Handler;
import android.util.Log;

import com.tradehero.chinabuild.data.KLineItem;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.data.SignedQuote;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

@Singleton
public class QuoteServiceWrapper {
    public static final int DEFAULT_REFRESH_QUOTE_DETAIL_DELAY = 10 * 1000;

    public static final int DEFAULT_REFRESH_QUOTE_TICKS_DELAY = 30 * 1000;

    private static final int DEFAULT_REFRESH_QUOTE_DELAY = 10 * 1000;

    public static final int MAX_API_RETRIES = 5;

    public static final String K_LINE_DAY = "day";
    public static final String K_LINE_WEEK = "week";
    public static final String K_LINE_MONTH = "month";


    @NotNull private final QuoteService quoteService;

    private Handler handler;

    private Runnable quoteDetailTask;

    private Runnable quoteTicksTask;

    private Runnable quoteTask;

    @Inject
    public QuoteServiceWrapper(
            @NotNull QuoteService quoteService) {
        super();
        this.quoteService = quoteService;
        handler = new Handler();
    }

    private void basicCheck(SecurityId securityId) {
        if (securityId == null) {
            throw new NullPointerException("securityId cannot be null");
        }
        if (securityId.getExchange() == null) {
            throw new NullPointerException("securityId.getExchange() cannot be null");
        }
        if (securityId.getSecuritySymbol() == null) {
            throw new NullPointerException("securityId.getSecuritySymbol() cannot be null");
        }
    }

    public BaseMiddleCallback<Response> getRawQuote(SecurityId securityId, Callback<Response> callback) {
        BaseMiddleCallback<Response> middleCallback = new BaseMiddleCallback<>(callback);
        basicCheck(securityId);
        this.quoteService.getRawQuote(UrlEncoderHelper.transform(securityId.getExchange()), UrlEncoderHelper.transform(
                securityId.getSecuritySymbol()), middleCallback);
        return middleCallback;
    }
    //</editor-fold>

    public void getRepeatingQuoteDetails(final String securitySymbol, final Callback<QuoteDetail> callback) {
        final RepeatingTaskCallBack<QuoteDetail> myCallback = new RepeatingTaskCallBack<>(callback, handler, DEFAULT_REFRESH_QUOTE_DETAIL_DELAY);
        if (quoteDetailTask != null) {
            handler.removeCallbacks(quoteDetailTask);
        }
        quoteDetailTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getQuoteDetails(securitySymbol, myCallback);
            }
        };
        myCallback.setTask(quoteDetailTask);
        handler.post(quoteDetailTask);

    }

    public void getQuoteTicks(final String securitySymbol, final int quoteTicksDelay, final Callback<List<QuoteTick>> callback) {
        final RepeatingTaskCallBack<List<QuoteTick>> myCallback = new RepeatingTaskCallBack<>(callback, handler, quoteTicksDelay);
        if (quoteTicksTask != null) {
            handler.removeCallbacks(quoteTicksTask);
        }
        quoteTicksTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getQuoteTicks(securitySymbol, myCallback);
            }
        };
        myCallback.setTask(quoteTicksTask);
        handler.post(quoteTicksTask);
    }

    public void getQuoteTicks(final String securitySymbol, final Callback<List<QuoteTick>> callback) {
        getQuoteTicks(securitySymbol, DEFAULT_REFRESH_QUOTE_TICKS_DELAY, callback);
    }

    public void getRepeatingQuote(final String securitySymbol, final Callback<SignedQuote> callback) {
        final RepeatingTaskCallBack<SignedQuote> myCallback = new RepeatingTaskCallBack<>(callback, handler, DEFAULT_REFRESH_QUOTE_DELAY);
        if (quoteTask != null) {
            handler.removeCallbacks(quoteTask);
        }
        quoteTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getQuote(securitySymbol, myCallback);
            }
        };
        myCallback.setTask(quoteTask);
        handler.post(quoteTask);
    }

    public void getQuote(final String securitySymbol, final Callback<SignedQuote> callback) {
        quoteService.getQuote(securitySymbol, callback);
    }

    public void getKline(final String securitySymbol, final String type, final Callback<List<KLineItem>> callback) {
        quoteService.getKLines(securitySymbol, type, callback);
    }



    public void stopQuoteDetailTask() {
        if (quoteDetailTask != null) {
            handler.removeCallbacks(quoteDetailTask);
            Log.e("test", "Stop quoteDetailTask................");
        }
    }

    public void stopQuoteTicksTask() {
        if (quoteTicksTask != null) {
            handler.removeCallbacks(quoteTicksTask);
            Log.e("test", "Stop quoteTicksTask................");
        }
    }

    public void stopQuoteTask() {
        if (quoteTask != null) {
            handler.removeCallbacks(quoteTask);
            Log.e("test", "Stop quoteTask................");
        }
    }

    static class RepeatingTaskCallBack<T> implements Callback<T> {
        private Runnable task;
        private Callback<T> callback;
        private Handler handler;
        private int delay;
        private int failureCount;

        public RepeatingTaskCallBack(Callback<T> callback, Handler handler, int delay) {
            this.callback = callback;
            this.handler = handler;
            this.delay = delay;
        }

        public void setTask(Runnable task) {
            this.task = task;
        }


        @Override
        public void success(T t, Response response) {
            if (callback != null) {
                callback.success(t, response);
            }
            handler.postDelayed(task, delay);
        }

        @Override
        public void failure(RetrofitError error) {
            if (failureCount < MAX_API_RETRIES) {
                Log.e("test", "failureCount: " + failureCount);
                handler.postDelayed(task, delay);
                failureCount++;
            }

            if (callback != null) {
                callback.failure(error);
            }
        }
    }
}
