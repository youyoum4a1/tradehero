package com.tradehero.th.api.market;

import com.android.internal.util.Predicate;
import com.tradehero.common.api.BaseArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BaseExchangeCompactDTOList<ExchangeCompactDTOType extends ExchangeCompactDTO>
    extends BaseArrayList<ExchangeCompactDTOType>
{
    //<editor-fold desc="Constructors">
    protected BaseExchangeCompactDTOList()
    {
        super();
    }

    public BaseExchangeCompactDTOList(@NotNull Collection<? extends ExchangeCompactDTOType> c)
    {
        super(c);
    }
    //</editor-fold>

    @Nullable public ExchangeCompactDTOType findFirstFor(@NotNull final Country country)
    {
        return findFirstWhere(new Predicate<ExchangeCompactDTOType>()
        {
            @Override public boolean apply(@NotNull ExchangeCompactDTOType exchangeCompactDTO)
            {
                return exchangeCompactDTO.countryCode.equals(country.name());
            }
        });
    }

    @Nullable public ExchangeCompactDTOType findFirstDefaultFor(@NotNull final Country country)
    {
        return findFirstWhere(new Predicate<ExchangeCompactDTOType>()
        {
            @Override public boolean apply(@NotNull ExchangeCompactDTOType exchangeCompactDTO)
            {
                Exchange exchange = exchangeCompactDTO.getExchangeByName();
                return exchangeCompactDTO.countryCode.equals(country.name())
                        && exchange != null
                        && exchange.isCountryDefault;
            }
        });
    }
}
