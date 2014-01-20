package com.tradehero.th.models.intent.competition;

import android.net.Uri;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import java.util.List;

/**
 * Created by xavier on 1/10/14.
 */
public class ProviderPageIntent extends OneProviderIntent
{
    public static final String TAG = ProviderPageIntent.class.getSimpleName();

    //<editor-fold desc="Constructors">
    public ProviderPageIntent(ProviderId providerId, String uri)
    {
        super();
        setData(getProviderActionUri(providerId, uri));
    }
    //</editor-fold>

    public Uri getProviderActionUri(ProviderId providerId, String uri)
    {
        return Uri.parse(getProviderActionUriPath(providerId, uri));
    }

    public String getProviderActionUriPath(ProviderId portfolioId, String uri)
    {
        return getString(
                getIntentActionUriResId(),
                getString(R.string.intent_scheme),
                getString(R.string.intent_host_providers),
                portfolioId.key,
                getString(getIntentProviderAction()),
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

    public String getForwardUriPath()
    {
        return getForwardUriPath(getData());
    }

    public static String getForwardUriPath(Uri data)
    {
        return getForwardUriPath(data.getPathSegments());
    }

    public static String getForwardUriPath(List<String> pathSegments)
    {
        return Uri.decode(Uri.decode(pathSegments.get(getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page))));
    }
}
