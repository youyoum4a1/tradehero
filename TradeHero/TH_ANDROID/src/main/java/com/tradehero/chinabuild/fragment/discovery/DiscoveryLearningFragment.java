package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Created by palmer on 15/1/30.
 */
public class DiscoveryLearningFragment extends DashboardFragment
{
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;
    private int maxID = -1;

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeheroprogressbar_discovery) TradeHeroProgressBar progressBar;

    private UserTimeLineAdapter adapter;

    @Inject Analytics analytics;

    private int PERPAGE = 20;

    //head view
    private View headerView;
    private ImageView iconHeadIV;
    private TextView titleHeadTV;
    private TextView totalHeadTV;
    private TextView numberTimelinesHeadTV;
    private TextView numberRepliesHeadTV;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new UserTimeLineAdapter(getActivity(), TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING);
        adapter.isShowHeadAndName = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_learning, container, false);
        ButterKnife.inject(this, view);

        initView();

        if (adapter.getCount() == 0)
        {
            fetchTimeLine();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_discovery);
            progressBar.startLoading();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(R.string.discovery_square_novice);
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        initHeadViews();
        listTimeLine.setAdapter(adapter);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override public void OnTimeLineItemClicked(int position)
            {
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                enterTimeLineDetail(dto);
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.DISCOVERY_GUIDE_ITEM, String.valueOf(position)));
            }

            @Override public void OnTimeLinePraiseClicked(int position)
            {
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PRAISE));
            }

            @Override public void OnTimeLinePraiseDownClicked(int position)
            {
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PRAISE_DOWN));
            }

            @Override public void OnTimeLineCommentsClicked(int position)
            {
            }

            @Override public void OnTimeLineShareClicked(int position)
            {
            }

            @Override public void OnTimeLineBuyClicked(int position)
            {

            }
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchTimeLine();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchTimeLineMore();
            }
        });
    }

    private void initHeadViews() {
        headerView = getActivity().getLayoutInflater().inflate(
                R.layout.discovery_timelines_head, null);
        iconHeadIV = (ImageView)headerView.findViewById(R.id.imageview_timelines_head_type);
        iconHeadIV.setBackgroundResource(R.drawable.square_novice);
        titleHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_title);
        titleHeadTV.setText(R.string.discovery_square_novice);
        totalHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_total);
        numberTimelinesHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_number_timeline);
        numberRepliesHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_number_replies);
        listTimeLine.getRefreshableView().addHeaderView(headerView);
    }

    private void enterTimeLineDetail(TimelineItemDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());

        //For Administrator
        bundle.putString(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_TIMELINE_FROM, TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_LEARNING);

        pushFragment(TimeLineItemDetailFragment.class, bundle);
    }

    @Override public void onPause()
    {
        super.onPause();
        if (listTimeLine != null)
        {
            listTimeLine.onRefreshComplete();
        }
    }

    @Override public void onDestroyView()
    {
        detachTimeLineMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    private void detachTimeLineMiddleCallback()
    {
        if (timeLineMiddleCallback != null)
        {
            timeLineMiddleCallback.setPrimaryCallback(null);
        }
        timeLineMiddleCallback = null;
    }

    public void fetchTimeLine()
    {
        detachTimeLineMiddleCallback();
        maxID = -1;
        timeLineMiddleCallback =
                timelineServiceWrapper.get().getTimelineLearning(currentUserId.toUserBaseKey(), PERPAGE, -1, maxID, new TimeLineCallback());
    }

    public void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback =
                timelineServiceWrapper.get().getTimelineLearning(currentUserId.toUserBaseKey(), PERPAGE, maxID, -1, new TimeLineCallback());
    }

    public class TimeLineCallback implements retrofit.Callback<TimelineDTO>
    {
        @Override public void success(TimelineDTO timelineDTO, Response response)
        {
            if (maxID == -1)//重新加载
            {
                adapter.setListData(timelineDTO);
                adapter.notifyDataSetChanged();
            }
            else
            {
                adapter.addItems(timelineDTO);
                adapter.notifyDataSetChanged();
            }
            onFinish();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            onFinish();
        }

        public void onFinish()
        {
            listTimeLine.onRefreshComplete();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
            progressBar.stopLoading();
        }
    }

    @Override public void onResume()
    {
        super.onResume();
        if(adapter!=null)
        {
            adapter.OnResumeDataAction();
        }
    }

}
