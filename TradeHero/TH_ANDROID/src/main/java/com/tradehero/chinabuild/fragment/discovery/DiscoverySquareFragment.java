package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.cache.NoticeNewsCache;
import com.tradehero.chinabuild.data.AdsDTO;
import com.tradehero.chinabuild.data.TimeLineTotalInfo;
import com.tradehero.chinabuild.fragment.competition.CompetitionDetailFragment;
import com.tradehero.chinabuild.fragment.competition.CompetitionMainFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.fragment.stocklearning.StockLearningMainFragment;
import com.tradehero.chinabuild.fragment.web.WebViewFragment;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.key.TimelineItemDTOKey;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.viewpagerindicator.CirclePageIndicator;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Discovery Square fragment
 *
 * Created by palmer on 15/1/26.
 */
public class DiscoverySquareFragment extends DashboardFragment implements View.OnClickListener
{
    //Square Reward Views
    private LinearLayout rewardLL;
    private TextView numberOfRewardTV;

    //Square Recent Views
    private LinearLayout recentLL;
    private TextView numberOfRecentTV;

    //Square Favorite Views
    private LinearLayout favoriteLL;
    private TextView numberOfEssentialTV;

    //Square Novice Views
    private LinearLayout noviceLL;
    private TextView numberOfLearningTV;

    //Square News
    private LinearLayout newsLL;

    //Square Classroom
    private LinearLayout classroomLL;

    @InjectView(R.id.ad_layout) RelativeLayout rlTopBanner;
    @InjectView(R.id.pager) ViewPager pager;
    @InjectView(R.id.indicator) CirclePageIndicator indicator;
    private List<View> views = new ArrayList();
    private Timer timer;

    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    @Inject CurrentUserId currentUserId;

    //Advertisement Record
    public static boolean SHOW_ADVERTISEMENT = true;

