package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.tradehero.thm.R;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.watchlist.WatchlistPositionFragment;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.social.VisitedFriendListPrefs;
import com.tradehero.th.utils.metrics.localytics.LocalyticsConstants;
import com.tradehero.th.utils.metrics.localytics.THLocalyticsSession;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

@Deprecated
public class PortfolioListFragment extends DashboardFragment
    implements WithTutorial
{
    private ProgressBar progressBar;
    private PortfolioListView portfolioListView;

    private PortfolioListItemAdapter portfolioListAdapter;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;

    @Inject CurrentUserId currentUserId;
    @Inject THLocalyticsSession localyticsSession;
    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);
        View view = inflater.inflate(R.layout.fragment_portfolios_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();
        displayablePortfolioFetchAssistant.setFetchedListener(new DisplayablePortfolioFetchAssistant.OnFetchedListener()
        {
            @Override public void onFetched()
            {
                displayPortfolios();
            }
        });
        if (view != null)
        {
            progressBar = (ProgressBar) view.findViewById(android.R.id.empty);

            if (portfolioListAdapter == null)
            {
                portfolioListAdapter = new PortfolioListItemAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.portfolio_list_item, R.layout.portfolio_list_header);
            }

            portfolioListView = (PortfolioListView) view.findViewById(R.id.own_portfolios_list);
            if (portfolioListView != null)
            {
                portfolioListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        handlePortfolioItemClicked(view, position, id);
                    }
                });
                portfolioListView.setAdapter(portfolioListAdapter);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getString(R.string.portfolio_topbar_title));
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override public void onResume()
    {
        super.onResume();

        localyticsSession.tagEvent(LocalyticsConstants.TabBar_Portfolio);

        displayProgress(true);
        displayablePortfolioFetchAssistant.fetch(getUserBaseKeys());
    }

    @Override public void onDestroyView()
    {
        displayablePortfolioFetchAssistant.setFetchedListener(null);
        displayablePortfolioFetchAssistant = null;

        if (portfolioListView != null)
        {
            portfolioListView.setOnItemClickListener(null);
        }
        portfolioListView = null;

        super.onDestroyView();
    }

    protected List<UserBaseKey> getUserBaseKeys()
    {
        List<UserBaseKey> userBaseKeys = VisitedFriendListPrefs.getVisitedIdList();
        userBaseKeys.add(currentUserId.toUserBaseKey());
        return userBaseKeys;
    }

    public void display()
    {
        displayPortfolios();
    }

    public void displayProgress(boolean running)
    {
        if (progressBar != null)
        {
            progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        }
    }

    public void displayPortfolios()
    {
        displayProgress(false);
        portfolioListAdapter.setItems(getAllPortfolios());
        portfolioListAdapter.notifyDataSetChanged();
    }

    private List<DisplayablePortfolioDTO> getAllPortfolios()
    {
        if (displayablePortfolioFetchAssistant != null)
        {
            return displayablePortfolioFetchAssistant.getDisplayablePortfolios();
        }
        return null;
    }

    private void handlePortfolioItemClicked(View view, int position, long id)
    {
        if (view instanceof PortfolioListItemView && portfolioListAdapter.getItem(position) instanceof DisplayablePortfolioDTO)
        {
            Bundle args = new Bundle();

            DisplayablePortfolioDTO displayablePortfolioDTO = (DisplayablePortfolioDTO) portfolioListAdapter.getItem(position);

            if (displayablePortfolioDTO.portfolioDTO != null && displayablePortfolioDTO.portfolioDTO.isWatchlist)
            {
                WatchlistPositionFragment.putOwnedPortfolioId(args, displayablePortfolioDTO.ownedPortfolioId);
                getDashboardNavigator().pushFragment(WatchlistPositionFragment.class, args);
            }
            else
            {
                PositionListFragment.putGetPositionsDTOKey(args, displayablePortfolioDTO.ownedPortfolioId);
                PositionListFragment.putShownUser(args, displayablePortfolioDTO.ownedPortfolioId.getUserBaseKey());
                getDashboardNavigator().pushFragment(PositionListFragment.class, args);
            }
        }
        else
        {
            Timber.d("Not handling portfolioItemClicked %s", view.getClass().getName());
        }
    }

    //<editor-fold desc="WithTutorial">
    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_portfolio_list;
    }
    //</editor-fold>
}
