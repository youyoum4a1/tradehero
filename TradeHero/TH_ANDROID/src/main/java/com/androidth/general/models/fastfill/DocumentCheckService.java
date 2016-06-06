package com.androidth.general.models.fastfill;

import android.support.annotation.NonNull;

import rx.Observable;

public interface DocumentCheckService
{
    @NonNull Observable<ScanStatus> getScanStatus(@NonNull ScanReference scanReference);

    @NonNull String getImageUrl(@NonNull ScanImageKey scanImageKey);
}
