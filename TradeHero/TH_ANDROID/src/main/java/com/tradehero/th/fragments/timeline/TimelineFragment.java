package com.tradehero.th.fragments.timeline;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.FlagNearEdgeScrollListener;
import com.tradehero.th.R;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.timeline.TimelineSection;
import com.tradehero.th.api.timeline.key.TimelineKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTOUtil;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.achievement.AchievementListFragment;
import com.tradehero.th.fragments.base.BaseLiveFragmentUtil;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinear;
import com.tradehero.th.fragments.discussion.AbstractDiscussionCompactItemViewLinearDTOFactory;
import com.tradehero.th.fragments.discussion.DiscussionFragmentUtil;
import com.tradehero.th.fragments.social.follower.FollowersFragment;
import com.tradehero.th.fragments.social.hero.HeroesFragment;
import com.tradehero.th.models.discussion.UserDiscussionAction;
import com.tradehero.th.persistence.portfolio.PortfolioCompactListCacheRx;
import com.tradehero.th.persistence.timeline.TimelineCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.utils.route.THRouter;
import com.tradehero.th.widget.LiveWidgetScrollListener;
import com.tradehero.th.widget.MultiScrollListener;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.Observer;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

abstract public class TimelineFragment extends DashboardFragment
{
    private static final String USER_BASE_KEY_BUNDLE_KEY = TimelineFragment.class.getName() + ".userBaseKey";

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

    @Inject Lazy<UserProfileCacheRx> userProfileCache;
    @Inject protected THRouter thRouter;
    @Inject protected TimelineCacheRx timelineCache;
    @Inject DiscussionFragmentUtil discussionFragmentUtil;
    @Inject AbstractDiscussionCompactItemViewLinearDTOFactory viewDTOFactory;
    @Inject CurrentUserId currentUserId;
    @Inject protected PortfolioCompactListCacheRx portfolioCompactListCache;

    @Bind(R.id.timeline_list_view) ListView timelineListView;
    @Bind(R.id.swipe_container) SwipeRefreshLayout swipeRefreshContainer;

    @Nullable protected UserProfileDTO shownProfile;

    protected UserBaseKey shownUserBaseKey;
    protected boolean mIsOtherProfile = false;

    private SubTimelineAdapterNew subTimelineAdapter;
    private UserProfileDetailView userProfileView;
    private boolean cancelRefreshingOnResume;
    private BaseLiveFragmentUtil liveFragmentUtil;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        thRouter.inject(this);
        shownUserBaseKey = getShownUserBaseKey();
        if (shownUserBaseKey == null)
        {
            throw new IllegalArgumentException("Should not end up with null shownUserBaseKey");
        }

