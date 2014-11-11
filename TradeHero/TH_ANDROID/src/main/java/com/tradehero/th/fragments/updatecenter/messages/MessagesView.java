package com.tradehero.th.fragments.updatecenter.messages;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import com.fortysevendeg.swipelistview.SwipeListView;
import com.tradehero.th.R;

public class MessagesView extends RelativeLayout
{
    @InjectView(R.id.message_list) SwipeListView swipeListView;
    @InjectView(R.id.swipe_container) SwipeRefreshLayout swipeRefreshLayout;
    @InjectView(android.R.id.progress) ProgressBar progressBar;
    @InjectView(android.R.id.empty) TextView emptyView;
    @InjectView(R.id.error) View errorView;
    @InjectView(R.id.listViewLayout) RelativeLayout listViewLayout;
    @InjectView(R.id.readAllLayout) View readAllLayout;

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
        swipeListView.setEmptyView(emptyView);
    }

    public void showErrorView()
    {
        showOnlyThis(errorView);
    }

    public void showListView()
    {
        showOnlyThis(listViewLayout);
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
            changeViewVisibility(swipeListView, true);
        }
    }

    public SwipeListView getListView()
    {
        return swipeListView;
    }

    private void showOnlyThis(View view)
    {
        changeViewVisibility(listViewLayout, view == listViewLayout);
        changeViewVisibility(errorView, view == errorView);
        changeViewVisibility(progressBar, view == progressBar);
        changeViewVisibility(emptyView, view == emptyView);
    }

    private void changeViewVisibility(View view, boolean visible)
    {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
