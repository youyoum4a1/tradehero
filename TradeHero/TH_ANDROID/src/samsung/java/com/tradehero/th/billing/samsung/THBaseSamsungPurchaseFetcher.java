package com.tradehero.th.billing.samsung;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import timber.log.Timber;

public class THBaseSamsungPurchaseFetcher
        extends BaseSamsungPurchaseFetcher<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseIncomplete,
        SamsungException>
        implements THSamsungPurchaseFetcher
{
    @NonNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NonNull protected final StringSetPreference processingPurchaseStringSet;
    @NonNull protected final List<SamsungPurchaseInProcessDTO> savedPurchasesInProcess;

    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaseFetcher(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull THSamsungExceptionFactory samsungExceptionFactory,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(requestCode, context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
        savedPurchasesInProcess = new ArrayList<>();
        populateSavedPurchasesInProcess();
    }
    //</editor-fold>

    @Override protected List<String> getKnownItemGroups()
    {
        List<String> knownGroupIds = new ArrayList<>();
        knownGroupIds.add(THSamsungConstants.IAP_ITEM_GROUP_ID);
        return knownGroupIds;
    }

    @Override @NonNull protected THSamsungPurchaseIncomplete createIncompletePurchase(String groupId, InboxVo inboxVo)
    {
        THSamsungPurchaseIncomplete purchase = new THSamsungPurchaseIncomplete(groupId, inboxVo);
        Timber.d("Created 1 purchase %s", inboxVo.getJsonString());
        return purchase;
    }

    @Override @Nullable protected SamsungException createException(ErrorVo errorVo)
    {
        return samsungExceptionFactory.create(errorVo);
    }

    @Override protected void handleFetched()
    {
        super.handleFetched();
        mergeWithSavedPurchases();
    }

    protected void mergeWithSavedPurchases()
    {
        populateSavedPurchasesInProcess();
        Timber.d("Merging %d purchases", savedPurchasesInProcess.size());
        for (SamsungPurchaseInProcessDTO savedPurchase : savedPurchasesInProcess)
        {
            for (THSamsungPurchaseIncomplete incompleteFetchedPurchase : fetchedIncompletePurchases)
            {
                if (incompleteFetchedPurchase.getPaymentId().equals(savedPurchase.paymentId))
                {
                    Timber.d("Populating for %s", incompleteFetchedPurchase.getPaymentId());
                    purchases.add(createByMergingSavedPurchase(
                            savedPurchase,
                            incompleteFetchedPurchase));
                }
            }
        }
    }

    protected THSamsungPurchase createByMergingSavedPurchase(SamsungPurchaseInProcessDTO savedPurchase, THSamsungPurchaseIncomplete incomplete)
    {
        THSamsungPurchase merged = new THSamsungPurchase(incomplete.getGroupId(), incomplete.getJsonString(), savedPurchase.applicablePortfolioId);
        merged.populate(savedPurchase);
        return merged;
    }

    protected void populateSavedPurchasesInProcess()
    {
        savedPurchasesInProcess.clear();
        Set<String> savedPurchaseStrings = processingPurchaseStringSet.get();
        Timber.d("Adding saved purchases");
        for (String savedPurchaseString : savedPurchaseStrings)
        {
            Timber.d("Adding saved purchase %s", savedPurchaseString);
            savedPurchasesInProcess.add(
                    (SamsungPurchaseInProcessDTO) THJsonAdapter.getInstance().fromBody(
                            savedPurchaseString,
                            SamsungPurchaseInProcessDTO.class));
        }
    }
}
