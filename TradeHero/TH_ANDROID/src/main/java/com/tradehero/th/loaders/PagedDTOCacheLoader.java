package com.tradehero.th.loaders;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import com.tradehero.common.api.PagedDTOKey;
import com.tradehero.common.persistence.DTO;
import com.tradehero.common.persistence.DTOCache;
import com.tradehero.th.utils.DaggerUtils;
import java.lang.ref.WeakReference;
import java.util.List;
import timber.log.Timber;

/**
 * Created by xavier on 12/13/13.
 */
abstract public class PagedDTOCacheLoader<
        DTOKeyType extends PagedDTOKey,
        AnyType,
        DTOType extends DTO & List<AnyType>>
        extends AsyncTaskLoader<DTOType>
{
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
        Timber.d("Wangliang PagedDTOCacheLoader contructor queryKey %s",queryKey);
    }

    abstract protected DTOCache<DTOKeyType, DTOType> getCache();
    abstract protected DTOType createEmptyValue();
    abstract protected DTOKeyType cloneAtPage(DTOKeyType initial, int page);

    public void setQueryKey(final DTOKeyType queryKey)
    {
        Timber.d("Wangliang PagedDTOCacheLoader setQueryKey %s",queryKey);
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
        Timber.d("setQueryKey %s", this.queryKey);
        value = null;
    }

    public DTOKeyType getQueryKey()
    {
        return queryKey;
    }

    public boolean isQuerying()
    {
        return this.querying;
    }

    protected void setQuerying(final boolean querying)
    {
        Timber.d("setQuerying %b", querying);
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
        Timber.d("setHasNoMorePages %b", noMorePages);
        boolean isChanged = noMorePages != this.noMorePages;
        this.noMorePages = noMorePages;
        if (isChanged)
        {
            notifyNoMorePagesChanged(noMorePages);
        }
    }

    @Override public DTOType loadInBackground()
    {
        Timber.d("Wangliang loadInBackground queryKey %s ",queryKey);
        Timber.d("loadInBackground %s", this.queryKey);
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
                Timber.d("Got value count " + value.size());
            }
        }
        catch (Throwable throwable)
        {
            Timber.e("Failed to get %s from cache", this.queryKey, throwable);
        }
        finally
        {
            setQuerying(false);
        }
        return value;
    }

    public void loadNextPage()
    {
        Timber.d("Wangliang  loadNextPage %s",queryKey);
        Timber.d("loadNextPage");
        if (this.queryKey != null && !isQuerying() && !hasNoMorePages())
        {
            this.queryKey = cloneAtPage(this.queryKey, this.queryKey.getPage() + 1);
            Timber.d("Loading page %d", this.queryKey.getPage());
            forceLoad();
        }
        else
        {
            Timber.d("Not loading next page");
        }
    }

    @Override public void deliverResult(DTOType data)
    {
        Timber.d("deliverResult");
        if (isReset())
        {
            Timber.d("deliverResult was reset");
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
            Timber.d("deliverResult is first");
            this.value = data;
        }
        else if (data != null)
        {
            Timber.d("deliverResult add to existing");
            // We create a new list otherwise the loader manager does not detect the change
            DTOType newList = createEmptyValue();
            newList.addAll(valueCopy);
            newList.addAll(data);
            this.value = newList;
        }

        if (isStarted())
        {
            Timber.d("passing super.deliverResult");
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(this.value);
        }
        else
        {
            Timber.d("Not started, not passing super.deliverResult");
        }
    }

    @Override
    protected void onStartLoading() {
        super.onStartLoading();
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
            Timber.d("notifyingQueryingChanged");
            queryingChangedListener.onQueryingChanged(querying);
        }
        else
        {
            Timber.d("no listener for notifyingQueryingChanged");
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
