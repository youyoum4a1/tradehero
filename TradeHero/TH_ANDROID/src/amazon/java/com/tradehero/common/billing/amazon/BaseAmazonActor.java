package com.tradehero.common.billing.amazon;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;

abstract public class BaseAmazonActor implements PurchasingListener, AmazonActor
{
    @NonNull protected final Context appContext;
    @NonNull protected final AmazonPurchasingService purchasingService;
    private final int activityRequestCode;
    @Nullable private RequestId currentRequestId;

    //<editor-fold desc="Constructors">
    public BaseAmazonActor(
            int activityRequestCode,
            @NonNull Context appContext,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super();
        this.activityRequestCode = activityRequestCode;
        this.appContext = appContext;
        this.purchasingService = purchasingService;
    }
    //</editor-fold>

    @Override public int getRequestCode()
    {
        return activityRequestCode;
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
        throw new IllegalStateException("You should have overwritten onUserDataResponse method");
    }

    @Override public void onProductDataResponse(@NonNull ProductDataResponse productDataResponse)
    {
        throw new IllegalStateException("You should have overwritten onProductDataResponse method");
    }

    @Override public void onPurchaseResponse(@NonNull PurchaseResponse purchaseResponse)
    {
        throw new IllegalStateException("You should have overwritten onPurchaseResponse method");
    }

    @Override public void onPurchaseUpdatesResponse(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        throw new IllegalStateException("You should have overwritten onPurchaseUpdatesResponse method");
    }
}
