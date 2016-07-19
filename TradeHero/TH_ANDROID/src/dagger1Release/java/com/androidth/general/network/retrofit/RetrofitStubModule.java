package com.androidth.general.network.retrofit;

import com.androidth.general.network.service.AchievementMockServiceWrapper;
import com.androidth.general.network.service.AchievementServiceWrapper;
import com.androidth.general.network.service.RetrofitStubProtectedModule;
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
