package com.tradehero.th.api.live;

import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import com.tradehero.common.annotation.ForUser;
import com.tradehero.common.persistence.prefs.BooleanPreference;
import com.tradehero.common.persistence.prefs.IntPreference;
import com.tradehero.th.persistence.prefs.IsLiveLogIn;
import com.tradehero.th.persistence.prefs.IsLiveTrading;
import javax.inject.Inject;
import timber.log.Timber;

public class LivePortfolioId extends IntPreference
{
    @Inject @IsLiveLogIn BooleanPreference isLiveLogIn;
    @Inject @IsLiveTrading BooleanPreference isLiveTrading;

    private static final String PREF_LIVE_PORTFOLIO_ID_KEY = "PREF_LIVE_PORTFOLIO_ID_KEY";

    @Inject public LivePortfolioId(@ForUser SharedPreferences preference)
    {
        super(preference, PREF_LIVE_PORTFOLIO_ID_KEY, 0);
    }

    @NonNull @Override public Integer get()
    {
        Integer id = super.get();

        if (id == 0)
        {
            if (isLiveTrading.get() || isLiveLogIn.get())
            {
                isLiveLogIn.set(false);
                isLiveTrading.set(false);

                Timber.e("Unable to get Live Portfolio Id from preference. Current mode should be virtual!");
            }
        }

        return id;
    }
}
