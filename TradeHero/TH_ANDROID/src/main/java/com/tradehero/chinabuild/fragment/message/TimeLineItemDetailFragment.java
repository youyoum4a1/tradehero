package com.tradehero.chinabuild.fragment.message;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.tradehero.chinabuild.data.DiscussReportDTO;
import com.tradehero.chinabuild.data.ManageEssentialDTO;
import com.tradehero.chinabuild.data.ManageLearningDTO;
import com.tradehero.chinabuild.data.ManageProductionDTO;
import com.tradehero.chinabuild.data.ManageTopDTO;
import com.tradehero.chinabuild.dialog.DialogFactory;
import com.tradehero.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineCommentDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineDetailDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineReportDialogLayout;
import com.tradehero.chinabuild.fragment.security.SecurityDetailSubCache;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.chinabuild.utils.UniversalImageLoader;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THLog;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimeLineBaseAdapter;
import com.tradehero.th.adapters.TimeLineDetailDiscussSecItem;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionKeyList;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionKeyFactory;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.AdministratorManageTimelineServiceWrapper;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.Constants;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.InputTools;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;

import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import dagger.Lazy;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class TimeLineItemDetailFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener, View.OnClickListener {

    public static final String BUNDLE_ARGUMENT_DISCUSSION_ID = "BUNDLE_ARGUMENT_DISCUSSION_ID";

    //For Administrator
    public static final String BUNDLE_ARGUMENT_TIMELINE_FROM = "BUNDLE_ARGUMENT_TIMELINE_FROM";
    public static final String BUNDLE_TIMELINE_FROM_LEARNING = "BUNDLE_TIMELINE_FROM_LEARNING";
    public static final String BUNDLE_TIMELINE_FROM_RECENT = "BUNDLE_TIMELINE_FROM_RECENT";
    public static final String BUNDLE_TIMELINE_FROM_FAVORITE = "BUNDLE_TIMELINE_FROM_FAVORITE";
    public static final String BUNDLE_TIMELINE_FROM_REWARD = "BUNDLE_TIMELINE_FROM_REWARD";
    private String timelineFrom = "";


    @Inject protected DiscussionCache discussionCache;
    private DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> discussionFetchListener;
    DiscussionKey timelineItemDTOKey;
    private PaginatedDiscussionListKey discussionListKey;
    @Inject DiscussionListCacheNew discussionListCache;

    @InjectView(R.id.btnSend) Button btnSend;
    @InjectView(R.id.edtSend) EditText edtSend;

    private TimeLineDetailDiscussSecItem adapter;
    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    @Inject DiscussionKeyFactory discussionKeyFactory;

    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;

    AbstractDiscussionCompactDTO dataDto;

    @Inject public Lazy<PrettyTime> prettyTime;
    private MiddleCallback<DiscussionDTO> voteCallback;
    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @InjectView(R.id.tradeheroprogressbar_timeline_detail) TradeHeroProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.rlAllView) RelativeLayout rlAllView;

    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    @Inject Lazy<AdministratorManageTimelineServiceWrapper> administratorManageTimelineServiceWrapper;

    protected LinearLayout llDisscurssOrNews;
    protected ImageView imgSecurityTLUserHeader;
    protected TextView tvUserTLTimeStamp;
    protected TextView tvUserTLContent;
    protected TextView tvUserTLName;
    private LinearLayout llTLPraise;
    private LinearLayout llTLPraiseDown;
    private LinearLayout llTLComment;
    private TextView tvTLPraise;
    private TextView tvTLPraiseDown;
    private TextView tvTLComment;
    protected TextView btnTLPraise;
    private TextView btnTLPraiseDown;

    protected LinearLayout mRefreshView;
    protected TextView tvIsReward;
    protected TextView tvIsEssential;
    protected TextView tvUserTVTitle;

    private String strReply = "";

    public static final int ITEMS_PER_PAGE = 50;

    private Dialog timeLineDetailMenuDialog;
    private Dialog timeLineCommentMenuDialog;
    private Dialog timeLineReportMenuDialog;
    private DialogFactory dialogFactory;

    //Delete TimeLine confirm dialog or apply comment dialog
    private Dialog deleteOrApplyTimeLineConfirmDialog;
    private TextView deleteOrApplyTLConfirmDlgTitleTV;
    private TextView deleteOrApplyTLConfirmDlgTitle2TV;
    private TextView deleteOrApplyTLConfirmDlgOKTV;
    private TextView deleteOrApplyTLConfirmDlgCancelTV;
    public static final int DIALOG_TYPE_DELETE_TIMELINE = 1;
    public static final int DIALOG_TYPE_DELETE_COMMENT = 2;
    public static final int DIALOG_TYPE_APPLY_COMMENT = 3;


    //Timeline Operater
    private LinearLayout timelineOperaterLL;
    private LinearLayout timeline_detail_llTLPraise;
    private TextView timeline_detail_btnTLPraise;
    private TextView timeline_detail_tvTLPraise;
    private LinearLayout timeline_detail_llTLPraiseDown;
    private TextView timeline_detail_btnTLPraiseDown;
    private TextView timeline_detail_tvTLPraiseDown;
    private LinearLayout timeline_detail_llTLComment;
    private TextView timeline_detail_tvTLComment;

    //If it is a time line, time line api is used to delete it. If it is a discussion, discussion api is for deleting.
    //Default, it is a time line.
    public static final String BUNDLE_ARGUMENT_DISCUSSION_TYPE = "bundle_argument_discuss_type";
    public static final int DISCUSSION_TIME_LINE_TYPE = 1;
    public static final int DISCUSSION_DISCUSSION_TYPE = 2;
    public static final String BUNDLE_ARGUMENT_IS_NEWS = "bundle_argument_is_news";
    private int discussion_type = 1;
    private boolean isNews = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        discussionFetchListener = createDiscussionCacheListener();
        initArgument();
        adapter = new TimeLineDetailDiscussSecItem(getActivity());
    }

    public void initArgument() {
        Bundle bundle = getArguments();
        if (bundle.containsKey(BUNDLE_ARGUMENT_DISCUSSION_ID)) {
            timelineItemDTOKey = discussionKeyFactory.fromBundle(bundle.getBundle(BUNDLE_ARGUMENT_DISCUSSION_ID));
            fetchDiscussion(timelineItemDTOKey, true);
            discussionListKey = new PaginatedDiscussionListKey(timelineItemDTOKey.getType(), timelineItemDTOKey.id, 1, ITEMS_PER_PAGE);
            fetchDiscussList(true);
        }
        //For Administrator
        if (bundle.containsKey(BUNDLE_ARGUMENT_TIMELINE_FROM)) {
            timelineFrom = bundle.getString(BUNDLE_ARGUMENT_TIMELINE_FROM);
        }

        //If it is a time line, time line api is used to delete it. If it is a discussion, discussion api is for deleting.
        //Default, it is a time line.
        if (bundle.containsKey(BUNDLE_ARGUMENT_DISCUSSION_TYPE)) {
            discussion_type = bundle.getInt(BUNDLE_ARGUMENT_DISCUSSION_TYPE, DISCUSSION_TIME_LINE_TYPE);
        }
        if (bundle.containsKey(BUNDLE_ARGUMENT_IS_NEWS)){
            isNews = bundle.getBoolean(BUNDLE_ARGUMENT_IS_NEWS, false);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("详情");
        if (isNews) {
            setHeadViewRight0(getString(R.string.discovery_discuss_send_share));
        } else {
            setHeadViewRight0(getString(R.string.discovery_discuss_send_more));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.timeline_item_detail, container, false);
        ButterKnife.inject(this, view);
        setNeedToMonitorBackPressed(true);
        ListView lv = listTimeLine.getRefreshableView();
        mRefreshView = getHeaderView(inflater);
        lv.addHeaderView(mRefreshView);
        initRoot(mRefreshView);

        initView();

        if (dataDto == null) {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_timeline_detail);
            progressBar.startLoading();
        } else {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
        }
        initRefreshView();

        initTimelineOperaterView(view);
        listTimeLine.getRefreshableView().setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
            }

            @Override
            public void onScroll(AbsListView absListView, int i, int i2, int i3) {
                if (timelineOperaterLL == null) {
                    return;
                }
                int currentPosition = absListView.getFirstVisiblePosition();
                if (currentPosition > 1) {
                    setTimelineOperaterLLVisibility(View.VISIBLE);
                } else {
                    setTimelineOperaterLLVisibility(View.GONE);
                }
            }
        });
        return view;
    }

    protected void setTimelineOperaterLLVisibility(int visible) {
        if (timelineOperaterLL == null) {
            return;
        }
        timelineOperaterLL.setVisibility(visible);
    }

    public LinearLayout getHeaderView(LayoutInflater inflater) {
        return (LinearLayout) inflater.inflate(R.layout.security_time_line_item_plus, null);
    }

    public void initRoot(View view) {
        llDisscurssOrNews = (LinearLayout) view.findViewById(R.id.llItemAll);
        imgSecurityTLUserHeader = (ImageView) view.findViewById(R.id.imgSecurityTLUserHeader);
        tvUserTLTimeStamp = (TextView) view.findViewById(R.id.tvUserTLTimeStamp);
        tvUserTLContent = (TextView) view.findViewById(R.id.tvUserTLContent);
        tvUserTLName = (TextView) view.findViewById(R.id.tvUserTLName);
        llTLPraise = (LinearLayout) view.findViewById(R.id.llTLPraise);
        llTLPraiseDown = (LinearLayout) view.findViewById(R.id.llTLPraiseDown);
        llTLComment = (LinearLayout) view.findViewById(R.id.llTLComment);
        tvTLPraise = (TextView) view.findViewById(R.id.tvTLPraise);
        tvTLPraiseDown = (TextView) view.findViewById(R.id.tvTLPraiseDown);
        tvTLComment = (TextView) view.findViewById(R.id.tvTLComment);
        btnTLPraise = (TextView) view.findViewById(R.id.btnTLViewCount);
        btnTLPraiseDown = (TextView) view.findViewById(R.id.btnTLPraise);
        tvUserTLName.setOnClickListener(this);
        imgSecurityTLUserHeader.setOnClickListener(this);
        llTLPraise.setOnClickListener(this);
        llTLPraiseDown.setOnClickListener(this);
        llTLComment.setOnClickListener(this);

        llDisscurssOrNews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setHintForSender(-1);
            }
        });
    }

    private void initView() {
        tvUserTLContent.setMaxLines(1000);
        listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listTimeLine.setAdapter(adapter);

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                discussionListKey.setPage(1);
                fetchDiscussList(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                discussionListKey = discussionListKey.next();
                fetchDiscussList(true);
            }
        });

        adapter.setListener(new TimeLineBaseAdapter.TimeLineOperater() {
            @Override
            public void OnTimeLineItemClicked(int position) {
                onCommentClick(position);
            }

            @Override
            public void OnTimeLinePraiseClicked(int position) {
            }

            @Override
            public void OnTimeLinePraiseDownClicked(int position) {
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
    }

    private void initRefreshView() {
        if (mRefreshView == null) {
            return;
        }
        tvIsReward = (TextView) mRefreshView.findViewById(R.id.tvIsReward);
        tvIsEssential = (TextView) mRefreshView.findViewById(R.id.tvIsEssential);
        tvUserTVTitle = (TextView) mRefreshView.findViewById(R.id.tvUserTVTitle);
        if (tvUserTVTitle != null) {
            if (!TextUtils.isEmpty(getHeader())) {
                tvUserTVTitle.setVisibility(View.VISIBLE);
                tvUserTVTitle.setText(getHeader());
            } else {
                tvUserTVTitle.setVisibility(View.GONE);
            }
        }

        if (tvIsReward != null && isRewardTimeLine() && !TextUtils.isEmpty(getRewardCount())) {
            tvIsReward.setVisibility(View.VISIBLE);
            tvIsReward.setText(getRewardCount());
        }

        if(tvIsEssential != null && isEssentialTimeLine()){
            tvIsEssential.setVisibility(View.VISIBLE);
        }
    }

    private void setRefreshViewRewarded() {
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO) dataDto;
            if (timelineItemDTO.isQuestionItem) {
                timelineItemDTO.isAnswered = true;
                tvIsReward.setVisibility(View.VISIBLE);
                tvIsReward.setText(getRewardCount());
            }
        }
    }

    private void initTimelineOperaterView(View parentView) {
        timelineOperaterLL = (LinearLayout) parentView.findViewById(R.id.linearlayout_timeline_detail_operater);
        timeline_detail_llTLPraise = (LinearLayout) parentView.findViewById(R.id.timeline_detail_llTLPraise);
        timeline_detail_btnTLPraise = (TextView) parentView.findViewById(R.id.timeline_detail_btnTLPraise);
        timeline_detail_tvTLPraise = (TextView) parentView.findViewById(R.id.timeline_detail_tvTLPraise);
        timeline_detail_llTLPraiseDown = (LinearLayout) parentView.findViewById(R.id.timeline_detail_llTLPraiseDown);
        timeline_detail_btnTLPraiseDown = (TextView) parentView.findViewById(R.id.timeline_detail_btnTLPraiseDown);
        timeline_detail_tvTLPraiseDown = (TextView) parentView.findViewById(R.id.timeline_detail_tvTLPraiseDown);
        timeline_detail_llTLComment = (LinearLayout) parentView.findViewById(R.id.timeline_detail_llTLComment);
        timeline_detail_tvTLComment = (TextView) parentView.findViewById(R.id.timeline_detail_tvTLComment);

        timeline_detail_llTLPraise.setOnClickListener(this);
        timeline_detail_llTLPraiseDown.setOnClickListener(this);
        timeline_detail_llTLComment.setOnClickListener(this);
    }

    public void setDefaultReply() {
        edtSend.setHint(getResources().getString(R.string.please_to_reply));
        strReply = "";
        isReplayFollower = false;
    }

    boolean isReplayFollower = false;

    public void setHintForSender(long position) {
        if (position == -1)//回复主题
        {
            setDefaultReply();
        } else//回复楼层
        {
            AbstractDiscussionCompactDTO dto = adapter.getItem((int) position);
            if (dto == null) {
                return;
            }
            if (dto instanceof DiscussionDTO) {
                String displayName = ((DiscussionDTO) dto).user.getDisplayName();
                int id = ((DiscussionDTO) dto).userId;
                String strHint = "回复 " + displayName + ":";
                if (edtSend != null) {
                    edtSend.setHint(strHint);
                    strReply = "<@@" + displayName + "," + id + "@>";
                    isReplayFollower = true;
                }
            }
        }
        openInputMethod();
    }

    public void openInputMethod() {
        InputTools.KeyBoard(edtSend, "open");
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isReplayFollower) {
            strReply = "";
            edtSend.setText("");
            edtSend.setHint(getResources().getString(R.string.please_to_reply));
            isReplayFollower = false;
        } else {
            popCurrentFragment();
        }
    }

    @Override
    public void onDestroyView() {
        detachDiscussionFetchTask();
        detachDiscussionFetch();
        unsetDiscussionEditMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
    }

    @Override
    public void onResume() {
        super.onResume();
        checkAndDealDeletedTimeLine();
        fetchDiscussion(timelineItemDTOKey, true);
    }

    private void fetchDiscussion(DiscussionKey discussionKey, boolean force) {
        detachDiscussionFetchTask();
        discussionCache.register(discussionKey, discussionFetchListener);
        discussionCache.getOrFetchAsync(discussionKey, force);
    }

    private void detachDiscussionFetchTask() {
        discussionCache.unregister(discussionFetchListener);
    }

    private void detachDiscussionFetch() {
        discussionListCache.unregister(this);
    }

    public void fetchDiscussList(boolean force) {
        if (discussionListKey != null) {
            detachDiscussionFetch();
            discussionListCache.register(discussionListKey, this);
            discussionListCache.getOrFetchAsync(discussionListKey, force);
        }
    }

    protected DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> createDiscussionCacheListener() {
        return new PrivateDiscussionViewDiscussionCacheListener();
    }

    @Override
    public void onClick(View view) {
        onOperaterClicked(view);
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> {
        @Override
        public void onDTOReceived(@NotNull DiscussionKey key, @NotNull AbstractDiscussionCompactDTO value) {
            linkWithDTO(value);
            OnFinish();
        }

        @Override
        public void onErrorThrown(@NotNull DiscussionKey key, @NotNull Throwable error) {
            OnFinish();
        }

        public void OnFinish() {
            if (betterViewAnimator != null) {
                betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
            }
            if (progressBar != null) {
                progressBar.stopLoading();
            }
        }
    }

    public void linkWithDTO(AbstractDiscussionCompactDTO value) {
        if (value != null) {
            this.dataDto = value;
            checkAndDealDeletedTimeLine();
            initRefreshView();
            fetchDiscussList(true);
            displayDiscussOrNewsDTO();
        }
    }

    public AbstractDiscussionCompactDTO getAbstractDiscussionCompactDTO() {
        return dataDto;
    }

    public void displayDiscussOrNewsDTO() {
        AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
        llDisscurssOrNews.setVisibility(dto == null ? View.INVISIBLE : View.VISIBLE);
        if (dto != null) {
            imgSecurityTLUserHeader.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);
            tvUserTLName.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);

            if (dto instanceof NewsItemDTO) {
                tvUserTLContent.setText(((NewsItemDTO) dto).text);
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((NewsItemCompactDTO) dto).createdAtUtc));
            } else if (dto instanceof DiscussionDTO) {
                tvUserTLName.setText(((DiscussionDTO) dto).user.getDisplayName());
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((DiscussionDTO) dto).createdAtUtc));
                tvUserTLContent.setText(((DiscussionDTO) dto).text);
                ImageLoader.getInstance()
                        .displayImage(((DiscussionDTO) dto).user.picture,
                                imgSecurityTLUserHeader,
                                UniversalImageLoader.getAvatarImageLoaderOptions(false));
            } else if (dto instanceof TimelineItemDTO) {
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((TimelineItemDTO) dto).createdAtUtc));
                tvUserTLName.setText(((TimelineItemDTO) dto).user.getDisplayName());
                tvUserTLContent.setText(((TimelineItemDTO) dto).text);
                ImageLoader.getInstance()
                        .displayImage(((TimelineItemDTO) dto).user.picture,
                                imgSecurityTLUserHeader,
                                UniversalImageLoader.getAvatarImageLoaderOptions(false));
            }

            btnTLPraise.setBackgroundResource(dto.voteDirection == 1 ? R.drawable.icon_praise_active : R.drawable.icon_praise_normal);
            btnTLPraiseDown.setBackgroundResource(dto.voteDirection == -1 ? R.drawable.icon_praise_down_active : R.drawable.icon_praise_down_normal);
            tvTLComment.setText("" + dto.commentCount);
            tvTLPraise.setText(Html.fromHtml(dto.getVoteUpString()));
            tvTLPraiseDown.setText(Html.fromHtml(dto.getVoteDownString()));


            timeline_detail_btnTLPraise.setBackgroundResource(dto.voteDirection == 1 ? R.drawable.icon_praise_active : R.drawable.icon_praise_normal);
            timeline_detail_btnTLPraiseDown.setBackgroundResource(dto.voteDirection == -1 ? R.drawable.icon_praise_down_active : R.drawable.icon_praise_down_normal);
            timeline_detail_tvTLComment.setText("" + dto.commentCount);
            timeline_detail_tvTLPraise.setText(Html.fromHtml(dto.getVoteUpString()));
            timeline_detail_tvTLPraiseDown.setText(Html.fromHtml(dto.getVoteDownString()));
        }
    }

    @Override
    public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value) {
        List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
        for (int i = 0; i < value.size(); i++) {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
            listData.add(dto);
        }
        if (discussionListKey.getPage() == 1) {
            adapter.setListData(listData);
        } else {
            adapter.addListData(listData);
        }
        if (listTimeLine == null) {
            return;
        }
        listTimeLine.onRefreshComplete();
        if (adapter.getCount() >= ITEMS_PER_PAGE) {
            listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        } else {
            listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    @Override
    public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error) {
        listTimeLine.onRefreshComplete();
    }

    @OnClick(R.id.btnSend)
    public void OnSendClicked() {
        postDiscussion();
    }

    private void unsetDiscussionEditMiddleCallback() {
        if (discussionEditMiddleCallback != null) {
            discussionEditMiddleCallback.setPrimaryCallback(null);
        }
        discussionEditMiddleCallback = null;
    }

    protected void postDiscussion() {
        if (validate()) {
            DiscussionFormDTO discussionFormDTO = buildDiscussionFormDTO();
            if (discussionFormDTO == null) return;
            unsetDiscussionEditMiddleCallback();
            progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.get().createDiscussion(discussionFormDTO, new SecurityDiscussionEditCallback());
        }
    }

    protected DiscussionFormDTO buildDiscussionFormDTO() {
        DiscussionType discussionType = getDiscussionType();
        if (discussionType != null) {
            DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
            if (timelineItemDTOKey != null) {
                discussionFormDTO.inReplyToId = timelineItemDTOKey.id;
            }
            discussionFormDTO.text = strReply + " " + edtSend.getText().toString();

            return discussionFormDTO;
        }

        return null;
    }

    protected DiscussionType getDiscussionType() {
        if (timelineItemDTOKey != null) {
            return timelineItemDTOKey.getType();
        }
        return null;
    }

    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO> {
        @Override
        public void success(DiscussionDTO discussionDTO, Response response) {
            onFinish();
            DeviceUtil.dismissKeyboard(getActivity());
            discussionListKey.setPage(1);
            fetchDiscussList(true);
            fetchDiscussion(timelineItemDTOKey, true);
            strReply = "";
            edtSend.setText("");
        }

        @Override
        public void failure(RetrofitError error) {
            onFinish();
            THException thException = new THException(error);
            THToast.show(thException);
        }

        private void onFinish() {
            dismissProgressDlg();
        }
    }

    private boolean validate() {
        boolean notEmptyText = validateNotEmptyText();
        if (!notEmptyText) {
            THToast.show(R.string.error_discussion_empty_post);
        }
        return notEmptyText;
    }

    private boolean validateNotEmptyText() {
        return !edtSend.getText().toString().trim().isEmpty();
    }

    public void onOperaterClicked(View view) {
        if (view.getId() == R.id.imgSecurityTLUserHeader || view.getId() == R.id.tvUserTLName) {
            //
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            if (dto instanceof DiscussionDTO) {
                openUserProfile(((DiscussionDTO) getAbstractDiscussionCompactDTO()).user.id);
            } else if (dto instanceof TimelineItemDTO) {
                openUserProfile(((TimelineItemDTO) getAbstractDiscussionCompactDTO()).user.id);
            }
        } else if (view.getId() == R.id.llTLPraise || view.getId() == R.id.timeline_detail_llTLPraise) {
            clickedPraise();
        } else if (view.getId() == R.id.llTLPraiseDown || view.getId() == R.id.timeline_detail_llTLPraiseDown) {
            clickedPraiseDown();
        } else if (view.getId() == R.id.llTLComment || view.getId() == R.id.timeline_detail_llTLComment) {
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            comments(dto);
        }
    }

    public void comments(AbstractDiscussionCompactDTO dto) {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        pushFragment(DiscussSendFragment.class, bundle);
    }

    public void clickedPraise() {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();
        if(item==null){
            return;
        }
        if (item.voteDirection == 1) {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        } else if (item.voteDirection == 0) {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            updateVoting(VoteDirection.UpVote, item);
        } else if (item.voteDirection == -1) {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UpVote, item);
        }

        displayDiscussOrNewsDTO();
        if (item.voteDirection != 0) {
            if (timelineOperaterLL != null && timelineOperaterLL.getVisibility() == View.VISIBLE) {
                timeline_detail_btnTLPraise.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_praise));
            } else {
                btnTLPraise.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_praise));
            }
        }
    }

    public void clickedPraiseDown() {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();
        if(item==null){
            return;
        }
        if (item.voteDirection == 1) {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.DownVote, item);
        } else if (item.voteDirection == 0) {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            updateVoting(VoteDirection.DownVote, item);

        } else if (item.voteDirection == -1) {
            item.voteDirection = 0;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        displayDiscussOrNewsDTO();
        if (item.voteDirection != 0) {
            if (timelineOperaterLL != null && timelineOperaterLL.getVisibility() == View.VISIBLE) {
                timeline_detail_btnTLPraiseDown.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_ani));
            } else {
                btnTLPraiseDown.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_ani));
            }
        }
    }

    public void share(String strShare) {
        mShareSheetTitleCache.set(strShare);
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener() {
                    @Override
                    public void onShareRequestedClicked() {

                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare) {
        if (TextUtils.isEmpty(strShare)) {
            return;
        }
        String show = getUnParsedText(strShare);
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null) {
            if (updatedUserProfileDTO.wbLinked) {
                String downloadCNTradeHeroWeibo = getString(R.string.download_tradehero_android_app_on_weibo);
                String outputStr = show;
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new Callback<Response>() {
                            @Override
                            public void success(Response response, Response response2) {

                            }

                            @Override
                            public void failure(RetrofitError retrofitError) {

                            }
                        });
            }
        }
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = 0;
        weChatDTO.type = WeChatMessageType.ShareSellToTimeline;
        weChatDTO.title = strShare;
        ((SocialSharerImpl) socialSharerLazy.get()).share(weChatDTO, getActivity());
    }

    private void openUserProfile(int userId) {
        if (userId >= 0) {
            Bundle bundle = new Bundle();
            bundle.putInt(UserMainPage.BUNDLE_USER_BASE_KEY, userId);
            pushFragment(UserMainPage.class, bundle);
        }
    }

    private void updateVoting(VoteDirection voteDirection, AbstractDiscussionCompactDTO discussionDTO) {
        if (discussionDTO == null) {
            return;
        }
        DiscussionType discussionType = getDiscussionType();

        DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                discussionType,
                discussionDTO.id,
                voteDirection);
        detachVoteMiddleCallback();
        voteCallback = discussionServiceWrapper.get().vote(discussionVoteKey, new VoteCallback(voteDirection));
    }

    protected void detachVoteMiddleCallback() {
        if (voteCallback != null) {
            voteCallback.setPrimaryCallback(null);
        }
        voteCallback = null;
    }

    protected class VoteCallback implements retrofit.Callback<DiscussionDTO> {
        public VoteCallback(VoteDirection voteDirection) {
        }

        @Override
        public void success(DiscussionDTO discussionDTO, Response response) {
        }

        @Override
        public void failure(RetrofitError error) {
        }
    }

    @Override
    public void onClickHeadRight0() {
        if (getActivity() == null) {
            return;
        }
        if(isNews){
            share();
            return;
        }


        if (dialogFactory == null) {
            dialogFactory = new DialogFactory();
        }

        boolean isDeleteAllowed = isDeleteAllowed(dataDto);

        boolean isReportAllowed = isReportAllowed(dataDto);
        timeLineDetailMenuDialog =
                dialogFactory.createTimeLineDetailDialog(getActivity(), new TimeLineDetailDialogLayout.TimeLineDetailMenuClickListener() {
                    @Override
                    public void onReportClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (getActivity() == null) {
                            return;
                        }
                        timeLineReportMenuDialog = dialogFactory.createTimeLineReportDialog(getActivity(),
                                new TimeLineReportDialogLayout.TimeLineReportMenuClickListener() {
                                    @Override
                                    public void onItemClickListener(int position) {
                                        timeLineReportMenuDialog.dismiss();
                                        sendReport(dataDto, position);
                                    }
                                });
                    }

                    @Override
                    public void onDeleteClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (dataDto != null) {
                            showDeleteTimeLineConfirmDlg(dataDto.id, DIALOG_TYPE_DELETE_TIMELINE);
                        }
                    }

                    @Override
                    public void onShareClick() {
                        share();
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onFavoriteClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }

                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            ManageEssentialDTO dto = new ManageEssentialDTO();
                            if (isFavorite()) {
                                dto.isEssential = false;
                            } else {
                                dto.isEssential = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationEssential(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onProductionClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            ManageProductionDTO dto = new ManageProductionDTO();
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            if (isProduction()) {
                                dto.isNotice = false;
                            } else {
                                dto.isNotice = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationProduction(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onTopClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if(timelineFrom.equals("")){
                            THToast.show("Please not...");
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            int originalStickType = ((TimelineItemDTO) dataDto).stickType;
                            ManageTopDTO dto = new ManageTopDTO();
                            if (isTop()) {
                                dto.stickType = UserTimeLineAdapter.toZero(originalStickType, timelineFrom);
                            }else{
                                dto.stickType = UserTimeLineAdapter.toOne(originalStickType, timelineFrom);
                            }
                            administratorManageTimelineServiceWrapper.get().operationTop(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onLearningClick() {
                        if (dataDto == null || currentUserId == null) {
                            return;
                        }
                        if (dataDto instanceof TimelineItemDTO) {
                            int timeLineId = ((TimelineItemDTO) dataDto).id;
                            ManageLearningDTO dto =new ManageLearningDTO();
                            if (isLearning()) {
                                dto.isGuide = false;
                            }else{
                                dto.isGuide = true;
                            }
                            administratorManageTimelineServiceWrapper.get().operationLearning(currentUserId.toUserBaseKey().key, timeLineId, dto, new ManagerOperateCallback());
                        }
                        timeLineDetailMenuDialog.dismiss();
                    }

                    @Override
                    public void onDeleteTimeLineClick() {
                        timeLineDetailMenuDialog.dismiss();
                        if (dataDto != null) {
                            showDeleteTimeLineConfirmDlg(dataDto.id, DIALOG_TYPE_DELETE_TIMELINE);
                        }
                    }
                }, isDeleteAllowed, isReportAllowed, isManager(), isTop(), isProduction(), isFavorite(), isLearning());
    }

    public void onCommentClick(final int position) {
        if (dialogFactory == null) {
            dialogFactory = new DialogFactory();
        }
        if (getActivity() == null) {
            return;
        }
        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
        boolean isApplyAllowed = isApplyAllowed(dto);
        boolean isDeleteAllowed = isDeleteAllowed(dto);
        boolean isReportAllowed = !isDeleteAllowed;

        timeLineCommentMenuDialog = dialogFactory.createTimeLineCommentDialog(getActivity(),
                new TimeLineCommentDialogLayout.TimeLineCommentMenuClickListener() {
                    @Override
                    public void onCommentClick() {
                        setHintForSender(position);
                        timeLineCommentMenuDialog.dismiss();
                    }

                    @Override
                    public void onReportClick() {
                        timeLineCommentMenuDialog.dismiss();
                        if (getActivity() == null) {
                            return;
                        }
                        timeLineReportMenuDialog = dialogFactory.createTimeLineReportDialog(getActivity(),
                                new TimeLineReportDialogLayout.TimeLineReportMenuClickListener() {
                                    @Override
                                    public void onItemClickListener(int position_report) {
                                        timeLineReportMenuDialog.dismiss();
                                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                                        sendReport(dto, position_report);
                                    }
                                });
                    }

                    @Override
                    public void onDeleteClick() {
                        timeLineCommentMenuDialog.dismiss();
                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                        if (dto != null) {
                            showDeleteTimeLineConfirmDlg(dto.id, DIALOG_TYPE_DELETE_COMMENT);
                        }
                    }

                    @Override
                    public void onApplyClick() {
                        timeLineCommentMenuDialog.dismiss();
                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                        if (dto != null) {
                            showDeleteTimeLineConfirmDlg(dto.id, DIALOG_TYPE_APPLY_COMMENT);
                        }
                    }
                }, isApplyAllowed, isDeleteAllowed, isReportAllowed);
    }

    private void share() {
        AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
        String strShare = "";
        if (dto instanceof NewsItemCompactDTO) {
            strShare = (((NewsItemCompactDTO) dto).description);
        } else if (dto instanceof DiscussionDTO) {
            strShare = (((DiscussionDTO) dto).text);
        }
        if (TextUtils.isEmpty(strShare)) {
            if (tvUserTLContent.getText() == null) {
                return;
            }
            shareToWechatMoment(tvUserTLContent.getText().toString());
            return;
        }
        shareToWechatMoment(strShare);
    }

    private void sendReport(AbstractDiscussionCompactDTO dto, int position) {
        if (dto == null) {
            return;
        }
        DiscussReportDTO discussReportDTO = new DiscussReportDTO();
        if (dto instanceof TimelineItemDTO) {
            discussReportDTO.discussionType = ((TimelineItemDTO) dto).type;
        } else if (dto instanceof DiscussionDTO) {
            //The type of all DiscussionDTO is 1 when report.
            discussReportDTO.discussionType = 1;
        } else {
            return;
        }
        discussReportDTO.reportType = position;
        discussReportDTO.discussionId = dto.id;
        THLog.d("discussReportDTO.discussionType " + discussReportDTO.discussionType + " discussReportDTO.reportType " + discussReportDTO.reportType + " discussReportDTO.discussionId " + discussReportDTO.discussionId);
        discussionServiceWrapper.get().reportTimeLineItem(discussReportDTO, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                THToast.show(R.string.discovery_discuss_report_successfully);
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException thException = new THException(retrofitError);
                THToast.show(thException);
            }
        });
    }

    private void deleteTimeLineItem(final int timeLineItemId) {
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteTimeLineItem(timeLineItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                UserTimeLineAdapter.setTimeLineItemDeleted(timeLineItemId);
                popCurrentFragment();
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish() {
                dismissProgressDlg();
            }
        });
    }

    private void deleteDiscussionItem(final int discussionItemId) {
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteDiscussionItem(discussionItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                adapter.removeDeletedItem(discussionItemId);
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish() {
                dismissProgressDlg();
            }
        });
    }

    private void deleteDiscussion(final int discussionItemId) {
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteDiscussionItem(discussionItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                popCurrentFragment();
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish() {
                dismissProgressDlg();
            }
        });
    }

    private void applyRightAnswerItem(final int commentItemId) {
        int userId = currentUserId.toUserBaseKey().getUserId();
        if (dataDto == null) {
            return;
        }
        showApplyProgressDlg();
        discussionServiceWrapper.get().applyRewardTimeLineAnswer(userId, dataDto.id, commentItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                THToast.show(R.string.discovery_discuss_apply_successfully);
                adapter.applyRightAnswer(commentItemId);
                setRefreshViewRewarded();
                if (dataDto != null) {
                    UserTimeLineAdapter.setTimeLineItemAnswered(dataDto.id);
                }
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish() {
                dismissProgressDlg();
            }
        });
    }

    private boolean isDeleteAllowed(AbstractDiscussionCompactDTO dto) {
        if (dto == null) {
            return false;
        }
        int userId = currentUserId.toUserBaseKey().getUserId();
        UserBaseDTO userBaseDTO = null;
        if (dto instanceof TimelineItemDTO) {
            userBaseDTO = ((TimelineItemDTO) dto).user;
        }
        if (dto instanceof DiscussionDTO) {
            userBaseDTO = ((DiscussionDTO) dto).user;
        }
        if (userBaseDTO != null) {
            if (userId == userBaseDTO.id) {
                return true;
            } else {
                return false;
            }
        }
        return false;
    }

    private boolean isReportAllowed(AbstractDiscussionCompactDTO dto) {
        if (dto == null) {
            return false;
        }
        return !isDeleteAllowed(dto);
    }

    private void showDeleteTimeLineConfirmDlg(final int itemId, int dialogType) {
        if (getActivity() == null) {
            return;
        }
        if (deleteOrApplyTimeLineConfirmDialog == null) {
            deleteOrApplyTimeLineConfirmDialog = new Dialog(getActivity());
            deleteOrApplyTimeLineConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deleteOrApplyTimeLineConfirmDialog.setContentView(R.layout.share_dialog_layout);
            deleteOrApplyTLConfirmDlgTitleTV = (TextView) deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.title);
            deleteOrApplyTLConfirmDlgTitle2TV = (TextView) deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.title2);
            deleteOrApplyTLConfirmDlgCancelTV = (TextView) deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.btn_cancel);
            deleteOrApplyTLConfirmDlgOKTV = (TextView) deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.btn_ok);
            deleteOrApplyTLConfirmDlgOKTV.setText(getString(R.string.discovery_discuss_dlg_btn_ok));
            deleteOrApplyTLConfirmDlgTitle2TV.setVisibility(View.GONE);
            deleteOrApplyTLConfirmDlgCancelTV.setText(getString(R.string.discovery_discuss_dlg_btn_cancel));
            deleteOrApplyTLConfirmDlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        if (deleteOrApplyTimeLineConfirmDialog.isShowing()) {
            return;
        }
        if (dialogType == DIALOG_TYPE_APPLY_COMMENT) {
            deleteOrApplyTLConfirmDlgTitleTV.setText(getString(R.string.discovery_discuss_dlg_title_applycomment));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                    applyRightAnswerItem(itemId);
                }
            });
        }
        if (dialogType == DIALOG_TYPE_DELETE_COMMENT) {
            deleteOrApplyTLConfirmDlgTitleTV.setText(getString(R.string.discovery_discuss_dlg_title_deletecomment));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDiscussionItem(itemId);
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        if (dialogType == DIALOG_TYPE_DELETE_TIMELINE) {
            deleteOrApplyTLConfirmDlgTitleTV.setText(getString(R.string.discovery_discuss_dlg_title_deletetimeline));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //Security Detail sub discuss cache clear
                    SecurityDetailSubCache.getInstance().onlyClearDiscuss();

                    if(discussion_type == DISCUSSION_TIME_LINE_TYPE) {
                        deleteTimeLineItem(itemId);
                    }
                    if(discussion_type == DISCUSSION_DISCUSSION_TYPE) {
                        deleteDiscussion(itemId);
                    }
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        deleteOrApplyTimeLineConfirmDialog.show();
    }

    private void showDeleteProgressDlg() {
        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.discovery_discuss_dlg_delete);
    }

    private void showApplyProgressDlg() {
        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.discovery_discuss_dlg_apply);
    }

    private void dismissProgressDlg() {
        if (getActivity() == null) {
            return;
        }
        progressDialogUtil.dismiss(getActivity());
    }

    private boolean isApplyAllowed(AbstractDiscussionCompactDTO dto) {
        if (dataDto == null) {
            return false;
        }
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO) dataDto;
            if (!timelineItemDTO.isQuestionItem) {
                return false;
            }
            if (timelineItemDTO.isAnswered) {
                return false;
            }
            int userId = currentUserId.toUserBaseKey().getUserId();
            UserProfileCompactDTO userBaseDTOTimeLineOwner = timelineItemDTO.user;
            if (userBaseDTOTimeLineOwner == null || userId != userBaseDTOTimeLineOwner.id) {
                return false;
            }
            UserBaseDTO userBaseDTO = null;
            if (dto instanceof DiscussionDTO) {
                userBaseDTO = ((DiscussionDTO) dto).user;
            }
            if (userBaseDTO == null || userId == userBaseDTO.id) {
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isRewardTimeLine() {
        if (dataDto == null) {
            return false;
        }
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO dto = (TimelineItemDTO) dataDto;
            if (dto.isQuestionItem) {
                return true;
            }
        }
        return false;
    }

    private boolean isEssentialTimeLine(){
        if (dataDto == null) {
            return false;
        }
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO dto = (TimelineItemDTO) dataDto;
            if (dto.isEssential) {
                return true;
            }
        }
        return false;
    }

    private String getRewardCount() {
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO dto = (TimelineItemDTO) dataDto;
            if (dto.isQuestionItem) {
                return dto.getRewardString();
            }
        }
        return "";
    }

    private String getHeader() {
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO dto = (TimelineItemDTO) dataDto;
            return dto.header;
        }
        return "";
    }

    private void checkAndDealDeletedTimeLine() {
        if (dataDto instanceof TimelineItemDTO) {
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO) dataDto;
            if (timelineItemDTO.isDeleted) {
                popCurrentFragment();
                THToast.show(R.string.discovery_discuss_already_deleted);
            }
        }
    }

    private boolean isManager() {
        if (userProfileCache != null && currentUserId != null && Constants.isManager) {
            UserProfileDTO meProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
            if (meProfileDTO != null && meProfileDTO.isAdmin) {
                return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private boolean isTop() {
        if (dataDto instanceof TimelineItemDTO) {
            return UserTimeLineAdapter.showIsTop(((TimelineItemDTO) dataDto).stickType, timelineFrom);
        } else {
            return false;
        }
    }

    private boolean isFavorite() {

        if (dataDto instanceof TimelineItemDTO) {
            return ((TimelineItemDTO) dataDto).isEssential;
        } else {
            return false;
        }
    }

    private boolean isProduction() {
        if (dataDto instanceof TimelineItemDTO) {
            return ((TimelineItemDTO) dataDto).isNotice;
        } else {
            return false;
        }
    }

    private boolean isLearning() {
        if (dataDto instanceof TimelineItemDTO) {
            return ((TimelineItemDTO) dataDto).isGuide;
        } else {
            return false;
        }
    }


    private class ManagerOperateCallback implements Callback {

        @Override
        public void success(Object o, Response response) {
            THToast.show(R.string.administrator_operate_success);
            popCurrentFragment();
        }

        @Override
        public void failure(RetrofitError retrofitError) {
            THException exception = new THException(retrofitError);
            THToast.show(exception.toString());
        }
    }

}
