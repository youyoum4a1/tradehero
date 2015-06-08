package com.tradehero.th.fragments.social.follower;

import android.app.Activity;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnItemClick;
import com.tradehero.th.R;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.social.key.FollowerHeroRelationId;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.rx.ToastAndLogOnErrorAction;
import java.util.List;
import javax.inject.Inject;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class FollowerManagerTabFragment extends DashboardFragment
        implements SwipeRefreshLayout.OnRefreshListener
{
    public static final int ITEM_ID_REFRESH_MENU = 0;
    private static final String HERO_ID_BUNDLE_KEY = FollowerManagerTabFragment.class.getName() + ".heroId";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;

    @InjectView(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(R.id.follower_list) ListView followerList;
    @InjectView(android.R.id.progress) ProgressBar progressBar;

    private FollowerListItemAdapter followerListAdapter;
    private UserBaseKey heroId;
    private FollowerSummaryDTO followerSummaryDTO;

    //<editor-fold desc="Argument passing">
    public static void putHeroId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(HERO_ID_BUNDLE_KEY, followerId.getArgs());
    }
    //</editor-fold>

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        followerListAdapter = new FollowerListItemAdapter(
                activity,
                R.layout.follower_list_item,
                R.layout.follower_list_item_empty);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        this.heroId = new UserBaseKey(getArguments().getBundle(HERO_ID_BUNDLE_KEY));
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
        swipeRefreshLayout.setOnRefreshListener(this);
        followerList.setAdapter(followerListAdapter);
        followerList.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
        displayProgress(true);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isCurrentUser()
                ? R.string.manage_my_followers_title
                : R.string.manage_followers_title);

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

    @Override public void onStart()
    {
        super.onStart();
        onStopSubscriptions.add(followerSummaryCache.get(heroId)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<Pair<UserBaseKey, FollowerSummaryDTO>, Pair<FollowerSummaryDTO, List<Object>>>()
                {
                    @Override public Pair<FollowerSummaryDTO, List<Object>> call(Pair<UserBaseKey, FollowerSummaryDTO> pair)
                    {
                        followerSummaryDTO = pair.second;
                        List<UserFollowerDTO> followerDTOs = getFollowers(pair.second);
                        return Pair.create(
                                pair.second,
                                followerDTOs == null
                                        ? null
                                        : FollowerListItemAdapter.createObjects(getResources(), followerDTOs));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<FollowerSummaryDTO, List<Object>>>()
                        {
                            @Override public void call(Pair<FollowerSummaryDTO, List<Object>> followerSummaryDTOListPair)
                            {
                                displayProgress(false);
                                followerListAdapter.setNotifyOnChange(false);
                                followerListAdapter.clear();
                                if (followerSummaryDTOListPair.second != null)
                                {
                                    followerListAdapter.addAll(followerSummaryDTOListPair.second);
                                }
                                followerListAdapter.setNotifyOnChange(true);
                                followerListAdapter.notifyDataSetChanged();
                                notifyFollowerLoaded(followerSummaryDTOListPair.first);
                            }
                        },
                        new ToastAndLogOnErrorAction(
                                getString(R.string.error_fetch_follower),
                                "Failed to fetch FollowerSummary")
                        {
                            @Override public void call(Throwable throwable)
                            {
                                super.call(throwable);
                                displayProgress(false);
                            }
                        }));

        onStopSubscriptions.add(followerListAdapter.getUserActionObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<FollowerListItemAdapter.UserAction>()
                        {
                            @Override public void call(FollowerListItemAdapter.UserAction userAction)
                            {
                                onUserAction(userAction);
                            }
                        },
                        new ToastAndLogOnErrorAction("Failed to listen to user actions")));
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        followerListAdapter = null;
        super.onDetach();
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

    private boolean isCurrentUser()
    {
        return heroId.equals(currentUserId.toUserBaseKey());
    }

    @NonNull protected HeroTypeResourceDTO getHeroTypeResource()
    {
        return HeroTypeResourceDTOFactory.create(getFollowerType());
    }

    @NonNull abstract protected HeroType getFollowerType();

    @Nullable abstract protected List<UserFollowerDTO> getFollowers(@NonNull FollowerSummaryDTO fromServer);

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
        if (followerSummaryDTO == null || followerSummaryDTO.userFollowers == null || followerSummaryDTO.userFollowers.size() == 0)
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
        followerSummaryCache.get(heroId);
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.follower_list)
    protected void onFollowerItemClick(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof FollowerListItemView.DTO)
        {
            UserFollowerDTO userFollowerDTO = ((FollowerListItemView.DTO) item).userFollowerDTO;
            if (isCurrentUser() && !userFollowerDTO.isFreeFollow)
            {
                FollowerHeroRelationId followerHeroRelationId =
                        new FollowerHeroRelationId(currentUserId.get(),
                                userFollowerDTO.id, userFollowerDTO.displayName);
                Bundle args = new Bundle();
                FollowerPayoutManagerFragment.put(args, followerHeroRelationId);
                navigator.get().pushFragment(FollowerPayoutManagerFragment.class, args);
            }
            else
            {
                Bundle bundle = new Bundle();
                PushableTimelineFragment.putUserBaseKey(bundle, new UserBaseKey(userFollowerDTO.id));
                navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
            }
        }
        else
        {
            Timber.e(new IllegalArgumentException(), "Unhandled item " + item);
        }
    }

    protected void onUserAction(@NonNull FollowerListItemAdapter.UserAction userAction)
    {
        if (userAction instanceof FollowerListItemView.ProfileUserAction)
        {
            Bundle bundle = new Bundle();
            PushableTimelineFragment.putUserBaseKey(bundle, ((FollowerListItemView.ProfileUserAction) userAction).dto.getBaseKey());
            navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
        }
        else if (userAction instanceof FollowerListCallToActionItemView.TradeNowUserAction)
        {
            navigator.get().goToTab(RootFragmentType.TRENDING);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled UserAction: " + userAction);
        }
    }
}
