package com.tradehero.th.fragments.social.hero;

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
import android.widget.AdapterView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.route.Routable;
import com.tradehero.route.RouteProperty;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TypedRecyclerAdapter;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.EmptyAction1;
import com.tradehero.th.rx.ReplaceWithFunc1;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
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

@Routable({
        "user/me/heroes",
        "user/id/:followerId/heroes",
})
public class AllHeroFragment extends DashboardFragment implements OnRefreshListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID = AllHeroFragment.class.getName() + ".followerId";

    @Inject protected HeroListCacheRx heroListCache;
    @Inject protected CurrentUserId currentUserId;
    @Inject protected UserProfileCacheRx userProfileCache;
    @Inject THRouter router;

    @Bind(R.id.swipe_to_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.heros_list) protected RecyclerView heroListView;
    @Bind(android.R.id.progress) protected ProgressBar progressBar;

    @RouteProperty("followerId") Integer routedFollowerId;

    private UserBaseKey followerId;
    private HeroRecyclerItemAdapter heroRecyclerItemAdapter;

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
        this.heroRecyclerItemAdapter = new HeroRecyclerItemAdapter(getActivity());
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_store_manage_heroes, container, false);
    }

    @Override public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);

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
                new Func2<Pair<UserBaseKey, HeroDTOExtWrapper>, UserProfileDTO, Pair<HeroDTOExtWrapper, List<HeroListItemView.DTO>>>()
                {
                    @Override
                    public Pair<HeroDTOExtWrapper, List<HeroListItemView.DTO>> call(Pair<UserBaseKey, HeroDTOExtWrapper> pair,
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
                        new Action1<Pair<HeroDTOExtWrapper, List<HeroListItemView.DTO>>>()
                        {
                            @Override public void call(Pair<HeroDTOExtWrapper, List<HeroListItemView.DTO>> pair)
                            {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                heroRecyclerItemAdapter.addAll(pair.second);
                                swipeRefreshLayout.setEnabled(true);
                                notifyHeroesLoaded(pair.first);
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
                        new Action1<HeroListItemView.DTO>()
                        {
                            @Override public void call(HeroListItemView.DTO dto)
                            {
                                handleFollowClicked(dto);
                            }
                        },
                        new TimberOnErrorAction1("Failed to handle UserAction")));
    }

    private void handleFollowClicked(HeroListItemView.DTO dto)
    {
        SimpleFollowUserAssistant assistant = new SimpleFollowUserAssistant(getActivity(), dto.heroDTO.getBaseKey());
        if (dto.isCurrentUserFollowing)
        {
            //Unfollow
            onDestroyViewSubscriptions.add(assistant.showUnFollowConfirmation(dto.titleText)
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<OnDialogClickEvent, SimpleFollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
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
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<SimpleFollowUserAssistant, HeroListItemView.DTO>(dto))
                    .doOnNext(new Action1<HeroListItemView.DTO>()
                    {
                        @Override public void call(HeroListItemView.DTO dto)
                        {
                            dto.isCurrentUserFollowing = false;
                            updateRow(dto);
                        }
                    })
                    .map(new ReplaceWithFunc1<HeroListItemView.DTO, SimpleFollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.unFollowFromServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to unfollow from heroes fragment")));
        }
        else
        {
            onDestroyViewSubscriptions.add(assistant.ensureCacheValue()
                    .subscribeOn(Schedulers.io())
                    .doOnNext(new Action1<SimpleFollowUserAssistant>()
                    {
                        @Override public void call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            simpleFollowUserAssistant.followingInCache();
                        }
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .map(new ReplaceWithFunc1<SimpleFollowUserAssistant, HeroListItemView.DTO>(dto))
                    .doOnNext(new Action1<HeroListItemView.DTO>()
                    {
                        @Override public void call(HeroListItemView.DTO dto)
                        {
                            dto.isCurrentUserFollowing = true;
                            updateRow(dto);
                        }
                    })
                    .map(new ReplaceWithFunc1<HeroListItemView.DTO, SimpleFollowUserAssistant>(assistant))
                    .observeOn(Schedulers.io())
                    .flatMap(new Func1<SimpleFollowUserAssistant, Observable<UserProfileDTO>>()
                    {
                        @Override public Observable<UserProfileDTO> call(SimpleFollowUserAssistant simpleFollowUserAssistant)
                        {
                            return simpleFollowUserAssistant.followingInServer();
                        }
                    })
                    .subscribe(new EmptyAction1<UserProfileDTO>(), new TimberOnErrorAction1("Failed to follow from heroes fragment")));
        }
    }

    private void updateRow(HeroListItemView.DTO dto)
    {
        int index = heroRecyclerItemAdapter.indexOf(dto);
        if (index >= 0)
        {
            if (currentUserId.toUserBaseKey().equals(followerId))
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
        ButterKnife.unbind(this);
    }

    @Override public void onDestroy()
    {
        heroRecyclerItemAdapter = null;
        super.onDestroy();
    }

    @SuppressWarnings("unused")
    protected void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof HeroListItemView.DTO)
        {
            Bundle args = new Bundle();
            PushableTimelineFragment.putUserBaseKey(args, ((HeroListItemView.DTO) parent.getItemAtPosition(position)).heroDTO.getBaseKey());
            navigator.get().pushFragment(PushableTimelineFragment.class, args);
        }
        //else if (item.equals(HeroListItemAdapter.DTO_CALL_ACTION))
        //{
        //    navigator.get().goToTab(RootFragmentType.COMMUNITY);
        //}
    }

    private void unfollow(@NonNull final HeroDTO clickedHeroDTO)
    {
        onStopSubscriptions.add(HeroAlertDialogRxUtil.popAlertUnFollowHero(getActivity())
                .flatMap(new Func1<OnDialogClickEvent, Observable<UserProfileDTO>>()
                {
                    @Override public Observable<UserProfileDTO> call(OnDialogClickEvent onDialogClickEvent)
                    {
                        if (onDialogClickEvent.isPositive())
                        {
                            return new SimpleFollowUserAssistant(getActivity(), clickedHeroDTO.getBaseKey())
                                    .launchUnFollowRx();
                        }
                        return Observable.empty();
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<UserProfileDTO>()
                        {
                            @Override public void call(UserProfileDTO profile)
                            {
                                heroListCache.get(followerId);
                            }
                        },
                        new ToastOnErrorAction1()
                ));
    }

    @Override public void onRefresh()
    {
        swipeRefreshLayout.setEnabled(false);
        heroListCache.get(followerId);
    }

    private void notifyHeroesLoaded(HeroDTOExtWrapper value)
    {
        OnHeroesLoadedListener listener =
                FragmentUtils.getParent(this, OnHeroesLoadedListener.class);
        if (listener != null && !isDetached())
        {
            listener.onHeroesLoaded(HeroTypeResourceDTOFactory.create(getHeroType()), value);
        }
    }

    @NonNull protected HeroType getHeroType()
    {
        return HeroType.ALL;
    }

    protected List<HeroDTO> getHeroes(@NonNull HeroDTOExtWrapper heroDTOExtWrapper)
    {
        return heroDTOExtWrapper.allActiveHeroes;
    }
}