package com.tradehero.th.models.fastfill.jumio;

import android.support.annotation.NonNull;
import javax.inject.Inject;
import rx.Observable;

public class NetverifyServiceWrapper
{
    @NonNull private final NetverifyServiceRx netverifyServiceRx;

    @Inject public NetverifyServiceWrapper(@NonNull NetverifyServiceRx netverifyServiceRx)
    {
        this.netverifyServiceRx = netverifyServiceRx;
    }

    @NonNull public Observable<NetverifyScanStatus> getScanStatus(@NonNull NetverifyScanReference scanReference)
    {
        return netverifyServiceRx.getScanStatus(scanReference.getValue());
    }

    @NonNull public Observable<NetverifyScanImagesDTO> getScanImages(@NonNull NetverifyScanReference scanReference)
    {
        return netverifyServiceRx.getScanImages(scanReference.getValue());
    }

    @NonNull public String getImageUrl(@NonNull NetverifyScanImageKey netverifyScanImageKey)
    {
        return NetverifyConstants.NETVERIFY_END_POINT
                + "/scans/" + netverifyScanImageKey.getScanReference().getValue()
                + "/images/" + netverifyScanImageKey.getClassifier().getValue()
                + (netverifyScanImageKey.getMaskHint() == null
                ? ""
                : "?maskhint=" + netverifyScanImageKey.getMaskHint().name());
    }
}
