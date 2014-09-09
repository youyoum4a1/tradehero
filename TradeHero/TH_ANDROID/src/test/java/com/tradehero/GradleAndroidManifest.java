package com.tradehero;

import com.tradehero.th.base.TestTHApp;

import org.robolectric.AndroidManifest;
import org.robolectric.res.FsFile;

/**
 * Created by tho on 8/15/2014.
 */
public class GradleAndroidManifest extends AndroidManifest
{
    public GradleAndroidManifest(FsFile fsManifest, FsFile fsRes, FsFile fsAsset)
    {
        super(fsManifest, fsRes, fsAsset);
    }

    /**
     * Note that robolectric does not support sdk ver 19 at this time
     *
     * @return minSdkTarget support
     */
    @Override
    public int getTargetSdkVersion()
    {
        return 16;
    }

    @Override
    public String getApplicationName()
    {
        return TestTHApp.class.getName();
    }
}
