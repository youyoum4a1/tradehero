package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.message.DiscoveryDiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.metrics.Analytics;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
悬赏帖
 */
public class DiscoveryRewardFragment extends DashboardFragment {

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    private TextView tvCreateTimeLine;
    private UserTimeLineAdapter adapter;
    private int maxID = -1;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;

    @Inject Analytics analytics;
    private int PERPAGE = 20;

    //head view
    private View headerView;
    private ImageView iconHeadIV;
    private TextView titleHeadTV;
    private TextView totalHeadTV;
    private TextView numberTimelinesHeadTV;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new UserTimeLineAdapter(getActivity(), TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD);
        adapter.isShowHeadAndName = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.discovery_reward, container, false);

        ButterKnife.inject(this, view);

        initView();

        if (adapter.getCount() == 0) {
            fetchTimeLine();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        } else {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }
        tvCreateTimeLine = (TextView) view.findViewById(R.id.tvCreateTimeLine);
        tvCreateTimeLine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createTimeLine();
            }
        });
        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.discovery_discuss_send_reward));
    }

    public void initView() {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        initHeadViews();
        listTimeLine.setAdapter(adapter);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater() {
            @Override
            public void OnTimeLineItemClicked(int position) {
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                enterTimeLineDetail(dto);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.BUTTON_DISCOVERY_REWARD, String.valueOf(position)));
            }

            @Override
            public void OnTimeLinePraiseClicked(int position) {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PRAISE));
            }

            @Override
            public void OnTimeLinePraiseDownClicked(int position) {
                analytics.addEvent(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PRAISE_DOWN));
            }

            @Override
            public void OnTimeLineCommentsClicked(int position) {
            }

            @Override
            public void OnTimeLineShareClicked(int position) {
            }

            @Override
            public void OnTimeLineBuyClicked(int position) {

            }
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchTimeLine();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                fetchTimeLineMore();
            }
        });
    }

    private void initHeadViews() {
        headerView = getActivity().getLayoutInflater().inflate(
                R.layout.discovery_timelines_head, null);
        iconHeadIV = (ImageView)headerView.findViewById(R.id.imageview_timelines_head_type);
        iconHeadIV.setBackgroundResource(R.drawable.square_reward);
        titleHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_title);
        titleHeadTV.setText(R.string.discovery_discuss_send_reward);
        totalHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_total);
        if(DiscoverySquareFragment.NUMBER_ACTIVITY_TIMELINES_REWRAD> 0){
            totalHeadTV.setText("(" + DiscoverySquareFragment.NUMBER_ACTIVITY_TIMELINES_REWRAD + ")");
        }
        numberTimelinesHeadTV = (TextView)headerView.findViewById(R.id.textview_timelines_head_number_timeline);
        if(DiscoverySquareFragment.NUMBER_TIMELINES_REWRAD > 0){
            numberTimelinesHeadTV.setText(String.valueOf(DiscoverySquareFragment.NUMBER_TIMELINES_REWRAD));
        }
        listTimeLine.getRefreshableView().addHeaderView(headerView);
    }

    private void enterTimeLineDetail(TimelineItemDTO dto) {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());

        //For Administrator
        bundle.putString(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_TIMELINE_FROM, TimeLineItemDetailFragment.BUNDLE_TIMELINE_FROM_REWARD);

        pushFragment(TimeLineItemDetailFragment.class, bundle);
    }

    private void detachTimeLineMiddleCallback() {
        if (timeLineMiddleCallback != null) {
            timeLineMiddleCallback.setPrimaryCallback(null);
        }
        timeLineMiddleCallback = null;
    }

    public void fetchTimeLine() {
        detachTimeLineMiddleCallback();
        maxID = -1;
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineReward(currentUserId.toUserBaseKey(), PERPAGE, -1, maxID, new TimeLineCallback());
    }

    public void fetchTimeLineMore() {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineReward(currentUserId.toUserBaseKey(), PERPAGE, maxID, -1, new TimeLineCallback());
    }

    public class TimeLineCallback implements Callback<TimelineDTO> {
        @Override
        public void success(TimelineDTO timelineDTO, Response response) {
            if (maxID == -1)//重新加载
            {
                adapter.setListData(timelineDTO);
                adapter.notifyDataSetChanged();
            } else {
                adapter.addItems(timelineDTO);
                adapter.notifyDataSetChanged();
            }
            onFinish();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THToast.show(new THException(retrofitError));
            onFinish();
        }

        public void onFinish() {
            listTimeLine.onRefreshComplete();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (listTimeLine != null) {
            listTimeLine.onRefreshComplete();
        }
    }

    @Override
    public void onDestroyView() {
        ButterKnife.reset(this);
        detachTimeLineMiddleCallback();
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (adapter != null) {
            adapter.OnResumeDataAction();
        }
    }

    private void createTimeLine() {
        Bundle bundle = new Bundle();
        bundle.putBoolean(DiscussSendFragment.BUNDLE_KEY_REWARD, true);
        bundle.putBoolean(DiscussSendFragment.BUNDLE_KEY_IS_GO_REWARD, true);
        pushFragment(DiscoveryDiscussSendFragment.class, bundle);
    }
}
