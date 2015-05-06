package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
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
import com.tradehero.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/*
股神动态
 */
public class DiscoveryStockGodNewsFragment extends DashboardFragment
{

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeheroprogressbar_discovery) TradeHeroProgressBar progressBar;
    private UserTimeLineAdapter adapter;
    private int maxID = -1;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;

    private int PERPAGE = 20;
    @Inject Analytics analytics;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new UserTimeLineAdapter(getActivity());
        adapter.isShowHeadAndName = true;
        adapter.isShowFollowBuy = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_stock_god_news, container, false);

        ButterKnife.inject(this, view);

        initView();

        if (adapter.getCount() == 0)
        {
            fetchTimeLine();
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.listTimeLine);
        }

        return view;
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        listTimeLine.setAdapter(adapter);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater() {
            @Override
            public void OnTimeLineItemClicked(int position) {
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                enterTimeLineDetail(dto);
                analytics.addEvent(new MethodEvent(AnalyticsConstants.BUTTON_DISCOVERY_GOD, String.valueOf(position)));
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

    public void enterTimeLineDetail(TimelineItemDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());
        gotoDashboard(TimeLineItemDetailFragment.class, bundle);
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
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineStockGodNews(currentUserId.toUserBaseKey(), PERPAGE, -1, maxID,
                new TimeLineCallback());
    }

    public void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineStockGodNews(currentUserId.toUserBaseKey(), PERPAGE, maxID, -1,
                new TimeLineCallback());
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
        }
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
        ButterKnife.reset(this);
        detachTimeLineMiddleCallback();
        super.onDestroyView();
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
