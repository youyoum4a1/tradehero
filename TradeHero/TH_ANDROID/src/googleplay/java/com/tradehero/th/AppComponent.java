package com.tradehero.th;

import com.tradehero.th.utils.dagger.AppModule;
import dagger.Component;
import javax.inject.Singleton;

@Singleton
@Component( modules = { AppModule.class } )
public interface AppComponent extends AppGraph
{
    public static class Initializer
    {
        public static AppComponent init(THApp thApp)
        {
            return Dagger_AppComponent.builder()
                    .appModule(new AppModule(thApp))
                    .build();
        }
    }
}
