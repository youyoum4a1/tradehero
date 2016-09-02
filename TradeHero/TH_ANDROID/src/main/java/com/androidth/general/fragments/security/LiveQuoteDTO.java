package com.androidth.general.fragments.security;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.androidth.general.api.RawResponseKeeper;
import com.androidth.general.api.portfolio.PortfolioCompactDTO;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;
import com.fasterxml.jackson.databind.ser.std.DateSerializer;

/**
 * Created by ayushnvijay on 6/24/16.
 */

public class LiveQuoteDTO implements RawResponseKeeper, Cloneable{

    public LiveQuoteDTO() {
        super();
    }

    public LiveQuoteDTO(@NonNull Bundle bundle){

    }

    public long id;//SecurityId

    @Nullable
    public Double a;//AskPrice

    @Nullable
    public Double b;//BidPrice

    @Nullable
    public Double v;//Volume

    public String l;

    @Nullable
    public double usdr;//toUSDRate

    @Nullable
    public String ciso;//currencyISO

    @Nullable
    public String cd;//currencyDisplay

    public String g;//GroupId

    public Double rp;//rise percent

    // This part is used for the signature container that came back
    @JsonIgnore private String rawResponse;



    public void setSecurityId(long securityId) {
        this.id = id;
    }

    public void setGroupId(String groupId) {
        this.g = g;
    }

    public void setAskPrice(@Nullable double askPrice) {
        this.a = a;
    }

    public void setBidPrice(@Nullable double bidPrice) {
        this.b = b;
    }

    public void setVolume(@Nullable double volume) {
        this.v = v;
    }

    public void setDate(String date) {
        this.l = date;
    }

    public void setUsdRate(@Nullable double usdRate) {
        this.usdr = usdr;
    }

    public void setCurrencyISO(String currencyISO) {
        this.ciso = ciso;
    }

    public void setCurrencyDisplay(String currencyDisplay) {
        this.cd = cd;
    }

    public long getSecurityId() {

        return id;
    }

    @Nullable
    public Double getAskPrice() {
        return a;
    }

    @Nullable
    public Double getBidPrice() {
        return b;
    }

    @Nullable
    public Double getVolume() {
        return v;
    }

    public String getDate() {
        return l;
    }

    @Nullable
    public Double getUsdRate() {
        return usdr;
    }

    public String getCurrencyISO() {
        return ciso;
    }

    public String getCurrencyDisplay() {
        return cd;
    }

    public String getGroupId() {
        return g;
    }

    @Nullable public Double getRisePercent() {
        return rp;
    }

    @JsonIgnore public Double getBidUSD()
    {
        if (getBidPrice() == null || getUsdRate() == null)
        {
            return null;
        }
        return getBidPrice() * getUsdRate();
    }

    @JsonIgnore public Double getAskUSD()
    {
        if (getAskPrice() == null || getUsdRate() == null)
        {
            return null;
        }
        return getAskPrice() * getUsdRate();
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

    @JsonIgnore
    @Nullable public Double getAskRefCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getAskRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    @JsonIgnore @Nullable public Double getPriceRefCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO, boolean isBuy)
    {
        return isBuy ? getAskRefCcy(portfolioCompactDTO) : getBidRefCcy(portfolioCompactDTO);
    }

    @JsonIgnore @Nullable public Double getBidRefCcy(@Nullable PortfolioCompactDTO portfolioCompactDTO)
    {
        if (portfolioCompactDTO == null)
        {
            return null;
        }
        return getBidRefCcy(portfolioCompactDTO.getProperRefCcyToUsdRate());
    }

    protected void putParameters(Bundle args)
    {
//        args.putInt(BUNDLE_KEY_SECURITY_ID, securityId);
//        if (asOfUtc != null)
//        {
//            args.putLong(BUNDLE_KEY_AS_OF_UTC, asOfUtc.getTime());
//        }
//        else
//        {
//            args.putLong(BUNDLE_KEY_AS_OF_UTC, 0);
//        }
//        if (bid != null)
//        {
//            args.putDouble(BUNDLE_KEY_BID_PRICE, bid);
//        }
//        else
//        {
//            args.remove(BUNDLE_KEY_BID_PRICE);
//        }
//        if (ask != null)
//        {
//            args.putDouble(BUNDLE_KEY_ASK_PRICE, ask);
//        }
//        else
//        {
//            args.remove(BUNDLE_KEY_ASK_PRICE);
//        }
//        args.putString(BUNDLE_KEY_CURRENCY_ISO, currencyISO);
//        args.putString(BUNDLE_KEY_CURRENCY_DISPLAY, currencyDisplay);
//        args.putBoolean(BUNDLE_KEY_FROM_CACHE, fromCache);
//        args.putInt(BUNDLE_KEY_QUOTE_TYPE, quoteType);
//        args.putDouble(BUNDLE_KEY_TO_USD_RATE, toUSDRate);
//        if (toUSDRateDate != null)
//        {
//            args.putLong(BUNDLE_KEY_TO_USD_RATE_DATE, toUSDRateDate.getTime());
//        }
//        else
//        {
//            args.putLong(BUNDLE_KEY_TO_USD_RATE_DATE, 0);
//        }
//        args.putString(BUNDLE_KEY_TIMESTAMP, timeStamp);
//        args.putString(BUNDLE_KEY_RAW_RESPONSE, rawResponse);

    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public String getRawResponse()
    {
        return rawResponse;
    }

    @Override public void setRawResponse(String rawResponse)
    {
        this.rawResponse = rawResponse;
    }

    @SuppressWarnings({"CloneDoesntCallSuperClone", "CloneDoesntDeclareCloneNotSupportedException"})
    @Override public LiveQuoteDTO clone() throws CloneNotSupportedException
    {
        LiveQuoteDTO cloned = (LiveQuoteDTO) super.clone();
        cloned.setAskPrice(getAskPrice()!=null? getAskPrice():0);
        cloned.setBidPrice(getBidPrice()!=null? getBidPrice():0);
        cloned.setCurrencyDisplay(getCurrencyDisplay());
        cloned.setCurrencyISO(getCurrencyISO());
        cloned.setDate(getDate()!=null? getDate():"");
        cloned.setGroupId(getGroupId());
        cloned.setRawResponse(getRawResponse());
        cloned.setSecurityId(getSecurityId());
        cloned.setUsdRate(getUsdRate()!=null? getUsdRate():0);
        cloned.setVolume(getVolume()!=null? getVolume():0);
        return cloned;
    }


}
