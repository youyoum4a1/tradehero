package com.ayondo.academy.network.retrofit;

import com.ayondo.academy.network.service.AchievementMockServiceWrapper;
import com.ayondo.academy.network.service.AchievementServiceWrapper;
import com.ayondo.academy.network.service.RetrofitStubProtectedModule;
import dagger.Module;
import dagger.Provides;

@Module(
        includes = {
                RetrofitStubProtectedModule.class,
        },
        injects = {
        },
        overrides = true,
        complete = false,
        library = true
)
public class RetrofitStubModule
{
    @Provides AchievementServiceWrapper provideAchievementServiceWrapper(AchievementMockServiceWrapper achievementServiceWrapper)
    {
        return achievementServiceWrapper;
    }

    //@Provides RequestHeaders provideSlowRequestHeader(SlowRequestHeaders requestHeaders)
    //{
    //    return requestHeaders;
    //}

    //@Provides @Singleton MessageService provideMessageServiceStub(MessageServiceStub messageService)
    //{
    //    return messageService;
    //}

    //@Provides @Singleton DiscussionService provideDiscussionServiceStub(DiscussionServiceStub discussionService)
    //{
    //    return discussionService;
    //}
}
