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
import com.tradehero.common.utils.THToast;
import com.tradehero.common.widget.dialog.THDialog;
import com.tradehero.th.R;
import com.tradehero.th.api.discussion.AbstractDiscussionCompactDTO;
import com.tradehero.th.api.discussion.DiscussionType;
import com.tradehero.th.api.discussion.key.DiscussionListKey;
import com.tradehero.th.api.news.NewsItemCompactDTO;
import com.tradehero.th.api.news.NewsItemDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.TimelineItemShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.share.wechat.WeChatMessageType;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.timeline.TimelineItemShareRequestDTO;
import com.tradehero.th.fragments.share.ShareDestinationSetAdapter;
import com.tradehero.th.models.share.FacebookShareDestination;
import com.tradehero.th.models.share.LinkedInShareDestination;
import com.tradehero.th.models.share.ShareDestination;
import com.tradehero.th.models.share.ShareDestinationFactory;
import com.tradehero.th.models.share.TwitterShareDestination;
import com.tradehero.th.models.share.WeChatShareDestination;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.share.SocialSharer;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import javax.inject.Inject;
import javax.inject.Provider;
import timber.log.Timber;

public class ShareDialogLayout extends LinearLayout implements THDialog.DialogCallback
{
    @InjectView(R.id.news_action_share_title2) protected TextView shareTitleView;
    @InjectView(R.id.news_action_share_cancel) protected View cancelView;
    @InjectView(R.id.news_action_list_sharing_items) protected ListView listViewSharingOptions;

    @Inject Lazy<DiscussionServiceWrapper> discussionServiceWrapper;
    @Inject Provider<SocialSharer> socialSharerProvider;
    @Inject ShareDestinationFactory shareDestinationFactory;

    protected THDialog.DialogInterface dialogCallback;
    protected OnShareMenuClickedListener menuClickedListener;
    protected SocialSharer currentSocialSharer;
    protected AbstractDiscussionCompactDTO abstractDiscussionCompactDTO;
    protected int id;
    protected String title;
    protected String description;
    protected WeChatMessageType mShareType;

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
        detachSocialSharer();
        ButterKnife.reset(this);
        super.onDetachedFromWindow();
    }

    protected void fillData()
    {
        BaseAdapter adapterForShare =
                new ShareDestinationSetAdapter(getContext(), shareDestinationFactory.getAllShareDestinations());
        listViewSharingOptions.setAdapter(adapterForShare);
        listViewSharingOptions.setDividerHeight(1);
    }

    protected void detachSocialSharer()
    {
        SocialSharer socialSharerCopy = currentSocialSharer;
        if (socialSharerCopy != null)
        {
            socialSharerCopy.setSharedListener(null);
        }
    }

    protected void handleShareAction(ShareDestination shareDestination)
    {
        SocialShareFormDTO shareFormDTO;
        SocialSharer.OnSharedListener sharedListener = null;
        if (shareDestination instanceof WeChatShareDestination)
        {
            shareFormDTO = createWeChatShareDTO();
            // TODO add sharedListener?
        }
        else
        {
            SocialNetworkEnum socialNetwork = null;
            if (shareDestination instanceof FacebookShareDestination)
            {
                socialNetwork = SocialNetworkEnum.FB;
            }
            else if (shareDestination instanceof LinkedInShareDestination)
            {
                socialNetwork = SocialNetworkEnum.LN;
            }
            else if (shareDestination instanceof TwitterShareDestination)
            {
                socialNetwork = SocialNetworkEnum.TW;
            }
            else
            {
                throw new IllegalArgumentException("Unhandled ShareDestination " + shareDestination.getClass().getName());
            }
            shareFormDTO = createTimelineItemShareFormDTO(socialNetwork);
            sharedListener = createDiscussionSharedListener(socialNetwork);
        }
        detachSocialSharer();
        currentSocialSharer = socialSharerProvider.get();
        currentSocialSharer.share(shareFormDTO, sharedListener);
    }

    protected WeChatDTO createWeChatShareDTO()
    {
        WeChatDTO weChatDTO = new WeChatDTO();
        weChatDTO.id = id;
        weChatDTO.type = mShareType;
        weChatDTO.title = title;
        return weChatDTO;
    }

    protected TimelineItemShareFormDTO createTimelineItemShareFormDTO(SocialNetworkEnum socialNetwork)
    {
        return new TimelineItemShareFormDTO(
                new DiscussionListKey(DiscussionType.NEWS, id),
                new TimelineItemShareRequestDTO(socialNetwork));
    }

    protected SocialSharer.OnSharedListener createDiscussionSharedListener(
            final SocialNetworkEnum socialNetworkEnum)
    {
        return new SocialSharer.OnSharedListener()
        {
            @Override public void onConnectRequired(SocialShareFormDTO shareFormDTO)
            {
                notifyShareConnectRequested(shareFormDTO);
            }

            @Override public void onShared(SocialShareFormDTO shareFormDTO,
                    SocialShareResultDTO socialShareResultDTO)
            {
                THToast.show(String.format(
                        getContext().getString(R.string.timeline_post_to_social_network),
                        socialNetworkEnum.getName()));
            }

            @Override public void onShareFailed(SocialShareFormDTO shareFormDTO,
                    Throwable throwable)
            {
                THToast.show("Share error " + socialNetworkEnum.getName());
                Timber.e(throwable, "Share error");
            }
        };
    }

    @OnItemClick(R.id.news_action_list_sharing_items)
    protected void onShareOptionsItemClicked(AdapterView<?> parent, View view, int position, long id)
    {
        handleShareAction((ShareDestination) parent.getItemAtPosition(position));
        dismissDialog();
    }

    @Override
    public void setOnDismissCallback(THDialog.DialogInterface listener)
    {
        this.dialogCallback = listener;
    }

    @OnClick(R.id.news_action_share_cancel)
    protected void dismissDialog()
    {
        if (dialogCallback != null)
        {
            dialogCallback.onDialogDismiss();
        }
    }

    public void setNewsData(NewsItemCompactDTO newsItemCompactDTO, WeChatMessageType shareType)
    {
        this.description = newsItemCompactDTO.description;
        this.title = null;
        setNewsData((AbstractDiscussionCompactDTO) newsItemCompactDTO, shareType);
    }

    public void setNewsData(NewsItemDTO newsItemDTO, WeChatMessageType shareType)
    {
        this.description = newsItemDTO.description;
        this.title = newsItemDTO.text;
        setNewsData((AbstractDiscussionCompactDTO) newsItemDTO, shareType);
    }

    public void setNewsData(AbstractDiscussionCompactDTO abstractDiscussionDTO, WeChatMessageType shareType)
    {
        this.abstractDiscussionCompactDTO = abstractDiscussionDTO;
        this.id = abstractDiscussionDTO.id;
        mShareType = shareType;
    }

    public void setMenuClickedListener(OnShareMenuClickedListener menuClickedListener)
    {
        this.menuClickedListener = menuClickedListener;
    }

    protected void notifyShareConnectRequested(SocialShareFormDTO shareFormDTO)
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareConnectRequested(shareFormDTO);
        }
    }

    protected void notifyShareClicked()
    {
        OnShareMenuClickedListener listenerCopy = menuClickedListener;
        if (listenerCopy != null)
        {
            listenerCopy.onShareRequestedClicked();
        }
    }

    public static interface OnShareMenuClickedListener
    {
        void onShareConnectRequested(SocialShareFormDTO socialShareFormDTO);
        void onShareRequestedClicked();
    }
}
