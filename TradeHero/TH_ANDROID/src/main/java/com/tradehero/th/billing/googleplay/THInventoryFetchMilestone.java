package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.BillingInventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exception.IABException;
import com.tradehero.common.milestone.BaseMilestone;
import com.tradehero.common.milestone.DependentMilestone;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedAsyncMilestone;
import com.tradehero.th.persistence.billing.googleplay.THIABProductDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;
import timber.log.Timber;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:50 PM To change this template use File | Settings | File Templates. */
public class THInventoryFetchMilestone extends BaseMilestone implements DependentMilestone
{
    private boolean running;
    private boolean complete;
    private boolean failed;
    private final Context context;
    private final IABSKUListType iabskuListType;
    private WeakReference<THIABActorInventoryFetcher> actorInventoryFetcherWeak = new WeakReference<>(null);
    private BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException> fetchListener;
    protected IABSKUListRetrievedAsyncMilestone dependsOn;
    private OnCompleteListener dependCompleteListener;
    @Inject Lazy<IABSKUListCache> iabskuListCache;
    @Inject Lazy<THIABProductDetailCache> thskuDetailCache;

    public THInventoryFetchMilestone(Context context, THIABActorInventoryFetcher actorInventoryFetcher, IABSKUListType iabskuListType)
    {
        super();
        running = false;
        complete = false;
        failed = false;
        this.context = context;
        this.actorInventoryFetcherWeak = new WeakReference<>(actorInventoryFetcher);
        this.iabskuListType = iabskuListType;
        fetchListener = new BillingInventoryFetcher.OnInventoryFetchedListener<IABSKU, THIABProductDetail, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers, Map<IABSKU, THIABProductDetail> inventory)
            {
                notifyCompleteListener();
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
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
        dependsOn = new IABSKUListRetrievedAsyncMilestone(iabskuListType);
        dependsOn.setOnCompleteListener(dependCompleteListener);
        DaggerUtils.inject(this);
    }

    @Override public void onDestroy()
    {
        if (dependsOn != null)
        {
            dependsOn.onDestroy();
        }
        actorInventoryFetcherWeak = null;
        fetchListener = null;
        dependsOn = null;
        dependCompleteListener = null;
    }

    @Override public void launch()
    {
        dependsOn.launch();
    }

    @Override public void launchOwn()
    {
        running = true;
        complete = false;
        failed = false;

        List<IABSKU> skus = iabskuListCache.get().get(IABSKUListType.getInApp());
        if (iabskuListCache.get().get(IABSKUListType.getSubs()) != null)
        {
            skus.addAll(iabskuListCache.get().get(IABSKUListType.getSubs()));
        }
        if (areDetailsInCache(skus))
        {
            Timber.d("Details are already in cache");
            notifyCompleteListener();
        }
        else
        {
            THIABActorInventoryFetcher actorInventoryFetcher = actorInventoryFetcherWeak.get();
            if (actorInventoryFetcher == null)
            {
                notifyFailedListener(new NullPointerException("actorInventoryFetcher was null"));
            }
            else
            {
                int requestCode = actorInventoryFetcher.registerInventoryFetchedListener(fetchListener);
                actorInventoryFetcher.launchInventoryFetchSequence(requestCode);
            }
        }
    }

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

    private boolean areDetailsInCache(List<IABSKU> skus)
    {
        for (IABSKU sku : skus)
        {
            if (thskuDetailCache.get().get(sku) == null)
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

    @Override public Milestone getDependsOn()
    {
        return dependsOn;
    }

    @Override public void setDependsOn(Milestone milestone)
    {
        if (!(milestone instanceof IABSKUListRetrievedAsyncMilestone))
        {
            throw new IllegalArgumentException("Only IABSKUListRetrievedAsyncMilestone is accepted");
        }
        milestone.setOnCompleteListener(dependCompleteListener);
        this.dependsOn = (IABSKUListRetrievedAsyncMilestone) milestone;
    }
}
