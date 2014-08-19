package com.tradehero.th.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.BaseSamsungPurchaseFetcher;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.common.billing.samsung.persistence.SamsungPurchaseCache;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.billing.samsung.exception.THSamsungExceptionFactory;
import com.tradehero.th.billing.samsung.persistence.THSamsungPurchaseCache;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import timber.log.Timber;

public class THBaseSamsungPurchaseFetcher
    extends BaseSamsungPurchaseFetcher<
            SamsungSKU,
            THSamsungOrderId,
            THSamsungPurchase,
            SamsungException>
    implements THSamsungPurchaseFetcher
{
    @NotNull protected final THSamsungExceptionFactory samsungExceptionFactory;
    @NotNull @ProcessingPurchase protected final StringSetPreference processingPurchaseStringSet;

    //<editor-fold desc="Constructors">
    @Inject public THBaseSamsungPurchaseFetcher(
            @NotNull Context context,
            @ForSamsungBillingMode int mode,
            @NotNull THSamsungExceptionFactory samsungExceptionFactory,
            @NotNull @ProcessingPurchase StringSetPreference processingPurchaseStringSet)
    {
        super(context, mode);
        this.samsungExceptionFactory = samsungExceptionFactory;
        this.processingPurchaseStringSet = processingPurchaseStringSet;
    }
    //</editor-fold>

    @Override protected List<String> getKnownItemGroups()
    {
        List<String> knownGroupIds = new ArrayList<>();
        knownGroupIds.add(THSamsungConstants.IAP_ITEM_GROUP_ID);
        return knownGroupIds;
    }

    @Override @NotNull protected THSamsungPurchase createPurchase(String groupId, InboxVo inboxVo)
    {
        THSamsungPurchase purchase = new THSamsungPurchase(groupId, inboxVo, null);
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
        mergeSavedPurchases();
    }

    protected void mergeSavedPurchases()
    {
        List<SamsungPurchaseInProcessDTO> savedPurchases = getSavedPurchasesInProcess();
        Timber.d("Merging %d purchases", savedPurchases.size());
        for (SamsungPurchaseInProcessDTO savedPurchase : savedPurchases)
        {
            boolean populated = false;
            for (THSamsungPurchase fetchedPurchase : purchases)
            {
                if (fetchedPurchase.getPaymentId().equals(savedPurchase.paymentId))
                {
                    Timber.d("Populating for %s", fetchedPurchase.getPaymentId());
                    fetchedPurchase.populate(savedPurchase);
                    populated = true;
                }
            }

            if (!populated)
            {
                Timber.d("Adding for %s", savedPurchase.paymentId);
                purchases.add(savedPurchase.createSamsungPurchase());
            }
        }
    }

    protected List<SamsungPurchaseInProcessDTO> getSavedPurchasesInProcess()
    {
        Set<String> savedPurchaseStrings = processingPurchaseStringSet.get();
        List<SamsungPurchaseInProcessDTO> savedPurchases = new ArrayList<>();
        if (savedPurchaseStrings != null)
        {
            Timber.d("Adding saved purchases");
            for (String savedPurchaseString: savedPurchaseStrings)
            {
                Timber.d("Adding saved purchase %s", savedPurchaseString);
                savedPurchases.add((SamsungPurchaseInProcessDTO) THJsonAdapter.getInstance().fromBody(savedPurchaseString, SamsungPurchaseInProcessDTO.class));
            }
        }
        else
        {
            Timber.d("No saved purchase");
        }
        return savedPurchases;
    }
}
