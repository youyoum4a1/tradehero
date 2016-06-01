package com.ayondo.academy.fragments.competition;

import android.os.Bundle;
import com.ayondo.academy.api.competition.ProviderId;
import com.ayondo.academy.api.portfolio.OwnedPortfolioId;
import com.ayondo.academy.fragments.DashboardNavigator;
import com.ayondo.academy.fragments.trade.AbstractBuySellFragment;
import com.ayondo.academy.fragments.trade.BuySellStockFragment;
import com.ayondo.academy.fragments.web.BaseWebViewIntentFragment;
import com.ayondo.academy.models.intent.THIntent;
import com.ayondo.academy.models.intent.THIntentPassedListener;
import com.ayondo.academy.models.intent.competition.ProviderPageIntent;
import com.ayondo.academy.models.intent.security.SecurityPushBuyIntent;
import timber.log.Timber;

abstract public class CompetitionWebFragmentTHIntentPassedListener implements THIntentPassedListener
{
    public CompetitionWebFragmentTHIntentPassedListener()
    {
        super();
    }

    abstract protected BaseWebViewIntentFragment getApplicableWebViewFragment();

    abstract protected OwnedPortfolioId getApplicablePortfolioId();

    abstract protected ProviderId getProviderId();

    abstract protected DashboardNavigator getNavigator();

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
        // We are probably coming back from the wizard
        getNavigator().popFragment(getClassToPop().getName());
        // Now moving on
        Bundle argsBundle = thIntent.getBundle();
        if (thIntent.getActionFragment().equals(BuySellStockFragment.class))
        {
            BuySellStockFragment.putRequisite(
                    argsBundle,
                    new AbstractBuySellFragment.Requisite(
                            thIntent.getSecurityId(),
                            getApplicablePortfolioId(),
                            0));
        }
        getNavigator().pushFragment(thIntent.getActionFragment(), argsBundle, null);
        Timber.d("onIntentPassed %s", thIntent);
    }
}