    public static long NUMBER_TIMELINES_NOTICE = 0;
    public static long NUMBER_TIMELINES_ESSENTIAL = 0;
    public static long NUMBER_TIMELINES_REWRAD = 0;
    public static long NUMBER_TIMELINES_RECENT = 0;
    public static long NUMBER_ACTIVITY_TIMELINES_NOTICE = 0;
    public static long NUMBER_ACTIVITY_TIMELINES_ESSENTIAL = 0;
    public static long NUMBER_ACTIVITY_TIMELINES_REWRAD = 0;
    public static long NUMBER_ACTIVITY_TIMELINES_RECENT = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_square, container, false);
        ButterKnife.inject(this, view);
        recentLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_recent);
        rewardLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_reward);
        favoriteLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_favorite);
        noviceLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_learning);
        newsLL = (LinearLayout) view.findViewById(R.id.linearlayout_square_news);
        classroomLL = (LinearLayout)view.findViewById(R.id.linearlayout_square_classroom);
        recentLL.setOnClickListener(this);
        rewardLL.setOnClickListener(this);
        favoriteLL.setOnClickListener(this);
        noviceLL.setOnClickListener(this);
        newsLL.setOnClickListener(this);
        classroomLL.setOnClickListener(this);
        numberOfEssentialTV = (TextView)view.findViewById(R.id.textview_timelines_head_total_favorite);
        numberOfLearningTV = (TextView)view.findViewById(R.id.textview_timelines_head_total_learning);
        numberOfRewardTV = (TextView)view.findViewById(R.id.textview_timelines_head_total_reward);
        numberOfRecentTV = (TextView)view.findViewById(R.id.textview_timelines_head_total_discuss);
        initNumberViews();

        //Download Advertisement
        downloadAdvertisements();

        //Download Notice News
        retrieveNoticeNews();

        //Retrieve Total TimeLine
        retrieveTimeLineTotalInfo();

        return view;
    }

    @Override
    public void onDestroyView(){
        stopTimer();
        super.onDestroyView();
    }

    @Override
    public void onClick(View view)
    {
        int viewId = view.getId();
        switch (viewId)
        {
            case R.id.linearlayout_square_recent:
                gotoDashboard(DiscoveryRecentNewsFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_reward:
                gotoDashboard(DiscoveryRewardFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_favorite:
                gotoDashboard(DiscoveryEssentialFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_learning:
                gotoDashboard(DiscoveryLearningFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_news:
                gotoDashboard(DiscoveryNewsFragment.class, new Bundle());
                break;
            case R.id.linearlayout_square_classroom:
                gotoDashboard(StockLearningMainFragment.class, new Bundle());
                break;
        }
    }

    @OnClick(R.id.ad_close_button)
    public void onClickBanner()
    {
        dismissTopBanner();
        stopTimer();
        SHOW_ADVERTISEMENT = false;
    }

    public void dismissTopBanner()
    {
        Animation animation = AnimationUtils.loadAnimation(getActivity(),R.anim.alpha_out);
        animation.setAnimationListener(new Animation.AnimationListener()
        {
            @Override public void onAnimationStart(Animation animation){}
            @Override public void onAnimationEnd(Animation animation)
            {
                rlTopBanner.setVisibility(View.GONE);
            }
            @Override public void onAnimationRepeat(Animation animation){}
        });
        rlTopBanner.startAnimation(animation);
    }

    private void initTopBanner(List<AdsDTO> adsDTOs)
    {
        if(getActivity()== null|| rlTopBanner == null){
            return;
        }
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        views = new ArrayList();
        rlTopBanner.setVisibility(View.VISIBLE);

        for (int num = 0; num < adsDTOs.size(); num++)
        {
            final AdsDTO adsDTO = adsDTOs.get(num);
            View view = layoutInflater.inflate(R.layout.search_square_top_banner_item, null);
            ImageView imageView = (ImageView) view.findViewById(R.id.imgTopBannerItem);
            ImageLoader.getInstance().displayImage(adsDTO.bannerImageUrl, imageView, UniversalImageLoader.getAdvertisementImageLoaderOptions());
            views.add(view);
            view.setOnClickListener(new View.OnClickListener()
            {
                @Override public void onClick(View view)
                {
                    enterTargetTopic(adsDTO);
                }
            });
        }

        pager.setAdapter(pageAdapter);
        indicator.setViewPager(pager);
        startTimer();

        pager.setOnTouchListener(new View.OnTouchListener()
        {
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    startTimer();
                }
                else
                {
                    stopTimer();
                }
                return false;
            }
        });
    }

    PagerAdapter pageAdapter = new PagerAdapter()
    {
        @Override
        public void destroyItem(ViewGroup container, int position, Object object)
        {
            container.removeView(views.get(position));
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position)
        {
            container.addView(views.get(position));
            return views.get(position);
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1)
        {
            return arg0 == arg1;
        }

        @Override
        public int getCount()
        {
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
                if(getActivity()==null){
                    stopTimer();
                    return;
                }
                getActivity().runOnUiThread(new Runnable()
                {
                    public void run()
                    {
                        pager.setCurrentItem((pager.getCurrentItem() + 1) % pageAdapter.getCount());
                    }
                });
            }
        }, 6000, 6000);
    }

    private void retrieveNoticeNews(){
        timelineServiceWrapper.get().getTimelineNotice(currentUserId.toUserBaseKey(), new Callback<TimelineDTO>() {
            @Override
            public void success(TimelineDTO timelineDTO, Response response) {
                NoticeNewsCache.getInstance().setTimelineDTO(timelineDTO);
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void downloadAdvertisements(){
        if(!SHOW_ADVERTISEMENT){
            if(rlTopBanner.getVisibility()==View.VISIBLE){
                rlTopBanner.setVisibility(View.GONE);
            }
            return;
        }
        timelineServiceWrapper.get().downloadAdvertisements(new Callback<List<AdsDTO>>() {
            @Override
            public void success(List<AdsDTO> adsDTOSet, Response response) {
                if(rlTopBanner == null){
                    return;
                }
                if(adsDTOSet!=null && adsDTOSet.size()>0){
                    initTopBanner(adsDTOSet);
                }else {
                    rlTopBanner.setVisibility(View.GONE);
                }
            }

            @Override
            public void failure(RetrofitError retrofitError) {
            }
        });
    }

    private void retrieveTimeLineTotalInfo(){
        timelineServiceWrapper.get().retrieveTimeLineTotalInfo(new Callback<TimeLineTotalInfo>() {
            @Override
            public void success(TimeLineTotalInfo timeLineTotalInfo, Response response) {
                NUMBER_TIMELINES_NOTICE = timeLineTotalInfo.guideCount;
                NUMBER_TIMELINES_ESSENTIAL = timeLineTotalInfo.essentialCount;
                NUMBER_TIMELINES_REWRAD = timeLineTotalInfo.questionCount;
                NUMBER_TIMELINES_RECENT = timeLineTotalInfo.originalCount;
                NUMBER_ACTIVITY_TIMELINES_NOTICE = timeLineTotalInfo.guideActivity;
                NUMBER_ACTIVITY_TIMELINES_ESSENTIAL = timeLineTotalInfo.essentialActivity;
                NUMBER_ACTIVITY_TIMELINES_REWRAD = timeLineTotalInfo.questionActivity;
                NUMBER_ACTIVITY_TIMELINES_RECENT = timeLineTotalInfo.originalActivity;
                initNumberViews();
            }

            @Override
            public void failure(RetrofitError retrofitError) {

            }
        });
    }

    private void initNumberViews(){
        if(NUMBER_ACTIVITY_TIMELINES_NOTICE > 0 && numberOfLearningTV !=null){
            numberOfLearningTV.setText("(" + NUMBER_ACTIVITY_TIMELINES_NOTICE + ")");
        }
        if(NUMBER_ACTIVITY_TIMELINES_ESSENTIAL > 0 && numberOfEssentialTV!=null){
            numberOfEssentialTV.setText("(" + NUMBER_ACTIVITY_TIMELINES_ESSENTIAL + ")");
        }
        if(NUMBER_ACTIVITY_TIMELINES_REWRAD > 0 && numberOfRewardTV !=null){
            numberOfRewardTV.setText("(" + NUMBER_ACTIVITY_TIMELINES_REWRAD + ")");
        }
        if(NUMBER_ACTIVITY_TIMELINES_RECENT > 0 && numberOfRecentTV !=null){
            numberOfRecentTV.setText("(" + NUMBER_ACTIVITY_TIMELINES_RECENT + ")");
        }

    }

    private void enterTargetTopic(AdsDTO adsDTO){
        if(adsDTO.competitionId >0){
            jumpCompetitionDetailPage(adsDTO.competitionId);
            return;
        }
        if(adsDTO.timeLineItemId >0){
            jumpTimeLine(adsDTO.timeLineItemId);
            return;
        }
        if(adsDTO.redirectUrl!=null && !adsDTO.redirectUrl.equals("")){
            jumpWeb(adsDTO.redirectUrl);
            return;
        }
    }

    private void jumpCompetitionDetailPage(int competitionId){
        Bundle bundle = new Bundle();
        bundle.putInt(CompetitionDetailFragment.BUNDLE_COMPETITION_ID, competitionId);
        gotoDashboard(CompetitionMainFragment.class, bundle);
    }

    private void jumpTimeLine(int timeLineItemId){
        Bundle bundle = new Bundle();
        Bundle discussBundle = new Bundle();
        discussBundle.putString(TimelineItemDTOKey.BUNDLE_KEY_TYPE, DiscussionType.TIMELINE_ITEM.name());
        discussBundle.putInt(TimelineItemDTOKey.BUNDLE_KEY_ID, timeLineItemId);
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, discussBundle);
        gotoDashboard(TimeLineItemDetailFragment.class, bundle);
        return;
    }

    private void jumpWeb(String url){
        Bundle bundle = new Bundle();
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_URL, url);
        bundle.putString(WebViewFragment.BUNDLE_WEBVIEW_TITLE, "");
        gotoDashboard(WebViewFragment.class, bundle);
    }
}
