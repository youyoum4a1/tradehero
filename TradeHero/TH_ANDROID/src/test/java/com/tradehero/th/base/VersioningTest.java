package com.ayondo.academy.base;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import javax.inject.Inject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import timber.log.Timber;

import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class VersioningTest
{
    @Inject THApp THApp;

    @Test public void testVersionFollowsProperConventionOfForceUpgrade() throws PackageManager.NameNotFoundException
    {
        PackageInfo pInfo = THApp.getPackageManager().getPackageInfo(THApp.getPackageName(), 0);
        String versionName = pInfo.versionName;
        int versionCode = pInfo.versionCode;

        Timber.d("VersionName.Code %s.%d", versionName, versionCode);
        String[] elements = versionName.split("\\.");
        assertThat(elements.length).isEqualTo(3);

        int v0 = Integer.parseInt(elements[0]);
        int v1 = Integer.parseInt(elements[1]);
        int v2 = Integer.parseInt(elements[2]);

        assertThat(v0).isGreaterThanOrEqualTo(0);
        assertThat(v0).isLessThanOrEqualTo(10);
        assertThat(v1).isGreaterThanOrEqualTo(0);
        assertThat(v1).isLessThanOrEqualTo(10);
        assertThat(v2).isGreaterThanOrEqualTo(0);
        assertThat(v2).isLessThanOrEqualTo(10);

        assertThat(versionCode).isGreaterThanOrEqualTo(175);
    }
}
