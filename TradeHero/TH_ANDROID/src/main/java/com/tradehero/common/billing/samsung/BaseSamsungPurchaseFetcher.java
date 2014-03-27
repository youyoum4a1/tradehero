package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.listener.OnGetInboxListener;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.InboxVo;
import com.tradehero.common.billing.BillingPurchaseFetcher;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/** Created with IntelliJ IDEA. User: xavier Date: 11/5/13 Time: 3:31 PM To change this template use File | Settings | File Templates. */
abstract public class BaseSamsungPurchaseFetcher<
        SamsungSKUType extends SamsungSKU,
        SamsungOrderIdType extends SamsungOrderId,
        SamsungPurchaseType extends SamsungPurchase<SamsungSKUType, SamsungOrderIdType>,
        SamsungExceptionType extends SamsungException>
    extends BaseSamsungActor
    implements SamsungPurchaseFetcher<
        SamsungSKUType,
        SamsungOrderIdType,
        SamsungPurchaseType,
        SamsungExceptionType>
{
    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected List<SamsungPurchaseType> purchases;
    protected OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> fetchListener;

    public BaseSamsungPurchaseFetcher(Context context, int mode)
    {
        super(context, mode);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        purchases = new ArrayList<>();
    }

    abstract protected SamsungPurchaseCache<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType> getPurchaseCache();

    @Override public void fetchPurchases(int requestCode)
    {
        checkNotFetching();
        setRequestCode(requestCode);
        this.fetching = true;
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
            notifyListenerFetched();
        }
    }

    protected void fetchItemGroup(String groupId)
    {
        fetchingGroupId = groupId;
        mIapHelper.getItemInboxList(
                groupId,
                0, Integer.MAX_VALUE,
                "", "",
                this);
    }

    @Override public void onGetItemInbox(ErrorVo errorVo, ArrayList<InboxVo> inboxList)
    {
        if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
        {
            addToPurchases(fetchingGroupId, inboxList);
        }
        else
        {
            notifyListenerFetchFailed(createException(errorVo.getErrorCode()));
        }
    }

    abstract protected SamsungExceptionType createException(int errorCode);

    protected void addToPurchases(String groupId, ArrayList<InboxVo> inboxList)
    {
        if (inboxList != null)
        {
            for (InboxVo inboxVo : inboxList)
            {
                purchases.add(createPurchase(groupId, inboxVo));
            }
        }
    }

    abstract protected SamsungPurchaseType createPurchase(String groupId, InboxVo inboxVo);

    @Override public OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> getFetchListener()
    {
        return fetchListener;
    }

    @Override public void setPurchaseFetchedListener(OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> fetchListener)
    {
        this.fetchListener = fetchListener;
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
        OnPurchaseFetchedListener<SamsungSKUType, SamsungOrderIdType, SamsungPurchaseType, SamsungExceptionType> listenerCopy = getFetchListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchPurchasesFailed(getRequestCode(), exception);
        }
    }
}
