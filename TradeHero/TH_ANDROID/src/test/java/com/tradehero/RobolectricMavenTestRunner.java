package com.tradehero;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;

@Deprecated // probably
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
            //return new MavenAndroidManifest(
            //        Fs.newFile(new File(TestConstants.BASE_APP_FOLDER))) {
            //    @Override protected List<FsFile> findLibraries()
            //    {
            //        List<FsFile> libraries = new ArrayList<>();
            //        libraries.addAll(super.findLibraries());
            //        libraries.addAll(getGitSubmoduleLibraries());
            //        return libraries;
            //    }
            //};
        }
        return super.getAppManifest(config);
    }
}
