package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import com.tradehero.th.R;
import com.tradehero.th.widget.TradeHeroProgressBar;

public class SocialFriendsListView extends RelativeLayout {

    @InjectView(R.id.content_wrapper)
    View contentWrapper;
    @InjectView(R.id.social_friends_list)
    ListView listView;
    @InjectView(R.id.social_follow_all)
    View followAllView;
    @InjectView(R.id.social_invite_all)
    View inviteAllView;
    @InjectView(R.id.tradeheroprogressbar_invite_friends)
    TradeHeroProgressBar progressBar;
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

    public void setInviteAllViewText(String strInvite)
    {
        if(inviteAllView!=null)
        {
            ((TextView)inviteAllView).setText(strInvite);
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

    public void setFollowAllViewEnable(boolean enable)
    {
        followAllView.setEnabled(enable);
    }

    public void setInviteAllViewEnable(boolean enable)
    {
        inviteAllView.setEnabled(enable);
    }

    public void setFollowOrInivteActionClickListener(OnClickListener l)
    {
        this.onClickListener = l;
    }

    public void showErrorView() {
        if(progressBar!=null){
            progressBar.stopLoading();
        }
        showOnlyThis(errorView);
    }

    public void showContentView() {
        if(progressBar!=null){
            progressBar.stopLoading();
        }
        showOnlyThis(contentWrapper);
    }

    public void showLoadingView() {
        showOnlyThis(progressBar);
        progressBar.startLoading();
    }

    public void showEmptyView() {
        if(progressBar!=null){
            progressBar.stopLoading();
        }
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
