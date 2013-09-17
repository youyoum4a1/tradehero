package com.tradehero.th.utils.dagger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.network.NetworkEngine;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.converter.Converter;

/** Created with IntelliJ IDEA. User: tho Date: 9/16/13 Time: 5:19 PM Copyright (c) TradeHero */
@Module(
        staticInjections = NetworkEngine.class
)
public class ConverterModule
{
    @Provides @Singleton Converter provideConverter()
    {
        return new JacksonConverter(new ObjectMapper());
    }
}
