package com.androidth.general.models.intent.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.fragments.competition.ProviderVideoListFragment;
import java.util.List;

abstract public class OneProviderIntent extends ProviderIntent
{
    //<editor-fold desc="Constructors">
    protected OneProviderIntent(@NonNull Resources resources)
    {
        super(resources);
    }

    protected OneProviderIntent(
            @NonNull Resources resources,
            @NonNull ProviderId providerId)
    {
        super(resources);
        setData(getProviderActionUri(providerId));
    }
    //</editor-fold>

    @NonNull public Uri getProviderActionUri(@NonNull ProviderId providerId)
    {
        return Uri.parse(getProviderActionUriPath(providerId));
    }

    @NonNull public String getProviderActionUriPath(@NonNull ProviderId portfolioId)
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
            @NonNull Resources resources,
            @NonNull Uri data)
    {
        return getProviderId(resources, data.getPathSegments());
    }

    public static ProviderId getProviderId(
            @NonNull Resources resources,
            @NonNull List<String> pathSegments)
    {
        return new ProviderId(Integer.parseInt(pathSegments.get(resources.getInteger(R.integer.intent_uri_action_provider_path_index_id))));
    }

    @Override abstract public Class<? extends Fragment> getActionFragment();

    @Override public void populate(@NonNull Bundle bundle)
    {
        super.populate(bundle);
        ProviderVideoListFragment.putProviderId(bundle, getProviderId());
    }
}
