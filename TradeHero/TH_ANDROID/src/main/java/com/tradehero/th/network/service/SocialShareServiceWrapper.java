package com.tradehero.th.network.service;

import android.support.annotation.NonNull;
import com.tradehero.th.api.share.BaseResponseSocialShareResultDTO;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.api.share.SocialShareResultDTO;
import com.tradehero.th.api.share.timeline.TimelineItemShareFormDTO;
import com.tradehero.th.api.social.ReferralCodeShareFormDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;

@Singleton public class SocialShareServiceWrapper
{
    @NonNull private final DiscussionServiceWrapper discussionServiceWrapper;
    @NonNull private final SocialServiceWrapper socialServiceWrapper;

    //<editor-fold desc="Constructors">
    @Inject public SocialShareServiceWrapper(
            @NonNull DiscussionServiceWrapper discussionServiceWrapper,
            @NonNull SocialServiceWrapper socialServiceWrapper)
    {
        this.discussionServiceWrapper = discussionServiceWrapper;
        this.socialServiceWrapper = socialServiceWrapper;
    }
    //</editor-fold>

    @NonNull public Observable<SocialShareResultDTO> shareRx(@NonNull SocialShareFormDTO socialShareFormDTO)
    {
        if (socialShareFormDTO instanceof TimelineItemShareFormDTO)
        {
            return shareRx((TimelineItemShareFormDTO) socialShareFormDTO)
                    .map(result -> result);
        }
        if (socialShareFormDTO instanceof ReferralCodeShareFormDTO)
        {
            return shareRx((ReferralCodeShareFormDTO) socialShareFormDTO)
                    .map(result -> result);
        }
        throw new IllegalArgumentException("Unhandled type " + socialShareFormDTO.getClass());
    }

    @NonNull public Observable<BaseResponseSocialShareResultDTO> shareRx(
            @NonNull TimelineItemShareFormDTO timelineItemShareFormDTO)
    {
        return discussionServiceWrapper.shareRx(
                timelineItemShareFormDTO.discussionListKey,
                timelineItemShareFormDTO.timelineItemShareRequestDTO)
                .map(BaseResponseSocialShareResultDTO::new);
    }

    @NonNull public Observable<BaseResponseSocialShareResultDTO> shareRx(@NonNull ReferralCodeShareFormDTO shareFormDTO)
    {
        return socialServiceWrapper.shareReferralCodeRx(shareFormDTO)
                .map(BaseResponseSocialShareResultDTO::new);
    }
}
