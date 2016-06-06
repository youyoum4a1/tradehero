package com.androidth.general.api.education;

import android.os.Bundle;
import android.support.annotation.NonNull;
import com.androidth.general.common.persistence.AbstractIntegerDTOKey;

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

    @NonNull @Override public String getBundleKey()
    {
        return BUNDLE_KEY_LEY;
    }
}
