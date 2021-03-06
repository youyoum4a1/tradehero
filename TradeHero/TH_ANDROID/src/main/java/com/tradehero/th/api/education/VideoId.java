package com.tradehero.th.api.education;

import android.os.Bundle;

import com.tradehero.common.persistence.AbstractIntegerDTOKey;

import android.support.annotation.NonNull;

public class VideoId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_LEY = VideoId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public VideoId(@NonNull Integer key)
    {
        super(key);
    }

    public VideoId(@NonNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_LEY;
    }
}
