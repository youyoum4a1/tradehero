package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.DTOView;
import com.tradehero.th.api.news.NewsHeadline;
import org.ocpsoft.prettytime.PrettyTime;

/**
 * Created by julien on 11/10/13
 */
public class NewsHeadlineView extends LinearLayout implements DTOView<NewsHeadline>,View.OnClickListener,THDialog.OnDialogItemClickListener
{
    private static final String TAG = NewsHeadlineView.class.getSimpleName();

    private TextView dateTextView;
    private TextView titleTextView;
    private View actionLikeView;
    private View actionCommentView;
    private View moreView;

    private NewsHeadline newsHeadline;

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


    private void showShareDialog() {
        THDialog.showUpDialog(getContext(),null, new String[]{"Translation","Share"},null,this,null);
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

    @Override public void display(NewsHeadline dto)
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
            titleTextView.setText(newsHeadline.getTitle());
        }

        if (dateTextView != null && newsHeadline.getDate() != null)
        {
            PrettyTime prettyTime = new PrettyTime();
            dateTextView.setText(prettyTime.format(newsHeadline.getDate()));
        }
    }
}
