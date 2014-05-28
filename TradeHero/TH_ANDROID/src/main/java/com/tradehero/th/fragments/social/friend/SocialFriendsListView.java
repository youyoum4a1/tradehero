package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;

public class SocialFriendsListView extends RelativeLayout {

    @InjectView(R.id.content_wrapper)
    View contentWrapper;
    @InjectView(R.id.social_friends_list)
    ListView listView;
    @InjectView(R.id.social_follow_all)
    View followAllView;
    @InjectView(R.id.social_invite_all)
    View inviteAllView;
    @InjectView(android.R.id.progress)
    ProgressBar progressBar;
    @InjectView(android.R.id.empty)
    TextView emptyView;
    @InjectView(R.id.error)
    View errorView;

    private OnClickListener onClickListener;

    //<editor-fold desc="Constructors">
    public SocialFriendsListView(Context context) {
        super(context);
    }

    public SocialFriendsListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SocialFriendsListView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        ButterKnife.inject(this);
        listView.setEmptyView(emptyView);
    }

    @OnClick(R.id.social_follow_all)
    public void onFollowAllClick()
    {
        if (onClickListener != null)
        {
            onClickListener.onClick(followAllView);
        }
    }

    @OnClick(R.id.social_invite_all)
    public void onInviteAllClick()
    {
        if (onClickListener != null)
        {
            onClickListener.onClick(inviteAllView);
        }
    }

    public void setInviteAllViewVisible(boolean viewVisible)
    {
        inviteAllView.setVisibility(viewVisible ? View.VISIBLE : View.GONE);
    }

    public void setFollowAllViewVisible(boolean viewVisible)
    {
        followAllView.setVisibility(viewVisible ? View.VISIBLE : View.GONE);
    }

    public void setFollowOrInivteActionClickListener(OnClickListener l)
    {
        this.onClickListener = l;
    }

    public void showErrorView() {
        showOnlyThis(errorView);
    }

    public void showContentView() {
        showOnlyThis(contentWrapper);
    }

    public void showLoadingView() {
        showOnlyThis(progressBar);
    }

    public void showEmptyView() {
        showOnlyThis(emptyView);
    }

    private void showOnlyThis(View view) {
        changeViewVisibility(contentWrapper, view == contentWrapper);
        changeViewVisibility(errorView, view == errorView);
        changeViewVisibility(progressBar, view == progressBar);
        changeViewVisibility(emptyView, view == emptyView);
    }

    private void changeViewVisibility(View view, boolean visible) {
        view.setVisibility(visible ? View.VISIBLE : View.GONE);
    }
}
