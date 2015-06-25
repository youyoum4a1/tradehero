package com.tradehero.th.models.fastfill;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import com.tradehero.common.activities.ActivityResultRequester;
import rx.Observable;

public interface FastFillUtil extends ActivityResultRequester
{
    void fastFill(@NonNull Activity activity);
    void fastFill(@NonNull Fragment fragment);
    @NonNull Observable<ScannedDocument> getScannedDocumentObservable();
}
