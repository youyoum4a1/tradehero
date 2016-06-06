package com.androidth.general.models.fastfill.jumio;

import android.support.annotation.NonNull;
import com.androidth.general.models.fastfill.DocumentCheckService;
import com.androidth.general.models.fastfill.ScanImageKey;
import com.androidth.general.models.fastfill.ScanReference;
import com.androidth.general.models.fastfill.ScanStatus;
import javax.inject.Inject;
import rx.Observable;

public class NetverifyServiceWrapper implements DocumentCheckService
{
    @NonNull private final NetverifyServiceRx netverifyServiceRx;

    @Inject public NetverifyServiceWrapper(@NonNull NetverifyServiceRx netverifyServiceRx)
    {
        this.netverifyServiceRx = netverifyServiceRx;
    }

    @NonNull public Observable<NetverifyScanStatus> getNetverifyScanStatus(@NonNull NetverifyScanReference scanReference)
    {
        return netverifyServiceRx.getScanStatus(scanReference.getValue());
    }

    @NonNull @Override public Observable<ScanStatus> getScanStatus(@NonNull ScanReference scanReference)
    {
        return getNetverifyScanStatus((NetverifyScanReference) scanReference)
                .cast(ScanStatus.class);
    }

    @NonNull public Observable<NetverifyScanImagesDTO> getScanImages(@NonNull NetverifyScanReference scanReference)
    {
        return netverifyServiceRx.getScanImages(scanReference.getValue());
    }

    @NonNull public String getNetverifyImageUrl(@NonNull NetverifyScanImageKey netverifyScanImageKey)
    {
        return NetverifyConstants.NETVERIFY_END_POINT
                + "/scans/" + netverifyScanImageKey.getScanReference().getValue()
                + "/images/" + netverifyScanImageKey.getClassifier().getValue()
                + (netverifyScanImageKey.getMaskHint() == null
                ? ""
                : "?maskhint=" + netverifyScanImageKey.getMaskHint().name());
    }

    @NonNull @Override public String getImageUrl(@NonNull ScanImageKey scanImageKey)
    {
        return getNetverifyImageUrl((NetverifyScanImageKey) scanImageKey);
    }
}
