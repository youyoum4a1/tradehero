package com.tradehero.th.network.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.BaseResponseDTO;
import com.tradehero.th.api.share.BaseResponseSocialShareResultDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.UserProfileSocialShareResultDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.social.ReferralCodeShareFormDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.DiscussionServiceWrapper;
import com.tradehero.th.network.service.SocialServiceWrapper;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import com.tradehero.th.wxapi.WXEntryActivity;
import java.util.List;
import javax.inject.Inject;
import rx.Observer;
import rx.android.schedulers.AndroidSchedulers;
import rx.observers.EmptyObserver;
import timber.log.Timber;

public class SocialSharerImpl implements SocialSharer
{
    @NonNull private final Activity activity;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NonNull private final SocialServiceWrapper socialServiceWrapper;
    @NonNull private final SocialShareVerifier socialShareVerifier;

    @Nullable private OnSharedListener sharedListener;
    @Nullable private UserProfileDTO currentUserProfile;
    @Nullable private SocialShareFormDTO waitingSocialShareFormDTO;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            @NonNull Activity activity,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull DiscussionServiceWrapper discussionServiceWrapper,
            @NonNull SocialServiceWrapper socialServiceWrapper,
            @NonNull SocialShareVerifier socialShareVerifier)
    {
        this.activity = activity;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.socialServiceWrapper = socialServiceWrapper;
        this.socialShareVerifier = socialShareVerifier;
    }
    //</editor-fold>

    //<editor-fold desc="Shared Listener">
    @Override public void setSharedListener(@Nullable OnSharedListener sharedListener)
    {
        this.sharedListener = sharedListener;
    }

    protected void notifyConnectRequiredListener(@NonNull SocialShareFormDTO shareFormDTO, @NonNull List<SocialNetworkEnum> toConnect)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onConnectRequired(shareFormDTO, toConnect);
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

    @Override public void share(@NonNull SocialShareFormDTO shareFormDTO)
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
                        notifyConnectRequiredListener(
                                waitingSocialShareFormDTO,
                                socialShareVerifier.getNeedAuthSocialNetworks(
                                        currentUserProfile,
                                        waitingSocialShareFormDTO));
                        break;

                    case TRY_AND_SEE:
                        shareWaitingDTO();
                        break;
                }
            } catch (IllegalStateException e)
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
            else if (waitingSocialShareFormDTO instanceof ReferralCodeShareFormDTO)
            {
                share((ReferralCodeShareFormDTO) waitingSocialShareFormDTO);
            }
            else
            {
                throw new IllegalArgumentException("Unhandled type " + waitingSocialShareFormDTO.getClass());
            }
        }
        waitingSocialShareFormDTO = null;
    }

    public void share(@NonNull TimelineItemShareFormDTO timelineItemShareFormDTO)
    {
        // We do not save the Subscription because the intermediation is already
        // handled by the OnSharedListener
        discussionServiceWrapper.shareRx(
                timelineItemShareFormDTO.discussionListKey,
                timelineItemShareFormDTO.timelineItemShareRequestDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createDiscussionObserver(timelineItemShareFormDTO));
    }

    @NonNull protected Observer<BaseResponseDTO> createDiscussionObserver(SocialShareFormDTO shareFormDTO)
    {
        return new SocialSharerImplDiscussionObserver(shareFormDTO);
    }

    protected class SocialSharerImplDiscussionObserver implements Observer<BaseResponseDTO>
    {
        private final SocialShareFormDTO shareFormDTO;

        public SocialSharerImplDiscussionObserver(SocialShareFormDTO shareFormDTO)
        {
            this.shareFormDTO = shareFormDTO;
        }

        @Override public void onNext(BaseResponseDTO baseResponseDTO)
        {
            notifySharedListener(shareFormDTO, new BaseResponseSocialShareResultDTO(baseResponseDTO));
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            notifySharedFailedListener(shareFormDTO, e);
        }
    }

    public void share(@NonNull WeChatDTO weChatDTO)
    {
        activity.startActivity(createWeChatIntent(activity, weChatDTO));
    }

    @NonNull public Intent createWeChatIntent(@NonNull Context activityContext, @NonNull WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(activityContext, WXEntryActivity.class);
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
        return intent;
    }

    public void share(@NonNull ReferralCodeShareFormDTO shareFormDTO)
    {
        socialServiceWrapper.shareReferralCodeRx(shareFormDTO)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(createReferralCodeShareObserver(shareFormDTO));
    }

    @NonNull protected Observer<UserProfileDTO> createReferralCodeShareObserver(@NonNull ReferralCodeShareFormDTO shareFormDTO)
    {
        return new ReferralCodeShareObserver(shareFormDTO);
    }

    protected class ReferralCodeShareObserver extends EmptyObserver<UserProfileDTO>
    {
        private final ReferralCodeShareFormDTO shareFormDTO;

        public ReferralCodeShareObserver(@NonNull ReferralCodeShareFormDTO shareFormDTO)
        {
            this.shareFormDTO = shareFormDTO;
        }

        @Override public void onNext(UserProfileDTO args)
        {
            notifySharedListener(shareFormDTO, new UserProfileSocialShareResultDTO(args));
        }

        @Override public void onError(Throwable e)
        {
            notifySharedFailedListener(shareFormDTO, e);
        }
    }

    //<editor-fold desc="User Profile">
    protected void fetchUserProfile()
    {
        // Here we do not care about keeping the subscription because the listener already provides
        // the intermediation
        userProfileCache.get(currentUserId.toUserBaseKey())
                .observeOn(AndroidSchedulers.mainThread())
                .first()
                .subscribe(createProfileObserver());
    }

    protected Observer<Pair<UserBaseKey, UserProfileDTO>> createProfileObserver()
    {
        return new SocialSharerUserProfileObserver();
    }

    protected class SocialSharerUserProfileObserver implements Observer<Pair<UserBaseKey, UserProfileDTO>>
    {
        @Override public void onNext(Pair<UserBaseKey, UserProfileDTO> pair)
        {
            currentUserProfile = pair.second;
            shareWaitingDTOIfCan();
        }

        @Override public void onCompleted()
        {
        }

        @Override public void onError(Throwable e)
        {
            THToast.show(R.string.error_fetch_user_profile);
        }
    }
    //</editor-fold>
}
