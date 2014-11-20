package com.tradehero.th.network.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Pair;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.service.SocialShareServiceWrapper;
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
    @NonNull private final SocialShareServiceWrapper socialShareServiceWrapper;
    @NonNull private final SocialShareVerifier socialShareVerifier;

    @Nullable private OnSharedListener sharedListener;
    @Nullable private UserProfileDTO currentUserProfile;
    @Nullable private SocialShareFormDTO waitingSocialShareFormDTO;

    //<editor-fold desc="Constructors">
    @Inject public SocialSharerImpl(
            @NonNull Activity activity,
            @NonNull CurrentUserId currentUserId,
            @NonNull UserProfileCacheRx userProfileCache,
            @NonNull SocialShareServiceWrapper socialShareServiceWrapper,
            @NonNull SocialShareVerifier socialShareVerifier)
    {
        this.activity = activity;
        this.currentUserId = currentUserId;
        this.userProfileCache = userProfileCache;
        this.socialShareServiceWrapper = socialShareServiceWrapper;
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

    protected void notifySharedListener(@NonNull SocialShareFormDTO shareFormDTO, @NonNull SocialShareResultDTO socialShareResultDTO)
    {
        OnSharedListener sharedListenerCopy = sharedListener;
        if (sharedListenerCopy != null)
        {
            sharedListenerCopy.onShared(shareFormDTO, socialShareResultDTO);
        }
    }

    protected void notifySharedFailedListener(@NonNull SocialShareFormDTO shareFormDTO, @NonNull Throwable throwable)
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
            if (waitingSocialShareFormDTO instanceof WeChatDTO)
            {
                activity.startActivity(createWeChatIntent(activity, (WeChatDTO) waitingSocialShareFormDTO));
            }
            else
            {
                socialShareServiceWrapper.shareRx(waitingSocialShareFormDTO)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(createShareObserver(waitingSocialShareFormDTO));
            }
        }
        waitingSocialShareFormDTO = null;
    }

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

    @NonNull public Intent createWeChatIntent(@NonNull Context activityContext, @NonNull WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(activityContext, WXEntryActivity.class);
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
        return intent;
    }

    @NonNull protected Observer<SocialShareResultDTO> createShareObserver(@NonNull SocialShareFormDTO shareFormDTO)
    {
        return new ShareObserver(shareFormDTO);
    }

    protected class ShareObserver extends EmptyObserver<SocialShareResultDTO>
    {
        @NonNull private SocialShareFormDTO shareFormDTO;

        //<editor-fold desc="Constructors">
        public ShareObserver(@NonNull SocialShareFormDTO shareFormDTO)
        {
            this.shareFormDTO = shareFormDTO;
        }
        //</editor-fold>

        @Override public void onNext(SocialShareResultDTO socialShareResultDTO)
        {
            notifySharedListener(shareFormDTO, socialShareResultDTO);
        }

        @Override public void onError(Throwable e)
        {
            notifySharedFailedListener(shareFormDTO, e);
        }
    }
}
