package com.tradehero.th.api.quote;

import java.util.Date;
import retrofit.mime.TypedInput;

/** Created with IntelliJ IDEA. User: xavier Date: 10/7/13 Time: 4:26 PM To change this template use File | Settings | File Templates. */
public class QuoteDTO
{
    public int securityId;

    public Date asOfUtc;
    public Double bid;
    public Double ask;

    public String currencyISO;
    public String currencyDisplay;

    public boolean fromCache;
    public int quoteType;

    public Double toUSDRate;
    public String toUSDRateDate;

    public String timeStamp;

    // This part is used for the signature container that came back
    public String rawResponse;
}
