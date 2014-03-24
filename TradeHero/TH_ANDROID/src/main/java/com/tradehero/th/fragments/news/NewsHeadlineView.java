package com.tradehero.th.fragments.news;

import android.content.Context;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import org.ocpsoft.prettytime.PrettyTime;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

import javax.inject.Inject;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by julien on 11/10/13
 *
 * modified by Wang Liang.
 */
public class NewsHeadlineView extends LinearLayout implements DTOView<NewsItemDTO>,View.OnClickListener,THDialog.OnDialogItemClickListener
{
    private static final String TAG = NewsHeadlineView.class.getSimpleName();

    private View titleViewWrapper;
    private View placeHolderView;
    private TextView dateTextView;
    private TextView titleTextView;
    private TextView descView;
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
        placeHolderView = findViewById(R.id.news_item_placeholder);
        titleViewWrapper = findViewById(R.id.news_item_layout_wrapper);
        titleTextView = (TextView) titleViewWrapper.findViewById(R.id.news_title_title);
        dateTextView = (TextView) titleViewWrapper.findViewById(R.id.news_title_date);
        descView = (TextView) titleViewWrapper.findViewById(R.id.news_title_description);

        actionCommentView = findViewById(R.id.news_action_button_comment_wrapper);
        moreView = findViewById(R.id.news_action_button_more_wrapper);

        TextView tvMore = (TextView)moreView.findViewById(R.id.news_action_tv_more);
        Typeface font = Typeface.createFromAsset(getContext().getAssets(), "FontAwesome.ttf");
        tvMore.setTypeface(font);
        tvMore.setText("\uf141");
        registerListener();
    }

    private void registerListener() {
        if (actionCommentView != null) {
            actionCommentView.setOnClickListener(this);
        }
        if (moreView != null) {
            moreView.setOnClickListener(this);
        }
    }

    private void unregisterListener() {
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
            case R.id.news_action_button_comment_wrapper:
                break;
            case R.id.news_action_button_more_wrapper:
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


    private void voteUpOrDown(boolean towardUp) {
        int id = newsHeadline.id;
        VoteDirection direction = towardUp ? VoteDirection.UpVote:VoteDirection.Unvote;
        DiscussionVoteKey key = new DiscussionVoteKey(DiscussionType.NEWS,id,direction);
        discussionServiceWrapperLazy.get().vote(key,createVoteCallback(towardUp));
        Timber.d("voteUpOrDown towardUp ? %s",towardUp);
    }

    private Callback<DiscussionDTO> createVoteCallback(final boolean towardUp){

       return new Callback<DiscussionDTO>() {
            @Override
            public void success(DiscussionDTO discussionDTO, Response response) {
                THToast.show("vote " + ((towardUp ? "up" : "down")) + " success");
                isVotedUp = towardUp;
                newsHeadline.voteDirection = isVotedUp ? 1:0;

            }

            @Override
            public void failure(RetrofitError error) {
                THToast.show("vote " + ((towardUp ? "up" : "down")) + " error");
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
        ((NewsDialogLayout)contentView).setNewsData(newsHeadline, true);
        THDialog.showUpDialog(getContext(), contentView, callback);
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
        this.isVotedUp = !(newsHeadline.voteDirection == 0);
        displayNews();
    }


    private String parseHost(String url) {
        try {
            String host = new URL(url).getHost();
            return host;
        }catch (MalformedURLException e) {
            return null;
        }

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
            StringBuffer sb = new StringBuffer();
            String text = prettyTime.format(newsHeadline.createdAtUtc);
            sb.append(text);
            if (newsHeadline.url != null) {
                String source = parseHost(newsHeadline.url);
                if (source != null) {
                    sb.append(" via ").append(source);
                }
            }
            dateTextView.setText(sb.toString());
        }

        if (descView != null)
        {
            descView.setText(newsHeadline.description);
//            if (TextUtils.isEmpty(newsHeadline.description)) {
//                descView.setVisibility(View.GONE);
//                Timber.d("newsHeadline description %s empty",newsHeadline.description);
//            }else {
//                descView.setVisibility(View.VISIBLE);
//                Timber.d("newsHeadline description %s not empty",newsHeadline.description);
//            }
        }

//        int h = titleViewWrapper.getMeasuredHeight();
//        if(h <= 0){
//            titleViewWrapper.measure(MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.MATCH_PARENT,View.MeasureSpec.AT_MOST
//                   ), MeasureSpec.makeMeasureSpec(ViewGroup.LayoutParams.WRAP_CONTENT,View.MeasureSpec.AT_MOST));
//            h = titleViewWrapper.getMeasuredHeight();
//        }
//        ViewGroup.LayoutParams lp = placeHolderView.getLayoutParams();
//        lp.height = h;
//        placeHolderView.setLayoutParams(lp);
    }
}
