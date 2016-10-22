package com.androidth.general.api.security;

import android.support.annotation.NonNull;

import com.androidth.general.common.persistence.DTO;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

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
