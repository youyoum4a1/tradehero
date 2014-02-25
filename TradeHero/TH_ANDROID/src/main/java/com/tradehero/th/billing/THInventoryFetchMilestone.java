package com.tradehero.th.billing;

import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.ProductDetail;
import com.tradehero.common.billing.ProductIdentifier;
import com.tradehero.common.billing.exception.BillingException;
import com.tradehero.common.milestone.BaseMilestone;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.persistence.DTOKey;
import com.tradehero.th.persistence.billing.ProductIdentifierListRetrievedAsyncMilestone;
import com.tradehero.th.utils.DaggerUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import timber.log.Timber;

/**
 * Created by xavier on 2/25/14.
 */
abstract public class THInventoryFetchMilestone<
        ProductIdentifierListKey extends DTOKey,
        ProductIdentifierType extends ProductIdentifier,
        ProductIdentifierListType extends ArrayList<ProductIdentifierType> & DTO,
        ProductIdentifierListCacheType extends DTOCache<ProductIdentifierListKey, ProductIdentifierListType>,
        ProductDetailType extends ProductDetail<ProductIdentifierType>,
        ProductDetailCacheType extends DTOCache<ProductIdentifierType, ProductDetailType>,
        BillingExceptionType extends BillingException>
        extends BaseMilestone
{
    protected boolean running;
    protected boolean complete;
    protected boolean failed;
    protected final ProductIdentifierListKey productIdentifierListKey;
    protected BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType> fetchListener;
    protected ProductIdentifierListRetrievedAsyncMilestone<
            ProductIdentifierListKey,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductIdentifierListCacheType> dependsOn;
    protected OnCompleteListener dependCompleteListener;

    public THInventoryFetchMilestone(ProductIdentifierListKey productIdentifierListKey)
    {
        super();
        DaggerUtils.inject(this);
        running = false;
        complete = false;
        failed = false;
        this.productIdentifierListKey = productIdentifierListKey;
        fetchListener = new BillingInventoryFetcher.OnInventoryFetchedListener<ProductIdentifierType, ProductDetailType, BillingExceptionType>()
        {
            @Override public void onInventoryFetchSuccess(
                    int requestCode,
                    List<ProductIdentifierType> productIdentifiers,
                    Map<ProductIdentifierType, ProductDetailType> inventory)
            {
                notifyCompleteListener();
            }

            @Override public void onInventoryFetchFail(
                    int requestCode,
                    List<ProductIdentifierType> productIdentifiers,
                    BillingExceptionType exception)
            {
                notifyFailedListener(exception);
            }
        };
        dependCompleteListener = new OnCompleteListener()
        {
            @Override public void onComplete(Milestone milestone)
            {
                launchOwn();
            }

            @Override public void onFailed(Milestone milestone, Throwable throwable)
            {
                notifyFailedListener(throwable);
            }
        };
        dependsOn = createDependsOnMilestone(productIdentifierListKey);
        dependsOn.setOnCompleteListener(dependCompleteListener);
    }

    abstract protected ProductIdentifierListRetrievedAsyncMilestone<
            ProductIdentifierListKey,
            ProductIdentifierType,
            ProductIdentifierListType,
            ProductIdentifierListCacheType> createDependsOnMilestone(ProductIdentifierListKey productIdentifierListKey);

    @Override public void onDestroy()
    {
        if (dependsOn != null)
        {
            dependsOn.onDestroy();
        }
        fetchListener = null;
        dependsOn = null;
        dependCompleteListener = null;
    }

    @Override public void launch()
    {
        dependsOn.launch();
    }

    abstract protected ProductIdentifierListCacheType getProductIdentifierListCache();
    abstract protected ProductDetailCacheType getProductDetailCache();

    public void launchOwn()
    {
        running = true;
        complete = false;
        failed = false;

        List<ProductIdentifierType> productIdentifiers = getAllProductIdentifiers();
        if (areDetailsInCache(productIdentifiers))
        {
            Timber.d("Details are already in cache");
            notifyCompleteListener();
        }
        else
        {
            launchFetchProper(productIdentifiers);
        }
    }

    abstract protected void launchFetchProper(List<ProductIdentifierType> productIdentifiers);
    abstract protected List<ProductIdentifierType> getAllProductIdentifiers();

    @Override public boolean isRunning()
    {
        return running;
    }

    @Override public boolean isComplete()
    {
        return complete;
    }

    @Override public boolean isFailed()
    {
        return failed;
    }

    protected boolean areDetailsInCache(List<ProductIdentifierType> skus)
    {
        for (ProductIdentifierType productIdentifier : skus)
        {
            if (getProductDetailCache().get(productIdentifier) == null)
            {
                return false;
            }
        }
        return true;
    }

    @Override protected void notifyCompleteListener()
    {
        complete = true;
        failed = false;
        running = false;
        super.notifyCompleteListener();
    }

    @Override protected void notifyFailedListener(Throwable throwable)
    {
        complete = false;
        failed = true;
        running = false;
        super.notifyFailedListener(throwable);
    }
}
