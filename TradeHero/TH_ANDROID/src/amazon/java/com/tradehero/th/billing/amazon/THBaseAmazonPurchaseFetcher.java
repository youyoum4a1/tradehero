package com.tradehero.th.billing.amazon;

import android.content.Context;
import com.amazon.device.iap.model.PurchaseUpdatesResponse;
import com.amazon.device.iap.model.Receipt;
import com.amazon.device.iap.model.UserData;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.BaseAmazonPurchaseFetcher;
import com.tradehero.common.billing.amazon.exception.AmazonException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.billing.AmazonPurchaseInProcessDTO;
import com.tradehero.th.billing.amazon.exception.THAmazonExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
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
    @NotNull protected final THAmazonExceptionFactory samsungExceptionFactory;
    @NotNull protected final StringSetPreference processingPurchaseStringSet;
    @NotNull protected final List<AmazonPurchaseInProcessDTO> savedPurchasesInProcess;

    //<editor-fold desc="Constructors">
    @Inject public THBaseAmazonPurchaseFetcher(
            @NotNull Context context,
            @NotNull THAmazonExceptionFactory samsungExceptionFactory,
            @NotNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super(context);
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
        savedPurchasesInProcess = new ArrayList<>();
        populateSavedPurchasesInProcess();
    }
    //</editor-fold>

    @NotNull @Override protected THAmazonPurchaseIncomplete createIncompletePurchase(@NotNull Receipt receipt, @NotNull UserData userData)
    {
        return new THAmazonPurchaseIncomplete(receipt, userData);
    }

    @Override @Nullable protected AmazonException createException(@NotNull PurchaseUpdatesResponse.RequestStatus requestStatus)
    {
        return samsungExceptionFactory.create(requestStatus, "Failed to fetch purchases");
    }

    @Override protected void handleReceived(@NotNull List<Receipt> receipts, @NotNull UserData userData)
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
        }
    }
}
