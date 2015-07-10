package com.tradehero.th.network.service;

import android.os.Handler;

import com.tradehero.chinabuild.data.KLineItem;
import com.tradehero.chinabuild.data.QuoteDetail;
import com.tradehero.chinabuild.data.QuoteTick;
import com.tradehero.chinabuild.data.SecurityUserOptDTO;
import com.tradehero.chinabuild.data.SecurityUserPositionDTO;
import com.tradehero.chinabuild.data.SignedQuote;
import com.tradehero.chinabuild.fragment.security.SecurityOptPositionsList;
import com.tradehero.common.utils.IOUtils;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.api.security.SecurityId;
import com.tradehero.th.network.UrlEncoderHelper;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.utils.DaggerUtils;

import org.jetbrains.annotations.NotNull;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Singleton;

import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.Converter;
import retrofit.mime.TypedByteArray;
import timber.log.Timber;

@Singleton
public class QuoteServiceWrapper {
    public static final int DEFAULT_REFRESH_QUOTE_DETAIL_DELAY = 10 * 1000;

    public static final int DEFAULT_REFRESH_QUOTE_TICKS_DELAY = 30 * 1000;

    private static final int DEFAULT_REFRESH_QUOTE_DELAY = 10 * 1000;

    private static final int DEFAULT_REFRESH_SECURITY_DELAY = 60 * 1000;

    public static final int MAX_API_RETRIES = 5;

    public static final String K_LINE_DAY = "day";
    public static final String K_LINE_WEEK = "week";
    public static final String K_LINE_MONTH = "month";


    @NotNull private final QuoteService quoteService;

    private Handler handler;

    private Runnable quoteDetailTask;

    private Runnable quoteTicksTask;

    private Runnable quoteTask;

    private Runnable securityCompactTask;

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

