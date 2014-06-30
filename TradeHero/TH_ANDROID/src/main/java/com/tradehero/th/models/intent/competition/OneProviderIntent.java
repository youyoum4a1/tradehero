package com.tradehero.th.models.intent.competition;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.thm.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.competition.CompetitionFragment;
import java.util.List;

abstract public class OneProviderIntent extends ProviderIntent
{
    //<editor-fold desc="Constructors">
    protected OneProviderIntent()
    {
        super();
    }

    protected OneProviderIntent(ProviderId providerId)
    {
        super();
        setData(getProviderActionUri(providerId));
    }
    //</editor-fold>

    public Uri getProviderActionUri(ProviderId providerId)
    {
        return Uri.parse(getProviderActionUriPath(providerId));
    }

    public String getProviderActionUriPath(ProviderId portfolioId)
    {
        return getString(
                getIntentActionUriResId(),
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_providers),
                portfolioId.key,
                getString(getIntentProviderAction()));
    }

    public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_one_provider;
    }

    abstract int getIntentProviderAction();

    public ProviderId getProviderId()
    {
        return getProviderId(getData());
    }

    public static ProviderId getProviderId(Uri data)
    {
        return getProviderId(data.getPathSegments());
    }

    public static ProviderId getProviderId(List<String> pathSegments)
    {
        return new ProviderId(Integer.parseInt(pathSegments.get(getInteger(R.integer.intent_uri_action_provider_path_index_id))));
    }

    @Override abstract public Class<? extends Fragment> getActionFragment();

    @Override public void populate(Bundle bundle)
    {
        super.populate(bundle);
        CompetitionFragment.putProviderId(bundle, getProviderId());
    }
}
