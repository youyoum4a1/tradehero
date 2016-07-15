package com.androidth.general.fragments.security;

import android.support.annotation.Nullable;

import com.fasterxml.jackson.databind.deser.std.DateDeserializers;

/**
 * Created by ayushnvijay on 6/24/16.
 */

public class LiveQuoteDTO {
    public int id;//SecurityId

    @Nullable
    public double a;//AskPrice

    @Nullable
    public double b;//BidPrice

    @Nullable
    public double v;//Volume

    public String l;

    @Nullable
    public double usdr;//toUSDRate

    @Nullable
    public String ciso;//currencyISO

    @Nullable
    public String cd;//currencyDisplay

    public String g;//GroupId



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

    public String getDate() {
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
