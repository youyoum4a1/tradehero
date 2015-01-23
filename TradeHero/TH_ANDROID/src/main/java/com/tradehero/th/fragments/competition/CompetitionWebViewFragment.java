package com.tradehero.th.fragments.competition;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.utils.route.THRouter;
import org.jetbrains.annotations.NotNull;

import javax.inject.Inject;

@Routable(
        "providers-enroll/:providerId"
)
public class CompetitionWebViewFragment extends BaseWebViewFragment
{
    @InjectRoute protected ProviderId providerId;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject ProviderUtil providerUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        DaggerUtils.inject(this);
        thRouter.inject(this);
    }

    @Override protected int getLayoutResId()
    {
        return R.layout.fragment_webview;
    }

    @Override @NotNull protected String getLoadingUrl()
    {
        String loadingUrl = super.getLoadingUrl();
        if (loadingUrl == null)
        {
            return providerUtil.getLandingPage(providerId, currentUserId.toUserBaseKey());
        }
        return loadingUrl;
    }

    @Override protected void onProgressChanged(WebView view, int newProgress)
    {
        super.onProgressChanged(view, newProgress);
        Activity activity = getActivity();
        if (activity != null)
        {
            activity.setProgress(newProgress * 100);
        }
    }
}
