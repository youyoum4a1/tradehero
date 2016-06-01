package com.ayondo.academy.models.intent.competition;

import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.inject.HierarchyInjector;
import java.util.List;
import javax.inject.Inject;
import retrofit.Endpoint;

public class ProviderPageIntent extends OneProviderIntent
{
    @Inject Endpoint apiServer;

    //<editor-fold desc="Constructors">
    public ProviderPageIntent(
            @NonNull Resources resources,
            @NonNull ProviderId providerId,
            @NonNull String uri)
    {
        super(resources);
        setData(getProviderActionUri(providerId, uri));

        HierarchyInjector.inject(THApp.context(), this);
    }
    //</editor-fold>

    @NonNull public Uri getProviderActionUri(
            @NonNull ProviderId providerId,
            @NonNull String uri)
    {
        return Uri.parse(getProviderActionUriPath(providerId, uri));
    }

    @NonNull public String getProviderActionUriPath(
            @NonNull ProviderId portfolioId,
            @NonNull String uri)
    {
        return resources.getString(
                getIntentActionUriResId(),
                resources.getString(R.string.intent_scheme),
                resources.getString(R.string.intent_host_providers),
                portfolioId.key,
                resources.getString(getIntentProviderAction()),
                Uri.encode(Uri.encode(uri)));
    }

    @Override int getIntentProviderAction()
    {
        return R.string.intent_action_provider_pages;
    }

    @Override public Class<? extends Fragment> getActionFragment()
    {
        throw new RuntimeException("There is no fragment attached to it");
    }

    @Override public int getIntentActionUriResId()
    {
        return R.string.intent_uri_action_provider_page;
    }

    @NonNull public String getCompleteForwardUriPath()
    {
        String path = getForwardUriPath();
        Uri forwardUri = Uri.parse(path);
        if (forwardUri.getScheme() == null && forwardUri.getHost() == null)
        {
            return apiServer.getUrl() + getForwardUriPath();
        }
        else
        {
            return path;
        }
    }

    @NonNull public String getForwardUriPath()
    {
        return getForwardUriPath(resources, getData());
    }

    @NonNull public static String getForwardUriPath(
            @NonNull Resources resources,
            @NonNull Uri data)
    {
        return getForwardUriPath(resources, data.getPathSegments());
    }

    @NonNull public static String getForwardUriPath(
            @NonNull Resources resources,
            @NonNull List<String> pathSegments)
    {
        // Only 1 decode is necessary here as the getDataPathSegments already does one.
        return Uri.decode(
                pathSegments.get(
                        resources.getInteger(
                                R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }
}
