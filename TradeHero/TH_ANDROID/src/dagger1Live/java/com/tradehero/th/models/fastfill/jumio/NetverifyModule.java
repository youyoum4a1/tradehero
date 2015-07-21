package com.tradehero.th.models.fastfill.jumio;

import com.tradehero.common.log.RetrofitErrorHandlerLogger;
import dagger.Module;
import dagger.Provides;
import retrofit.Endpoints;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

@Module(
        complete = false,
        library = true
)
public class NetverifyModule
{
    private RestAdapter createNetverifyRestAdapter(
            RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return builder
                .setEndpoint(Endpoints.newFixedEndpoint(NetverifyConstants.NETVERIFY_END_POINT))
                .setRequestInterceptor(new RequestInterceptor()
                {
                    @Override public void intercept(RequestFacade request)
                    {
                        request.addHeader("Authorization", NetverifyConstants.NETVERIFY_AUTH_HEADER);
                        request.addHeader("Accept", "application/json");
                        request.addHeader("User-Agent", NetverifyConstants.NETVERIFY_USER_AGENT);
                    }
                })
                .setErrorHandler(errorHandlerLogger)
                .build();
    }

    @Provides NetverifyServiceRx provideNetverifyServiceRx(RestAdapter.Builder builder,
            RetrofitErrorHandlerLogger errorHandlerLogger)
    {
        return createNetverifyRestAdapter(builder, errorHandlerLogger).create(NetverifyServiceRx.class);
    }
}
