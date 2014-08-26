package com.tradehero.th.network.retrofit;

import com.tradehero.th.network.service.MarketServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapper;
import com.tradehero.th.network.service.SecurityServiceWrapperStub;
import com.tradehero.th.network.service.UserServiceWrapper;
import com.tradehero.th.network.service.MarketServiceWrapperStub;
import com.tradehero.th.network.service.UserServiceWrapperStub;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

@Module(
        includes = {
        },
        injects = {
        },
        overrides = true,
        complete = false,
        library = true
)
public class RetrofitStubModule
{
    //@Provides RequestHeaders providesSlowRequestHeader(SlowRequestHeaders requestHeaders)
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

    @Provides @Singleton MarketServiceWrapper provideMarketServiceWrapper(MarketServiceWrapperStub marketServiceWrapperStub)
    {
        return marketServiceWrapperStub;
    }

    @Provides @Singleton SecurityServiceWrapper provideSecurityServiceWrapper(SecurityServiceWrapperStub securityServiceWrapperStub)
    {
        return securityServiceWrapperStub;
    }

    @Provides @Singleton UserServiceWrapper provideUserServiceWrapper(UserServiceWrapperStub userServiceWrapperStub)
    {
        return userServiceWrapperStub;
    }
}