        subTimelineAdapter = new SubTimelineAdapterNew(
                getActivity(),
                R.layout.timeline_item_view,
                R.layout.timeline_list_item_empty,
                R.layout.timeline_list_item_loading);
        subTimelineAdapter.appendHead(Collections.singletonList(SubTimelineAdapterNew.DTO_LOADING));
    }

    @Nullable protected UserBaseKey getShownUserBaseKey()
    {
        return getUserBaseKey(getArguments());
    }

    @SuppressLint("InflateParams")
    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        userProfileView = (UserProfileDetailView) inflater.inflate(R.layout.user_profile_detail_view, null);
        return inflater.inflate(R.layout.fragment_timeline, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        timelineListView.addHeaderView(userProfileView, null, false);
        timelineListView.setAdapter(subTimelineAdapter);
        timelineListView.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                onMainItemClick(parent, view, position, id);
            }
        });
        registerButtonClicks();
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        displayActionBarTitle();
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();

        fetchShownUserProfile();
        loadLatestTimeline();
        registerUserDiscussionActions();
    }

    @Override public void onResume()
    {
        super.onResume();
        loadLatestTimeline();

        FlagNearEdgeScrollListener nearEndScrollListener = createNearEndScrollListener();
        nearEndScrollListener.lowerEndFlag();
        nearEndScrollListener.activateEnd();
        LiveWidgetScrollListener liveWidgetScrollListener = new LiveWidgetScrollListener(fragmentElements.get(), liveFragmentUtil);
        timelineListView.setOnScrollListener(
                new MultiScrollListener(fragmentElements.get().getListViewScrollListener(), nearEndScrollListener, liveWidgetScrollListener));
        swipeRefreshContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener()
        {
            @Override public void onRefresh()
            {
                loadLatestTimeline();
            }
        });

        if (cancelRefreshingOnResume)
        {
            swipeRefreshContainer.setRefreshing(false);
            cancelRefreshingOnResume = false;
        }
        liveFragmentUtil.onResume();
    }

    protected void loadLatestTimeline()
    {
        onStopSubscriptions.add(getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                shownUserBaseKey,
                subTimelineAdapter.getLatestRange()))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                subTimelineAdapter.appendHead(subTimelineAdapter.reprocess(processed));
                                subTimelineAdapter.notifyDataSetChanged();
                                swipeRefreshContainer.setRefreshing(false);
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    protected void loadOlderTimeline()
    {
        subTimelineAdapter.appendTail(Collections.singletonList(SubTimelineAdapterNew.DTO_LOADING));
        subTimelineAdapter.notifyDataSetChanged();
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                getTimelineObservable(new TimelineKey(TimelineSection.Timeline,
                        shownUserBaseKey,
                        subTimelineAdapter.getOlderRange())))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<List<AbstractDiscussionCompactItemViewLinear.DTO>>()
                        {
                            @Override public void call(List<AbstractDiscussionCompactItemViewLinear.DTO> processed)
                            {
                                subTimelineAdapter.appendTail(subTimelineAdapter.reprocess(processed));
                                subTimelineAdapter.notifyDataSetChanged();
                            }
                        },
                        new ToastOnErrorAction1()));
    }

    @NonNull protected Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> getTimelineObservable(
            @NonNull TimelineKey key)
    {
        return timelineCache.get(key)
                .subscribeOn(Schedulers.computation())
                .take(1)
                .flatMap(new Func1<Pair<TimelineKey, TimelineDTO>, Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>>>()
                {
                    @Override public Observable<List<AbstractDiscussionCompactItemViewLinear.DTO>> call(
                            Pair<TimelineKey, TimelineDTO> pair)
                    {
                        List<TimelineItemDTO> enhancedItems = pair.second.getEnhancedItems();
                        if (enhancedItems != null)
                        {
                            return viewDTOFactory.createTimelineItemViewLinearDTOs(enhancedItems);
                        }
                        return Observable.<List<AbstractDiscussionCompactItemViewLinear.DTO>>just(
                                new ArrayList<AbstractDiscussionCompactItemViewLinear.DTO>());
                    }
                });
    }

    private FlagNearEdgeScrollListener createNearEndScrollListener()
    {
        return new FlagNearEdgeScrollListener()
        {
            @Override public void raiseEndFlag()
            {
                super.raiseEndFlag();
                loadOlderTimeline();
            }
        };
    }

    @Override public void onPause()
    {
        fragmentElements.get().getMovableBottom().setOnMovableBottomTranslateListener(null);
        timelineListView.setOnScrollListener(null);
        swipeRefreshContainer.setOnRefreshListener(null);
        super.onPause();
    }

    @Override public void onDestroyView()
    {
        liveFragmentUtil.onDestroyView();
        liveFragmentUtil = null;
        this.userProfileView = null;
        this.timelineListView.setOnItemClickListener(null);

        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        subTimelineAdapter = null;
        super.onDestroy();
    }

    protected void registerButtonClicks()
    {
        onDestroyViewSubscriptions.add(userProfileView.getButtonClickedObservable()
                .subscribe(
                        new Action1<UserProfileCompactViewHolder.ButtonType>()
                        {
                            @Override public void call(UserProfileCompactViewHolder.ButtonType buttonType)
                            {
                                handleButtonClicked(buttonType);
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to register to button clicks")));
    }

    protected void linkWith(@NonNull UserProfileDTO userProfileDTO)
    {
        this.shownProfile = userProfileDTO;
        userProfileView.display(shownProfile);
        displayActionBarTitle();
    }

    protected void displayActionBarTitle()
    {
        if (shownProfile != null)
        {
            if (shownProfile.id == currentUserId.get())
            {
                setActionBarTitle(getString(R.string.me));
            }
            else
            {
                setActionBarTitle(UserBaseDTOUtil.getLongDisplayName(getResources(), shownProfile));
            }
        }
        else
        {
            setActionBarTitle(R.string.loading_loading);
        }
    }

    protected void fetchShownUserProfile()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(this, userProfileCache.get().get(shownUserBaseKey))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new TimelineFragmentUserProfileCacheObserver()));
    }

    protected void onMainItemClick(AdapterView<?> adapterView, View view, int i, long l)
    {
        Object item = adapterView.getItemAtPosition(i);

        if (item.equals(SubTimelineAdapterNew.DTO_CALL_ACTION))
        {
            navigator.get().goToTab(RootFragmentType.TRENDING);
        }
        else
        {
            Timber.d("TimelineFragment, unhandled view %s", view);
        }
    }

    protected class TimelineFragmentUserProfileCacheObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            linkWith(pair.second);
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(getString(R.string.error_fetch_user_profile));
        }
    }

    protected boolean isFollowing()
    {
        UserBaseKey currentUser = currentUserId.toUserBaseKey();
        UserProfileDTO currentProfile = userProfileCache.get().getCachedValue(currentUser);
        return currentProfile != null && currentProfile.isFollowingUser(shownUserBaseKey);
    }

    protected void registerUserDiscussionActions()
    {
        onStopSubscriptions.add(AppObservable.bindSupportFragment(
                this,
                subTimelineAdapter.getUserActionObservable())
                .observeOn(AndroidSchedulers.mainThread())
                .flatMap(new Func1<UserDiscussionAction, Observable<UserDiscussionAction>>()
                {
                    @Override public Observable<UserDiscussionAction> call(UserDiscussionAction userDiscussionAction)
                    {
                        return discussionFragmentUtil.handleUserAction(getActivity(), userDiscussionAction);
                    }
                })
                .retry()
                .subscribe(
                        new Action1<UserDiscussionAction>()
                        {
                            @Override public void call(UserDiscussionAction userDiscussionAction)
                            {
                                Timber.e(new Exception("Not handled " + userDiscussionAction), "");
                            }
                        },
                        new TimberOnErrorAction1("When registering user actions")));
    }

    protected void handleButtonClicked(@NonNull UserProfileCompactViewHolder.ButtonType buttonType)
    {
        switch (buttonType)
        {
            case HEROES:
                pushHeroFragment();
                break;

            case FOLLOWERS:
                pushFollowerFragment();
                break;

            case ACHIEVEMENTS:
                pushAchievementFragment();
                break;

            default:
                throw new IllegalArgumentException("Unhandled ButtonType." + buttonType);
        }
    }

    protected void pushHeroFragment()
    {
        Bundle bundle = new Bundle();
        HeroesFragment.putFollowerId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(HeroesFragment.class, bundle);
    }

    protected void pushFollowerFragment()
    {
        Bundle bundle = new Bundle();
        FollowersFragment.putHeroId(
                bundle,
                mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(FollowersFragment.class, bundle);
    }

    protected void pushAchievementFragment()
    {
        Bundle bundle = new Bundle();
        AchievementListFragment.putUserId(bundle, mIsOtherProfile ? shownUserBaseKey : currentUserId.toUserBaseKey());
        navigator.get().pushFragment(AchievementListFragment.class, bundle);
    }
}
