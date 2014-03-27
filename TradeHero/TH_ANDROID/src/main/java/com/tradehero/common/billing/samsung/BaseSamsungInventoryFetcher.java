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
    protected boolean fetching;
    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected List<SamsungSKUType> samsungSKUs;
    protected Map<SamsungSKUType, SamsungProductDetailType> inventory;
    private OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> inventoryFetchedListener;

    public BaseSamsungInventoryFetcher(Context context, int mode)
    {
        super(context, mode);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        samsungSKUs = new ArrayList<>();
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
        return samsungSKUs;
    }

    @Override public void setProductIdentifiers(List<SamsungSKUType> productIdentifiers)
    {
        this.samsungSKUs = productIdentifiers;
    }

    @Override public void fetchInventory(int requestCode)
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
            addToInventory(fetchingGroupId, itemList);
        }
        else
        {
            notifyListenerFetchFailed(createException(errorVo.getErrorCode()));
        }
    }

    protected void addToInventory(String groupId, ArrayList<ItemVo> itemList)
    {
        if (itemList != null)
        {
            SamsungSKUType samsungSKU;
            for (ItemVo itemVo : itemList)
            {
                samsungSKU = createSamsungSku(groupId, itemVo.getItemId());
                inventory.put(
                        samsungSKU,
                        createSamsungProductDetail(samsungSKU, itemVo));
            }
        }
    }

    abstract protected SamsungSKUType createSamsungSku(String groupId, String itemId);
    abstract protected SamsungProductDetailType createSamsungProductDetail(SamsungSKUType samsungSKU, ItemVo itemVo);
    abstract protected SamsungExceptionType createException(int errorCode);

    protected void notifyListenerFetched()
    {
        OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchSuccess(getRequestCode(), getProductIdentifiers(),
                    inventory);
        }
    }

    protected void notifyListenerFetchFailed(SamsungExceptionType exception)
    {
        OnInventoryFetchedListener<SamsungSKUType, SamsungProductDetailType, SamsungExceptionType> listenerCopy = getInventoryFetchedListener();
        if (listenerCopy != null)
        {
            listenerCopy.onInventoryFetchFail(getRequestCode(), getProductIdentifiers(), exception);
        }
    }
}
