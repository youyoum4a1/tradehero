package com.ayondo.academy.models.user.follow;

import com.ayondo.academyRobolectric;
import com.ayondo.academyRobolectricTestRunner;
import com.ayondo.academy.BuildConfig;
import com.ayondo.academy.activities.DashboardActivityExtended;
import com.ayondo.academy.api.users.UserProfileDTO;
import com.ayondo.academy.base.THApp;
import com.ayondo.academy.models.user.FollowUserAssistantTestBase;
import com.ayondo.academy.models.user.OpenSimpleFollowUserAssistant;
import com.ayondo.academy.rx.EmptyAction1;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.annotation.Config;
import retrofit.RetrofitError;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(THRobolectricTestRunner.class)
@Config(constants = BuildConfig.class)
public class SimpleFollowUserAssistantTest extends FollowUserAssistantTestBase
{
    private FollowUserAssistant assistant;

    @Before @Override public void setUp()
    {
        super.setUp();
        THRobolectric.setupActivity(DashboardActivityExtended.class).inject(this);
    }

    @After public void tearDown()
    {
        assistant = null;
    }

    //<editor-fold desc="Call forwarding">
    @Test public void unfollowCallsService()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollowRx().subscribe(new EmptyAction1<UserProfileDTO>(), new EmptyAction1<Throwable>());

        verify(userServiceWrapper, times(1)).unfollowRx(heroId);
    }

    @Test public void followCallsService()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        ((OpenSimpleFollowUserAssistant) assistant).launchPremiumFollowRx();

        verify(userServiceWrapper, times(1)).followRx(heroId);
    }
    //</editor-fold>

    //<editor-fold desc="Error and success forwarding">
    @Test(expected = RetrofitError.class)
    public void unfollowErrorNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailUnfollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollowRx().subscribe();
    }

    @Test public void unfollowSuccessNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessUnfollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollowRx().subscribe(new EmptyAction1<UserProfileDTO>(), new EmptyAction1<Throwable>());

        //verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test(expected = RetrofitError.class)
    public void followErrorNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx().subscribe();
    }

    @Test public void followSuccessNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(THApp.context(), heroId);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessFollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchPremiumFollowRx();

        //verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }
    //</editor-fold>
}
