package com.tradehero.th.models.intent.competition;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import org.jetbrains.annotations.NotNull;

public class SimpleOneProviderIntent extends OneProviderIntent
{
    //<editor-fold desc="Constructors">
    protected SimpleOneProviderIntent(
            @NotNull Resources resources,
            @NotNull ProviderId portfolioId)
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
