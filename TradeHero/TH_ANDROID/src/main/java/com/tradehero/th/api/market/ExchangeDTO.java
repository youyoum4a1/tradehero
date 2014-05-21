package com.tradehero.th.api.market;

import android.os.Bundle;
import com.tradehero.common.persistence.DTO;
import java.util.List;

public class ExchangeDTO implements DTO
{
    public static final String BUNDLE_KEY_ID = ExchangeDTO.class.getName() + ".id";
    public static final String BUNDLE_KEY_NAME = ExchangeDTO.class.getName() + ".name";
    public static final String BUNDLE_KEY_DESC = ExchangeDTO.class.getName() + ".desc";
    public static final String BUNDLE_KEY_IS_INCLUDED_IN_TRENDING = ExchangeDTO.class.getName() + ".isIncludedInTrending";

    public int id;
    public String name;

    public double sumMarketCap;
    public List<SectorDTO> sectors;

    public String desc;
    public boolean isInternal;
    public boolean isIncludedInTrending;

    //<editor-fold desc="Constructors">
    public ExchangeDTO()
    {
        super();
    }

    public ExchangeDTO(int id, String name, double sumMarketCap, List<SectorDTO> sectors, String desc, boolean isInternal,
            boolean isIncludedInTrending)
    {
        this.id = id;
        this.name = name;
        this.sumMarketCap = sumMarketCap;
        this.sectors = sectors;
        this.desc = desc;
        this.isInternal = isInternal;
        this.isIncludedInTrending = isIncludedInTrending;
    }

    public ExchangeDTO(ExchangeDTO other)
    {
        this.id = other.id;
        this.name = other.name;
        this.sumMarketCap = other.sumMarketCap;
        this.sectors = other.sectors;
        this.desc = other.desc;
        this.isInternal = other.isInternal;
        this.isIncludedInTrending = other.isIncludedInTrending;
    }

    public ExchangeDTO(Bundle bundle)
    {
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
