package com.androidth.general.models.fastfill.jumio;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import com.jumio.core.enums.JumioDataCenter;
import com.jumio.core.exceptions.MissingPermissionException;
import com.jumio.core.exceptions.PlatformNotSupportedException;
import com.jumio.nv.NetverifyDocumentData;
import com.jumio.nv.NetverifySDK;
import com.jumio.nv.data.document.NVDocumentType;
import com.neovisionaries.i18n.CountryCode;
import com.tradehero.th.R;
import com.androidth.general.api.users.CurrentUserId;
import com.androidth.general.models.fastfill.FastFillUtil;
import com.androidth.general.models.fastfill.IdentityScannedDocumentType;
import com.androidth.general.models.fastfill.ScannedDocument;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.subjects.BehaviorSubject;
import timber.log.Timber;

/**
 * https://www.jumio.com/downloads/pdf/fastfill_netverify_mobile_implementation_guide_for_android_v1_5_0_jumio_sdk.pdf
 */
public class NetverifyFastFillUtil implements FastFillUtil
{
    public static final JumioDataCenter DATA_CENTER = JumioDataCenter.US;
    public static final int NET_VERIFY_REQUEST_CODE = R.string.net_verify_request_code & 0xFF;

    @NonNull private final CurrentUserId currentUserId;
    @NonNull private final NetverifyServiceWrapper netverifyServiceWrapper;
    @NonNull private final BehaviorSubject<ScannedDocument> scannedDocumentSubject;
    private NetverifySDK netverifySDK;
    private Map<IdentityScannedDocumentType, NVDocumentType> documentTypeMap;

    @Inject public NetverifyFastFillUtil(@NonNull CurrentUserId currentUserId,
                                         @NonNull NetverifyServiceWrapper netverifyServiceWrapper)
    {
        this.currentUserId = currentUserId;
        this.netverifyServiceWrapper = netverifyServiceWrapper;
        this.scannedDocumentSubject = BehaviorSubject.create();
        this.documentTypeMap = new HashMap<>();
        documentTypeMap.put(IdentityScannedDocumentType.DRIVER_LICENSE, NVDocumentType.DRIVER_LICENSE);
        documentTypeMap.put(IdentityScannedDocumentType.IDENTITY_CARD, NVDocumentType.IDENTITY_CARD);
        documentTypeMap.put(IdentityScannedDocumentType.PASSPORT, NVDocumentType.PASSPORT);
    }

    @NonNull public NetverifySDK getNetverifySDK(@NonNull Activity activity)
    {
        NetverifySDK netverifySDK;
        if (isSupported(activity))
        {
            try
            {
                netverifySDK = NetverifySDK.create(activity, NetverifyConstants.NET_VERIFY_MERCHANT_API_TOKEN,
                        NetverifyConstants.NET_VERIFY_ACTIVE_API_SECRET, DATA_CENTER);
                netverifySDK.setCustomerId(currentUserId.get().toString());
            }
            catch ( PlatformNotSupportedException e)
            {
                Timber.e(e, "Failed to initialise NetverifySDK");
                throw new IllegalArgumentException("Failed to initialise NetverifySDK");
            }
        }
        else
        {
            throw new IllegalArgumentException("Netverify is not supported");
        }
        return netverifySDK;
    }

    public static boolean isSupported(@NonNull Activity activity)
    {
        boolean supported = NetverifySDK.isSupportedPlatform();
        if (!supported)
        {
            Timber.e(new Exception(), "Netverify %s is not supported on this device", NetverifySDK.getSDKVersion());
        }
        return supported;
    }

    @NonNull @Override public Observable<Boolean> isAvailable(@NonNull Activity activity)
    {
        return Observable.just(isSupported(activity));
    }

    @Override public void fastFill(@NonNull Activity activity)
    {
        fastFill(activity, null);
    }

    @Override public void fastFill(@NonNull Activity activity, @Nullable IdentityScannedDocumentType documentType)
    {
        fastFill(activity, getNetverifySDK(activity), documentType);
    }

    public void fastFill(@NonNull Activity activity, @NonNull NetverifySDK netverifySDK, @Nullable IdentityScannedDocumentType documentType)
    {
        fastFill(activity, netverifySDK, documentType, null);
    }

    @Override public void fastFill(@NonNull Activity activity, @Nullable IdentityScannedDocumentType documentType, @Nullable
    CountryCode countryCode)
    {
        fastFill(activity, getNetverifySDK(activity), documentType, countryCode);
    }

    public void fastFill(@NonNull Activity activity, @NonNull NetverifySDK netverifySDK, @Nullable IdentityScannedDocumentType documentType, @Nullable
    CountryCode country)
    {
        if (documentType != null)
        {
            //TODO This is just a dummy fix
            ArrayList<NVDocumentType> docType = new ArrayList<NVDocumentType>();
            docType.add(documentTypeMap.get(documentType));
            netverifySDK.setPreselectedDocumentTypes(docType);
        }

        if (country != null)
        {
            netverifySDK.setPreselectedCountry(country.getAlpha3());
        }

        netverifySDK.setRequireVerification(true);
        this.netverifySDK = netverifySDK;
        try {
            activity.startActivityForResult(netverifySDK.getIntent(), NET_VERIFY_REQUEST_CODE);
        } catch (MissingPermissionException e) {
            e.printStackTrace();
        }
    }

    @Override public void fastFill(@NonNull Fragment fragment)
    {
        fastFill(fragment, getNetverifySDK(fragment.getActivity()));
    }

    public void fastFill(@NonNull Fragment fragment, @NonNull NetverifySDK netverifySDK)
    {
        this.netverifySDK = netverifySDK;
        try {
            fragment.startActivityForResult(netverifySDK.getIntent(), NET_VERIFY_REQUEST_CODE);
        } catch (MissingPermissionException e) {
            e.printStackTrace();
        }
    }

    @Override public void onActivityResult(@NonNull Activity activity, int requestCode, final int resultCode, Intent data)
    {
        if (requestCode == NET_VERIFY_REQUEST_CODE)
        {
            if (resultCode == activity.RESULT_OK)
            {
                NetverifyScanReference scanReference = new NetverifyScanReference(data.getStringExtra(NetverifySDK.EXTRA_SCAN_REFERENCE));
                scannedDocumentSubject.onNext(new NetverifyScannedDocument(
                        scanReference,
                        data.<NetverifyDocumentData>getParcelableExtra(NetverifySDK.EXTRA_SCAN_DATA)));
            }
            else if (resultCode == activity.RESULT_CANCELED)
            {
                //Consecutive scan will fail if we have onError
            }
            if (this.netverifySDK != null)
            {
                this.netverifySDK.destroy();
            }
        }
    }

    @NonNull @Override public Observable<ScannedDocument> getScannedDocumentObservable()
    {
        return scannedDocumentSubject.asObservable();
    }
}
