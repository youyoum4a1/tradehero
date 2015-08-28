package com.tradehero.th.fragments.social.hero;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnItemClick;
import com.tradehero.th.R;
import com.tradehero.th.api.social.HeroDTO;
import com.tradehero.th.api.social.HeroDTOExtWrapper;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.dashboard.RootFragmentType;
import com.tradehero.th.fragments.social.FragmentUtils;
import com.tradehero.th.fragments.timeline.PushableTimelineFragment;
import com.tradehero.th.models.social.follower.HeroTypeResourceDTOFactory;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.persistence.social.HeroListCacheRx;
import com.tradehero.th.persistence.social.HeroType;
import com.tradehero.th.rx.TimberAndToastOnErrorAction1;
import com.tradehero.th.rx.TimberOnErrorAction1;
import com.tradehero.th.rx.ToastOnErrorAction1;
import com.tradehero.th.rx.dialog.OnDialogClickEvent;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

public class AllHeroFragment extends DashboardFragment implements OnRefreshListener
{
    private static final String BUNDLE_KEY_FOLLOWER_ID = AllHeroFragment.class.getName() + ".followerId";

    @Inject protected HeroListCacheRx heroListCache;
    @Inject protected CurrentUserId currentUserId;

    @Bind(R.id.swipe_to_refresh_layout) protected SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.heros_list) protected ListView heroListView;
    @Bind(android.R.id.progress) protected ProgressBar progressBar;

    private UserBaseKey followerId;
    private HeroListItemAdapter heroListAdapter;

    //<editor-fold desc="Argument Passing">
    public static void putFollowerId(@NonNull Bundle args, @NonNull UserBaseKey followerId)
    {
        args.putBundle(BUNDLE_KEY_FOLLOWER_ID, followerId.getArgs());
    }
    //</editor-fold>

    @Override public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        this.heroListAdapter = new HeroListItemAdapter(
                activity,
                R.layout.hero_list_item,
                R.layout.hero_list_item_empty_placeholder,
                R.layout.hero_list_item_empty_placeholder_for_other);
    }

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.followerId = new UserBaseKey(getArguments().getBundle(BUNDLE_KEY_FOLLOWER_ID));
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
        this.heroListView.setAdapter(this.heroListAdapter);
        this.heroListView.setOnScrollListener(fragmentElements.get().getListViewScrollListener());
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        setActionBarTitle(followerId.equals(currentUserId.toUserBaseKey())
                ? R.string.manage_my_heroes_title
                : R.string.manage_heroes_title);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override public void onStart()
    {
        super.onStart();
        progressBar.setVisibility(View.VISIBLE);
        swipeRefreshLayout.setEnabled(false);
        onStopSubscriptions.add(heroListCache.get(followerId)
                .subscribeOn(Schedulers.computation())
                .map(new Func1<Pair<UserBaseKey, HeroDTOExtWrapper>, Pair<HeroDTOExtWrapper, List<Object>>>()
                {
                    @Override public Pair<HeroDTOExtWrapper, List<Object>> call(Pair<UserBaseKey, HeroDTOExtWrapper> pair)
                    {
                        return Pair.create(
                                pair.second,
                                HeroListItemAdapter.createObjects(
                                        getResources(),
                                        currentUserId,
                                        followerId,
                                        getHeroes(pair.second)));
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<Pair<HeroDTOExtWrapper, List<Object>>>()
                        {
                            @Override public void call(Pair<HeroDTOExtWrapper, List<Object>> pair)
                            {
                                swipeRefreshLayout.setRefreshing(false);
                                progressBar.setVisibility(View.GONE);
                                heroListAdapter.setNotifyOnChange(false);
                                heroListAdapter.clear();
                                heroListAdapter.addAll(pair.second);
                                heroListAdapter.setNotifyOnChange(true);
                                heroListAdapter.notifyDataSetChanged();
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

        onStopSubscriptions.add(heroListAdapter.getUserActionObservable()
                .subscribe(
                        new Action1<HeroListItemView.UserAction>()
                        {
                            @Override public void call(HeroListItemView.UserAction userAction)
                            {
                                if (userAction instanceof HeroListItemView.UserActionDelete)
                                {
                                    unfollow(((HeroListItemView.UserActionDelete) userAction).heroDTO);
                                }
                                else
                                {
                                    throw new IllegalArgumentException("Unhandled userAction " + userAction);
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to handle UserAction")));
    }

    @Override public void onDestroyView()
    {
        if (this.heroListView != null)
        {
            this.heroListView.setOnScrollListener(null);
        }
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDetach()
    {
        heroListAdapter = null;
        super.onDetach();
    }

    @SuppressWarnings("unused")
    @OnItemClick(R.id.heros_list)
    protected void handleHeroClicked(AdapterView<?> parent, View view, int position, long id)
    {
        Object item = parent.getItemAtPosition(position);
        if (item instanceof HeroListItemView.DTO)
        {
            Bundle args = new Bundle();
            PushableTimelineFragment.putUserBaseKey(args, ((HeroListItemView.DTO) parent.getItemAtPosition(position)).heroDTO.getBaseKey());
            navigator.get().pushFragment(PushableTimelineFragment.class, args);
        }
        else if (item.equals(HeroListItemAdapter.DTO_CALL_ACTION))
        {
            navigator.get().goToTab(RootFragmentType.COMMUNITY);
        }
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