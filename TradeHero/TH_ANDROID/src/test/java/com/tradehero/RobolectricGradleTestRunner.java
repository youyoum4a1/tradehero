package com.tradehero;

import com.ayondo.academy.BuildConfig;
import java.io.File;
import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.res.Fs;

@Deprecated // probably
public class RobolectricGradleTestRunner extends RobolectricTestRunner
{
    public RobolectricGradleTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override
    protected AndroidManifest getAppManifest(Config config)
    {
        if (config.manifest().equals(Config.DEFAULT))
        {
            if (BuildConfig.IS_INTELLIJ)
            {
                String manifestProperty = getConfigPath("manifests/full") + "AndroidManifest.xml";
                String resProperty = getConfigPath("res");
                String assetsProperty = getConfigPath("assets");
                return new GradleAndroidManifest(
                        Fs.fileFromPath(manifestProperty),
                        Fs.fileFromPath(resProperty),
                        Fs.fileFromPath(assetsProperty)
                );
            }
            else
            {
                String manifestProperty = System.getProperty("android.manifest");
                String resProperty = System.getProperty("android.resources");
                String assetsProperty = System.getProperty("android.assets");
                return new GradleAndroidManifest(
                        Fs.fileFromPath(manifestProperty),
                        Fs.fileFromPath(resProperty),
                        Fs.fileFromPath(assetsProperty)
                );
            }
        }
        return super.getAppManifest(config);
    }

    private String getConfigPath(String configType)
    {
        StringBuilder sb = new StringBuilder();
        sb
                //.append("TradeHero").append(File.separator).append("TH_ANDROID").append(File.separator)
                .append("build")
                .append(File.separator)
                .append("intermediates")
                .append(File.separator)
                .append(configType)
                .append(File.separator);
        if (BuildConfig.FLAVOR.length() > 0)
        {
            sb.append(BuildConfig.FLAVOR)
                    .append(File.separator);
        }
        sb.append(BuildConfig.BUILD_TYPE)
                .append(File.separator);
        return sb.toString();
    }
}
