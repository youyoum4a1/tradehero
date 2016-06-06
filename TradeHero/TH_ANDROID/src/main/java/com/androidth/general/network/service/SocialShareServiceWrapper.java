package com.androidth.general.network.service;

import android.support.annotation.NonNull;
import com.androidth.general.api.BaseResponseDTO;
import com.androidth.general.api.share.BaseResponseSocialShareResultDTO;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.api.share.SocialShareResultDTO;
import com.androidth.general.api.share.timeline.TimelineItemShareFormDTO;
import com.androidth.general.api.social.ReferralCodeShareFormDTO;
import javax.inject.Inject;
import javax.inject.Singleton;
import rx.Observable;
import rx.functions.Func1;

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
                    .map(new Func1<BaseResponseSocialShareResultDTO, SocialShareResultDTO>()
                    {
                        @Override public SocialShareResultDTO call(BaseResponseSocialShareResultDTO result)
                        {
                            return result;
                        }
                    });
        }
        if (socialShareFormDTO instanceof ReferralCodeShareFormDTO)
        {
            return shareRx((ReferralCodeShareFormDTO) socialShareFormDTO)
                    .map(new Func1<BaseResponseSocialShareResultDTO, SocialShareResultDTO>()
                    {
                        @Override public SocialShareResultDTO call(BaseResponseSocialShareResultDTO result)
                        {
                            return result;
                        }
                    });
        }
        throw new IllegalArgumentException("Unhandled type " + socialShareFormDTO.getClass());
    }

    @NonNull public Observable<BaseResponseSocialShareResultDTO> shareRx(
            @NonNull TimelineItemShareFormDTO timelineItemShareFormDTO)
    {
        return discussionServiceWrapper.shareRx(
                timelineItemShareFormDTO.discussionListKey,
                timelineItemShareFormDTO.timelineItemShareRequestDTO)
                .map(new Func1<BaseResponseDTO, BaseResponseSocialShareResultDTO>()
                {
                    @Override public BaseResponseSocialShareResultDTO call(BaseResponseDTO t1)
                    {
                        return new BaseResponseSocialShareResultDTO(t1);
                    }
                });
    }

    @NonNull public Observable<BaseResponseSocialShareResultDTO> shareRx(@NonNull ReferralCodeShareFormDTO shareFormDTO)
    {
        return socialServiceWrapper.shareReferralCodeRx(shareFormDTO)
                .map(new Func1<BaseResponseDTO, BaseResponseSocialShareResultDTO>()
                {
                    @Override public BaseResponseSocialShareResultDTO call(BaseResponseDTO t1)
                    {
                        return new BaseResponseSocialShareResultDTO(t1);
                    }
                });
    }
}
