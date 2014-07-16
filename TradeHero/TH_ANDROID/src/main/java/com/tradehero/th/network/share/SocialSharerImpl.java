package com.tradehero.th.network.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.activities.CurrentActivityHolder;
import com.tradehero.th.api.discussion.DiscussionDTO;
import com.tradehero.th.api.share.DiscussionShareResultDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCache;
import com.tradehero.th.wxapi.WXEntryActivity;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;

public class SocialSharerImpl implements SocialSharer
{
    private final CurrentActivityHolder currentActivityHolder;
    private final CurrentUserId currentUserId;
    private final UserProfileCache userProfileCache;
    private final DiscussionServiceWrapper discussionServiceWrapper;
    private final SocialShareVerifier socialShareVerifier;

    private OnSharedListener sharedListener;
    private UserProfileDTO currentUserProfile;
    private SocialShareFormDTO waitingSocialShareFormDTO;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            CurrentActivityHolder currentActivityHolder,
            CurrentUserId currentUserId,
            UserProfileCache userProfileCache,
            DiscussionServiceWrapper discussionServiceWrapper,
            SocialShareVerifier socialShareVerifier)
    {
        this.currentActivityHolder = currentActivityHolder;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.socialShareVerifier = socialShareVerifier;
    }
    //</editor-fold>

    //<editor-fold desc="Shared Listener">
    @Override public void setSharedListener(OnSharedListener sharedListener)
    {
        this.sharedListener = sharedListener;
    }

    protected void notifyConnectRequiredListener(SocialShareFormDTO shareFormDTO)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onConnectRequired(shareFormDTO);
        }
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

    @Override public void share(SocialShareFormDTO shareFormDTO)
    {
        this.waitingSocialShareFormDTO = shareFormDTO;
        shareWaitingDTOIfCan();
    }

    protected void shareWaitingDTOIfCan()
    {
        if (currentUserProfile == null)
        {
            fetchUserProfile();
        }
        else
        {
            try
            {
                switch (socialShareVerifier.canShare(currentUserProfile, waitingSocialShareFormDTO))
                {
                    case YES:
                        shareWaitingDTO();
                        break;

                    case NO:
                        notifySharedFailedListener(waitingSocialShareFormDTO, new CannotShareException("Cannot share this"));
                        break;

                    case NEED_AUTH:
                        notifyConnectRequiredListener(waitingSocialShareFormDTO);
                        break;

                    case TRY_AND_SEE:
                        shareWaitingDTO();
                        break;
                }
            }
            catch (IllegalStateException e)
            {
                notifySharedFailedListener(waitingSocialShareFormDTO, e);
            }
        }
    }

    protected void shareWaitingDTO()
    {
        if (waitingSocialShareFormDTO != null)
        {
            if (waitingSocialShareFormDTO instanceof TimelineItemShareFormDTO)
            {
                share((TimelineItemShareFormDTO) waitingSocialShareFormDTO);
            }
            else if (waitingSocialShareFormDTO instanceof WeChatDTO)
            {
                share((WeChatDTO) waitingSocialShareFormDTO);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + waitingSocialShareFormDTO.getClass());
            }
        }
        waitingSocialShareFormDTO = null;
    }

    public void share(TimelineItemShareFormDTO timelineItemShareFormDTO)
    {
        // We do not save the MiddleCallback because the intermediation is already
        // handled by the OnSharedListener
        discussionServiceWrapper.share(
                timelineItemShareFormDTO.discussionListKey,
                timelineItemShareFormDTO.timelineItemShareRequestDTO,
                createDiscussionCallback(timelineItemShareFormDTO));
    }

    public void share(@NotNull WeChatDTO weChatDTO)
    {
        Activity currentActivity = currentActivityHolder.getCurrentActivity();
        if (currentActivity != null)
        {
            currentActivity.startActivity(createWeChatIntent(currentActivity, weChatDTO));
        }
    }

    public Intent createWeChatIntent(@NotNull Context activityContext, @NotNull WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(activityContext, WXEntryActivity.class);
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
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
        userProfileCache.register(currentUserId.toUserBaseKey(), createProfileListener());
        userProfileCache.getOrFetchAsync(currentUserId.toUserBaseKey());
    }

    protected DTOCacheNew.Listener<UserBaseKey, UserProfileDTO> createProfileListener()
    {
        return new SocialSharerUserProfileListener();
    }

    protected class SocialSharerUserProfileListener implements DTOCacheNew.Listener<UserBaseKey, UserProfileDTO>
    {
        @Override public void onDTOReceived(@NotNull UserBaseKey key, @NotNull UserProfileDTO value)
        {
            currentUserProfile = value;
            shareWaitingDTOIfCan();
        }

        @Override public void onErrorThrown(@NotNull UserBaseKey key, @NotNull Throwable error)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }
    //</editor-fold>
}
