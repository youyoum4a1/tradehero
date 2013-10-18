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
import com.tradehero.common.utils.THLog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.portfolio.PortfolioItemHeaderAdapter;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.base.THUser;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.network.service.PortfolioService;
import com.tradehero.th.persistence.portfolio.PortfolioCache;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCache;
import com.tradehero.th.widget.portfolio.PortfolioListView;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 10/14/13 Time: 11:47 AM To change this template use File | Settings | File Templates. */
public class PortfolioListFragment extends DashboardFragment
{
    public static final String TAG = PortfolioListFragment.class.getSimpleName();

    private PortfolioListView ownPortfoliosView;
    private PortfolioListView otherPortfoliosView;

    private PortfolioItemHeaderAdapter ownPortfolioAdapter;
    private PortfolioItemHeaderAdapter otherPortfolioAdapter;

    private List<OwnedPortfolioId> otherOwnedPortfolioIds;
    private List<OwnedPortfolioId> ownedPortfolioIds;

    @Inject Lazy<PortfolioService> portfolioService;
    @Inject Lazy<PortfolioCompactListCache> portfolioListCache;
    @Inject Lazy<PortfolioCache> portfolioCache;

    private PortfolioCompactListCache.Listener<UserBaseKey, List<OwnedPortfolioId>> ownPortfolioListener;
    private AsyncTask<Void, Void, List<OwnedPortfolioId>> fetchOwnPortfoliosTask;

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
            if (ownPortfolioAdapter == null)
            {
                ownPortfolioAdapter = new PortfolioItemHeaderAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.portfolio_header_item);
            }

            ownPortfoliosView = (PortfolioListView) view.findViewById(R.id.own_portfolios_list);
            if (ownPortfoliosView != null)
            {
                ownPortfoliosView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        handleOwnItemClicked(view, position, id);
                    }
                });
                ownPortfoliosView.setAdapter(ownPortfolioAdapter);
            }

            if (otherPortfolioAdapter == null)
            {
                otherPortfolioAdapter = new PortfolioItemHeaderAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.portfolio_header_item);
            }

            otherPortfoliosView = (PortfolioListView) view.findViewById(R.id.other_portfolios_list);
            if (otherPortfoliosView != null)
            {
                otherPortfoliosView.setOnItemClickListener(new AdapterView.OnItemClickListener()
                {
                    @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
                    {
                        handleOtherItemClicked(view, position, id);
                    }
                });
                otherPortfoliosView.setAdapter(otherPortfolioAdapter);
            }
        }
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        createOptionsMenu();
    }

    private void createOptionsMenu()
    {
        getSherlockActivity().getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        getSherlockActivity().getSupportActionBar().setCustomView(R.layout.topbar_portfolios_list);
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
        if (ownPortfoliosView != null)
        {
            ownPortfoliosView.setOnItemClickListener(null);
        }
        ownPortfoliosView = null;

        if (otherPortfoliosView != null)
        {
            otherPortfoliosView.setOnItemClickListener(null);
        }
        otherPortfoliosView = null;

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

    public void linkWithOther(List<OwnedPortfolioId> otherOwnedPortfolioIds, boolean andDisplay)
    {
        this.otherOwnedPortfolioIds = otherOwnedPortfolioIds;

        if (andDisplay)
        {
            displayOtherPortfolios();
        }
    }

    public void linkWithOwn(List<OwnedPortfolioId> ownedPortfolioIds, boolean andDisplay)
    {
        this.ownedPortfolioIds = ownedPortfolioIds;

        if (andDisplay)
        {
            displayOwnPortfolios();
        }
    }

    public void display()
    {
        displayOwnPortfolios();
        displayOtherPortfolios();
    }

    public void displayOwnPortfolios()
    {
        if (ownPortfolioAdapter != null && ownedPortfolioIds != null)
        {
            ownPortfolioAdapter.setItems(ownedPortfolioIds);
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    if (ownPortfolioAdapter != null)
                    {
                        ownPortfolioAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    public void displayOtherPortfolios()
    {
        if (otherPortfoliosView != null && otherOwnedPortfolioIds != null)
        {
            otherPortfolioAdapter.setItems(otherOwnedPortfolioIds);
            getView().post(new Runnable()
            {
                @Override public void run()
                {
                    if (otherPortfolioAdapter != null)
                    {
                        otherPortfolioAdapter.notifyDataSetChanged();
                    }
                }
            });
        }
    }

    private void handleOwnItemClicked(View view, int position, long id)
    {
        navigator.pushFragment(PositionListFragment.class, ownedPortfolioIds.get(position).getArgs());
    }

    private void handleOtherItemClicked(View view, int position, long id)
    {
        navigator.pushFragment(PositionListFragment.class, otherOwnedPortfolioIds.get(position).getArgs());
    }

    private PortfolioCompactListCache.Listener<UserBaseKey, List<OwnedPortfolioId>> createOwnPortfolioListener()
    {
        return new PortfolioCompactListCache.Listener<UserBaseKey, List<OwnedPortfolioId>>()
        {
            @Override public void onDTOReceived(UserBaseKey key, List<OwnedPortfolioId> value)
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
