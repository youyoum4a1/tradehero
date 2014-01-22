package com.tradehero.th.fragments.competition;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.api.competition.ProviderConstants;
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneLegalDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneTradeNowDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneWizardDTO;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.fragments.web.WebViewFragment;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;

/**
 * Created by xavier on 1/17/14.
 */
public class MainCompetitionFragment extends CompetitionFragment
{
    public static final String TAG = MainCompetitionFragment.class.getSimpleName();

    private ActionBar actionBar;
    private ProgressBar progressBar;
    private AbsListView listView;
    private CompetitionZoneListItemAdapter competitionZoneListItemAdapter;

    private THIntentPassedListener webViewTHIntentPassedListener;
    private WebViewFragment webViewFragment;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.webViewTHIntentPassedListener = new MainCompetitionWebViewTHIntentPassedListener();
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_competition, container, false);
        initViews(view);
        return view;
    }

    @Override protected void initViews(View view)
    {
        this.competitionZoneListItemAdapter = new CompetitionZoneListItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.competition_zone_item,
                R.layout.competition_zone_trade_now,
                R.layout.competition_zone_header,
                R.layout.competition_zone_legal_mentions);
        this.competitionZoneListItemAdapter.setParentOnLegalElementClicked(new MainCompetitionLegalClickedListener());

        this.progressBar = (ProgressBar) view.findViewById(android.R.id.empty);
        if (this.progressBar != null)
        {
            this.progressBar.setVisibility(View.VISIBLE);
        }
        this.listView = (AbsListView) view.findViewById(R.id.competition_zone_list);
        if (this.listView != null)
        {
            this.listView.setAdapter(this.competitionZoneListItemAdapter);
            this.listView.setOnItemClickListener(new MainCompetitionFragmentItemClickListener());
        }
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        displayActionBarTitle();

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyOptionsMenu()
    {
        super.onDestroyOptionsMenu();
        this.actionBar = null;
    }
    //</editor-fold>

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
        if (this.competitionZoneListItemAdapter != null)
        {
            this.competitionZoneListItemAdapter.setParentOnLegalElementClicked(null);
        }
        this.competitionZoneListItemAdapter = null;

        this.progressBar = null;

        if (this.listView != null)
        {
            this.listView.setOnItemClickListener(null);
        }
        this.listView = null;

        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        this.webViewTHIntentPassedListener = null;
        super.onDestroy();
    }

    @Override protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        this.competitionZoneListItemAdapter.setProviderAndUser(providerDTO);
        this.competitionZoneListItemAdapter.notifyDataSetChanged();
        if (progressBar != null)
        {
            progressBar.setVisibility(View.GONE);
        }
        if (andDisplay)
        {
            displayActionBarTitle();
        }
    }

    @Override public boolean isTabBarVisible()
    {
        return false;
    }

    private void displayActionBarTitle()
    {
        if (this.actionBar != null)
        {
            if (this.providerDTO == null || this.providerDTO.name == null)
            {
                this.actionBar.setTitle("");
            }
            else
            {
                this.actionBar.setTitle(this.providerDTO.name);
            }
        }
    }

    private void handleItemClicked(CompetitionZoneDTO competitionZoneDTO)
    {
        if (competitionZoneDTO instanceof CompetitionZoneTradeNowDTO)
        {
            pushTradeNowElement((CompetitionZoneTradeNowDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
        {
            pushPortfolioElement((CompetitionZonePortfolioDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneVideoDTO)
        {
            pushVideoElement((CompetitionZoneVideoDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneWizardDTO)
        {
            pushWizardElement((CompetitionZoneWizardDTO) competitionZoneDTO);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneLegalDTO)
        {
            pushLegalElement((CompetitionZoneLegalDTO) competitionZoneDTO);
        }

        // TODO others?
    }

    private void pushTradeNowElement(CompetitionZoneTradeNowDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
        args.putBundle(ProviderSecurityListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, userInteractor.getApplicablePortfolioId().getArgs());
        navigator.pushFragment(ProviderSecurityListFragment.class, args);
    }

    private void pushPortfolioElement(CompetitionZonePortfolioDTO competitionZoneDTO)
    {
        // TODO We need to be able to launch async when the portfolio Id is finally not null
        OwnedPortfolioId portfolioId = userInteractor.getApplicablePortfolioId();
        if (portfolioId != null)
        {
            Bundle args = new Bundle();
            args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, portfolioId.getArgs());
            navigator.pushFragment(PositionListFragment.class, args);
        }
    }

    private void pushVideoElement(CompetitionZoneVideoDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PROVIDER_ID, providerId.getArgs());
        args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE,
                providerDTO.associatedPortfolio.getPortfolioId().getArgs());
        navigator.pushFragment(ProviderVideoListFragment.class, args);
    }

    private void pushWizardElement(CompetitionZoneWizardDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getWizardPage(providerId) + "&previous=whatever");
        this.webViewFragment = (WebViewFragment) navigator.pushFragment(WebViewFragment.class, args);
        this.webViewFragment.setThIntentPassedListener(this.webViewTHIntentPassedListener);
    }

    private void pushLegalElement(CompetitionZoneLegalDTO competitionZoneDTO)
    {
        Bundle args = new Bundle();
        if ((competitionZoneDTO).requestedLink.equals(CompetitionZoneLegalDTO.LinkType.RULES))
        {
            args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getRulesPage(providerId));
        }
        else
        {
            args.putString(WebViewFragment.BUNDLE_KEY_URL, ProviderConstants.getTermsPage(providerId));
        }
        navigator.pushFragment(WebViewFragment.class, args);
    }

    private class MainCompetitionFragmentItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            THLog.d(TAG, "onItemClient");
            handleItemClicked((CompetitionZoneDTO) adapterView.getItemAtPosition(i));
        }
    }

    private class MainCompetitionWebViewTHIntentPassedListener implements THIntentPassedListener
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
            else if (thIntent == null)
            {
                navigator.popFragment();
            }
            else
            {
                THLog.w(TAG, "Unhandled intent " + thIntent);
            }
        }
    }

    private class MainCompetitionLegalClickedListener implements CompetitionZoneLegalMentionsView.OnElementClickedListener
    {
        @Override public void onElementClicked(CompetitionZoneDTO competitionZoneLegalDTO)
        {
            handleItemClicked(competitionZoneLegalDTO);
        }
    }
}
