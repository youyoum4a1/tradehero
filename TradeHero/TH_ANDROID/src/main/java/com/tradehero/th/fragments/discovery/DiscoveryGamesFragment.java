package com.tradehero.th.fragments.discovery;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import com.tradehero.th.BottomTabsQuickReturnListViewListener;
import com.tradehero.th.R;
import com.tradehero.th.api.games.GamesDTO;
import com.tradehero.th.api.games.GamesListDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.CompetitionWebViewFragment;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.persistence.games.GamesListCacheRx;
import com.tradehero.th.rx.RxLoaderManager;
import com.tradehero.th.widget.MultiScrollListener;
import javax.inject.Inject;
import butterknife.ButterKnife;
import butterknife.InjectView;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import timber.log.Timber;

public class DiscoveryGamesFragment extends DashboardFragment implements AdapterView.OnItemClickListener
{
    private static final String GAMES_LIST_LOADER_ID = DiscoveryGamesFragment.class.getName() + ".gamesList";

    @InjectView(R.id.progress) ProgressBar mProgressBar;
    @InjectView(R.id.games_list_view) ListView mGamesListView;
    @Inject @BottomTabsQuickReturnListViewListener AbsListView.OnScrollListener dashboardBottomTabsScrollListener;
    @Inject RxLoaderManager rxLoaderManager;
    @Inject CurrentUserId currentUserId;
    @Inject GamesListCacheRx gamesListCacheRx;
    private DiscoveryGamesAdapter discoveryGamesAdapter;
    @NonNull private Subscription gamesListSubscriptions;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discoveryGamesAdapter = new DiscoveryGamesAdapter(getActivity(), R.layout.games_item_view);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_games, container, false);
        initView(view);
        return view;
    }

    private void initView(View view)
    {
        ButterKnife.inject(this, view);

        mGamesListView.setAdapter(discoveryGamesAdapter);
        mGamesListView.setOnItemClickListener(this);
        mGamesListView.setOnScrollListener(new MultiScrollListener(dashboardBottomTabsScrollListener));

        unsubscribe(gamesListSubscriptions);
        mProgressBar.setVisibility(View.VISIBLE);
        gamesListSubscriptions = gamesListCacheRx.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new GamesListCacheObserver());
    }

    @Override public void onDestroyView()
    {
        unsubscribe(gamesListSubscriptions);
        rxLoaderManager.remove(GAMES_LIST_LOADER_ID);
        super.onDestroyView();
    }

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        HierarchyInjector.inject(this);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Bundle args = new Bundle();
        CompetitionWebViewFragment.putUrl(args, ((GamesDTO) discoveryGamesAdapter.getItem(position)).url + "?userId=" + currentUserId.toUserBaseKey().getUserId());
        if (navigator != null)
        {
            navigator.get().pushFragment(CompetitionWebViewFragment.class, args);
        }
    }

    private class GamesListCacheObserver implements Observer<Pair<UserBaseKey, GamesListDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, GamesListDTO> pair)
        {
            discoveryGamesAdapter.setItems(pair.second);
            discoveryGamesAdapter.notifyDataSetChanged();
            mProgressBar.setVisibility(View.GONE);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            Timber.e(e, "Failed fetching games page for key");
        }
    }
}
