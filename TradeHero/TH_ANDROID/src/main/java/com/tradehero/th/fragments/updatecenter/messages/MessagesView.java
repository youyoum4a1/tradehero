package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.handmark.pulltorefresh.library.PullToRefreshInterceptedScrollSwipeListView;
import com.tradehero.th.R;
import timber.log.Timber;

public class MessagesView extends RelativeLayout
{
    @InjectView(R.id.message_list) PullToRefreshInterceptedScrollSwipeListView
            pullToRefreshSwipeListView;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) TextView emptyView;
    @InjectView(R.id.error) View errorView;

    public MessagesView(Context context)
    {
        super(context);
    }

    public MessagesView(Context context, AttributeSet attrs)
    {
        super(context, attrs);

        Timber.d("constructor");
    }

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
     * Show progressabar or/and listview
     */
    public void showLoadingView(boolean onlyShowLoadingView)
    {
        showOnlyThis(progressBar);
        if (!onlyShowLoadingView)
        {
            changeViewVisibility(pullToRefreshSwipeListView, true);
        }
    }

    public ListView getListView()
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

    private void changeViewVisibility(View view, boolean visible)
    {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
