package com.tradehero.th.billing.googleplay;

import android.content.Context;
import com.tradehero.common.billing.InventoryFetcher;
import com.tradehero.common.billing.googleplay.IABSKU;
import com.tradehero.common.billing.googleplay.IABSKUListType;
import com.tradehero.common.billing.googleplay.exceptions.IABException;
import com.tradehero.common.milestone.BaseMilestone;
import com.tradehero.common.milestone.DependentMilestone;
import com.tradehero.common.milestone.Milestone;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListCache;
import com.tradehero.th.persistence.billing.googleplay.IABSKUListRetrievedMilestone;
import com.tradehero.th.persistence.billing.googleplay.THSKUDetailCache;
import com.tradehero.th.utils.DaggerUtils;
import dagger.Lazy;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

/** Created with IntelliJ IDEA. User: xavier Date: 11/21/13 Time: 6:50 PM To change this template use File | Settings | File Templates. */
public class THInventoryFetchMilestone extends BaseMilestone implements DependentMilestone
{
    public static final String TAG = THInventoryFetchMilestone.class.getSimpleName();

    private boolean running;
    private boolean complete;
    private boolean failed;
    private final Context context;
    private final IABSKUListType iabskuListType;
    private WeakReference<THIABActorInventoryFetcher> actorInventoryFetcherWeak = new WeakReference<>(null);
    private InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException> fetchListener;
    protected IABSKUListRetrievedMilestone dependsOn;
    private OnCompleteListener dependCompleteListener;
    @Inject Lazy<IABSKUListCache> iabskuListCache;
    @Inject Lazy<THSKUDetailCache> thskuDetailCache;

    public THInventoryFetchMilestone(Context context, THIABActorInventoryFetcher actorInventoryFetcher, IABSKUListType iabskuListType)
    {
        super();
        running = false;
        complete = false;
        failed = false;
        this.context = context;
        this.actorInventoryFetcherWeak = new WeakReference<>(actorInventoryFetcher);
        this.iabskuListType = iabskuListType;
        fetchListener = new InventoryFetcher.OnInventoryFetchedListener<IABSKU, THSKUDetails, IABException>()
        {
            @Override public void onInventoryFetchSuccess(int requestCode, List<IABSKU> productIdentifiers, Map<IABSKU, THSKUDetails> inventory)
            {
                running = false;
                notifyCompleteListener();
            }

            @Override public void onInventoryFetchFail(int requestCode, List<IABSKU> productIdentifiers, IABException exception)
            {
                running = false;
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
        dependsOn = new IABSKUListRetrievedMilestone(iabskuListType);
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
            THLog.d(TAG, "Details are already in cache");
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
        super.notifyCompleteListener();
    }

    @Override protected void notifyFailedListener(Throwable throwable)
    {
        failed = true;
        super.notifyFailedListener(throwable);
    }

    @Override public Milestone getDependsOn()
    {
        return dependsOn;
    }

    @Override public void setDependsOn(Milestone milestone)
    {
        if (!(milestone instanceof IABSKUListRetrievedMilestone))
        {
            throw new IllegalArgumentException("Only IABSKUListRetrievedMilestone is accepted");
        }
        milestone.setOnCompleteListener(dependCompleteListener);
        this.dependsOn = (IABSKUListRetrievedMilestone) milestone;
    }
}
