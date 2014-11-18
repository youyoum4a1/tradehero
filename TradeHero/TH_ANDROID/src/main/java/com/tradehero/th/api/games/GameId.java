package com.tradehero.th.api.games;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import com.tradehero.route.RouteProperty;

public class GameId extends AbstractIntegerDTOKey
{
    public static final String BUNDLE_KEY_KEY = GameId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public GameId(Integer key)
    {
        super(key);
    }

    public GameId(@NonNull Bundle args)
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
        return BUNDLE_KEY_KEY;
    }

    @Override public String toString()
    {
        return String.format("[GameId key=%d]", key);
    }
}
