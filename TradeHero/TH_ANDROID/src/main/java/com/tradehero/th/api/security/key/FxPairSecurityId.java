package com.tradehero.th.api.security.key;

import android.support.annotation.NonNull;

public class FxPairSecurityId
{
    @NonNull public final String left;
    @NonNull public final String right;

    //<editor-fold desc="Constructors">
    public FxPairSecurityId(@NonNull String left, @NonNull String right)
    {
        this.left = left;
        this.right = right;
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return left.hashCode() ^ right.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other.getClass().equals(getClass())
                && equals((FxPairSecurityId) other);
    }

    protected boolean equals(@NonNull FxPairSecurityId other)
    {
        return other.left.equals(left)
                && other.right.equals(right);
    }
}
