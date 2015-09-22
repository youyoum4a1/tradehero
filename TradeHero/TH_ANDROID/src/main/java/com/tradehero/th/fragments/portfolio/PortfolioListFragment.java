package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.TabbedPositionListFragment;
import com.tradehero.th.fragments.watchlist.MainWatchlistPositionFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction1;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class PortfolioListFragment extends DashboardFragment
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = PortfolioListFragment.class.getName() + ".userBaseKey";

    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Toolbar toolbar;
    @Inject DrawerLayout drawerLayout;

    @Bind(R.id.portfolio_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.portfolio_list) RecyclerView portfolioList;
    @Bind(R.id.portfolios_progressbar) ProgressBar progressBar;

    protected UserBaseKey shownUserBaseKey;
    protected PortfolioRecyclerAdapter portfolioRecyclerAdapter;

    public static void putUserBaseKey(@NonNull Bundle bundle, @NonNull UserBaseKey userBaseKey)
    {
        bundle.putBundle(USER_BASE_KEY_BUNDLE_KEY, userBaseKey.getArgs());
    }

    @Nullable protected static UserBaseKey getUserBaseKey(@NonNull Bundle args)
    {
        Bundle userBundle = args.getBundle(USER_BASE_KEY_BUNDLE_KEY);
        if (userBundle != null)
        {
            return new UserBaseKey(userBundle);
        }
        return null;
    }

    @Override public boolean shouldShowLiveTradingToggle()
    {
        return true;
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        HierarchyInjector.inject(getActivity(), this);

        shownUserBaseKey = getUserBaseKey(getArguments());
        if (shownUserBaseKey == null)
        {
            shownUserBaseKey = currentUserId.toUserBaseKey();
        }

        portfolioRecyclerAdapter = new PortfolioRecyclerAdapter();
        portfolioRecyclerAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<PortfolioDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<PortfolioDisplayDTO> viewHolder, PortfolioDisplayDTO object)
            {
                if (object.isWatchlist)
                {
                    pushWatchlistPositionFragment();
                }
                else if (object.ownedPortfolioId != null)
                {
                    pushPositionListFragment(object);
                }
            }
        });
    }

    private void pushPositionListFragment(@NonNull PortfolioDisplayDTO object)
    {
        Bundle args = new Bundle();

        if (object.ownedPortfolioId.userId.equals(currentUserId.get()))
        {
            TabbedPositionListFragment.putApplicablePortfolioId(args, object.ownedPortfolioId);
        }
        TabbedPositionListFragment.putGetPositionsDTOKey(args, object.ownedPortfolioId);
        TabbedPositionListFragment.putShownUser(args, object.ownedPortfolioId.getUserBaseKey());

        TabbedPositionListFragment.putIsFX(args, object.assetClass);
        if (object.providerId != null && object.providerId > 0)
        {
            TabbedPositionListFragment.putProviderId(args, new ProviderId(object.providerId));
            navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
            return;
        }
        navigator.get().pushFragment(TabbedPositionListFragment.class, args);
    }

    private void pushWatchlistPositionFragment()
    {
        Bundle args = new Bundle();
        MainWatchlistPositionFragment.putShowActionBarTitle(args, true);
        navigator.get().pushFragment(MainWatchlistPositionFragment.class, args);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setActionBarTitle(R.string.dashboard_portfolios);
    }

    @Nullable @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_portfolios, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                portfolioCompactListCache.invalidate(shownUserBaseKey);
                portfolioCompactListCache.get(shownUserBaseKey);
                userProfileCache.get().invalidate(shownUserBaseKey);
                userProfileCache.get().get(shownUserBaseKey);
            }
        });

        DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();

        portfolioList.setAdapter(portfolioRecyclerAdapter);
        portfolioList.setLayoutManager(new LinearLayoutManager(getActivity()));

        onDestroyViewSubscriptions.add(displayablePortfolioFetchAssistant.get(shownUserBaseKey)
                        .map(new Func1<DisplayablePortfolioDTOList, List<PortfolioDisplayDTO>>()
                        {
                            @Override public List<PortfolioDisplayDTO> call(DisplayablePortfolioDTOList displayablePortfolioDTOs)
                            {
                                ArrayList<PortfolioDisplayDTO> list = new ArrayList<>(displayablePortfolioDTOs.size());
                                for (DisplayablePortfolioDTO dto : displayablePortfolioDTOs)
                                {
                                    list.add(new PortfolioDisplayDTO(getResources(), currentUserId, dto));
                                }
                                return list;
                            }
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Action1<List<PortfolioDisplayDTO>>()
                        {
                            @Override public void call(List<PortfolioDisplayDTO> portfolioDisplayDTOs)
                            {
                                if (portfolioDisplayDTOs.size() > 0)
                                {
                                    progressBar.setVisibility(View.GONE);
                                }
                                swipeRefreshLayout.setRefreshing(false);
                                portfolioRecyclerAdapter.addAll(portfolioDisplayDTOs);
                            }
                        }, new TimberOnErrorAction1("Failed to fetch portfolio list"))
        );
    }
}
