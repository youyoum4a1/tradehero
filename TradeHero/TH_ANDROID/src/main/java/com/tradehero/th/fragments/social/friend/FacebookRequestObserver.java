package com.tradehero.th.fragments.social.friend;

import android.content.Context;
import android.support.annotation.NonNull;
import com.tradehero.th.api.BaseResponseDTO;

public class FacebookRequestObserver extends RequestObserver<BaseResponseDTO>
{
    public FacebookRequestObserver(@NonNull Context context)
    {
        super(context);
    }

    @Override public void onNext(BaseResponseDTO baseResponseDTO)
    {
        this.success();
    }

    public void success()
    {
    }

    public void failure()
    {
    }

    @Override public void onError(Throwable e)
    {
        this.failure();
    }
}
