package com.tradehero.common.billing.amazon;

import android.content.Context;
import android.util.LruCache;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;
import timber.log.Timber;

@Singleton public class AmazonPurchasingService
    implements PurchasingListener
{
    private static final int DEFAULT_MAP_LENGTH = 30;

    @NotNull private final LruCache<RequestId, PurchasingListener> purchasingListeners;
    @NotNull private final LruCache<RequestId, Object> waitingResponses;

    //<editor-fold desc="Constructors">
    @Inject public AmazonPurchasingService(@NotNull Context appContext)
    {
        super();
        this.purchasingListeners = new LruCache<>(DEFAULT_MAP_LENGTH);
        this.waitingResponses = new LruCache<>(DEFAULT_MAP_LENGTH);
        PurchasingService.registerListener(appContext, this);
        Timber.e(new Exception("Sandbox is " + PurchasingService.IS_SANDBOX_MODE), "Sandbox is %s", PurchasingService.IS_SANDBOX_MODE);
    }
    //</editor-fold>

    public void unregisterListener(@NotNull PurchasingListener listener)
    {
        for (RequestId requestId : new HashSet<>(purchasingListeners.snapshot().keySet()))
        {
            if (listener.equals(purchasingListeners.get(requestId)))
            {
                purchasingListeners.remove(requestId);
            }
        }
    }

    public void unregisterListener(@NotNull RequestId requestId)
    {
        purchasingListeners.remove(requestId);
    }

    @NotNull public RequestId getUserData(@NotNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getUserData();
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    @NotNull public RequestId purchase(@NotNull String sku, @NotNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.purchase(sku);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    @NotNull public RequestId getProductData(@NotNull Set<String> skus, @NotNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getProductData(skus);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    // TODO implement further calling of purchases within this class
    @NotNull public RequestId getPurchaseUpdates(boolean reset, @NotNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getPurchaseUpdates(reset);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    public void notifyFulfillment(@NotNull String receiptId, @NotNull FulfillmentResult fulfillmentResult)
    {
        PurchasingService.notifyFulfillment(receiptId, fulfillmentResult);
    }

    @Override public void onUserDataResponse(@NotNull UserDataResponse userDataResponse)
    {
        putWaitingResponse(userDataResponse.getRequestId(), userDataResponse);
        callWaitingResponses();
    }

    @Override public void onProductDataResponse(@NotNull ProductDataResponse productDataResponse)
    {
        putWaitingResponse(productDataResponse.getRequestId(), productDataResponse);
        callWaitingResponses();
    }

    @Override public void onPurchaseResponse(@NotNull PurchaseResponse purchaseResponse)
    {
        putWaitingResponse(purchaseResponse.getRequestId(), purchaseResponse);
        callWaitingResponses();
    }

    @Override public void onPurchaseUpdatesResponse(@NotNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        putWaitingResponse(purchaseUpdatesResponse.getRequestId(), purchaseUpdatesResponse);
        callWaitingResponses();
    }

    protected void putWaitingResponse(@NotNull RequestId requestId, @NotNull Object response)
    {
        waitingResponses.put(requestId, response);
    }

    protected void callWaitingResponses()
    {
        PurchasingListener listener;
        Object response;
        for (@NotNull Map.Entry<RequestId, Object> requestEntry : new HashSet<>(waitingResponses.snapshot().entrySet()))
        {
            listener = purchasingListeners.get(requestEntry.getKey());
            response = requestEntry.getValue();
            if (listener != null)
            {
                purchasingListeners.remove(requestEntry.getKey());
                waitingResponses.remove(requestEntry.getKey());
                if (response instanceof UserDataResponse)
                {
                    listener.onUserDataResponse((UserDataResponse) response);
                }
                else if (response instanceof ProductDataResponse)
                {
                    listener.onProductDataResponse((ProductDataResponse) response);
                }
                else if (response instanceof PurchaseResponse)
                {
                    listener.onPurchaseResponse((PurchaseResponse) response);
                }
                else
                {
                    listener.onPurchaseUpdatesResponse((PurchaseUpdatesResponse) response);
                }
            }

            // HACK because RequestId seems messed up on App Tester
            {
                if (response instanceof PurchaseUpdatesResponse)
                {
                    for (RequestId listenerId : new ArrayList<>(purchasingListeners.snapshot().keySet()))
                    {
                        listener = purchasingListeners.get(listenerId);
                        if (BaseAmazonPurchaseFetcher.class.isAssignableFrom(listener.getClass()))
                        {
                            purchasingListeners.remove(listenerId);
                            waitingResponses.remove(requestEntry.getKey());
                            listener.onPurchaseUpdatesResponse((PurchaseUpdatesResponse) response);
                        }
                    }
                }
            }
        }
    }
}
