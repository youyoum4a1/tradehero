package com.ayondo.academy.models.push;

import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import rx.Observable;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@Module(
        includes = {
                FlavorPushTestModule.class
        },
        complete = false,
        library = true,
        overrides = true
)
public class PushTestModule
{
    @Provides @Singleton PushNotificationManager providePushNotificationManager()
    {
        PushNotificationManager manager = mock(PushNotificationManager.class);
        when(manager.initialise())
                .then(new Answer<Object>()
                {
                    @Override public Object answer(InvocationOnMock invocationOnMock) throws Throwable
                    {
                        return Observable.just(new PushNotificationManager.InitialisationCompleteDTO("Mocked"));
                    }
                });
        return manager;
    }
}
