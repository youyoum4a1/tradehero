package com.androidth.general.api.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.DTOKey;

public class VideoCategoryId implements DTOKey
{
    private static final String BUNDLE_KEY_ID = VideoCategoryId.class.getName() + ".id";

    @NonNull public final Integer id;

    //<editor-fold desc="Constructors">
    public VideoCategoryId(@NonNull Integer id)
    {
        super();
        this.id = id;
    }

    public VideoCategoryId(@NonNull Bundle args)
    {
        super();
        this.id = args.getInt(BUNDLE_KEY_ID);
    }
    //</editor-fold>

    @Override public int hashCode()
    {
        return id.hashCode();
    }

    @Override public boolean equals(Object other)
    {
        return other instanceof VideoCategoryId
                && equalsFields((VideoCategoryId) other);
    }

    protected boolean equalsFields(@NonNull VideoCategoryId other)
    {
        return id.equals(other.id);
    }

    public Bundle getArgs()
    {
        Bundle args = new Bundle();
        putParameters(args);
        return args;
    }

    protected void putParameters(Bundle args)
    {
        args.putInt(BUNDLE_KEY_ID, id);
    }
}
