package com.tradehero.th.network.retrofit;

import com.tradehero.th.network.service.AchievementMockService;
import com.tradehero.th.network.service.AchievementMockServiceWrapper;
import com.tradehero.th.network.service.AchievementServiceWrapper;
import com.tradehero.th.network.service.RetrofitStubProtectedModule;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;

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
    @Provides @Singleton AchievementMockService provideAchievementMockService(RestAdapter adapter)
    {
        return adapter.create(AchievementMockService.class);
    }

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
