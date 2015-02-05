package com.tradehero.th.network.share;

import android.support.annotation.NonNull;
import com.tradehero.th.api.share.SocialShareFormDTO;
import com.tradehero.th.network.share.dto.SocialShareResult;
import rx.Observable;

public interface SocialSharer
{
    @NonNull Observable<SocialShareResult> share(@NonNull SocialShareFormDTO shareFormDTO);
}
