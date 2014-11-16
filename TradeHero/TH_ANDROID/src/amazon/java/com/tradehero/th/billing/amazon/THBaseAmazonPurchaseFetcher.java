package com.tradehero.th.billing.amazon;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.billing.amazon.service.AmazonPurchasingService;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import timber.log.Timber;

public class THBaseAmazonPurchaseFetcher
        extends BaseAmazonPurchaseFetcher<
        AmazonSKU,
        THAmazonOrderId,
        THAmazonPurchase,
        THAmazonPurchaseIncomplete,
        AmazonException>
        implements THAmazonPurchaseFetcher
{
    @NonNull protected final THAmazonExceptionFactory amazonExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;
    @NonNull protected final List<AmazonPurchaseInProcessDTO> savedPurchasesInProcess;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcher(
            int request,
            @NonNull AmazonPurchasingService purchasingService,
            @NonNull THAmazonExceptionFactory amazonExceptionFactory,
            @NonNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super(request, purchasingService);
        this.amazonExceptionFactory = amazonExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
        savedPurchasesInProcess = new ArrayList<>();
        populateSavedPurchasesInProcess();
    }
    //</editor-fold>

    @NonNull @Override protected THAmazonPurchaseIncomplete createIncompletePurchase(@NonNull Receipt receipt, @NonNull UserData userData)
    {
        return new THAmazonPurchaseIncomplete(receipt, userData);
    }

    @Override @Nullable protected AmazonException createException(@NonNull PurchaseUpdatesResponse.RequestStatus requestStatus)
    {
        return amazonExceptionFactory.create(requestStatus, "Failed to fetch purchases");
    }

    @Override protected void handleReceived(@NonNull List<Receipt> receipts, @NonNull UserData userData)
    {
        super.handleReceived(receipts, userData);
        mergeWithSavedPurchases();
    }

    protected void mergeWithSavedPurchases()
    {
        populateSavedPurchasesInProcess();
        Timber.d("Merging %d purchases", savedPurchasesInProcess.size());
        for (AmazonPurchaseInProcessDTO savedPurchase : savedPurchasesInProcess)
        {
            for (THAmazonPurchaseIncomplete incompleteFetchedPurchase : fetchedIncompletePurchases)
            {
                if (incompleteFetchedPurchase.getOrderId().receipt.getReceiptId()
                        .equals(savedPurchase.amazonPurchaseToken))
                {
                    Timber.d("Populating for %s", incompleteFetchedPurchase.getOrderId().receipt.getReceiptId());
                    purchases.add(new THComposedAmazonPurchase(
                            incompleteFetchedPurchase.getOrderId().receipt,
                            savedPurchase));
                }
            }
        }
    }

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
}
