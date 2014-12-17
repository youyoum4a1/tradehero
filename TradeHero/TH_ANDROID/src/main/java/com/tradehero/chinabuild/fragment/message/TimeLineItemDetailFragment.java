package com.tradehero.chinabuild.fragment.message;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.*;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.squareup.picasso.Picasso;
import com.tradehero.chinabuild.data.DiscussReportDTO;
import com.tradehero.chinabuild.dialog.*;
import com.tradehero.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.chinabuild.listview.SecurityListView;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimeLineBaseAdapter;
import com.tradehero.th.adapters.TimeLineDetailDiscussSecItem;
import com.tradehero.th.adapters.UserTimeLineAdapter;
import com.tradehero.th.api.discussion.*;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.key.*;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.InviteFormDTO;
import com.tradehero.th.api.social.InviteFormWeiboDTO;
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseDTO;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.InputTools;
import com.tradehero.th.utils.ProgressDialogUtil;
import com.tradehero.th.utils.WeiboUtils;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class TimeLineItemDetailFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener, View.OnClickListener
{

    public static final String BUNDLE_ARGUMENT_DISCUSSTION_ID = "bundle_argment_discusstion_id";

    @Inject protected DiscussionCache discussionCache;
    private DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> discussionFetchListener;
    DiscussionKey timelineItemDTOKey;
    private PaginatedDiscussionListKey discussionListKey;
    @Inject  DiscussionListCacheNew discussionListCache;

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
    @Inject Picasso picasso;
    private MiddleCallback<DiscussionDTO> voteCallback;
    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @InjectView(R.id.tradeheroprogressbar_timeline_detail) TradeHeroProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.rlAllView) RelativeLayout rlAllView;

    @Inject UserProfileCache userProfileCache;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;
    @Inject CurrentUserId currentUserId;

    private LinearLayout llDisscurssOrNews;
    private ImageView imgSecurityTLUserHeader;
    private TextView tvUserTLTimeStamp;
    private TextView tvUserTLContent;
    private TextView tvUserTLName;
    private LinearLayout llTLPraise;
    private LinearLayout llTLPraiseDown;
    private LinearLayout llTLComment;
    private TextView tvTLPraise;
    private TextView tvTLPraiseDown;
    private TextView tvTLComment;
    private TextView btnTLPraise;
    private TextView btnTLPraiseDown;

    private LinearLayout mRefreshView;
    private TextView tvIsReward;
    private TextView tvUserTVTitle;

    private String strReply = "";

    private final int ITEMS_PER_PAGE = 50;

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
    private final int DIALOG_TYPE_DELETE_TIMELINE = 1;
    private final int DIALOG_TYPE_DELETE_COMMENT = 2;
    private final int DIALOG_TYPE_APPLY_COMMENT = 3;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discussionFetchListener = createDiscussionCacheListener();
        initArgument();
        adapter = new TimeLineDetailDiscussSecItem(getActivity());
    }

    public void initArgument()
    {
        Bundle bundle = getArguments();
        if (bundle.containsKey(BUNDLE_ARGUMENT_DISCUSSTION_ID))
        {
            timelineItemDTOKey = discussionKeyFactory.fromBundle(bundle.getBundle(BUNDLE_ARGUMENT_DISCUSSTION_ID));
            fetchDiscussion(timelineItemDTOKey, true);
            discussionListKey = new PaginatedDiscussionListKey(timelineItemDTOKey.getType(), timelineItemDTOKey.id, 1, ITEMS_PER_PAGE);
            fetchDiscussList(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("详情");
        setHeadViewRight0(getActivity().getResources().getString(R.string.discovery_discuss_send_more));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_item_detail, container, false);
        ButterKnife.inject(this, view);
        setNeedToMonitorBackPressed(true);
        ListView lv = listTimeLine.getRefreshableView();
        mRefreshView = (LinearLayout) inflater.inflate(R.layout.security_time_line_item_plus, null);
        lv.addHeaderView(mRefreshView);
        initRoot(mRefreshView);

        initView();

        if (dataDto == null)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.tradeheroprogressbar_timeline_detail);
            progressBar.startLoading();
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
        }
        initRefreshView();
        return view;
    }

    public void initRoot(View view)
    {
        llDisscurssOrNews = (LinearLayout) view.findViewById(R.id.llItemAll);
        imgSecurityTLUserHeader = (ImageView) view.findViewById(R.id.imgSecurityTLUserHeader);
        tvUserTLTimeStamp = (TextView) view.findViewById(R.id.tvUserTLTimeStamp);
        tvUserTLContent = (TextView) view.findViewById(R.id.tvUserTLContent);
        tvUserTLName = (TextView) view.findViewById(R.id.tvUserTLName);
        llTLPraise = (LinearLayout) view.findViewById(R.id.llTLPraise);
        llTLPraiseDown = (LinearLayout) view.findViewById(R.id.llTLPraiseDown);
        llTLComment = (LinearLayout) view.findViewById(R.id.llTLComment);
        tvTLPraise = (TextView) view.findViewById(R.id.tvTLPraise);
        tvTLPraiseDown =  (TextView) view.findViewById(R.id.tvTLPraiseDown);
        tvTLComment = (TextView) view.findViewById(R.id.tvTLComment);
        btnTLPraise = (TextView) view.findViewById(R.id.btnTLPraise);
        btnTLPraiseDown = (TextView) view.findViewById(R.id.btnTLPraiseDown);
        tvUserTLName.setOnClickListener(this);
        imgSecurityTLUserHeader.setOnClickListener(this);
        llTLPraise.setOnClickListener(this);
        llTLPraiseDown.setOnClickListener(this);
        llTLComment.setOnClickListener(this);

        llDisscurssOrNews.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                setHintForSender(-1);
            }
        });
    }

    public void initView()
    {
        tvUserTLContent.setMaxLines(1000);
        listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listTimeLine.setAdapter(adapter);

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                discussionListKey.setPage(1);
                fetchDiscussList(true);
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                discussionListKey = discussionListKey.next();
                fetchDiscussList(true);
            }
        });

        adapter.setListener(new TimeLineBaseAdapter.TimeLineOperater()
        {
            @Override
            public void OnTimeLineItemClicked(int position)
            {
                onCommentClick(position);
            }

            @Override
            public void OnTimeLinePraiseClicked(int position)
            {

            }

            @Override
            public void OnTimeLinePraiseDownClicked(int position)
            {

            }

            @Override
            public void OnTimeLineCommentsClicked(int position)
            {

            }

            @Override
            public void OnTimeLineShareClicked(int position)
            {

            }

            @Override
            public void OnTimeLineBuyClicked(int position)
            {

            }
        });

    }

    private void initRefreshView(){
        if(mRefreshView==null){
            return;
        }
        if(tvIsReward == null){
            tvIsReward = (TextView)mRefreshView.findViewById(R.id.tvIsReward);
        }
        if(tvUserTVTitle == null) {
            tvUserTVTitle = (TextView) mRefreshView.findViewById(R.id.tvUserTVTitle);
        }
        if(!TextUtils.isEmpty(getHeader())){
            tvUserTVTitle.setVisibility(View.VISIBLE);
            tvUserTVTitle.setText(getHeader());
        }else{
            tvUserTVTitle.setVisibility(View.GONE);
        }
        if(isRewardTimeLine()){
            tvIsReward.setVisibility(View.VISIBLE);
            tvIsReward.setText(getRewardCount());
        }else{
            tvIsReward.setVisibility(View.GONE);
        }
    }

    private void setRefreshViewRewarded(){
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO)dataDto;
            if(timelineItemDTO.isQuestionItem) {
                timelineItemDTO.isAnswered = true;
                tvIsReward.setVisibility(View.VISIBLE);
                tvIsReward.setText(getRewardCount());
            }
        }
    }

    public void setDefaultReply()
    {
        edtSend.setHint(getResources().getString(R.string.please_to_reply));
        strReply = "";
        isReplayFollower = false;
    }

    boolean isReplayFollower = false;

    public void setHintForSender(long position)
    {
        if (position == -1)//回复主题
        {
            setDefaultReply();
        }
        else//回复楼层
        {
            AbstractDiscussionCompactDTO dto = adapter.getItem((int) position);
            if (dto == null)
            {
                return;
            }
            if (dto instanceof DiscussionDTO)
            {
                String displayName = ((DiscussionDTO) dto).user.getDisplayName();
                int id = ((DiscussionDTO) dto).userId;
                String strHint = "回复 " + displayName + ":";
                if (edtSend != null)
                {
                    edtSend.setHint(strHint);
                    //"<@(.+?),(\\d+)@>"
                    strReply = "<@@" + displayName + "," + id + "@>";
                    isReplayFollower = true;
                }
            }
        }
        openInputMethod();
    }

    public void openInputMethod()
    {
        InputTools.KeyBoard(edtSend, "open");
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (isReplayFollower)
        {
            strReply = "";
            edtSend.setText("");
            edtSend.setHint(getResources().getString(R.string.please_to_reply));
            isReplayFollower = false;
        }
        else
        {
            popCurrentFragment();
        }
    }

    @Override
    public void onDestroyView()
    {
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

    private void fetchDiscussion(DiscussionKey discussionKey, boolean force)
    {
        detachDiscussionFetchTask();
        discussionCache.register(discussionKey, discussionFetchListener);
        discussionCache.getOrFetchAsync(discussionKey, force);
    }

    private void detachDiscussionFetchTask()
    {
        discussionCache.unregister(discussionFetchListener);
    }

    private void detachDiscussionFetch()
    {
        discussionListCache.unregister(this);
    }

    public void fetchDiscussList(boolean force)
    {
        if (discussionListKey != null)
        {
            detachDiscussionFetch();
            discussionListCache.register(discussionListKey, this);
            discussionListCache.getOrFetchAsync(discussionListKey, force);
        }
    }

    protected DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> createDiscussionCacheListener()
    {
        return new PrivateDiscussionViewDiscussionCacheListener();
    }

    @Override
    public void onClick(View view)
    {
        onOperaterClicked(view);
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO>
    {
        @Override
        public void onDTOReceived(@NotNull DiscussionKey key, @NotNull AbstractDiscussionCompactDTO value) {
            linkWithDTO(value);
            OnFinish();
        }

        @Override
        public void onErrorThrown(@NotNull DiscussionKey key, @NotNull Throwable error)
        {
            OnFinish();
        }

        public void OnFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
            progressBar.stopLoading();
        }
    }

    public void linkWithDTO(AbstractDiscussionCompactDTO value)
    {
        if(value!=null) {
            this.dataDto = value;
            checkAndDealDeletedTimeLine();
            initRefreshView();
            fetchDiscussList(true);
            displayDiscussOrNewsDTO();
        }
    }

    public AbstractDiscussionCompactDTO getAbstractDiscussionCompactDTO()
    {
        return dataDto;
    }

    public void displayDiscussOrNewsDTO()
    {
        AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
        llDisscurssOrNews.setVisibility(dto == null ? View.INVISIBLE : View.VISIBLE);
        if (dto != null)
        {
            imgSecurityTLUserHeader.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);
            tvUserTLName.setVisibility(dto instanceof NewsItemCompactDTO ? View.GONE : View.VISIBLE);

            if (dto instanceof NewsItemDTO)
            {
                tvUserTLContent.setText(((NewsItemDTO) dto).text);
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((NewsItemCompactDTO) dto).createdAtUtc));
            }
            else if (dto instanceof DiscussionDTO)
            {
                tvUserTLName.setText(((DiscussionDTO) dto).user.getDisplayName());
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((DiscussionDTO) dto).createdAtUtc));
                tvUserTLContent.setText(((DiscussionDTO) dto).text);
                picasso.load(((DiscussionDTO) dto).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(imgSecurityTLUserHeader);
            }
            else if (dto instanceof TimelineItemDTO)
            {
                tvUserTLTimeStamp.setText(prettyTime.get().formatUnrounded(((TimelineItemDTO) dto).createdAtUtc));
                tvUserTLName.setText(((TimelineItemDTO) dto).user.getDisplayName());
                tvUserTLContent.setText(((TimelineItemDTO) dto).text);
                picasso.load(((TimelineItemDTO) dto).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(imgSecurityTLUserHeader);
            }


            btnTLPraise.setBackgroundResource(dto.voteDirection==1?R.drawable.icon_praise_active:R.drawable.icon_praise_normal);
            btnTLPraiseDown.setBackgroundResource(dto.voteDirection==-1?R.drawable.icon_praise_down_active:R.drawable.icon_praise_down_normal);

            tvTLComment.setText("" + dto.commentCount);
            tvTLPraise.setText(Html.fromHtml(dto.getVoteUpString()));
            tvTLPraiseDown.setText(Html.fromHtml(dto.getVoteDownString()));
        }
    }

    @Override
    public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value)
    {
        List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
        for (int i = 0; i < value.size(); i++)
        {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
            listData.add(dto);
        }
        if (discussionListKey.getPage() == 1)
        {
            adapter.setListData(listData);
        }
        else
        {
            adapter.addListData(listData);
        }
        listTimeLine.onRefreshComplete();
        if (adapter.getCount() >= ITEMS_PER_PAGE)
        {
            listTimeLine.setMode(PullToRefreshBase.Mode.BOTH);
        }
        else
        {
            listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    @Override
    public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {
        listTimeLine.onRefreshComplete();
    }

    @OnClick(R.id.btnSend)
    public void OnSendClicked()
    {
        postDiscussion();
    }

    private void unsetDiscussionEditMiddleCallback()
    {
        if (discussionEditMiddleCallback != null)
        {
            discussionEditMiddleCallback.setPrimaryCallback(null);
        }
        discussionEditMiddleCallback = null;
    }

    protected void postDiscussion()
    {
        if (validate())
        {
            DiscussionFormDTO discussionFormDTO = buildDiscussionFormDTO();
            if (discussionFormDTO == null) return;
            unsetDiscussionEditMiddleCallback();
            progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
            discussionEditMiddleCallback = discussionServiceWrapper.get().createDiscussion(discussionFormDTO, new SecurityDiscussionEditCallback());
        }
    }

    protected DiscussionFormDTO buildDiscussionFormDTO()
    {
        DiscussionType discussionType = getDiscussionType();
        if (discussionType != null)
        {
            DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
            if (timelineItemDTOKey != null)
            {
                discussionFormDTO.inReplyToId = timelineItemDTOKey.id;
            }
            discussionFormDTO.text = strReply + " " + edtSend.getText().toString();

            return discussionFormDTO;
        }

        return null;
    }

    protected DiscussionType getDiscussionType()
    {
        if (timelineItemDTOKey != null)
        {
            return timelineItemDTOKey.getType();
        }
        return null;
    }

    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override
        public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();
            DeviceUtil.dismissKeyboard(getActivity());
            discussionListKey.setPage(1);
            fetchDiscussList(true);
            fetchDiscussion(timelineItemDTOKey, true);
            strReply = "";
            edtSend.setText("");
        }

        @Override
        public void failure(RetrofitError error)
        {
            onFinish();
            //THToast.show(error.getMessage());
            THException thException = new THException(error);
            THToast.show(thException);
        }

        private void onFinish()
        {
            dismissProgressDlg();
        }
    }

    private boolean validate()
    {
        boolean notEmptyText = validateNotEmptyText();
        if (!notEmptyText)
        {
            THToast.show(R.string.error_discussion_empty_post);
        }
        return notEmptyText;
    }

    private boolean validateNotEmptyText()
    {
        return !edtSend.getText().toString().trim().isEmpty();
    }

    public void onOperaterClicked(View view)
    {
        if (view.getId() == R.id.imgSecurityTLUserHeader || view.getId() == R.id.tvUserTLName)
        {
            //
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            if (dto instanceof DiscussionDTO)
            {
                openUserProfile(((DiscussionDTO) getAbstractDiscussionCompactDTO()).user.id);
            }
            else if (dto instanceof TimelineItemDTO)
            {
                openUserProfile(((TimelineItemDTO) getAbstractDiscussionCompactDTO()).user.id);
            }
        }
        else if (view.getId() == R.id.llTLPraise)
        {
            clickedPraise();
        }
        else if (view.getId() == R.id.llTLPraiseDown)
        {
            clickedPraiseDown();
        }
        else if (view.getId() == R.id.llTLComment)
        {
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            comments(dto);
        }
    }

    public void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        pushFragment(DiscussSendFragment.class, bundle);
    }


    public void clickedPraise()
    {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();

        if (item.voteDirection == 1)
        {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        else if(item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
            updateVoting(VoteDirection.UpVote, item);
        }
        else if(item.voteDirection == -1)
        {
            item.voteDirection = 1;
            item.upvoteCount +=1;
            item.downvoteCount = item.downvoteCount > 0?(item.downvoteCount -1):0;
            updateVoting(VoteDirection.UpVote, item);
        }

        displayDiscussOrNewsDTO();


    }

    public void clickedPraiseDown()
    {
        AbstractDiscussionCompactDTO item = getAbstractDiscussionCompactDTO();

        if (item.voteDirection == 1)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            item.upvoteCount = item.upvoteCount>0?(item.upvoteCount-1):0;
            updateVoting(VoteDirection.DownVote, item);
        }
        else if (item.voteDirection == 0)
        {
            item.voteDirection = -1;
            item.downvoteCount += 1;
            updateVoting(VoteDirection.DownVote, item);
        }
        else if(item.voteDirection == -1)
        {
            item.voteDirection = 0;
            item.downvoteCount = item.downvoteCount > 0 ? (item.downvoteCount - 1) : 0;
            updateVoting(VoteDirection.UnVote, item);
        }
        displayDiscussOrNewsDTO();
    }

    public void share(String strShare)
    {
        mShareSheetTitleCache.set(strShare);
        ShareSheetDialogLayout contentView = (ShareSheetDialogLayout) LayoutInflater.from(getActivity())
                .inflate(R.layout.share_sheet_dialog_layout, null);
        contentView.setLocalSocialClickedListener(
                new ShareSheetDialogLayout.OnLocalSocialClickedListener()
                {
                    @Override
                    public void onShareRequestedClicked()
                    {

                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    //Share to wechat moment and share to weibo on the background
    private void shareToWechatMoment(final String strShare)
    {
        if (TextUtils.isEmpty(strShare))
        {
            return;
        }
        String show = getUnParsedText(strShare);
        UserProfileDTO updatedUserProfileDTO = userProfileCache.get(currentUserId.toUserBaseKey());
        if (updatedUserProfileDTO != null)
        {
            if (updatedUserProfileDTO.wbLinked)
            {
                String downloadCNTradeHeroWeibo = getActivity().getResources().getString(R.string.download_tradehero_android_app_on_weibo);
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

    private void updateVoting(VoteDirection voteDirection, AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO == null)
        {
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

    protected void detachVoteMiddleCallback()
    {
        if (voteCallback != null)
        {
            voteCallback.setPrimaryCallback(null);
        }
        voteCallback = null;
    }

    protected class VoteCallback implements retrofit.Callback<DiscussionDTO>
    {
        public VoteCallback(VoteDirection voteDirection)
        {
        }

        @Override
        public void success(DiscussionDTO discussionDTO, Response response)
        {
        }

        @Override
        public void failure(RetrofitError error)
        {
        }
    }

    @Override
    public void onClickHeadRight0()
    {
        if (dialogFactory == null)
        {
            dialogFactory = new DialogFactory();
        }
        if (getActivity() == null)
        {
            return;
        }

        boolean isDeleteAllowed = isDeleteAllowed(dataDto);
        boolean isReportAllowed = isReportAllowed(dataDto);
        timeLineDetailMenuDialog = dialogFactory.createTimeLineDetailDialog(getActivity(), new TimeLineDetailDialogLayout.TimeLineDetailMenuClickListener() {
            @Override
            public void onReportClick() {
                timeLineDetailMenuDialog.dismiss();
                if(getActivity() ==  null){
                    return;
                }
                timeLineReportMenuDialog = dialogFactory.createTimeLineReportDialog(getActivity(), new TimeLineReportDialogLayout.TimeLineReportMenuClickListener() {
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
                if(dataDto!=null){
                    showDeleteTimeLineConfirmDlg(dataDto.id, DIALOG_TYPE_DELETE_TIMELINE);
                }
            }

            @Override
            public void onShareClick() {
                share();
                timeLineDetailMenuDialog.dismiss();
            }
        }, isDeleteAllowed, isReportAllowed);
    }

    public void onCommentClick(final int position)
    {
        if (dialogFactory == null)
        {
            dialogFactory = new DialogFactory();
        }
        if (getActivity() == null)
        {
            return;
        }
        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
        boolean isApplyAllowed = isApplyAllowed(dto);
        boolean isDeleteAllowed = isDeleteAllowed(dto);
        boolean isReportAllowed = !isDeleteAllowed;

        timeLineCommentMenuDialog = dialogFactory.createTimeLineCommentDialog(getActivity(),
                new TimeLineCommentDialogLayout.TimeLineCommentMenuClickListener()
                {
                    @Override
                    public void onCommentClick()
                    {
                        setHintForSender(position);
                        timeLineCommentMenuDialog.dismiss();
                    }

                    @Override
                    public void onReportClick()
                    {
                        timeLineCommentMenuDialog.dismiss();
                        if (getActivity() == null)
                        {
                            return;
                        }
                        timeLineReportMenuDialog = dialogFactory.createTimeLineReportDialog(getActivity(),
                                new TimeLineReportDialogLayout.TimeLineReportMenuClickListener()
                                {
                                    @Override
                                    public void onItemClickListener(int position_report)
                                    {
                                        timeLineReportMenuDialog.dismiss();
                                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                                        sendReport(dto, position_report);
                                    }
                                });
                    }

                    @Override
                    public void onDeleteClick()
                    {
                        timeLineCommentMenuDialog.dismiss();
                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                        if (dto != null)
                        {
                            showDeleteTimeLineConfirmDlg(dto.id, DIALOG_TYPE_DELETE_COMMENT);
                        }
                    }

                    @Override
                    public void onApplyClick()
                    {
                        timeLineCommentMenuDialog.dismiss();
                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                        if(dto!=null) {
                            showDeleteTimeLineConfirmDlg(dto.id, DIALOG_TYPE_APPLY_COMMENT);
                        }
                    }
                }, isApplyAllowed, isDeleteAllowed, isReportAllowed);
    }

    private void share()
    {
        AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
        String strShare = "";
        if (dto instanceof NewsItemCompactDTO)
        {
            strShare = (((NewsItemCompactDTO) dto).description);
        }
        else if (dto instanceof DiscussionDTO)
        {
            strShare = (((DiscussionDTO) dto).text);
        }
        if (TextUtils.isEmpty(strShare))
        {
            if (tvUserTLContent.getText() == null)
            {
                return;
            }
            shareToWechatMoment(tvUserTLContent.getText().toString());
            return;
        }
        shareToWechatMoment(strShare);
    }

    private void sendReport(AbstractDiscussionCompactDTO dto, int position){
        if (dto == null) {
            return;
        }
        DiscussReportDTO discussReportDTO = new DiscussReportDTO();
        if(dto instanceof  TimelineItemDTO) {
            discussReportDTO.discussionType = ((TimelineItemDTO)dto).type;
        }else  if(dto instanceof  DiscussionDTO){
            discussReportDTO.discussionType = ((DiscussionDTO) dto).type.value;
        }else {
            return;
        }
        discussReportDTO.reportType = position;
        discussReportDTO.discussionId = dto.id;
        discussionServiceWrapper.get().reportTimeLineItem(discussReportDTO, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                THToast.show(R.string.discovery_discuss_report_successfully);
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                //THToast.show(retrofitError.getMessage());
                THException thException = new THException(retrofitError);
                THToast.show(thException);
            }
        });
    }

    private void deleteTimeLineItem(final int timeLineItemId){
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteTimeLineItem(timeLineItemId, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                UserTimeLineAdapter.setTimeLineItemDeleted(timeLineItemId);
                popCurrentFragment();
                onFinish();
                //通知APP删除timelineItemId的帖子
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                //THToast.show(retrofitError.getMessage());
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish()
            {
                dismissProgressDlg();
            }
        });
    }

    private void deleteDiscussionItem(final int discussionItemId){
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteDiscussionItem(discussionItemId, new Callback<Response>()
        {
            @Override
            public void success(Response response, Response response2)
            {
                adapter.removeDeletedItem(discussionItemId);
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError)
            {
                //THToast.show(retrofitError.getMessage());
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish()
            {
                dismissProgressDlg();
            }
        });
    }

    private void applyRightAnswerItem(final int commentItemId){
        int userId = currentUserId.toUserBaseKey().getUserId();
        if(dataDto== null){
            return;
        }
        showApplyProgressDlg();
        discussionServiceWrapper.get().applyRewardTimeLineAnswer(userId, dataDto.id, commentItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                THToast.show(R.string.discovery_discuss_apply_successfully);
                adapter.applyRightAnswer(commentItemId);
                setRefreshViewRewarded();
                if(dataDto!=null)
                {
                    UserTimeLineAdapter.setTimeLineItemAnswered(dataDto.id);
                }
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                //THToast.show(retrofitError.getMessage());
                THException thException = new THException(retrofitError);
                THToast.show(thException);
                onFinish();
            }

            private void onFinish(){
                dismissProgressDlg();
            }
        });
    }

    private boolean isDeleteAllowed(AbstractDiscussionCompactDTO dto){
        if(dto ==null){
            return false;
        }
        int userId = currentUserId.toUserBaseKey().getUserId();
        UserBaseDTO userBaseDTO = null;
        if(dto instanceof TimelineItemDTO){
            userBaseDTO= ((TimelineItemDTO)dto).user;

        }
        if(dto instanceof DiscussionDTO){
            userBaseDTO = ((DiscussionDTO)dto).user;
        }
        if(userBaseDTO!=null){
            if(userId == userBaseDTO.id){
                return true;
            }else{
                return false;
            }
        }
        return false;
    }

    private boolean isReportAllowed(AbstractDiscussionCompactDTO dto){
        if(dto ==null){
            return false;
        }
        return !isDeleteAllowed(dto);
    }

    private void showDeleteTimeLineConfirmDlg(final int itemId, int dialogType){
        if(getActivity()==null){
            return;
        }
        if(deleteOrApplyTimeLineConfirmDialog==null) {
            deleteOrApplyTimeLineConfirmDialog = new Dialog(getActivity());
            deleteOrApplyTimeLineConfirmDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            deleteOrApplyTimeLineConfirmDialog.setContentView(R.layout.share_dialog_layout);
            deleteOrApplyTLConfirmDlgTitleTV = (TextView)deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.title);
            deleteOrApplyTLConfirmDlgTitle2TV = (TextView)deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.title2);
            deleteOrApplyTLConfirmDlgCancelTV = (TextView)deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.btn_cancel);
            deleteOrApplyTLConfirmDlgOKTV = (TextView)deleteOrApplyTimeLineConfirmDialog.findViewById(R.id.btn_ok);
            deleteOrApplyTLConfirmDlgOKTV.setText(getActivity().getResources().getString(R.string.discovery_discuss_dlg_btn_ok));
            deleteOrApplyTLConfirmDlgTitle2TV.setVisibility(View.GONE);
            deleteOrApplyTLConfirmDlgCancelTV.setText(getActivity().getResources().getString(R.string.discovery_discuss_dlg_btn_cancel));
            deleteOrApplyTLConfirmDlgCancelTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        if(deleteOrApplyTimeLineConfirmDialog.isShowing()){
            return;
        }
        if(dialogType == DIALOG_TYPE_APPLY_COMMENT){
            deleteOrApplyTLConfirmDlgTitleTV.setText(getActivity().getResources().getString(R.string.discovery_discuss_dlg_title_applycomment));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                    applyRightAnswerItem(itemId);
                }
            });
        }
        if(dialogType == DIALOG_TYPE_DELETE_COMMENT){
            deleteOrApplyTLConfirmDlgTitleTV.setText(getActivity().getResources().getString(R.string.discovery_discuss_dlg_title_deletecomment));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteDiscussionItem(itemId);
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        if(dialogType ==DIALOG_TYPE_DELETE_TIMELINE){
            deleteOrApplyTLConfirmDlgTitleTV.setText(getActivity().getResources().getString(R.string.discovery_discuss_dlg_title_deletetimeline));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteTimeLineItem(itemId);
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        deleteOrApplyTimeLineConfirmDialog.show();
    }

    private void showDeleteProgressDlg(){
        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.discovery_discuss_dlg_delete);
    }

    private void showApplyProgressDlg(){
        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.discovery_discuss_dlg_apply);
    }

    private void dismissProgressDlg(){
        if(getActivity()==null){
            return;
        }
        progressDialogUtil.dismiss(getActivity());
    }

    private boolean isApplyAllowed(AbstractDiscussionCompactDTO dto){
        if(dataDto==null){
            return false;
        }
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO)dataDto;
            if(!timelineItemDTO.isQuestionItem){
                return false;
            }
            if(timelineItemDTO.isAnswered){
                return false;
            }
            int userId = currentUserId.toUserBaseKey().getUserId();
            UserBaseDTO userBaseDTO = null;
            if(dto instanceof TimelineItemDTO){
                userBaseDTO= ((TimelineItemDTO)dto).user;

            }
            if(dto instanceof DiscussionDTO){
                userBaseDTO = ((DiscussionDTO)dto).user;
            }
            if(userBaseDTO==null || userId == userBaseDTO.id){
                return false;
            }
            return true;
        }
        return false;
    }

    private boolean isRewardTimeLine(){
        if(dataDto==null){
            return false;
        }
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO dto = (TimelineItemDTO)dataDto;
            if(dto.isQuestionItem){
                return true;
            }
        }
        return false;
    }

    private String getRewardCount(){
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO dto = (TimelineItemDTO)dataDto;
            if(dto.isQuestionItem){
                return dto.getRewardString();
            }
        }
        return "";
    }

    private String getHeader(){
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO dto = (TimelineItemDTO)dataDto;
            return dto.header;
        }
        return "";
    }

    private void checkAndDealDeletedTimeLine(){
        if(dataDto instanceof TimelineItemDTO){
            TimelineItemDTO timelineItemDTO = (TimelineItemDTO)dataDto;
            if(timelineItemDTO.isDeleted){
                popCurrentFragment();
                THToast.show(R.string.discovery_discuss_already_deleted);
            }
        }
    }
}
