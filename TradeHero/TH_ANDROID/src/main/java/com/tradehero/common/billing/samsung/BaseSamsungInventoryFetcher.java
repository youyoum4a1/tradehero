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
import timber.log.Timber;

/**
 * Product Identifier Fetcher and Inventory Fetcher are essentially making the same calls.
 * Created by xavier on 3/27/14.
 */
abstract public class BaseSamsungInventoryFetcher<
        SamsungSKUType extends SamsungSKU,
        SamsungProductDetailType extends SamsungProductDetail<SamsungSKUType>,
        SamsungExceptionType extends SamsungException>
    extends BaseSamsungActor
    implements SamsungInventoryFetcher<
        SamsungSKUType,
        SamsungProductDetailType,
        SamsungExceptionType>
{
    public static final int FIRST_ITEM_NUM = 1;

    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected Map<SamsungSKUType, SamsungProductDetailType> inventory;
    private OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> inventoryFetchedListener;

    public BaseSamsungInventoryFetcher(Context context, int mode)
    {
        super(context, mode);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        inventory = new HashMap<>();
    }

    @Override public OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> getInventoryFetchedListener()
    {
        return inventoryFetchedListener;
    }

    @Override public void setInventoryFetchedListener(OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> onInventoryFetchedListener)
    {
        this.inventoryFetchedListener = onInventoryFetchedListener;
    }

    @Override public List<SamsungSKUType> getProductIdentifiers()
    {
        throw new IllegalArgumentException("This list is not to be used on this class");
    }

    @Override public void setProductIdentifiers(List<SamsungSKUType> productIdentifiers)
    {
        throw new IllegalArgumentException("It is not necessary to set the identifiers on this one");
    }

    @Override public void fetchInventory(int requestCode)
    {
        Timber.d("Fetching inventory");
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
        Timber.d("fetchKnownItemGroups");
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
        Timber.d("FetchItemGroup %s", groupId);
        fetchingGroupId = groupId;
        mIapHelper.getItemList(
                groupId,
                FIRST_ITEM_NUM, Integer.MAX_VALUE,
                SamsungIapHelper.ITEM_TYPE_ALL,
                mode,
                this);
    }

    @Override public void onGetItem(ErrorVo errorVo, ArrayList<ItemVo> itemList)
    {
        Timber.d("onGetItem error:%s count:%d", errorVo.dump(), itemList.size());
        if (errorVo.getErrorCode() == SamsungIapHelper.IAP_ERROR_NONE)
        {
            addToInventory(fetchingGroupId, itemList);
            notifyListenerFetched();
        }
        else
        {
            notifyListenerFetchFailed(createException(errorVo));
        }
    }

    protected void addToInventory(String groupId, ArrayList<ItemVo> itemList)
    {
        if (itemList != null)
        {
            SamsungSKUType samsungSKU;
            for (ItemVo itemVo : itemList)
            {
                Timber.d("Adding %s", itemVo.dump());
                samsungSKU = createSamsungSku(groupId, itemVo.getItemId());
                inventory.put(
                        samsungSKU,
                        createSamsungProductDetail(samsungSKU, itemVo));
            }
        }
    }

    abstract protected SamsungSKUType createSamsungSku(String groupId, String itemId);
    abstract protected SamsungProductDetailType createSamsungProductDetail(SamsungSKUType samsungSKU, ItemVo itemVo);
    abstract protected SamsungExceptionType createException(ErrorVo errorVo);

    protected void notifyListenerFetched()
    {
        OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            Timber.d("Notify listener");
            listenerCopy.onInventoryFetchSuccess(
                    getRequestCode(),
                    new ArrayList<>(inventory.keySet()),
                    inventory);
        }
        else
        {
            Timber.d("Listener null");
        }
    }

    protected void notifyListenerFetchFailed(SamsungExceptionType exception)
    {
        Timber.e(exception, "");
        OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(getRequestCode(), null, exception);
        }
    }
}
