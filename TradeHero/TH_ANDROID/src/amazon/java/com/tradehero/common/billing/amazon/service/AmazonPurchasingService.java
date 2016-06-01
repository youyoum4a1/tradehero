package com.tradehero.common.billing.amazon.service;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.LruCache;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserDataResponse;
import com.ayondo.academy.billing.amazon.AmazonAlertDialogRxUtil;
import com.ayondo.academy.rx.EmptyAction1;
import com.ayondo.academy.rx.dialog.OnDialogClickEvent;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import timber.log.Timber;

@Singleton public class AmazonPurchasingService
        implements PurchasingListener
{
    private static final int DEFAULT_MAP_LENGTH = 30;

    @NonNull private final LruCache<RequestId, PurchasingListener> purchasingListeners;
    @NonNull private final LruCache<RequestId, Object> waitingResponses;

    //<editor-fold desc="Constructors">
    @Inject public AmazonPurchasingService(
            @NonNull Context appContext,
            @NonNull Provider<Activity> activityProvider,
            @NonNull AmazonAlertDialogRxUtil dialogUtil)
    {
        super();
        this.purchasingListeners = new LruCache<>(DEFAULT_MAP_LENGTH);
        this.waitingResponses = new LruCache<>(DEFAULT_MAP_LENGTH);
        PurchasingService.registerListener(appContext, this);
        Timber.e(new Exception("Sandbox is " + PurchasingService.IS_SANDBOX_MODE), "Sandbox is %s", PurchasingService.IS_SANDBOX_MODE);
        if (PurchasingService.IS_SANDBOX_MODE)
        {
            dialogUtil.popSandboxModeAndHandle(activityProvider.get())
                    .subscribe(
                            new EmptyAction1<OnDialogClickEvent>(),
                            new EmptyAction1<Throwable>());
        }
    }
    //</editor-fold>

    public void unregisterListener(@NonNull PurchasingListener listener)
    {
        for (RequestId requestId : new HashSet<>(purchasingListeners.snapshot().keySet()))
        {
            if (listener.equals(purchasingListeners.get(requestId)))
            {
                purchasingListeners.remove(requestId);
            }
        }
    }

    public void unregisterListener(@NonNull RequestId requestId)
    {
        purchasingListeners.remove(requestId);
    }

    @NonNull public RequestId getUserData(@NonNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getUserData();
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    @NonNull public RequestId purchase(@NonNull String sku, @NonNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.purchase(sku);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    @NonNull public RequestId getProductData(@NonNull Set<String> skus, @NonNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getProductData(skus);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    // TODO implement further calling of purchases within this class
    @NonNull public RequestId getPurchaseUpdates(boolean reset, @NonNull PurchasingListener listener)
    {
        RequestId requestId = PurchasingService.getPurchaseUpdates(reset);
        purchasingListeners.put(requestId, listener);
        callWaitingResponses();
        return requestId;
    }

    public void notifyFulfillment(@NonNull String receiptId, @NonNull FulfillmentResult fulfillmentResult)
    {
        PurchasingService.notifyFulfillment(receiptId, fulfillmentResult);
    }

    @Override public void onUserDataResponse(@NonNull UserDataResponse userDataResponse)
    {
        putWaitingResponse(userDataResponse.getRequestId(), userDataResponse);
        callWaitingResponses();
    }

    @Override public void onProductDataResponse(@NonNull ProductDataResponse productDataResponse)
    {
        putWaitingResponse(productDataResponse.getRequestId(), productDataResponse);
        callWaitingResponses();
    }

    @Override public void onPurchaseResponse(@NonNull PurchaseResponse purchaseResponse)
    {
        putWaitingResponse(purchaseResponse.getRequestId(), purchaseResponse);
        callWaitingResponses();
    }

    @Override public void onPurchaseUpdatesResponse(@NonNull PurchaseUpdatesResponse purchaseUpdatesResponse)
    {
        putWaitingResponse(purchaseUpdatesResponse.getRequestId(), purchaseUpdatesResponse);
        callWaitingResponses();
    }

    protected void putWaitingResponse(@NonNull RequestId requestId, @NonNull Object response)
    {
        waitingResponses.put(requestId, response);
    }

    protected void callWaitingResponses()
    {
        PurchasingListener listener;
        Object response;
        for (Map.Entry<RequestId, Object> requestEntry : new HashSet<>(waitingResponses.snapshot().entrySet()))
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
        }
    }
}
