package com.tradehero.th.fragments.alert;

import android.os.Bundle;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.THRobolectricTestRunner;
import com.tradehero.common.annotation.ForApp;
import com.tradehero.th.activities.DashboardActivity;
import com.tradehero.th.activities.DashboardActivityExtended;
import com.tradehero.th.api.alert.AlertDTO;
import com.tradehero.th.api.alert.AlertPlanDTO;
import com.tradehero.th.api.alert.UserAlertPlanDTO;
import com.tradehero.th.api.users.CurrentUserId;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.fragments.DashboardNavigator;
import com.tradehero.th.persistence.alert.AlertCacheRx;
import com.tradehero.th.persistence.user.UserProfileCacheRx;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.Robolectric;

import static com.tradehero.THRobolectric.runBgUiTasks;
import static org.fest.assertions.api.Assertions.assertThat;

@RunWith(THRobolectricTestRunner.class)
public class AlertViewFragmentTest
{
    @Inject @ForApp ObjectMapper mapper;
    @Inject CurrentUserId currentUserId;
    @Inject UserProfileCacheRx userProfileCache;
    @Inject AlertCacheRx alertCache;
    @Inject DashboardNavigator dashboardNavigator;
    private AlertDTO cachedAlertDTO;
    private UserProfileDTO cachedProfileDTO;

    protected String getPackagePath(Class<?> klass)
    {
        return '/' + klass.getPackage().getName().replace('.', '/');
    }

    @Before public void setUp() throws IOException
    {
        DashboardActivity activity = Robolectric.setupActivity(DashboardActivityExtended.class);
        activity.inject(this);

        currentUserId.set(2207);
        cachedAlertDTO = mapper.readValue(
                getClass().getResourceAsStream(getPackagePath(AlertDTO.class) + "/AlertDTOBody1.json"),
                AlertDTO.class);
        alertCache.onNext(cachedAlertDTO.getAlertId(currentUserId.toUserBaseKey()), cachedAlertDTO);

        InputStream profileStream = getClass().getResourceAsStream(getPackagePath(UserProfileDTO.class) + "/UserProfileDTO1.json");
        cachedProfileDTO = mapper.readValue(
                profileStream,
                UserProfileDTO.class);
        UserAlertPlanDTO userAlertPlan = new UserAlertPlanDTO();
        userAlertPlan.alertPlan = new AlertPlanDTO();
        userAlertPlan.alertPlan.numberOfAlerts = 1;
        cachedProfileDTO.userAlertPlans = Collections.singletonList(userAlertPlan);
        userProfileCache.onNext(currentUserId.toUserBaseKey(), cachedProfileDTO);
    }

    @After public void tearDown()
    {
        alertCache.invalidateAll();
        userProfileCache.invalidateAll();
    }

    @Test public void launchWillPopulateFromCache() throws InterruptedException
    {
        Bundle args = new Bundle();
        AlertViewFragment.putAlertId(args, cachedAlertDTO.getAlertId(currentUserId.toUserBaseKey()));
        AlertViewFragment alertViewFragment = dashboardNavigator.pushFragment(AlertViewFragment.class, args);

        runBgUiTasks(3);

        assertThat(cachedAlertDTO).isNotNull();
        assertThat(alertViewFragment.alertDTO).isNotNull();
        assertThat(alertViewFragment.alertDTO.id).isEqualTo(cachedAlertDTO.id);
    }

    @Test public void clickOnOffWillSetMiddleCallback() throws InterruptedException {
        Bundle args = new Bundle();
        AlertViewFragment.putAlertId(args, cachedAlertDTO.getAlertId(currentUserId.toUserBaseKey()));
        AlertViewFragment alertViewFragment = dashboardNavigator.pushFragment(AlertViewFragment.class, args);

        runBgUiTasks(3);

        assertThat(alertViewFragment.updateAlertSubscription).isNull();

        assertThat(alertViewFragment.alertToggle).isNotNull();
        alertViewFragment.alertToggle.performClick();

        assertThat(alertViewFragment.updateAlertSubscription).isNotNull();
    }
}
