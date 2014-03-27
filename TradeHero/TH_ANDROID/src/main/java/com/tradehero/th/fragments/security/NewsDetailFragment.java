package com.tradehero.th.fragments.security;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.Optional;
import com.tradehero.common.widget.BetterViewAnimator;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.adapters.LoaderDTOAdapter;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.news.NewsItemDTOKey;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.discussion.DiscussionListAdapter;
import com.tradehero.th.fragments.news.NewsDetailFullView;
import com.tradehero.th.fragments.news.NewsDetailSummaryView;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.news.NewsDiscussionListLoader;
import com.tradehero.th.loaders.ListLoader;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.utils.FontUtil;
import com.tradehero.th.widget.VotePair;
import java.util.List;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alex & Liang on 14-3-10.
 */
public class NewsDetailFragment extends DashboardFragment /*AbstractSecurityInfoFragment*/
{
    public static final String BUNDLE_KEY_TITLE_BACKGROUND_RES = NewsDetailFragment.class.getName() + ".title_bg";

    private NewsItemDTO mSummaryNewsItemDTO;
    private NewsItemDTO mDetailNewsItemDTO;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject FontUtil fontUtil;

    @InjectView(R.id.news_detail_summary) NewsDetailSummaryView newsDetailSummaryView;
    @InjectView(R.id.news_detail_full) NewsDetailFullView newsDetailFullView;

    // Action buttons
    @InjectView(R.id.vote_pair) VotePair votePair;
    @InjectView(R.id.news_action_button_comment) TextView mNewsActionButtonCommentWrapper;
    @InjectView(R.id.news_action_tv_more) TextView mNewsActionTvMore;

    // Comment list
    @InjectView(R.id.news_comment_list_wrapper) @Optional BetterViewAnimator mNewsCommentListWrapper;
    @InjectView(R.id.news_detail_comment_list) @Optional ListView mNewsDetailCommentList;
    @InjectView(R.id.news_detail_comment_empty) @Optional TextView mNewsDetailCommentEmpty;

    private MiddleCallback<NewsItemDTO> newsServiceCallback;
    private DiscussionListAdapter discussionAdapter;
    private NewsItemDTOKey newsItemDTOKey;
    private int commentListWrapperDisplayedChildId;

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        getSherlockActivity().getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View listViewWrapper = inflater.inflate(R.layout.news_detail_view, container, false);
        View listHeaderWrapper = inflater.inflate(R.layout.news_detail_view_header, null);
        ButterKnife.inject(this, listHeaderWrapper);
        initViews(listViewWrapper);
        mNewsDetailCommentList.addHeaderView(listHeaderWrapper);
        return listViewWrapper;
    }

    @OnClick(R.id.news_action_tv_more) void onClickTvMore(View v)
    {
        showShareDialog();
    }

    @Override public void onResume()
    {
        super.onResume();

        if (commentListWrapperDisplayedChildId != 0)
        {
            mNewsCommentListWrapper.setDisplayedChildByLayoutId(commentListWrapperDisplayedChildId);
        }

        //if (newsItemDTOKey == null)
        //{
        //    newsItemDTOKey = new NewsItemDTOKey(getArguments());
        //}

        linkWith();

        // TODO have to remove this hack, please!
        int bgRes = getArguments().getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        if (bgRes != 0)
        {
            newsDetailSummaryView.setBackgroundResource(bgRes);
        }

        discussionAdapter = createDiscussionAdapter();
        mNewsDetailCommentList.setAdapter(discussionAdapter);
        mNewsDetailCommentList.setEmptyView(mNewsDetailCommentEmpty);

        getActivity().getSupportLoaderManager()
                .initLoader(discussionAdapter.getLoaderId(), null,
                        discussionAdapter.getLoaderCallback());
    }

    @Override public void onPause()
    {
        super.onPause();

        commentListWrapperDisplayedChildId = mNewsCommentListWrapper.getDisplayedChildLayoutId();
    }

    private DiscussionListAdapter createDiscussionAdapter()
    {
        // TODO, following code looks ugly coz of all the hacks :(, remove hacks
        int loaderId = 0;
        if (mSummaryNewsItemDTO != null)
        {
            loaderId = mSummaryNewsItemDTO.id;
        }
        else if (mDetailNewsItemDTO != null)
        {
            loaderId = mDetailNewsItemDTO.id;
        }

        DiscussionListAdapter adapter = new DiscussionListAdapter(getActivity(), getActivity().getLayoutInflater(),
                loaderId, R.layout.news_discussion_comment_item);
        adapter.setDTOLoaderCallback(new LoaderDTOAdapter.ListLoaderCallback<DiscussionDTO>()
        {
            @Override protected void onLoadFinished(ListLoader<DiscussionDTO> loader, List<DiscussionDTO> data)
            {
                mNewsCommentListWrapper.setDisplayedChildByLayoutId(R.id.news_detail_comment_list);
                //if (discussionStatus != null)
                //{
                //    int statusResource = discussionListAdapter.getCount() != 0 ? R.string.discussion_loaded : R.string.discussion_empty;
                //    discussionStatus.setText(getString(statusResource));
                //}
            }

            @Override protected ListLoader<DiscussionDTO> onCreateLoader(Bundle args)
            {
                return createNewsDiscussionLoader();
            }
        });
        return adapter;
    }

    private ListLoader<DiscussionDTO> createNewsDiscussionLoader()
    {
        return new NewsDiscussionListLoader(getActivity(), newsItemDTOKey);
    }

    @Override public void onDestroy()
    {
        if (newsServiceCallback != null)
        {
            newsServiceCallback.setPrimaryCallback(null);
        }
        super.onDestroy();
    }

    private void initViews(View view)
    {
        fontUtil.setTypeFace(mNewsActionTvMore, FontUtil.FontType.AWESOME);
        mNewsCommentListWrapper = (BetterViewAnimator) view.findViewById(R.id.news_comment_list_wrapper);
        mNewsDetailCommentList = (ListView) view.findViewById(R.id.news_detail_comment_list);
        mNewsDetailCommentEmpty = (TextView) view.findViewById(R.id.news_detail_comment_empty);
    }

    private void linkWith()
    {
        Bundle args = getArguments();
        mSummaryNewsItemDTO = NewsItemDTO.getSampleNewsItemDTO(args);
        newsItemDTOKey = new NewsItemDTOKey(mSummaryNewsItemDTO.id);

        votePair.display(mSummaryNewsItemDTO);
        newsDetailSummaryView.display(mSummaryNewsItemDTO);

        newsServiceCallback = newsServiceWrapper.getSecurityNewsDetail(mSummaryNewsItemDTO.id, createNewsDetailCallback());
    }

    private void linkWith(NewsItemDTO newsItemDTO)
    {
        mDetailNewsItemDTO = newsItemDTO;
        newsDetailFullView.display(mDetailNewsItemDTO);
    }

    private Callback<NewsItemDTO> createNewsDetailCallback()
    {
        return new Callback<NewsItemDTO>()
        {
            @Override
            public void success(NewsItemDTO newsItemDTO, Response response)
            {
                linkWith(newsItemDTO);
            }

            @Override
            public void failure(RetrofitError error)
            {
            }
        };
    }

    private void showShareDialog()
    {
        View contentView = LayoutInflater.from(getSherlockActivity()).inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(mDetailNewsItemDTO == null ? mSummaryNewsItemDTO : mDetailNewsItemDTO, false);
        THDialog.showUpDialog(getSherlockActivity(), contentView, callback);
    }

    @Override
    public boolean isTabBarVisible()
    {
        return false;
    }
}
