package com.ayondo.academy.models.intent.competition;

import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderId;

public class SimpleOneProviderIntent extends OneProviderIntent
{
    //<editor-fold desc="Constructors">
    protected SimpleOneProviderIntent(
            @NonNull Resources resources,
            @NonNull ProviderId portfolioId)
    {
        super(resources, portfolioId);
    }
    //</editor-fold>

    @Override int getIntentProviderAction()
    {
        return R.string.intent_action_provider_pages;
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        return null;
    }
}
