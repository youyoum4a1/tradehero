package com.tradehero;

import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

/**
 * Created by tho on 8/14/2014.
 */
public class RobolectricGradleTestRunner extends RobolectricTestRunner
{
    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config)
    {
        String manifestProperty = System.getProperty("android.manifest");
        if (config.manifest().equals(Config.DEFAULT))
        {
            String resProperty = System.getProperty("android.resources");
            String assetsProperty = System.getProperty("android.assets");
            return new GradleAndroidManifest(Fs.fileFromPath(manifestProperty),
                    Fs.fileFromPath(resProperty),
                    Fs.fileFromPath(assetsProperty));
        }
        return super.getAppManifest(config);
    }
}
