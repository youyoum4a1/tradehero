package com.tradehero;

import com.ayondo.academy.base.TestTHApp;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.FsFile;

@Deprecated // probably
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
        return 21;
    }

    @Override
    public String getApplicationName()
    {
        return TestTHApp.class.getName();
    }
}
