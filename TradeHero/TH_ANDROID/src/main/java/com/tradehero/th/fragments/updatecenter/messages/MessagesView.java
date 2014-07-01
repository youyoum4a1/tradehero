package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.fortysevendeg.android.swipelistview.SwipeListView;
import com.handmark.pulltorefresh.library.PullToRefreshSwipeListView;
import com.tradehero.th.R;
import timber.log.Timber;

public class MessagesView extends RelativeLayout
{
    @InjectView(R.id.message_list) PullToRefreshSwipeListView
            pullToRefreshSwipeListView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) TextView emptyView;
    @InjectView(R.id.error) View errorView;

    //<editor-fold desc="Constructors">
    public MessagesView(Context context)
    {
        super(context);
    }

    public MessagesView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public MessagesView(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        pullToRefreshSwipeListView.setEmptyView(emptyView);
    }

    public void showErrorView()
    {
        showOnlyThis(errorView);
    }

    public void showListView()
    {
        showOnlyThis(pullToRefreshSwipeListView);
    }

    public void showEmptyView()
    {
        showOnlyThis(emptyView);
    }

    /**
     * Show progress bar or/and listview
     */
    public void showLoadingView(boolean onlyShowLoadingView)
    {
        showOnlyThis(progressBar);
        if (!onlyShowLoadingView)
        {
            changeViewVisibility(pullToRefreshSwipeListView, true);
        }
    }

    public SwipeListView getListView()
    {
        return pullToRefreshSwipeListView.getRefreshableView();
    }

    private void showOnlyThis(View view)
    {
        changeViewVisibility(pullToRefreshSwipeListView, view == pullToRefreshSwipeListView);
        changeViewVisibility(errorView, view == errorView);
        changeViewVisibility(progressBar, view == progressBar);
        changeViewVisibility(emptyView, view == emptyView);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        Timber.d("windy onAttachToWindow");
    }

    @Override protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        Timber.d("windy onDetachedFromWindow");
    }

    private void changeViewVisibility(View view, boolean visible)
    {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
