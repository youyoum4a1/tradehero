package com.androidth.general.network.share;

import android.app.Activity;
import android.support.annotation.NonNull;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.api.share.wechat.WeChatDTO;
import com.androidth.general.api.social.HasSocialNetworkEnum;
import com.androidth.general.api.social.HasSocialNetworkEnumList;
import com.androidth.general.api.social.SocialNetworkEnum;
import com.androidth.general.api.users.UserProfileCompactDTO;
import com.androidth.general.auth.FacebookAuthenticationProvider;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;
import javax.inject.Provider;
import rx.Observable;
import rx.functions.Func1;

public class SocialShareVerifier
{
    enum CanShareType
    {
        YES,
        NO,
        NEED_AUTH,
        TRY_AND_SEE,
    }

    @NonNull private final FacebookAuthenticationProvider facebookAuthenticationProvider;
    @NonNull private final Provider<Activity> activityProvider;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareVerifier(
            @NonNull FacebookAuthenticationProvider facebookAuthenticationProvider,
            @NonNull Provider<Activity> activityProvider)
    {
        super();
        this.facebookAuthenticationProvider = facebookAuthenticationProvider;
        this.activityProvider = activityProvider;
    }
    //</editor-fold>

    @NonNull public Observable<CanShareType> canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull SocialShareFormDTO toShare)
    {
        if (toShare instanceof WeChatDTO)
        {
            return Observable.just(CanShareType.TRY_AND_SEE);
        }
        if (toShare instanceof HasSocialNetworkEnum)
        {
            return canShare(currentUserProfile, (HasSocialNetworkEnum) toShare);
        }
        if (toShare instanceof HasSocialNetworkEnumList)
        {
            return canShare(currentUserProfile, (HasSocialNetworkEnumList) toShare);
        }
        throw new IllegalStateException("Unhandled type " + toShare.getClass().getName());
    }

    @NonNull protected Observable<CanShareType> canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull HasSocialNetworkEnum hasSocialNetworkEnum)
    {
        SocialNetworkEnum socialNetwork = hasSocialNetworkEnum.getSocialNetworkEnum();
        if (socialNetwork == null)
        {
            return Observable.just(CanShareType.NO);
        }
        return canShare(currentUserProfile, socialNetwork);
    }

    @NonNull protected Observable<CanShareType> canShare(
            @NonNull final UserProfileCompactDTO currentUserProfile,
            @NonNull HasSocialNetworkEnumList hasSocialNetworkEnumList)
    {
        return Observable.from(hasSocialNetworkEnumList.getSocialNetworkEnumList())
                .flatMap(new Func1<SocialNetworkEnum, Observable<CanShareType>>()
                {
                    @Override public Observable<CanShareType> call(SocialNetworkEnum socialNetworkEnum)
                    {
                        return canShare(currentUserProfile, socialNetworkEnum);
                    }
                })
                .toList()
                .map(new Func1<List<CanShareType>, CanShareType>()
                {
                    @Override public CanShareType call(List<CanShareType> canShareTypes)
                    {
                        for (CanShareType canShare : canShareTypes)
                        {
                            if (canShare != CanShareType.YES)
                            {
                                return canShare;
                            }
                        }
                        return CanShareType.YES;
                    }
                });
    }

    @NonNull protected Observable<CanShareType> canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
        {
            case FB:
                if (!currentUserProfile.fbLinked)
                {
                    return Observable.just(CanShareType.NEED_AUTH);
                }
                return facebookAuthenticationProvider.canShare(activityProvider.get())
                        .map(new Func1<Boolean, CanShareType>()
                        {
                            @Override public CanShareType call(Boolean aBoolean)
                            {
                                return aBoolean ? CanShareType.YES : CanShareType.NEED_AUTH;
                            }
                        });

            case LN:
                return Observable.just(currentUserProfile.liLinked ? CanShareType.YES : CanShareType.NEED_AUTH);

            case TW:
                return Observable.just(currentUserProfile.twLinked ? CanShareType.YES : CanShareType.NEED_AUTH);

            case WECHAT:
                return Observable.error(new IllegalStateException("WeChat is not shared like this"));

            case WB:
                return Observable.just(currentUserProfile.wbLinked ? CanShareType.YES : CanShareType.NEED_AUTH);

            case TH:
                return Observable.error(new IllegalStateException("There is no sharing to TH"));

            default:
                return Observable.error(new IllegalArgumentException("Unhandled SocialNetworkEnum." + socialNetworkEnum));
        }
    }

    @NonNull public Observable<List<SocialNetworkEnum>> getNeedAuthSocialNetworks(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull SocialShareFormDTO toShare)
    {
        if (toShare instanceof HasSocialNetworkEnum)
        {
            return getNeedAuthSocialNetworks(
                    currentUserProfile,
                    Arrays.asList(((HasSocialNetworkEnum) toShare).getSocialNetworkEnum()));
        }
        if (toShare instanceof HasSocialNetworkEnumList)
        {
            return getNeedAuthSocialNetworks(
                    currentUserProfile,
                    ((HasSocialNetworkEnumList) toShare).getSocialNetworkEnumList());
        }
        return Observable.error(new IllegalStateException("Unhandled type " + toShare.getClass().getName()));
    }

    @NonNull public Observable<List<SocialNetworkEnum>> getNeedAuthSocialNetworks(
            @NonNull final UserProfileCompactDTO currentUserProfile,
            @NonNull List<SocialNetworkEnum> candidates)
    {
        return Observable.from(candidates)
                .flatMap(new Func1<SocialNetworkEnum, Observable<SocialNetworkEnum>>()
                {
                    @Override public Observable<SocialNetworkEnum> call(final SocialNetworkEnum candidate)
                    {
                        return canShare(currentUserProfile, candidate)
                                .flatMap(new Func1<CanShareType, Observable<SocialNetworkEnum>>()
                                {
                                    @Override public Observable<SocialNetworkEnum> call(CanShareType canShare)
                                    {
                                        if (canShare.equals(CanShareType.NEED_AUTH))
                                        {
                                            return Observable.just(candidate);
                                        }
                                        return Observable.empty();
                                    }
                                });
                    }
                })
                .toList();
    }
}
