package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

abstract public class BaseAmazonActor implements PurchasingListener, AmazonActor
{
    @NonNull protected final Context appContext;
    @NonNull protected final AmazonPurchasingService purchasingService;
    private int activityRequestCode;
    @Nullable private RequestId currentRequestId;

    //<editor-fold desc="Constructors">
    public BaseAmazonActor(
            @NonNull Context appContext,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super();
        this.appContext = appContext;
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return activityRequestCode;
    }

    protected void setRequestCode(int requestCode)
    {
        this.activityRequestCode = requestCode;
    }

    @Override public void onDestroy()
    {
        if (currentRequestId != null)
        {
            purchasingService.unregisterListener(currentRequestId);
        }
        currentRequestId = null;
    }

    @Override public void onUserDataResponse(@NonNull UserDataResponse userDataResponse)
    {
    }

    @Override public void onProductDataResponse(@NonNull ProductDataResponse productDataResponse)
    {
    }

    @Override public void onPurchaseResponse(@NonNull PurchaseResponse purchaseResponse)
    {
    }

    @Override public void onPurchaseUpdatesResponse(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
    }
}
