package com.ayondo.academy.fragments.leaderboard.main;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.common.SlidingTabLayout;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.rx.PairGetSecond;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.route.Routable;
import com.ayondo.academy.R;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTO;
import com.ayondo.academy.api.leaderboard.def.LeaderboardDefDTOList;
import com.ayondo.academy.api.leaderboard.key.LeaderboardDefListKey;
import com.ayondo.academy.api.users.CurrentUserId;
import com.ayondo.academy.api.users.UserBaseKey;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.fragments.base.BaseLiveFragmentUtil;
import com.ayondo.academy.fragments.billing.BasePurchaseManagerFragment;
import com.ayondo.academy.fragments.leaderboard.FriendLeaderboardMarkUserRecyclerFragment;
import com.ayondo.academy.fragments.leaderboard.LeaderboardMarkUserRecyclerFragment;
import com.ayondo.academy.fragments.leaderboard.LeaderboardType;
import com.ayondo.academy.fragments.social.PeopleSearchFragment;
import com.ayondo.academy.fragments.tutorial.WithTutorial;
import com.ayondo.academy.fragments.web.BaseWebViewIntentFragment;
import com.ayondo.academy.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.ayondo.academy.persistence.leaderboard.LeaderboardDefListCacheRx;
import com.ayondo.academy.persistence.prefs.PreferenceModule;
import com.ayondo.academy.persistence.prefs.THPreference;
import com.ayondo.academy.persistence.user.UserProfileCacheRx;
import com.ayondo.academy.rx.TimberOnErrorAction1;
import com.ayondo.academy.rx.ToastOnErrorAction1;
import com.ayondo.academy.widget.OffOnViewSwitcher;
import com.ayondo.academy.widget.OffOnViewSwitcherEvent;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable("providers")
public class LeaderboardCommunityFragment extends BasePurchaseManagerFragment
        implements WithTutorial, View.OnClickListener
{
    @Inject LeaderboardDefListCacheRx leaderboardDefListCache;
    //TODO Change Analytics
    //@Inject Analytics analytics;
    @Inject @THPreference(PreferenceModule.PREF_ON_BOARDING_EXCHANGE) StringPreference onBoardExchangePref;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject Toolbar toolbar;

    @Bind(R.id.community_screen) BetterViewAnimator communityScreen;
    @Bind(R.id.pager) ViewPager tabViewPager;
    @Bind(R.id.tabs) SlidingTabLayout pagerSlidingTabStrip;

    private BaseWebViewIntentFragment webFragment;
    private int currentDisplayedChildLayoutId;
    private LeaderboardDefDTOList leaderboardDefDTOs;

    /* The following 2 static fields are used to save the status of ActionBar and Tabs, so that users can still
    * return to the same page from other fragments.
    * */
    private static LeaderboardType leaderboardType = LeaderboardType.STOCKS;
    private static int lastTabPosition = 0;

    @Nullable protected Subscription leaderboardDefListFetchSubscription;
    private OffOnViewSwitcher stockFxSwitcher;
    private BaseLiveFragmentUtil liveFragmentUtil;

    @Override public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.leaderboard_community_screen, container, false);
    }

    @Override public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
        liveFragmentUtil = BaseLiveFragmentUtil.createFor(this, view);
    }

    private void setUpViewPager()
    {
        if (leaderboardDefDTOs == null)
        {
            return;
        }

        TabbedLBPageAdapter adapter;
        switch (leaderboardType)
        {
            case STOCKS:
                adapter = new TabbedLBPageAdapter(getChildFragmentManager(), leaderboardDefDTOs, leaderboardType);
                break;
            case FX:
                LeaderboardDefDTOList filteredList = new LeaderboardDefDTOList();
                for (LeaderboardDefDTO dto : leaderboardDefDTOs)
                {
                    if (dto.exchangeRestrictions)
                    {
                        continue;
                    }
                    filteredList.add(dto);
                }
                adapter = new TabbedLBPageAdapter(getChildFragmentManager(), filteredList, leaderboardType);
                break;
            default:
                Timber.e("Invalid leaderboardType: " + leaderboardType);
                return;
        }

        tabViewPager.setAdapter(adapter);
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        tabViewPager.setCurrentItem(lastTabPosition, true);
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                //
            }

            @Override public void onPageSelected(int position)
            {
                lastTabPosition = position;
            }

            @Override public void onPageScrollStateChanged(int state)
            {
                //
            }
        });
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
        //TODO Change Analytics
        //analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Community));
        if (leaderboardDefDTOs == null)
        {
            fetchLeaderboardDefList();
        }
        else
        {
            setUpViewPager();
        }
        // We came back into view so we have to forget the web fragment
        detachWebFragment();
        liveFragmentUtil.onResume();
    }

    @Override public void onLiveTradingChanged(boolean isLive)
    {
        super.onLiveTradingChanged(isLive);
        liveFragmentUtil.setCallToAction(isLive);
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
        liveFragmentUtil.onDestroyView();
        liveFragmentUtil = null;
        ButterKnife.unbind(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        detachWebFragment();
        toolbar = null;
        super.onDestroy();
    }

    //<editor-fold desc="ActionBar">
    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.social_search_menu, menu);
        setUpCustomToolbarView();
    }

    private void setUpCustomToolbarView()
    {
        View view = LayoutInflater.from(actionBarOwnerMixin.getActionBar().getThemedContext())
                .inflate(R.layout.leaderboard_custom_actionbar, toolbar, false);
        setActionBarTitle("");
        stockFxSwitcher = (OffOnViewSwitcher) view.findViewById(R.id.switch_stock_fx);
        onDestroyOptionsMenuSubscriptions.add(stockFxSwitcher.getSwitchObservable()
                .subscribe(
                        new Action1<OffOnViewSwitcherEvent>()
                        {
                            @Override public void call(OffOnViewSwitcherEvent event)
                            {
                                LeaderboardType type;
                                if (!event.isOn)
                                {
                                    type = LeaderboardType.STOCKS;
                                }
                                else
                                {
                                    type = LeaderboardType.FX;
                                }
                                if (type != leaderboardType)
                                {
                                    leaderboardType = type;
                                    setUpViewPager();
                                }
                            }
                        },
                        new TimberOnErrorAction1("Failed to listen to stockFxSwitcher in LeaderboardCommunityFragment")));
        stockFxSwitcher.setIsOn(leaderboardType.equals(LeaderboardType.FX), false);
        actionBarOwnerMixin.setCustomView(view);
    }

    @Override public boolean onOptionsItemSelected(MenuItem item)
    {
        // TODO switch sorting type for leaderboard
        switch (item.getItemId())
        {
            case R.id.btn_search:
                navigator.get().pushFragment(PeopleSearchFragment.class);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override public void onDestroyOptionsMenu()
    {
        stockFxSwitcher = null;
        super.onDestroyOptionsMenu();
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
        final String prefCountryCode = onBoardExchangePref.get();
        Observable<LeaderboardDefDTOList> observable = Observable.combineLatest(
                userProfileCache.getOne(currentUserId.toUserBaseKey())
                        .map(new PairGetSecond<UserBaseKey, UserProfileDTO>()),
                leaderboardDefListCache.get(new LeaderboardDefListKey(1))
                        .map(new PairGetSecond<LeaderboardDefListKey, LeaderboardDefDTOList>()),
                new Func2<UserProfileDTO, LeaderboardDefDTOList, LeaderboardDefDTOList>()
                {
                    @Override public LeaderboardDefDTOList call(UserProfileDTO userProfileDTO, LeaderboardDefDTOList leaderboardDefDTOs)
                    {
                        String countryCode;
                        if (TextUtils.isEmpty(prefCountryCode))
                        {
                            countryCode = userProfileDTO.countryCode;
                        }
                        else
                        {
                            countryCode = prefCountryCode;
                        }
                        return CommunityPageDTOFactory.reOrder(leaderboardDefDTOs, countryCode);
                    }
                })
                .subscribeOn(Schedulers.computation());

        unsubscribe(leaderboardDefListFetchSubscription);
        leaderboardDefListFetchSubscription = AppObservable.bindSupportFragment(this, observable)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                        new Action1<LeaderboardDefDTOList>()
                        {
                            @Override public void call(LeaderboardDefDTOList leaderboardDefDTOs)
                            {
                                LeaderboardCommunityFragment.this.leaderboardDefDTOs = leaderboardDefDTOs;
                                setUpViewPager();
                                communityScreen.setDisplayedChildByLayoutId(R.id.lb_pager_wrapper);
                            }
                        },
                        new ToastOnErrorAction1(getString(R.string.error_fetch_leaderboard_def_list_key)));
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

    private class TabbedLBPageAdapter extends FragmentPagerAdapter
    {
        private final LeaderboardDefDTOList dtoList;
        private final LeaderboardType leaderboardType;

        public TabbedLBPageAdapter(
                @NonNull FragmentManager fm,
                @NonNull LeaderboardDefDTOList leaderboardDefDTOs,
                @NonNull LeaderboardType leaderboardType)
        {
            super(fm);
            dtoList = leaderboardDefDTOs;
            this.leaderboardType = leaderboardType;
        }

        @Override public Fragment getItem(int position)
        {
            //TODO Change Analytics
            //analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_ShowLeaderboard));
            LeaderboardDefDTO leaderboardDefDTO = dtoList.get(position);
            Bundle args = new Bundle(getArguments());

            if (leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.FRIEND_ID)
            {
                FriendLeaderboardMarkUserRecyclerFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
                return Fragment.instantiate(getActivity(), FriendLeaderboardMarkUserRecyclerFragment.class.getName(), args);
            }

            LeaderboardMarkUserRecyclerFragment.setHasOptionMenu(args, false);
            LeaderboardMarkUserRecyclerFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
            LeaderboardMarkUserRecyclerFragment.putLeaderboardType(args, leaderboardType);
            return Fragment.instantiate(getActivity(), LeaderboardMarkUserRecyclerFragment.class.getName(), args);
        }

        @Override public int getCount()
        {
            return dtoList.size();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return LeaderboardDefKeyKnowledge.getDesiredName(getResources(), dtoList.get(position));
        }

        @Override public long getItemId(int position)
        {
            return super.getItemId(position) + leaderboardType.hashCode();
        }
    }
}
