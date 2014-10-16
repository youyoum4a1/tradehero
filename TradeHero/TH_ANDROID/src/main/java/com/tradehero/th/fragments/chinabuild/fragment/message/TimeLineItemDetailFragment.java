package com.tradehero.th.fragments.chinabuild.fragment.message;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.squareup.picasso.Picasso;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.SecurityTimeLineDiscussOrNewsAdapter;
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
import com.tradehero.th.api.timeline.TimelineItemDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.chinabuild.dialog.ShareSheetDialogLayout;
import com.tradehero.th.fragments.chinabuild.fragment.userCenter.UserMainPage;
import com.tradehero.th.fragments.chinabuild.listview.SecurityListView;
import com.tradehero.th.misc.exception.THException;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.discussion.DiscussionCache;
import com.tradehero.th.persistence.discussion.DiscussionListCacheNew;
import com.tradehero.th.persistence.prefs.ShareSheetTitleCache;
import com.tradehero.th.utils.DeviceUtil;
import com.tradehero.th.utils.ProgressDialogUtil;
import dagger.Lazy;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class TimeLineItemDetailFragment extends DashboardFragment implements DiscussionListCacheNew.DiscussionKeyListListener,View.OnClickListener
{

    public static final String BUNDLE_ARGUMENT_DISCUSSTION_ID = "bundle_argment_discusstion_id";

    @Inject protected DiscussionCache discussionCache;
    private DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO> discussionFetchListener;
    DiscussionKey timelineItemDTOKey;
    private PaginatedDiscussionListKey discussionListKey;
    @Inject DiscussionListCacheNew discussionListCache;

    //@InjectView(R.id.tvTimeLineDetailContent) TextView tvTimeLineDetailContent;
    @InjectView(R.id.btnSend) Button btnSend;
    @InjectView(R.id.edtSend) EditText edtSend;

    private SecurityTimeLineDiscussOrNewsAdapter adapter;
    @InjectView(R.id.listTimeLine) SecurityListView listTimeLine;

    @Inject DiscussionKeyFactory discussionKeyFactory;

    private MiddleCallback<DiscussionDTO> discussionEditMiddleCallback;
    private ProgressDialog progressDialog;
    @Inject ProgressDialogUtil progressDialogUtil;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject DiscussionFormDTOFactory discussionFormDTOFactory;

    AbstractDiscussionCompactDTO dataDto;

    @Inject public Lazy<PrettyTime> prettyTime;
    @Inject Picasso picasso;
    private MiddleCallback<DiscussionDTO> voteCallback;
    private Dialog mShareSheetDialog;
    @Inject @ShareSheetTitleCache StringPreference mShareSheetTitleCache;

    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(R.id.bvaViewAll) BetterViewAnimator betterViewAnimator;
    @InjectView(R.id.rlAllView) RelativeLayout rlAllView;
    //@InjectView(R.id.tvEmpty) TextView tvEmpty;

    //@InjectView(R.id.llDisscurssOrNews)
    LinearLayout llDisscurssOrNews;
    //@InjectView(R.id.imgSecurityTLUserHeader)
    ImageView imgSecurityTLUserHeader;
    //@InjectView(R.id.tvUserTLTimeStamp)
    TextView tvUserTLTimeStamp;
    //@InjectView(R.id.tvUserTLContent)
    TextView tvUserTLContent;
    //@InjectView(R.id.tvUserTLName)
    TextView tvUserTLName;
    //@InjectView(R.id.llTLPraise)
    LinearLayout llTLPraise;
    //@InjectView(R.id.llTLComment)
    LinearLayout llTLComment;
    //@InjectView(R.id.llTLShare)
    LinearLayout llTLShare;
    //@InjectView(R.id.tvTLPraise)
    TextView tvTLPraise;
    //@InjectView(R.id.tvTLComment)
    TextView tvTLComment;
    //@InjectView(R.id.tvTLShare)
    TextView tvTLShare;

    private LinearLayout mRefreshView;


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        discussionFetchListener = createDiscussionCacheListener();
        initArgment();
        adapter = new SecurityTimeLineDiscussOrNewsAdapter(getActivity(), true);
    }

    public void initArgment()
    {
        Bundle bundle = getArguments();
        if (bundle.containsKey(BUNDLE_ARGUMENT_DISCUSSTION_ID))
        {
            timelineItemDTOKey = discussionKeyFactory.fromBundle(bundle.getBundle(BUNDLE_ARGUMENT_DISCUSSTION_ID));
            fetchDiscussion(timelineItemDTOKey, false);
            discussionListKey = new PaginatedDiscussionListKey(timelineItemDTOKey.getType(), timelineItemDTOKey.id, 1, 1000);
            fetchDiscussList(true);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
        setHeadViewMiddleMain("详情");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.timeline_item_detail, container, false);
        ButterKnife.inject(this, view);

        ListView lv = listTimeLine.getRefreshableView();
        mRefreshView = (LinearLayout) inflater.inflate(R.layout.security_time_line_item, null);
        lv.addHeaderView(mRefreshView);
        //llDisscurssOrNews.setLayoutParams(new ListView.LayoutParams(AbsoluteLayout.LayoutParams.FILL_PARENT,ListView.LayoutParams.FILL_PARENT));
        initRoot(mRefreshView);

        initView();

        if (dataDto == null)
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.progress);
        }
        else
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
        }

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
        llTLComment = (LinearLayout) view.findViewById(R.id.llTLComment);
        llTLShare = (LinearLayout) view.findViewById(R.id.llTLShare);
        tvTLPraise = (TextView) view.findViewById(R.id.tvTLPraise);
        tvTLComment = (TextView) view.findViewById(R.id.tvTLComment);
        tvTLShare = (TextView) view.findViewById(R.id.tvTLShare);
        llTLPraise.setOnClickListener(this);
        llTLComment.setOnClickListener(this);
        llTLShare.setOnClickListener(this);
    }



    public void initView()
    {
        tvUserTLContent.setMaxLines(1000);
        //listTimeLine.setEmptyView(tvEmpty);
        listTimeLine.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        listTimeLine.setAdapter(adapter);

        listTimeLine.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>()
        {
            @Override public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("下拉刷新");
                fetchDiscussList(true);
            }

            @Override public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView)
            {
                Timber.d("上拉加载更多");
                //refreshDataMore(false);
            }
        });
    }

    @Override public void onStop()
    {
        super.onStop();
    }

    @Override public void onDestroyView()
    {
        detachDiscussionFetchTask();
        detachDiscussionFetch();
        unsetDiscussionEditMiddleCallback();
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
        fetchDiscussion(timelineItemDTOKey, false);
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

    @Override public void onClick(View view)
    {
        onOperaterClicked(view);
    }

    protected class PrivateDiscussionViewDiscussionCacheListener implements DTOCacheNew.Listener<DiscussionKey, AbstractDiscussionCompactDTO>
    {
        @Override public void onDTOReceived(@NotNull DiscussionKey key, @NotNull AbstractDiscussionCompactDTO value)
        {
            //linkWithInitiating((PrivateDiscussionDTO) value, true);
            linkWithDTO(value);
            OnFinish();
        }

        @Override public void onErrorThrown(@NotNull DiscussionKey key, @NotNull Throwable error)
        {
            //THToast.show(R.string.error_fetch_private_message_initiating_discussion);
            Timber.d("");
            OnFinish();
        }

        public void OnFinish()
        {
            betterViewAnimator.setDisplayedChildByLayoutId(R.id.rlAllView);
        }
    }

    public void linkWithDTO(AbstractDiscussionCompactDTO value)
    {
        this.dataDto = value;
        fetchDiscussList(true);
        displayDiscussOrNewsDTO();
        //if (value instanceof TimelineItemDTO)
        //{
        //    tvTimeLineDetailContent.setText(((TimelineItemDTO) value).text);
        //}
        //else if (value instanceof NewsItemCompactDTO)
        //{
        //    tvTimeLineDetailContent.setText(((NewsItemCompactDTO) value).description);
        //}
        //else if (value instanceof DiscussionDTO)
        //{
        //    tvTimeLineDetailContent.setText(((DiscussionDTO) value).text);
        //}
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
                tvUserTLName.setText(((DiscussionDTO) dto).user.displayName);
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
                tvUserTLName.setText(((TimelineItemDTO) dto).user.displayName);
                tvUserTLContent.setText(((TimelineItemDTO) dto).text);
                picasso.load(((TimelineItemDTO) dto).user.picture)
                        .placeholder(R.drawable.superman_facebook)
                        .error(R.drawable.superman_facebook)
                        .into(imgSecurityTLUserHeader);
            }

            tvTLComment.setText("" + dto.commentCount);
            tvTLPraise.setText(dto.getVoteString());
        }
    }

    @Override public void onDTOReceived(@NotNull DiscussionListKey key, @NotNull DiscussionKeyList value)
    {
        List<AbstractDiscussionCompactDTO> listData = new ArrayList<>();
        for (int i = 0; i < value.size(); i++)
        {
            AbstractDiscussionCompactDTO dto = discussionCache.get(value.get(i));
            listData.add(dto);
        }
        adapter.setListData(listData);
        listTimeLine.onRefreshComplete();
    }

    @Override public void onErrorThrown(@NotNull DiscussionListKey key, @NotNull Throwable error)
    {
        listTimeLine.onRefreshComplete();
    }

    @OnClick(R.id.btnSend)
    public void OnSendClicked()
    {
        Timber.d("OnSendClicked!!");
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
            progressDialog = progressDialogUtil.show(getActivity(), R.string.alert_dialog_please_wait, R.string.processing);
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
            discussionFormDTO.text = edtSend.getText().toString();
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
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            onFinish();
            DeviceUtil.dismissKeyboard(getActivity());
            fetchDiscussList(true);
        }

        @Override public void failure(RetrofitError error)
        {
            onFinish();
            THToast.show(new THException(error));
        }

        private void onFinish()
        {
            if (progressDialog != null)
            {
                progressDialog.hide();
            }
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

    //@OnClick({R.id.llTLComment, R.id.llTLPraise, R.id.llTLShare, R.id.llDisscurssOrNews, R.id.imgSecurityTLUserHeader})
    public void onOperaterClicked(View view)
    {
        //if(view.getId() == R.id.llDisscurssOrNews)
        //{
        //    enterTimeLineDetail(getAbstractDiscussionCompactDTO());
        //}
        //else
        if (view.getId() == R.id.imgSecurityTLUserHeader)
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
        else if (view.getId() == R.id.llTLComment)
        {
            AbstractDiscussionCompactDTO dto = getAbstractDiscussionCompactDTO();
            comments(dto);
        }
        else if (view.getId() == R.id.llTLShare)
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
            share(strShare);
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
        updateVoting((item.voteDirection == 0) ? VoteDirection.UpVote : VoteDirection.UnVote, item);

        if (item.voteDirection == 0)
        {
            item.voteDirection = 1;
            item.upvoteCount += 1;
        }
        else
        {
            item.voteDirection = 0;
            item.upvoteCount = item.upvoteCount > 0 ? (item.upvoteCount - 1) : 0;
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
                    @Override public void onShareRequestedClicked()
                    {

                    }
                });
        mShareSheetDialog = THDialog.showUpDialog(getActivity(), contentView);
    }

    private void openUserProfile(int userId)
    {
        if (userId >= 0)
        {
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
        //<editor-fold desc="Constructors">
        public VoteCallback(VoteDirection voteDirection)
        {
        }
        //</editor-fold>

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            Timber.d("VoteCallback success");
        }

        @Override public void failure(RetrofitError error)
        {
            Timber.d("VoteCallback failed :" + error.toString());
        }
    }
}
