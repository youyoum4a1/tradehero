package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;

public class ExchangeCompactDTO implements DTO
{
    public static final String BUNDLE_KEY_ID = ExchangeDTO.class.getName() + ".id";
    public static final String BUNDLE_KEY_NAME = ExchangeDTO.class.getName() + ".name";
    public static final String BUNDLE_KEY_DESC = ExchangeDTO.class.getName() + ".desc";
    public static final String BUNDLE_KEY_IS_INCLUDED_IN_TRENDING = ExchangeDTO.class.getName() + ".isIncludedInTrending";

    public int id;
    public String name;
    public String countryCode;
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

    public ExchangeCompactDTO(int id, String name, String countryCode, double sumMarketCap, String desc, boolean isInternal,
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

    public ExchangeCompactDTO(ExchangeCompactDTO other)
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

    public ExchangeCompactDTO(Bundle bundle)
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
        if (name != null)
        {
            return name.equals(((ExchangeDTO) other).name);
        }
        //if both names are null,return true
        return ((ExchangeDTO) other).name == null;
    }
}
