package com.tradehero.th.fragments.competition;

import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.intent.security.SecurityPushBuyIntent;
import timber.log.Timber;

abstract public class CompetitionWebFragmentTHIntentPassedListener implements THIntentPassedListener
{
    public CompetitionWebFragmentTHIntentPassedListener()
    {
        super();
    }

    abstract protected BaseWebViewFragment getApplicableWebViewFragment();
    abstract protected OwnedPortfolioId getApplicablePortfolioId();
    abstract protected ProviderId getProviderId();
    abstract protected Navigator getNavigator();
    abstract protected Class<?> getClassToPop();

    @Override public void onIntentPassed(THIntent thIntent)
    {
        if (thIntent instanceof ProviderPageIntent)
        {
            Timber.d("Intent is ProviderPageIntent");
            if (getApplicableWebViewFragment() != null)
            {
                Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                getApplicableWebViewFragment().loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
            }
            else
            {
                Timber.d("WebFragment is null");
            }
        }
        else if (thIntent instanceof SecurityPushBuyIntent)
        {
            handleSecurityPushBuyIntent((SecurityPushBuyIntent) thIntent);
        }
        else if (thIntent == null)
        {
            getNavigator().popFragment();
        }
        else
        {
            Timber.w("Unhandled intent %s", thIntent);
        }
    }

    protected void handleSecurityPushBuyIntent(SecurityPushBuyIntent thIntent)
    {
    }
}
