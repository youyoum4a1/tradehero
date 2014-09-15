package com.tradehero.th.fragments.news;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;
import butterknife.OnItemClick;
import com.tradehero.th2.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareFormDTOFactory;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationFactory;
import com.tradehero.th.utils.DaggerUtils;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ShareDialogLayout extends LinearLayout
{
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    @Inject ShareDestinationFactory shareDestinationFactory;
    @Inject SocialShareFormDTOFactory socialShareFormDTOFactory;

    @Nullable protected OnShareMenuClickedListener menuClickedListener;
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
        listViewSharingOptions.setAdapter(new ShareDestinationSetAdapter(getContext(), shareDestinationFactory.getAllShareDestinations()));
        listViewSharingOptions.setDividerHeight(1);
    }

    public void setDiscussionToShare(@NotNull AbstractDiscussionCompactDTO discussionToShare)
    {
        this.discussionToShare = discussionToShare;
    }

    public void setMenuClickedListener(@Nullable OnShareMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    @OnClick(R.id.news_action_share_cancel)
    protected void onCancelClicked(View view)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCancelClicked();
        }
    }

    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked(
                    socialShareFormDTOFactory.createForm(
                            (ShareDestination) parent.getItemAtPosition(position),
                            discussionToShare));
        }
    }

    public static interface OnShareMenuClickedListener
    {
        void onCancelClicked();
        void onShareRequestedClicked(SocialShareFormDTO socialShareFormDTO);
    }
}
