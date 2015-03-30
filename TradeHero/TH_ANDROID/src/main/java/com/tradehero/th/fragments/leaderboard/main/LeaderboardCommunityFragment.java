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
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.tradehero.th.fragments.base.DashboardFragment;
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
import java.lang.ref.WeakReference;
import javax.inject.Inject;
import rx.Observable;
import rx.Subscription;
import rx.android.app.AppObservable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

@Routable("providers")
public class LeaderboardCommunityFragment extends BasePurchaseManagerFragment
        implements WithTutorial, View.OnClickListener
{
    private static final String KEY_CURRENT_LB_TYPE = "current.leader.board.type.";

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
    private LeaderboardDefDTOList leaderboardDefDTOs;

    private WeakReference<TabbedLBPageAdapter> stockLBAdapterRef;
    private WeakReference<TabbedLBPageAdapter> fxLBAdapterRef;

    /* The following 2 static fields are used to save the status of ActionBar and Tabs, so that users can still
    * return to the same page from other fragments.
    * */
    private static LeaderboardType LEADER_BOARD_Type = LeaderboardType.STOCKS;
    private static int LAST_TAB_POSITION = 0;

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

    private void setUpViewPager() {
        if (leaderboardDefDTOs == null) {
            return;
        }


        TabbedLBPageAdapter adapter = null;
        switch (LEADER_BOARD_Type) {
            case STOCKS:
                if (stockLBAdapterRef != null) {
                    adapter = stockLBAdapterRef.get();
                }
                if (adapter == null) {
                    adapter = new TabbedLBPageAdapter(getChildFragmentManager(), leaderboardDefDTOs);
                    stockLBAdapterRef = new WeakReference<>(adapter);
                }
                break;
            case FX:
                if (fxLBAdapterRef != null) {
                    adapter = fxLBAdapterRef.get();
                }
                if (adapter == null) {
                    LeaderboardDefDTOList filteredList = new LeaderboardDefDTOList();

                    for (LeaderboardDefDTO dto : leaderboardDefDTOs) {
                        if (dto.exchangeRestrictions){
                            continue;
                        }
                        filteredList.add(dto);
                    }
                    adapter = new TabbedLBPageAdapter(getChildFragmentManager(), filteredList);
                    fxLBAdapterRef = new WeakReference<>(adapter);
                }
                break;
            default:
                Timber.e("Invalid leaderboardType: " + LEADER_BOARD_Type);
                return;
        }

        tabViewPager.setAdapter(adapter);
        pagerSlidingTabStrip.setCustomTabView(R.layout.th_page_indicator, android.R.id.title);
        pagerSlidingTabStrip.setSelectedIndicatorColors(getResources().getColor(R.color.tradehero_tab_indicator_color));
        pagerSlidingTabStrip.setViewPager(tabViewPager);
        tabViewPager.setCurrentItem(LAST_TAB_POSITION, true);
        pagerSlidingTabStrip.setOnPageChangeListener(new ViewPager.OnPageChangeListener()
        {
            @Override public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels)
            {
                //
            }

            @Override public void onPageSelected(int position)
            {
                LAST_TAB_POSITION = position;
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
        analytics.addEvent(new SimpleEvent(AnalyticsConstants.TabBar_Community));
        showToolbarSpinner();
        if (leaderboardDefDTOs == null) {
            fetchLeaderboardDefList();
        } else {
            setUpViewPager();
        }
        // We came back into view so we have to forget the web fragment
        detachWebFragment();
    }

    @Override public void onStop()
    {
        unsubscribe(leaderboardDefListFetchSubscription);
        leaderboardDefListFetchSubscription = null;
        currentDisplayedChildLayoutId = communityScreen.getDisplayedChildLayoutId();

        hideToolbarSpinner();

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
        setActionBarTitle("");
        setUpToolbarSpinner();
    }

    private void setUpToolbarSpinner() {
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener()
        {
            @Override public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
            {
                LeaderboardType type;
                if (position == 0) {
                    type = LeaderboardType.STOCKS;
                } else {
                    type = LeaderboardType.FX;
                }
                Timber.e("onItemSelected: " + parent.getItemAtPosition(position));
                if (type != LEADER_BOARD_Type)
                {
                    LEADER_BOARD_Type = type;
                    setUpViewPager();
                }
            }

            @Override public void onNothingSelected(AdapterView<?> parent)
            {
                //do nothing
            }
        };
        configureDefaultSpinner(new String[] {
                getString(R.string.leaderboard_type_stocks),
                getString(R.string.leaderboard_type_fx)},
                listener, LEADER_BOARD_Type.ordinal());
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
                countryCode = onBoardExchangePref.get();
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
        private LeaderboardDefDTOList dtoList;

        public TabbedLBPageAdapter(FragmentManager fm, LeaderboardDefDTOList leaderboardDefDTOs)
        {
            super(fm);
            dtoList = leaderboardDefDTOs;
        }

        @Override public Fragment getItem(int position)
        {
            analytics.addEvent(new SimpleEvent(AnalyticsConstants.Leaderboards_ShowLeaderboard));
            LeaderboardDefDTO leaderboardDefDTO = dtoList.get(position);
            Bundle args = new Bundle(getArguments());

            if (leaderboardDefDTO.id == LeaderboardDefKeyKnowledge.FRIEND_ID) {
                FriendLeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
                return  Fragment.instantiate(getActivity(), FriendLeaderboardMarkUserListFragment.class.getName(), args);
            }

            DashboardFragment.setHasOptionMenu(args, false);
            LeaderboardMarkUserListFragment.putLeaderboardDefKey(args, leaderboardDefDTO.getLeaderboardDefKey());
            LeaderboardMarkUserListFragment.putLeaderboardType(args, LEADER_BOARD_Type);
            Fragment f = new LeaderboardMarkUserListFragment();
            f.setArguments(args);
            return  f;

        }

        @Override public int getCount()
        {
            return dtoList.size();
        }

        @Override public CharSequence getPageTitle(int position)
        {
            return dtoList.get(position).name;
        }

        @Override public long getItemId(int position)
        {
            return super.getItemId(position) + LEADER_BOARD_Type.hashCode();
        }
    }
}
