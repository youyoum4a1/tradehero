package com.ayondo.academy.models.fastfill;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.common.activities.ActivityResultRequester;
import rx.Observable;

public interface FastFillUtil extends ActivityResultRequester
{
    @NonNull Observable<Boolean> isAvailable(@NonNull Activity activity);
    void fastFill(@NonNull Activity activity);
    void fastFill(@NonNull Activity activity, @Nullable IdentityScannedDocumentType documentType);
    void fastFill(@NonNull Activity activity, @Nullable IdentityScannedDocumentType documentType, @Nullable CountryCode countryCode);
    void fastFill(@NonNull Fragment fragment);
    @NonNull Observable<ScannedDocument> getScannedDocumentObservable();
}
