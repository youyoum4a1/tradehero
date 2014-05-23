package com.tradehero.th.network.share;

import android.content.Intent;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.TimelineItemShareFormDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SocialSharerImpl implements SocialSharer
{
    private final CurrentActivityHolder currentActivityHolder;
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private OnSharedListener sharedListener;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            CurrentActivityHolder currentActivityHolder,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        this.currentActivityHolder = currentActivityHolder;
        this.discussionServiceWrapper = discussionServiceWrapper;
    }
    //</editor-fold>

    @Override public void setSharedListener(OnSharedListener sharedListener)
    {
        this.sharedListener = sharedListener;
    }

    protected void notifySharedListener()
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onShared();
        }
    }

    protected void notifySharedFailedListener(Throwable throwable)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onShareFailed(throwable);
        }
    }

    @Override public void share(SocialShareFormDTO shareFormDTO, OnSharedListener sharedListener)
    {
        setSharedListener(sharedListener);
        if (shareFormDTO instanceof TimelineItemShareFormDTO)
        {
            share((TimelineItemShareFormDTO) shareFormDTO);
        }
        else if (shareFormDTO instanceof WeChatDTO)
        {
            share((WeChatDTO) shareFormDTO);
        }
        else
        {
            throw new IllegalArgumentException("Unhandled type " + shareFormDTO.getClass());
        }
    }

    public void share(TimelineItemShareFormDTO timelineItemShareFormDTO)
    {
        // We do not save the MiddleCallback because the intermediation is already
        // handled by the OnSharedListener
        discussionServiceWrapper.share(
                timelineItemShareFormDTO.discussionListKey,
                timelineItemShareFormDTO.timelineItemShareRequestDTO,
                createDiscussionCallback());
    }

    public void share(WeChatDTO weChatDTO)
    {
        currentActivityHolder.getCurrentActivity().startActivity(createWeChatIntent(weChatDTO));
    }

    public Intent createWeChatIntent(WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(currentActivityHolder.getCurrentContext(), WXEntryActivity.class);
        intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_TYPE_KEY, weChatDTO.type);
        intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_ID_KEY, weChatDTO.id);
        if (weChatDTO.title != null)
        {
            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_TITLE_KEY, weChatDTO.title);
        }
        if (weChatDTO.imageURL != null && !weChatDTO.imageURL.isEmpty())
        {
            intent.putExtra(WXEntryActivity.WECHAT_MESSAGE_IMAGE_URL_KEY, weChatDTO.imageURL);
        }
        return intent;
    }

    protected Callback<DiscussionDTO> createDiscussionCallback()
    {
        return new SocialSharerImplDiscussionCallback();
    }

    protected class SocialSharerImplDiscussionCallback implements Callback<DiscussionDTO>
    {
        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            notifySharedListener();
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            notifySharedFailedListener(retrofitError);
        }
    }
}
