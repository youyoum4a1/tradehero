package com.ayondo.academy.billing.amazon.purchase;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.PurchaseResponse;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchase.BaseAmazonPurchaserRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.amazon.THAmazonPurchaseOrder;
import com.ayondo.academy.billing.amazon.THBaseAmazonPurchase;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import timber.log.Timber;

public class THBaseAmazonPurchaserRx
        extends BaseAmazonPurchaserRx<
        AmazonSKU,
        THAmazonPurchaseOrder,
        THAmazonOrderId,
        THAmazonPurchase>
        implements THAmazonPurchaserRx
{
    @NonNull protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    public THBaseAmazonPurchaserRx(
            int request,
            @NonNull THAmazonPurchaseOrder purchaseOrder,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(request, purchaseOrder, purchasingService);
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @NonNull @Override protected THBaseAmazonPurchase createPurchase(@NonNull PurchaseResponse purchaseResponse)
    {
        THBaseAmazonPurchase purchase = new THBaseAmazonPurchase(purchaseResponse, getPurchaseOrder().getApplicablePortfolioId());
        if (getPurchaseOrder().getUserToFollow() != null)
        {
            purchase.setUserToFollow(getPurchaseOrder().getUserToFollow());
        }
        savePurchaseInPref(purchase);
        return purchase;
    }

    protected void savePurchaseInPref(THAmazonPurchase purchase)
    {
        Timber.d("Saving purchase %s", purchase);

        String stringedPurchase = null;
        try
        {
            stringedPurchase = THJsonAdapter.getInstance().toStringBody(purchase.getPurchaseToSaveDTO());
        } catch (IOException e)
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
