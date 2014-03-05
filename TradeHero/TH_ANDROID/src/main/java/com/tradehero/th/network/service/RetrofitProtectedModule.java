package com.tradehero.th.network.service;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tradehero.common.persistence.prefs.StringPreference;
import com.tradehero.common.utils.JacksonConverter;
import com.tradehero.th.fragments.settings.SettingsPayPalFragment;
import com.tradehero.th.fragments.settings.SettingsTransactionHistoryFragment;
import com.tradehero.th.models.intent.competition.ProviderPageIntent;
import com.tradehero.th.network.CompetitionUrl;
import com.tradehero.th.network.FriendlyUrlConnectionClient;
import com.tradehero.th.network.NetworkConstants;
import com.tradehero.th.network.ServerEndpoint;
import com.tradehero.th.network.retrofit.RequestHeaders;
import com.tradehero.th.network.retrofit.RetrofitSynchronousErrorHandler;
import com.tradehero.th.utils.RetrofitConstants;
import dagger.Module;
import dagger.Provides;
import javax.inject.Singleton;
import retrofit.RestAdapter;
import retrofit.Server;
import retrofit.converter.Converter;

/**
 * Created with IntelliJ IDEA. User: tho Date: 1/27/14 Time: 11:39 AM Copyright (c) TradeHero
 */

@Module(
        injects = {
        },
        complete = false,
        library = true
)
public class RetrofitProtectedModule
{
    //<editor-fold desc="API Services">
    @Provides @Singleton AlertServiceProtected provideAlertServiceProtected(RestAdapter engine)
    {
        return engine.create(AlertServiceProtected.class);
    }
    //</editor-fold>
}
