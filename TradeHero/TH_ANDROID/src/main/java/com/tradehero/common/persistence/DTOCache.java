package com.tradehero.common.persistence;

import android.os.AsyncTask;

/**
 * Created with IntelliJ IDEA. User: xavier Date: 10/3/13 Time: 4:48 PM To change this template use File | Settings | File Templates.
 *
 * See DTOKeyIdList to avoid duplicating data in caches.
 */
public interface DTOCache<DTOKeyType extends DTOKey, DTOType extends DTO>
{
    /**
     * This method should be implemented so that it is very fast. Indeed this method is sometimes used before deciding
     * whether to getOrFetch
     * @param key
     * @return
     */
    DTOType get(DTOKeyType key);

    DTOType getOrFetch(DTOKeyType key);
    DTOType getOrFetch(DTOKeyType key, boolean force);
    GetOrFetchTask<DTOType> getOrFetch(DTOKeyType key, Listener<DTOKeyType, DTOType> callback);
    GetOrFetchTask<DTOType> getOrFetch(DTOKeyType key, boolean force, Listener<DTOKeyType, DTOType> callback);
    DTOType put(DTOKeyType key, DTOType value);

    public static interface Listener<DTOKeyType, DTOType>
    {
        void onDTOReceived(DTOKeyType key, DTOType value);
    }

    /**
     * Indicates whether a task should forget its listener
     */
    public static interface ForgettableListenerTask
    {
        void forgetListener(boolean forgetListener);
    }

    /**
     * The advantage of this class over simply a cancellable AsyncTask is that implementations can let the fetch run its course,
     * which includes caching, but stop just before calling the listener.
     * @param <DTOType>
     */
    abstract public static class GetOrFetchTask<DTOType> extends AsyncTask<Void, Void, DTOType> implements ForgettableListenerTask
    {
        private boolean forgetListener;

        public GetOrFetchTask()
        {
            super();
        }

        public boolean hasForgottenListener()
        {
            return forgetListener;
        }

        @Override public void forgetListener(boolean forgetListener)
        {
            this.forgetListener = forgetListener;
        }
    }
}
