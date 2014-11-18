package com.tradehero.th.api.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;

public class MiniGameDefKey extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY = MiniGameDefKey.class.getName() +".key";

    //<editor-fold desc="Constructors">
    public MiniGameDefKey(Integer key)
    {
        super(key);
    }

    public MiniGameDefKey(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @SuppressWarnings("UnusedDeclaration")
    @RouteProperty
    public void setGameId(int gameId)
    {
        this.key = gameId;
    }

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY;
    }

    @Override public String toString()
    {
        return String.format("[GameId key=%d]", key);
    }
}
