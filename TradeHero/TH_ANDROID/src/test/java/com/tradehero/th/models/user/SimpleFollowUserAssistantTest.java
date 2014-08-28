package com.tradehero.th.models.user;

import com.tradehero.THRobolectricTestRunner;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import retrofit.RetrofitError;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@RunWith(THRobolectricTestRunner.class)
public class SimpleFollowUserAssistantTest extends FollowUserAssistantTestBase
{
    private SimpleFollowUserAssistant assistant;

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
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetSuccess()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetSuccess()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowSuccess(heroId, expected);

        verify(listener, times(0)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerInConstructorWillGetFail()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        RetrofitError expected = mock(RetrofitError.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetFail()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        RetrofitError expected = mock(RetrofitError.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetFail()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        RetrofitError expected = mock(RetrofitError.class);

        ((OpenSimpleFollowUserAssistant) assistant).notifyFollowFailed(heroId, expected);

        verify(listener, times(0)).onUserFollowFailed(heroId, expected);
    }
    //</editor-fold>

    //<editor-fold desc="Call forwarding">
    @Test public void unfollowCallsService()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, null);
        // Prepare user service
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollow();

        verify(userServiceWrapper, times(1)).unfollow(heroId, assistant);
    }

    @Test public void followCallsService()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, null);
        // Prepare user service
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        ((OpenSimpleFollowUserAssistant) assistant).launchPremiumFollow();

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }
    //</editor-fold>

    //<editor-fold desc="Error and success forwarding">
    @Test public void unfollowErrorNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailUnfollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void unfollowSuccessNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessUnfollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followErrorNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        // Prepare user service
        final RetrofitError expected = mock(RetrofitError.class);
        prepareUserServiceForFailFollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        ((OpenSimpleFollowUserAssistant) assistant).launchPremiumFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followSuccessNotifiesListener()
    {
        assistant = new OpenSimpleFollowUserAssistant(heroId, listener);
        // Prepare user service
        UserProfileDTO expected = mock(UserProfileDTO.class);
        prepareUserServiceForSuccessFollow(assistant, expected);
        ((OpenSimpleFollowUserAssistant) assistant).setUserServiceWrapper(userServiceWrapper);

        ((OpenSimpleFollowUserAssistant) assistant).launchPremiumFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }
    //</editor-fold>
}
