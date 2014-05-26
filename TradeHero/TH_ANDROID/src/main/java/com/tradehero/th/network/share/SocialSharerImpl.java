package com.tradehero.th.network.share;

import android.content.Intent;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.share.DiscussionShareResultDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.TimelineItemShareFormDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.wxapi.WXEntryActivity;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import javax.inject.Inject;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SocialSharerImpl implements SocialSharer
{
    private final CurrentActivityHolder currentActivityHolder;
    private final CurrentUserId currentUserId;
    private final UserProfileCache userProfileCache;
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private OnSharedListener sharedListener;
    private UserProfileDTO currentUserProfile;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            CurrentActivityHolder currentActivityHolder,
            CurrentUserId currentUserId,
            UserProfileCache userProfileCache,
            DiscussionServiceWrapper discussionServiceWrapper)
    {
        this.currentActivityHolder = currentActivityHolder;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.discussionServiceWrapper = discussionServiceWrapper;
        fetchUserProfile();
    }
    //</editor-fold>

    //<editor-fold desc="Shared Listener">
    @Override public void setSharedListener(OnSharedListener sharedListener)
    {
        this.sharedListener = sharedListener;
    }

    protected void notifySharedListener(SocialShareFormDTO shareFormDTO, SocialShareResultDTO socialShareResultDTO)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onShared(shareFormDTO, socialShareResultDTO);
        }
    }

    protected void notifySharedFailedListener(SocialShareFormDTO shareFormDTO, Throwable throwable)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onShareFailed(shareFormDTO, throwable);
        }
    }
    //</editor-fold>

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
                createDiscussionCallback(timelineItemShareFormDTO));

        // TODO add check that it is able to share
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

    protected Callback<DiscussionDTO> createDiscussionCallback(SocialShareFormDTO shareFormDTO)
    {
        return new SocialSharerImplDiscussionCallback(shareFormDTO);
    }

    protected class SocialSharerImplDiscussionCallback implements Callback<DiscussionDTO>
    {
        private final SocialShareFormDTO shareFormDTO;

        public SocialSharerImplDiscussionCallback(SocialShareFormDTO shareFormDTO)
        {
            this.shareFormDTO = shareFormDTO;
        }

        @Override public void success(DiscussionDTO discussionDTO, Response response)
        {
            notifySharedListener(shareFormDTO, new DiscussionShareResultDTO(discussionDTO));
        }

        @Override public void failure(RetrofitError retrofitError)
        {
            notifySharedFailedListener(shareFormDTO, retrofitError);
        }
    }

    //<editor-fold desc="User Profile">
    protected void fetchUserProfile()
    {
        // Here we do not care about keeping the task because the listener already provides
        // the intermediation
        userProfileCache.getOrFetch(currentUserId.toUserBaseKey(), createProfileListener()).execute();
    }

    protected DTOCache.Listener<UserBaseKey, UserProfileDTO> createProfileListener()
    {
        return new SocialSharerUserProfileListener();
    }

    protected class SocialSharerUserProfileListener implements DTOCache.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(UserBaseKey key, UserProfileDTO value,
                boolean fromCache)
        {
            currentUserProfile = value;
            // TODO something?
        }

        @Override public void onErrorThrown(UserBaseKey key, Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }
    //</editor-fold>
}
