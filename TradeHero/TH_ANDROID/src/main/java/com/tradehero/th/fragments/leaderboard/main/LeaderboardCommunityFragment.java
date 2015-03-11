package com.tradehero.th.fragments.leaderboard.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.android.common.SlidingTabLayout;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.route.Routable;
import com.tradehero.th.R;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTO;
import com.tradehero.th.api.leaderboard.def.LeaderboardDefDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefListKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.billing.BasePurchaseManagerFragment;
import com.tradehero.th.fragments.leaderboard.FriendLeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardMarkUserListFragment;
import com.tradehero.th.fragments.leaderboard.LeaderboardType;
import com.tradehero.th.fragments.social.PeopleSearchFragment;
import com.tradehero.th.fragments.tutorial.WithTutorial;
import com.tradehero.th.fragments.web.BaseWebViewFragment;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.tradehero.th.persistence.prefs.PreferenceModule;
import com.tradehero.th.persistence.prefs.THPreference;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.rx.ToastAction;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.SimpleEvent;
import dagger.Lazy;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Routable("providers")
public class LeaderboardCommunityFragment extends BasePurchaseManagerFragment
        implements WithTutorial, View.OnClickListener
{
    @Inject Lazy<LeaderboardDefListCacheRx> leaderboardDefListCache;
    @Inject Analytics analytics;
    @Inject CommunityPageDTOFactory communityPageDTOFactory;
    @Inject @THPreference(PreferenceModule.PREF_ON_BOARDING_EXCHANGE)
    StringPreference onBoardExchangePref;

    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;

    @InjectView(R.id.community_screen) BetterViewAnimator communityScreen;
    @InjectView(R.id.pager) ViewPager tabViewPager;
    @InjectView(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    private BaseWebViewFragment webFragment;
    private int currentDisplayedChildLayoutId;
    private String countryCode;
    @Nullable protected Subscription leaderboardDefListFetchSubscription;

    @Override public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leaderboard_community_screen, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.inject(this, view);
    }

    private void setUpViewPager(LeaderboardDefDTOList leaderboardDefDTOs) {
        tabViewPager.setAdapter(new TabbedLBPageAdapter(getChildFragmentManager(), leaderboardDefDTOs));
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
    }

    @Override public void onStart()
    {
        super.onStart();
        // show either progress bar or def list, whichever last seen on this screen
        if (currentDisplayedChildLayoutId != 0)
        {
            communityScreen.setDisplayedChildByLayoutId(currentDisplayedChildLayoutId);
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Community));

        fetchLeaderboardDefList();
        // We came back into view so we have to forget the web fragment
        detachWebFragment();
    }

    @Override public void onStop()
    {
        unsubscribe(leaderboardDefListFetchSubscription);
        leaderboardDefListFetchSubscription = null;
        currentDisplayedChildLayoutId = communityScreen.getDisplayedChildLayoutId();
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        detachWebFragment();
        super.onDestroy();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_search_menu, menu);
        setActionBarTitle(R.string.dashboard_community);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
            case R.id.btn_search:
                pushSearchFragment();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
    //</editor-fold>

    private void detachWebFragment()
    {
        if (this.webFragment != null)
        {
            this.webFragment.setThIntentPassedListener(null);
        }
        this.webFragment = null;
    }

    private void fetchLeaderboardDefList()
    {
        unsubscribe(leaderboardDefListFetchSubscription);

        Observable observable = userProfileCache.getOne(currentUserId.toUserBaseKey())
                .switchMap(new Func1<Pair<UserBaseKey, UserProfileDTO>, Observable<?>>()
        {
            @Override public Observable<?> call(Pair<UserBaseKey, UserProfileDTO> pair)
            {
                countryCode = null; //onBoardExchangePref.get();
                UserProfileDTO userProfileDTO = pair.second;
                if (TextUtils.isEmpty(countryCode)) {
                    countryCode = userProfileDTO.countryCode;
                }
                Observable<LeaderboardDefDTOList> leaderboardDefObservable =
                        leaderboardDefListCache.get().get(new LeaderboardDefListKey(1))
                                .map(new PairGetSecond<LeaderboardDefListKey, LeaderboardDefDTOList>())
                                .map(new Func1<LeaderboardDefDTOList, LeaderboardDefDTOList>()
                                {
                                    @Override public LeaderboardDefDTOList call(LeaderboardDefDTOList leaderboardDefDTOs)
                                    {
                                        return communityPageDTOFactory.collectFromCaches(countryCode);
                                    }
                                })
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread());
                return leaderboardDefObservable;
            }
        });

        leaderboardDefListFetchSubscription = AppObservable.bindFragment(
                this,
                observable)
                .subscribe(
                        new Action1<LeaderboardDefDTOList>()
                        {
                            @Override public void call(LeaderboardDefDTOList leaderboardDefDTOs)
                            {
                                setUpViewPager(leaderboardDefDTOs);
                                communityScreen.setDisplayedChildByLayoutId(R.id.lb_pager_wrapper);
                            }
                        },
                        new ToastAction<Throwable>(getString(R.string.error_fetch_leaderboard_def_list_key)));
    }

    @Override public int getTutorialLayout()
    {
        return R.layout.tutorial_leaderboard_community;
    }

    @Override public void onClick(View v)
    {
        if (v.getId() == R.id.error)
        {
            //if error view is click it means to reload the data
            communityScreen.setDisplayedChildByLayoutId(R.id.progress);
            fetchLeaderboardDefList();
        }
    }

    private void pushSearchFragment()
    {
        if (navigator != null)
        {
            navigator.get().pushFragment(PeopleSearchFragment.class, null);
        }
    }

    private class TabbedLBPageAdapter extends FragmentPagerAdapter {
        private LeaderboardDefDTOList leaderboardDefDTOs;

        public TabbedLBPageAdapter(FragmentManager fm, LeaderboardDefDTOList leaderboardDefDTOs)
        {
            super(fm);
            this.leaderboardDefDTOs = leaderboardDefDTOs;
        }

        @Override public Fragment getItem(int position)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_ShowLeaderboard));
            LeaderboardDefDTO leaderboardDefDTO = leaderboardDefDTOs.get(position);
            Bundle args = new Bundle(getArguments());

            if (leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.FRIEND_ID) {
                FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
                return  Fragment.instantiate(getActivity(), FriendLeaderboardMarkUserListFragment.class.getName(), args);
            }

            LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
            LeaderboardMarkUserListFragment.putLeaderboardType(args, LeaderboardType.STOCKS);
            return  Fragment.instantiate(getActivity(), LeaderboardMarkUserListFragment.class.getName(), args);

        }

        @Override public int getCount()
        {
            return leaderboardDefDTOs.size();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return leaderboardDefDTOs.get(position).name;
        }
    }
}
