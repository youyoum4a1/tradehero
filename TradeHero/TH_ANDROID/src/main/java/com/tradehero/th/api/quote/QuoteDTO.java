package com.tradehero.th.api.quote;

import android.os.Bundle;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.tradehero.th.api.portfolio.PortfolioCompactDTO;
import java.util.Date;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class QuoteDTO
{
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
    public Double bid;
    public Double ask;

    public String currencyISO;
    public String currencyDisplay;

    public boolean fromCache;
    public int quoteType;

    public Double toUSDRate;
    public Date toUSDRateDate;

    public String timeStamp;

    // This part is used for the signature container that came back
    public String rawResponse;

    public QuoteDTO()
    {
        super();
    }

    public QuoteDTO(@NotNull Bundle bundle)
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
        bid = bundle.getDouble(BUNDLE_KEY_BID_PRICE);
        ask = bundle.getDouble(BUNDLE_KEY_ASK_PRICE);
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
        args.putDouble(BUNDLE_KEY_BID_PRICE, bid);
        args.putDouble(BUNDLE_KEY_ASK_PRICE, ask);
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
