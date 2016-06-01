package com.ayondo.academy.network.share;

import android.support.annotation.NonNull;
import com.ayondo.academy.api.share.SocialShareFormDTO;
import com.ayondo.academy.network.share.dto.SocialShareResult;
import rx.Observable;

public interface SocialSharer
{
    @NonNull Observable<SocialShareResult> share(@NonNull SocialShareFormDTO shareFormDTO);
}
