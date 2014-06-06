package com.tradehero;

import com.tradehero.th.base.TestApplication;
import java.io.File;
import java.util.List;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

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
            return new MavenAndroidManifest(Fs.newFile(new File(TestConstants.BASE_APP_FOLDER)));
        }
        return super.getAppManifest(config);
    }

    /**
     * This class do a nice trick to search for generated libraries, for each library, it will pick up corresponding resource folder
     * and add it into resource pool, they are to be used for testing with reobolectric
     */
    public static class MavenAndroidManifest extends AndroidManifest
    {
        public MavenAndroidManifest(FsFile baseDir)
        {
            super(baseDir);
        }

        @Override public String getApplicationName()
        {
            return TestApplication.class.getName();
        }

        /**
         * Note that robolectric does not support sdk ver 19 at this time
         * @return minSdkTarget support
         */
        @Override public int getTargetSdkVersion()
        {
            return 16;
        }

        @Override protected List<FsFile> findLibraries()
        {
            // Try unpack folder from Maven/IntelliJ
            FsFile unpack = getBaseDir().join(TestConstants.LIBRARIES_GENERATED_FOLDER);
            if (unpack.exists())
            {
                FsFile[] libs = unpack.listFiles(new FsFile.Filter()
                {
                    @Override public boolean accept(FsFile fsFile)
                    {
                        return (fsFile != null) && (fsFile.isDirectory());
                    }
                });

                if (libs != null)
                {
                    return asList(libs);
                }
            }
            return emptyList();
        }

        @Override protected AndroidManifest createLibraryAndroidManifest(FsFile libraryBaseDir)
        {
            return new MavenAndroidManifest(libraryBaseDir);
        }
    }
}
