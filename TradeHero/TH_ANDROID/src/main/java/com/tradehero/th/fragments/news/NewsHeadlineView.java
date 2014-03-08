package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.VoteDirection;
import com.tradehero.th.api.discussion.key.DiscussionVoteKey;
import com.tradehero.th.api.news.NewsHeadline;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

import javax.inject.Inject;
import javax.swing.text.html.ImageView;
import java.awt.*;

/**
 * Created by julien on 11/10/13
 *
 * modified by Wang Liang.
 */
public class NewsHeadlineView extends LinearLayout implements DTOView<NewsItemDTO>,View.OnClickListener,THDialog.OnDialogItemClickListener
{
    private static final String TAG = NewsHeadlineView.class.getSimpleName();

    private TextView dateTextView;
    private TextView titleTextView;
    private View actionLikeView;
    private View actionCommentView;
    private View moreView;

    private NewsItemDTO newsHeadline;

    private boolean isVotedUp = false;

    @Inject
    Lazy<DiscussionServiceWrapper> discussionServiceWrapperLazy;

    //<editor-fold desc="Constructors">
    public NewsHeadlineView(Context context)
    {
        this(context, null);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs)
    {
        this(context, attrs, 0);
    }

    public NewsHeadlineView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        fetchViews();
        DaggerUtils.inject(this);
    }

    private void fetchViews()
    {
        titleTextView = (TextView) findViewById(R.id.news_title_title);
        dateTextView = (TextView) findViewById(R.id.news_title_date);
        actionLikeView = findViewById(R.id.news_action_button_like_wrapper);
        actionCommentView = findViewById(R.id.news_action_button_comment_wrapper);
        moreView = findViewById(R.id.news_action_button_share_wrapper);
        registerListener();

    }

    private void registerListener() {
        if (actionLikeView != null) {
            actionLikeView.setOnClickListener(this);
        }
        if (actionCommentView != null) {
            actionCommentView.setOnClickListener(this);
        }
        if (moreView != null) {
            moreView.setOnClickListener(this);
        }
    }

    private void unregisterListener() {
        if (actionLikeView != null) {
            actionLikeView.setOnClickListener(null);
        }
        if (actionCommentView != null) {
            actionCommentView.setOnClickListener(null);
        }
        if (moreView != null) {
            moreView.setOnClickListener(null);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.news_action_button_like_wrapper:
                voteUpOrDown(!isVotedUp);
                break;
            case R.id.news_action_button_comment_wrapper:
                break;
            case R.id.news_action_button_share_wrapper:
                showShareDialog();
                break;
        }
    }

    @Override
    public void onClick(int whichButton){
        switch (whichButton) {
            case 0:
                break;
            case 1:
                break;
        }
    }


    private void voteUpOrDown(boolean up) {
        int id = newsHeadline.id;
        VoteDirection direction = up ? VoteDirection.UpVote:VoteDirection.DownVote;
        DiscussionVoteKey key = new DiscussionVoteKey(DiscussionType.NEWS,id,direction);
        discussionServiceWrapperLazy.get().vote(key,createVoteCallback(up));
    }

    private void changeLikeViewDisplay(boolean isVotedUp) {
        android.widget.ImageView likeImageView = (android.widget.ImageView)actionLikeView.findViewById(R.id.new_action_iv_like);
        TextView likeTextView = (TextView)actionLikeView.findViewById(R.id.new_action_tv_like);

        likeTextView.setText(isVotedUp?"Unlike":"Like");

    }

    private Callback<DiscussionDTO> createVoteCallback(final boolean up){

       return new Callback<DiscussionDTO>() {
            @Override
            public void success(DiscussionDTO discussionDTO, Response response) {
                THToast.show("vote "+((up?"up":"down"))+" success");
                changeLikeViewDisplay(up);
                isVotedUp = !up;
            }

            @Override
            public void failure(RetrofitError error) {
                THToast.show("vote "+((up?"up":"down"))+" error");
            }
        };
    }

    /**
     * show dialog including sharing and translation.
     */
    private void showShareDialog() {
        //THDialog.showUpDialog(getContext(),null, new String[]{"Translation","Share"},null,this,null);
        View contentView = LayoutInflater.from(getContext()).inflate(R.layout.sharing_translation_dialog_layout,null);
        THDialog.DialogCallback callback = (THDialog.DialogCallback)contentView;
        ((NewsDialogLayout)contentView).setNewsData(newsHeadline);
        THDialog.showUpDialog(getContext(),contentView,callback);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        registerListener();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        unregisterListener();
    }

    @Override public void display(NewsItemDTO dto)
    {
        this.newsHeadline = dto;
        displayNews();
    }

    private void displayNews()
    {
        if (newsHeadline == null)
        {
            return;
        }

        if (titleTextView != null)
        {
            titleTextView.setText(newsHeadline.title);
        }

        if (dateTextView != null && newsHeadline.createdAtUtc != null)
        {
            PrettyTime prettyTime = new PrettyTime();
            dateTextView.setText(prettyTime.format(newsHeadline.createdAtUtc));
        }
    }
}
