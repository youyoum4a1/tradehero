package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseAmazonActor implements PurchasingListener, AmazonActor
{
    @NotNull protected final Context appContext;
    @NotNull protected final AmazonPurchasingService purchasingService;
    private int activityRequestCode;
    @Nullable private RequestId currentRequestId;

    //<editor-fold desc="Constructors">
    public BaseAmazonActor(
            @NotNull Context appContext,
            @NotNull AmazonPurchasingService purchasingService)
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

    @Override public void onUserDataResponse(@NotNull UserDataResponse userDataResponse)
    {
    }

    @Override public void onProductDataResponse(@NotNull ProductDataResponse productDataResponse)
    {
    }

    @Override public void onPurchaseResponse(@NotNull PurchaseResponse purchaseResponse)
    {
    }

    @Override public void onPurchaseUpdatesResponse(@NotNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
    }
}
