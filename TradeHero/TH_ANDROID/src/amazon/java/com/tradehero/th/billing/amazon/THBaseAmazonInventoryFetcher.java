package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Product;
import com.amazon.device.iap.model.ProductDataResponse;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonInventoryFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;

public class THBaseAmazonInventoryFetcher
    extends BaseAmazonInventoryFetcher<
            AmazonSKU,
            THAmazonProductDetail,
            AmazonException>
    implements THAmazonInventoryFetcher
{
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;

    //<editor-fold desc="Constructors">
    public THBaseAmazonInventoryFetcher(
            int request,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory)
    {
        super(request, purchasingService);
        this.amazonExceptionFactory = amazonExceptionFactory;
    }
    //</editor-fold>

    @Override protected AmazonSKU createAmazonSku(String skuId)
    {
        return new AmazonSKU(skuId);
    }

    @Override protected THAmazonProductDetail createAmazonProductDetail(AmazonSKU amazonSKU, Product product)
    {
        return new THAmazonProductDetail(product);
    }

    @Override protected AmazonException createException(ProductDataResponse.RequestStatus requestStatus)
    {
        return amazonExceptionFactory.create(requestStatus, "Failed to fetch inventory");
    }
}
