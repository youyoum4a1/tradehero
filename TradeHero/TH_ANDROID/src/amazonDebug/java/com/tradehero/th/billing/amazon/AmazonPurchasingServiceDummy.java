package com.tradehero.th.billing.amazon;

import android.content.Context;
import android.util.LruCache;
import com.amazon.device.iap.PurchasingListener;
import com.amazon.device.iap.PurchasingService;
import com.amazon.device.iap.internal.model.PriceBuilder;
import com.amazon.device.iap.internal.model.ProductBuilder;
import com.amazon.device.iap.internal.model.ProductDataResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseResponseBuilder;
import com.amazon.device.iap.internal.model.PurchaseUpdatesResponseBuilder;
import com.amazon.device.iap.internal.model.ReceiptBuilder;
import com.amazon.device.iap.internal.model.UserDataBuilder;
import com.amazon.device.iap.internal.model.UserDataResponseBuilder;
import com.amazon.device.iap.model.FulfillmentResult;
import com.amazon.device.iap.model.Price;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.amazon.device.iap.model.ProductType;
import com.amazon.device.iap.model.PurchaseResponse;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.RequestId;
import com.amazon.device.iap.model.UserData;
import com.amazon.device.iap.model.UserDataResponse;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.billing.amazon.THAmazonConstants;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.inject.Inject;
import javax.inject.Singleton;
import org.jetbrains.annotations.NotNull;

@Singleton public class AmazonPurchasingServiceDummy extends AmazonPurchasingService
{
    @NotNull protected final CurrentUserId currentUserId;

    //<editor-fold desc="Constructors">
    @Inject public AmazonPurchasingServiceDummy(
            @NotNull Context appContext,
            @NotNull CurrentUserId currentUserId)
    {
        super(appContext);
        this.currentUserId = currentUserId;
    }
    //</editor-fold>

    @NotNull public RequestId getUserData(@NotNull PurchasingListener listener)
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

    @NotNull protected UserData getUserData()
    {
        return new UserDataBuilder()
                .setUserId("amazonUserId" + currentUserId.get())
                .setMarketplace("SG")
                .build();
    }

    @NotNull public RequestId purchase(@NotNull String sku, @NotNull PurchasingListener listener)
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

    @NotNull public RequestId getProductData(@NotNull Set<String> skus, @NotNull PurchasingListener listener)
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

    @NotNull protected Map<String, Product> getProductData(@NotNull Set<String> skus)
    {
        Map<String, Product> data = new HashMap<>();
        for (@NotNull String sku : skus)
        {
            data.put(sku, getProductData(sku));
        }
        return data;
    }

    @NotNull protected Product getProductData(@NotNull String sku)
    {
        ProductBuilder builder = new ProductBuilder()
                .setSku(sku)
                .setDescription("Description of " + sku)
                .setTitle("Title of " + sku)
                .setSmallIconUrl("Nothing");
        PriceBuilder priceBuilder = new PriceBuilder()
                .setCurrency(Currency.getInstance("USD"));
        switch (sku)
        {
            case THAmazonConstants.EXTRA_CASH_T0_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $10K")
                        .setDescription("Additional 10,000 Virtual Dollars");
                priceBuilder.setValue(new BigDecimal(0.99f));
                break;
            case THAmazonConstants.EXTRA_CASH_T1_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $50K")
                        .setDescription("Additional 50,000 Virtual Dollars");
                priceBuilder.setValue(new BigDecimal(2.99f));
                break;
            case THAmazonConstants.EXTRA_CASH_T2_KEY:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("TH $100K")
                        .setDescription("Additional 100,000 Virtual Dollars");
                priceBuilder.setValue(new BigDecimal(4.99f));
                break;
            case THAmazonConstants.CREDIT_1:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("1 Premium Follow Credit")
                        .setDescription("Follow your hero trades in real time");
                priceBuilder.setValue(new BigDecimal(1.99f));
                break;
            case THAmazonConstants.CREDIT_10:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("10 Premium Follow Credits")
                        .setDescription("Follow your heroes trades in real time");
                priceBuilder.setValue(new BigDecimal(19.99f));
                break;
            case THAmazonConstants.CREDIT_20:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("20 Premium Follow Credits")
                        .setDescription("Follow your heroes trades in real time");
                priceBuilder.setValue(new BigDecimal(39.99f));
                break;
            case THAmazonConstants.ALERT_1:
            case THAmazonConstants.ALERT_5:
            case THAmazonConstants.ALERT_UNLIMITED:
                builder.setProductType(ProductType.SUBSCRIPTION)
                        .setTitle("TH $10K")
                        .setDescription("Additional 10,000 Virtual Dollars");
                priceBuilder.setValue(new BigDecimal(0f));
                break;
            case THAmazonConstants.RESET_PORTFOLIO_0:
                builder.setProductType(ProductType.CONSUMABLE)
                        .setTitle("Reset Portfolio")
                        .setDescription("Reset your Portfolio, and start anew");
                priceBuilder.setValue(new BigDecimal(1.99f));
                break;
        }
        return builder
                .setPrice(priceBuilder.build())
                .build();
    }

    @NotNull public RequestId getPurchaseUpdates(boolean reset, @NotNull PurchasingListener listener)
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

    @NotNull protected List<Receipt> getReceiptList(boolean reset)
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
