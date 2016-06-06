package com.androidth.general.network.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Pair;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.api.share.SocialShareResultDTO;
import com.androidth.general.api.share.wechat.WeChatDTO;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.api.users.UserBaseKey;
import com.androidth.general.api.users.UserProfileDTO;
import com.androidth.general.network.service.SocialShareServiceWrapper;
import com.androidth.general.network.share.dto.ConnectRequired;
import com.androidth.general.network.share.dto.SharedSuccessful;
import com.androidth.general.network.share.dto.SocialShareResult;
import com.androidth.general.persistence.user.UserProfileCacheRx;
import com.androidth.general.wxapi.WXEntryActivity;
import java.util.List;
import javax.inject.Inject;
import rx.Observable;
import rx.functions.Func1;

public class SocialSharerImpl implements SocialSharer
{
    @NonNull private final Activity activity;
    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final UserProfileCacheRx userProfileCache;
    @NonNull private final SocialShareServiceWrapper socialShareServiceWrapper;
    @NonNull private final SocialShareVerifier socialShareVerifier;

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

    @NonNull @Override public Observable<SocialShareResult> share(@NonNull final SocialShareFormDTO shareFormDTO)
    {
        return userProfileCache.getOne(currentUserId.toUserBaseKey())
                .flatMap(new Func1<Pair<UserBaseKey, UserProfileDTO>, Observable<? extends SocialShareResult>>()
                {
                    @Override public Observable<? extends SocialShareResult> call(final Pair<UserBaseKey, UserProfileDTO> pair)
                    {
                        return socialShareVerifier.canShare(pair.second, shareFormDTO)
                                .flatMap(new Func1<SocialShareVerifier.CanShareType, Observable<SocialShareResult>>()
                                {
                                    @Override public Observable<SocialShareResult> call(SocialShareVerifier.CanShareType shareType)
                                    {
                                        switch (shareType)
                                        {
                                            case TRY_AND_SEE:
                                            case YES:
                                                if (shareFormDTO instanceof WeChatDTO)
                                                {
                                                    activity.startActivity(
                                                            SocialSharerImpl.this.createWeChatIntent(activity, (WeChatDTO) shareFormDTO));
                                                    return Observable.just(
                                                            (SocialShareResult) new SharedSuccessful(shareFormDTO, new SocialShareResultDTO()
                                                            {
                                                            })); // TODO perhaps wait for a return
                                                }
                                                return socialShareServiceWrapper.shareRx(shareFormDTO)
                                                        .map(new Func1<SocialShareResultDTO, SocialShareResult>()
                                                        {
                                                            @Override public SocialShareResult call(SocialShareResultDTO result)
                                                            {
                                                                return new SharedSuccessful(shareFormDTO, result);
                                                            }
                                                        });

                                            case NO:
                                                return Observable.error(new CannotShareException("Cannot share this"));

                                            case NEED_AUTH:
                                                return socialShareVerifier.getNeedAuthSocialNetworks(
                                                        pair.second,
                                                        shareFormDTO)
                                                        .map(new Func1<List<SocialNetworkEnum>, SocialShareResult>()
                                                        {
                                                            @Override public SocialShareResult call(List<SocialNetworkEnum> socialNetworkEnums)
                                                            {
                                                                return new ConnectRequired(
                                                                        shareFormDTO,
                                                                        socialNetworkEnums);
                                                            }
                                                        });
                                        }
                                        return Observable.error(new IllegalArgumentException("Unhandled ShareType." + shareType));
                                    }
                                });
                    }
                });
    }

    @NonNull public Intent createWeChatIntent(@NonNull Context activityContext, @NonNull WeChatDTO weChatDTO)
    {
        Intent intent = new Intent(activityContext, WXEntryActivity.class);
        WXEntryActivity.putWeChatDTO(intent, weChatDTO);
        return intent;
    }
}
