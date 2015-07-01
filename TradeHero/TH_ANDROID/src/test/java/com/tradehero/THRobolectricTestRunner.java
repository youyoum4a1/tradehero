package com.tradehero;

import org.junit.runners.model.InitializationError;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.manifest.AndroidManifest;
import org.robolectric.util.ReflectionHelpers;

public class THRobolectricTestRunner extends RobolectricGradleTestRunner
{
    public THRobolectricTestRunner(Class<?> testClass) throws InitializationError
    {
        super(testClass);
    }

    @Override protected AndroidManifest getAppManifest(Config config)
    {
        if (config.manifest().equals(Config.DEFAULT))
        {
            System.out.println("Flavor " + getFlavor(config));
            //return new MavenAndroidManifest(Fs.newFile(new File(TestConstants.BASE_APP_FOLDER))) {
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

    private String getFlavor(Config config) {
        try {
            return (String) ReflectionHelpers.getStaticField(config.constants(), "FLAVOR");
        } catch (Throwable var3) {
            return null;
        }
    }
}
