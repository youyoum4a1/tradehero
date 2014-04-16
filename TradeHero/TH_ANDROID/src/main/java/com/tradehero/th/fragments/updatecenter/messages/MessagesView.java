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
import com.tradehero.th.R;
import timber.log.Timber;

/**
 * Created by wangliang on 14-4-4.
 */
public class MessagesView extends RelativeLayout
{
    static final String TAG = "MessagesView";

    @InjectView(android.R.id.list) ListView listView;
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

        Timber.d("%s constructor", TAG);
    }

    @Override protected void onFinishInflate()
    {
        super.onFinishInflate();
        ButterKnife.inject(this);
        listView.setEmptyView(emptyView);
    }

    public void showErrorView()
    {
        showOnlyThis(errorView);
    }

    public void showListView()
    {
        showOnlyThis(listView);
    }

    public void showEmptyView()
    {
        showOnlyThis(emptyView);
    }

    /**
     * Show progressabar or/and listview
     * @param onlyShowLoadingView
     */
    public void showLoadingView(boolean onlyShowLoadingView)
    {
        showOnlyThis(progressBar);
        if (!onlyShowLoadingView)
        {
            changeViewVisibility(listView,true);
        }

    }

    public ListView getListView()
    {
        return listView;
    }

    private void showOnlyThis(View view)
    {
        changeViewVisibility(listView, view == listView);
        changeViewVisibility(errorView, view == errorView);
        changeViewVisibility(progressBar, view == progressBar);
        changeViewVisibility(emptyView, view == emptyView);
    }

    private void changeViewVisibility(View view, boolean visible)
    {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
