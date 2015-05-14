package com.tradehero.chinabuild.fragment.discovery;

import android.app.Dialog;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AnimationUtils;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import butterknife.ButterKnife;
import android.view.Menu;
import android.view.MenuInflater;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshBase;
import com.handmark.pulltorefresh.library.pulltorefresh.PullToRefreshListView;
import com.tradehero.chinabuild.data.DiscussReportDTO;
import com.tradehero.chinabuild.dialog.DialogFactory;
import com.tradehero.chinabuild.dialog.TimeLineCommentDialogLayout;
import com.tradehero.chinabuild.dialog.TimeLineReportDialogLayout;
import com.tradehero.chinabuild.fragment.message.DiscussSendFragment;
import com.tradehero.chinabuild.fragment.message.TimeLineItemDetailFragment;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.adapters.TimeLineBaseAdapter;
import com.tradehero.th.adapters.TimeLineDetailDiscussSecItem;
import com.tradehero.th.api.discussion.*;
import com.tradehero.th.api.discussion.form.DiscussionFormDTO;
import com.tradehero.th.api.discussion.form.DiscussionFormDTOFactory;
import com.tradehero.th.api.discussion.key.DiscussionKey;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.discussion.key.PaginatedDiscussionListKey;
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
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.network.share.SocialSharerImpl;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.utils.*;
import com.tradehero.th.widget.TradeHeroProgressBar;
import dagger.Lazy;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Detail of News
 *
 * Created by palmer on 15/1/16.
 */
public class NewsDetailFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener, View.OnClickListener{

    @Inject Lazy<NewsServiceWrapper> newsServiceWrapper;
    @Inject UserProfileCache userProfileCache;
    @Inject CurrentUserId currentUserId;
    @Inject Lazy<UserServiceWrapper> userServiceWrapper;
    @Inject Lazy<SocialSharer> socialSharerLazy;

    //Download comments
    private PaginatedDiscussionListKey discussionListKey;
    @Inject DiscussionListCacheNew discussionListCache;
    @Inject protected DiscussionCache discussionCache;


    private PullToRefreshListView pullToRefreshListView;

    private RelativeLayout sendDiscussionRL;
    private TimeLineDetailDiscussSecItem adapter;
    private TextView textview_news_detail_title;
    private TradeHeroProgressBar mTradeHeroProgressBar;

    //News Operater
    private LinearLayout timeline_detail_llTLPraise;
    private TextView timeline_detail_btnTLPraise;
    private TextView timeline_detail_tvTLPraise;
    private LinearLayout timeline_detail_llTLPraiseDown;
    private TextView timeline_detail_btnTLPraiseDown;
    private TextView timeline_detail_tvTLPraiseDown;
    private LinearLayout timeline_detail_llTLComment;
    private TextView timeline_detail_tvTLComment;

    //Header view for the list
    private WebView newsWebView;
    private View headerView;
    private Button sendCommentBtn;
    private EditText editCommentET;

    private DisplayMetrics dm;

    private long newsId;
    private String newsTitle;
    private String htmlContent;

    public final static String KEY_BUNDLE_NEWS_ID = "key_bundle_news_id";
    public final static String KEY_BUNDLE_NEWS_TITLE = "key_bundle_news_title";

    private String strReply = "";
    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;


    //Delete TimeLine confirm dialog or apply comment dialog
    private Dialog deleteOrApplyTimeLineConfirmDialog;
    private TextView deleteOrApplyTLConfirmDlgTitleTV;
    private TextView deleteOrApplyTLConfirmDlgTitle2TV;
    private TextView deleteOrApplyTLConfirmDlgOKTV;
    private TextView deleteOrApplyTLConfirmDlgCancelTV;

    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    private Dialog timeLineCommentMenuDialog;
    private Dialog timeLineReportMenuDialog;
    private DialogFactory dialogFactory;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;


    private NewsItemDTO newsItemDTO;
    private MiddleCallback<DiscussionDTO> voteCallback;

    private int marginWebView = 0;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        adapter = new TimeLineDetailDiscussSecItem(getActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.news_detail, container, false);

