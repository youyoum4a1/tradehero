package com.tradehero.th.fragments.competition;

import android.app.Activity;
import android.content.Context;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import javax.inject.Inject;
import timber.log.Timber;

public class CompetitionEnrollmentWebViewFragment extends CompetitionWebViewFragment
{
    @Inject Context dummyContextDoNotRemove;

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);

        ProviderId providerId = new ProviderId(getArguments());
        CompetitionWebViewFragment.putUrl(getArguments(), providerUtil.getLandingPage(
                providerId,
                currentUserId.toUserBaseKey()));
        CompetitionWebViewFragment.putIsOptionMenuVisible(getArguments(), true);

        thIntentPassedListener = createCompetitionTHIntentPassedListener();
        setThIntentPassedListener(thIntentPassedListener);
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
