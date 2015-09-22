package com.tradehero.chinabuild.mainTab;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.buyWhat.FollowBuyFragment;
import com.tradehero.chinabuild.buyWhat.FragmentStockGod;
import com.tradehero.chinabuild.buyWhat.MainTabBuyWhatAdapter;
import com.tradehero.chinabuild.data.AdsDTO;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMainFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.LeaderboardUserDTOList;
import com.tradehero.th.api.leaderboard.key.LeaderboardDefKey;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.leaderboard.key.PagedLeaderboardKey;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.models.leaderboard.key.LeaderboardDefKeyKnowledge;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainTabBuyWhatFragment extends AbsBaseFragment implements View.OnClickListener {
    private ImageView mQueryBtn;
    private ImageView mNewSuggestBtn;
    private ImageView mFollowChanceBtn;
    private ImageView mHotStockBtn;
    private ImageView mWinRateBtn;
    private SecurityListView mListView;
    private MainTabBuyWhatAdapter mListViewAdapter;
    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 10;
    RelativeLayout mAdLayout;
    ViewPager pager;
    CirclePageIndicator mAdIndicator;
    Button mAdCloseButton;
    private ProgressBar mProgress;
    @Inject
    LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;
    @Inject
    Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private List<View> views = new ArrayList();
    private Timer timer;
    public static boolean SHOW_ADVERTISEMENT = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_stockgod_new_layout, container, false);
        initView(view);
        fetchBuyWhatList();
        downloadAdvertisements();
        return view;
    }

    private void fetchBuyWhatList() {
        leaderboardCacheListener = new BaseLeaderboardFragmentLeaderboardCacheListener();
        fetchLeaderboard();
    }

    protected void fetchLeaderboard() {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(new LeaderboardDefKey(LeaderboardDefKeyKnowledge.BUY_WHAT).key, PagedLeaderboardKey.FIRST_PAGE);
        key.perPage = ITEMS_PER_PAGE;
        key.page = 1;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key, true);
    }

    protected void fetchLeaderboardMore() {
        detachLeaderboardCacheListener();
        PagedLeaderboardKey key = new PagedLeaderboardKey(new LeaderboardDefKey(LeaderboardDefKeyKnowledge.BUY_WHAT).key, currentPage + 1);
        key.perPage = ITEMS_PER_PAGE;
        leaderboardCache.register(key, leaderboardCacheListener);
        leaderboardCache.getOrFetchAsync(key, true);
    }

    protected void detachLeaderboardCacheListener() {
        leaderboardCache.unregister(leaderboardCacheListener);
    }

    private void initView(View view) {
        mAdLayout = (RelativeLayout) view.findViewById(R.id.ad_layout);
        pager = (ViewPager) view.findViewById(R.id.pager);
        mAdIndicator = (CirclePageIndicator) view.findViewById(R.id.indicator);
        mAdCloseButton = (Button) view.findViewById(R.id.ad_close_button);
        mAdCloseButton.setOnClickListener(this);
        mQueryBtn = (ImageView) view.findViewById(R.id.query_btn);
        mQueryBtn.setOnClickListener(this);
        mNewSuggestBtn = (ImageView) view.findViewById(R.id.new_suggest_icon);
        mNewSuggestBtn.setOnClickListener(this);
        mFollowChanceBtn = (ImageView) view.findViewById(R.id.follow_chance_icon);
        mFollowChanceBtn.setOnClickListener(this);
        mHotStockBtn = (ImageView) view.findViewById(R.id.hot_stock_icon);
        mHotStockBtn.setOnClickListener(this);
        mWinRateBtn = (ImageView) view.findViewById(R.id.win_rate_icon);
        mWinRateBtn.setOnClickListener(this);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);

        mListView = (SecurityListView) view.findViewById(R.id.list);
        if (mListViewAdapter == null) {
            mListViewAdapter = new MainTabBuyWhatAdapter(getActivity());
        }
        mListView.setAdapter(mListViewAdapter);
        mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchLeaderboard();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchLeaderboardMore();
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.query_btn:
                gotoDashboard(SearchUnitFragment.class.getName(), new Bundle());
                break;
            case R.id.new_suggest_icon:
                gotoDashboard(FragmentStockGod.class.getName());
                break;
            case R.id.follow_chance_icon:
                gotoDashboard(FollowBuyFragment.class.getName());
                break;
            case R.id.hot_stock_icon:
                Bundle args = new Bundle();
                args.putInt(FragmentStockGod.TAB_KEY, 3);
                gotoDashboard(FragmentStockGod.class.getName(), args);
                break;
            case R.id.win_rate_icon:
                Bundle args2 = new Bundle();
                args2.putInt(FragmentStockGod.TAB_KEY, 1);
                gotoDashboard(FragmentStockGod.class.getName(), args2);
                break;
            case R.id.ad_close_button:
                dismissTopBanner();
                stopTimer();
                SHOW_ADVERTISEMENT = false;
                break;
        }
    }

    public void dismissTopBanner() {
        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mAdLayout.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mAdLayout.startAnimation(animation);
    }

    protected class BaseLeaderboardFragmentLeaderboardCacheListener implements DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> {
        @Override
        public void onDTOReceived(@NotNull LeaderboardKey key, @NotNull LeaderboardDTO value) {
            setListData(key, value.users);
            if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
                mListView.setMode(PullToRefreshBase.Mode.BOTH);
            }
            mListView.onRefreshComplete();
        }

        @Override
        public void onErrorThrown(@NotNull LeaderboardKey key, @NotNull Throwable error) {
            if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
                mListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
            }
            mListView.onRefreshComplete();
        }
    }

    private void setListData(LeaderboardKey key, LeaderboardUserDTOList listData) {
        if (((PagedLeaderboardKey) key).page == PagedLeaderboardKey.FIRST_PAGE) {
            currentPage = 0;
            mListViewAdapter.setItems(listData);
            if (listData.size() == 0) {
                mProgress.setVisibility(View.INVISIBLE);
            }
        } else {
            mListViewAdapter.addItems(listData);
        }

        //如果返回数据已经为空了，说明没有了下一页。
        if (listData.size() > 0) {
            currentPage += 1;
        }
        mListViewAdapter.notifyDataSetChanged();
    }

    @Override
    public void onPause() {
        detachLeaderboardCacheListener();
        super.onPause();
    }

    private void downloadAdvertisements() {
        if (!SHOW_ADVERTISEMENT) {
            if (mAdLayout.getVisibility() == View.VISIBLE) {
                mAdLayout.setVisibility(View.GONE);
            }
            return;
        }
        timelineServiceWrapper.get().downloadBuyWhatAdvertisements(new Callback<List<AdsDTO>>() {
            @Override
            public void success(List<AdsDTO> adsDTOSet, Response response) {
                if (mAdLayout == null) {
                    return;
                }
                if (adsDTOSet != null && adsDTOSet.size() > 0) {
                    initTopBanner(adsDTOSet);
                } else {
                    mAdLayout.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void initTopBanner(List<AdsDTO> adsDTOs) {
        if (getActivity() == null || mAdLayout == null) {
            return;
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList();
        mAdLayout.setVisibility(View.VISIBLE);

        for (int num = 0; num < adsDTOs.size(); num++) {
            final AdsDTO adsDTO = adsDTOs.get(num);
            View view = layoutInflater.inflate(R.layout.search_square_top_banner_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgTopBannerItem);
            ImageLoader.getInstance().displayImage(adsDTO.bannerImageUrl, imageView, UniversalImageLoader.getAdvertisementImageLoaderOptions());
            views.add(view);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    enterTargetTopic(adsDTO);
                }
            });
        }

        pager.setAdapter(pageAdapter);
        mAdIndicator.setViewPager(pager);
        startTimer();

        pager.setOnTouchListener(new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    startTimer();
                } else {
                    stopTimer();
                }
                return false;
            }
        });
    }

    private void enterTargetTopic(AdsDTO adsDTO) {
        if (adsDTO.competitionId > 0) {
            jumpCompetitionDetailPage(adsDTO.competitionId);
            return;
        }
        if (adsDTO.timeLineItemId > 0) {
            jumpTimeLine(adsDTO.timeLineItemId);
            return;
        }
        if (adsDTO.redirectUrl != null && !adsDTO.redirectUrl.equals("")) {
            jumpWeb(adsDTO.redirectUrl);
            return;
        }
    }

    private void jumpCompetitionDetailPage(int competitionId) {
        Bundle bundle = new Bundle();
        bundle.putInt(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, competitionId);
        gotoDashboard(CompetitionMainFragment.class.getName(), bundle);
    }

    private void jumpTimeLine(int timeLineItemId) {
        Bundle bundle = new Bundle();
        Bundle discussBundle = new Bundle();
        discussBundle.putString(TimelineItemDTOKey.BUNDLE_KEY_TYPE, DiscussionType.TIMELINE_ITEM.name());
        discussBundle.putInt(TimelineItemDTOKey.BUNDLE_KEY_ID, timeLineItemId);
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, discussBundle);
        gotoDashboard(TimeLineItemDetailFragment.class.getName(), bundle);
        return;
    }

    private void jumpWeb(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_URL, url);
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_TITLE, "");
        gotoDashboard(WebViewFragment.class.getName(), bundle);
    }

    PagerAdapter pageAdapter = new PagerAdapter() {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public int getCount() {
            return (views == null) ? 0 : views.size();
        }
    };

    public void stopTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void startTimer() {
        stopTimer();
        timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                if (getActivity() == null) {
                    stopTimer();
                    return;
                }
                getActivity().runOnUiThread(new Runnable() {
                    public void run() {
                        pager.setCurrentItem((pager.getCurrentItem() + 1) % pageAdapter.getCount());
                    }
                });
            }
        }, 6000, 6000);
    }
}
