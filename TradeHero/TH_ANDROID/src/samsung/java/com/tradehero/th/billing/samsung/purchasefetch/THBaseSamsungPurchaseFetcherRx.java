package com.tradehero.th.billing.samsung.purchasefetch;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.SamsungSKU;
import com.tradehero.common.billing.samsung.purchasefetch.BaseSamsungPurchaseFetcherRx;
import com.tradehero.common.billing.samsung.rx.InboxListQueryGroup;
import com.tradehero.common.persistence.prefs.StringSetPreference;
import com.tradehero.common.utils.THJsonAdapter;
import com.tradehero.th.api.billing.SamsungPurchaseInProcessDTO;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import com.tradehero.th.billing.samsung.THSamsungOrderId;
import com.tradehero.th.billing.samsung.THSamsungPurchase;
import com.tradehero.th.billing.samsung.THSamsungPurchaseIncomplete;
import com.tradehero.th.billing.samsung.exception.SamsungNoSavedPurchaseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import timber.log.Timber;

public class THBaseSamsungPurchaseFetcherRx
        extends BaseSamsungPurchaseFetcherRx<
        SamsungSKU,
        THSamsungOrderId,
        THSamsungPurchase,
        THSamsungPurchaseIncomplete>
        implements THSamsungPurchaseFetcherRx
{
    public static final int FIRST_ITEM_NUM = 1;
    public static final String FIRST_DATE = "20140101";


    @NonNull protected final StringSetPreference processingPurchaseStringSet;
    @NonNull protected final List<SamsungPurchaseInProcessDTO> savedPurchasesInProcess;

    //<editor-fold desc="Constructors">
    public THBaseSamsungPurchaseFetcherRx(
            int requestCode,
            @NonNull Context context,
            int mode,
            @NonNull StringSetPreference processingPurchaseStringSet)
    {
        super(requestCode, context, mode);
        this.processingPurchaseStringSet = processingPurchaseStringSet;
        savedPurchasesInProcess = new ArrayList<>();
        populateSavedPurchasesInProcess();
    }
    //</editor-fold>

    @NonNull @Override protected List<InboxListQueryGroup> getInboxListQueryGroups()
    {
        return Collections.singletonList(new InboxListQueryGroup(FIRST_ITEM_NUM,
                Integer.MAX_VALUE,
                THSamsungConstants.IAP_ITEM_GROUP_ID,
                FIRST_DATE,
                THSamsungConstants.getTodayStringForInbox()));
    }

    @NonNull @Override protected THSamsungPurchaseIncomplete createIncompletePurchase(
            @NonNull InboxListQueryGroup queryGroup,
            @NonNull InboxVo inboxVo)
    {
        return new THSamsungPurchaseIncomplete(queryGroup.groupId, inboxVo);
    }

    @NonNull @Override protected THSamsungPurchase mergeWithSaved(@NonNull THSamsungPurchaseIncomplete incomplete)
    {
        SamsungPurchaseInProcessDTO savedCopy = getSavedCopy(incomplete);
        if (savedCopy != null)
        {
            return createByMergingSavedPurchase(savedCopy, incomplete);
        }
        throw new SamsungNoSavedPurchaseException(incomplete);
    }

    @Nullable protected SamsungPurchaseInProcessDTO getSavedCopy(@NonNull THSamsungPurchaseIncomplete incomplete)
    {
        for (SamsungPurchaseInProcessDTO savedPurchase : savedPurchasesInProcess)
        {
            if (incomplete.getPaymentId().equals(savedPurchase.paymentId))
            {
                Timber.d("Populating for %s", incomplete.getPaymentId());
                return savedPurchase;
            }
        }
        return null;
    }

    @NonNull protected THSamsungPurchase createByMergingSavedPurchase(
            @NonNull SamsungPurchaseInProcessDTO savedPurchase,
            @NonNull THSamsungPurchaseIncomplete incomplete)
    {
        THSamsungPurchase merged = new THSamsungPurchase(
                incomplete.getGroupId(),
                incomplete.getJsonString(),
                savedPurchase.applicablePortfolioId);
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
