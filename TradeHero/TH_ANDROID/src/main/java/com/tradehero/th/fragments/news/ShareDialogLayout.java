package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.thm.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareFormDTOFactory;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationFactory;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;

public class ShareDialogLayout extends LinearLayout
{
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject ShareDestinationFactory shareDestinationFactory;
    @Inject SocialShareFormDTOFactory socialShareFormDTOFactory;

    protected OnShareMenuClickedListener menuClickedListener;
    protected AbstractDiscussionCompactDTO discussionToShare;

    //<editor-fold desc="Constructors">
    public ShareDialogLayout(Context context)
    {
        super(context);
    }

    public ShareDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public ShareDialogLayout(Context context, AttributeSet attrs, int defStyle)
    {
        super(context, attrs, defStyle);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
        DaggerUtils.inject(this);
        ButterKnife.inject(this);
    }

    @Override protected void onAttachedToWindow()
    {
        super.onAttachedToWindow();
        ButterKnife.inject(this);
        fillData();
    }

    @Override protected void onDetachedFromWindow()
    {
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    protected void fillData()
    {
        listViewSharingOptions.setAdapter(createAdapterForShare());
        listViewSharingOptions.setDividerHeight(1);
    }

    protected BaseAdapter createAdapterForShare()
    {
        return new ShareDestinationSetAdapter(getContext(), shareDestinationFactory.getAllShareDestinations());
    }

    public void setDiscussionToShare(AbstractDiscussionCompactDTO discussionToShare)
    {
        this.discussionToShare = discussionToShare;
    }

    public void setMenuClickedListener(OnShareMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    @OnClick(R.id.news_action_share_cancel)
    protected void onCancelClicked(View view)
    {
        notifyCancelClicked();
    }

    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        handleShareAction((ShareDestination) parent.getItemAtPosition(position));
    }

    protected void handleShareAction(ShareDestination shareDestination)
    {
        notifyShareClicked(socialShareFormDTOFactory.createForm(shareDestination,
                discussionToShare));
    }

    protected void notifyCancelClicked()
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCancelClicked();
        }
    }

    protected void notifyShareClicked(SocialShareFormDTO shareFormDTO)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked(shareFormDTO);
        }
    }

    public static interface OnShareMenuClickedListener
    {
        void onCancelClicked();
        void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO);
    }
}
