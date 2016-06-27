package com.androidth.general.fragments.competition;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import com.androidth.general.activities.IdentityPromptActivity;
import com.androidth.general.activities.SignUpLiveActivity;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.competition.ProviderUtil;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.inject.HierarchyInjector;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.utils.broadcast.BroadcastUtils;
import com.androidth.general.utils.route.THRouter;
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
    protected Integer providerIdInteger;

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

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        super.webView.setOnTouchListener(new View.OnTouchListener()
        {
            @Override public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN)
                {
                    Intent kycIntent = new Intent(getActivity(), IdentityPromptActivity.class);
                    kycIntent.putExtra(SignUpLiveActivity.KYC_CORRESPONDENT_PROVIDER_ID, providerIdInteger);
                    startActivity(kycIntent);
                }

                return false;
            }
        });
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
        } else {
            Uri uri = Uri.parse(loadingUrl);
            providerIdInteger = Integer.parseInt(uri.getQueryParameter("providerId"));
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
