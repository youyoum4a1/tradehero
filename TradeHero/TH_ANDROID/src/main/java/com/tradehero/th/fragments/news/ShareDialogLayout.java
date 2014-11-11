package com.tradehero.th.fragments.news;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
import com.tradehero.common.persistence.DTO;
import com.tradehero.th.R;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareFormDTOFactory;
import com.tradehero.th.inject.HierarchyInjector;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationFactory;
import java.util.Comparator;
import javax.inject.Inject;

public class ShareDialogLayout extends LinearLayout
{
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    @Inject ShareDestinationFactory shareDestinationFactory;
    @Inject SocialShareFormDTOFactory socialShareFormDTOFactory;
    @Inject @NonNull Comparator<ShareDestination> shareDestinationIndexResComparator;

    @Nullable protected OnShareMenuClickedListener menuClickedListener;
    @Nullable protected DTO whatToShare;

    //<editor-fold desc="Constructors">
    public ShareDialogLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        HierarchyInjector.inject(this);
    }
    //</editor-fold>

    @Override
    protected void onFinishInflate()
    {
        super.onFinishInflate();
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
        listViewSharingOptions.setAdapter(new ShareDestinationSetAdapter(
                getContext(),
                shareDestinationIndexResComparator,
                shareDestinationFactory.getAllShareDestinations()));
        listViewSharingOptions.setDividerHeight(1);
    }

    public void setWhatToShare(@SuppressWarnings("NullableProblems") @NonNull DTO whatToShare)
    {
        this.whatToShare = whatToShare;
    }

    public void setMenuClickedListener(@Nullable OnShareMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnClick(R.id.news_action_share_cancel)
    protected void onCancelClicked(@SuppressWarnings("UnusedParameters") View view)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onCancelClicked();
        }
    }

    @SuppressWarnings("UnusedDeclaration")
    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(
            AdapterView<?> parent,
            @SuppressWarnings("UnusedParameters") View view,
            int position,
            @SuppressWarnings("UnusedParameters") long id)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        DTO whatToShareCopy = whatToShare;
        if (listenerCopy != null && whatToShareCopy != null)
        {
            if (whatToShareCopy instanceof SocialShareFormDTO)
            {
                listenerCopy.onShareRequestedClicked((SocialShareFormDTO) whatToShareCopy);
            }
            else
            {
                listenerCopy.onShareRequestedClicked(
                        socialShareFormDTOFactory.createForm(
                                (ShareDestination) parent.getItemAtPosition(position),
                                whatToShareCopy));
            }
        }
    }

    public static interface OnShareMenuClickedListener
    {
        void onCancelClicked();
        void onShareRequestedClicked(@NonNull SocialShareFormDTO socialShareFormDTO);
    }
}
