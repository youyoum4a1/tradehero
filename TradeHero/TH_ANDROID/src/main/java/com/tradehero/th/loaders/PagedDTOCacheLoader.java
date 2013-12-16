package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.common.utils.THLog;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.ref.WeakReference;
import java.util.List;

/**
 * Created by xavier on 12/13/13.
 */
abstract public class PagedDTOCacheLoader<
        DTOKeyType extends PagedDTOKey,
        AnyType,
        DTOType extends DTO & List<AnyType>>
        extends AsyncTaskLoader<DTOType>
{
    public static final String TAG = PagedDTOCacheLoader.class.getSimpleName();

    private DTOKeyType queryKey;
    private DTOType value;
    private boolean querying = false;
    /** When true, we should stop asking */
    private boolean noMorePages = false;
    private WeakReference<OnQueryingChangedListener> queryingChangedListenerWeak = new WeakReference<>(null);
    private WeakReference<OnNoMorePagesChangedListener> noMorePagesChangedListenerWeak = new WeakReference<>(null);

    public PagedDTOCacheLoader(Context context)
    {
        super(context);
        DaggerUtils.inject(this);
    }

    abstract protected DTOCache<DTOKeyType, DTOType> getCache();
    abstract protected DTOType createEmptyValue();
    abstract protected DTOKeyType cloneAtPage(DTOKeyType initial, int page);

    public void setQueryKey(final DTOKeyType queryKey)
    {
        if (queryKey == null)
        {
            throw new NullPointerException("queryKey cannot be null");
        }
        if (queryKey.getPage() == null)
        {
            throw new NullPointerException("queryKey.page cannot be null");
        }
        setHasNoMorePages(false);
        this.queryKey = queryKey;
        THLog.d(TAG, "setQueryKey " + this.queryKey);
        value = null;
    }

    public boolean isQuerying()
    {
        return this.querying;
    }

    protected void setQuerying(final boolean querying)
    {
        THLog.d(TAG, "setQuerying " + querying);
        boolean isChanged = querying != this.querying;
        this.querying = querying;
        if (isChanged)
        {
            notifyQueryingChanged(querying);
        }
    }

    public boolean hasNoMorePages()
    {
        return this.noMorePages;
    }

    protected void setHasNoMorePages(final boolean noMorePages)
    {
        THLog.d(TAG, "setHasNoMorePages " + noMorePages);
        boolean isChanged = noMorePages != this.noMorePages;
        this.noMorePages = noMorePages;
        if (isChanged)
        {
            notifyNoMorePagesChanged(noMorePages);
        }
    }

    @Override public DTOType loadInBackground()
    {
        THLog.d(TAG, "loadInBackground " + this.queryKey);
        setQuerying(true);
        DTOType value = null;
        try
        {
            value = getCache().getOrFetch(this.queryKey);
            if (value == null || value.size() == 0)
            {
                setHasNoMorePages(true);
            }
            else
            {
                THLog.d(TAG, "Got value count " + value.size());
            }
        }
        catch (Throwable throwable)
        {
            THLog.e(TAG, "Failed to get " + this.queryKey + " from cache", throwable);
        }
        finally
        {
            setQuerying(false);
        }
        return value;
    }

    public void loadNextPage()
    {
        THLog.d(TAG, "loadNextPage");
        if (this.queryKey != null && !isQuerying() && !hasNoMorePages())
        {
            this.queryKey = cloneAtPage(this.queryKey, this.queryKey.getPage() + 1);
            THLog.d(TAG, "Loading page " + this.queryKey.getPage());
            forceLoad();
        }
        else
        {
            THLog.d(TAG, "Not loading next page");
        }
    }

    @Override public void deliverResult(DTOType data)
    {
        THLog.d(TAG, "deliverResult");
        if (isReset())
        {
            THLog.d(TAG, "deliverResult was reset");
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (data != null)
            {
                data.clear();
                return;
            }
        }

        DTOType valueCopy = this.value;
        if (valueCopy == null)
        {
            THLog.d(TAG, "deliverResult is first");
            this.value = data;
        }
        else if (data != null)
        {
            THLog.d(TAG, "deliverResult add to existing");
            // We create a new list otherwise the loader manager does not detect the change
            DTOType newList = createEmptyValue();
            newList.addAll(valueCopy);
            newList.addAll(data);
            this.value = newList;
        }

        if (isStarted())
        {
            THLog.d(TAG, "passing super.deliverResult");
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(this.value);
        }
        else
        {
            THLog.d(TAG, "Not started, not passing super.deliverResult");
        }
    }

    /**
     * Listeners should be strongly referenced elsewhere
     * @param queryingChangedListener
     */
    public void setQueryingChangedListenerWeak(OnQueryingChangedListener queryingChangedListener)
    {
        this.queryingChangedListenerWeak = new WeakReference<>(queryingChangedListener);
    }

    /**
     * Listener should be strongly referenced elsewhere
     * @param onNoMorePagesChangedListener
     */
    public void setNoMorePagesChangedListenerWeak(OnNoMorePagesChangedListener onNoMorePagesChangedListener)
    {
        this.noMorePagesChangedListenerWeak = new WeakReference<>(onNoMorePagesChangedListener);
    }

    protected void notifyQueryingChanged(boolean querying)
    {
        OnQueryingChangedListener queryingChangedListener = this.queryingChangedListenerWeak.get();
        if (queryingChangedListener != null)
        {
            THLog.d(TAG, "notifyingQueryingChanged");
            queryingChangedListener.onQueryingChanged(querying);
        }
        else
        {
            THLog.d(TAG, "no listener for notifyingQueryingChanged");
        }
    }

    protected void notifyNoMorePagesChanged(boolean noMorePages)
    {
        OnNoMorePagesChangedListener noMorePagesChangedListener = this.noMorePagesChangedListenerWeak.get();
        if (noMorePagesChangedListener != null)
        {
            noMorePagesChangedListener.onNoMorePagesChanged(noMorePages);
        }
    }

    public static interface OnQueryingChangedListener
    {
        void onQueryingChanged(boolean querying);
    }

    public static interface OnNoMorePagesChangedListener
    {
        void onNoMorePagesChanged(boolean noMorePages);
    }
}
