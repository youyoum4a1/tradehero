package com.androidth.general.network.share;

import android.support.annotation.NonNull;
import com.androidth.general.api.share.SocialShareFormDTO;
import com.androidth.general.network.share.dto.SocialShareResult;
import rx.Observable;

public interface SocialSharer
{
    @NonNull Observable<SocialShareResult> share(@NonNull SocialShareFormDTO shareFormDTO);
}
