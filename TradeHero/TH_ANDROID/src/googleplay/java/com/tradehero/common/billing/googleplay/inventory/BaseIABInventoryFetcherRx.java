package com.tradehero.common.billing.googleplay.inventory;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.support.annotation.NonNull;
import com.tradehero.common.billing.googleplay.BaseIABServiceCaller;
import com.tradehero.common.billing.googleplay.IABConstants;
import com.tradehero.common.billing.googleplay.IABProductDetail;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListKey;
import com.tradehero.common.billing.googleplay.IABServiceResult;
import com.tradehero.common.billing.googleplay.exception.IABBadResponseException;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.billing.googleplay.exception.IABExceptionFactory;
import com.tradehero.common.billing.googleplay.exception.IABRemoteException;
import com.tradehero.common.billing.inventory.ProductInventoryResult;
import com.ayondo.academy.BuildConfig;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONException;
import rx.Observable;
import rx.functions.Func1;
import timber.log.Timber;

abstract public class BaseIABInventoryFetcherRx<
        IABSKUType extends IABSKU,
        IABProductDetailsType extends IABProductDetail<IABSKUType>>
        extends BaseIABServiceCaller
        implements IABInventoryFetcherRx<
        IABSKUType,
        IABProductDetailsType>
{
    @NonNull private final List<IABSKUType> iabSKUs;

    //<editor-fold desc="Constructors">
    public BaseIABInventoryFetcherRx(
            int requestCode,
            @NonNull List<IABSKUType> iabSKUs,
            @NonNull Context context,
            @NonNull IABExceptionFactory iabExceptionFactory)
    {
        super(requestCode, context, iabExceptionFactory);
        this.iabSKUs = iabSKUs;
    }

    //</editor-fold>

    @Override @NonNull public List<IABSKUType> getProductIdentifiers()
    {
        return iabSKUs;
    }

    @NonNull abstract protected IABProductDetailsType createSKUDetails(IABSKUListKey itemType, String json) throws JSONException;

    @NonNull @Override public Observable<ProductInventoryResult<IABSKUType, IABProductDetailsType>> get()
    {
        return getBillingServiceResult().flatMap(
                new Func1<IABServiceResult, Observable<? extends ProductInventoryResult<IABSKUType, IABProductDetailsType>>>()
                {
                    @Override public Observable<? extends ProductInventoryResult<IABSKUType, IABProductDetailsType>> call(IABServiceResult result)
                    {
                        try
                        {
                            Map<IABSKUType, IABProductDetailsType> mapped = new HashMap<>();
                            mapped.putAll(BaseIABInventoryFetcherRx.this.fetchOne(result, IABSKUListKey.getInApp()).mapped);
                            if (result.subscriptionSupported)
                            {
                                mapped.putAll(BaseIABInventoryFetcherRx.this.fetchOne(result, IABSKUListKey.getSubs()).mapped);
                            }
                            return Observable.just(new ProductInventoryResult<>(getRequestCode(), mapped));
                        } catch (RemoteException e)
                        {
                            Timber.e("Remote Exception while fetching inventory.", e);
                            return Observable.error(new IABRemoteException("RemoteException while fetching IAB", e));
                        } catch (JSONException e)
                        {
                            Timber.e("Error parsing json.", e);
                            return Observable.error(new IABBadResponseException("Unable to parse JSON", e));
                        } catch (IABException e)
                        {
                            Timber.e("IAB error.", e);
                            return Observable.error(e);
                        }
                    }
                });
    }

    @NonNull private ProductInventoryResult<IABSKUType, IABProductDetailsType>
    fetchOne(@NonNull IABServiceResult iabServiceResult, @NonNull IABSKUListKey itemType)
            throws RemoteException, JSONException
    {
        Bundle querySkus = getQuerySKUBundle();
        Bundle productDetails = iabServiceResult.billingService.getSkuDetails(
                TARGET_BILLING_API_VERSION3,
                BuildConfig.GOOGLE_PLAY_PACKAGE_NAME,
                itemType.key,
                querySkus);
        if (!productDetails.containsKey(IABConstants.RESPONSE_GET_SKU_DETAILS_LIST))
        {
            int statusCode = IABConstants.getResponseCodeFromBundle(productDetails);
            if (statusCode != IABConstants.BILLING_RESPONSE_RESULT_OK)
            {
                throw iabExceptionFactory.create(statusCode, String.format("While getting itemType=%s", itemType));
            }
            else
            {
                throw new IABBadResponseException(IABConstants.getStatusCodeDescription(statusCode));
            }
        }

        List<String> responseList = productDetails.getStringArrayList(IABConstants.RESPONSE_GET_SKU_DETAILS_LIST);
        Map<IABSKUType, IABProductDetailsType> mapped = new HashMap<>();
        for (String json : responseList)
        {
            IABProductDetailsType details = createSKUDetails(itemType, json);
            Timber.d("Got iabSKU details: %s", details);
            mapped.put(details.getProductIdentifier(), details);
        }
        return new ProductInventoryResult<>(getRequestCode(), mapped);
    }

    @NonNull private Bundle getQuerySKUBundle()
    {
        ArrayList<String> identifiers = new ArrayList<>(this.iabSKUs.size());
        List<IABSKUType> iabSKUClone = new ArrayList<>(this.iabSKUs);
        for (IABSKU iabSKU : iabSKUClone)
        {
            identifiers.add(iabSKU.identifier);
        }
        Bundle querySkus = new Bundle();
        querySkus.putStringArrayList(IABConstants.GET_SKU_DETAILS_ITEM_LIST, identifiers);
        return querySkus;
    }
}
