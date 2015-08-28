package com.tradehero.th.fragments.social.follower;

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
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.social.FollowerSummaryDTO;
import com.tradehero.th.api.social.UserFollowerDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTO;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.social.FollowerSummaryCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWithFunc1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import com.tradehero.th.utils.route.THRouter;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable({
        "user/me/followers",
        "user/id/:heroId/followers",
})
public class FollowersFragment extends DashboardFragment implements SwipeRefreshLayout.OnRefreshListener
{
    private static final String HERO_ID_BUNDLE_KEY = FollowersFragment.class.getName() + ".heroId";

    @Inject protected CurrentUserId currentUserId;
    @Inject protected FollowerSummaryCacheRx followerSummaryCache;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject THRouter router;

    @Bind(R.id.swipe_to_refresh_layout) SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.follower_list) RecyclerView followerList;
    @Bind(android.R.id.progress) ProgressBar progressBar;

    @RouteProperty("heroId") Integer routedHeroId;

    private FollowerRecyclerItemAdapter followerRecyclerAdapter;
    private UserBaseKey heroId;
    private FollowerSummaryDTO followerSummaryDTO;

    //<editor-fold desc="Argument passing">
    public static void putHeroId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(HERO_ID_BUNDLE_KEY, followerId.getArgs());
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        //noinspection ConstantConditions
        this.heroId = new UserBaseKey(getArguments().getBundle(HERO_ID_BUNDLE_KEY));
        router.inject(this);
        if (routedHeroId != null)
        {
            this.heroId = new UserBaseKey(routedHeroId);
        }
        followerRecyclerAdapter = new FollowerRecyclerItemAdapter(getActivity());
        followerRecyclerAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<FollowerListItemView.DTO>()
        {
            @Override public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<FollowerListItemView.DTO> viewHolder,
                    FollowerListItemView.DTO object)
            {
                Bundle bundle = new Bundle();
                PushableTimelineFragment.putUserBaseKey(bundle, new UserBaseKey(object.userFollowerDTO.id));
                navigator.get().pushFragment(PushableTimelineFragment.class, bundle);
            }
        });
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_followers, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        swipeRefreshLayout.setOnRefreshListener(this);
        followerList.setLayoutManager(new LinearLayoutManager(getActivity()));
        followerList.setAdapter(followerRecyclerAdapter);
        followerList.setHasFixedSize(true);
        followerList.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity()));
        //followerList.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
        displayProgress(true);

        onDestroyViewSubscriptions.add(
                Observable.combineLatest(
                        userProfileCache.getOne(currentUserId.toUserBaseKey()),
                        followerSummaryCache.get(heroId)
                                .subscribeOn(Schedulers.computation()),
                        new Func2<Pair<UserBaseKey, UserProfileDTO>, Pair<UserBaseKey, FollowerSummaryDTO>, Pair<FollowerSummaryDTO, List<FollowerListItemView.DTO>>>()
                        {
                            @Override public Pair<FollowerSummaryDTO, List<FollowerListItemView.DTO>> call(
                                    Pair<UserBaseKey, UserProfileDTO> userProfilePair,
                                    Pair<UserBaseKey, FollowerSummaryDTO> userFollowerPair)
                            {
                                followerSummaryDTO = userFollowerPair.second;
                                List<UserFollowerDTO> followerDTOs = followerSummaryDTO.userFollowers;
                                return Pair.create(
                                        userFollowerPair.second,
                                        followerDTOs == null
                                                ? null
                                                : FollowerRecyclerItemAdapter.createItems(getResources(), followerDTOs,
                                                        userProfilePair.second));
                            }
                        }
                )
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                new Action1<Pair<FollowerSummaryDTO, List<FollowerListItemView.DTO>>>()
                                {
                                    @Override
                                    public void call(Pair<FollowerSummaryDTO, List<FollowerListItemView.DTO>> followerSummaryDTOListPair)
                                    {
                                        displayProgress(false);
                                        if (followerSummaryDTOListPair.second != null)
                                        {
                                            followerRecyclerAdapter.addAll(followerSummaryDTOListPair.second);
                                        }
                                        notifyFollowerLoaded(followerSummaryDTOListPair.first);
                                    }
                                },
                                new TimberAndToastOnErrorAction1(
                                        getString(R.string.error_fetch_follower),
                                        "Failed to fetch FollowerSummary")
                                {
                                    @Override public void call(Throwable throwable)
                                    {
                                        super.call(throwable);
                                        displayProgress(false);
                                    }
                                }));

        onDestroyViewSubscriptions.add(followerRecyclerAdapter.getFollowerDTOObservable()
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<FollowerListItemView.DTO>()
                        {
                            @Override public void call(FollowerListItemView.DTO dto)
                            {
                                onUserAction(dto);
                            }
                        },
                        new TimberAndToastOnErrorAction1("Failed to listen to user actions")));
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(isCurrentUser()
                ? R.string.manage_my_followers_title
                : R.string.manage_followers_title);

        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyView()
    {
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        followerRecyclerAdapter = null;
        super.onDestroy();
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

    private void doRefreshContent()
    {
        followerSummaryCache.get(heroId);
    }

    protected void onUserAction(@NonNull FollowerListItemView.DTO dto)
    {
        SimpleFollowUserAssistant assistant = new SimpleFollowUserAssistant(getActivity(), dto.userFollowerDTO.getBaseKey());
        if (dto.isFollowing)
        {
            //Unfollow
            onDestroyViewSubscriptions.add(assistant.showUnFollowConfirmation(dto.titleText)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, SimpleFollowUserAssistant>(assistant))
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<SimpleFollowUserAssistant>>()
                    {
                        @Override public Observable<SimpleFollowUserAssistant> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.ensureCacheValue().doOnNext(new Action1<SimpleFollowUserAssistant>()
                            {
                                @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                                {
                                    simpleFollowUserAssistant.unFollowFromCache();
                                }
                            });
                        }
                    })
                    .map(new ReplaceWithFunc1<SimpleFollowUserAssistant, FollowerListItemView.DTO>(dto))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowerListItemView.DTO>()
                    {
                        @Override public void call(FollowerListItemView.DTO dto)
                        {
                            dto.isFollowing = false;
                            updateSingleRow(dto);
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new ReplaceWithFunc1<FollowerListItemView.DTO, SimpleFollowUserAssistant>(assistant))
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.unFollowFromServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to unfollow user from followers fragment")));
        }
        else
        {
            onDestroyViewSubscriptions.add(assistant.ensureCacheValue()
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(Schedulers.io())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            simpleFollowUserAssistant.followingInCache();
                        }
                    })
                    .map(new ReplaceWithFunc1<SimpleFollowUserAssistant, FollowerListItemView.DTO>(dto))
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext(new Action1<FollowerListItemView.DTO>()
                    {
                        @Override public void call(FollowerListItemView.DTO dto)
                        {
                            dto.isFollowing = true;
                            updateSingleRow(dto);
                        }
                    })
                    .observeOn(Schedulers.io())
                    .map(new ReplaceWithFunc1<FollowerListItemView.DTO, SimpleFollowUserAssistant>(assistant))
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.followingInServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to follow user from followers fragment")));
        }
    }

    private void updateSingleRow(FollowerListItemView.DTO dto)
    {
        if (followerRecyclerAdapter != null)
        {
            int index = followerRecyclerAdapter.indexOf(dto);
            if (index >= 0)
            {
                followerRecyclerAdapter.notifyItemChanged(index);
            }
        }
    }

    @NonNull protected HeroType getFollowerType()
    {
        return HeroType.ALL;
    }

    private void broadcast(DiscussionType discussionType)
    {
        //int page = mTabHost.getCurrentTab();
        //HeroType followerType = HeroType.fromId(page);
        //
        //Bundle args = new Bundle();
        //MessageType messageType;
        //switch (followerType)
        //{
        //    case ALL:
        //        messageType = MessageType.BROADCAST_ALL_FOLLOWERS;
        //        break;
        //    case PREMIUM:
        //        messageType = MessageType.BROADCAST_PAID_FOLLOWERS;
        //        break;
        //    case FREE:
        //        messageType = MessageType.BROADCAST_FREE_FOLLOWERS;
        //        break;
        //    default:
        //        throw new IllegalStateException("unknown followerType!");
        //}
        //
        //SendMessageFragment.putMessageType(args, messageType);
        //Timber.d("goToMessagePage index:%d, tabIndex:%d, followerType:%s, discussionType:%s", page,
        //        page, followerType, discussionType);
        //navigator.get().pushFragment(SendMessageFragment.class, args);
    }
}
