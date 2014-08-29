package com.tradehero.common.billing.amazon;

import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.UserDataResponse;
import org.jetbrains.annotations.Nullable;

/**
 * This class exists to work around the fact that there is no unregister
 * method from PurchasingService
 */
public class MiddlePurchasingListener implements PurchasingListener
{
    @Nullable private PurchasingListener innerListener;

    public MiddlePurchasingListener(@Nullable PurchasingListener innerListener)
    {
        this.innerListener = innerListener;
    }

    public void setInnerListener(@Nullable PurchasingListener innerListener)
    {
        this.innerListener = innerListener;
    }

    @Override public void onUserDataResponse(UserDataResponse userDataResponse)
    {
        PurchasingListener innerCopy = innerListener;
        if (innerCopy != null)
        {
            innerCopy.onUserDataResponse(userDataResponse);
        }
    }

    @Override public void onProductDataResponse(ProductDataResponse productDataResponse)
    {
        PurchasingListener innerCopy = innerListener;
        if (innerCopy != null)
        {
            innerCopy.onProductDataResponse(productDataResponse);
        }
    }

    @Override public void onPurchaseResponse(PurchaseResponse purchaseResponse)
    {
        PurchasingListener innerCopy = innerListener;
        if (innerCopy != null)
        {
            innerCopy.onPurchaseResponse(purchaseResponse);
        }
    }

    @Override public void onPurchaseUpdatesResponse(PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        PurchasingListener innerCopy = innerListener;
        if (innerCopy != null)
        {
            innerCopy.onPurchaseUpdatesResponse(purchaseUpdatesResponse);
        }
    }
}
