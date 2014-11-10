package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.retrofit.BaseMiddleCallback;
import com.tradehero.th.network.service.UserServiceWrapper;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.Callback;
import retrofit.RetrofitError;
import rx.Observer;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract public class FollowUserAssistantTestBase
{
    protected UserBaseKey heroId;
    protected SimpleFollowUserAssistant.OnUserFollowedListener listener;
    protected UserServiceWrapper userServiceWrapper;

    public void setUp()
    {
        heroId = new UserBaseKey(123);
        listener = mock(SimpleFollowUserAssistant.OnUserFollowedListener.class);
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
                Observer<UserProfileDTO> callback = new BaseMiddleCallback<>((Observer<UserProfileDTO>) invocation.getArguments()[1]);
                callback.onError(expected);
                return callback;
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="Prepare ServiceWrapper">
    protected void prepareUserServiceForFailUnfollow(
            SimpleFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.unfollowRx(heroId, assistant)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessUnfollow(
            SimpleFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.unfollowRx(heroId, assistant)).then(createSuccessUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForFailFollow(
            SimpleFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.followRx(heroId, assistant)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessFollow(
            SimpleFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.followRx(heroId, assistant)).then(createSuccessUserServiceAnswer(expected));
    }
    //</editor-fold>
}
