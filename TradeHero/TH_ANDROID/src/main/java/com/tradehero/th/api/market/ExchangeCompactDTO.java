package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class ExchangeCompactDTO implements DTO
{
    public static final String BUNDLE_KEY_ID = ExchangeDTO.class.getName() + ".id";
    public static final String BUNDLE_KEY_NAME = ExchangeDTO.class.getName() + ".name";
    public static final String BUNDLE_KEY_DESC = ExchangeDTO.class.getName() + ".desc";
    public static final String BUNDLE_KEY_IS_INCLUDED_IN_TRENDING = ExchangeDTO.class.getName() + ".isIncludedInTrending";

    public int id;
    @NotNull public String name;
    @NotNull public String countryCode;
    public double sumMarketCap;
    public String desc;
    public boolean isInternal;
    public boolean isIncludedInTrending;
    public boolean chartDataSource;

    //<editor-fold desc="Constructors">
    public ExchangeCompactDTO()
    {
        super();
    }

    public ExchangeCompactDTO(int id, @NotNull String name, @NotNull String countryCode, double sumMarketCap, String desc, boolean isInternal,
            boolean isIncludedInTrending,
            boolean chartDataSource)
    {
        super();
        this.id = id;
        this.name = name;
        this.countryCode = countryCode;
        this.sumMarketCap = sumMarketCap;
        this.desc = desc;
        this.isInternal = isInternal;
        this.isIncludedInTrending = isIncludedInTrending;
        this.chartDataSource = chartDataSource;
    }

    public ExchangeCompactDTO(@NotNull ExchangeCompactDTO other)
    {
        super();
        this.id = other.id;
        this.name = other.name;
        this.countryCode = other.countryCode;
        this.sumMarketCap = other.sumMarketCap;
        this.desc = other.desc;
        this.isInternal = other.isInternal;
        this.isIncludedInTrending = other.isIncludedInTrending;
        this.chartDataSource = other.chartDataSource;
    }

    public ExchangeCompactDTO(@NotNull Bundle bundle)
    {
        super();
        this.id = bundle.getInt(BUNDLE_KEY_ID);
        this.name = bundle.getString(BUNDLE_KEY_NAME);
        this.desc = bundle.getString(BUNDLE_KEY_DESC);
        this.isIncludedInTrending = bundle.getBoolean(BUNDLE_KEY_IS_INCLUDED_IN_TRENDING);
    }
    //</editor-fold>

    public ExchangeIntegerId getExchangeIntegerId()
    {
        return new ExchangeIntegerId(id);
    }

    public ExchangeStringId getExchangeStringId()
    {
        return new ExchangeStringId(name);
    }

    public Integer getFlagResId()
    {
        Integer fromName = getNameFlagResId();
        if (fromName != null)
        {
            return fromName;
        }
        return getCountryCodeFlagResId();
    }

    @Nullable public Integer getNameFlagResId()
    {
        try
        {
            return Exchange.valueOf(name).logoId;
        }
        catch (IllegalArgumentException e)
        {
            Timber.e(e, "Exchange logo does not exist for name", name);
        }
        return null;
    }

    @Nullable public Integer getCountryCodeFlagResId()
    {
        try
        {
            return Country.valueOf(countryCode).logoId;
        }
        catch (IllegalArgumentException e)
        {
            Timber.e(e, "Exchange logo does not exist for countryCode %s", countryCode);
        }
        return null;
    }

    protected void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_ID, this.id);
        args.putString(BUNDLE_KEY_NAME, this.name);
        args.putString(BUNDLE_KEY_DESC, this.desc);
        args.putBoolean(BUNDLE_KEY_IS_INCLUDED_IN_TRENDING, this.isIncludedInTrending);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    @Override public boolean equals(Object other)
    {
        if (other == null || !(other instanceof ExchangeDTO))
        {
            return false;
        }
        return name.equals(((ExchangeDTO) other).name);
    }
}
