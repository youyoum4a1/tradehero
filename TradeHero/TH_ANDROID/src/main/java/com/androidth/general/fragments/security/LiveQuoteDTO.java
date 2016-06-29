package com.androidth.general.fragments.security;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

/**
 * Created by ayushnvijay on 6/24/16.
 */

public class LiveQuoteDTO {
    private int id;//SecurityId

    @Nullable
    private double a;//AskPrice

    @Nullable
    private double b;//BidPrice

    @Nullable
    private double v;//Volume

    private DateDeserializers.DateDeserializer l;

    @Nullable
    private double usdr;//toUSDRate

    private String ciso;//currencyISO

    private String cd;//currencyDisplay

    private String g;//GroupId

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
        this.id = id;
        this.a = a;
        this.b = b;
        this.v = v;
        this.l = l;
        this.usdr = usdr;
        this.ciso = ciso;
        this.cd = cd;
        this.g = g;
    }

    public void setSecurityId(int securityId) {
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

    public void setDate(DateDeserializers.DateDeserializer date) {
        this.l = l;
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

    public int getSecurityId() {

        return id;
    }

    @Nullable
    public double getAskPrice() {
        return a;
    }

    @Nullable
    public double getBidPrice() {
        return b;
    }

    @Nullable
    public double getVolume() {
        return v;
    }

    public DateDeserializers.DateDeserializer getDate() {
        return l;
    }

    @Nullable
    public double getUsdRate() {
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

}
