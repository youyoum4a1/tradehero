package com.tradehero.th.api.education;

import android.os.Bundle;
import com.tradehero.common.persistence.AbstractIntegerDTOKey;
import org.jetbrains.annotations.NotNull;

public class VideoId extends AbstractIntegerDTOKey
{
    private static final String BUNDLE_KEY_LEY = VideoId.class.getName() + ".key";

    //<editor-fold desc="Constructors">
    public VideoId(@NotNull Integer key)
    {
        super(key);
    }

    public VideoId(@NotNull Bundle args)
    {
        super(args);
    }
    //</editor-fold>

    @Override public String getBundleKey()
    {
        return BUNDLE_KEY_LEY;
    }
}
