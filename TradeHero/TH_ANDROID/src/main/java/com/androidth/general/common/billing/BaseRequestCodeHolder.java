package com.androidth.general.common.billing;

import android.support.annotation.NonNull;
import java.util.HashMap;
import java.util.Map;

abstract public class BaseRequestCodeHolder<ActorType> implements RequestCodeHolder
{
    @NonNull protected final Map<Integer /*requestCode*/, ActorType> actors;

    //<editor-fold desc="Constructors">
    public BaseRequestCodeHolder()
    {
        super();
        this.actors = new HashMap<>();
    }
    //</editor-fold>

    @Override public void onDestroy()
    {
        actors.clear();
    }

    @Override public boolean isUnusedRequestCode(int requestCode)
    {
        return !actors.containsKey(requestCode);
    }

    @Override public void forgetRequestCode(int requestCode)
    {
        actors.remove(requestCode);
    }
}
