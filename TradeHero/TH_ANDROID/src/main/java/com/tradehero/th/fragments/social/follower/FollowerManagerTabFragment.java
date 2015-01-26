package com.tradehero.th.fragments.social.follower;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.portfolio.OwnedPortfolioId;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.utils.route.THRouter;
import javax.inject.Inject;
import rx.Observer;
import rx.Subscription;
import rx.android.observables.AndroidObservable;
import timber.log.Timber;

abstract public class FollowerManagerTabFragment extends BasePurchaseManagerFragment
        implements SwipeRefreshLayout.OnRefreshListener
{
    public static final int ITEM_ID_REFRESH_MENU = 0;
    private static final String HERO_ID_BUNDLE_KEY =
            FollowerManagerTabFragment.class.getName() + ".heroId";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected HeroTypeResourceDTOFactory heroTypeResourceDTOFactory;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.follower_list) ListView followerList;
    @InjectView(android.R.id.empty) View emptyView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    private FollowerListItemAdapter followerListAdapter;
    private UserBaseKey heroId;
    private FollowerSummaryDTO followerSummaryDTO;
    @Inject THRouter thRouter;
    private Subscription followerSubscription;

    public static void putHeroId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(HERO_ID_BUNDLE_KEY, followerId.getArgs());
    }

    @NonNull public static UserBaseKey getHeroId(@NonNull Bundle args)
    {
        return new UserBaseKey(args.getBundle(HERO_ID_BUNDLE_KEY));
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store_manage_followers, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
        if (followerListAdapter == null)
        {
            followerListAdapter = new FollowerListItemAdapter(getActivity(),
                    R.layout.follower_list_item
            );
        }
        swipeRefreshLayout.setOnRefreshListener(this);
        followerList.setAdapter(followerListAdapter);
        followerList.setOnScrollListener(dashboardBottomTabsListViewScrollListener.get());
        followerList.setOnItemClickListener((parent, view1, position, id) -> {
            ListView listView = (ListView) parent;
            handleFollowerItemClicked(view1, position - listView.getHeaderViewsCount(), id);
        });
        displayProgress(true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(getTitle());

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        Timber.d("onOptionsItemSelected");
        if (item.getItemId() == ITEM_ID_REFRESH_MENU)
        {
            refreshContent();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override public void onResume()
    {
        super.onResume();

        Timber.d("FollowerManagerTabFragment onResume");
        heroId = getHeroId(getArguments());
        fetchFollowers();
    }

    @Override public void onDestroyView()
    {
        this.followerListAdapter = null;
        unSubscribe();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    protected void fetchFollowers()
    {
        unSubscribe();
        followerSubscription = AndroidObservable.bindFragment(
                this,
                followerSummaryCache.get(heroId))
                .subscribe(createFollowerSummaryCacheObserver());
    }

    private void unSubscribe()
    {
        unsubscribe(followerSubscription);
    }

    private boolean isCurrentUser()
    {
        UserBaseKey heroId = getHeroId(getArguments());
        if (currentUserId != null)
        {
            return heroId.equals(currentUserId.toUserBaseKey());
        }
        return false;
    }

    private int getTitle()
    {
        if (isCurrentUser())
        {
            return R.string.manage_my_followers_title;
        }
        else
        {
            return R.string.manage_followers_title;
        }

    }

    protected HeroTypeResourceDTO getHeroTypeResource()
    {
        return heroTypeResourceDTOFactory.create(getFollowerType());
    }

    abstract protected HeroType getFollowerType();

    abstract protected void handleFollowerSummaryDTOReceived(FollowerSummaryDTO fromServer);

    public void display(FollowerSummaryDTO summaryDTO)
    {
        Timber.d("onDTOReceived display followerType:%s,%s", getFollowerType(), summaryDTO);
        linkWith(summaryDTO, true);
    }

    public void linkWith(FollowerSummaryDTO summaryDTO, boolean andDisplay)
    {
        this.followerSummaryDTO = summaryDTO;
        if (andDisplay)
        {
            displayFollowerList();
        }
    }

    public void display()
    {
        displayFollowerList();
    }

    public void displayFollowerList()
    {
        if (this.followerListAdapter != null)
        {
            if(this.followerSummaryDTO.userFollowers.isEmpty())
            {
                emptyView.setVisibility(View.VISIBLE);
            }
            else
            {
                emptyView.setVisibility(View.GONE);
                this.followerListAdapter.setFollowerSummaryDTO(this.followerSummaryDTO);
                this.followerListAdapter.notifyDataSetChanged();
            }
        }
    }

    private void redisplayProgress()
    {
        progressBar.setVisibility(View.VISIBLE);
    }

    public void displayProgress(boolean running)
    {
        progressBar.setVisibility(running ? View.VISIBLE : View.GONE);
        followerList.setVisibility(running ? View.GONE : View.VISIBLE);
        swipeRefreshLayout.setRefreshing(running);
    }

    @Override public void onRefresh()
    {
        if(followerSummaryDTO == null || followerSummaryDTO.userFollowers == null || followerSummaryDTO.userFollowers.size() == 0)
        {
            displayProgress(true);
        }

        doRefreshContent();
    }

    private void refreshContent()
    {
        Timber.d("refreshContent");
        redisplayProgress();
        doRefreshContent();
    }

    private void doRefreshContent()
    {
        Timber.d("refreshContent");

        if (heroId == null)
        {
            heroId = getHeroId(getArguments());
        }
        fetchFollowers();
    }

    private void pushTimelineFragment(int followerId)
    {
        Bundle bundle = new Bundle();
        thRouter.save(bundle, new UserBaseKey(followerId));
        navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
    }

    private void pushPayoutFragment(UserFollowerDTO followerDTO)
    {
        OwnedPortfolioId applicablePortfolioId = getApplicablePortfolioId();
        if (applicablePortfolioId != null)
        {
            FollowerHeroRelationId followerHeroRelationId =
                    new FollowerHeroRelationId(applicablePortfolioId.userId,
                            followerDTO.id, followerDTO.displayName);
            Bundle args = new Bundle();
            FollowerPayoutManagerFragment.put(args, followerHeroRelationId);
            navigator.get().pushFragment(FollowerPayoutManagerFragment.class, args);
        }
    }

    private void handleFollowerItemClicked(
            @SuppressWarnings("UnusedParameters") View view,
            int position,
            @SuppressWarnings("UnusedParameters") long id)
    {
        if (followerListAdapter != null)
        {
            UserFollowerDTO followerDTO =
                    (UserFollowerDTO) followerListAdapter.getItem(position);
            if (followerDTO != null)
            {
                if (isCurrentUser() && !followerDTO.isFreeFollow)
                {
                    pushPayoutFragment(followerDTO);
                }
                else
                {
                    pushTimelineFragment(followerDTO.id);
                }
            }
            else
            {
                Timber.d("handleFollowerItemClicked: FollowerDTO was null");
            }
        }
        else
        {
            Timber.d("Position clicked ", position);
        }
    }

    protected Observer<Pair<UserBaseKey, FollowerSummaryDTO>> createFollowerSummaryCacheObserver()
    {
        return new FollowerManagerFollowerSummaryObserver();
    }

    protected class FollowerManagerFollowerSummaryObserver
            implements Observer<Pair<UserBaseKey, FollowerSummaryDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, FollowerSummaryDTO> pair)
        {
            displayProgress(false);
            handleFollowerSummaryDTOReceived(pair.second);
            notifyFollowerLoaded(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            displayProgress(false);
            THToast.show(R.string.error_fetch_follower);
            Timber.e("Failed to fetch FollowerSummary", e);
        }
    }

    private void notifyFollowerLoaded(FollowerSummaryDTO value)
    {
        Timber.d("notifyFollowerLoaded for followerTabIndex:%d",
                getHeroTypeResource().followerTabIndex);
        OnFollowersLoadedListener loadedListener =
                FragmentUtils.getParent(this, OnFollowersLoadedListener.class);
        if (loadedListener != null && !isDetached())
        {
            loadedListener.onFollowerLoaded(getHeroTypeResource().followerTabIndex, value);
        }
    }
}
