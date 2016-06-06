package com.androidth.general.models.fastfill;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.androidth.general.common.activities.ActivityResultRequester;
import com.neovisionaries.i18n.CountryCode;

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
