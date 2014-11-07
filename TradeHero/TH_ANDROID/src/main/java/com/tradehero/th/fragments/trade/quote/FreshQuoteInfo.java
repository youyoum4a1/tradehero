package com.tradehero.th.fragments.trade.quote;

import android.support.annotation.Nullable;
import com.tradehero.th.api.quote.QuoteDTO;
import com.tradehero.th.rx.os.CountDownTick;

public class FreshQuoteInfo
{
    public final boolean isRefreshing;
    @Nullable public final Long milliSecToRefresh;
    @Nullable public final QuoteDTO freshQuote;

    //<editor-fold desc="Constructors">
    public FreshQuoteInfo(boolean isRefreshing)
    {
        this(isRefreshing, null, null);
    }

    public FreshQuoteInfo(CountDownTick tick)
    {
        this(tick.millisUntilFinished);
    }

    public FreshQuoteInfo(Long milliSecToRefresh)
    {
        this(false, milliSecToRefresh, null);
    }

    public FreshQuoteInfo(QuoteDTO freshQuote)
    {
        this(false, null, freshQuote);
    }

    protected FreshQuoteInfo(boolean isRefreshing,
            @Nullable Long milliSecToRefresh,
            @Nullable QuoteDTO freshQuote)
    {
        this.isRefreshing = isRefreshing;
        this.milliSecToRefresh = milliSecToRefresh;
        this.freshQuote = freshQuote;
    }
    //</editor-fold>
}
