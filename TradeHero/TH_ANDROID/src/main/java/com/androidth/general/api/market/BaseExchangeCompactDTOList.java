package com.androidth.general.api.market;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.android.internal.util.Predicate;
import com.androidth.general.common.api.BaseArrayList;
import java.util.Collection;

public class BaseExchangeCompactDTOList<ExchangeCompactDTOType extends ExchangeCompactDTO>
    extends BaseArrayList<ExchangeCompactDTOType>
{
    //<editor-fold desc="Constructors">
    protected BaseExchangeCompactDTOList()
    {
        super();
    }

    public BaseExchangeCompactDTOList(@NonNull Collection<? extends ExchangeCompactDTOType> c)
    {
        super(c);
    }
    //</editor-fold>

    @Nullable public ExchangeCompactDTOType findFirstFor(@NonNull final Country country)
    {
        return findFirstWhere(new Predicate<ExchangeCompactDTOType>()
        {
            @Override public boolean apply(@NonNull ExchangeCompactDTOType exchangeCompactDTO)
            {
                return exchangeCompactDTO.countryCode.equals(country.name());
            }
        });
    }

    @Nullable public ExchangeCompactDTOType findFirstDefaultFor(@NonNull final Country country)
    {
        return findFirstWhere(new Predicate<ExchangeCompactDTOType>()
        {
            @Override public boolean apply(@NonNull ExchangeCompactDTOType exchangeCompactDTO)
            {
                Exchange exchange = exchangeCompactDTO.getExchangeByName();
                return exchangeCompactDTO.countryCode.equals(country.name())
                        && exchange != null
                        && exchange.isCountryDefault;
            }
        });
    }
}
