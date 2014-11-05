package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import com.tradehero.th.billing.samsung.THSamsungConstants;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import timber.log.Timber;

abstract public class BaseSamsungPurchaseFetcher<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungPurchaseIncompleteType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
    extends BaseSamsungActor
    implements SamsungPurchaseFetcher<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
    public static final int FIRST_ITEM_NUM = 1;
    public static final String FIRST_DATE = "20140101";

    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    @NonNull protected final List<SamsungPurchaseIncompleteType> fetchedIncompletePurchases;
    @NonNull protected final List<SamsungPurchaseType> purchases;
    @Nullable protected OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> fetchListener;

    //<editor-fold desc="Constructors">
    public BaseSamsungPurchaseFetcher(
            @NonNull Context context,
            int mode)
    {
        super(context, mode);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        fetchedIncompletePurchases = new ArrayList<>();
        purchases = new ArrayList<>();
    }
    //</editor-fold>

    @Override public void fetchPurchases(int requestCode)
    {
        checkNotFetching();
        this.fetching = true;
        setRequestCode(requestCode);
        fetchKnownItemGroups();
    }

    protected void checkNotFetching()
    {
        if (fetching)
        {
            throw new IllegalStateException("Already fetching");
        }
    }

    protected void fetchKnownItemGroups()
    {
        remainingGroupIds = new LinkedList<>(getKnownItemGroups());
        fetchOneInRemainingItemGroups();
    }

    abstract protected List<String> getKnownItemGroups();

    protected void fetchOneInRemainingItemGroups()
    {
        if (remainingGroupIds.size() > 0)
        {
            fetchItemGroup(remainingGroupIds.removeFirst());
        }
        else
        {
            handleFetched();
            notifyListenerFetched();
        }
    }

    protected void fetchItemGroup(String groupId)
    {
        fetchingGroupId = groupId;
        mIapHelper.getItemInboxList(
                groupId,
                FIRST_ITEM_NUM, Integer.MAX_VALUE,
                FIRST_DATE, THSamsungConstants.getTodayStringForInbox(),
                this);
    }

    @Override public void onGetItemInbox(ErrorVo errorVo, ArrayList<InboxVo> inboxList)
    {
        if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
        {
            addToIncompletePurchases(fetchingGroupId, inboxList);
            fetchOneInRemainingItemGroups();
        }
        else
        {
            notifyListenerFetchFailed(createException(errorVo));
        }
    }

    abstract protected SamsungExceptionType createException(ErrorVo errorVo);

    protected void addToIncompletePurchases(String groupId, ArrayList<InboxVo> inboxList)
    {
        if (inboxList != null)
        {
            for (InboxVo inboxVo : inboxList)
            {
                fetchedIncompletePurchases.add(createIncompletePurchase(groupId, inboxVo));
            }
        }
    }

    @NonNull abstract protected SamsungPurchaseIncompleteType createIncompletePurchase(String groupId, InboxVo inboxVo);

    @Override @Nullable public OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> getFetchListener()
    {
        return fetchListener;
    }

    @Override public void setPurchaseFetchedListener(@Nullable OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> fetchListener)
    {
        this.fetchListener = fetchListener;
    }

    protected void handleFetched()
    {
        // Nothing to do
    }

    protected void notifyListenerFetched()
    {
        OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedPurchases(getRequestCode(), this.purchases);
        }
    }

    protected void notifyListenerFetchFailed(SamsungExceptionType exception)
    {
        Timber.e(exception, "");
        OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchPurchasesFailed(getRequestCode(), exception);
        }
    }
}
