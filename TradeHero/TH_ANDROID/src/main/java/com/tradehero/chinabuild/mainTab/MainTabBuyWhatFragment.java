package com.tradehero.chinabuild.mainTab;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.buyWhat.FollowBuyFragment;
import com.tradehero.chinabuild.buyWhat.FragmentStockGod;
import com.tradehero.chinabuild.data.AdsDTO;
import com.tradehero.chinabuild.fragment.AbsBaseFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMainFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.search.SearchUnitFragment;
import com.tradehero.chinabuild.fragment.stockRecommend.StockRecommendFragment;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.th.R;
import com.tradehero.th.adapters.StockRecommendListAdapter;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.leaderboard.LeaderboardDTO;
import com.tradehero.th.api.leaderboard.key.LeaderboardKey;
import com.tradehero.th.api.stockRecommend.StockRecommendDTOList;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.leaderboard.LeaderboardCache;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class MainTabBuyWhatFragment extends AbsBaseFragment implements View.OnClickListener {
    private ImageView mQueryBtn;
    private ImageView mNewSuggestBtn;
    private ImageView mFollowChanceBtn;
    private ImageView mHotStockBtn;
    private ImageView mWinRateBtn;
    private TextView mMoreButton;
    private SecurityListView mListView;
    private StockRecommendListAdapter stockRecommendListAdapter;
    private int currentPage = 0;
    private int ITEMS_PER_PAGE = 10;
    private RelativeLayout mAdLayout;
    private ViewPager pager;
    private CirclePageIndicator mAdIndicator;
//    Button mAdCloseButton;
    private ProgressBar mProgress;
    @Inject LeaderboardCache leaderboardCache;
    protected DTOCacheNew.Listener<LeaderboardKey, LeaderboardDTO> leaderboardCacheListener;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    @Inject CurrentUserId currentUserId;
    private List<View> views;
//    private List<View> views = new ArrayList();
//    private Timer timer;
//    public static boolean SHOW_ADVERTISEMENT = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.main_tab_fragment_buy_what_new_layout, container, false);
        View listHeader = inflater.inflate(R.layout.main_tab_fragment_stockgod_top_layout, null, false);
        initView(view, listHeader);
        fetchStockRecommendList();
        downloadAdvertisements();
        return view;
    }

    private void fetchStockRecommendList() {
        timelineServiceWrapper.get().getTimelineStockRecommend(currentUserId.toUserBaseKey(), 3, -1, -1, new Callback<StockRecommendDTOList>() {
            @Override
            public void success(StockRecommendDTOList stockRecommendDTOList, Response response) {
                stockRecommendListAdapter.setItems(stockRecommendDTOList);
                stockRecommendListAdapter.notifyDataSetChanged();
                finish();
            }

            @Override
            public void failure(RetrofitError error) {
                finish();
            }

            private void finish() {
                mProgress.setVisibility(View.INVISIBLE);
                mListView.onRefreshComplete();
            }
        });
    }

    private void initView(View view, View header) {
        mAdLayout = (RelativeLayout) header.findViewById(R.id.ad_layout);
        pager = (ViewPager) header.findViewById(R.id.pager);
        mAdIndicator = (CirclePageIndicator) header.findViewById(R.id.indicator);
//        mAdCloseButton = (Button) view.findViewById(R.id.ad_close_button);
//        mAdCloseButton.setOnClickListener(this);
        mQueryBtn = (ImageView) header.findViewById(R.id.query_btn);
        mQueryBtn.setOnClickListener(this);
        mNewSuggestBtn = (ImageView) header.findViewById(R.id.new_suggest_icon);
        mNewSuggestBtn.setOnClickListener(this);
        mFollowChanceBtn = (ImageView) header.findViewById(R.id.follow_chance_icon);
        mFollowChanceBtn.setOnClickListener(this);
        mHotStockBtn = (ImageView) header.findViewById(R.id.stock_recommend);
        mHotStockBtn.setOnClickListener(this);
        mWinRateBtn = (ImageView) header.findViewById(R.id.win_rate_icon);
        mWinRateBtn.setOnClickListener(this);
        mProgress = (ProgressBar) view.findViewById(R.id.progress);
        mMoreButton = (TextView) header.findViewById(R.id.showAllStockRecommend);
        mMoreButton.setOnClickListener(this);

        mListView = (SecurityListView) view.findViewById(R.id.list);
        if (stockRecommendListAdapter == null) {
            stockRecommendListAdapter = new StockRecommendListAdapter(getActivity());
        }
        mListView.setAdapter(stockRecommendListAdapter);
        mListView.getRefreshableView().addHeaderView(header);
        mListView.setMode(PullToRefreshBase.Mode.DISABLED);
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
            case R.id.stock_recommend:
            case R.id.showAllStockRecommend:
                gotoDashboard(StockRecommendFragment.class.getName());
                break;
            case R.id.win_rate_icon:
                Bundle args2 = new Bundle();
                args2.putInt(FragmentStockGod.TAB_KEY, 1);
                gotoDashboard(FragmentStockGod.class.getName(), args2);
                break;
//            case R.id.ad_close_button:
//                dismissTopBanner();
//                stopTimer();
//                SHOW_ADVERTISEMENT = false;
//                break;
        }
    }

//    public void dismissTopBanner() {
//        Animation animation = AnimationUtils.loadAnimation(getActivity(), R.anim.alpha_out);
//        animation.setAnimationListener(new Animation.AnimationListener() {
//            @Override
//            public void onAnimationStart(Animation animation) {
//            }
//
//            @Override
//            public void onAnimationEnd(Animation animation) {
//                mAdLayout.setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onAnimationRepeat(Animation animation) {
//            }
//        });
//        mAdLayout.startAnimation(animation);
//    }


    @Override
    public void onResume() {
        super.onResume();
        fetchStockRecommendList();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void downloadAdvertisements() {
//        if (!SHOW_ADVERTISEMENT) {
//            if (mAdLayout.getVisibility() == View.VISIBLE) {
//                mAdLayout.setVisibility(View.GONE);
//            }
//            return;
//        }
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
//        startTimer();

//        pager.setOnTouchListener(new View.OnTouchListener() {
//            public boolean onTouch(View v, MotionEvent event) {
//                if (event.getAction() == MotionEvent.ACTION_UP) {
//                    startTimer();
//                } else {
//                    stopTimer();
//                }
//                return false;
//            }
//        });
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

//    public void stopTimer() {
//        if (timer != null) {
//            timer.cancel();
//            timer = null;
//        }
//    }
//
//    public void startTimer() {
//        stopTimer();
//        timer = new Timer();
//        timer.schedule(new TimerTask() {
//            public void run() {
//                if (getActivity() == null) {
//                    stopTimer();
//                    return;
//                }
//                getActivity().runOnUiThread(new Runnable() {
//                    public void run() {
//                        pager.setCurrentItem((pager.getCurrentItem() + 1) % pageAdapter.getCount());
//                    }
//                });
//            }
//        }, 6000, 6000);
//    }
}
