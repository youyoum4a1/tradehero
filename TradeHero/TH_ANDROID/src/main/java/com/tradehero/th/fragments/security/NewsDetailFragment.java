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
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.SecurityItemViewAdapterFactory;
import com.tradehero.th.fragments.news.NewsDetailFullView;
import com.tradehero.th.fragments.news.NewsDetailSummaryView;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.network.retrofit.MiddleCallback;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.utils.FontUtil;
import com.tradehero.th.widget.VotePair;
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

    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;

    private NewsItemDTO mSummaryNewsItemDTO;
    private NewsItemDTO mDetailNewsItemDto;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject FontUtil fontUtil;

    @InjectView(R.id.news_detail_summary) NewsDetailSummaryView newsDetailSummaryView;
    @InjectView(R.id.news_detail_full) NewsDetailFullView newsDetailFullView;

    // Action buttons
    @InjectView(R.id.vote_pair) VotePair votePair;
    @InjectView(R.id.news_action_button_comment) TextView mNewsActionButtonCommentWrapper;
    @InjectView(R.id.news_action_tv_more) TextView mNewsActionTvMore;

    // Comment list
    @InjectView(R.id.news_detail_comment_list) ListView mNewsDetailCommentList;
    @InjectView(R.id.news_detail_comment_empty) TextView mNewsDetailCommentEmpty;

    private MiddleCallback<NewsItemDTO> newsServiceCallback;

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
        View view = inflater.inflate(R.layout.news_detail_view, container, false);
        ButterKnife.inject(this, view);
        initViews(view);
        return view;
    }

    @OnClick(R.id.news_action_tv_more) void onClickTvMore(View v)
    {
        showShareDialog();
    }

    @Override public void onResume()
    {
        super.onResume();

        linkWith();

        // TODO have to remove this hack, please!
        int bgRes = getArguments().getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        if (bgRes != 0)
        {
            newsDetailSummaryView.setBackgroundResource(bgRes);
        }
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
    }

    private void linkWith()
    {
        Bundle args = getArguments();
        mSummaryNewsItemDTO = NewsItemDTO.getSampleNewsItemDTO(args);

        votePair.display(mSummaryNewsItemDTO);
        newsDetailSummaryView.display(mSummaryNewsItemDTO);

        newsServiceCallback = newsServiceWrapper.getSecurityNewsDetail(mSummaryNewsItemDTO.id, createNewsDetailCallback());
    }

    private void linkWith(NewsItemDTO newsItemDTO)
    {
        mDetailNewsItemDto = newsItemDTO;
        newsDetailFullView.display(mDetailNewsItemDto);
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
        ((NewsDialogLayout) contentView).setNewsData(mDetailNewsItemDto == null ? mSummaryNewsItemDTO : mDetailNewsItemDto, false);
        THDialog.showUpDialog(getSherlockActivity(), contentView, callback);
    }

    @Override
    public boolean isTabBarVisible()
    {
        return false;
    }
}
