package com.tradehero.th.billing.amazon;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.internal.model.ProductBuilder;
import com.amazon.device.iap.internal.model.ProductDataResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseUpdatesResponseBuilder;
import com.amazon.device.iap.internal.model.ReceiptBuilder;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.internal.model.UserDataResponseBuilder;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserData;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.th.api.users.CurrentUserId;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;

@Singleton public class AmazonPurchasingServiceDummy extends AmazonPurchasingService
{
    @NonNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public AmazonPurchasingServiceDummy(
            @NonNull Context appContext,
            @NonNull Provider<Activity> activityProvider,
            @NonNull AmazonAlertDialogRxUtil dialogUtil,
            @NonNull CurrentUserId currentUserId)
    {
        super(appContext, activityProvider, dialogUtil);
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @NonNull public RequestId getUserData(@NonNull PurchasingListener listener)
    {
        RequestId requestId = super.getUserData(listener);
        putWaitingResponse(requestId, new UserDataResponseBuilder()
                .setRequestId(requestId)
                .setUserData(getUserData())
                .setRequestStatus(UserDataResponse.RequestStatus.SUCCESSFUL)
                .build());
        callWaitingResponses();
        return requestId;
    }

    @NonNull protected UserData getUserData()
    {
        return new UserDataBuilder()
                .setUserId("amazonUserId" + currentUserId.get())
                .setMarketplace("SG")
                .build();
    }

    @NonNull public RequestId purchase(@NonNull String sku, @NonNull PurchasingListener listener)
    {
        RequestId requestId = super.purchase(sku, listener);
        putWaitingResponse(requestId, new PurchaseResponseBuilder()
                .setRequestId(requestId)
                .setRequestStatus(PurchaseResponse.RequestStatus.SUCCESSFUL)
                .setReceipt(new ReceiptBuilder()
                        .setReceiptId("receiptId1")
                        .setSku(sku)
                        .setProductType(ProductType.CONSUMABLE)
                        .setPurchaseDate(new Date())
                        .setCancelDate(null)
                        .build())
                .setUserData(getUserData())
                .build());
        callWaitingResponses();
        return requestId;
    }

    @NonNull public RequestId getProductData(@NonNull Set<String> skus, @NonNull PurchasingListener listener)
    {
        RequestId requestId = super.getProductData(skus, listener);
        putWaitingResponse(requestId, new ProductDataResponseBuilder()
                .setRequestId(requestId)
                .setRequestStatus(ProductDataResponse.RequestStatus.SUCCESSFUL)
                .setProductData(getProductData(skus))
                .setUnavailableSkus(new HashSet<String>())
                .build());
        callWaitingResponses();
        return requestId;
    }

    @NonNull protected Map<String, Product> getProductData(@NonNull Set<String> skus)
    {
        Map<String, Product> data = new HashMap<>();
        for (String sku : skus)
        {
            data.put(sku, getProductData(sku));
        }
        return data;
    }

    @NonNull protected Product getProductData(@NonNull String sku)
    {
        ProductBuilder builder = new ProductBuilder()
                .setSku(sku)
                .setDescription("Description of " + sku)
                .setTitle("Title of " + sku)
                .setSmallIconUrl("Nothing");
        float price = 0;
        switch (sku)
        {
            case THAmazonConstants.EXTRA_CASH_T0_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $10K")
                        .setDescription("Additional 10,000 Virtual Dollars");
                price = 0.99f;
                break;
            case THAmazonConstants.EXTRA_CASH_T1_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $50K")
                        .setDescription("Additional 50,000 Virtual Dollars");
                price = 2.99f;
                break;
            case THAmazonConstants.EXTRA_CASH_T2_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $100K")
                        .setDescription("Additional 100,000 Virtual Dollars");
                price = 4.99f;
                break;
            case THAmazonConstants.RESET_PORTFOLIO_0:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("Reset Portfolio")
                        .setDescription("Reset your Portfolio, and start anew");
                price = 1.99f;
                break;
        }
        return builder
                .setPrice(String.format("%.2f USD", price))
                .build();
    }

    @NonNull public RequestId getPurchaseUpdates(boolean reset, @NonNull PurchasingListener listener)
    {
        RequestId requestId = super.getPurchaseUpdates(reset, listener);
        putWaitingResponse(requestId, new PurchaseUpdatesResponseBuilder()
                .setRequestId(requestId)
                .setRequestStatus(PurchaseUpdatesResponse.RequestStatus.SUCCESSFUL)
                .setHasMore(false)
                .setReceipts(getReceiptList(reset))
                .setUserData(getUserData())
                .build());
        callWaitingResponses();
        return requestId;
    }

    @NonNull protected List<Receipt> getReceiptList(boolean reset)
    {
        List<Receipt> receipts = new ArrayList<>();
        if (reset)
        {
            receipts.add(new ReceiptBuilder()
                    .setPurchaseDate(new Date(2014, 6, 1))
                    .setReceiptId("receiptId1")
                    .setSku(THAmazonConstants.EXTRA_CASH_T0_KEY)
                    .setProductType(ProductType.CONSUMABLE)
                    .setCancelDate(null)
                    .build());
        }
        else
        {
            receipts.add(new ReceiptBuilder()
                    .setPurchaseDate(new Date(2014, 7, 1))
                    .setReceiptId("receiptId2")
                    .setSku(THAmazonConstants.EXTRA_CASH_T1_KEY)
                    .setProductType(ProductType.CONSUMABLE)
                    .setCancelDate(null)
                    .build());
        }
        return receipts;
    }
}
