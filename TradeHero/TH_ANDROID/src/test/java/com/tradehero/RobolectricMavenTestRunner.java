package com.tradehero;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.runners.model.InitializationError;
import org.robolectric.AndroidManifest;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.res.Fs;
import org.robolectric.res.FsFile;

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
            return new MavenAndroidManifest(Fs.newFile(new File(TestConstants.BASE_APP_FOLDER))) {
                @Override protected List<FsFile> findLibraries()
                {
                    List<FsFile> libraries = new ArrayList<>();
                    libraries.addAll(super.findLibraries());
                    libraries.addAll(getGitSubmoduleLibraries());
                    return libraries;
                }
            };
        }
        return super.getAppManifest(config);
    }
}
