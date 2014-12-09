package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaser;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.exception.AmazonPurchaseCanceledException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class THBaseAmazonPurchaser
    extends BaseAmazonPurchaser<
            AmazonSKU,
            THAmazonPurchaseOrder,
            THAmazonOrderId,
            THAmazonPurchase,
            AmazonException>
    implements THAmazonPurchaser
{
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    public THBaseAmazonPurchaser(
            int request,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull /*@ProcessingPurchase*/ StringSetPreference processingPurchaseStringSet)
    {
        super(request, purchasingService);
        this.amazonExceptionFactory = amazonExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @Override protected THBaseAmazonPurchase createAmazonPurchase(PurchaseResponse purchaseResponse)
    {
        return new THBaseAmazonPurchase(purchaseResponse, purchaseOrder.getApplicablePortfolioId());
    }

    @Override protected AmazonException createAmazonException(PurchaseResponse.RequestStatus requestStatus)
    {
        return amazonExceptionFactory.create(requestStatus, "Failed to purchase " + purchaseOrder.getProductIdentifier().skuId);
    }

    @Override protected AmazonException createAmazonCanceledException()
    {
        return new AmazonPurchaseCanceledException("Purchase canceled by user");
    }

    @Override protected void handlePurchaseFinished(THAmazonPurchase purchase)
    {
        savePurchaseInPref(purchase);
        super.handlePurchaseFinished(purchase);
    }

    protected void savePurchaseInPref(THAmazonPurchase purchase)
    {
        Timber.d("Saving purchase %s", purchase);

        String stringedPurchase = null;
        try
        {
            stringedPurchase = THJsonAdapter.getInstance().toStringBody(purchase.getPurchaseToSaveDTO());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        if (stringedPurchase != null)
        {
            List<String> list = new ArrayList<>();
            list.add(stringedPurchase);
            processingPurchaseStringSet.add(list);
        }
    }
}
