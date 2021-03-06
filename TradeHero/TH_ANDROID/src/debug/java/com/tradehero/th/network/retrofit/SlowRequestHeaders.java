package com.tradehero.th.network.retrofit;

import android.accounts.AccountManager;
import android.content.Context;
import com.tradehero.th.persistence.prefs.LanguageCode;
import javax.inject.Inject;
import timber.log.Timber;

public class SlowRequestHeaders extends RequestHeaders
{
    public static final long SLEEP_MILLI_SEC = 10000;

    @Inject public SlowRequestHeaders(
            Context context,
            @LanguageCode String languageCode,
            AccountManager accountManager)
    {
        super(context, languageCode, accountManager);
    }

    @Override public void intercept(RequestFacade request)
    {
        super.intercept(request);
        try
        {
            Timber.d("Slowing for %d millisec", SLEEP_MILLI_SEC);
            Thread.sleep(SLEEP_MILLI_SEC);
        }
        catch (InterruptedException e)
        {
            Timber.e(e, "Interrupted");
        }
    }
}
