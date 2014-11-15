package com.tradehero.common.billing.amazon.identifier;

import android.content.Context;
import android.support.annotation.NonNull;
import com.amazon.device.iap.model.ProductType;
import com.tradehero.common.billing.amazon.AmazonPurchasingService;
import com.tradehero.common.billing.amazon.AmazonSKU;
import com.tradehero.common.billing.amazon.AmazonSKUListKey;
import com.tradehero.common.billing.amazon.BaseAmazonActor;
import com.tradehero.common.billing.amazon.BaseAmazonSKUList;
import com.tradehero.common.billing.identifier.ProductIdentifierListResult;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import rx.Observable;
import rx.subjects.BehaviorSubject;

abstract public class BaseAmazonProductIdentifierFetcherRx<
        AmazonSKUListKeyType extends AmazonSKUListKey,
        AmazonSKUType extends AmazonSKU,
        AmazonSKUListType extends BaseAmazonSKUList<AmazonSKUType>>
        extends BaseAmazonActor
        implements AmazonProductIdentifierFetcherRx<
                AmazonSKUListKeyType,
                AmazonSKUType,
                AmazonSKUListType>
{
    public static final int FIRST_ITEM_NUM = 1;

    protected LinkedList<String> remainingGroupIds;
    protected String fetchingGroupId;
    protected Map<AmazonSKUListKeyType, AmazonSKUListType> amazonSKUs;
    protected BehaviorSubject<ProductIdentifierListResult<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType>> subject;
    protected Observable<ProductIdentifierListResult<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType>> replayObservable;

    //<editor-fold desc="Constructors">
    public BaseAmazonProductIdentifierFetcherRx(
            int requestCode,
            @NonNull Context context,
            @NonNull AmazonPurchasingService purchasingService)
    {
        super(context, purchasingService);
        remainingGroupIds = new LinkedList<>();
        fetchingGroupId = null;
        amazonSKUs = new HashMap<>();
        this.subject = BehaviorSubject.create();
        this.replayObservable = subject.replay().publish();
        populateSubject();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        super.onDestroy();
    }

    @NonNull @Override public Observable<ProductIdentifierListResult<AmazonSKUListKeyType, AmazonSKUType, AmazonSKUListType>> get()
    {
        return replayObservable;
    }

    protected void populateSubject()
    {
        for (ProductType productType : ProductType.values())
        {
            AmazonSKUListType list = createAmazonSKUList();
            populate(list, productType);
            subject.onNext(new ProductIdentifierListResult<>(
                    getRequestCode(),
                    createAmazonListKey(productType),
                    list));
        }
        subject.onCompleted();
    }

    abstract protected AmazonSKUListType createAmazonSKUList();
    abstract protected void populate(AmazonSKUListType list, ProductType productType);
    abstract protected AmazonSKUListKeyType createAmazonListKey(ProductType productType);
}
