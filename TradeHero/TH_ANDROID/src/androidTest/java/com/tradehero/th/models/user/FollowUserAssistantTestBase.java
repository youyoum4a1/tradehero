package com.tradehero.th.models.user;

import com.tradehero.th.api.users.UserBaseKey;
import com.tradehero.th.api.users.UserProfileDTO;
import com.tradehero.th.models.user.follow.SimpleFollowUserAssistant;
import com.tradehero.th.network.service.UserServiceWrapper;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import retrofit.RetrofitError;
import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

abstract public class FollowUserAssistantTestBase
{
    protected UserBaseKey heroId;
    protected UserServiceWrapper userServiceWrapper;

    public void setUp()
    {
        heroId = new UserBaseKey(123);
        userServiceWrapper = mock(UserServiceWrapper.class);
    }

    //<editor-fold desc="UserServiceWrapper answers">
    protected Answer<Object> createSuccessUserServiceAnswer(final UserProfileDTO expected)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return Observable.just(expected);
            }
        };
    }

    protected Answer<Object> createFailUserServiceAnswer(final RetrofitError expected)
    {
        return new Answer<Object>()
        {
            @Override public Object answer(InvocationOnMock invocation) throws Throwable
            {
                return Observable.error(expected);
            }
        };
    }
    //</editor-fold>

    //<editor-fold desc="Prepare ServiceWrapper">
    protected void prepareUserServiceForFailUnfollow(
            SimpleFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.unfollowRx(heroId)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessUnfollow(
            SimpleFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.unfollowRx(heroId)).then(createSuccessUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForFailFollow(
            SimpleFollowUserAssistant assistant,
            RetrofitError expected)
    {
        when(userServiceWrapper.followRx(heroId)).then(createFailUserServiceAnswer(expected));
    }

    protected void prepareUserServiceForSuccessFollow(
            SimpleFollowUserAssistant assistant,
            UserProfileDTO expected)
    {
        when(userServiceWrapper.followRx(heroId)).then(createSuccessUserServiceAnswer(expected));
    }
    //</editor-fold>
}
