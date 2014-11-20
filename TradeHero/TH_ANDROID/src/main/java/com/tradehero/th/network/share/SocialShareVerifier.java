package com.tradehero.th.network.share;

import android.support.annotation.NonNull;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.wechat.WeChatDTO;
import com.tradehero.th.api.social.HasSocialNetworkEnum;
import com.tradehero.th.api.social.HasSocialNetworkEnumList;
import com.tradehero.th.api.social.SocialNetworkEnum;
import com.tradehero.th.api.users.UserProfileCompactDTO;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.inject.Inject;

public class SocialShareVerifier
{
    enum CanShareType
    {
        YES,
        NO,
        NEED_AUTH,
        TRY_AND_SEE,
    }

    //<editor-fold desc="Constructors">
    @Inject public SocialShareVerifier()
    {
        super();
    }
    //</editor-fold>

    @NonNull public CanShareType canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull SocialShareFormDTO toShare)
    {
        if (toShare instanceof WeChatDTO)
        {
            return CanShareType.TRY_AND_SEE;
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

    @NonNull protected CanShareType canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull HasSocialNetworkEnum hasSocialNetworkEnum)
    {
        SocialNetworkEnum socialNetwork = hasSocialNetworkEnum.getSocialNetworkEnum();
        if (socialNetwork == null)
        {
            return CanShareType.NO;
        }
        return canShare(currentUserProfile, socialNetwork);
    }

    @NonNull protected CanShareType canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull HasSocialNetworkEnumList hasSocialNetworkEnumList)
    {
        CanShareType canShare;
        for (SocialNetworkEnum socialNetworkEnum : hasSocialNetworkEnumList.getSocialNetworkEnumList())
        {
            canShare = canShare(currentUserProfile, socialNetworkEnum);
            if (canShare != CanShareType.YES)
            {
                return canShare;
            }
        }
        return CanShareType.YES;
    }

    @NonNull protected CanShareType canShare(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull SocialNetworkEnum socialNetworkEnum)
    {
        switch (socialNetworkEnum)
        {
            case FB:
                return currentUserProfile.fbLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

            case LN:
                return currentUserProfile.liLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

            case TW:
                return currentUserProfile.twLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

            case WECHAT:
                throw new IllegalStateException("WeChat is not shared like this");

            case WB:
                return currentUserProfile.wbLinked ? CanShareType.YES : CanShareType.NEED_AUTH;

            case TH:
                throw new IllegalStateException("There is no sharing to TH");

            default:
                throw new IllegalArgumentException("Unhandled SocialNetworkEnum." + socialNetworkEnum)    ;
        }
    }

    @NonNull public List<SocialNetworkEnum> getNeedAuthSocialNetworks(
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
        throw new IllegalStateException("Unhandled type " + toShare.getClass().getName());
    }

    @NonNull public List<SocialNetworkEnum> getNeedAuthSocialNetworks(
            @NonNull UserProfileCompactDTO currentUserProfile,
            @NonNull List<SocialNetworkEnum> candidates)
    {
        List<SocialNetworkEnum> needAuth = new ArrayList<>();
        for (SocialNetworkEnum candidate : candidates)
        {
            if (canShare(currentUserProfile, candidate).equals(CanShareType.NEED_AUTH))
            {
                needAuth.add(candidate);
            }
        }
        return needAuth;
    }
}
