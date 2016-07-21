package com.androidth.general.fragments.social.hero;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewStub;
import android.widget.Button;
import android.widget.ProgressBar;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.androidth.general.common.rx.PairGetSecond;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.androidth.general.R;
import com.androidth.general.adapters.TypedRecyclerAdapter;
import com.androidth.general.api.social.HeroDTO;
import com.androidth.general.api.social.HeroDTOExtWrapper;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.fragments.base.DashboardFragment;
import com.androidth.general.fragments.dashboard.RootFragmentType;
import com.androidth.general.fragments.timeline.PushableTimelineFragment;
import com.androidth.general.models.user.follow.FollowUserAssistant;
import com.androidth.general.persistence.social.HeroListCacheRx;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.rx.EmptyAction1;
import com.androidth.general.rx.ReplaceWithFunc1;
import com.androidth.general.rx.TimberAndToastOnErrorAction1;
import com.androidth.general.rx.TimberOnErrorAction1;
import com.androidth.general.rx.dialog.OnDialogClickEvent;
import com.androidth.general.utils.route.THRouter;
import java.util.List;
import javax.inject.Inject;

import butterknife.Unbinder;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;

@Routable({
        "user/me/heroes",
        "user/id/:followerId/heroes",
})
public class HeroesFragment extends DashboardFragment implements OnRefreshListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID = HeroesFragment.class.getName() + ".followerId";

    @Inject protected HeroListCacheRx heroListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject THRouter router;

    @BindView(R.id.swipe_to_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.heros_list) protected RecyclerView heroListView;
    @BindView(android.R.id.progress) protected ProgressBar progressBar;
    @BindView(R.id.empty_view_stub) ViewStub emptyStub;
    @Nullable View emptyView;

    @RouteProperty("followerId") Integer routedFollowerId;

    private UserBaseKey followerId;
    private HeroRecyclerItemAdapter heroRecyclerItemAdapter;

    private Unbinder unbinder;

    //<editor-fold desc="Argument Passing">
    public static void putFollowerId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }
    //</editor-fold>

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        router.inject(this);
        this.followerId = new UserBaseKey(getArguments().getBundle(BUNDLE_KEY_FOLLOWER_ID));
        if (routedFollowerId != null)
        {
            this.followerId = new UserBaseKey(routedFollowerId);
        }
        this.heroRecyclerItemAdapter = new HeroRecyclerItemAdapter(getActivity());
        this.heroRecyclerItemAdapter.setOnItemClickedListener(new TypedRecyclerAdapter.OnItemClickedListener<HeroDisplayDTO>()
        {
            @Override
            public void onItemClicked(int position, TypedRecyclerAdapter.TypedViewHolder<HeroDisplayDTO> viewHolder,
                    HeroDisplayDTO object)
            {
                Bundle args = new Bundle();
                PushableTimelineFragment.putUserBaseKey(args, object.heroDTO.getBaseKey());
                navigator.get().pushFragment(PushableTimelineFragment.class, args);
            }
        });
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_heroes, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        unbinder = ButterKnife.bind(this, view);

        this.swipeRefreshLayout.setOnRefreshListener(this);
        this.heroListView.setLayoutManager(new LinearLayoutManager(getActivity()));
        this.heroListView.setAdapter(this.heroRecyclerItemAdapter);
        this.heroListView.setHasFixedSize(true);
        this.heroListView.addItemDecoration(new TypedRecyclerAdapter.DividerItemDecoration(getActivity()));

        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);

        onDestroyViewSubscriptions.add(Observable.combineLatest(
                heroListCache.get(followerId),
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                new Func2<Pair<UserBaseKey, HeroDTOExtWrapper>, UserProfileDTO, Pair<HeroDTOExtWrapper, List<HeroDisplayDTO>>>()
                {
                    @Override
                    public Pair<HeroDTOExtWrapper, List<HeroDisplayDTO>> call(Pair<UserBaseKey, HeroDTOExtWrapper> pair,
                            UserProfileDTO userProfileDTO)
                    {
                        return Pair.create(
                                pair.second,
                                HeroRecyclerItemAdapter.createObjects(
                                        getResources(),
                                        currentUserId,
                                        followerId,
                                        getHeroes(pair.second), userProfileDTO));
                    }
                })
                .subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<HeroDTOExtWrapper, List<HeroDisplayDTO>>>()
                        {
                            @Override public void call(Pair<HeroDTOExtWrapper, List<HeroDisplayDTO>> pair)
                            {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                heroRecyclerItemAdapter.addAll(pair.second);
                                swipeRefreshLayout.setEnabled(true);
                                updateEmptyView();
                            }
                        },
                        new TimberAndToastOnErrorAction1(
                                getString(R.string.error_fetch_hero),
                                "Could not fetch heroes")
                        {
                            @Override public void call(Throwable throwable)
                            {
                                super.call(throwable);
                                progressBar.setVisibility(View.GONE);
                                swipeRefreshLayout.setEnabled(true);
                            }
                        }));

        onDestroyViewSubscriptions.add(heroRecyclerItemAdapter.getHeroDTOObservable()
                .subscribe(
                        new Action1<HeroDisplayDTO>()
                        {
                            @Override public void call(HeroDisplayDTO dto)
                            {
                                handleFollowClicked(dto);
                            }
                        },
                        new TimberOnErrorAction1("Failed to handle UserAction")));
    }

    private void updateEmptyView()
    {
        if (heroRecyclerItemAdapter.getItemCount() > 0)
        {
            if (emptyView != null)
            {
                emptyView.setVisibility(View.GONE);
            }
        }
        else
        {
            if (emptyView == null)
            {
                emptyStub.setLayoutResource(isCurrentUser() ? R.layout.hero_list_item_empty_placeholder :
                        R.layout.hero_list_item_empty_placeholder_for_other);
                emptyView = emptyStub.inflate();

                if (isCurrentUser())
                {
                    Button callToAction = (Button) emptyView.findViewById(R.id.empty_call_to_action);
                    if (callToAction != null)
                    {
                        callToAction.setOnClickListener(new View.OnClickListener()
                        {
                            @Override public void onClick(View v)
                            {
                                navigator.get().goToTab(RootFragmentType.COMMUNITY);
                            }
                        });
                    }
                }
            }
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    private void handleFollowClicked(HeroDisplayDTO dto)
    {
        FollowUserAssistant assistant = new FollowUserAssistant(getActivity(), dto.heroDTO.getBaseKey());
        if (dto.isCurrentUserFollowing)
        {
            //Unfollow
            onDestroyViewSubscriptions.add(assistant.showUnFollowConfirmation(dto.titleText)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, FollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<FollowUserAssistant, Observable<FollowUserAssistant>>()
                    {
                        @Override public Observable<FollowUserAssistant> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.ensureCacheValue().doOnNext(new Action1<FollowUserAssistant>()
                            {
                                @Override public void call(FollowUserAssistant followUserAssistant)
                                {
                                    followUserAssistant.unFollowFromCache();
                                }
                            });
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<FollowUserAssistant, HeroDisplayDTO>(dto))
                    .doOnNext(new Action1<HeroDisplayDTO>()
                    {
                        @Override public void call(HeroDisplayDTO dto)
                        {
                            dto.isCurrentUserFollowing = false;
                            updateRow(dto);
                        }
                    })
                    .map(new ReplaceWithFunc1<HeroDisplayDTO, FollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.unFollowFromServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to unfollow from heroes fragment")));
        }
        else
        {
            onDestroyViewSubscriptions.add(assistant.ensureCacheValue()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(new Action1<FollowUserAssistant>()
                    {
                        @Override public void call(FollowUserAssistant followUserAssistant)
                        {
                            followUserAssistant.followingInCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<FollowUserAssistant, HeroDisplayDTO>(dto))
                    .doOnNext(new Action1<HeroDisplayDTO>()
                    {
                        @Override public void call(HeroDisplayDTO dto)
                        {
                            dto.isCurrentUserFollowing = true;
                            updateRow(dto);
                        }
                    })
                    .map(new ReplaceWithFunc1<HeroDisplayDTO, FollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<FollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(FollowUserAssistant followUserAssistant)
                        {
                            return followUserAssistant.followingInServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to follow from heroes fragment")));
        }
    }

    private void updateRow(HeroDisplayDTO dto)
    {
        int index = heroRecyclerItemAdapter.indexOf(dto);
        if (index >= 0)
        {
            if (isCurrentUser())
            {
                //Is current user, if unfollow remove from list
                heroRecyclerItemAdapter.removeItemAt(index);
            }
            else
            {
                heroRecyclerItemAdapter.notifyItemChanged(index);
            }
        }
    }

    private boolean isCurrentUser()
    {
        return currentUserId.toUserBaseKey().equals(followerId);
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(followerId.equals(currentUserId.toUserBaseKey())
                ? R.string.manage_my_heroes_title
                : R.string.manage_heroes_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onDestroyView()
    {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override public void onDestroy()
    {
        heroRecyclerItemAdapter = null;
        super.onDestroy();
    }

    @Override public void onRefresh()
    {
        swipeRefreshLayout.setEnabled(false);
        heroListCache.get(followerId);
    }

    protected List<HeroDTO> getHeroes(@NonNull HeroDTOExtWrapper heroDTOExtWrapper)
    {
        return heroDTOExtWrapper.allActiveHeroes;
    }
}