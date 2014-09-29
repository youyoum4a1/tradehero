package com.tradehero.th.fragments.chinabuild.fragment.discovery;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.th.fragments.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

/*
最新动态
 */
public class DiscoveryRecentNewsFragment extends DashboardFragment
{

    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;
    private int maxID = -1;

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    private UserTimeLineAdapter adapter;

    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new UserTimeLineAdapter(getActivity());
        adapter.isShowHeadAndName = true;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_recent_news, container, false);
        ButterKnife.inject(this, view);
        fetchTimeLine();
        initView();
        return view;
    }

    public void initView()
    {
        listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        listTimeLine.setAdapter(adapter);

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override public void OnTimeLineItemClicked(int position)
            {
                Timber.d("Item position = " + position);
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                enterTimeLineDetail(dto);
            }

            @Override public void OnTimeLinePraiseClicked(int position)
            {
                Timber.d("Praise position = " + position);
            }

            @Override public void OnTimeLineCommentsClicked(int position)
            {
                Timber.d("Comments position = " + position);
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                comments(dto);
            }

            @Override public void OnTimeLineShareClied(int position)
            {
                Timber.d("Share position = " + position);
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                share(dto.text);
            }
        });

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                fetchTimeLine();
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                fetchTimeLineMore();
            }
        });
    }

    public void enterTimeLineDetail(TimelineItemDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSTION_ID, dto.getDiscussionKey().getArgs());
        gotoDashboard(TimeLineItemDetailFragment.class, bundle);
    }

    public void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        gotoDashboard(DiscussSendFragment.class, bundle);
    }

    public void share(String strShare)
    {

        mShareSheetTitleCache.set(strShare);

        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override public void onShareRequestedClicked()
                    {

                    }
                });

        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachTimeLineMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @Override public void onResume()
    {
        super.onResume();
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
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineSquare(currentUserId.toUserBaseKey(), 10, -1, maxID, new TimeLineCallback());
    }

    public void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineSquare(currentUserId.toUserBaseKey(), 10, maxID, -1, new TimeLineCallback());
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
            listTimeLine.onRefreshComplete();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            THToast.show(new THException(retrofitError));
            listTimeLine.onRefreshComplete();
        }
    }
}
