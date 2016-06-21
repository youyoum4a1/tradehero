package com.androidth.general.api.security;

import android.support.annotation.DrawableRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.androidth.general.R;
import com.androidth.general.api.market.Exchange;
import com.androidth.general.api.security.compact.BondCompactDTO;
import com.androidth.general.api.security.compact.CoveredWarrantDTO;
import com.androidth.general.api.security.compact.DepositoryReceiptDTO;
import com.androidth.general.api.security.compact.EquityCompactDTO;
import com.androidth.general.api.security.compact.FundCompactDTO;
import com.androidth.general.api.security.compact.FxSecurityCompactDTO;
import com.androidth.general.api.security.compact.IndexSecurityCompactDTO;
import com.androidth.general.api.security.compact.LockedSecurityCompactDTO;
import com.androidth.general.api.security.compact.PreferenceShareDTO;
import com.androidth.general.api.security.compact.PreferredSecurityDTO;
import com.androidth.general.api.security.compact.StapledSecurityDTO;
import com.androidth.general.api.security.compact.TradableRightsIssueDTO;
import com.androidth.general.api.security.compact.UnitCompactDTO;
import com.androidth.general.api.security.compact.UnitTrustSecurityCompactDTO;
import com.androidth.general.api.security.compact.WarrantDTO;
import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

import java.util.Date;

import timber.log.Timber;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        defaultImpl = ExchangeCompactDTO.class
    )

public class ExchangeCompactDTO implements DTO
{
    public Integer id;
    public String name;
    public String imageUrl;
    public Double PerfMarketCap;
    public String description;
    public String countryCode;
    public String openingTimeUtc;
    public String closingTimeUtc;
    public String TimezoneMsftName;

    //<editor-fold desc="Constructors">
    public ExchangeCompactDTO()
    {
        super();
    }

    public ExchangeCompactDTO(@NonNull ExchangeCompactDTO other)
    {
        super();
        this.id = other.id;
        this.name = other.name;
        this.imageUrl = other.imageUrl;
        this.PerfMarketCap = other.PerfMarketCap;
        this.description = other.description;
        this.countryCode = other.countryCode;
        this.openingTimeUtc = other.openingTimeUtc;
        this.closingTimeUtc = other.closingTimeUtc;
        this.TimezoneMsftName = other.TimezoneMsftName;
    }
    //</editor-fold>

    @Override public String toString()
    {
        return "ExchangeCompactDTO{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", PerfMarketCap='" + PerfMarketCap + '\'' +
                ", description='" + description + '\'' +
                ", countryCode='" + countryCode + '\'' +
                ", openingTimeUtc=" + openingTimeUtc +
                ", closingTimeUtc=" + closingTimeUtc +
                ", TimezoneMsftName='" + TimezoneMsftName + '\'' +
                '}';
    }
}
