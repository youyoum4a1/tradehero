package com.tradehero.th.api.users;

import android.content.Context;
import com.tradehero.th.R;
import com.tradehero.th.api.market.Country;
import com.tradehero.th.api.market.Exchange;
import com.tradehero.th.api.market.ExchangeCompactDTO;
import com.tradehero.th.api.market.ExchangeCompactDTOList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class UserBaseDTOUtil
{
    //<editor-fold desc="Constructors">
    @Inject public UserBaseDTOUtil()
    {
        super();
    }
    //</editor-fold>

    @NotNull public String getLongDisplayName(@NotNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            if (userBaseDTO.firstName != null &&
                !userBaseDTO.firstName.isEmpty() &&
                userBaseDTO.lastName != null &&
                !userBaseDTO.lastName.isEmpty())
            {
                return getFirstLastName(context, userBaseDTO);
            }

            return userBaseDTO.displayName;
        }

        return context.getString(R.string.na);
    }

    @NotNull public String getFirstLastName(@NotNull Context context, @Nullable UserBaseDTO userBaseDTO)
    {
        if (userBaseDTO != null)
        {
            return String.format(context.getString(R.string.user_profile_first_last_name_display),
                    userBaseDTO.firstName == null ? "" : userBaseDTO.firstName,
                    userBaseDTO.lastName == null ? "" : userBaseDTO.lastName).trim();
        }
        return context.getString(R.string.na);
    }

    @Nullable public <ExchangeCompactDTOType extends ExchangeCompactDTO> ExchangeCompactDTOType getInitialExchange(
            @NotNull UserBaseDTO userBaseDTO,
            @NotNull List<? extends ExchangeCompactDTOType> exchangeCompactDTOs)
    {
        @Nullable Country userCountry = userBaseDTO.getCountry();
        if (userCountry != null)
        {
            @Nullable Country exchangeCountry;
            @Nullable Exchange exchange;
            for (@NotNull ExchangeCompactDTOType exchangeCompactDTO : exchangeCompactDTOs)
            {
                exchangeCountry = exchangeCompactDTO.getCountry();
                exchange = exchangeCompactDTO.getExchangeByName();
                if (exchangeCountry != null && exchangeCountry.equals(userCountry) &&
                        exchange != null && exchange.isCountryDefault)
                {
                    return exchangeCompactDTO;
                }
            }
        }
        return null;
    }
}
