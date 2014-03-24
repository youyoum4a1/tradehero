package com.tradehero.th.fragments.security;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.SecurityItemViewAdapterFactory;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alex on 14-3-10.
 */
public class NewsDetailFragment extends DashboardFragment /*AbstractSecurityInfoFragment*/
{

    public static final String BUNDLE_KEY_TITLE_BACKGROUND_RES = NewsDetailFragment.class.getName() + ".title_bg";

    SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;

    NewsItemDTO sampleItemDto;
    NewsItemDTO detailItemDto;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;
    
    @InjectView(R.id.news_detail_title_placeholder) ImageView mNewsDetailTitlePlaceholder;
    @InjectView(R.id.news_detail_title) TextView mNewsDetailTitle;
    @InjectView(R.id.news_detail_date) TextView mNewsDetailDate;
    @InjectView(R.id.news_detail_title_layout_wrapper) LinearLayout mNewsDetailTitleLayoutWrapper;
    @InjectView(R.id.new_action_iv_like) ImageView mNewActionIvLike;
    @InjectView(R.id.new_action_tv_like) TextView mNewActionTvLike;
    @InjectView(R.id.news_action_button_like_wrapper) LinearLayout mNewsActionButtonLikeWrapper;
    @InjectView(R.id.news_action_tv_comment) TextView mNewsActionTvComment;
    @InjectView(R.id.news_action_button_comment_wrapper) LinearLayout mNewsActionButtonCommentWrapper;
    @InjectView(R.id.news_action_tv_more) TextView mNewsActionTvMore;
    @InjectView(R.id.news_action_button_more_wrapper) LinearLayout mNewsActionButtonMoreWrapper;
    @InjectView(R.id.timeline_share_buttons) LinearLayout mTimelineShareButtons;
    @InjectView(R.id.news_detail_desc) TextView mNewsDetailDesc;
    @InjectView(R.id.news_detail_content) TextView mNewsDetailContent;
    @InjectView(R.id.news_detail_loading) TextView mNewsDetailLoading;
    @InjectView(R.id.news_detail_reference_gv) GridView mNewsDetailReferenceGv;
    @InjectView(R.id.news_detail_reference_gv_container) LinearLayout mNewsDetailReferenceGvContainer;
    @InjectView(R.id.news_detail_comment_list) ListView mNewsDetailCommentList;
    @InjectView(R.id.news_detail_comment_empty) TextView mNewsDetailCommentEmpty;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        return super.onOptionsItemSelected(item);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View view = inflater.inflate(R.layout.news_detail_view, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        linkWith();
        return view;
    }

    @OnClick(R.id.news_action_button_like_wrapper) void onClickLike(View v)
    {
        voteUpOrDown(sampleItemDto.voteDirection == 1 ? false : true);
    }

    @OnClick(R.id.news_action_tv_more) void onClickTvMore(View v)
    {
        showShareDialog();
    }

    private void initViews(View view)
    {
        Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "FontAwesome.ttf");
        mNewsActionTvMore.setTypeface(font);
        mNewsActionTvMore.setText("\uf141");

        simpleSecurityItemViewAdapter =
                new SimpleSecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);
        mNewsDetailReferenceGv.setAdapter(simpleSecurityItemViewAdapter);
        mNewsDetailReferenceGv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Bundle args = new Bundle();
                args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE,
                        ((SecurityCompactDTO) simpleSecurityItemViewAdapter.getItem(
                                position)).getSecurityId().getArgs());
                getNavigator().pushFragment(BuySellFragment.class, args);
            }
        });
    }

    private void linkWith()
    {
        Bundle args = getArguments();
        this.sampleItemDto = NewsItemDTO.getSampleNewsItemDTO(args);
        mNewsDetailTitle.setText(sampleItemDto.title);
        PrettyTime prettyTime = new PrettyTime();
        mNewsDetailDate.setText(prettyTime.format(sampleItemDto.createdAtUtc));

        int bgRes = args.getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        mNewsDetailTitlePlaceholder.setImageResource(bgRes);

        newsServiceWrapper.getSecurityNewsDetail(sampleItemDto.id, createNewsDetailCallback());
        //TODO change to R.string
        mNewActionTvLike.setText(sampleItemDto.voteDirection == 1 ? "Unlike" : "Like");
        mNewActionIvLike.setImageResource(sampleItemDto.voteDirection == 1 ? R.drawable.icn_actions_downvote : R.drawable.icn_actions_upvote);
    }

    private void fillDetailData(NewsItemDTO data)
    {
        this.detailItemDto = data;
        mNewsDetailContent.setText(detailItemDto.text);
        mNewsDetailContent.setVisibility(View.VISIBLE);
        mNewsDetailLoading.setVisibility(View.GONE);

        securityServiceWrapper.getMultipleSecurities2(createNewsDetailSecurityCallback(),
                detailItemDto.getSecurityIds());
    }

    private Callback<NewsItemDTO> createNewsDetailCallback()
    {

        return new Callback<NewsItemDTO>()
        {
            @Override
            public void success(NewsItemDTO newsItemDTO, Response response)
            {
                fillDetailData(newsItemDTO);
            }

            @Override
            public void failure(RetrofitError error)
            {
            }
        };
    }

    private Callback<List<SecurityCompactDTO>> createNewsDetailSecurityCallback()
    {

        return new Callback<List<SecurityCompactDTO>>()
        {
            @Override
            public void success(List<SecurityCompactDTO> securityCompactDTOList, Response response)
            {
                ViewGroup.LayoutParams lp = mNewsDetailReferenceGvContainer.getLayoutParams();
                //TODO it changes with solution
                lp.width = 510 * securityCompactDTOList.size();
                mNewsDetailReferenceGvContainer.setLayoutParams(lp);
                mNewsDetailReferenceGv.setNumColumns(securityCompactDTOList.size());
                simpleSecurityItemViewAdapter.setItems(securityCompactDTOList);
                simpleSecurityItemViewAdapter.notifyDataSetChanged();
            }

            @Override
            public void failure(RetrofitError error)
            {
            }
        };
    }

    @Override
    public boolean isTabBarVisible()
    {
        return false;
    }

    private void voteUpOrDown(boolean towardUp)
    {
        int id = sampleItemDto.id;
        VoteDirection direction = towardUp ? VoteDirection.UpVote : VoteDirection.Unvote;
        DiscussionVoteKey key = new DiscussionVoteKey(DiscussionType.NEWS, id, direction);
        discussionServiceWrapperLazy.get().vote(key, createVoteCallback(towardUp));
    }

    private Callback<DiscussionDTO> createVoteCallback(final boolean towardUp)
    {

        return new Callback<DiscussionDTO>()
        {
            //TODO change to R.string
            @Override
            public void success(DiscussionDTO discussionDTO, Response response)
            {
                THToast.show("vote " + ((towardUp ? "up" : "down")) + " success");
                mNewActionTvLike.setText(towardUp ? "Unlike" : "Like");
                mNewActionIvLike.setImageResource(towardUp ? R.drawable.icn_actions_downvote : R.drawable.icn_actions_upvote);
                sampleItemDto.voteDirection = towardUp ? 1 : 0;
            }

            @Override
            public void failure(RetrofitError error)
            {
                THToast.show("vote " + ((towardUp ? "up" : "down")) + " error");
            }
        };
    }

    private void showShareDialog()
    {
        View contentView = LayoutInflater.from(getSherlockActivity()).inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(detailItemDto == null ? sampleItemDto : detailItemDto, false);
        THDialog.showUpDialog(getSherlockActivity(), contentView, callback);
    }
}
