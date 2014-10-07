package com.tradehero.th.network.retrofit;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MiddleCallbackWeakList<DTOType> extends ArrayList<WeakReference<MiddleCallback<DTOType>>>
{
    //<editor-fold desc="Constructors">
    public MiddleCallbackWeakList()
    {
        super();
    }
    //</editor-fold>

    public void detach()
    {
        @Nullable MiddleCallback<DTOType> callback;
        for (@NotNull WeakReference<MiddleCallback<DTOType>> ref : this)
        {
            callback = ref.get();
            if (callback != null)
            {
                callback.setPrimaryCallback(null);
            }
        }
        clear();
    }

    public void add(@Nullable MiddleCallback<DTOType> callback)
    {
        if (callback != null)
        {
            add(new WeakReference<>(callback));
        }
    }
}
