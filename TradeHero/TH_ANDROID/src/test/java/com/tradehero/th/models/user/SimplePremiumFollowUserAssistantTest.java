package com.tradehero.th.models.user;

import com.tradehero.RobolectricMavenTestRunner;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.billing.THBillingInteractor;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import javax.inject.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.Callback;
import retrofit.RetrofitError;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(RobolectricMavenTestRunner.class)
public class SimplePremiumFollowUserAssistantTest
{
    @Inject protected THBillingInteractor billingInteractor;
    private UserServiceWrapper userServiceWrapper;
    private SimplePremiumFollowUserAssistant assistant;

    @Before public void setUp()
    {
        userServiceWrapper = mock(UserServiceWrapper.class);
    }

    @After public void tearDown()
    {
        if (assistant != null)
        {
            assistant.setUserFollowedListener(null);
        }
    }

    private Answer<Object> createFailAnswer(final RetrofitError expected)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                //noinspection unchecked
                Callback<UserProfileDTO> callback = new BaseMiddleCallback<>((Callback<UserProfileDTO>) invocation.getArguments()[1]);
                callback.failure(expected);
                return callback;
            }
        };
    }

    private Answer<Object> createSuccessAnswer(final UserProfileDTO expected)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                //noinspection unchecked
                Callback<UserProfileDTO> callback = new BaseMiddleCallback<>((Callback<UserProfileDTO>) invocation.getArguments()[1]);
                callback.success(expected, null);
                return callback;
            }
        };
    }

    @Test public void listenerInConstructorWillGetSuccess()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetSuccess()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetSuccess()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        UserProfileDTO expected = mock(UserProfileDTO.class);

        assistant.notifyFollowSuccess(heroId, expected);

        verify(listener, times(0)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void listenerInConstructorWillGetFail()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerSetLaterWillGetFail()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, null);
        assistant.setUserFollowedListener(listener);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void listenerUnsetLaterWillNotGetFail()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        assistant.setUserFollowedListener(null);
        RetrofitError expected = mock(RetrofitError.class);

        assistant.notifyFollowFailed(heroId, expected);

        verify(listener, times(0)).onUserFollowFailed(heroId, expected);
    }

    @Test public void unfollowCallsService()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        assistant = new SimplePremiumFollowUserAssistant(
                heroId, null);
        assistant.userServiceWrapper = userServiceWrapper;
        assistant.launchUnFollow();

        verify(userServiceWrapper, times(1)).unfollow(heroId, assistant);
    }

    @Test public void followCallsService()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        assistant = new SimplePremiumFollowUserAssistant(
                heroId, null);
        assistant.userServiceWrapper = userServiceWrapper;
        assistant.launchFollow();

        verify(userServiceWrapper, times(1)).follow(heroId, assistant);
    }

    @Test public void unfollowErrorNotifiesListener()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        final RetrofitError expected = mock(RetrofitError.class);
        when(userServiceWrapper.unfollow(heroId, assistant)).then(createFailAnswer(expected));
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void unfollowSuccessNotifiesListener()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);
        when(userServiceWrapper.unfollow(heroId, assistant)).then(createSuccessAnswer(expected));
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchUnFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }

    @Test public void followErrorNotifiesListener()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        final RetrofitError expected = mock(RetrofitError.class);
        when(userServiceWrapper.follow(heroId, assistant)).then(createFailAnswer(expected));
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();

        verify(listener, times(1)).onUserFollowFailed(heroId, expected);
    }

    @Test public void followSuccessNotifiesListener()
    {
        UserBaseKey heroId = new UserBaseKey(123);
        SimplePremiumFollowUserAssistant.OnUserFollowedListener listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        assistant = new SimplePremiumFollowUserAssistant(heroId, listener);
        UserProfileDTO expected = mock(UserProfileDTO.class);
        when(userServiceWrapper.follow(heroId, assistant)).then(createSuccessAnswer(expected));
        assistant.userServiceWrapper = userServiceWrapper;

        assistant.launchFollow();

        verify(listener, times(1)).onUserFollowSuccess(heroId, expected);
    }
}
