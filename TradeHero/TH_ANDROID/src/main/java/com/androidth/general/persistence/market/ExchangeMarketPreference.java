package com.androidth.general.persistence.market;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.prefs.IntPreference;
import com.androidth.general.api.market.BaseExchangeCompactDTOList;
import com.androidth.general.api.market.Country;
import com.androidth.general.api.market.ExchangeCompactDTO;
import com.androidth.general.api.market.ExchangeIntegerId;
import com.androidth.general.api.users.UserProfileDTO;

public class ExchangeMarketPreference extends IntPreference
{
    public final static int UNSET_VALUE = -1;

    //<editor-fold desc="Constructors">
    public ExchangeMarketPreference(@NonNull SharedPreferences preference,
            @NonNull String key)
    {
        super(preference, key, UNSET_VALUE);
    }
    //</editor-fold>

    @Override public void set(@NonNull Integer value)
    {
        throw new IllegalStateException("You should set through set(ExchangeIntegerId)");
    }

    public void set(@NonNull ExchangeIntegerId exchangeId)
    {
        super.set(exchangeId.key);
    }

    public boolean setDefaultIfUnset(
            @NonNull BaseExchangeCompactDTOList<? extends ExchangeCompactDTO> exchangeCompactDTOs,
            @NonNull UserProfileDTO currentUserProfile)
    {
        if (get().equals(UNSET_VALUE))
        {
            Country country = currentUserProfile.getCountry();
            if (country != null)
            {
                ExchangeCompactDTO initial = exchangeCompactDTOs.findFirstDefaultFor(country);
                if (initial != null)
                {
                    set(initial.getExchangeIntegerId());
                    return true;
                }
            }
        }
        return false;
    }

    @NonNull public ExchangeIntegerId getExchangeIntegerId()
    {
        return new ExchangeIntegerId(get());
    }
}
