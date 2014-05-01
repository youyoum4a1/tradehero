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
import com.localytics.android.LocalyticsSession;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
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
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:47 AM To change this template use File | Settings | File Templates. */
@Deprecated
public class PortfolioListFragment extends DashboardFragment
    implements WithTutorial
{
    public static final String TAG = PortfolioListFragment.class.getSimpleName();

    private ProgressBar progressBar;
    private PortfolioListView portfolioListView;

    private PortfolioListItemAdapter portfolioListAdapter;
    private DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant;

    @Inject CurrentUserId currentUserId;
    @Inject LocalyticsSession localyticsSession;

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
        displayablePortfolioFetchAssistant = new DisplayablePortfolioFetchAssistant();
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
        ActionBar actionBar = getSherlockActivity().getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_TITLE | ActionBar.DISPLAY_SHOW_HOME);
        actionBar.setDisplayHomeAsUpEnabled(isDisplayHomeAsUpEnabled());
        actionBar.setTitle(getString(R.string.portfolio_topbar_title));
        super.onCreateOptionsMenu(menu, inflater);
    }

    public boolean isDisplayHomeAsUpEnabled()
    {
        return false;
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
                getNavigator().pushFragment(WatchlistPositionFragment.class, args);
            }
            else
            {
                args.putBundle(PositionListFragment.BUNDLE_KEY_SHOW_PORTFOLIO_ID_BUNDLE, displayablePortfolioDTO.ownedPortfolioId.getArgs());
                getNavigator().pushFragment(PositionListFragment.class, args);
            }
        }
        else
        {
            THLog.d(TAG, "Not handling portfolioItemClicked " + view.getClass().getName());
        }
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>

    //<editor-fold desc="WithTutorial">
    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_portfolio_list;
    }
    //</editor-fold>
}
