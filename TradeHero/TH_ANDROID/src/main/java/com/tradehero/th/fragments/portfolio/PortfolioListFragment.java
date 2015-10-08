package com.tradehero.th.fragments.portfolio;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
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
import com.tradehero.th.api.competition.ProviderDTO;
import com.tradehero.th.api.competition.ProviderDTOList;
import com.tradehero.th.api.competition.ProviderId;
import com.tradehero.th.api.competition.ProviderUtil;
import com.tradehero.th.api.competition.key.ProviderListKey;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTO;
import com.tradehero.th.api.portfolio.DisplayablePortfolioDTOList;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.fragments.position.CompetitionLeaderboardPositionListFragment;
import com.tradehero.th.fragments.position.PositionListFragment;
import com.tradehero.th.fragments.watchlist.MainWatchlistPositionFragment;
import com.tradehero.th.fragments.web.BaseWebViewIntentFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.intent.THIntent;
import com.tradehero.th.models.intent.THIntentPassedListener;
import com.tradehero.th.models.intent.competition.ProviderIntent;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.models.portfolio.DisplayablePortfolioFetchAssistant;
import com.tradehero.th.persistence.competition.ProviderListCacheRx;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.widget.OffOnViewSwitcherEvent;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public class PortfolioListFragment extends DashboardFragment
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = PortfolioListFragment.class.getName() + ".userBaseKey";

    @Inject Provider<DisplayablePortfolioFetchAssistant> displayablePortfolioFetchAssistantProvider;

    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;
    @Inject protected ProviderListCacheRx providerListCache;
    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject ProviderUtil providerUtil;

    @Bind(R.id.portfolio_refresh) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.portfolio_list) RecyclerView portfolioList;
    @Bind(R.id.portfolios_progressbar) ProgressBar progressBar;

    protected UserBaseKey shownUserBaseKey;
    protected PortfolioRecyclerAdapter portfolioRecyclerAdapter;
    private BaseWebViewIntentFragment webFragment;
    private THIntentPassedListener thIntentPassedListener;

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

    @Override public void onLiveTradingChanged(OffOnViewSwitcherEvent event)
    {
        super.onLiveTradingChanged(event);
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

        portfolioRecyclerAdapter = new PortfolioRecyclerAdapter(getActivity());
        portfolioRecyclerAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<PortfolioDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<PortfolioDisplayDTO> viewHolder, PortfolioDisplayDTO object)
            {
                if (object.isWatchlist)
                {
                    pushWatchlistPositionFragment();
                }
                else if (object.isCompetition)
                {
                    // HACK Just in case the user eventually enrolls
                    portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
                    Bundle args = new Bundle();
                    CompetitionWebViewFragment.putUrl(args, providerUtil.getLandingPage(
                            new ProviderId(object.providerId)
                    ));
                    webFragment = navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
                    webFragment.setThIntentPassedListener(thIntentPassedListener);
                }
                else if (object.ownedPortfolioId != null)
                {
                    pushPositionListFragment(object);
                }
            }
        });

        this.thIntentPassedListener = new PortfolioTHIntentPassedListener();
    }

    private void pushPositionListFragment(@NonNull PortfolioDisplayDTO object)
    {
        Bundle args = new Bundle();

        if (object.ownedPortfolioId.userId.equals(currentUserId.get()))
        {
            PositionListFragment.putApplicablePortfolioId(args, object.ownedPortfolioId);
        }
        PositionListFragment.putGetPositionsDTOKey(args, object.ownedPortfolioId);
        PositionListFragment.putShownUser(args, object.ownedPortfolioId.getUserBaseKey());

        //PositionListFragment.putIsFX(args, object.assetClass);
        if (object.providerId != null && object.providerId > 0)
        {
            CompetitionLeaderboardPositionListFragment.putProviderId(args, new ProviderId(object.providerId));
            navigator.get().pushFragment(CompetitionLeaderboardPositionListFragment.class, args);
            return;
        }
        navigator.get().pushFragment(PositionListFragment.class, args);
    }

    private void pushWatchlistPositionFragment()
    {
        Bundle args = new Bundle();
        MainWatchlistPositionFragment.putShowActionBarTitle(args, true);
        navigator.get().pushFragment(MainWatchlistPositionFragment.class, args);
    }

    @Override public void onResume()
    {
        super.onResume();
        detachWebFragment();
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

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
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
                providerListCache.invalidateAll();
                providerListCache.get(new ProviderListKey());
            }
        });

        DisplayablePortfolioFetchAssistant displayablePortfolioFetchAssistant = displayablePortfolioFetchAssistantProvider.get();

        portfolioList.setAdapter(portfolioRecyclerAdapter);
        portfolioList.setLayoutManager(new LinearLayoutManager(getActivity()));

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                displayablePortfolioFetchAssistant.get(shownUserBaseKey)
                        .map(new Func1<DisplayablePortfolioDTOList, List<PortfolioDisplayDTO>>()
                        {
                            @Override public List<PortfolioDisplayDTO> call(DisplayablePortfolioDTOList displayablePortfolioDTOs)
                            {
                                ArrayList<PortfolioDisplayDTO> list = new ArrayList<>(displayablePortfolioDTOs.size());
                                for (int i = 0; i < displayablePortfolioDTOs.size(); i++)
                                {
                                    DisplayablePortfolioDTO dto = displayablePortfolioDTOs.get(i);
                                    list.add(new PortfolioDisplayDTO(getResources(), currentUserId, dto));
                                }
                                return list;
                            }
                        }),
                providerListCache.get(new ProviderListKey())
                        .map(new Func1<Pair<ProviderListKey, ProviderDTOList>, List<PortfolioDisplayDTO>>()
                        {
                            @Override public List<PortfolioDisplayDTO> call(Pair<ProviderListKey, ProviderDTOList> providerListKeyProviderDTOListPair)
                            {
                                ArrayList<PortfolioDisplayDTO> list = new ArrayList<>(providerListKeyProviderDTOListPair.second.size());
                                for (ProviderDTO providerDTO : providerListKeyProviderDTOListPair.second)
                                {
                                    if (!providerDTO.isUserEnrolled)
                                    {
                                        list.add(new PortfolioDisplayDTO(getResources(), providerDTO));
                                    }
                                }
                                return list;
                            }
                        })
                , new Func2<List<PortfolioDisplayDTO>, List<PortfolioDisplayDTO>, List<PortfolioDisplayDTO>>()
                {
                    @Override public List<PortfolioDisplayDTO> call(List<PortfolioDisplayDTO> portfolioDisplayDTOs,
                            List<PortfolioDisplayDTO> portfolioDisplayDTOs2)
                    {
                        portfolioDisplayDTOs.addAll(portfolioDisplayDTOs2);
                        return portfolioDisplayDTOs;
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<PortfolioDisplayDTO>>()
                {
                    @Override public void call(List<PortfolioDisplayDTO> portfolioDisplayDTOs)
                    {
                        finishLoading(portfolioDisplayDTOs);
                    }
                }, new TimberOnErrorAction1("Error fetching data")));
    }

    protected void finishLoading(List<PortfolioDisplayDTO> portfolioDisplayDTOs)
    {
        if (portfolioDisplayDTOs.size() > 0)
        {
            progressBar.setVisibility(View.GONE);
        }
        swipeRefreshLayout.setRefreshing(false);
        portfolioRecyclerAdapter.addAll(portfolioDisplayDTOs);
    }

    @Override public void onDestroy()
    {
        this.thIntentPassedListener = null;
        detachWebFragment();
        super.onDestroy();
    }

    protected class PortfolioTHIntentPassedListener implements THIntentPassedListener
    {
        @Override public void onIntentPassed(THIntent thIntent)
        {
            Timber.d("LeaderboardCommunityTHIntentPassedListener " + thIntent);
            if (thIntent instanceof ProviderIntent)
            {
                // Just in case the user has enrolled
                portfolioCompactListCache.invalidate(currentUserId.toUserBaseKey());
                providerListCache.invalidateAll();
            }

            if (thIntent instanceof ProviderPageIntent)
            {
                Timber.d("Intent is ProviderPageIntent");
                if (webFragment != null)
                {
                    Timber.d("Passing on %s", ((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                    webFragment.loadUrl(((ProviderPageIntent) thIntent).getCompleteForwardUriPath());
                }
                else
                {
                    Timber.d("WebFragment is null");
                }
            }
            else
            {
                Timber.w("Unhandled intent %s", thIntent);
            }
        }
    }
}
