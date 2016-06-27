package com.androidth.general.fragments.security;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

/**
 * Created by ayushnvijay on 6/24/16.
 */

public class LiveQuoteDTO {
    private int securityId;//SecurityId

    @Nullable
    private double askPrice;//AskPrice

    @Nullable
    private double bidPrice;//BidPrice

    @Nullable
    private double volume;//Volume

    private DateDeserializers.DateDeserializer date;

    @Nullable
    private double usdRate;//toUSDRate

    private String currencyISO;//currencyISO

    private String currencyDisplay;//currencyDisplay

    private String groupId;//GroupId

    @JsonCreator
    public LiveQuoteDTO(
            @JsonProperty("id") int id,
            @JsonProperty("a") @Nullable double a,
            @JsonProperty("b") @Nullable double b,
            @JsonProperty("v") @Nullable double v,
            @JsonProperty("l")DateDeserializers.DateDeserializer l,
            @JsonProperty("usdr") @Nullable double usdr,
            @JsonProperty("ciso") String ciso,
            @JsonProperty("cd") String cd,
            @JsonProperty("g") String g
    ){
        this.securityId = id;
        this.askPrice = a;
        this.bidPrice = b;
        this.volume = v;
        this.date = l;
        this.usdRate = usdr;
        this.currencyISO = ciso;
        this.currencyDisplay = cd;
        this.groupId = g;
    }

    public void setSecurityId(int securityId) {
        this.securityId = securityId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public void setAskPrice(@Nullable double askPrice) {
        this.askPrice = askPrice;
    }

    public void setBidPrice(@Nullable double bidPrice) {
        this.bidPrice = bidPrice;
    }

    public void setVolume(@Nullable double volume) {
        this.volume = volume;
    }

    public void setDate(DateDeserializers.DateDeserializer date) {
        this.date = date;
    }

    public void setUsdRate(@Nullable double usdRate) {
        this.usdRate = usdRate;
    }

    public void setCurrencyISO(String currencyISO) {
        this.currencyISO = currencyISO;
    }

    public void setCurrencyDisplay(String currencyDisplay) {
        this.currencyDisplay = currencyDisplay;
    }

    public int getSecurityId() {

        return securityId;
    }

    @Nullable
    public double getAskPrice() {
        return askPrice;
    }

    @Nullable
    public double getBidPrice() {
        return bidPrice;
    }

    @Nullable
    public double getVolume() {
        return volume;
    }

    public DateDeserializers.DateDeserializer getDate() {
        return date;
    }

    @Nullable
    public double getUsdRate() {
        return usdRate;
    }

    public String getCurrencyISO() {
        return currencyISO;
    }

    public String getCurrencyDisplay() {
        return currencyDisplay;
    }

    public String getGroupId() {
        return groupId;
    }

}
