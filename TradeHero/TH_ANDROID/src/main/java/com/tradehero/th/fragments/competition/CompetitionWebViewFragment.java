package com.ayondo.academy.fragments.competition;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.ayondo.academy.R;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.competition.ProviderUtil;
import com.ayondo.academy.fragments.web.BaseWebViewIntentFragment;
import com.ayondo.academy.inject.HierarchyInjector;
import com.ayondo.academy.models.intent.THIntent;
import com.ayondo.academy.models.intent.THIntentPassedListener;
import com.ayondo.academy.models.intent.competition.ProviderPageIntent;
import com.ayondo.academy.utils.broadcast.BroadcastUtils;
import com.ayondo.academy.utils.route.THRouter;
import javax.inject.Inject;
import timber.log.Timber;

@Routable({
        "providers-enroll/:enrollProviderId",
        "providers-enroll/:enrollProviderId/pages/:encodedUrl",
})
public class CompetitionWebViewFragment extends BaseWebViewIntentFragment
{
    @RouteProperty("enrollProviderId") protected Integer enrollProviderId;
    @RouteProperty("encodedUrl") protected String encodedUrl;
    @Inject THRouter thRouter;
    @Inject ProviderUtil providerUtil;
    @Inject BroadcastUtils broadcastUtils;

    protected ProviderId providerId;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);

        if (enrollProviderId != null)
        {
            providerId = new ProviderId(enrollProviderId);
        }

        if (encodedUrl != null)
        {
            CompetitionWebViewFragment.putUrl(getArguments(), Uri.decode(encodedUrl));
        }
        else if (providerId != null)
        {
            CompetitionWebViewFragment.putUrl(getArguments(), providerUtil.getLandingPage(
                    providerId));
        }
        CompetitionWebViewFragment.putIsOptionMenuVisible(getArguments(), true);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        thIntentPassedListener = createCompetitionTHIntentPassedListener();
        setThIntentPassedListener(thIntentPassedListener);
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu.competition_webview_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.webview_done:
                navigator.get().popFragment();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    @Override @NonNull protected String getLoadingUrl()
    {
        String loadingUrl = super.getLoadingUrl();
        if (loadingUrl == null)
        {
            return providerUtil.getLandingPage(providerId);
        }
        return loadingUrl;
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
        broadcastUtils.nextPlease();
    }

    //<editor-fold desc="Intent Listener">
    protected THIntentPassedListener createCompetitionTHIntentPassedListener()
    {
        return new CompetitionTHIntentPassedListener();
    }

    protected class CompetitionTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }
    //</editor-fold>
}
