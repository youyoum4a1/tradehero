package com.tradehero.th.fragments.portfolio;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.squareup.picasso.Picasso;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.portfolio.PortfolioListItemAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.portfolio.OwnedPortfolioIdList;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.widget.portfolio.PortfolioListItemView;
import com.tradehero.th.widget.portfolio.PortfolioListView;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:47 AM To change this template use File | Settings | File Templates. */
public class PortfolioListFragment extends DashboardFragment
{
    public static final String TAG = PortfolioListFragment.class.getSimpleName();

    private PortfolioListView portfolioListView;

    private PortfolioListItemAdapter portfolioListAdapter;

    private List<OwnedPortfolioId> otherOwnedPortfolioIds;
    private List<OwnedPortfolioId> ownedPortfolioIds;

    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<PortfolioCompactListCache> portfolioListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;

    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> ownPortfolioListener;
    private AsyncTask<Void, Void, OwnedPortfolioIdList> fetchOwnPortfoliosTask;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        THLog.d(TAG, "onCreateView");
        super.onCreateView(inflater, container, savedInstanceState);
        View view = null;
        view = inflater.inflate(R.layout.fragment_portfolios_list, container, false);
        initViews(view);
        return view;
    }

    private void initViews(View view)
    {
        if (view != null)
        {
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
        actionBar.setTitle(getString(R.string.topbar_portfolios_title));
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onResume()
    {
        super.onResume();
        fetchOwn();
        fetchOther();
    }

    @Override public void onPause()
    {
        ownPortfolioListener = null;
        if (fetchOwnPortfoliosTask != null)
        {
            fetchOwnPortfoliosTask.cancel(false);
        }
        fetchOwnPortfoliosTask = null;
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        if (portfolioListView != null)
        {
            portfolioListView.setOnItemClickListener(null);
        }
        portfolioListView = null;

        super.onDestroyView();
    }

    private void fetchOwn()
    {
        ownPortfolioListener = createOwnPortfolioListener();
        if (fetchOwnPortfoliosTask != null)
        {
            fetchOwnPortfoliosTask.cancel(false);
        }
        fetchOwnPortfoliosTask = portfolioListCache.get().getOrFetch(THUser.getCurrentUserBase().getBaseKey(), false, ownPortfolioListener);
        fetchOwnPortfoliosTask.execute();
    }

    private void fetchOther()
    {
        linkWithOther(portfolioCache.get().getAllOtherUserKeys(), true);
    }

    private List<OwnedPortfolioId> getAllPortfolioIds()
    {
        if (ownedPortfolioIds == null && otherOwnedPortfolioIds == null)
        {
            return null;
        }
        List<OwnedPortfolioId> all = new ArrayList<>();
        if (ownedPortfolioIds != null)
        {
            all.addAll(ownedPortfolioIds);
        }
        if (otherOwnedPortfolioIds != null)
        {
            all.addAll(otherOwnedPortfolioIds);
        }
        return all;
    }

    public void linkWithOther(List<OwnedPortfolioId> otherOwnedPortfolioIds, boolean andDisplay)
    {
        this.otherOwnedPortfolioIds = otherOwnedPortfolioIds;

        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    public void linkWithOwn(List<OwnedPortfolioId> ownedPortfolioIds, boolean andDisplay)
    {
        this.ownedPortfolioIds = ownedPortfolioIds;

        if (andDisplay)
        {
            displayPortfolios();
        }
    }

    public void display()
    {
        displayPortfolios();
    }

    public void displayPortfolios()
    {
        if (portfolioListAdapter != null)
        {
            portfolioListAdapter.setItems(getAllPortfolioIds());
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    PortfolioListItemAdapter adapter = portfolioListAdapter;
                    if (adapter != null)
                    {
                        adapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void handlePortfolioItemClicked(View view, int position, long id)
    {
        if (view instanceof PortfolioListItemView)
        {
            navigator.pushFragment(PositionListFragment.class, ((PortfolioListItemView) view).getOwnedPortfolioId().getArgs());
        }
        else
        {
            THLog.d(TAG, "Not handling portfolioItemClicked " + view.getClass().getName());
        }
    }

    private PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList> createOwnPortfolioListener()
    {
        return new PortfolioCompactListCache.Listener<UserBaseKey, OwnedPortfolioIdList>()
        {
            @Override public void onDTOReceived(UserBaseKey key, OwnedPortfolioIdList value)
            {
                if (key.equals(THUser.getCurrentUserBase().getBaseKey()))
                {
                    linkWithOwn(value, true);
                }
            }
        };
    }

    //<editor-fold desc="BaseFragment.TabBarVisibilityInformer">
    @Override public boolean isTabBarVisible()
    {
        return true;
    }
    //</editor-fold>
}
