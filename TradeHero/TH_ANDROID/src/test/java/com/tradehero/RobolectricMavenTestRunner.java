package com.tradehero;

import com.tradehero.base.TestApplication;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;

public class RobolectricMavenTestRunner extends RobolectricTestRunner
{
    public RobolectricMavenTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override protected AndroidManifest getAppManifest(Config config)
    {
        if (config.manifest().equals(Config.DEFAULT))
        {
            return new AndroidManifest(
                    Fs.fileFromPath(TestConstants.MANIFEST_PATH),
                    Fs.fileFromPath(TestConstants.RES_PATH),
                    Fs.fileFromPath(TestConstants.ASSETS_PATH)) {
                @Override public String getApplicationName()
                {
                    return TestApplication.class.getName();
                }

                @Override public int getTargetSdkVersion()
                {
                    return 17;
                }
            };
        }
        return super.getAppManifest(config);
    }
}
