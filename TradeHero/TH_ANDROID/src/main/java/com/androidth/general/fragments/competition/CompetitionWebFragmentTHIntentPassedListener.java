package com.androidth.general.fragments.competition;

import android.os.Bundle;
import com.androidth.general.api.competition.ProviderId;
import com.androidth.general.api.portfolio.OwnedPortfolioId;
import com.androidth.general.fragments.DashboardNavigator;
import com.androidth.general.fragments.trade.AbstractBuySellFragment;
import com.androidth.general.fragments.trade.BuySellStockFragment;
import com.androidth.general.fragments.web.BaseWebViewIntentFragment;
import com.androidth.general.models.intent.THIntent;
import com.androidth.general.models.intent.THIntentPassedListener;
import com.androidth.general.models.intent.competition.ProviderPageIntent;
import com.androidth.general.models.intent.security.SecurityPushBuyIntent;
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
//        argsBundle.putParcelable(AbstractBuySellFragment.BUNDLE_KEY_SECURITY_DTO, thIntent);
        getNavigator().pushFragment(thIntent.getActionFragment(), argsBundle, null);
        Timber.d("onIntentPassed %s", thIntent);
    }
}
