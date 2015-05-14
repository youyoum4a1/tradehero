package com.tradehero.chinabuild.fragment.userCenter;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import butterknife.ButterKnife;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.base.DashboardNavigatorActivity;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import javax.inject.Inject;
import org.jetbrains.annotations.Nullable;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by palmer on 15/2/27.
 */
public class MyMainSubPage extends Fragment {

    private SecurityListView listTimeLine;
    private TradeHeroProgressBar progressBar;

    private UserTimeLineAdapter adapter;

    private int maxID = -1;

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;

    private ImageView emptyIV;

    public final static String MY_MAIN_SUB_PAGE_TYPE = "my_main_sub_page_type";
    public final static int TYPE_TRADE_HISTORY = 1;
    public final static int TYPE_DISCUSS = 0;

    private int type = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        type = getArguments().getInt(MY_MAIN_SUB_PAGE_TYPE, 0);

        View view = inflater.inflate(R.layout.user_my_main_subpage, container, false);
        ButterKnife.inject(this, view);
        listTimeLine = (SecurityListView)view.findViewById(R.id.list_my_history);
        progressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_my_history);
        adapter = new UserTimeLineAdapter(getActivity(), true);
        adapter.isShowFollowBuy = true;
        progressBar.setVisibility(View.VISIBLE);
        progressBar.startLoading();

        emptyIV = (ImageView)view.findViewById(R.id.imgEmpty);

        initView();
        fetchTimeLine();
        return view;
    }

    @Override public void onAttach(Activity activity){
        super.onAttach(activity);
        DaggerUtils.inject(this);
    }

    @Override
    public void onDestroyView(){
        detachTimeLineMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onDestroy(){
        timeLineMiddleCallback = null;
        super.onDestroy();
    }

    private void detachTimeLineMiddleCallback(){
        if (timeLineMiddleCallback != null)
        {
            timeLineMiddleCallback.setPrimaryCallback(null);
        }
        timeLineMiddleCallback = null;
    }

    private void initView(){

        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override
            public void OnTimeLineItemClicked(int position)
            {
                enterTimeLineDetail((TimelineItemDTO) adapter.getItem(position));
            }

            @Override
            public void OnTimeLinePraiseClicked(int position){}

            @Override
            public void OnTimeLinePraiseDownClicked(int position) {}

            @Override
            public void OnTimeLineCommentsClicked(int position)
            {
            }

            @Override
            public void OnTimeLineShareClicked(int position)
            {
            }

            @Override public void OnTimeLineBuyClicked(int position){}
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchTimeLine();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                fetchTimeLineMore();
            }
        });
        listTimeLine.setAdapter(adapter);

    }

    private void enterTimeLineDetail(AbstractDiscussionCompactDTO dto)
    {
        if (dto == null) return;
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSION_ID, dto.getDiscussionKey().getArgs());
        getDashboardNavigator().pushFragment(TimeLineItemDetailFragment.class, bundle);
    }


    private void fetchTimeLine()
    {
        maxID = -1;
        if(type == TYPE_TRADE_HISTORY) {
            timeLineMiddleCallback = timelineServiceWrapper.get().getTradeHistory(currentUserId.toUserBaseKey(), 10, -1, maxID, new TimeLineCallback());
        }
        if(type == TYPE_DISCUSS){
            timeLineMiddleCallback = timelineServiceWrapper.get().getTimelines(currentUserId.toUserBaseKey(), 10, -1, maxID, new TimeLineCallback());
        }
    }

    private void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        if(type == TYPE_TRADE_HISTORY) {
            timeLineMiddleCallback = timelineServiceWrapper.get().getTradeHistory(currentUserId.toUserBaseKey(), 10, maxID, -1, new TimeLineCallback());
        }
        if(type == TYPE_DISCUSS){
            timeLineMiddleCallback = timelineServiceWrapper.get().getTimelines(currentUserId.toUserBaseKey(), 10, maxID, -1, new TimeLineCallback());
        }
    }

    public class TimeLineCallback implements retrofit.Callback<TimelineDTO>
    {
        @Override
        public void success(TimelineDTO timelineDTO, Response response)
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

        @Override
        public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            onFinish();
        }

        public void onFinish()
        {
            listTimeLine.onRefreshComplete();
            if(progressBar != null){
                progressBar.stopLoading();
                progressBar.setVisibility(View.GONE);
            }
            listTimeLine.setEmptyView(emptyIV);
        }


    }

    private DashboardNavigator getDashboardNavigator()
    {
        @Nullable DashboardNavigatorActivity activity = ((DashboardNavigatorActivity) getActivity());
        if (activity != null)
        {
            return activity.getDashboardNavigator();
        }
        return null;
    }
}
