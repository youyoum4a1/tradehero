package com.tradehero.th.api.quote;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.tradehero.th.api.RawResponseKeeper;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import java.util.Date;
import timber.log.Timber;

public class QuoteDTO implements RawResponseKeeper
{
    public static final int QUOTE_TYPE_YAHOO_CSV = 1;
    public static final int QUOTE_TYPE_ACTIV_FINANCIAL = 2;
    public static final int QUOTE_TYPE_BBG_JSON = 3;
    public static final int QUOTE_TYPE_EDI_CSV = 4;
    public static final int QUOTE_TYPE_VSAT_JSON = 5;
    public static final int QUOTE_TYPE_OANDA_JSON = 6;

    private static final String BUNDLE_KEY_SECURITY_ID = QuoteDTO.class.getName() + ".security_id";
    private static final String BUNDLE_KEY_AS_OF_UTC = QuoteDTO.class.getName() + ".as_of_utc";
    private static final String BUNDLE_KEY_BID_PRICE = QuoteDTO.class.getName() + ".bid_price";
    private static final String BUNDLE_KEY_ASK_PRICE = QuoteDTO.class.getName() + ".ask_price";
    private static final String BUNDLE_KEY_CURRENCY_ISO = QuoteDTO.class.getName() + ".currency_iso";
    private static final String BUNDLE_KEY_CURRENCY_DISPLAY = QuoteDTO.class.getName() + ".currency_display";
    private static final String BUNDLE_KEY_FROM_CACHE = QuoteDTO.class.getName() + ".from_cache";
    private static final String BUNDLE_KEY_QUOTE_TYPE = QuoteDTO.class.getName() + ".quote_type";
    private static final String BUNDLE_KEY_TO_USD_RATE = QuoteDTO.class.getName() + ".usd_rate";
    private static final String BUNDLE_KEY_TO_USD_RATE_DATE = QuoteDTO.class.getName() + ".usd_rate_date";
    private static final String BUNDLE_KEY_TIMESTAMP = QuoteDTO.class.getName() + ".timestamp";
    private static final String BUNDLE_KEY_RAW_RESPONSE = QuoteDTO.class.getName() + ".raw_response";

    public int securityId;

    @Nullable public Date asOfUtc;
    @JsonProperty("asOfEST")
    public Date asOfEst;
    @Nullable public Double bid;
    @Nullable public Double ask;

    public String currencyISO;
    public String currencyDisplay;

    public boolean fromCache;
    public int quoteType;

    public Double toUSDRate;
    public Date toUSDRateDate;

    public String timeStamp;

    @JsonProperty("IsInverted")
    public Boolean isInverted;
    @JsonProperty("IsOneSided")
    public Boolean isOneSided;
    @JsonProperty("IsValid")
    public Boolean isValid;

    // This part is used for the signature container that came back
    private String rawResponse;

    public QuoteDTO()
    {
        super();
    }

