package com.ayondo.academy.billing.amazon.purchasefetch;

import android.support.annotation.NonNull;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.purchasefetch.BaseAmazonPurchaseFetcherRx;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.ayondo.academy.api.billing.AmazonPurchaseInProcessDTO;
import com.ayondo.academy.billing.amazon.THAmazonOrderId;
import com.ayondo.academy.billing.amazon.THAmazonPurchase;
import com.ayondo.academy.billing.amazon.THAmazonPurchaseIncomplete;
import com.ayondo.academy.billing.amazon.THComposedAmazonPurchase;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import timber.log.Timber;

public class THBaseAmazonPurchaseFetcherRx
        extends BaseAmazonPurchaseFetcherRx<
                AmazonSKU,
                THAmazonOrderId,
                THAmazonPurchase,
                THAmazonPurchaseIncomplete>
        implements THAmazonPurchaseFetcherRx
{
    @NonNull protected final StringSetPreference processingPurchaseStringSet;
    @NonNull protected final List<AmazonPurchaseInProcessDTO> savedPurchasesInProcess;

    //<editor-fold desc="Constructors">
    public THBaseAmazonPurchaseFetcherRx(
            int request,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(request, purchasingService);
        this.processingPurchaseStringSet = processingPurchaseStringSet;
        savedPurchasesInProcess = new ArrayList<>();
        populateSavedPurchasesInProcess();
    }
    //</editor-fold>

    protected void populateSavedPurchasesInProcess()
    {
        savedPurchasesInProcess.clear();
        Set<String> savedPurchaseStrings = processingPurchaseStringSet.get();
        Timber.d("Adding saved purchases");
        for (String savedPurchaseString: savedPurchaseStrings)
        {
            Timber.d("Adding saved purchase %s", savedPurchaseString);
            savedPurchasesInProcess.add(
                    (AmazonPurchaseInProcessDTO) THJsonAdapter.getInstance().fromBody(
                            savedPurchaseString,
                            AmazonPurchaseInProcessDTO.class));
            Timber.d("Added %s", savedPurchaseString);
        }
    }

    @NonNull protected THComposedAmazonPurchase complete(@NonNull THAmazonPurchaseIncomplete incompleteFetchedPurchase)
    {
        for (AmazonPurchaseInProcessDTO savedPurchase : savedPurchasesInProcess)
        {
            if (incompleteFetchedPurchase.getOrderId().receipt.getReceiptId()
                    .equals(savedPurchase.amazonPurchaseToken))
            {
                Timber.d("Populating for %s", incompleteFetchedPurchase.getOrderId().receipt.getReceiptId());
                return new THComposedAmazonPurchase(
                        incompleteFetchedPurchase.getOrderId().receipt,
                        savedPurchase);
            }
        }
        throw new IllegalArgumentException("Incomplete Purchase has no matching Purchase In Process");
    }

    @NonNull @Override protected THAmazonPurchaseIncomplete createIncompletePurchase(@NonNull Receipt receipt, @NonNull UserData userData)
    {
        return new THAmazonPurchaseIncomplete(receipt, userData);
    }
}
