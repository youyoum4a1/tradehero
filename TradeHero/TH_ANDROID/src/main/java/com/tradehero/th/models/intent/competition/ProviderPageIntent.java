package com.tradehero.th.models.intent.competition;

import android.net.Uri;
import android.support.v4.app.Fragment;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.utils.DaggerUtils;
import java.util.List;
import javax.inject.Inject;
import retrofit.Server;

/**
 * Created by xavier on 1/10/14.
 */
public class ProviderPageIntent extends OneProviderIntent
{
    public static final String TAG = ProviderPageIntent.class.getSimpleName();
    @Inject Server apiServer;

    //<editor-fold desc="Constructors">
    public ProviderPageIntent(ProviderId providerId, String uri)
    {
        super();
        setData(getProviderActionUri(providerId, uri));

        DaggerUtils.inject(this);
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

    public String getCompleteForwardUriPath()
    {
        return apiServer.getUrl() + getForwardUriPath();
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
        // Only 1 decode is necessary here as the getDataPathSegments already does one.
        return Uri.decode(pathSegments.get(getInteger(R.integer.intent_uri_action_provider_path_index_encoded_page)));
    }
}
