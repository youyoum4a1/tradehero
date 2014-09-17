package com.tradehero.th.network.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import com.tradehero.common.persistence.DTOCacheNew;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.share.BaseResponseSocialShareResultDTO;
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
import org.jetbrains.annotations.Nullable;
import retrofit.Callback;
import retrofit.RetrofitError;
import retrofit.client.Response;
import timber.log.Timber;

public class SocialSharerImpl implements SocialSharer
{
    @NotNull private final Activity activity;
    @NotNull private final CurrentUserId currentUserId;
    @NotNull private final UserProfileCache userProfileCache;
    @NotNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NotNull private final SocialShareVerifier socialShareVerifier;

    private OnSharedListener sharedListener;
    @Nullable private UserProfileDTO currentUserProfile;
    @Nullable private SocialShareFormDTO waitingSocialShareFormDTO;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            @NotNull Activity activity,
            @NotNull CurrentUserId currentUserId,
            @NotNull UserProfileCache userProfileCache,
            @NotNull DiscussionServiceWrapper discussionServiceWrapper,
            @NotNull SocialShareVerifier socialShareVerifier)
    {
        this.activity = activity;
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

    @Override public void share(@NotNull SocialShareFormDTO shareFormDTO)
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
        else if (waitingSocialShareFormDTO == null)
        {
            Timber.e(new Exception(), "You should not shareWaitingDTOIfCan where there is no waitingSocialShareFormDTO");
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
        activity.startActivity(createWeChatIntent(activity, weChatDTO));
    }

    public Intent createWeChatIntent(@NotNull Context activityContext, @NotNull WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(activityContext, WXEntryActivity.class);
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
        return intent;
    }

    protected Callback<BaseResponseDTO> createDiscussionCallback(SocialShareFormDTO shareFormDTO)
    {
        return new SocialSharerImplDiscussionCallback(shareFormDTO);
    }

    protected class SocialSharerImplDiscussionCallback implements Callback<BaseResponseDTO>
    {
        private final SocialShareFormDTO shareFormDTO;

        public SocialSharerImplDiscussionCallback(SocialShareFormDTO shareFormDTO)
        {
            this.shareFormDTO = shareFormDTO;
        }

        @Override public void success(BaseResponseDTO responseDTO, Response response)
        {
            notifySharedListener(shareFormDTO, new BaseResponseSocialShareResultDTO(responseDTO));
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
