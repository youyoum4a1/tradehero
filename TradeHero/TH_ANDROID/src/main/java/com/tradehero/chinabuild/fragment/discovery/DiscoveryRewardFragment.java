package com.tradehero.chinabuild.fragment.discovery;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.th.R;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.timeline.TimelineDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.UserTimelineServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.utils.metrics.Analytics;
import com.tradehero.th.utils.metrics.AnalyticsConstants;
import com.tradehero.th.utils.metrics.events.MethodEvent;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/*
悬赏帖
 */
public class DiscoveryRewardFragment extends DashboardFragment
{
    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.tradeheroprogressbar_discovery)TradeHeroProgressBar progressBar;
    private UserTimeLineAdapter adapter;
    private int maxID = -1;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserTimelineServiceWrapper> timelineServiceWrapper;
    private MiddleCallback<TimelineDTO> timeLineMiddleCallback;

    @Inject Analytics analytics;
    private int PERPAGE = 20;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        adapter = new UserTimeLineAdapter(getActivity());
        adapter.isShowHeadAndName = true;
        adapter.isShowLastCommentUtc = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.discovery_reward, container, false);

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

        adapter.setTimeLineOperater(new UserTimeLineAdapter.TimeLineOperater()
        {
            @Override public void OnTimeLineItemClicked(int position)
            {
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                enterTimeLineDetail(dto);
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.BUTTON_DISCOVERY_HOT, String.valueOf(position)));
            }

            @Override public void OnTimeLinePraiseClicked(int position)
            {
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_PRAISE));
            }

            @Override public void OnTimeLineCommentsClicked(int position)
            {
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_COMMENT));
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                comments(dto);
            }

            @Override public void OnTimeLineShareClicked(int position)
            {
                analytics.addEventAuto(new MethodEvent(AnalyticsConstants.CHINA_BUILD_BUTTON_CLICKED, AnalyticsConstants.DISCOVERY_ITEM_SHARE));
                TimelineItemDTO dto = (TimelineItemDTO) adapter.getItem(position);
                shareToWechatMoment(dto.text);
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

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare)
    {
        String show = getUnParsedText(strShare);
        if (TextUtils.isEmpty(show))
        {
            return;
        }
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                String outputStr = show;
                String downloadCNTradeHeroWeibo = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new RequestCallback());
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = show;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());
    }

    private class RequestCallback implements Callback
    {

        @Override
        public void success(Object o, Response response)
        {

        }

        @Override
        public void failure(RetrofitError retrofitError)
        {

        }
    }

    public void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        gotoDashboard(DiscussSendFragment.class, bundle);
    }

    public void enterTimeLineDetail(TimelineItemDTO dto)
    {
        Bundle bundle = new Bundle();
        bundle.putBundle(TimeLineItemDetailFragment.BUNDLE_ARGUMENT_DISCUSSTION_ID, dto.getDiscussionKey().getArgs());
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
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineHotTopic(currentUserId.toUserBaseKey(), PERPAGE, -1, maxID, new TimeLineCallback());
    }

    public void fetchTimeLineMore()
    {
        detachTimeLineMiddleCallback();
        maxID = adapter.getMaxID();
        timeLineMiddleCallback = timelineServiceWrapper.get().getTimelineHotTopic(currentUserId.toUserBaseKey(), PERPAGE, maxID, -1, new TimeLineCallback());
    }

    public class TimeLineCallback implements Callback<TimelineDTO>
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
        if(listTimeLine!=null)
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
}
