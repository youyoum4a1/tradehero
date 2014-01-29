package com.tradehero.th.fragments.leaderboard;

import android.os.Bundle;
import android.view.View;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.CompetitionDTO;
import com.tradehero.th.api.competition.CompetitionId;
import com.tradehero.th.api.competition.ProviderConstants;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.intent.security.SecurityPushBuyIntent;
import com.tradehero.th.models.provider.ProviderSpecificResourcesDTO;
import com.tradehero.th.models.provider.ProviderSpecificResourcesFactory;
import com.tradehero.th.persistence.competition.CompetitionCache;
import com.tradehero.th.persistence.competition.ProviderCache;
import javax.inject.Inject;

/**
 * Created by xavier on 1/23/14.
 */
public class CompetitionLeaderboardMarkUserListViewFragment extends LeaderboardMarkUserListViewFragment
{
    public static final String TAG = CompetitionLeaderboardMarkUserListViewFragment.class.getSimpleName();
    public static final String BUNDLE_KEY_PROVIDER_ID = CompetitionLeaderboardMarkUserListViewFragment.class.getName() + ".providerId";
    public static final String BUNDLE_KEY_COMPETITION_ID = CompetitionLeaderboardMarkUserListViewFragment.class.getName() + ".competitionId";

    protected CompetitionLeaderboardTimedHeader headerView;
    @Inject ProviderCache providerCache;
    protected ProviderId providerId;
    protected ProviderDTO providerDTO;
    @Inject ProviderSpecificResourcesFactory providerSpecificResourcesFactory;
    protected ProviderSpecificResourcesDTO providerSpecificResourcesDTO;
    @Inject CompetitionCache competitionCache;
    protected CompetitionDTO competitionDTO;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private WebViewFragment webViewFragment;


    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        providerId = new ProviderId(getArguments().getBundle(BUNDLE_KEY_PROVIDER_ID));
        providerDTO = providerCache.get(providerId);
        THLog.d(TAG, "providerDTO " + providerDTO);
        providerSpecificResourcesDTO = providerSpecificResourcesFactory.createResourcesDTO(providerDTO);
        THLog.d(TAG, "providerSpecificResourcesDTO " + providerSpecificResourcesDTO);

        CompetitionId competitionId = new CompetitionId(getArguments().getBundle(BUNDLE_KEY_COMPETITION_ID));
        competitionDTO = competitionCache.get(competitionId);
        THLog.d(TAG, "competitionDTO " + competitionDTO);

        this.webViewTHIntentPassedListener = new CompetitionLeaderboardListWebViewTHIntentPassedListener();
    }

    @Override protected int getHeaderViewResId()
    {
        return R.layout.leaderboard_listview_header_competition;
    }

    @Override protected void initHeaderView(View headerView)
    {
        super.initHeaderView(headerView);
        this.headerView = (CompetitionLeaderboardTimedHeader) headerView;
        this.headerView.setCompetitionDTO(competitionDTO);
        this.headerView.setProviderSpecificResourcesDTO(providerSpecificResourcesDTO);
        this.headerView.linkWith(providerDTO, true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.competition_leaderboard_list_menu, menu);

        MenuItem wizardButton = menu.findItem(R.id.btn_wizard);
        if (wizardButton != null)
        {
            wizardButton.setVisible(providerDTO != null && providerDTO.hasWizard());
        }
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId())
        {
            case R.id.btn_wizard:
                pushWizardElement();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();
        // We came back into view so we have to forget the web fragment
        if (this.webViewFragment != null)
        {
            this.webViewFragment.setThIntentPassedListener(null);
        }
        this.webViewFragment = null;
    }

    @Override public void onDestroyView()
    {
        this.headerView = null;
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    private void pushWizardElement()
    {
        Bundle args = new Bundle();
        args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getWizardPage(providerId) + "&previous=whatever");
        args.putBoolean(WebViewFragment.BUNDLE_KEY_IS_OPTION_MENU_VISIBLE, false);
        this.webViewFragment = (WebViewFragment) navigator.pushFragment(WebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private class CompetitionLeaderboardListWebViewTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            if (thIntent instanceof ProviderPageIntent)
            {
                THLog.d(TAG, "Intent is ProviderPageIntent");
                if (webViewFragment != null)
                {
                    THLog.d(TAG, "Passing on " + ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webViewFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
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
                navigator.popFragment();
            }
            else
            {
                THLog.w(TAG, "Unhandled intent " + thIntent);
            }
        }

        protected void handleSecurityPushBuyIntent(SecurityPushBuyIntent thIntent)
        {
            // We are probably coming back from the wizard
            getNavigator().popFragment(CompetitionLeaderboardMarkUserListViewFragment.class.getName());
            // Now moving on
            Bundle argsBundle = thIntent.getBundle();
            if (thIntent.getActionFragment().equals(BuySellFragment.class))
            {
                argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, getApplicablePortfolioId().getArgs());
                argsBundle.putBundle(BuySellFragment.BUNDLE_KEY_PROVIDER_ID_BUNDLE, providerId.getArgs());
            }
            getNavigator().pushFragment(thIntent.getActionFragment(), argsBundle,
                    new int[] {
                            R.anim.slide_right_in, R.anim.alpha_out,
                            R.anim.slide_left_in, R.anim.slide_right_out
                    }, null);
            THLog.d(TAG, "onIntentPassed " + thIntent);
        }
    }
}
