package com.tradehero.th.fragments.competition;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import com.tradehero.route.InjectRoute;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.utils.broadcast.BroadcastUtils;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import timber.log.Timber;

@Routable(
        "providers-enroll/:enrollProviderId"
)
public class CompetitionWebViewFragment extends BaseWebViewFragment
{
    @InjectRoute protected ProviderId enrollProviderId;
    @Inject CurrentUserId currentUserId;
    @Inject THRouter thRouter;
    @Inject ProviderUtil providerUtil;
    @Inject BroadcastUtils broadcastUtils;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(this);
        thRouter.inject(this);
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        //noinspection ConstantConditions
        if (enrollProviderId != null && enrollProviderId.key != null)
        {
            CompetitionWebViewFragment.putUrl(getArguments(), providerUtil.getLandingPage(
                    enrollProviderId, currentUserId.toUserBaseKey()));
        }
        CompetitionWebViewFragment.putIsOptionMenuVisible(getArguments(), true);

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
            return providerUtil.getLandingPage(enrollProviderId, currentUserId.toUserBaseKey());
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