    public QuoteDTO(@NonNull Bundle bundle)
    {
        if(!isValid(bundle))
        {
            Timber.e("Invalid bundle passed to QuoteDTO %s", bundle.keySet());
        }

        securityId = bundle.getInt(BUNDLE_KEY_SECURITY_ID);
        long ofUtc = bundle.getLong(BUNDLE_KEY_AS_OF_UTC);
        if (ofUtc > 0)
        {
            asOfUtc = new Date(ofUtc);
        }
        if (bundle.containsKey(BUNDLE_KEY_BID_PRICE))
        {
            bid = bundle.getDouble(BUNDLE_KEY_BID_PRICE);
        }
        else
        {
            bid = null;
        }
        if (bundle.containsKey(BUNDLE_KEY_ASK_PRICE))
        {
            ask = bundle.getDouble(BUNDLE_KEY_ASK_PRICE);
        }
        else
        {
            ask = null;
        }
        currencyISO = bundle.getString(BUNDLE_KEY_CURRENCY_ISO);
        currencyDisplay = bundle.getString(BUNDLE_KEY_CURRENCY_DISPLAY);
        fromCache = bundle.getBoolean(BUNDLE_KEY_FROM_CACHE);
        quoteType = bundle.getInt(BUNDLE_KEY_QUOTE_TYPE);
        toUSDRate = bundle.getDouble(BUNDLE_KEY_TO_USD_RATE);
        long usdRateDate = bundle.getLong(BUNDLE_KEY_TO_USD_RATE_DATE);
        if(usdRateDate > 0)
        {
            toUSDRateDate = new Date();
        }
        timeStamp = bundle.getString(BUNDLE_KEY_TIMESTAMP);
        rawResponse = bundle.getString(BUNDLE_KEY_RAW_RESPONSE);
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonIgnore public Double getPrice(boolean isBuy)
    {
        return isBuy ? ask : bid;
    }

    @JsonIgnore public Double getBidUSD()
    {
        if (bid == null || toUSDRate == null)
        {
            return null;
        }
        return bid * toUSDRate;
    }

    @JsonIgnore public Double getAskUSD()
    {
        if (ask == null || toUSDRate == null)
        {
            return null;
        }
        return ask * toUSDRate;
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonIgnore public Double getPriceUSD(boolean isBuy)
    {
        return isBuy ? getAskUSD() : getBidUSD();
    }

    /**
     * @param refCcyToUsdRate Pass 1 if refCcy is USD
     */
    @JsonIgnore public Double getBidRefCcy(Double refCcyToUsdRate)
    {
        Double bidUSD = getBidUSD();
        if (bidUSD == null || refCcyToUsdRate == null || refCcyToUsdRate.equals(0d))
        {
            return null;
        }
        return bidUSD / refCcyToUsdRate;
    }

    /**
     * @param refCcyToUsdRate Pass 1 if refCcy is USD
     */
    @JsonIgnore public Double getAskRefCcy(Double refCcyToUsdRate)
    {
        Double askUSD = getAskUSD();
        if (askUSD == null || refCcyToUsdRate == null || refCcyToUsdRate.equals(0d))
        {
            return null;
        }
        return askUSD / refCcyToUsdRate;
    }

    @SuppressWarnings("UnusedDeclaration")
    @JsonIgnore public Double getPriceRefCcy(Double refCcyToUsdRate, boolean isBuy)
    {
        return isBuy ? getAskRefCcy(refCcyToUsdRate) : getBidRefCcy(refCcyToUsdRate);
    }

    @JsonIgnore public Double getBidRefCcy(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getBidRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    @JsonIgnore public Double getAskRefCcy(PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getAskRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    @JsonIgnore public Double getPriceRefCcy(PortfolioCompactDTO portfolioCompactDTO, boolean isBuy)
    {
        return isBuy ? getAskRefCcy(portfolioCompactDTO) : getBidRefCcy(portfolioCompactDTO);
    }

    @Override public String getRawResponse()
    {
        return rawResponse;
    }

    @Override public void setRawResponse(String rawResponse)
    {
        this.rawResponse = rawResponse;
    }

    public static boolean isValid(Bundle args)
    {
        return args != null &&
                args.containsKey(BUNDLE_KEY_SECURITY_ID) &&
                args.getInt(BUNDLE_KEY_SECURITY_ID) > 0 &&
                args.containsKey(BUNDLE_KEY_AS_OF_UTC) &&
                args.containsKey(BUNDLE_KEY_BID_PRICE) &&
                args.containsKey(BUNDLE_KEY_ASK_PRICE) &&
                args.containsKey(BUNDLE_KEY_CURRENCY_ISO) &&
                args.containsKey(BUNDLE_KEY_CURRENCY_DISPLAY) &&
                args.containsKey(BUNDLE_KEY_FROM_CACHE) &&
                args.containsKey(BUNDLE_KEY_QUOTE_TYPE) &&
                args.containsKey(BUNDLE_KEY_TO_USD_RATE) &&
                args.containsKey(BUNDLE_KEY_TO_USD_RATE_DATE) &&
                args.containsKey(BUNDLE_KEY_TIMESTAMP) &&
                args.containsKey(BUNDLE_KEY_RAW_RESPONSE)
        ;
    }

    protected void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_SECURITY_ID, securityId);
        if (asOfUtc != null)
        {
            args.putLong(BUNDLE_KEY_AS_OF_UTC, asOfUtc.getTime());
        }
        else
        {
            args.putLong(BUNDLE_KEY_AS_OF_UTC, 0);
        }
        if (bid != null)
        {
            args.putDouble(BUNDLE_KEY_BID_PRICE, bid);
        }
        else
        {
            args.remove(BUNDLE_KEY_BID_PRICE);
        }
        if (ask != null)
        {
            args.putDouble(BUNDLE_KEY_ASK_PRICE, ask);
        }
        else
        {
            args.remove(BUNDLE_KEY_ASK_PRICE);
        }
        args.putString(BUNDLE_KEY_CURRENCY_ISO, currencyISO);
        args.putString(BUNDLE_KEY_CURRENCY_DISPLAY, currencyDisplay);
        args.putBoolean(BUNDLE_KEY_FROM_CACHE, fromCache);
        args.putInt(BUNDLE_KEY_QUOTE_TYPE, quoteType);
        args.putDouble(BUNDLE_KEY_TO_USD_RATE, toUSDRate);
        if (toUSDRateDate != null)
        {
            args.putLong(BUNDLE_KEY_TO_USD_RATE_DATE, toUSDRateDate.getTime());
        }
        else
        {
            args.putLong(BUNDLE_KEY_TO_USD_RATE_DATE, 0);
        }
        args.putString(BUNDLE_KEY_TIMESTAMP, timeStamp);
        args.putString(BUNDLE_KEY_RAW_RESPONSE, rawResponse);

    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }
}
