package com.tradehero.th.models.intent.interactor;

import android.content.Intent;
import android.net.Uri;
import com.tradehero.thm.R;
import com.tradehero.th.models.intent.THIntentFactory;

public abstract class InteractorIntentFactory extends THIntentFactory<InteractorIntent>
{
    public InteractorIntentFactory()
    {}

    @Override public InteractorIntent create(Intent intent)
    {
        Uri uri = intent.getData();
        if (uri == null)
        {
            return null;
        }
        else if (uri.getHost().equalsIgnoreCase(getString(R.string.intent_host_reset_portfolio)))
        {
            return new ResetPortfolioIntent();
        }
        return null;
    }
}
