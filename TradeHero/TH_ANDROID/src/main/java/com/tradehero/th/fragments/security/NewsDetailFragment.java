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
import android.widget.TextView;
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
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;

/**
 * Created by tradehero on 14-3-10.
 */
public class NewsDetailFragment extends DashboardFragment /*AbstractSecurityInfoFragment*/
{

    public static final String BUNDLE_KEY_TITLE_BACKGROUND_RES = NewsDetailFragment.class.getName() + ".title_bg";

    TextView titleView;
    TextView dateView;
    ImageView titlePlaceholderView;
    LinearLayout likeContainer;
    TextView likeTextView;
    ImageView likeImageView;
    TextView moreTextView;
    TextView contentView;
    TextView loadingView;
    GridView securityGridView;
    LinearLayout gridViewContainer;

    SimpleSecurityItemViewAdapter simpleSecurityItemViewAdapter;

    NewsItemDTO sampleItemDto;
    NewsItemDTO detailItemDto;

    @Inject NewsServiceWrapper newsServiceWrapper;
    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;

    @Inject SecurityServiceWrapper securityServiceWrapper;
    @Inject SecurityItemViewAdapterFactory securityItemViewAdapterFactory;
    protected SecurityItemViewAdapter<SecurityCompactDTO> securityItemViewAdapter;

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
        initViews(view);
        linkWith();
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        //linkWith();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume()
    {
        super.onResume();
    }

    private void initViews(View view)
    {
        this.titleView = (TextView) view.findViewById(R.id.news_detail_title);
        this.dateView = (TextView) view.findViewById(R.id.news_detail_date);
        this.titlePlaceholderView = (ImageView) view.findViewById(R.id.news_detail_title_placeholder);
        likeContainer = (LinearLayout) view.findViewById(R.id.news_action_button_like_wrapper);
        likeContainer.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                voteUpOrDown(sampleItemDto.voteDirection == 1 ? false : true);
            }
        });
        likeTextView = (TextView) view.findViewById(R.id.new_action_tv_like);
        likeImageView = (ImageView) view.findViewById(R.id.new_action_iv_like);
        moreTextView = (TextView) view.findViewById(R.id.news_action_tv_more);
        Typeface font = Typeface.createFromAsset(getActivity().getApplicationContext().getAssets(), "FontAwesome.ttf");
        moreTextView.setTypeface(font);
        moreTextView.setText("\uf141");
        moreTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override public void onClick(View v)
            {
                showShareDialog();
            }
        });

        this.contentView = (TextView) view.findViewById(R.id.news_detail_content);
        this.loadingView = (TextView) view.findViewById(R.id.news_detail_loading);
        securityGridView = (GridView) view.findViewById(R.id.news_detail_reference_gv);
        simpleSecurityItemViewAdapter =
                new SimpleSecurityItemViewAdapter(getActivity(), getActivity().getLayoutInflater(), R.layout.trending_security_item);
        securityGridView.setAdapter(simpleSecurityItemViewAdapter);
        securityGridView.setOnItemClickListener(new AdapterView.OnItemClickListener()
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
        gridViewContainer = (LinearLayout) view.findViewById(R.id.news_detail_reference_gv_container);
    }

    private void linkWith()
    {
        Bundle args = getArguments();
        this.sampleItemDto = NewsItemDTO.getSampleNewsItemDTO(args);
        this.titleView.setText(sampleItemDto.title);
        PrettyTime prettyTime = new PrettyTime();
        this.dateView.setText(prettyTime.format(sampleItemDto.createdAtUtc));

        int bgRes = args.getInt(BUNDLE_KEY_TITLE_BACKGROUND_RES, 0);
        titlePlaceholderView.setImageResource(bgRes);

        newsServiceWrapper.getSecurityNewsDetail(sampleItemDto.id, createNewsDetailCallback());
        //TODO change to R.string
        likeTextView.setText(sampleItemDto.voteDirection == 1 ? "Unlike" : "Like");
        likeImageView.setImageResource(sampleItemDto.voteDirection == 1 ? R.drawable.icn_actions_downvote : R.drawable.icn_actions_upvote);
    }

    private void fillDetailData(NewsItemDTO data)
    {
        this.detailItemDto = data;
        this.contentView.setText(detailItemDto.text);
        contentView.setVisibility(View.VISIBLE);
        loadingView.setVisibility(View.GONE);

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
                ViewGroup.LayoutParams lp = gridViewContainer.getLayoutParams();
                //TODO it changes with solution
                lp.width = 510 * securityCompactDTOList.size();
                gridViewContainer.setLayoutParams(lp);
                securityGridView.setNumColumns(securityCompactDTOList.size());
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
                likeTextView.setText(towardUp ? "Unlike" : "Like");
                likeImageView.setImageResource(towardUp ? R.drawable.icn_actions_downvote : R.drawable.icn_actions_upvote);
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
