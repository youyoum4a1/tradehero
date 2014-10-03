package com.tradehero.th.models.intent.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.competition.CompetitionFragment;
import java.util.List;
import org.jetbrains.annotations.NotNull;

abstract public class OneProviderIntent extends ProviderIntent
{
    //<editor-fold desc="Constructors">
    protected OneProviderIntent(@NotNull Resources resources)
    {
        super(resources);
    }

    protected OneProviderIntent(
            @NotNull Resources resources,
            @NotNull ProviderId providerId)
    {
        super(resources);
        setData(getProviderActionUri(providerId));
    }
    //</editor-fold>

    @NotNull public Uri getProviderActionUri(@NotNull ProviderId providerId)
    {
        return Uri.parse(getProviderActionUriPath(providerId));
    }

    @NotNull public String getProviderActionUriPath(@NotNull ProviderId portfolioId)
    {
        return resources.getString(
                getIntentActionUriResId(),
                resources.getString(R.string.intent_scheme),
                resources.getString(R.string.intent_host_providers),
                portfolioId.key,
                resources.getString(getIntentProviderAction()));
    }

    public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_one_provider;
    }

    abstract int getIntentProviderAction();

    public ProviderId getProviderId()
    {
        return getProviderId(resources, getData());
    }

    public static ProviderId getProviderId(
            @NotNull Resources resources,
            @NotNull Uri data)
    {
        return getProviderId(resources, data.getPathSegments());
    }

    public static ProviderId getProviderId(
            @NotNull Resources resources,
            @NotNull List<String> pathSegments)
    {
        return new ProviderId(Integer.parseInt(pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_id))));
    }

    @Override abstract public Class<? extends Fragment> getActionFragment();

    @Override public void populate(@NotNull Bundle bundle)
    {
        super.populate(bundle);
        CompetitionFragment.putProviderId(bundle, getProviderId());
    }
}
