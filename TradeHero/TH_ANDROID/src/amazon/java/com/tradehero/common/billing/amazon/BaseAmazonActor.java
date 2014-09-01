package com.tradehero.common.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.RequestCodeActor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract public class BaseAmazonActor implements PurchasingListener, AmazonActor
{
    @NotNull protected Context appContext;
    private int activityRequestCode;
    @Nullable protected RequestId currentRequestId;
    @NotNull protected MiddlePurchasingListener middlePurchasingListener;

    //<editor-fold desc="Constructors">
    public BaseAmazonActor(@NotNull Context appContext)
    {
        super();
        this.appContext = appContext;
        this.middlePurchasingListener = new MiddlePurchasingListener(null);
        PurchasingService.registerListener(appContext, middlePurchasingListener);
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
        middlePurchasingListener.setInnerListener(null);
        currentRequestId = null;
    }

    protected void prepareListener()
    {
        middlePurchasingListener.setInnerListener(this);
    }

    protected boolean isMyRequestId(@Nullable RequestId requestId)
    {
        return currentRequestId != null && currentRequestId.equals(requestId);
    }

    @Override public final void onUserDataResponse(@NotNull UserDataResponse userDataResponse)
    {
        if (isMyRequestId(userDataResponse.getRequestId()))
        {
            onMyUserDataResponse(userDataResponse);
        }
    }

    protected void onMyUserDataResponse(@NotNull UserDataResponse userDataResponse)
    {
    }

    @Override public final void onProductDataResponse(@NotNull ProductDataResponse productDataResponse)
    {
        if (isMyRequestId(productDataResponse.getRequestId()))
        {
            onMyProductDataResponse(productDataResponse);
        }
    }

    protected void onMyProductDataResponse(@NotNull ProductDataResponse productDataResponse)
    {
    }

    @Override public final void onPurchaseResponse(@NotNull PurchaseResponse purchaseResponse)
    {
        if (isMyRequestId(purchaseResponse.getRequestId()))
        {
            onMyPurchaseResponse(purchaseResponse);
        }
    }

    protected void onMyPurchaseResponse(@NotNull PurchaseResponse purchaseResponse)
    {
    }

    @Override public final void onPurchaseUpdatesResponse(@NotNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        if (isMyRequestId(purchaseUpdatesResponse.getRequestId()))
        {
            onMyPurchaseUpdatesResponse(purchaseUpdatesResponse);
        }
    }

    protected void onMyPurchaseUpdatesResponse(@NotNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
    }
}
