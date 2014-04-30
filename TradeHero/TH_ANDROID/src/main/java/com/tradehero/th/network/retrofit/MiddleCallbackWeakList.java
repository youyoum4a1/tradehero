package com.tradehero.th.network.retrofit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Collection;

public class MiddleCallbackWeakList<DTOType> extends ArrayList<WeakReference<MiddleCallback<DTOType>>>
{
    //<editor-fold desc="Constructors">
    public MiddleCallbackWeakList(int initialCapacity)
    {
        super(initialCapacity);
    }

    public MiddleCallbackWeakList()
    {
        super();
    }

    public MiddleCallbackWeakList(Collection<? extends WeakReference<MiddleCallback<DTOType>>> c)
    {
        super(c);
    }
    //</editor-fold>

    public void detach()
    {
        MiddleCallback<DTOType> callback;
        for (WeakReference<MiddleCallback<DTOType>> ref : this)
        {
            callback = ref.get();
            if (callback != null)
            {
                callback.setPrimaryCallback(null);
            }
        }
        clear();
    }

    public void add(MiddleCallback<DTOType> callback)
    {
        add(new WeakReference<>(callback));
    }
}
