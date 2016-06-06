package com.androidth.general.api.security.key;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

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

    @Override public boolean equals(@Nullable Object other)
    {
        return other instanceof FxPairSecurityId
                && equalFields((FxPairSecurityId) other);
    }

    protected boolean equalFields(@NonNull FxPairSecurityId other)
    {
        return other.left.equals(left)
                && other.right.equals(right);
    }
}
