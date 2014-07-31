package com.tradehero.th.models.user;

import com.tradehero.AbstractTestBase;
import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.Callback;
import retrofit.RetrofitError;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract public class FollowUserAssistantTestBase extends AbstractTestBase
{
    protected UserBaseKey heroId;
    protected SimplePremiumFollowUserAssistant.OnUserFollowedListener listener;
    protected UserServiceWrapper userServiceWrapper;

    public void setUp()
    {
        heroId = new UserBaseKey(123);
        listener = mock(SimplePremiumFollowUserAssistant.OnUserFollowedListener.class);
        userServiceWrapper = mock(UserServiceWrapper.class);
    }

    //<editor-fold desc="UserServiceWrapper answers">
    protected Answer<Object> createSuccessUserServiceAnswer(final UserProfileDTO expected)
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

    protected Answer<Object> createFailUserServiceAnswer(final RetrofitError expected)
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
    //</editor-fold>

    //<editor-fold desc="Prepare ServiceWrapper">
    protected void prepareUserServiceForFailUnfollow(
            SimplePremiumFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.unfollow(heroId, assistant)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessUnfollow(
            SimplePremiumFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.unfollow(heroId, assistant)).then(createSuccessUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForFailFollow(
            SimplePremiumFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.follow(heroId, assistant)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessFollow(
            SimplePremiumFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.follow(heroId, assistant)).then(createSuccessUserServiceAnswer(expected));
    }
    //</editor-fold>
}
