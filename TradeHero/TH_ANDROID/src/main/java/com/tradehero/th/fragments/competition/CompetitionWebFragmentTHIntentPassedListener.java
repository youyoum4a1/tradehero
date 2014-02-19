package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.base.Navigator;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.intent.security.SecurityPushBuyIntent;

/**
 * Created by xavier on 1/29/14.
 */
abstract public class CompetitionWebFragmentTHIntentPassedListener implements THIntentPassedListener
{
    public static final String TAG = CompetitionWebFragmentTHIntentPassedListener.class.getSimpleName();

    public CompetitionWebFragmentTHIntentPassedListener()
    {
        super();
    }

    abstract protected WebViewFragment getApplicableWebViewFragment();
    abstract protected OwnedPortfolioId getApplicablePortfolioId();
    abstract protected ProviderId getProviderId();
    abstract protected Navigator getNavigator();
    abstract protected Class<?> getClassToPop();

    @Override public void onIntentPassed(THIntent thIntent)
    {
        if (thIntent instanceof ProviderPageIntent)
        {
            THLog.d(TAG, "Intent is ProviderPageIntent");
            if (getApplicableWebViewFragment() != null)
            {
                THLog.d(TAG, "Passing on " + ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                getApplicableWebViewFragment().loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
            }
            else
            {
                THLog.d(TAG, "WebFragment is null");
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
            THLog.w(TAG, "Unhandled intent " + thIntent);
        }
    }

    protected void handleSecurityPushBuyIntent(SecurityPushBuyIntent thIntent)
    {
        // We are probably coming back from the wizard
        getNavigator().popFragment(getClassToPop().getName());
        // Now moving on
        Bundle argsBundle = thIntent.getBundle();
        if (thIntent.getActionFragment().equals(BuySellFragment.class))
        {
            argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
            argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, getProviderId().getArgs());
        }
        getNavigator().pushFragment(thIntent.getActionFragment(), argsBundle,
                new int[] {
                        R.anim.slide_right_in, R.anim.alpha_out,
                        R.anim.slide_left_in, R.anim.slide_right_out
                }, null);
        THLog.d(TAG, "onIntentPassed " + thIntent);
    }
}