        ButterKnife.inject(this, view);
        setNeedToMonitorBackPressed(true);
        dm = new DisplayMetrics();
        dm = getActivity().getResources().getDisplayMetrics();
        marginWebView = (int)getActivity().getResources().getDimension(R.dimen.margin_small);
        sendDiscussionRL = (RelativeLayout)view.findViewById(R.id.rlSend);
        sendCommentBtn = (Button)view.findViewById(R.id.btnSend);
        sendCommentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postDiscussion();
            }
        });
        editCommentET = (EditText)view.findViewById(R.id.edtSend);

        mTradeHeroProgressBar = (TradeHeroProgressBar)view.findViewById(R.id.tradeheroprogressbar_discovery_news_detail_loading);

        pullToRefreshListView = (PullToRefreshListView)view.findViewById(R.id.pulltorefreshlistview_discovery_news_comments);
        pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        pullToRefreshListView.setAdapter(adapter);

        adapter.setListener(new TimeLineBaseAdapter.TimeLineOperater()
        {
            @Override
            public void OnTimeLineItemClicked(int position)
            {
                onCommentClick(position);
            }
            @Override
            public void OnTimeLinePraiseClicked(int position){}
            @Override
            public void OnTimeLinePraiseDownClicked(int position){}
            @Override
            public void OnTimeLineCommentsClicked(int position){}
            @Override
            public void OnTimeLineShareClicked(int position){}
            @Override
            public void OnTimeLineBuyClicked(int position){}
        });

        initHeaderViews();

        Bundle bundle = getArguments();
        newsId = bundle.getLong(KEY_BUNDLE_NEWS_ID);
        newsTitle = bundle.getString(KEY_BUNDLE_NEWS_TITLE);
        textview_news_detail_title.setText(newsTitle);


        pullToRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(discussionListKey==null){
                    return;
                }
                discussionListKey.setPage(1);
                fetchComments();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
                if(discussionListKey==null){
                    return;
                }
                discussionListKey = discussionListKey.next();
                fetchComments();
            }
        });
        showLoadingProgressBar();
        retrieveNewsDetail();
        return view;
    }

    private void initHeaderViews(){
        headerView = getActivity().getLayoutInflater().inflate(
                R.layout.discovery_news_detail_header, null);
        newsWebView =  (WebView)headerView.findViewById(R.id.webview_news_html_content);
        newsWebView.setScrollBarStyle(View.SCROLLBARS_OUTSIDE_OVERLAY);
        newsWebView.getSettings().setBuiltInZoomControls(false);
        newsWebView.getSettings().setJavaScriptEnabled(true);
        newsWebView.setWebViewClient(new WebViewClient(){
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return true;
            }
        });
        textview_news_detail_title = (TextView)headerView.findViewById(R.id.textview_news_detail_title);
        initNewsOperaterView(headerView);
    }

    public void setDefaultReply()
    {
        editCommentET.setHint(getResources().getString(R.string.please_to_reply));
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
                if (editCommentET != null)
                {
                    editCommentET.setHint(strHint);
                    strReply = "<@@" + displayName + "," + id + "@>";
                    isReplayFollower = true;
                }
            }
        }
        openInputMethod();
    }

    public void openInputMethod()
    {
        InputTools.KeyBoard(editCommentET, "open");
    }

    @Override
    public void onBackPressed()
    {
        super.onBackPressed();
        if (isReplayFollower)
        {
            strReply = "";
            editCommentET.setText("");
            editCommentET.setHint(getResources().getString(R.string.please_to_reply));
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
        detachDiscussionFetch();
        unsetDiscussionEditMiddleCallback();
        ButterKnife.reset(this);
        super.onDestroyView();
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
                            showDeleteTimeLineConfirmDlg(dto.id, TimeLineItemDetailFragment.DIALOG_TYPE_DELETE_COMMENT);
                        }
                    }

                    @Override
                    public void onApplyClick()
                    {
                        timeLineCommentMenuDialog.dismiss();
                        AbstractDiscussionCompactDTO dto = adapter.getItem(position);
                        if (dto != null)
                        {
                            showDeleteTimeLineConfirmDlg(dto.id, TimeLineItemDetailFragment.DIALOG_TYPE_APPLY_COMMENT);
                        }
                    }
                }, false, isDeleteAllowed, isReportAllowed);
    }


    private void fetchComments(){
        if(discussionListKey == null) {
            discussionListKey = new PaginatedDiscussionListKey(DiscussionType.NEWS, (int)newsId, 1, TimeLineItemDetailFragment.ITEMS_PER_PAGE);
        }
        detachDiscussionFetch();
        discussionListCache.register(discussionListKey, this);
        discussionListCache.getOrFetchAsync(discussionListKey, true);
    }

    private void detachDiscussionFetch()
    {
        discussionListCache.unregister(this);
    }


    private void retrieveNewsDetail(){
        newsServiceWrapper.get().getNewsDetail(newsId, new Callback<NewsItemDTO>() {
            @Override
            public void success(NewsItemDTO newsItemDTO, Response response) {
                NewsDetailFragment.this.newsItemDTO = newsItemDTO;
                if (newsItemDTO != null && newsItemDTO.text != null && newsWebView != null && sendDiscussionRL != null) {
                    htmlContent = StringUtils.convertToHtmlFormat(newsItemDTO.text, (int) (dm.widthPixels / dm.density - marginWebView));
                    fetchComments();
                    newsWebView.loadData(htmlContent, "text/html; charset=UTF-8", null);
                    displayNewsVoteViews();
                    displayNewsCommentViews();
                    pullToRefreshListView.getRefreshableView().addHeaderView(headerView);
                    sendDiscussionRL.setVisibility(View.VISIBLE);
                } else {
                    sendDiscussionRL.setVisibility(View.GONE);
                }
                onFinish();
            }

            @Override
            public void failure(RetrofitError retrofitError) {
                THToast.show(new THException(retrofitError).getMessage());
                if (sendDiscussionRL != null) {
                    sendDiscussionRL.setVisibility(View.GONE);
                }
                onFinish();
            }

            private void onFinish(){
                finishLoadingProgressBar();
            }
        });
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain(getString(R.string.discovery_news_detail));
        setHeadViewRight0(getString(R.string.discovery_discuss_send_share));
    }

    @Override
    public void onClickHeadRight0()
    {
        shareToWechatMoment(newsTitle);
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
                String downloadCNTradeHeroWeibo = getString(R.string.download_tradehero_android_app_on_weibo);
                String outputStr = show;
                outputStr = WeiboUtils.getShareContentWeibo(outputStr, downloadCNTradeHeroWeibo);
                InviteFormDTO inviteFormDTO = new InviteFormWeiboDTO(outputStr);
                userServiceWrapper.get().inviteFriends(
                        currentUserId.toUserBaseKey(), inviteFormDTO, new Callback<Response>()
                        {
                            @Override
                            public void success(Response response, Response response2)
                            {

                            }

                            @Override
                            public void failure(RetrofitError retrofitError)
                            {

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

    @Override
    public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value) {
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
        if(pullToRefreshListView==null){
            return;
        }
        pullToRefreshListView.onRefreshComplete();
        if (adapter.getCount() >= TimeLineItemDetailFragment.ITEMS_PER_PAGE)
        {
            pullToRefreshListView.setMode(PullToRefreshBase.Mode.BOTH);
        }
        else
        {
            pullToRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        }
    }

    @Override
    public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error) {
        discussionListKey.setPage(1);
    }

    private boolean isDeleteAllowed(AbstractDiscussionCompactDTO dto)
    {
        if (dto == null)
        {
            return false;
        }
        int userId = currentUserId.toUserBaseKey().getUserId();
        UserBaseDTO userBaseDTO = null;
        if (dto instanceof TimelineItemDTO)
        {
            userBaseDTO = ((TimelineItemDTO) dto).user;
        }
        if (dto instanceof DiscussionDTO)
        {
            userBaseDTO = ((DiscussionDTO) dto).user;
        }
        if (userBaseDTO != null)
        {
            if (userId == userBaseDTO.id)
            {
                return true;
            }
            else
            {
                return false;
            }
        }
        return false;
    }

    private void showDeleteTimeLineConfirmDlg(final int itemId, int dialogType)
    {
        if (getActivity() == null)
        {
            return;
        }
        if (deleteOrApplyTimeLineConfirmDialog == null)
        {
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
            deleteOrApplyTLConfirmDlgCancelTV.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        if (deleteOrApplyTimeLineConfirmDialog.isShowing())
        {
            return;
        }
        if (dialogType == TimeLineItemDetailFragment.DIALOG_TYPE_DELETE_COMMENT)
        {
            deleteOrApplyTLConfirmDlgTitleTV.setText(getString(R.string.discovery_discuss_dlg_title_deletecomment));
            deleteOrApplyTLConfirmDlgOKTV.setOnClickListener(new View.OnClickListener()
            {
                @Override
                public void onClick(View view)
                {
                    deleteDiscussionItem(itemId);
                    deleteOrApplyTimeLineConfirmDialog.dismiss();
                }
            });
        }
        deleteOrApplyTimeLineConfirmDialog.show();
    }

    private void deleteDiscussionItem(final int discussionItemId)
    {
        showDeleteProgressDlg();
        discussionServiceWrapper.get().deleteDiscussionItem(discussionItemId, new Callback<Response>() {
            @Override
            public void success(Response response, Response response2) {
                if(adapter==null || newsItemDTO == null){
                    return;
                }
                adapter.removeDeletedItem(discussionItemId);
                newsItemDTO.commentCount--;
                if(newsItemDTO.commentCount < 0){
                    newsItemDTO.commentCount = 0;
                }
                displayNewsCommentViews();
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

    private void showDeleteProgressDlg()
    {
        progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.discovery_discuss_dlg_delete);
    }

    private void dismissProgressDlg()
    {
        if (getActivity() == null)
        {
            return;
        }
        progressDialogUtil.dismiss(getActivity());
    }

    private void sendReport(AbstractDiscussionCompactDTO dto, int position)
    {
        if (dto == null)
        {
            return;
        }
        DiscussReportDTO discussReportDTO = new DiscussReportDTO();
        if (dto instanceof TimelineItemDTO)
        {
            discussReportDTO.discussionType = ((TimelineItemDTO) dto).type;
        }
        else if (dto instanceof DiscussionDTO)
        {
            //The type of all DiscussionDTO is 1 when report.
            discussReportDTO.discussionType = 1;
        }
        else
        {
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
                THException thException = new THException(retrofitError);
                THToast.show(thException);
            }
        });
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
        DiscussionType discussionType = DiscussionType.NEWS;
        if (discussionType != null)
        {
            DiscussionFormDTO discussionFormDTO = discussionFormDTOFactory.createEmpty(discussionType);
            discussionFormDTO.inReplyToId = (int)newsId;
            discussionFormDTO.text = strReply + " " + editCommentET.getText().toString();

            return discussionFormDTO;
        }

        return null;
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.timeline_detail_llTLPraise)
        {
            clickPraiseUp();
            return;
        }
        if (view.getId() == R.id.timeline_detail_llTLPraiseDown)
        {
            clickPraiseDown();
            return;
        }
        if (view.getId() == R.id.timeline_detail_llTLComment)
        {
            if(newsItemDTO!=null) {
                comments(newsItemDTO);
            }
            return;
        }
    }

    private void comments(AbstractDiscussionCompactDTO dto)
    {
        DiscussionKey discussionKey = dto.getDiscussionKey();
        Bundle bundle = new Bundle();
        bundle.putBundle(DiscussionKey.BUNDLE_KEY_DISCUSSION_KEY_BUNDLE,
                discussionKey.getArgs());
        pushFragment(DiscussSendFragment.class, bundle);
    }

    private class SecurityDiscussionEditCallback implements Callback<DiscussionDTO>
    {
        @Override
        public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();
            if(editCommentET==null || getActivity()==null){
                return;
            }
            DeviceUtil.dismissKeyboard(getActivity());
            discussionListKey.setPage(1);
            fetchComments();
            if(newsItemDTO!=null){
                newsItemDTO.commentCount++;
                displayNewsCommentViews();
            }
            strReply = "";
            editCommentET.setText("");
        }

        @Override
        public void failure(RetrofitError error)
        {
            onFinish();
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
        return !editCommentET.getText().toString().trim().isEmpty();
    }

    private void initNewsOperaterView(View parentView){
        timeline_detail_llTLPraise = (LinearLayout)parentView.findViewById(R.id.timeline_detail_llTLPraise);
        timeline_detail_btnTLPraise = (TextView)parentView.findViewById(R.id.timeline_detail_btnTLPraise);
        timeline_detail_tvTLPraise = (TextView)parentView.findViewById(R.id.timeline_detail_tvTLPraise);
        timeline_detail_llTLPraiseDown = (LinearLayout)parentView.findViewById(R.id.timeline_detail_llTLPraiseDown);
        timeline_detail_btnTLPraiseDown = (TextView)parentView.findViewById(R.id.timeline_detail_btnTLPraiseDown);
        timeline_detail_tvTLPraiseDown = (TextView)parentView.findViewById(R.id.timeline_detail_tvTLPraiseDown);
        timeline_detail_llTLComment = (LinearLayout)parentView.findViewById(R.id.timeline_detail_llTLComment);
        timeline_detail_tvTLComment = (TextView)parentView.findViewById(R.id.timeline_detail_tvTLComment);

        timeline_detail_llTLPraise.setOnClickListener(this);
        timeline_detail_llTLPraiseDown.setOnClickListener(this);
        timeline_detail_llTLComment.setOnClickListener(this);
    }

    private void displayNewsVoteViews(){
        if(newsItemDTO==null){
            return;
        }
        timeline_detail_btnTLPraise.setBackgroundResource(newsItemDTO.voteDirection == 1 ? R.drawable.icon_praise_active : R.drawable.icon_praise_normal);
        timeline_detail_btnTLPraiseDown.setBackgroundResource(newsItemDTO.voteDirection == -1 ? R.drawable.icon_praise_down_active : R.drawable.icon_praise_down_normal);
        timeline_detail_tvTLPraise.setText(String.valueOf(newsItemDTO.upvoteCount));
        timeline_detail_tvTLPraiseDown.setText(String.valueOf(newsItemDTO.downvoteCount));
    }

    private void displayNewsCommentViews(){
        if(newsItemDTO==null){
            return;
        }
        timeline_detail_tvTLComment.setText(String.valueOf(newsItemDTO.commentCount));
    }


    private void clickPraiseUp(){
        if(newsItemDTO.voteDirection == 0){
            newsItemDTO.voteDirection = 1;
            newsItemDTO.upvoteCount++;
            updateVoting(VoteDirection.UpVote, newsItemDTO);
        } else if(newsItemDTO.voteDirection == -1){
            newsItemDTO.voteDirection = 1;
            newsItemDTO.upvoteCount++;
            newsItemDTO.downvoteCount--;
            if(newsItemDTO.downvoteCount<0){
                newsItemDTO.downvoteCount = 0;
            }
            updateVoting(VoteDirection.UpVote, newsItemDTO);
        } else if(newsItemDTO.voteDirection==1){
            newsItemDTO.voteDirection = 0;
            newsItemDTO.upvoteCount--;
            if(newsItemDTO.upvoteCount < 0){
                newsItemDTO.upvoteCount = 0;
            }
            updateVoting(VoteDirection.UnVote, newsItemDTO);
        }
        displayNewsVoteViews();
        timeline_detail_btnTLPraise.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_praise));
    }

    private void clickPraiseDown(){
        if(newsItemDTO.voteDirection ==0){
            newsItemDTO.voteDirection = -1;
            newsItemDTO.downvoteCount++;
            updateVoting(VoteDirection.DownVote, newsItemDTO);
        }else if(newsItemDTO.voteDirection ==1){
            newsItemDTO.voteDirection = -1;
            newsItemDTO.downvoteCount++;
            newsItemDTO.upvoteCount--;
            if(newsItemDTO.upvoteCount < 0){
                newsItemDTO.upvoteCount = 0;
            }
            updateVoting(VoteDirection.DownVote, newsItemDTO);
        }else if(newsItemDTO.voteDirection == -1){
            newsItemDTO.voteDirection = 0;
            newsItemDTO.downvoteCount--;
            if(newsItemDTO.downvoteCount <0){
                newsItemDTO.downvoteCount = 0 ;
            }
            updateVoting(VoteDirection.UnVote, newsItemDTO);
        }
        displayNewsVoteViews();
        timeline_detail_btnTLPraiseDown.startAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.vote_ani));
    }

    private void updateVoting(VoteDirection voteDirection, AbstractDiscussionCompactDTO discussionDTO)
    {
        if (discussionDTO == null)
        {
            return;
        }

        DiscussionVoteKey discussionVoteKey = new DiscussionVoteKey(
                DiscussionType.NEWS,
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

    private void showLoadingProgressBar(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(mTradeHeroProgressBar!=null) {
                    mTradeHeroProgressBar.setVisibility(View.VISIBLE);
                    mTradeHeroProgressBar.startLoading();
                }
            }
        });
    }

    private void finishLoadingProgressBar(){
        Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if(mTradeHeroProgressBar!=null) {
                    mTradeHeroProgressBar.stopLoading();
                    mTradeHeroProgressBar.setVisibility(View.GONE);
                }
            }
        });
    }
}
