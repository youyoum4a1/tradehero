package com.tradehero.th.fragments.security;

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
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.security.SecurityCompactDTO;
import com.tradehero.th.fragments.base.DashboardFragment;
import com.tradehero.th.fragments.competition.SecurityItemViewAdapterFactory;
import com.tradehero.th.fragments.news.NewsDialogLayout;
import com.tradehero.th.fragments.trade.BuySellFragment;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.NewsServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.utils.FontUtil;
import com.tradehero.th.widget.VotePair;
import dagger.Lazy;
import java.util.List;
import javax.inject.Inject;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

/**
 * Created by Alex & Liang on 14-3-10.
 */
public class NewsDetailFragment extends DashboardFragment /*AbstractSecurityInfoFragment*/
{
    public static final String BUNDLE_KEY_TITLE_BACKGROUND_RES = NewsDetailFragment.class.getName() + ".title_bg";

    private SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;

    private NewsItemDTO mSummaryNewsItemDTO;
    private NewsItemDTO mDetailNewsItemDto;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;
    @Inject FontUtil fontUtil;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;

    @InjectView(R.id.news_detail_title_placeholder) ImageView mNewsDetailTitlePlaceholder;
    @InjectView(R.id.news_detail_title) TextView mNewsDetailTitle;
    @InjectView(R.id.news_detail_date) TextView mNewsDetailDate;
    @InjectView(R.id.news_detail_title_layout_wrapper) LinearLayout mNewsDetailTitleLayoutWrapper;
    @InjectView(R.id.vote_pair) VotePair votePair;
    @InjectView(R.id.news_action_button_comment) TextView mNewsActionButtonCommentWrapper;
    @InjectView(R.id.news_action_tv_more) TextView mNewsActionTvMore;
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
    }

    private void initViews(View view)
    {
        fontUtil.setTypeFace(mNewsActionTvMore, FontUtil.FontType.AWESOME);

        simpleSecurityItemViewAdapter = new SimpleSecurityItemViewAdapter(
                getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);
        mNewsDetailReferenceGv.setAdapter(simpleSecurityItemViewAdapter);
        mNewsDetailReferenceGv.setOnItemClickListener(new AdapterView.OnItemClickListener()
        {
            @Override public void onItemClick(AdapterView<?> adapterView, View view, int position, long l)
            {
                Object item = simpleSecurityItemViewAdapter.getItem(position);

                if (item instanceof SecurityCompactDTO)
                {
                    SecurityCompactDTO securityCompactDTO = (SecurityCompactDTO) item;
                    Bundle args = new Bundle();
                    args.putBundle(BuySellFragment.BUNDLE_KEY_SECURITY_ID_BUNDLE, securityCompactDTO.getSecurityId().getArgs());
                    getNavigator().pushFragment(BuySellFragment.class, args);
                }
            }
        });
    }

    private void linkWith()
    {
        Bundle args = getArguments();
        mSummaryNewsItemDTO = NewsItemDTO.getSampleNewsItemDTO(args);
        mNewsDetailTitle.setText(mSummaryNewsItemDTO.title);
        PrettyTime prettyTime = new PrettyTime();
        mNewsDetailDate.setText(prettyTime.format(mSummaryNewsItemDTO.createdAtUtc));

        int bgRes = args.getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        mNewsDetailTitlePlaceholder.setImageResource(bgRes);

        newsServiceWrapper.getSecurityNewsDetail(mSummaryNewsItemDTO.id, createNewsDetailCallback());
        votePair.display(mSummaryNewsItemDTO);
    }

    private void fillDetailData(NewsItemDTO data)
    {
        mDetailNewsItemDto = data;
        mNewsDetailContent.setText(mDetailNewsItemDto.text);
        mNewsDetailContent.setVisibility(View.VISIBLE);
        mNewsDetailLoading.setVisibility(View.GONE);

        securityServiceWrapper.getMultipleSecurities2(createNewsDetailSecurityCallback(), mDetailNewsItemDto.getSecurityIds());
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

    private void showShareDialog()
    {
        View contentView = LayoutInflater.from(getSherlockActivity()).inflate(R.layout.sharing_translation_dialog_layout, null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback) contentView;
        ((NewsDialogLayout) contentView).setNewsData(mDetailNewsItemDto == null ? mSummaryNewsItemDTO : mDetailNewsItemDto, false);
        THDialog.showUpDialog(getSherlockActivity(), contentView, callback);
    }
}
