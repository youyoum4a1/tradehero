package com.tradehero.th.network.retrofit;

import com.tradehero.th.network.service.MessageService;
import com.tradehero.th.network.service.MessageServiceStub;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */

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
    @Provides @Singleton MessageService provideDiscussionServiceSync(MessageServiceStub messageService)
    {
        return messageService;
    }
}
