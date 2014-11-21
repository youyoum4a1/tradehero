package com.tradehero.th.fragments.social.facebook;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.facebook.FacebookException;
import com.facebook.FacebookOperationCanceledException;
import com.facebook.widget.WebDialog;
import com.tradehero.common.utils.THToast;
import com.tradehero.th.R;
import com.tradehero.th.utils.Constants;
import rx.Observer;

public class SubscriberOnCompleteListener implements WebDialog.OnCompleteListener
{
    @NonNull private Observer<? super String> observer;

    //<editor-fold desc="Constructors">
    public SubscriberOnCompleteListener(@NonNull Observer<? super String> observer)
    {
        this.observer = observer;
    }
    //</editor-fold>

    @Override public void onComplete(@Nullable Bundle values, @Nullable FacebookException error)
    {
        if (error != null)
        {
            if (error instanceof FacebookOperationCanceledException && !Constants.RELEASE)
            {
                THToast.show(R.string.invite_friend_request_canceled);
            }
            observer.onError(error);
        }
        else if (values != null)
        {
            String requestId = values.getString(WebDialogConstants.RESULT_BUNDLE_KEY_REQUEST_ID);
            if (requestId != null && !Constants.RELEASE)
            {
                THToast.show(R.string.invite_friend_request_sent);
            }
            else if (!Constants.RELEASE)
            {
                THToast.show(R.string.invite_friend_request_canceled);
            }
            observer.onNext(requestId);
        }
        else
        {
            observer.onError(new NullPointerException("Both values and error were null"));
        }
    }
}
