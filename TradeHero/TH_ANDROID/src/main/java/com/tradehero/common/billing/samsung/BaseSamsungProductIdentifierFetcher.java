package com.tradehero.common.billing.samsung;

import android.content.Context;
import com.sec.android.iap.lib.helper.SamsungIapHelper;
import com.sec.android.iap.lib.vo.ErrorVo;
import com.sec.android.iap.lib.vo.ItemVo;
import com.tradehero.common.billing.samsung.exception.SamsungException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by xavier on 3/27/14.
 */
abstract public class BaseSamsungProductIdentifierFetcher<
        SamsungSKUListKeyType extends SamsungSKUListKey,
        SamsungSKUType extends SamsungSKU,
        SamsungSKUListType extends BaseSamsungSKUList<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
        extends BaseSamsungActor
        implements SamsungProductIdentifierFetcher<
        SamsungSKUListKeyType,
        SamsungSKUType,
        SamsungSKUListType,
        SamsungExceptionType>
{
    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected Map<SamsungSKUListKeyType, SamsungSKUListType> samsungSKUs;
    private OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> fetchedListener;

    public BaseSamsungProductIdentifierFetcher(Context context, int mode)
    {
        super(context, mode);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        samsungSKUs = new HashMap<>();
    }

    @Override public OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> getProductIdentifierListener()
    {
        return fetchedListener;
    }

    @Override public void setProductIdentifierListener(OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> listener)
    {
        this.fetchedListener = listener;
    }

    @Override public void fetchProductIdentifiers(int requestCode)
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
        mIapHelper.getItemList(
                groupId,
                0, Integer.MAX_VALUE,
                SamsungIapHelper.ITEM_TYPE_ALL,
                mode,
                this);
    }

    @Override public void onGetItem(ErrorVo errorVo, ArrayList<ItemVo> itemList)
    {
        if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
        {
            addToSkus(fetchingGroupId, itemList);
        }
        else
        {
            notifyListenerFetchFailed(createException(errorVo.getErrorCode()));
        }
    }

    protected void addToSkus(String groupId, ArrayList<ItemVo> itemList)
    {
        if (itemList != null)
        {
            for (ItemVo itemVo : itemList)
            {
                samsungSKUs.get(createSamsungListKey(itemVo.getType())).add(createSamsungSku(groupId, itemVo.getItemId()));
            }
        }
    }

    abstract protected SamsungSKUListKeyType createSamsungListKey(String itemType);
    abstract protected SamsungSKUType createSamsungSku(String groupId, String itemId);
    abstract protected SamsungExceptionType createException(int errorCode);

    protected void notifyListenerFetched()
    {
        OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchedProductIdentifiers(getRequestCode(), this.samsungSKUs);
        }
    }

    protected void notifyListenerFetchFailed(SamsungExceptionType exception)
    {
        OnProductIdentifierFetchedListener<SamsungSKUListKeyType, SamsungSKUType, SamsungSKUListType, SamsungExceptionType> listenerCopy = getProductIdentifierListener();
        if (listenerCopy != null)
        {
            listenerCopy.onFetchProductIdentifiersFailed(getRequestCode(), exception);
        }
    }
}
