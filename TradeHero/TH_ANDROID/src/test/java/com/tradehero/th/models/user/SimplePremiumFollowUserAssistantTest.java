package com.tradehero.th.models.user;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.users.UserProfileDTO;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.RetrofitError;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(RobolectricMavenTestRunner.class)
public class SimplePremiumFollowUserAssistantTest extends FollowUserAssistantTestBase
{
    private SimplePremiumFollowUserAssistant assistant;

    @Before @Override public void setUp()
    {
        super.setUp();
    }

    @After public void tearDown()
    {
        if (assistant != null)
        {
            assistant.setUserFollowedListener(null);
        }
        assistant = null;
    }

    //<editor-fold desc="Wired notify methods">
    @Test public void listenerInConstructorWillGetSuccess()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetSuccess()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetSuccess()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(0)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerInConstructorWillGetFail()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetFail()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetFail()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(0)).onUserFollowFailed(heroId, expected);
    }
    //</editor-fold>

    //<editor-fold desc="Call forwarding">
    @Test public void unfollowCallsService()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        // Prepare user service
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchUnFollow();

        verify(userServiceWrapper, times(1)).unfollow(heroId, assistant);
    }

    @Test public void followCallsService()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        // Prepare user service
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }
    //</editor-fold>

    //<editor-fold desc="Error and success forwarding">
    @Test public void unfollowErrorNotifiesListener()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailUnfollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void unfollowSuccessNotifiesListener()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessUnfollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followErrorNotifiesListener()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followSuccessNotifiesListener()
    {
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessFollow(assistant, expected);
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }
    //</editor-fold>
}