    public void getRepeatingQuoteDetails(final SecurityId securityId, final Callback<QuoteDetail> callback) {
        final RepeatingTaskCallBack<QuoteDetail> myCallback = new RepeatingTaskCallBack<>(callback, handler, DEFAULT_REFRESH_QUOTE_DETAIL_DELAY);
        if (quoteDetailTask != null) {
            handler.removeCallbacks(quoteDetailTask);
        }
        quoteDetailTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getQuoteDetails(securityId.getExchange(), securityId.getSecuritySymbol(), myCallback);
            }
        };
        myCallback.setTask(quoteDetailTask);
        handler.post(quoteDetailTask);

    }

    public void getQuoteDetails(final SecurityId securityId, final Callback<QuoteDetail> callback) {
        quoteService.getQuoteDetails(securityId.getExchange(), securityId.getSecuritySymbol(), callback);
    }

    public void getQuoteDetails(final String securityExchange, final String securitySymbol, final Callback<QuoteDetail> callback) {
        quoteService.getQuoteDetails(securityExchange, securitySymbol, callback);
    }

    public void retrieveMainPositions(Callback<SecurityOptPositionsList> callback){
        quoteService.retrieveMainPositions(callback);
    }

    public void getQuoteTicks(final SecurityId securityId, final int quoteTicksDelay, final Callback<List<QuoteTick>> callback) {
        final RepeatingTaskCallBack<List<QuoteTick>> myCallback = new RepeatingTaskCallBack<>(callback, handler, quoteTicksDelay);
        if (quoteTicksTask != null) {
            handler.removeCallbacks(quoteTicksTask);
        }
        quoteTicksTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getQuoteTicks(securityId.getExchange(), securityId.getSecuritySymbol(), myCallback);
            }
        };
        myCallback.setTask(quoteTicksTask);
        handler.post(quoteTicksTask);
    }

    public void getQuoteTicks(final SecurityId securityId, final Callback<List<QuoteTick>> callback) {
        getQuoteTicks(securityId, DEFAULT_REFRESH_QUOTE_TICKS_DELAY, callback);
    }

    public void getRepeatingQuote(final SecurityId securityId, final Callback<QuoteDTO> callback) {
        int delay = DEFAULT_REFRESH_QUOTE_DELAY;
        if (!isChinaStock(securityId)) {
            delay = DEFAULT_REFRESH_SECURITY_DELAY;
        }
        final QuoteDTORepeatingTaskCallBack myCallback = new QuoteDTORepeatingTaskCallBack(callback, handler, delay);
        if (quoteTask != null) {
            handler.removeCallbacks(quoteTask);
        }
        if (isChinaStock(securityId)) {
            quoteTask = new Runnable() {
                @Override
                public void run() {
                    quoteService.getQuote(securityId.getExchange(), securityId.getSecuritySymbol(), myCallback);
                }
            };
        } else {
            quoteTask = new Runnable() {
                @Override
                public void run() {
                    quoteService.getQuoteLegacy(securityId.getExchange(),
                            securityId.getSecuritySymbol(), myCallback);
                }
            };
        }
        myCallback.setTask(quoteTask);
        handler.post(quoteTask);
    }

    public static boolean isChinaStock(final SecurityId securityId) {
        if (securityId == null) {
            return false;
        }
        return  Exchange.SHA.name().equals(securityId.getExchange())
                || Exchange.SHE.name().equals(securityId.getExchange());

    }

    public void getQuote(final SecurityId securityId, final Callback<QuoteDTO> callback) {
        QuoteDTOCallBack myCallback = new QuoteDTOCallBack(callback);
        if (isChinaStock(securityId)) {
            quoteService.getQuote(securityId.getExchange(), securityId.getSecuritySymbol(), myCallback);
        } else {
            quoteService.getQuoteLegacy(securityId.getExchange(),
                    securityId.getSecuritySymbol(), myCallback);
        }
    }

    public void getKline(final SecurityId securityId, final String type, final Callback<List<KLineItem>> callback) {
        quoteService.getKLines(securityId.getExchange(), securityId.getSecuritySymbol(), type, callback);
    }

    public void getRepeatingSecurityCompactDTO(final SecurityId securityId, final Callback<SecurityCompactDTO> callback) {
        final RepeatingTaskCallBack<SecurityCompactDTO> myCallback = new RepeatingTaskCallBack<>(callback, handler, DEFAULT_REFRESH_SECURITY_DELAY);
        if (securityCompactTask != null) {
            handler.removeCallbacks(securityCompactTask);
        }

        securityCompactTask = new Runnable() {
            @Override
            public void run() {
                quoteService.getSecurityCompactDTO(securityId.getExchange(), securityId.getSecuritySymbol(), myCallback);
            }
        };

        myCallback.setTask(securityCompactTask);
        handler.post(securityCompactTask);
    }

    public void getSecurityCompactDTO(final SecurityId securityId, final Callback<SecurityCompactDTO> callback) {
        quoteService.getSecurityCompactDTO(securityId.getExchange(), securityId.getSecuritySymbol(), callback);
    }

    public void getTradeRecords(final SecurityId securityId, int page, int perPage, Callback<List<SecurityUserOptDTO>> callback) {
        quoteService.getTradeRecords(securityId.getExchange(),
                securityId.getSecuritySymbol(),
                page,
                perPage,
                callback);
    }

    public void getSharePosition(final SecurityId securityId, int page, int perPage, Callback<List<SecurityUserPositionDTO>> callback) {
        quoteService.getSharePositions(securityId.getExchange(), securityId.getSecuritySymbol(), page, perPage, callback);
    }

    public void stopSecurityCompactTask() {
        if (securityCompactTask != null) {
            handler.removeCallbacks(securityCompactTask);
            Timber.e("Stop SecurityCompactTask................");
        }
    }

    public void stopQuoteDetailTask() {
        if (quoteDetailTask != null) {
            handler.removeCallbacks(quoteDetailTask);
            Timber.e("Stop quoteDetailTask................");
        }
    }

    public void stopQuoteTicksTask() {
        if (quoteTicksTask != null) {
            handler.removeCallbacks(quoteTicksTask);
            Timber.e("Stop quoteTicksTask................");
        }
    }

    public void stopQuoteTask() {
        if (quoteTask != null) {
            handler.removeCallbacks(quoteTask);
            Timber.e("Stop quoteTask................");
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
                Timber.e("failureCount: " + failureCount);
                handler.postDelayed(task, delay);
                failureCount++;
            }

            if (callback != null) {
                callback.failure(error);
            }
        }
    }

    public static class QuoteDTORepeatingTaskCallBack implements Callback<Response> {
        @Inject Converter converter;

        private Runnable task;
        private Callback<QuoteDTO> callback;
        private Handler handler;
        private int delay;
        private int failureCount;

        public QuoteDTORepeatingTaskCallBack(Callback<QuoteDTO> callback, Handler handler, int delay) {
            this.callback = callback;
            this.handler = handler;
            this.delay = delay;
            DaggerUtils.inject(this);
        }

        public void setTask(Runnable task) {
            this.task = task;
        }

        @Override
        public void success(Response rawResponse, Response response) {
            try {
                byte[] bytes = IOUtils.streamToBytes(rawResponse.getBody().in());
                SignedQuote signedQuote = (SignedQuote) converter.fromBody(new TypedByteArray(rawResponse.getBody().mimeType(), bytes), SignedQuote.class);
                QuoteDTO quoteDTO = signedQuote.signedObject;
                quoteDTO.rawResponse = new String(bytes);
                if (callback != null) {
                    callback.success(quoteDTO, response);
                }
            } catch (Exception e) {
                Timber.e(e, "Error in parsing retrofit response.");
            }

            handler.postDelayed(task, delay);
        }

        @Override
        public void failure(RetrofitError error) {
            if (failureCount < MAX_API_RETRIES) {
                Timber.e("failureCount: " + failureCount);
                handler.postDelayed(task, delay);
                failureCount++;
            }

            if (callback != null) {
                callback.failure(error);
            }
        }
    }

    public static class QuoteDTOCallBack implements Callback<Response> {
        @Inject Converter converter;
        private Callback<QuoteDTO> callback;
        public QuoteDTOCallBack(Callback<QuoteDTO> callback) {
            this.callback = callback;
            DaggerUtils.inject(this);
        }

        @Override
        public void success(Response rawResponse, Response response) {
            try {
                byte[] bytes = IOUtils.streamToBytes(rawResponse.getBody().in());
                SignedQuote signedQuote = (SignedQuote) converter.fromBody(new TypedByteArray(rawResponse.getBody().mimeType(), bytes), SignedQuote.class);
                QuoteDTO quoteDTO = signedQuote.signedObject;
                quoteDTO.rawResponse = new String(bytes);
                if (callback != null) {
                    callback.success(quoteDTO, response);
                }
            } catch (Exception e) {
                Timber.e(e, "Error in parsing retrofit response.");
            }
        }

        @Override
        public void failure(RetrofitError error) {
            if (callback != null) {
                callback.failure(error);
            }
        }
    }
}
