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
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZonePortfolioDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneTradeNowDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneVideoDTO;
import com.tradehero.th.fragments.competition.zone.CompetitionZoneWizardDTO;

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

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.fragment_main_competition, container, false);

        this.competitionZoneListItemAdapter = new CompetitionZoneListItemAdapter(
                getActivity(),
                getActivity().getLayoutInflater(),
                R.layout.competition_zone_item,
                R.layout.competition_zone_trade_now,
                R.layout.competition_zone_header,
                R.layout.competition_zone_legal_mentions);

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
        return view;
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

    @Override protected void linkWith(ProviderDTO providerDTO, boolean andDisplay)
    {
        super.linkWith(providerDTO, andDisplay);
        this.competitionZoneListItemAdapter.setProvider(providerDTO);
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

        }
        else if (competitionZoneDTO instanceof CompetitionZonePortfolioDTO)
        {

        }
        else if (competitionZoneDTO instanceof CompetitionZoneVideoDTO)
        {
            Bundle args = new Bundle();
            args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PROVIDER_ID, providerDTO.getProviderId().getArgs());
            args.putBundle(ProviderVideoListFragment.BUNDLE_KEY_PURCHASE_APPLICABLE_PORTFOLIO_ID_BUNDLE, providerDTO.associatedPortfolio.getPortfolioId().getArgs());
            navigator.pushFragment(ProviderVideoListFragment.class, args);
        }
        else if (competitionZoneDTO instanceof CompetitionZoneWizardDTO)
        {

        }
        // TODO others?
    }

    private class MainCompetitionFragmentItemClickListener implements AdapterView.OnItemClickListener
    {
        @Override public void onItemClick(AdapterView<?> adapterView, View view, int i, long l)
        {
            THLog.d(TAG, "onItemClient");
            handleItemClicked((CompetitionZoneDTO) adapterView.getItemAtPosition(i));
        }
    }
}
